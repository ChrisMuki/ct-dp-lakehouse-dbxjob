package ct.dna.lakehouse.core.jobs.orchestrator

import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.json.mapper
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.logging.PrintlnAppender
import ct.dna.utils.runtime.Configuration

/** Body of the "Sun" task ([[OrchestratorTask.JobSetup]]).
  *
  * Bootstraps shared state before the Orchestrator and Workers start. After this task succeeds the following invariants hold for downstream tasks:
  *   - [[CatalogOrchestrator.orchestratorConfig]] is non-null.
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
        .required("orchestratorConfig")
        .withSparkConfig
        .build(task.runtimeArgs)

    val orchestratorConfig = mapper.readValue[OrchestratorConfig](parsed.getProperty("orchestratorConfig"))
    CatalogOrchestrator.orchestratorConfig.set(orchestratorConfig)

    SparkEnv.ensureInitialized(parsed.getSparkConfig)

    val catalogSpec = resolveCatalog(task.catalogClass)
    CatalogOrchestrator.catalogSpec.set(catalogSpec)

    logger.info(s"JobSetup starting for catalog '${catalogSpec.id.name}' (workerCount=${orchestratorConfig.workerCount})")

    try {
      val (plan, descendants) = CatalogOrchestrator.buildPlan(catalogSpec)
      CatalogOrchestrator.transitiveDescendants.set(descendants)

      plan.foreach { case (tableSpec, parents) =>
        CatalogOrchestrator.queue.enqueue(tableSpec.id, tableSpec, parents)
      }
      logger.info(s"JobSetup enqueued ${plan.size} table(s) for catalog '${catalogSpec.id.name}'")

      // Live monitor: announce the run so Monitor can compute "X / total processed" without polling Spark.
      HeartbeatStore.writeRunInfo(
        orchestratorConfig,
        HeartbeatStore.RunInfo(
          runId = task.runId,
          catalog = catalogSpec.id.name,
          totalTables = plan.size,
          startedAtMs = CatalogOrchestrator.runStartMs.get().longValue()
        )
      )
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
