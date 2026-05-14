package ct.dna.lakehouse.core.jobs.orchestrator

import java.util.concurrent.atomic.AtomicBoolean

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import ct.dna.utils.ExecuteOnce
import ct.dna.utils.runtime.Task

/** Polymorphic Databricks-task descriptor consumed by `CatalogOrchestrator.createInstance`.
  *
  * The DAG emitted by [[ct.dna.lakehouse.core.CatalogWorkflowBuilder]] has three task kinds with explicit `depends_on` edges:
  *
  * {{{
  *   JobSetup ──▶ WorkerPool ──▶ Summary (run_if: ALL_DONE)
  * }}}
  *
  * The `clazz` discriminator distinguishes them on the Databricks task command line. WorkerPool spawns its worker threads + status reporter in-process and
  * shares the in-JVM `CatalogOrchestrator` singleton with JobSetup and Summary (all three run in the same driver REPL JVM via `run_as_repl=true`).
  */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "clazz")
@JsonSubTypes(
  Array(
    new JsonSubTypes.Type(value = classOf[OrchestratorTask.JobSetup], name = "JobSetup"),
    new JsonSubTypes.Type(value = classOf[OrchestratorTask.Summary], name = "Summary"),
    new JsonSubTypes.Type(value = classOf[OrchestratorTask.WorkerPool], name = "WorkerPool")
  )
)
sealed trait OrchestratorTask extends Task {

  /** Spark + runtime args (everything after the leading JSON arg). Stored once via [[withRuntimeArguments]] before `start()`. */
  private[orchestrator] var runtimeArgs: Array[String] = Array.empty

  def withRuntimeArguments(args: Array[String]): Unit = { runtimeArgs = args }

  override def name: String = getClass.getSimpleName

  /** Cooperative shutdown flag observed by the long-running loops in the runners. Flipped by `shutdownHook`. */
  protected final val keepRunning: AtomicBoolean = new AtomicBoolean(true)

  def shouldKeepRunning: Boolean = keepRunning.get()

  protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce {
    keepRunning.set(false)
    logger.info(s"$name shutdown hook triggered")
  }
}

object OrchestratorTask {

  /** "JobSetup" — boot task. Resolves the `CatalogSpec` from its FQCN, builds the dependency plan, enqueues every table and exits. Every other task
    * `depends_on` this one so they observe a fully populated singleton when they start.
    */
  final case class JobSetup(
      catalogClass: String,
      runId: String
  ) extends OrchestratorTask {
    override def name: String = TaskNames.SetupTaskKey
    override def uid: String = s"CatalogOrchestrator-JobSetup-$runId"
    override def start(): Unit = JobSetupTaskRunner.run(this)
  }

  /** "Summary" — terminal task. Runs with `run_if: ALL_DONE` after WorkerPool finishes, regardless of outcome. Writes the per-run row to the configured summary
    * Delta table and emits the final SUMMARY log line.
    */
  final case class Summary(
      runId: String
  ) extends OrchestratorTask {
    override def name: String = TaskNames.SummaryTaskKey
    override def uid: String = s"CatalogOrchestrator-Summary-$runId"
    override def start(): Unit = SummaryTaskRunner.run(this)
  }

  /** "WorkerPool" — single Databricks task that runs `poolSize` worker threads in-process and an in-process status reporter that emits the consolidated STATUS
    * block. One task is enough because every task already shares the same driver-REPL JVM via `run_as_repl`, so the historical `N × Worker_i` fan-out added
    * Databricks task overhead without buying any parallelism that internal threads couldn't provide.
    */
  final case class WorkerPool(
      poolSize: Int,
      runId: String
  ) extends OrchestratorTask {
    override def name: String = TaskNames.WorkerTaskKey
    override def uid: String = s"CatalogOrchestrator-WorkerPool-$runId"
    override def start(): Unit = WorkerPoolTaskRunner.run(this)
  }
}
