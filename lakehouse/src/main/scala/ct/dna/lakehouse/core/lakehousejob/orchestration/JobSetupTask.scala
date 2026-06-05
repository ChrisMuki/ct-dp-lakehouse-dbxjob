package ct.dna.lakehouse.core.lakehousejob.orchestration

import scala.collection.mutable

import ct.dna.lakehouse.core.lakehousejob.SharedState
import ct.dna.lakehouse.core.lakehousejob.config.OrchestratorConfig
import ct.dna.lakehouse.core.lakehousejob.config.RunConfig
import ct.dna.lakehouse.core.lakehousejob.config.SummaryConfig
import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.runtime.SparkConfig
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.ExecuteOnce
import ct.dna.utils.collections.DagQueue
import ct.dna.utils.json.mapper
import ct.dna.utils.runtime.Configuration
import ct.dna.utils.runtime.Task

/** "JobSetup" — boot step. Parses its config objects, resolves the `CatalogSpec`, builds the dependency plan, enqueues every table and publishes
  * [[SharedState]]. It is the single config parse point of the job: the typed configs it produces are read by every later step through [[SharedState]].
  */
final case class JobSetupTask() extends Task {

  /** Spark + runtime args (everything after the leading task-name arg). Stored once via [[withRuntimeArguments]] before `start()`. */
  private var runtimeArgs: Seq[String] = Seq.empty

  def withRuntimeArguments(args: Seq[String]): JobSetupTask = {
    runtimeArgs = args
    this
  }

  override val name: String = EntryPoint.JobSetup
  override val uid: String = name

  protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce {
    logger.info(s"$name shutdown hook triggered")
  }

  override def start(): Unit = {
    val runStartMs = System.currentTimeMillis()

    val configs = parseConfig()
    SparkEnv.ensureInitialized(configs.sparkConfig)

    val catalogSpec = resolveCatalog(configs.runConfig.catalogClass)
    logger.info(s"JobSetup starting for catalog '${catalogSpec.id.name}'")

    val (plan, descendants) = JobSetupTask.buildPlan(catalogSpec)
    val queue = enqueuePlan(plan)
    logPlanOverview(catalogSpec, plan, descendants)

    SharedState.initialize(
      SharedState(
        jobRunId = configs.runConfig.runId,
        catalogSpec = catalogSpec,
        orchestratorConfig = configs.orchestratorConfig,
        summaryConfig = configs.summaryConfig,
        sparkConfig = configs.sparkConfig,
        queue = queue,
        runStartMs = runStartMs,
        transitiveDescendants = descendants,
        totalTables = plan.size,
        workerCount = configs.runConfig.workerCount
      )
    )
  }

  /** All config objects parsed once from the runtime args: the run identity/sizing plus the per-task settings each later step reads through [[SharedState]]. */
  private case class ParsedConfigs(
      runConfig: RunConfig,
      orchestratorConfig: OrchestratorConfig,
      summaryConfig: SummaryConfig,
      sparkConfig: SparkConfig
  )

  /** Parse the run identity (`catalogClass` / `workerCount` from the config file plus the runtime `runId` argument) and the two per-task config objects
    * (`orchestratorConfig`, `summaryConfig`) plus the Spark config. Everything except `runId` is resolved from the catalog's `configFile`.
    */
  private def parseConfig(): ParsedConfigs = {
    val parsed =
      Configuration
        .required("catalogClass")
        .required("workerCount")
        .required("runId")
        .required("orchestratorConfig")
        .required("summaryConfig")
        .withSparkConfig
        .build(runtimeArgs)

    ParsedConfigs(
      runConfig = RunConfig(
        catalogClass = parsed.getProperty("catalogClass"),
        runId = parsed.getProperty("runId"),
        workerCount = parsed.getProperty("workerCount").toInt
      ),
      orchestratorConfig = mapper.readValue[OrchestratorConfig](parsed.getProperty("orchestratorConfig")),
      summaryConfig = mapper.readValue[SummaryConfig](parsed.getProperty("summaryConfig")),
      sparkConfig = parsed.getSparkConfig
    )
  }

  /** Enqueue every planned table into a fresh [[DagQueue]] in topological order. */
  private def enqueuePlan(plan: Seq[(TableSpec[Entity], Set[TableID])]): DagQueue[TableID, TableSpec[Entity]] = {
    val queue: DagQueue[TableID, TableSpec[Entity]] = DagQueue.empty[TableID, TableSpec[Entity]]
    plan.foreach { case (tableSpec, parents) =>
      queue.enqueue(tableSpec.id, tableSpec, parents)
    }
    queue
  }

  /** Log the DAG-shape overview: if `edges == 0` the layer is effectively a flat queue (e.g. SR ← SR_RAW 1:1) and intra-catalog ordering / cascade-skip never
    * kick in. If `edges > 0` there is real intra-catalog wiring and some workers may have to idle-wait for parents to complete.
    */
  private def logPlanOverview(
      catalogSpec: CatalogSpec,
      plan: Seq[(TableSpec[Entity], Set[TableID])],
      descendants: Map[TableID, Set[TableID]]
  ): Unit = {
    logger.info(s"JobSetup enqueued ${plan.size} table(s) for catalog '${catalogSpec.id.name}'")
    val schemas = plan.iterator.map(_._1.id.schema).toSet
    val roots = plan.count(_._2.isEmpty)
    val edges = plan.iterator.map(_._2.size).sum
    val maxParents = if (plan.isEmpty) 0 else plan.iterator.map(_._2.size).max
    val maxDescendants = if (descendants.isEmpty) 0 else descendants.values.iterator.map(_.size).max
    logger.warn(
      s"PLAN [${catalogSpec.id.name}] tables=${plan.size} schemas=${schemas.size} " +
        s"roots=$roots edges=$edges maxParents=$maxParents maxDescendants=$maxDescendants"
    )
  }

  private def resolveCatalog(fqcn: String): CatalogSpec = {
    val cls = Class.forName(s"$fqcn.package$$")
    cls.getField("MODULE$").get(null).asInstanceOf[CatalogSpec]
  }
}

object JobSetupTask {

  /** Pure dependency-graph builder. Side-effect free so it can be unit-tested directly against a real `CatalogSpec`.
    *
    * Builds the topologically ordered `(TableSpec, parentIds)` enqueue plan and the transitive-descendants map (for cascade-skip). Cross-catalog edges are
    * filtered out — those tables are produced by another catalog's job. Self-references are ignored.
    */
  private[lakehousejob] def buildPlan(
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
