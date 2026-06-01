package ct.dna.lakehouse.core.jobs.orchestrator

import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.lakehouse.core.runtime.PoolStrategy
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.json.mapper
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.logging.PrintlnAppender
import ct.dna.utils.runtime.Configuration

/** Body of the "Sun" task ([[OrchestratorTask.JobSetup]]).
  *
  * Bootstraps shared state before the Orchestrator and Workers start. After this task succeeds the following invariants hold for downstream tasks:
  *   - [[CatalogOrchestrator.monitoringConfig]] is non-null.
  *   - [[CatalogOrchestrator.catalogSpec]] is non-null.
  *   - [[CatalogOrchestrator.queue]] contains every catalog table with its parent set wired.
  *   - [[CatalogOrchestrator.transitiveDescendants]] is populated.
  *   - [[CatalogOrchestrator.enqueueComplete]] is `true`.
  *
  * Because Workers and Orchestrator have an explicit `depends_on: JobSetup` edge in the bundle, they no longer have to poll for these conditions on startup.
  */
private[orchestrator] object JobSetupTaskRunner extends LoggingTrait {

  def run(task: OrchestratorTask.JobSetup): Unit = {
    // Route every log4j2 ConsoleAppender through System.out.println so log lines from ct.dna.* appear in the
    // Databricks task **Output** tab (in addition to the driver log file). Mirrors dp-pipeline-dbxjob's DBXLoadWorker.
    PrintlnAppender.replaceConsoleAppendersWithPrintlnAppenders()

    // Capture the run-id + wall-clock start as soon as possible so the Monitor / Summary tasks can rely on them.
    CatalogOrchestrator.runId.set(task.runId)
    CatalogOrchestrator.runStartMs.set(java.lang.Long.valueOf(System.currentTimeMillis()))
    CatalogOrchestrator.lastOutcomeMs.set(java.lang.Long.valueOf(System.currentTimeMillis()))

    val parsed =
      Configuration
        .required("rootDir")
        .required("monitoringConfig")
        .withSparkConfig
        .build(task.runtimeArgs)

    val monitoringConfig = mapper.readValue[MonitoringConfig](parsed.getProperty("monitoringConfig"))
    CatalogOrchestrator.monitoringConfig.set(monitoringConfig)

    SparkEnv.ensureInitialized(parsed.getSparkConfig)
    SparkEnv.setPoolStrategy(PoolStrategy.Layered("lakehouse"))

    val catalogSpec = resolveCatalog(task.catalogClass)
    CatalogOrchestrator.catalogSpec.set(catalogSpec)

    logger.info(s"JobSetup starting for catalog '${catalogSpec.id.name}'")

    try {
      val (plan, descendants) = CatalogOrchestrator.buildPlan(catalogSpec)
      CatalogOrchestrator.transitiveDescendants.set(descendants)

      plan.foreach { case (tableSpec, parents) =>
        CatalogOrchestrator.queue.enqueue(tableSpec.id, tableSpec, parents)
      }
      logger.info(s"JobSetup enqueued ${plan.size} table(s) for catalog '${catalogSpec.id.name}'")

      // DAG-shape overview: if `edges == 0` the layer is effectively a flat queue (e.g. SR ← SR_RAW 1:1) and
      // intra-catalog ordering / cascade-skip never kick in. If `edges > 0` there is real intra-catalog wiring
      // and some workers may have to idle-wait for parents to complete.
      val schemas = plan.iterator.map(_._1.id.schemaId.name).toSet
      val roots = plan.count(_._2.isEmpty)
      val edges = plan.iterator.map(_._2.size).sum
      val maxParents = if (plan.isEmpty) 0 else plan.iterator.map(_._2.size).max
      val maxDescendants = if (descendants.isEmpty) 0 else descendants.values.iterator.map(_.size).max
      logger.warn(
        s"PLAN [${catalogSpec.id.name}] tables=${plan.size} schemas=${schemas.size} " +
          s"roots=$roots edges=$edges maxParents=$maxParents maxDescendants=$maxDescendants"
      )

      // Live monitor: announce the run so Monitor can compute "X / total processed" without polling Spark.
      CatalogOrchestrator.totalTables.set(plan.size)
    } catch {
      case t: Throwable =>
        CatalogOrchestrator.setupError.set(t)
        CatalogOrchestrator.enqueueComplete.set(true) // workers must not wait forever
        throw t
    }
    CatalogOrchestrator.enqueueComplete.set(true)
  }

  private def resolveCatalog(fqcn: String): CatalogSpec = {
    val cls = Class.forName(s"$fqcn.package$$")
    cls.getField("MODULE$").get(null).asInstanceOf[CatalogSpec]
  }
}
