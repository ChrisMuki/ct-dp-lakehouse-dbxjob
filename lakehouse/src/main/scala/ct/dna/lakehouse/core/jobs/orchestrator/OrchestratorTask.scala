package ct.dna.lakehouse.core.jobs.orchestrator

import java.util.concurrent.atomic.AtomicBoolean

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import ct.dna.utils.ExecuteOnce
import ct.dna.utils.runtime.Task

/** Polymorphic Databricks-task descriptor consumed by `CatalogOrchestrator.createInstance`.
  *
  * The DAG emitted by [[ct.dna.lakehouse.core.CatalogWorkflowBuilder]] has four task kinds with explicit `depends_on` edges:
  *
  * {{{
  *                 ┌── Worker_0 ─────┐
  *                 ├── Worker_1 ─────┤
  *   JobSetup ──────┤      …          ├──── Summary (run_if: ALL_DONE)
  *                 ├── Worker_N ─────┤
  *                 └── Monitor ──────┘
  * }}}
  *
  * The `clazz` discriminator distinguishes them on the Databricks task command line. The Monitor reads the live heartbeat directory written by JobSetup and
  * Workers (see [[HeartbeatStore]]) — it does NOT rely on shared in-JVM state, because each Databricks task runs in its own driver JVM.
  */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "clazz")
@JsonSubTypes(
  Array(
    new JsonSubTypes.Type(value = classOf[OrchestratorTask.JobSetup], name = "JobSetup"),
    new JsonSubTypes.Type(value = classOf[OrchestratorTask.Monitor], name = "Monitor"),
    new JsonSubTypes.Type(value = classOf[OrchestratorTask.Summary], name = "Summary"),
    new JsonSubTypes.Type(value = classOf[OrchestratorTask.Worker], name = "Worker")
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

  /** "Monitor" — long-lived observer. Polls the [[HeartbeatStore]] directory every `statusIntervalSeconds` and emits a consolidated `STATUS` line. Exits when
    * every Worker has reported `done` or the queue is fully accounted for.
    */
  final case class Monitor(
      runId: String
  ) extends OrchestratorTask {
    override def name: String = TaskNames.MonitorTaskKey
    override def uid: String = s"CatalogOrchestrator-Monitor-$runId"
    override def start(): Unit = MonitorTaskRunner.run(this)
  }

  /** "Summary" — terminal task. Runs with `run_if: ALL_DONE` after every Worker has finished, regardless of outcome. Writes the per-run row to the configured
    * summary Delta table and emits the final SUMMARY log line.
    */
  final case class Summary(
      runId: String
  ) extends OrchestratorTask {
    override def name: String = TaskNames.SummaryTaskKey
    override def uid: String = s"CatalogOrchestrator-Summary-$runId"
    override def start(): Unit = SummaryTaskRunner.run(this)
  }

  /** A worker task. Polls the shared queue, runs `TableUpdaterCore.update` on each polled table and reports the outcome back to `CatalogOrchestrator`. `id` is
    * the numeric worker index; the Databricks `taskKey` is `Worker_$i` via [[TaskNames.workerName]].
    */
  final case class Worker(
      id: String,
      runId: String
  ) extends OrchestratorTask {
    override def name: String = TaskNames.workerName(id.toInt)
    override def uid: String = s"CatalogOrchestrator-Worker-${runId}_$id"
    override def start(): Unit = WorkerTaskRunner.run(this)
  }
}
