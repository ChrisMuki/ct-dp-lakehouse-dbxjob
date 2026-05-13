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
  * @param orchestratorJson
  *   raw JSON passed as the `orchestratorConfig` Spark argument to every task; produced by `AssetDirectory.createDatabricksYml`.
  */
final case class CatalogJobConfig(
    jobCluster: JobCluster,
    schedule: Option[JobSchedule] = None,
    continuous: Option[Continuous] = None,
    maxConcurrentRuns: Int = 1,
    maxRetries: Option[Integer] = Some(2),
    minRetryIntervalMillis: Option[Integer] = Some(60000),
    workerCount: Int = 4,
    orchestratorJson: String = "{}"
)

/** Builds a single Databricks Job per `CatalogSpec`.
  *
  * The job emits four task kinds connected by explicit `depends_on` edges:
  *
  *   - one `JobSetup` task that resolves the catalog, builds the dependency plan, enqueues every table and writes the `run.json` heartbeat,
  *   - N `Worker_$i` tasks that poll the queue, emit per-table START/END lines to their Output tab and overwrite their `worker_<i>.json` heartbeat,
  *   - one `Monitor` task that reads the heartbeat directory and prints a consolidated `STATUS` line every `statusIntervalSeconds`,
  *   - one terminal `Summary` task with `run_if: ALL_DONE` that appends the per-run row to the configured Delta table and emits the final SUMMARY log line.
  *
  * All tasks run on the shared `jobCluster` but each Databricks task gets its own driver JVM — cross-task communication therefore goes through Delta
  * (`lakehouse_runs`, `lakehouse_table_runs`) and the Unity Catalog volume heartbeat directory, NOT through JVM-static state.
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
      s"Catalog '${catalogSpec.id.name}': emitting ${TaskNames.SetupTaskKey} + ${config.workerCount} worker(s) + " +
        s"${TaskNames.MonitorTaskKey} + ${TaskNames.SummaryTaskKey} (catalogClass=$catalogClass)"
    )

    val library = Library(jar = jarPath)

    /** Common runtime params: every task receives the orchestrator JSON arg as arg(0) and the `configFile` / `orchestratorConfig` properties as positional args
      * for `ct.dna.utils.runtime.Configuration`.
      */
    def runtimeParams(taskJson: String): List[String] = List(
      taskJson,
      s"configFile=$configFilePath",
      s"orchestratorConfig=${config.orchestratorJson}"
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

    // --- Workers (Worker_0 … Worker_N-1): poll the shared queue ---
    val workerTasks: List[Task] = Range(0, config.workerCount).toList.map { id =>
      Task(
        taskKey = TaskNames.workerName(id),
        dependsOn = setupDependency,
        sparkJarTask = Some(
          SparkJarTask(
            mainClassName = entryPointClass,
            parameters = runtimeParams(s"""{"clazz":"Worker","id":"$id","runId":"{{job.run_id}}"}""")
          )
        ),
        jobClusterKey = config.jobCluster.jobClusterKey,
        libraries = List(library),
        // Workers don't retry: a worker dying mid-update would leave its DagQueue entry in `Running` and stall the catalog. Letting the whole job fail
        // surfaces the problem instead of silently re-running everything.
        maxRetries = None,
        minRetryIntervalMillis = None
      )
    }

    // --- Monitor: long-lived observer reading heartbeat JSON files. Runs in parallel to Workers, exits when the run is complete or maxRuntime hits.
    val monitorTask = Task(
      taskKey = TaskNames.MonitorTaskKey,
      dependsOn = setupDependency,
      sparkJarTask = Some(
        SparkJarTask(
          mainClassName = entryPointClass,
          parameters = runtimeParams("""{"clazz":"Monitor","runId":"{{job.run_id}}"}""")
        )
      ),
      jobClusterKey = config.jobCluster.jobClusterKey,
      libraries = List(library),
      maxRetries = None,
      minRetryIntervalMillis = None
    )

    // --- Summary: terminal observer. Depends on every Worker AND the Monitor so it can only start after the rest of the job is done.
    // Note: Databricks `run_if: ALL_DONE` (so Summary fires even on failure) is injected post-serialisation in `AssetDirectory.injectSummaryRunIf`, because the
    // shared `Task` case class doesn't expose that field yet.
    val summaryDependsOn: List[DependsRef] =
      DependsRef(taskKey = TaskNames.MonitorTaskKey) :: workerTasks.map(t => DependsRef(taskKey = t.taskKey))
    val summaryTask = Task(
      taskKey = TaskNames.SummaryTaskKey,
      dependsOn = summaryDependsOn,
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

    val tasks: List[Task] = jobSetupTask :: workerTasks ::: monitorTask :: summaryTask :: Nil

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
