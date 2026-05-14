package ct.dna.lakehouse.core

import ct.dna.lakehouse.core.jobs.orchestrator.TaskNames
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
  *   number of `Worker_$i` tasks emitted alongside the single `Summary` task. All share the same `jobCluster` driver JVM and consume the in-memory `DagQueue`.
  *   Larger values raise driver-side parallelism but also driver memory pressure.
  * @param monitoringJson
  *   raw JSON passed as the `monitoringConfig` Spark argument to every task; produced by `AssetDirectory.createDatabricksYml`.
  */
final case class CatalogJobConfig(
    jobCluster: JobCluster,
    schedule: Option[JobSchedule] = None,
    continuous: Option[Continuous] = None,
    maxConcurrentRuns: Int = 1,
    maxRetries: Option[Integer] = Some(2),
    minRetryIntervalMillis: Option[Integer] = Some(60000),
    workerCount: Int = 4,
    monitoringJson: String = "{}"
)

/** Builds a single Databricks Job per `CatalogSpec`.
  *
  * The job emits three task kinds connected by explicit `depends_on` edges:
  *
  * {{{
  *   JobSetup  ----->  Worker  ----->  Summary (run_if: ALL_DONE)
  * }}}
  *
  *   - one `JobSetup` task that resolves the catalog, builds the dependency plan and enqueues every table,
  *   - one `Worker` task that spawns `workerCount` internal threads draining the shared `DagQueue` and one in-process status reporter that emits the
  *     consolidated STATUS block to the same Output tab,
  *   - one terminal `Summary` task with `run_if: ALL_DONE` that appends the per-run row to the configured Delta table and emits the final SUMMARY log line.
  *
  * Every task runs on the shared `jobCluster` and \u2014 thanks to `run_as_repl=true` on the Spark JAR task descriptor \u2014 inside the same driver REPL JVM,
  * so the in-JVM `CatalogOrchestrator` singleton genuinely spans tasks. The `lakehouse_runs` / `lakehouse_table_runs` Delta tables hold cross-run persistent
  * state and remain the integration surface for external dashboards.
  */
object CatalogWorkflowBuilder extends LoggingTrait {

  private val entryPointClass: String =
    "ct.dna.lakehouse.core.jobs.orchestrator.CatalogOrchestrator"

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
      s"Catalog '${catalogSpec.id.name}': emitting ${TaskNames.SetupTaskKey} -> ${TaskNames.WorkerTaskKey}(poolSize=${config.workerCount}) -> " +
        s"${TaskNames.SummaryTaskKey} (catalogClass=$catalogClass)"
    )

    val library = Library(jar = jarPath)

    /** Common runtime params: every task receives the monitoring JSON arg as arg(0) and the `configFile` / `monitoringConfig` properties as positional args for
      * `ct.dna.utils.runtime.Configuration`.
      */
    def runtimeParams(taskJson: String): List[String] = List(
      taskJson,
      s"configFile=$configFilePath",
      s"monitoringConfig=${config.monitoringJson}"
    )

    // --- JobSetup: boots the singleton, builds the plan, exits ---
    val jobSetupTask = Task(
      taskKey = TaskNames.SetupTaskKey,
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = entryPointClass,
          parameters = runtimeParams(s"""{"clazz":"JobSetup","catalogClass":"$catalogClass","runId":"{{job.run_id}}"}""")
        )
      ),
      jobClusterKey = config.jobCluster.jobClusterKey,
      libraries = List(library),
      maxRetries = config.maxRetries,
      minRetryIntervalMillis = config.minRetryIntervalMillis
    )

    val setupDependency = List(DependsRef(taskKey = TaskNames.SetupTaskKey))

    // --- Worker: single task that spawns `workerCount` internal threads + an in-process status reporter ---
    val workerTask = Task(
      taskKey = TaskNames.WorkerTaskKey,
      dependsOn = setupDependency,
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = entryPointClass,
          parameters = runtimeParams(s"""{"clazz":"WorkerPool","poolSize":${config.workerCount},"runId":"{{job.run_id}}"}""")
        )
      ),
      jobClusterKey = config.jobCluster.jobClusterKey,
      libraries = List(library),
      // No retries: a worker pool dying mid-run would re-execute already-merged tables on retry, which is not idempotent.
      maxRetries = None,
      minRetryIntervalMillis = None
    )

    // --- Summary: terminal observer. Depends only on Worker now (Monitor task no longer exists).
    // Databricks `run_if: ALL_DONE` (so Summary fires even on Worker failure) is injected post-serialisation in `AssetDirectory.injectSummaryRunIf`.
    val summaryTask = Task(
      taskKey = TaskNames.SummaryTaskKey,
      dependsOn = List(DependsRef(taskKey = TaskNames.WorkerTaskKey)),
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = entryPointClass,
          parameters = runtimeParams("""{"clazz":"Summary","runId":"{{job.run_id}}"}""")
        )
      ),
      jobClusterKey = config.jobCluster.jobClusterKey,
      libraries = List(library),
      maxRetries = None,
      minRetryIntervalMillis = None
    )

    val tasks: List[Task] = List(jobSetupTask, workerTask, summaryTask)

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
