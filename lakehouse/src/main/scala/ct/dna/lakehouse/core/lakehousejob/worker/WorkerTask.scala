package ct.dna.lakehouse.core.lakehousejob.worker

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import ct.dna.lakehouse.core.framework.internal.TableManager
import ct.dna.lakehouse.core.framework.internal.UpdatedTableProcessor
import ct.dna.lakehouse.core.lakehousejob.SharedState
import ct.dna.lakehouse.core.lakehousejob.TableOutcome
import ct.dna.lakehouse.core.lakehousejob.orchestration.TableRunRow
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.core.runtime.PoolStrategy
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.utils.ExecuteOnce
import ct.dna.utils.SetOnce
import ct.dna.utils.collections.DagQueue
import ct.dna.utils.runtime.Task
import ct.dna.utils.tryOrIgnore
import org.apache.spark.sql.SparkSession

/** One worker — a single Databricks-internal [[Task]] launched by [[EntryPoint]] (one per pool thread, via its own `EntryPoint.main` call). Encapsulates all
  * thread-private state (name, [[TableRunRow]] buffer, current table, its own reused [[TableManager]] / [[UpdatedTableProcessor]]). Cross-thread state (queue,
  * outcomes) lives in [[SharedState]]; the pool-wide skip / timeout signal sets live on [[EntryPoint]].
  */
final case class WorkerTask(idx: Int, poolSize: Int) extends Task {

  override val name: String = s"WorkerPool_${EntryPoint.formatIdx(idx, poolSize)}"
  override val uid: String = name

  /** Run id handed to the per-table processors: job run id + this worker's thread name. */
  private lazy val workerRunId: String = s"${SharedState.jobRunId}_$name".toLowerCase()

  /** Append-only buffer of per-table result rows; single-threaded use, drained by the pool once this thread finishes. */
  val rowBuffer: mutable.ArrayBuffer[TableRunRow] = mutable.ArrayBuffer.empty[TableRunRow]

  // Per-thread processors — reused across every table this worker handles, never shared with another thread.
  private lazy val tableManager: TableManager = TableManager(workerRunId)
  private lazy val updatedTableProcessor: UpdatedTableProcessor = UpdatedTableProcessor(workerRunId)

  @volatile private var running: Option[RunningTable] = None

  /** The table this worker is currently processing with its start timestamp, or `None` while idle. */
  def currentTable: Option[RunningTable] = running

  private val workerSpark: SetOnce[SparkSession] = SetOnce.empty

  override protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce {
    logger.info(s"$name shutdown hook triggered")
    tryOrIgnore { workerSpark.foreach(SparkEnv.cancelAll(_)) }
  }

  /** Main loop: drains the shared dependency queue until it is empty or the task is cooperatively stopped (a sibling crashed, flipping `shouldContinue`).
    * Table-level errors are handled inside [[processOne]] and never escape; any other throwable propagates to `EntryPoint.main`, aborting the whole pool.
    */
  override def start(): Unit = {
    SparkEnv.ensureInitialized(SharedState.sparkConfig)

    SparkEnv.setPoolStrategy(PoolStrategy.Layered("lakehouse"))
    workerSpark.set(SparkSession.active)

    while (shouldContinue && SharedState.queue.pendingCount > 0) {
      SharedState.queue.pollOne() match {
        case None =>
          logger.debug("Sleeping.")
          Thread.sleep(WorkerTask.IdleSleepMs)
        case Some(item) =>
          // Table-level errors are fully handled inside processOne and never propagate here.
          processOne(item)
      }
    }
  }

  private def processOne(item: DagQueue.QueueItem[TableID, TableSpec[Entity]]): Unit = {
    val tableId = item.key
    val tableSpec = item.value

    if (EntryPoint.skippedTables.contains(tableId)) {
      val ancestor = firstFailedAncestor(tableId).getOrElse(tableId)
      val ancestorLabel = s"${ancestor.schema}.${ancestor.name}"
      val nowMs = System.currentTimeMillis()
      SharedState.recordOutcome(tableId, TableOutcome.SkippedByAncestor(ancestor))
      logger.debug(s"SKIP    $tableId  (ancestor failed: $ancestorLabel)")
      rowBuffer += TableRunRow(
        runId = SharedState.jobRunId,
        catalog = tableId.catalog,
        schema = tableId.schema,
        table = tableId.name,
        status = TableRunRow.Status_Skipped,
        startedAtMs = nowMs,
        endedAtMs = nowMs,
        workerName = name,
        errorClass = null,
        errorMessage = null,
        errorStack = null,
        skippedDueTo = ancestor.toString
      )
      SharedState.queue.complete(tableId)
      return
    }

    val t0 = System.currentTimeMillis()
    logger.debug(s"START   $tableId")
    running = Some(RunningTable(tableId, t0))

    // Tag every Spark job this table submits with a unique job tag. The watchdog (a different thread) cancels exactly
    // this table by that tag. Routed through `SparkJobTags` (the upcoming `SparkEnv.tag`/`cancel`): classic uses the
    // shared SparkContext (context-global), connect the session — both reach the worker's jobs without sharing state.
    val jobTag = WorkerTask.buildJobTag(tableId)
    val jobDesc = WorkerTask.buildJobDesc(tableId)
    SparkEnv.tag(jobTag)
    SparkEnv.setDescription(tableId.toString)

    val (status, errOpt): (String, Option[Throwable]) =
      try {
        Try(update(tableSpec)) match {
          case Success(_) =>
            SharedState.recordOutcome(tableId, TableOutcome.Updated)
            (TableRunRow.Status_Updated, None)
          case Failure(ex) =>
            // The watchdog adds the tableId to `cancelledForTimeout` *before* it cancels this table's job tag,
            // so on the surfaced "job cancelled" exception we can still attribute the cause.
            val wasTimedOut = EntryPoint.cancelledForTimeout.remove(tableId)
            if (wasTimedOut) {
              val elapsedMs = System.currentTimeMillis() - t0
              val limitMs = SharedState.orchestratorConfig.maxTableRuntimeMs.getOrElse(0L)
              val timeoutEx = new TableTimeoutException(tableId, elapsedMs, limitMs)
              logger.warn(
                s"TIMEOUT $tableId  elapsed=${elapsedMs / 1000}s limit=${limitMs / 1000}s — cancelled by watchdog; descendants will be skipped"
              )
              SharedState.recordOutcome(tableId, TableOutcome.Failed(timeoutEx))
              markDescendantsSkipped(tableId)
              (TableRunRow.Status_TimedOut, Some(timeoutEx))
            } else {
              logger.debug(s"FAIL    $tableId  ${ex.getClass.getSimpleName}: ${Option(ex.getMessage).getOrElse("<no message>")}", ex)
              SharedState.recordOutcome(tableId, TableOutcome.Failed(ex))
              markDescendantsSkipped(tableId)
              (TableRunRow.Status_Failed, Some(ex))
            }
        }
      } finally {
        running = None
        Try(SparkEnv.untag(jobTag))
        Try(SparkEnv.clearDescription())
      }
    val t1 = System.currentTimeMillis()
    val durSec = math.max(0L, (t1 - t0) / 1000L)
    val statusLabel = status match {
      case TableRunRow.Status_Updated  => "OK"
      case TableRunRow.Status_TimedOut => "TIMEOUT"
      case _                           => "FAILED"
    }
    logger.info(f"DONE    $tableId%-60s $statusLabel%-7s ${durSec}%5ds  worker=$name")
    rowBuffer += TableRunRow(
      runId = SharedState.jobRunId,
      catalog = tableId.catalog,
      schema = tableId.schema,
      table = tableId.name,
      status = status,
      startedAtMs = t0,
      endedAtMs = t1,
      workerName = name,
      errorClass = errOpt.map(_.getClass.getName).orNull,
      errorMessage = errOpt.map(e => Option(e.getMessage).getOrElse("<no message>")).orNull,
      errorStack = errOpt.map(TableRunRow.stackTraceOf).orNull,
      skippedDueTo = null
    )
    SharedState.queue.complete(tableId)
  }

  /** Update or create the target table described by `tableSpec`. Throws if `tableSpec` is not an `Updated` table. */
  private def update(tableSpec: TableSpec[Entity]): Unit = {
    val updated = tableSpec match {
      case ts: Updated => ts
      case _           => throw new IllegalArgumentException(s"TableSpec ${tableSpec.id} is not an Updated table and cannot be materialised")
    }

    updated.validateToRoot()
    tableManager.reconcileUpdatedTable(updated, asTarget = true)
    updatedTableProcessor.update(updated, SharedState.logicVersion)
  }

  private def markDescendantsSkipped(failedTable: TableID): Unit = {
    val desc = SharedState.transitiveDescendants.getOrElse(failedTable, Set.empty)
    desc.foreach(EntryPoint.skippedTables.add)
    if (desc.nonEmpty) logger.warn(s"Marked ${desc.size} descendant table(s) as skipped due to failure of $failedTable")
  }

  private def firstFailedAncestor(tableId: TableID): Option[TableID] = {
    val it = SharedState.results.asScala.iterator
    val transitive = SharedState.transitiveDescendants
    it.collectFirst { case (id, _: TableOutcome.Failed) if transitive.getOrElse(id, Set.empty).contains(tableId) => id }
  }
}

object WorkerTask {

  /** Spark job tag identifying every job a worker submits for one table. The worker attaches it via [[SparkJobTags.tag]] and the watchdog cancels by it via
    * [[SparkJobTags.cancel]] from another thread. Must contain no comma (Spark's tag separator).
    */
  def buildJobTag(id: TableID): String = s"${SharedState.jobRunId}/${id.catalog}.${id.schema}.${id.name}"
  def buildJobDesc(id: TableID): String = id.catalog + "." + id.schema + "." + id.name

  /** Backoff between empty polls of the in-memory dependency queue. A worker only sees an empty poll while pollable tables are still blocked on ancestors other
    * workers are computing — pure local backoff to avoid busy-spinning, no I/O involved, so a small fixed value is all that's needed.
    */
  private val IdleSleepMs: Long = 1000L
}

/** Marker exception raised by a Worker when the pool watchdog cancels a table that exceeded `OrchestratorConfig.maxTableRuntimeSeconds`. Wrapped inside a
  * regular [[TableOutcome.Failed]] so existing match sites keep compiling, but detected by the Worker to write a distinct `TIMED_OUT` status row to
  * `lakehouse_table_runs` (see `TableRunRow.Status_TimedOut`).
  */
final class TableTimeoutException(val tableId: TableID, val elapsedMs: Long, val limitMs: Long)
    extends RuntimeException(
      s"Table $tableId cancelled by watchdog after ${elapsedMs / 1000}s (limit ${limitMs / 1000}s)"
    )

/** A table a worker is currently processing, together with the wall-clock time (ms) the worker started it. Replaces the bare `(TableID, Long)` tuple so the
  * timestamp's meaning is explicit and the elapsed-time arithmetic lives in one place.
  */
final case class RunningTable(tableId: TableID, startedAtMs: Long) {

  /** Milliseconds this table has been running as of `nowMs` (never negative). */
  def elapsedMs(nowMs: Long): Long = math.max(0L, nowMs - startedAtMs)
}
