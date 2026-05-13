package ct.dna.lakehouse.core.jobs.orchestrator

import scala.util.Try

import ct.dna.utils.json.mapper
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.logging.PrintlnAppender
import ct.dna.utils.runtime.Configuration

/** Body of the long-lived "Monitor" task ([[OrchestratorTask.Monitor]]).
  *
  * Polls the heartbeat directory written by JobSetup ([[HeartbeatStore.RunInfo]]) and Workers ([[HeartbeatStore.WorkerStatus]]) every `statusIntervalSeconds`
  * and emits one consolidated `STATUS` line per tick to the task Output tab, e.g.
  *
  * {{{
  *   STATUS [my_catalog] 5/50 processed (3 ok, 1 failed, 1 skipped) | Worker_0 \u2192 schema.table_xy (42s) | Worker_1 idle
  * }}}
  *
  * The Monitor never touches Spark or the in-JVM `CatalogOrchestrator` state \u2014 it works purely off the on-disk heartbeat files, so it can run in its own
  * driver JVM without seeing Worker memory.
  *
  * Exit conditions (whichever fires first):
  *   - Run is complete: `run.json` is present, every worker has reported `done`, and `sum(completed+failed+skipped) >= totalTables`.
  *   - Wall-clock cap: `maxRuntimeSeconds > 0` and elapsed >= cap.
  *   - Setup never published `run.json` within the grace period (`max(30s, 2 \u00d7 statusIntervalSeconds)`) \u2014 task fails fast.
  *   - No-progress watchdog: `noProgressTimeoutSeconds > 0` and neither the processed counter nor any worker's `lastUpdateAtMs` has advanced for that many
  *     seconds \u2014 task fails fast (likely a worker JVM crash or stall).
  *   - Live-monitoring is disabled: `heartbeatDir = None`. We log a warning and exit cleanly so the task succeeds.
  */
private[orchestrator] object MonitorTaskRunner extends LoggingTrait {

  def run(task: OrchestratorTask.Monitor): Unit = {
    PrintlnAppender.replaceConsoleAppendersWithPrintlnAppenders()

    val parsed =
      Configuration
        .required("rootDir")
        .required("orchestratorConfig")
        .build(task.runtimeArgs)

    val cfg = mapper.readValue[OrchestratorConfig](parsed.getProperty("orchestratorConfig"))

    if (cfg.heartbeatDir.isEmpty) {
      logger.warn("Monitor: heartbeatDir is unset \u2014 live monitoring disabled, exiting cleanly")
      return
    }

    val runId = task.runId
    val intervalMs = math.max(1, cfg.statusIntervalSeconds.toLong) * 1000L
    val maxRuntimeMs = if (cfg.maxRuntimeSeconds > 0) cfg.maxRuntimeSeconds * 1000L else Long.MaxValue
    val startMs = System.currentTimeMillis()
    val deadline = startMs + maxRuntimeMs
    val setupGraceMs = math.max(30_000L, 2L * intervalMs)
    val noProgressMs = if (cfg.noProgressTimeoutSeconds > 0) cfg.noProgressTimeoutSeconds * 1000L else Long.MaxValue

    logger.warn(
      s"Monitor: polling heartbeatDir='${cfg.heartbeatDir.get}' runId=$runId interval=${intervalMs}ms" +
        s" setupGrace=${setupGraceMs}ms noProgressTimeout=${if (noProgressMs == Long.MaxValue) "off" else s"${cfg.noProgressTimeoutSeconds}s"}"
    )

    // Progress signal: monotonically non-decreasing pair of (processed-count, max lastUpdateAtMs across workers).
    // Reset the watchdog whenever either advances. Initialised to "never seen" so the first real reading always counts as progress.
    var lastProgressAtMs = startMs
    var lastProcessed = -1
    var lastMaxWorkerUpdateMs = -1L

    var done = false
    while (!done && System.currentTimeMillis() < deadline) {
      val tickMs = System.currentTimeMillis()
      val runInfoOpt = HeartbeatStore.readRunInfo(cfg, runId)
      val workers = HeartbeatStore.readAllWorkerStatuses(cfg, runId)
      logger.warn(formatStatusLine(runInfoOpt, workers))

      // Fast-fail #1: JobSetup never wrote run.json.
      if (runInfoOpt.isEmpty && (tickMs - startMs) >= setupGraceMs) {
        throw new RuntimeException(
          s"Monitor: run.json not found after ${setupGraceMs}ms \u2014 JobSetup likely failed before publishing run info (runId=$runId)"
        )
      }

      // Fast-fail #2: no-progress watchdog. Only armed once we actually have a run.json and at least one worker reporting,
      // otherwise the setup-grace check above is the relevant guard.
      val processed = workers.iterator.map(w => w.completed + w.failed + w.skipped).sum
      val maxWorkerUpdate = if (workers.isEmpty) -1L else workers.iterator.map(_.lastUpdateAtMs).max
      val advanced = processed > lastProcessed || maxWorkerUpdate > lastMaxWorkerUpdateMs
      if (advanced) {
        lastProgressAtMs = tickMs
        lastProcessed = processed
        lastMaxWorkerUpdateMs = maxWorkerUpdate
      } else if (runInfoOpt.isDefined && workers.nonEmpty && (tickMs - lastProgressAtMs) >= noProgressMs) {
        throw new RuntimeException(
          s"Monitor: no progress for ${cfg.noProgressTimeoutSeconds}s (processed=$processed/${runInfoOpt.get.totalTables}) \u2014 " +
            s"workers may have crashed or stalled (runId=$runId)"
        )
      }

      done = isRunComplete(runInfoOpt, workers)
      if (!done) Try(Thread.sleep(intervalMs))
    }

    if (done) logger.warn(s"Monitor: run $runId complete \u2014 exiting")
    else logger.warn(s"Monitor: maxRuntime hit (${cfg.maxRuntimeSeconds}s) \u2014 exiting")
  }

  /** Run is "done" when JobSetup published `run.json`, every reporting worker is in state `done`, and the sum of per-worker counters meets the planned table
    * count. We require at least one worker to have reported \u2014 otherwise an immediate "no workers yet" tick would be misread as completion.
    */
  private[orchestrator] def isRunComplete(
      runInfoOpt: Option[HeartbeatStore.RunInfo],
      workers: Seq[HeartbeatStore.WorkerStatus]
  ): Boolean = {
    val info = runInfoOpt.getOrElse(return false)
    if (workers.isEmpty) return false
    val allDone = workers.forall(_.state == HeartbeatStore.State_Done)
    val processed = workers.iterator.map(w => w.completed + w.failed + w.skipped).sum
    allDone && processed >= info.totalTables
  }

  private[orchestrator] def formatStatusLine(
      runInfoOpt: Option[HeartbeatStore.RunInfo],
      workers: Seq[HeartbeatStore.WorkerStatus]
  ): String = {
    val nowMs = System.currentTimeMillis()
    val total = runInfoOpt.map(_.totalTables).getOrElse(-1)
    val catalog = runInfoOpt.map(_.catalog).getOrElse("?")
    val processed = workers.iterator.map(w => w.completed + w.failed + w.skipped).sum
    val ok = workers.iterator.map(_.completed).sum
    val failed = workers.iterator.map(_.failed).sum
    val skipped = workers.iterator.map(_.skipped).sum
    val totalLabel = if (total >= 0) total.toString else "?"
    val pctLabel = if (total > 0) f" (${processed * 100.0 / total}%.0f%%)" else ""
    val header =
      s"STATUS [$catalog] $processed/$totalLabel$pctLabel processed (" +
        s"$ok ok, $failed failed, $skipped skipped)"
    if (workers.isEmpty) header + " — no workers reporting yet"
    else {
      val sorted = workers.sortBy(workerSortKey)
      val nameWidth = sorted.iterator.map(_.workerName.length).max
      val tableWidth = sorted.iterator.map(w => prettifyTable(w.currentTable).length).max.max(1)
      val rows = sorted.map(formatWorkerRow(_, nowMs, nameWidth, tableWidth))
      (header +: rows).mkString("\n  ")
    }
  }

  /** Strip the legacy `TableID(SchemaID(CatalogID(c),s),t)` wrapper to `s.t` so log lines stay compact even if a worker that hasn't been redeployed yet is
    * still writing the raw case-class `toString` into its heartbeat.
    */
  private val LegacyTableIdRegex = """TableID\(SchemaID\(CatalogID\([^)]*\),([^)]*)\),([^)]*)\)""".r
  private val WorkerNameSuffixRegex = """^(.*?)(\d+)$""".r

  private def workerSortKey(w: HeartbeatStore.WorkerStatus): (String, Long, String) =
    w.workerName match {
      case WorkerNameSuffixRegex(prefix, number) => (prefix, number.toLong, w.workerName)
      case _                                     => (w.workerName, Long.MaxValue, w.workerName)
    }

  private[orchestrator] def prettifyTable(raw: Option[String]): String = raw match {
    case None => ""
    case Some(s) =>
      LegacyTableIdRegex.findFirstMatchIn(s) match {
        case Some(m) => s"${m.group(1)}.${m.group(2)}"
        case None    => s
      }
  }

  private def formatWorkerRow(w: HeartbeatStore.WorkerStatus, nowMs: Long, nameWidth: Int, tableWidth: Int): String = {
    val name = w.workerName.padTo(nameWidth, ' ')
    val countersStr = s"[ok=${w.completed} fail=${w.failed} skip=${w.skipped}]"
    w.state match {
      case HeartbeatStore.State_Running =>
        val startedAt = w.currentStartedAtMs.getOrElse(nowMs)
        val elapsedSec = math.max(0L, (nowMs - startedAt) / 1000L)
        val table = prettifyTable(w.currentTable).padTo(tableWidth, ' ')
        f"$name → $table ${elapsedSec}%4ds  $countersStr"
      case HeartbeatStore.State_Done =>
        s"$name   ${" ".padTo(tableWidth, ' ')}  done  $countersStr"
      case _ =>
        s"$name   ${" ".padTo(tableWidth, ' ')}  idle  $countersStr"
    }
  }
}
