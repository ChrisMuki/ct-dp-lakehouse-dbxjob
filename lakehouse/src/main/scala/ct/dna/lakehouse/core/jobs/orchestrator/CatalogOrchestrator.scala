package ct.dna.lakehouse.core.jobs.orchestrator

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

import scala.collection.mutable

import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.utils.ExecuteOnce
import ct.dna.utils.collections.DagQueue
import ct.dna.utils.json.mapper
import ct.dna.utils.runtime.Task
import ct.dna.utils.runtime.TaskEntryPoint

/** JVM-shared state for one per-catalog orchestrator job.
  *
  * All Databricks tasks of the same Job run on the same `job_cluster` driver JVM, so this `object` is a genuine cross-task singleton: Setup populates it; every
  * Worker reads from it. Mirrors the pattern used by `ct.dna.lakehouse.pipeline.DBXLoadWorker` in `dp-pipeline-dxbjob`.
  *
  * State is only valid for the duration of a single Databricks job run. Because Databricks recreates the driver JVM between runs, no reset logic is needed in
  * production. Concurrency is handled with `java.util.concurrent` primitives — no external "SetOnce" / "ExecuteOnce" helpers are required.
  */
object CatalogOrchestrator extends TaskEntryPoint {

  // ---- shared state (populated by Setup, read by Setup + Workers) ----

  /** Set by Setup once the `CatalogSpec` has been resolved from its FQCN. Workers don't depend on it; kept for status logging. */
  val catalogSpec: AtomicReference[CatalogSpec] = new AtomicReference[CatalogSpec](null)

  /** Set by Setup once the parsed [[OrchestratorConfig]] is available. */
  val orchestratorConfig: AtomicReference[OrchestratorConfig] = new AtomicReference[OrchestratorConfig](null)

  /** The dependency-aware queue. Created at class-load time so workers that race ahead of Setup can already poll (they'll just get empty results). */
  val queue: DagQueue[TableID, TableSpec[Entity]] = DagQueue.empty

  /** Flipped to true by Setup after every table has been enqueued. Workers use this to decide when an empty poll means "done" vs "wait for more". */
  val enqueueComplete: AtomicBoolean = new AtomicBoolean(false)

  /** Cross-task shutdown signal. Flipped by the Monitor when it decides the run must stop (hard cap reached, drain timeout, stall detected). Executors observe
    * this in their poll loop and exit cleanly without picking up new tables. The shared flag is necessary because each Databricks task has its own
    * `keepRunning` flag that only its own shutdown hook can flip.
    */
  val shutdownRequested: AtomicBoolean = new AtomicBoolean(false)

  /** Set non-null if Setup itself crashed before / during enqueue. Workers exit early when they observe this. */
  val setupError: AtomicReference[Throwable] = new AtomicReference[Throwable](null)

  /** Run-id propagated from the Databricks `{{job.run_id}}` substitution. Captured by JobSetup; consumed by Monitor / Summary for log lines and the summary
    * row.
    */
  val runId: AtomicReference[String] = new AtomicReference[String](null)

  /** Wall-clock ms at which JobSetup started, used by the Monitor's no-progress timeout (it tracks "time since last recorded outcome") and by the Summary task
    * to compute total elapsed time.
    */
  val runStartMs: AtomicReference[java.lang.Long] = new AtomicReference[java.lang.Long](null)

  /** ms timestamp of the last recorded outcome — refreshed in `recordOutcome`. Monitor reads this to drive `noProgressTimeoutSeconds`. */
  val lastOutcomeMs: AtomicReference[java.lang.Long] = new AtomicReference[java.lang.Long](null)

  /** Live "executor → (tableId, started_at_ms)" view populated by `WorkerTaskRunner.processOne`. The Monitor reads this every `statusIntervalSeconds` to print
    * which executor is currently busy with which table. Lock-free via `ConcurrentHashMap`.
    */
  val runningTables: ConcurrentHashMap[String, (TableID, java.lang.Long)] =
    new ConcurrentHashMap[String, (TableID, java.lang.Long)]()

  /** Precomputed transitive descendants per table (built once by Setup from the intra-catalog DAG). Workers consult this to mark downstream tables as
    * `SkippedByAncestor` when a table fails.
    */
  val transitiveDescendants: AtomicReference[Map[TableID, Set[TableID]]] =
    new AtomicReference[Map[TableID, Set[TableID]]](Map.empty)

  /** Per-table outcomes — written by workers when they finish (or skip) a table. Read by Setup for the final status line. */
  val results: ConcurrentHashMap[TableID, TableOutcome] = new ConcurrentHashMap[TableID, TableOutcome]()

  /** Set of table-ids whose dependency closure includes a failed ancestor. Workers must mark, complete and skip these instead of running them. Uses
    * `ConcurrentHashMap.newKeySet` so lookup / insert is lock-free.
    */
  val skippedTables: java.util.Set[TableID] =
    ConcurrentHashMap.newKeySet[TableID]()

  // ---- counters for the status timer ----

  private val _completed = new java.util.concurrent.atomic.AtomicInteger(0)
  private val _failed = new java.util.concurrent.atomic.AtomicInteger(0)
  private val _skipped = new java.util.concurrent.atomic.AtomicInteger(0)

  def recordOutcome(tableId: TableID, outcome: TableOutcome): Unit = {
    results.put(tableId, outcome)
    outcome match {
      case TableOutcome.Updated              => _completed.incrementAndGet()
      case _: TableOutcome.Failed            => _failed.incrementAndGet()
      case _: TableOutcome.SkippedByAncestor => _skipped.incrementAndGet()
    }
    lastOutcomeMs.set(java.lang.Long.valueOf(System.currentTimeMillis()))
  }

  def completedCount: Int = _completed.get()
  def failedCount: Int = _failed.get()
  def skippedCount: Int = _skipped.get()

  // ---- TaskEntryPoint plumbing ----

  override def createInstance(args: Array[String]): Task = {
    require(args.nonEmpty, "First arg must be a JSON OrchestratorTask descriptor (JobSetup, Monitor, Summary or Worker)")
    val runtimeArgs = args.drop(1)
    val task = mapper.readValue[OrchestratorTask](args(0))
    task.withRuntimeArguments(runtimeArgs)
    task
  }

  protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce { logger.info("CatalogOrchestrator shutdown hook triggered") }

  // ---- helpers exposed for SetupTask / WorkerTask ----

  /** Walks the catalog and builds:
    *   - the list of `(TableSpec[Entity], parentIds)` ready to be enqueued in topological order,
    *   - the transitive-descendants map used for cascade-skip.
    *
    * Cross-catalog edges are filtered out — those tables are produced by another catalog's orchestrator. Self-references are ignored.
    */
  private[orchestrator] def buildPlan(
      catalogSpec: CatalogSpec
  ): (Seq[(TableSpec[Entity], Set[TableID])], Map[TableID, Set[TableID]]) = {

    import ct.dna.lakehouse.core.model.internal.findSchemaSpecs
    import ct.dna.lakehouse.core.model.internal.findTableSpecs
    import ct.dna.lakehouse.core.model.Updated

    val schemas = findSchemaSpecs(catalogSpec)
    val allTables: Seq[TableSpec[Entity]] = schemas.flatMap(findTableSpecs)
    val intraIds: Set[TableID] = allTables.map(_.id).toSet

    val parentsByTable: Map[TableSpec[Entity], Set[TableID]] = allTables.map { ts =>
      val deps: Set[TableID] = ts match {
        case u: Updated =>
          u.sourceTableSpecs
            .map(_.id)
            .filter(intraIds.contains)
            .filter(_ != ts.id)
            .toSet
        case _ => Set.empty
      }
      ts -> deps
    }.toMap

    // Direct-children map first.
    val directChildren = mutable.HashMap.empty[TableID, mutable.Set[TableID]]
    parentsByTable.foreach { case (child, parents) =>
      parents.foreach { p =>
        directChildren.getOrElseUpdate(p, mutable.HashSet.empty[TableID]) += child.id
      }
    }

    // Topological order (Kahn) — also serves as the cycle check. We need a stable enqueue order so DagQueue accepts each entry's parents.
    val inDegree = mutable.HashMap.from(allTables.map(t => t.id -> parentsByTable(t).size))
    val readyQueue = mutable.Queue.from(inDegree.collect { case (id, 0) => id })
    val ordered = mutable.ArrayBuffer.empty[TableID]
    while (readyQueue.nonEmpty) {
      val id = readyQueue.dequeue()
      ordered += id
      directChildren.getOrElse(id, Iterable.empty).foreach { child =>
        inDegree(child) -= 1
        if (inDegree(child) == 0) readyQueue.enqueue(child)
      }
    }
    require(
      ordered.size == allTables.size,
      s"Cycle detected in catalog '${catalogSpec.id.name}' dependency graph (processed ${ordered.size}/${allTables.size})"
    )

    val byId: Map[TableID, TableSpec[Entity]] = allTables.map(t => t.id -> t).toMap
    val orderedPlan: Seq[(TableSpec[Entity], Set[TableID])] =
      ordered.toSeq.map(id => byId(id) -> parentsByTable(byId(id)))

    // Transitive descendants — memoised reverse BFS.
    val transitive = mutable.HashMap.empty[TableID, Set[TableID]]
    def descendants(id: TableID): Set[TableID] =
      transitive.getOrElseUpdate(
        id, {
          val children = directChildren.getOrElse(id, Set.empty).toSet
          children ++ children.flatMap(descendants)
        }
      )
    allTables.foreach(t => descendants(t.id))

    (orderedPlan, transitive.toMap.withDefaultValue(Set.empty))
  }
}
