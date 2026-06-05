package ct.dna.lakehouse.deploy

import ct.dna.lakehouse.core.lakehousejob.orchestration.{EntryPoint => OrchestrationEntryPoint}
import ct.dna.lakehouse.core.lakehousejob.summary.{EntryPoint => SummaryEntryPoint}
import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.utils.deploy.databrickscli.assetbundle._
import ct.dna.utils.logging.LoggingTrait

object CatalogJobBuilder extends LoggingTrait {

  private val entryPointClass: String =
    "ct.dna.lakehouse.core.lakehousejob.orchestration.EntryPoint"

  private val summaryEntryPointClass: String =
    "ct.dna.lakehouse.core.lakehousejob.summary.EntryPoint"

  /** Assemble the Databricks Job for one catalog.
    *
    * @param catalogConfig
    *   the catalog's already-resolved [[CatalogConfig]] — supplies the trigger (`schedule`) and the in-JVM table parallelism (`taskParallelism`). The matching
    *   `jobCluster` is built from the same config by `AssetDirectory` and passed in alongside.
    * @param jobCluster
    *   the `JobCluster` shared by every task in this job (built from `catalogConfig` plus deploy-time volume paths).
    * @param configFilePath
    *   volume path of the catalog's `config-<catalog>.json`; `JobSetup` loads everything it needs from it (catalog identity, worker count, the per-task
    *   orchestrator/summary configs and the Spark config), so the only remaining task argument is the runtime `runId`.
    */
  def buildJob(
      catalogSpec: CatalogSpec,
      catalogConfig: CatalogConfig,
      jobCluster: JobCluster,
      jarPath: String,
      configFilePath: String
  ): (String, Job) = {

    val workerCount = catalogConfig.taskParallelism
    require(workerCount >= 1, s"taskParallelism must be >= 1 (catalog '${catalogSpec.id.name}')")

    val catalogClass = catalogSpec.getClass.getPackage.getName
    logger.info(
      s"Catalog '${catalogSpec.id.name}': emitting ${OrchestrationEntryPoint.JobSetup} -> ${OrchestrationEntryPoint.Orchestrator}(poolSize=$workerCount) -> " +
        s"${SummaryEntryPoint.Summary} (catalogClass=$catalogClass)"
    )

    // --- JobSetup: boots the shared state, builds the plan, exits ---
    val jobSetupTask = Task(
      taskKey = OrchestrationEntryPoint.JobSetup,
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = entryPointClass,
          parameters = List(
            OrchestrationEntryPoint.JobSetup,
            """runId={{job.run_id}}""",
            s"configFile=$configFilePath"
          )
        )
      ),
      jobClusterKey = jobCluster.jobClusterKey,
      libraries = List(Library(jar = jarPath)),
      maxRetries = None,
      minRetryIntervalMillis = None
    )

    // --- Orchestrator: launches `workerCount` internal worker threads + drives the live status / watchdog ---
    val orchestratorTask = jobSetupTask.copy(
      taskKey = OrchestrationEntryPoint.Orchestrator,
      dependsOn = List(DependsRef(taskKey = OrchestrationEntryPoint.JobSetup)),
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = entryPointClass,
          parameters = List(OrchestrationEntryPoint.Orchestrator)
        )
      )
    )

    // --- Summary: terminal observer with its own entry point. Depends on Orchestrator.
    // Databricks `run_if: ALL_DONE` (so Summary fires even on Orchestrator failure) is injected post-serialisation in `AssetDirectory.injectSummaryRunIf`.
    val summaryTask = jobSetupTask.copy(
      taskKey = SummaryEntryPoint.Summary,
      dependsOn = List(DependsRef(taskKey = OrchestrationEntryPoint.Orchestrator)),
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = summaryEntryPointClass,
          parameters = List(SummaryEntryPoint.Summary)
        )
      )
    )

    val tasks: List[Task] = List(jobSetupTask, orchestratorTask, summaryTask)

    val jobName = s"lakehouse-${catalogSpec.id.name}"
    jobName -> Job(
      name = jobName,
      schedule = Some(catalogConfig.schedule),
      continuous = None,
      maxConcurrentRuns = 1,
      tasks = tasks,
      jobClusters = List(jobCluster),
      queue = Queue(enabled = true),
      runAs = null,
      tags = null
    )
  }
}
