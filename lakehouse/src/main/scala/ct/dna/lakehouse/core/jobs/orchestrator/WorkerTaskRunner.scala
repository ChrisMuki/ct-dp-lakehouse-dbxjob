package ct.dna.lakehouse.core.jobs.orchestrator

import scala.collection.mutable
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.collections.DagQueue
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.logging.PrintlnAppender
import ct.dna.utils.runtime.Configuration
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext

/** Body of a `Worker` task. Polls `CatalogOrchestrator.queue` and runs `TableUpdaterCore.update` on each polled `TableSpec`. Console output for this runner is
  * throttled to WARN+ via a programmatic logger-level override applied to every `ct.dna.*` logger before any work runs — the appender-level `ThresholdFilter`
  * on `console.warn` is bypassed by `PrintlnAppender`, so we have to filter at the logger level instead. Lifecycle lines that operators care about
  * (`START`/`IN-PROGRESS`/`END`/`SKIP`) are emitted at WARN so they survive that filter and show up in the per-Worker Databricks **Output** tab — those tabs
  * are now the live status view (the standalone Monitor task was removed because its JVM couldn't see Worker state).
  */
private[orchestrator] object WorkerTaskRunner extends LoggingTrait {

  def run(task: OrchestratorTask.Worker): Unit = {
    Thread.currentThread().setName(task.name)

    // Route every log4j2 ConsoleAppender through System.out.println so log lines from ct.dna.* appear in the
    // Databricks task **Output** tab (in addition to the driver log file). Mirrors dp-pipeline-dbxjob's DBXLoadWorker.
    PrintlnAppender.replaceConsoleAppendersWithPrintlnAppenders()

    // `PrintlnAppender` bypasses appender-level filters, so the `ThresholdFilter level="WARN"` on `console.warn`
    // is ignored after the replacement above. Raise the logger level for every `ct.dna.*` logger to WARN to drop
    // INFO chatter at the logger level (which is checked before appenders). Worker JVM only — does not affect
    // Summary / JobSetup tasks which run in their own JVMs.
    raiseCtDnaLoggersToWarn()

    val parsed = Configuration
      .required("rootDir")
      .required("orchestratorConfig")
      .withSparkConfig
      .build(task.runtimeArgs)
    SparkEnv.ensureInitialized(parsed.getSparkConfig)

    // `depends_on: JobSetup` guarantees JobSetup has populated the singleton before any worker starts. If the field is
    // still null we are mis-wired in the bundle — fail fast rather than poll silently.
    val cfg = Option(CatalogOrchestrator.orchestratorConfig.get()).getOrElse {
      throw new IllegalStateException(
        s"Worker ${task.name}: OrchestratorConfig not set. JobSetup must run before Workers — check the bundle's depends_on edges."
      )
    }

    val idleSleepMs = cfg.idleSleepSeconds * 1000L
    val heartbeatIntervalMs = math.max(1, cfg.statusIntervalSeconds.toLong) * 1000L
    val runId = Option(CatalogOrchestrator.runId.get()).getOrElse(task.runId)
    // Per-task buffer of TableRunRow entries. One Delta append at the end of run() flushes everything to lakehouse_table_runs.
    val rowBuffer: mutable.ArrayBuffer[TableRunRow] = mutable.ArrayBuffer.empty[TableRunRow]
    // Per-worker counters mirrored to heartbeat JSON for the live Monitor.
    val counters = new WorkerCounters()

    logger.warn(s"Worker '${task.name}' polling (idleSleep=${idleSleepMs}ms, heartbeatInterval=${heartbeatIntervalMs}ms)")
    publishStatus(cfg, runId, task.name, HeartbeatStore.State_Idle, currentTable = None, currentStartedAtMs = None, counters)

    try {
      while (shouldKeepPolling(task)) {
        CatalogOrchestrator.queue.pollOne() match {
          case None =>
            if (CatalogOrchestrator.enqueueComplete.get() && CatalogOrchestrator.queue.size == 0) {
              // Queue is fully drained and Setup has finished enqueuing — we're done.
              return
            }
            Try(Thread.sleep(idleSleepMs))
          case Some(item) =>
            processOne(task.name, item, heartbeatIntervalMs, runId, rowBuffer, cfg, counters)
        }
      }
      logger.warn(s"Worker '${task.name}' stopping (keepRunning=${task.shouldKeepRunning}, setupError=${CatalogOrchestrator.setupError.get() != null})")
    } finally {
      // Always attempt the per-table flush — even on shutdown / setupError — so we never lose what this Worker observed.
      if (cfg.tableRunsEnabled) TableRunsWriter.appendBatch(cfg, rowBuffer.toSeq)
      // Final heartbeat: tell the Monitor we're done so it can decide when to exit.
      publishStatus(cfg, runId, task.name, HeartbeatStore.State_Done, currentTable = None, currentStartedAtMs = None, counters)
    }
  }

  private def shouldKeepPolling(task: OrchestratorTask.Worker): Boolean = {
    if (!task.shouldKeepRunning) return false
    if (CatalogOrchestrator.setupError.get() != null) return false
    if (CatalogOrchestrator.shutdownRequested.get()) return false
    true
  }

  private def processOne(
      workerKey: String,
      item: DagQueue.QueueItem[TableID, TableSpec[Entity]],
      heartbeatIntervalMs: Long,
      runId: String,
      rowBuffer: mutable.ArrayBuffer[TableRunRow],
      cfg: OrchestratorConfig,
      counters: WorkerCounters
  ): Unit = {
    val tableId = item.key
    val tableSpec = item.value

    // Skip path: this table's run was cancelled because of a failing ancestor.
    if (CatalogOrchestrator.skippedTables.contains(tableId)) {
      val ancestor = firstFailedAncestor(tableId).getOrElse(tableId)
      val nowMs = System.currentTimeMillis()
      CatalogOrchestrator.recordOutcome(tableId, TableOutcome.SkippedByAncestor(ancestor))
      logger.warn(s"[${Thread.currentThread().getName}] SKIP  $tableId reason=ancestor_failed ancestor=$ancestor")
      rowBuffer += TableRunRow(
        runId = runId,
        catalog = tableId.schemaId.catalogId.name,
        schema = tableId.schemaId.name,
        table = tableId.name,
        status = TableRunRow.Status_Skipped,
        startedAtMs = nowMs,
        endedAtMs = nowMs,
        workerName = workerKey,
        errorClass = null,
        errorMessage = null,
        errorStack = null,
        skippedDueTo = ancestor.toString
      )
      counters.skipped += 1
      publishStatus(cfg, runId, workerKey, HeartbeatStore.State_Idle, currentTable = None, currentStartedAtMs = None, counters)
      CatalogOrchestrator.queue.complete(tableId)
      return
    }

    val packageName = tableSpec.getClass.getPackage.getName
    val tableName = tableId.name
    val thread = Thread.currentThread().getName

    val t0 = System.currentTimeMillis()
    logger.warn(s"[$thread] START $tableId")
    // Use a human-friendly `schema.table` label in the heartbeat so the Monitor doesn't have to render the raw
    // `TableID(SchemaID(CatalogID(...),...),...)` case-class toString. The catalog is already shown in the run header.
    val tableLabel = s"${tableId.schemaId.name}.${tableId.name}"
    publishStatus(
      cfg,
      runId,
      workerKey,
      HeartbeatStore.State_Running,
      currentTable = Some(tableLabel),
      currentStartedAtMs = Some(t0),
      counters
    )
    // Publish the live "this worker is busy with this table" view (kept for in-JVM consumers; cross-task observers use HeartbeatStore).
    CatalogOrchestrator.runningTables.put(workerKey, (tableId, java.lang.Long.valueOf(t0)))
    val heartbeat = startHeartbeat(thread, tableId.toString, t0, heartbeatIntervalMs)
    val (outcomeLabel, status, errOpt): (String, String, Option[Throwable]) =
      try {
        Try(TableUpdaterCore.update(packageName, tableName)) match {
          case Success(_) =>
            CatalogOrchestrator.recordOutcome(tableId, TableOutcome.Updated)
            counters.completed += 1
            ("Updated", TableRunRow.Status_Updated, None)
          case Failure(ex) =>
            logger.error(s"[$thread] Update failed for $tableId: ${ex.getMessage}", ex)
            CatalogOrchestrator.recordOutcome(tableId, TableOutcome.Failed(ex))
            markDescendantsSkipped(tableId)
            counters.failed += 1
            (s"Failed(${ex.getClass.getSimpleName})", TableRunRow.Status_Failed, Some(ex))
        }
      } finally {
        heartbeat.interrupt()
        CatalogOrchestrator.runningTables.remove(workerKey)
      }
    val t1 = System.currentTimeMillis()
    val durationMs = t1 - t0
    logger.warn(s"[$thread] END   $tableId outcome=$outcomeLabel durationMs=$durationMs")
    rowBuffer += TableRunRow(
      runId = runId,
      catalog = tableId.schemaId.catalogId.name,
      schema = tableId.schemaId.name,
      table = tableId.name,
      status = status,
      startedAtMs = t0,
      endedAtMs = t1,
      workerName = workerKey,
      errorClass = errOpt.map(_.getClass.getName).orNull,
      errorMessage = errOpt.map(e => Option(e.getMessage).getOrElse("<no message>")).orNull,
      errorStack = errOpt.map(TableRunRow.stackTraceOf).orNull,
      skippedDueTo = null
    )
    publishStatus(cfg, runId, workerKey, HeartbeatStore.State_Idle, currentTable = None, currentStartedAtMs = None, counters)
    // Always complete: a failed parent must release its children so workers can pull and mark them as skipped.
    CatalogOrchestrator.queue.complete(tableId)
  }

  /** Mutable per-Worker counters mirrored to the heartbeat JSON. Single-threaded use within a Worker JVM. */
  private final class WorkerCounters {
    var completed: Int = 0
    var failed: Int = 0
    var skipped: Int = 0
  }

  private def publishStatus(
      cfg: OrchestratorConfig,
      runId: String,
      workerName: String,
      state: String,
      currentTable: Option[String],
      currentStartedAtMs: Option[Long],
      counters: WorkerCounters
  ): Unit =
    HeartbeatStore.writeWorkerStatus(
      cfg,
      runId,
      HeartbeatStore.WorkerStatus(
        workerName = workerName,
        state = state,
        currentTable = currentTable,
        currentStartedAtMs = currentStartedAtMs,
        completed = counters.completed,
        failed = counters.failed,
        skipped = counters.skipped,
        lastUpdateAtMs = System.currentTimeMillis()
      )
    )

  /** Background daemon that emits `IN-PROGRESS $tableId durationMs=…` every `intervalMs` while a worker is updating a table. Returned thread should be
    * `interrupt()`ed when the update completes (the heartbeat treats `InterruptedException` as a clean stop signal).
    */
  private def startHeartbeat(thread: String, tableLabel: String, startMs: Long, intervalMs: Long): Thread = {
    val t = new Thread(
      () => {
        try {
          while (!Thread.currentThread().isInterrupted) {
            Thread.sleep(intervalMs)
            val elapsedMs = System.currentTimeMillis() - startMs
            logger.warn(s"[$thread] IN-PROGRESS $tableLabel durationMs=$elapsedMs")
          }
        } catch { case _: InterruptedException => () }
      },
      s"$thread-heartbeat"
    )
    t.setDaemon(true)
    t.start()
    t
  }

  /** Adds every transitive descendant of `failedTable` to the global skip-set. Workers that later poll those tables will see them as skipped instead of running
    * them. Note: this only protects descendants that have not been polled yet — already-running descendants can't be cancelled with the current `DagQueue`
    * primitives. See "Future improvements" in the project notes.
    */
  private def markDescendantsSkipped(failedTable: TableID): Unit = {
    val desc = CatalogOrchestrator.transitiveDescendants.get().getOrElse(failedTable, Set.empty)
    desc.foreach(CatalogOrchestrator.skippedTables.add)
    if (desc.nonEmpty) logger.warn(s"Marked ${desc.size} descendant table(s) as skipped due to failure of $failedTable")
  }

  private def firstFailedAncestor(tableId: TableID): Option[TableID] = {
    import scala.jdk.CollectionConverters._
    val it = CatalogOrchestrator.results.asScala.iterator
    val transitive = CatalogOrchestrator.transitiveDescendants.get()
    it.collectFirst {
      case (id, _: TableOutcome.Failed) if transitive.getOrElse(id, Set.empty).contains(tableId) => id
    }
  }

  /** Programmatically force every existing `ct.dna.*` logger (and the package roots themselves) to WARN. Required because `PrintlnAppender` ignores
    * appender-level `ThresholdFilter`s, so console suppression has to happen at the logger level.
    */
  private[orchestrator] def raiseCtDnaLoggersToWarn(): Unit = {
    import scala.jdk.CollectionConverters._
    val ctx = LogManager.getContext(false).asInstanceOf[LoggerContext]
    val config = ctx.getConfiguration
    // Pin the package roots so loggers created later inherit WARN.
    Seq("ct.dna", "ct.dna.lakehouse", "ct.dna.lakehouse.core.jobs.orchestrator").foreach { name =>
      Option(config.getLoggerConfig(name)).filter(_.getName == name).foreach(_.setLevel(Level.WARN))
    }
    // Also lower any already-instantiated child loggers under ct.dna.*.
    config.getLoggers.asScala.foreach { case (name, lc) =>
      if (name.startsWith("ct.dna")) lc.setLevel(Level.WARN)
    }
    ctx.updateLoggers()
  }
}
