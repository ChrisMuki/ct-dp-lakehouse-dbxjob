package ct.dna.lakehouse.core.lakehousejob.worker

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import scala.jdk.CollectionConverters._
import scala.util.Try

import ct.dna.lakehouse.core.lakehousejob.SharedState
import ct.dna.lakehouse.core.lakehousejob.orchestration.OrchestratorTask
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.utils.ExecuteOnce
import ct.dna.utils.SetOnce
import ct.dna.utils.json.mapper
import ct.dna.utils.logging.PrintlnAppender
import ct.dna.utils.runtime.Task
import ct.dna.utils.runtime.TaskEntryPoint

/** Entry point owning the in-process worker pool. The Orchestrator launches it via [[launch]] and only ever queries aggregated state ([[isRunning]],
  * [[firstFailure]], [[runningTables]]). Each worker is a [[WorkerTask]] run through this entry point's own `main`, so when one crashes the framework's
  * `stopAll` cooperatively stops the siblings and runs [[cleanUpDuringShutdown]]. The crashing throwable is captured in [[firstFailure]] for the Orchestrator
  * to re-throw (Databricks run RED) while this object reports `isRunning = false`.
  */
object EntryPoint extends TaskEntryPoint {

  /** Task name of an in-process worker (leading arg of each `EntryPoint.main` launch). */
  final val Worker = "Worker"

  private val workers: ConcurrentLinkedQueue[WorkerTask] = new ConcurrentLinkedQueue[WorkerTask]()

  private val pool: SetOnce[ExecutorService] = SetOnce.empty
  private val futures: SetOnce[List[Future[_]]] = SetOnce.empty

  /** Every throwable that escaped a worker, in arrival order. Multiple workers can fail concurrently, so this is an append-only concurrent collection rather
    * than a single-writer cell; the Orchestrator re-throws the first to turn the Databricks run RED.
    */
  private val failures: ConcurrentLinkedQueue[Throwable] = new ConcurrentLinkedQueue[Throwable]()

  // ---- per-run cross-thread coordination, shared by every worker and the watchdog ----
  // Exactly one pool runs per driver JVM (Databricks recreates the driver between runs), so these
  // live for the whole run as plain object fields instead of a separately-injected state instance.

  /** Table-ids whose dependency closure includes a failed ancestor — workers skip these instead of running them. */
  private[worker] val skippedTables: java.util.Set[TableID] = ConcurrentHashMap.newKeySet[TableID]()

  /** Tables the watchdog has asked Spark to cancel, mapped to the wall-clock ms of that *first* cancel. The processing worker reads this to classify the
    * surfaced exception as a timeout (then clears it via [[claimTimeout]]); the watchdog reads it to decide when a still-running table must be force-escalated.
    */
  private[worker] val cancelDeadlines: ConcurrentHashMap[TableID, java.lang.Long] = new ConcurrentHashMap[TableID, java.lang.Long]()

  /** Tables already hard-escalated (worker thread interrupted) so the watchdog interrupts each one at most once. */
  private[worker] val escalatedTables: java.util.Set[TableID] = ConcurrentHashMap.newKeySet[TableID]()

  /** Grace window after the first cooperative cancel before the watchdog escalates to interrupting the worker thread. The soft cancel ([[SparkEnv.cancel]])
    * only stops Spark *jobs*; a table stuck in non-Spark driver code or an unresponsive Connect await needs the interrupt to break out.
    */
  private final val EscalateGraceMs: Long = 60_000L

  override def createInstance(args: Array[String]): Task = {
    PrintlnAppender.replaceConsoleAppendersWithPrintlnAppenders()
    val worker = args match {
      case Array(Worker, json) => mapper.readValue[WorkerTask](json)
      case Array(other, _*)    => throw new IllegalArgumentException(s"Unknown task name '$other' for orchestration entry point")
    }
    workers.add(worker)
    worker
  }

  /** Build the executor pool and start `poolSize` workers, each via its own `WorkerPool.main` call. Returns immediately; a throwable escaping a worker is
    * recorded in [[failures]].
    */
  def launch(poolSize: Int): Unit = {
    require(poolSize > 0, s"poolSize must be positive (got $poolSize)")
    pool.set(Executors.newFixedThreadPool(poolSize))

    val submitted: List[Future[_]] = (0 until poolSize).toList.map { i =>
      val workerJson = s"""{"idx":$i,"poolSize":$poolSize}"""
      pool.get.submit(new Runnable {
        override def run(): Unit =
          try EntryPoint.main(Array(Worker, workerJson))
          catch { case t: Throwable => failures.add(t) }
      })
    }
    futures.set(submitted)
    pool.get.shutdown() // no more tasks; the submitted ones keep running

    logger.warn(s"WorkerPool: launched $poolSize worker(s) for run ${SharedState.jobRunId}")
  }

  /** `true` while at least one worker future has not finished. */
  def isRunning: Boolean = futures.isDefined && futures.get.exists(!_.isDone)

  /** The first throwable that escaped a worker, if any. */
  def firstFailure: Option[Throwable] = Option(failures.peek())

  /** Every worker currently processing a table, paired with what it is running. */
  def runningTables: Seq[(WorkerTask, RunningTable)] =
    workers.asScala.iterator.flatMap(w => w.currentTable.map(w -> _)).toSeq

  /** Per-table watchdog: cancels any table running longer than `limitMs` by cancelling that table's context-global Spark job tag, and records it as a timeout
    * so the worker writes a `TIMED_OUT` row and cascade-skips descendants. Driven by the Orchestrator once per status tick.
    *
    * Escalation: the first tick over the limit issues a cooperative [[SparkEnv.cancel]]. While the table keeps running on later ticks the cancel is re-issued
    * (a fresh stage may have started a new job after the first cancel); if it is still running after [[EscalateGraceMs]] the watchdog escalates once by
    * interrupting the worker thread, which breaks it out of non-Spark driver code or an unresponsive await.
    */
  def enforceTimeouts(limitMs: Long): Unit = {
    val nowMs = System.currentTimeMillis()
    workers.asScala.foreach { w =>
      w.currentTable.foreach { rt =>
        val tableId = rt.tableId
        val elapsedMs = rt.elapsedMs(nowMs)
        if (elapsedMs > limitMs) {
          val jobTag = WorkerTask.buildJobTag(tableId)
          val firstCancelMs = cancelDeadlines.putIfAbsent(tableId, nowMs)
          if (firstCancelMs == null) {
            logger.warn(s"WATCHDOG cancelling $tableId on ${w.name}: elapsed=${elapsedMs / 1000}s > limit=${limitMs / 1000}s (tag=$jobTag)")
            tryCancel(jobTag)
          } else {
            // Already cancelled on an earlier tick but still running: re-issue the cancel to catch a job started after it ...
            tryCancel(jobTag)
            val sinceCancelMs = nowMs - firstCancelMs
            if (sinceCancelMs > EscalateGraceMs && escalatedTables.add(tableId)) {
              // ... and if the soft cancel still hasn't taken hold, escalate hard by interrupting the worker thread (once).
              logger.warn(s"WATCHDOG escalating $tableId on ${w.name}: still running ${sinceCancelMs / 1000}s after cancel — interrupting worker thread")
              rt.thread.interrupt()
            }
          }
        }
      }
    }
  }

  /** Best-effort cooperative cancel of every Spark job carrying `jobTag`; failures are logged, not propagated. */
  private def tryCancel(jobTag: String): Unit =
    Try(SparkEnv.cancel(jobTag)).failed.foreach { t =>
      logger.warn(s"WATCHDOG cancel($jobTag) failed: ${t.getClass.getSimpleName}: ${t.getMessage}")
    }

  /** Called by a worker when its table surfaced an exception: returns `true` if the watchdog had cancelled this table (so it is a timeout, not a real failure),
    * clearing the per-table cancel and escalation state in the process.
    */
  private[worker] def claimTimeout(tableId: TableID): Boolean = {
    escalatedTables.remove(tableId)
    cancelDeadlines.remove(tableId) != null
  }

  /** Flush result rows, log the final STATUS + slowest-tables blocks, terminate any still-running workers. Runs at most once — triggered either by the
    * Orchestrator at the normal end of the run or by the framework shutdown hook on a crash.
    */
  override protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce {
    logger.warn("WorkerPool: cleaning up — flushing results, terminating workers, releasing Spark")
    val all = workers.asScala.toSeq
    Try(OrchestratorTask.flushAndReport(all, SharedState.orchestratorConfig, SharedState.jobRunId))
    pool.foreach(_.shutdownNow())
  }

  def ensureCleanup(): Unit = cleanUpDuringShutdown.ensureExecuted()

  /** Zero-pad worker index `i` to the width implied by `poolSize` (at least 2 so logs stay column-aligned). */
  def formatIdx(i: Int, poolSize: Int): String = {
    val width = math.max(2, math.max(1, poolSize - 1).toString.length)
    ("%0" + width + "d").format(i)
  }
}
