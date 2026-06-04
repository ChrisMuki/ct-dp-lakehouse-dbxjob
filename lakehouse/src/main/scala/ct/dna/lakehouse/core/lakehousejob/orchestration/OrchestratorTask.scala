package ct.dna.lakehouse.core.lakehousejob.orchestration

import ct.dna.lakehouse.core.lakehousejob.SharedState
import ct.dna.lakehouse.core.lakehousejob.config.OrchestratorConfig
import ct.dna.lakehouse.core.lakehousejob.worker.RunningTable
import ct.dna.lakehouse.core.lakehousejob.worker.WorkerTask
import ct.dna.lakehouse.core.lakehousejob.worker.{EntryPoint => WorkerPool}
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.utils.ExecuteOnce
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.runtime.Task

/** "Orchestrator" — drives the run: launches `workerCount` in-process workers, tracks live status (+ watchdog), then turns the run RED on any failure.
  *
  * `start()` reads as the high-level run plan; the live status block formatting and the final flush + slowest-tables report live in the [[OrchestratorTask]]
  * companion object (the run's reporting), called both here and by the [[WorkerPool]] cleanup so the final block is emitted even on a crash.
  */
final case class OrchestratorTask() extends Task {

  lazy val runId: String = SharedState.jobRunId
  override val name: String = EntryPoint.Orchestrator
  override val uid: String = s"$name-$runId"

  protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce {
    logger.info(s"$name shutdown hook triggered")
  }

  override def start(): Unit = {
    SparkEnv.ensureInitialized(SharedState.sparkConfig)

    val poolSize = SharedState.workerCount
    logWatchdogMode(poolSize)

    WorkerPool.launch(poolSize)
    trackStatusUntilDone()
    WorkerPool.stopAll()

    failRunIfNeeded()
  }

  /** Drive the live STATUS block + per-table watchdog once per tick until every worker has finished. */
  private def trackStatusUntilDone(): Unit = while (WorkerPool.isRunning) {
    Thread.sleep(SharedState.orchestratorConfig.statusIntervalMs)
    logger.warn(OrchestratorTask.statusBlock(WorkerPool.runningTables, finalBlock = false))
    SharedState.orchestratorConfig.maxTableRuntimeMs.foreach(WorkerPool.enforceTimeouts)
  }

  private def logWatchdogMode(poolSize: Int): Unit = {
    logger.warn(s"Orchestrator: starting poolSize=$poolSize runId=$runId statusInterval=${SharedState.orchestratorConfig.statusIntervalMs}ms")
    SharedState.orchestratorConfig.maxTableRuntimeMs match {
      case Some(ms) =>
        logger.warn(
          s"Orchestrator: per-table watchdog enabled, maxTableRuntimeSeconds=${ms / 1000}. Tables exceeding this are cancelled by " +
            "cancelling their Spark job tag (`SparkContext.cancelJobsWithTag`) and recorded as TIMED_OUT in lakehouse_table_runs."
        )
      case None =>
        logger.warn("Orchestrator: per-table watchdog disabled (orchestratorConfig.maxTableRuntimeSeconds=None).")
    }
  }

  /** Turn the Databricks run RED on a bad outcome: any failed/timed-out table or an undrained queue. Returns normally only on a clean run. */
  private def failRunIfNeeded(): Unit = {
    WorkerPool.firstFailure.foreach(t => throw t)

    val catalogName = SharedState.catalogSpec.id.name
    val failed = SharedState.failedCount
    val remaining = SharedState.queue.size
    if (failed > 0 || remaining > 0) {
      val msg = s"Orchestrator run '$runId' (catalog=$catalogName) finished with failed=$failed, queueRemaining=$remaining"
      logger.error(msg)
      throw new IllegalStateException(msg)
    }
  }
}

/** Run reporting for the worker pool: the live STATUS block and the final flush + slowest-tables block. Reads run-wide counters from [[SharedState]];
  * per-worker running tables are passed in by the [[WorkerPool]].
  */
object OrchestratorTask extends LoggingTrait {

  /** Persist every per-table result row (when enabled) and log the final STATUS + slowest-tables blocks. Always safe to call. */
  private[lakehousejob] def flushAndReport(workers: Seq[WorkerTask], cfg: OrchestratorConfig, runId: String): Unit = {
    val allRows = workers.iterator.flatMap(_.rowBuffer.iterator).toSeq
    if (cfg.tableRunsEnabled) TableRunsWriter.appendBatch(cfg, allRows)
    logger.warn(statusBlock(Seq.empty, finalBlock = true))
    logger.warn(slowestTablesBlock(allRows, topN = 10))
  }

  /** Consolidated live status block: run-wide progress counters + the tables each worker is currently processing. */
  private[lakehousejob] def statusBlock(running: Seq[(WorkerTask, RunningTable)], finalBlock: Boolean): String = {
    val nowMs = System.currentTimeMillis()
    val startMs = SharedState.runStartMs
    val elapsed = formatHMS(nowMs - startMs)
    val catalog = SharedState.catalogSpec.id.name
    val runId = SharedState.jobRunId

    val totalTables = SharedState.totalTables
    val ok = SharedState.completedCount
    val failed = SharedState.failedCount
    val skipped = SharedState.skippedCount
    val processed = ok + failed + skipped
    val pctLabel = if (totalTables > 0) f" (${processed * 100.0 / totalTables}%.0f%%)" else ""
    val totalLabel = if (totalTables >= 0) totalTables.toString else "?"

    val runningQ = SharedState.queue.runningCount
    // pendingCount = enqueued-but-not-yet-polled, i.e. ready (Neutral) + blocked (BlockedByParent) combined.
    val pending = SharedState.queue.pendingCount

    val workerLines = renderWorkerLines(running, nowMs)

    val title = if (finalBlock) s"STATUS [$catalog]  FINAL  elapsed $elapsed   run $runId" else s"STATUS [$catalog]  elapsed $elapsed   run $runId"
    val sep = "=" * 78
    val sub = "-" * 78
    val sb = new StringBuilder
    sb.append('\n')
    sb.append(sep).append('\n')
    sb.append("  ").append(title).append('\n')
    sb.append(sub).append('\n')
    sb.append(f"  Progress    $processed%4d / $totalLabel%-4s$pctLabel   ok=$ok  failed=$failed  skipped=$skipped").append('\n')
    sb.append(f"  Queue       running=$runningQ  pending=$pending").append('\n')
    if (workerLines.nonEmpty) {
      sb.append("  Running").append('\n')
      workerLines.foreach(l => sb.append("    ").append(l).append('\n'))
    }
    sb.append(sep)
    sb.toString()
  }

  private def slowestTablesBlock(rows: Seq[TableRunRow], topN: Int): String = {
    val sep = "=" * 78
    val sub = "-" * 78
    val sb = new StringBuilder
    sb.append('\n')
    sb.append(sep).append('\n')
    sb.append(s"  TOP $topN SLOWEST TABLES (this run, all statuses)").append('\n')
    sb.append(sub).append('\n')
    if (rows.isEmpty) {
      sb.append("  (no tables recorded)").append('\n')
    } else {
      val ranked = rows.sortBy(-_.durationSeconds).take(topN)
      val labelW = ranked.iterator.map(r => s"${r.schema}.${r.table}".length).max
      ranked.foreach { r =>
        val label = s"${r.schema}.${r.table}".padTo(labelW, ' ')
        sb.append(f"  $label  ${r.status}%-9s ${r.durationSeconds}%6ds  worker=${r.workerName}").append('\n')
      }
    }
    sb.append(sep)
    sb.toString()
  }

  private def renderWorkerLines(running: Seq[(WorkerTask, RunningTable)], nowMs: Long): Seq[String] = {
    val rows = running.map { case (worker, rt) => (worker.name, s"${rt.tableId.schema}.${rt.tableId.name}", rt.startedAtMs) }
    if (rows.isEmpty) return Nil
    val workerWidth = rows.iterator.map(_._1.length).max
    val tableWidth = rows.iterator.map(_._2.length).max
    rows
      .sortBy { case (threadName, _, _) => threadName }
      .map { case (threadName, label, startMs) =>
        val elapsedSec = math.max(0L, (nowMs - startMs) / 1000L)
        f"${threadName.padTo(workerWidth, ' ')}  ${label.padTo(tableWidth, ' ')}  ${elapsedSec}%4ds"
      }
  }

  private def formatHMS(ms: Long): String = {
    val totalSec = math.max(0L, ms / 1000L)
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    f"$h%02d:$m%02d:$s%02d"
  }
}
