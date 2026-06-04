package ct.dna.lakehouse.core.lakehousejob

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import ct.dna.lakehouse.core.lakehousejob.config.OrchestratorConfig
import ct.dna.lakehouse.core.lakehousejob.config.SummaryConfig
import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.runtime.SparkConfig
import ct.dna.utils.SetOnce
import ct.dna.utils.collections.DagQueue

/** Immutable snapshot of everything `JobSetup` prepares once for the rest of the run. Published via [[SharedState.initialize]] and read through the getters on
  * the [[SharedState]] companion.
  */
final case class SharedState(
    jobRunId: String,
    catalogSpec: CatalogSpec,
    orchestratorConfig: OrchestratorConfig,
    summaryConfig: SummaryConfig,
    sparkConfig: SparkConfig,
    queue: DagQueue[TableID, TableSpec[Entity]],
    runStartMs: Long,
    transitiveDescendants: Map[TableID, Set[TableID]],
    totalTables: Int,
    workerCount: Int
)

/** JVM-wide hub for one per-catalog lakehouse run. All Databricks steps share one driver-REPL JVM (`run_as_repl=true`), so this `object` is a genuine
  * cross-step singleton: JobSetup populates it; Orchestrator, Workers and Summary read it. State lives only for one run — Databricks recreates the driver JVM
  * between runs, so no reset logic is needed. It holds the immutable [[SharedState]] snapshot plus the run-time accumulators workers write and the rest read.
  */
object SharedState {

  /** Logic version stamped onto every table update. */
  val logicVersion: String = "1.0"

  // ---- immutable setup snapshot ----

  private val snapshot: SetOnce[SharedState] = SetOnce.empty

  def initialize(state: SharedState): Unit = snapshot.set(state)
  def isInitialized: Boolean = snapshot.isDefined

  def jobRunId: String = snapshot.get.jobRunId
  def catalogSpec: CatalogSpec = snapshot.get.catalogSpec
  def orchestratorConfig: OrchestratorConfig = snapshot.get.orchestratorConfig
  def summaryConfig: SummaryConfig = snapshot.get.summaryConfig
  def sparkConfig: SparkConfig = snapshot.get.sparkConfig
  def queue: DagQueue[TableID, TableSpec[Entity]] = snapshot.get.queue
  def runStartMs: Long = snapshot.get.runStartMs
  def transitiveDescendants: Map[TableID, Set[TableID]] = snapshot.get.transitiveDescendants
  def totalTables: Int = snapshot.get.totalTables
  def workerCount: Int = snapshot.get.workerCount

  // ---- run-time accumulators ----

  /** Per-table outcomes — written by Workers when they finish (or skip) a table. Read by Summary for the final status line. */
  val results: ConcurrentHashMap[TableID, TableOutcome] = new ConcurrentHashMap[TableID, TableOutcome]()

  private val _completed = new AtomicInteger(0)
  private val _failed = new AtomicInteger(0)
  private val _skipped = new AtomicInteger(0)

  def recordOutcome(tableId: TableID, outcome: TableOutcome): Unit = {
    results.put(tableId, outcome)
    outcome match {
      case TableOutcome.Updated              => _completed.incrementAndGet()
      case _: TableOutcome.Failed            => _failed.incrementAndGet()
      case _: TableOutcome.SkippedByAncestor => _skipped.incrementAndGet()
    }
    ()
  }

  def completedCount: Int = _completed.get()
  def failedCount: Int = _failed.get()
  def skippedCount: Int = _skipped.get()
}
