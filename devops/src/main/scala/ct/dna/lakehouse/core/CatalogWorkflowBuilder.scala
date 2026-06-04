package ct.dna.lakehouse.core

import ct.dna.lakehouse.core.lakehousejob.orchestration.{EntryPoint => OrchestrationEntryPoint}
import ct.dna.lakehouse.core.lakehousejob.summary.{EntryPoint => SummaryEntryPoint}
import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.utils.deploy.databrickscli.assetbundle._
import ct.dna.utils.logging.LoggingTrait

/** Per-catalog deployment knobs.
  *
  * @param jobCluster
  *   the `JobCluster` shared by every task in the catalog job
  * @param schedule
  *   optional cron schedule for the catalog job
  * @param continuous
  *   optional continuous-run config (mutually exclusive with `schedule` from Databricks' perspective)
  * @param maxConcurrentRuns
  *   per-job concurrent-run cap (default 1 to prevent overlap)
  * @param maxRetries
  *   default `max_retries` applied to every task; `None` disables retries
  * @param minRetryIntervalMillis
  *   default retry interval; ignored when `maxRetries` is `None`
  * @param workerCount
  *   number of in-process worker threads the single `Orchestrator` task launches inside its driver JVM to consume the in-memory `DagQueue`. Larger values raise
  *   driver-side parallelism but also driver memory pressure.
  * @param orchestratorConfigJson
  *   raw JSON passed as the `orchestratorConfig` Spark argument to `JobSetup`; produced by `AssetDirectory.createDatabricksYml`.
  * @param summaryConfigJson
  *   raw JSON passed as the `summaryConfig` Spark argument to `JobSetup`; produced by `AssetDirectory.createDatabricksYml`.
  */
final case class CatalogJobConfig(
    jobCluster: JobCluster,
    schedule: Option[JobSchedule] = None,
    continuous: Option[Continuous] = None,
    maxConcurrentRuns: Int = 1,
    maxRetries: Option[Integer] = Some(2),
    minRetryIntervalMillis: Option[Integer] = Some(60000),
    workerCount: Int = 4,
    orchestratorConfigJson: String = "{}",
    summaryConfigJson: String = "{}"
)

/** Builds a single Databricks Job per `CatalogSpec`.
  *
  * The job has two visible steps connected by an explicit `depends_on` edge, plus a terminal observer:
  *
  * {{{
  *   JobSetup  ----->  Orchestrator        Summary (run_if: ALL_DONE)
  * }}}
  *
  *   - one `JobSetup` step that resolves the catalog, builds the dependency plan, enqueues every table and publishes the shared state,
  *   - one `Orchestrator` step that launches `workerCount` in-process worker threads, tracks live status and turns the run RED on failure,
  *   - one terminal `Summary` step with `run_if: ALL_DONE` that appends the per-run row to the configured Delta table and emits the final SUMMARY log line.
  *
  * `JobSetup` and `Orchestrator` share the [[ct.dna.lakehouse.core.lakehousejob.orchestration.EntryPoint]] entry point; `Summary` has its own
  * ([[ct.dna.lakehouse.core.lakehousejob.summary.EntryPoint]]) so it survives an aborted Orchestrator. Every step runs on the shared `jobCluster` and — thanks
  * to `run_as_repl=true` — inside the same driver REPL JVM, so the in-JVM shared state genuinely spans steps. The `lakehouse_runs` / `lakehouse_table_runs`
  * Delta tables hold cross-run persistent state and remain the integration surface for external dashboards.
  */
object CatalogWorkflowBuilder extends LoggingTrait {

  private val entryPointClass: String =
    "ct.dna.lakehouse.core.lakehousejob.orchestration.EntryPoint"

  private val summaryEntryPointClass: String =
    "ct.dna.lakehouse.core.lakehousejob.summary.EntryPoint"

  def calcJobName(catalogSpec: CatalogSpec): String =
    s"lakehouse-${catalogSpec.id.name}"

  def buildJob(
      catalogSpec: CatalogSpec,
      config: CatalogJobConfig,
      jarPath: String,
      configFilePath: String
  ): (String, Job) = {

    require(config.workerCount >= 1, s"workerCount must be >= 1 (catalog '${catalogSpec.id.name}')")

    val catalogClass = catalogSpec.getClass.getPackage.getName
    logger.info(
      s"Catalog '${catalogSpec.id.name}': emitting ${OrchestrationEntryPoint.JobSetup} -> ${OrchestrationEntryPoint.Orchestrator}(poolSize=${config.workerCount}) -> " +
        s"${SummaryEntryPoint.Summary} (catalogClass=$catalogClass)"
    )

    val library = Library(jar = jarPath)

    // --- JobSetup: boots the shared state, builds the plan, exits ---
    val jobSetupTask = Task(
      taskKey = OrchestrationEntryPoint.JobSetup,
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = entryPointClass,
          parameters = List(
            OrchestrationEntryPoint.JobSetup,
            s"""runConfig={"catalogClass":"$catalogClass","runId":"{{job.run_id}}","workerCount":${config.workerCount}}""",
            s"configFile=$configFilePath",
            s"orchestratorConfig=${config.orchestratorConfigJson}",
            s"summaryConfig=${config.summaryConfigJson}"
          )
        )
      ),
      jobClusterKey = config.jobCluster.jobClusterKey,
      libraries = List(library),
      maxRetries = config.maxRetries,
      minRetryIntervalMillis = config.minRetryIntervalMillis
    )

    // --- Orchestrator: launches `workerCount` internal worker threads + drives the live status / watchdog ---
    val orchestratorTask = Task(
      taskKey = OrchestrationEntryPoint.Orchestrator,
      dependsOn = List(DependsRef(taskKey = OrchestrationEntryPoint.JobSetup)),
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = entryPointClass,
          parameters = List(OrchestrationEntryPoint.Orchestrator)
        )
      ),
      jobClusterKey = config.jobCluster.jobClusterKey,
      libraries = List(library),
      // No retries: a worker pool dying mid-run would re-execute already-merged tables on retry, which is not idempotent.
      maxRetries = None,
      minRetryIntervalMillis = None
    )

    // --- Summary: terminal observer with its own entry point. Depends on Orchestrator.
    // Databricks `run_if: ALL_DONE` (so Summary fires even on Orchestrator failure) is injected post-serialisation in `AssetDirectory.injectSummaryRunIf`.
    val summaryTask = Task(
      taskKey = SummaryEntryPoint.Summary,
      dependsOn = List(DependsRef(taskKey = OrchestrationEntryPoint.Orchestrator)),
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = summaryEntryPointClass,
          parameters = List(SummaryEntryPoint.Summary)
        )
      ),
      jobClusterKey = config.jobCluster.jobClusterKey,
      libraries = List(library),
      maxRetries = None,
      minRetryIntervalMillis = None
    )

    val tasks: List[Task] = List(jobSetupTask, orchestratorTask, summaryTask)

    val jobName = calcJobName(catalogSpec)
    jobName -> Job(
      name = jobName,
      schedule = config.schedule,
      continuous = config.continuous,
      maxConcurrentRuns = config.maxConcurrentRuns,
      tasks = tasks,
      jobClusters = List(config.jobCluster),
      queue = Queue(enabled = true),
      runAs = null,
      tags = null
    )
  }
}
