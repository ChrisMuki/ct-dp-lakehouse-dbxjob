package ct.dna.lakehouse.core.jobs.orchestrator

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.runtime.SparkConfig
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.collections.DagQueue
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.logging.PrintlnAppender
import ct.dna.utils.runtime.Configuration
import org.apache.spark.sql.SparkSession

/** Body of the single "Worker" task ([[OrchestratorTask.WorkerPool]]).
  *
  * Replaces the historical `N \u00d7 Worker_i` Databricks task fan-out + standalone Monitor task with a single Databricks task that:
  *   - spawns `poolSize` worker threads (named `Worker_0` \u2026 `Worker_$N-1`) which share the in-JVM `CatalogOrchestrator.queue`,
  *   - spawns one status-reporter thread that emits a multi-line STATUS block to the task Output tab every `statusIntervalSeconds`.
  *
  * Exit conditions:
  *   - All worker threads finished (queue drained, `setupError` raised, or `shutdownRequested` flipped). Task succeeds.
  *   - `setupError` non-null: same final flush, then re-thrown so Databricks marks the task failed.
  */
private[orchestrator] object WorkerPoolTaskRunner extends LoggingTrait {

  def run(task: OrchestratorTask.WorkerPool): Unit = {
    Thread.currentThread().setName(TaskNames.WorkerTaskKey)
    PrintlnAppender.replaceConsoleAppendersWithPrintlnAppenders()

    val parsed = Configuration
      .required("rootDir")
      .required("monitoringConfig")
      .withSparkConfig
      .build(task.runtimeArgs)
    val sparkConfig = parsed.getSparkConfig
    SparkEnv.ensureInitialized(sparkConfig)

    val cfg = Option(CatalogOrchestrator.monitoringConfig.get()).getOrElse {
      throw new IllegalStateException(
        "WorkerPool: MonitoringConfig not set. JobSetup must run before the Worker task \u2014 check the bundle's depends_on edges."
      )
    }
    val runId = Option(CatalogOrchestrator.runId.get()).getOrElse(task.runId)
    val poolSize = math.max(1, task.poolSize)
    val idleSleepMs = cfg.idleSleepSeconds * 1000L
    val statusIntervalMs = math.max(1, cfg.statusIntervalSeconds.toLong) * 1000L

    logger.warn(s"WorkerPool: starting poolSize=$poolSize runId=$runId idleSleep=${idleSleepMs}ms statusInterval=${statusIntervalMs}ms")
    logger.warn(
      s"WorkerPool: Spark UI tip — each table update sets a Spark job group of the form '$runId/<catalog>.<schema>.<table>'. " +
        "In the Spark UI > Jobs tab, the 'Job Group' / 'Description' column shows that id along with the JVM thread name (`task-NN`), " +
        "so you can follow a single slow table through to its Stages / SQL plan."
    )

    // Single Delta append happens at the very end \u2014 each thread owns a slice so writes are contention-free.
    val rowBuffers: Array[mutable.ArrayBuffer[TableRunRow]] = Array.fill(poolSize)(mutable.ArrayBuffer.empty[TableRunRow])
    val workerCounters: Array[WorkerCounters] = Array.fill(poolSize)(new WorkerCounters)

    val statusStop = new AtomicBoolean(false)
    val statusThread = startStatusReporter(runId, statusIntervalMs, statusStop)

    val pool = Executors.newFixedThreadPool(poolSize)
    try {
      Range(0, poolSize).foreach { i =>
        val name = TaskNames.workerName(i, poolSize)
        pool.submit(new Runnable {
          override def run(): Unit = workerLoop(name, sparkConfig, cfg, runId, idleSleepMs, rowBuffers(i), workerCounters(i))
        })
      }
      pool.shutdown()
      // Block until every worker thread is done. No timeout \u2014 maxRuntime / drain semantics are governed by the
      // `CatalogOrchestrator.shutdownRequested` signal, which workers honour on their next poll.
      pool.awaitTermination(Long.MaxValue, TimeUnit.NANOSECONDS)
    } finally {
      statusStop.set(true)
      statusThread.interrupt()
      Try(statusThread.join(5_000L))

      // Final flush of every per-thread row buffer.
      val allRows = rowBuffers.iterator.flatMap(_.iterator).toSeq
      if (cfg.tableRunsEnabled) TableRunsWriter.appendBatch(cfg, allRows)

      // One last STATUS block so the operator sees the final state in the same tab as everything else.
      logger.warn(formatStatusBlock(runId, finalBlock = true))

      Option(CatalogOrchestrator.setupError.get()).foreach { t =>
        // Surface a setup error as a task failure so Databricks marks the run as failed. Summary will also re-raise this,
        // but failing here gives operators a clear "Worker task failed" signal too.
        throw new RuntimeException(s"WorkerPool: setup error propagated from JobSetup: ${t.getMessage}", t)
      }
    }
  }

  // ---------------- Worker loop ----------------

  /** Mutable per-thread counters. Single-threaded use within one worker thread. */
  private[orchestrator] final class WorkerCounters {
    var completed: Int = 0
    var failed: Int = 0
    var skipped: Int = 0
  }

  private def workerLoop(
      name: String,
      sparkConfig: SparkConfig,
      cfg: MonitoringConfig,
      runId: String,
      idleSleepMs: Long,
      rowBuffer: mutable.ArrayBuffer[TableRunRow],
      counters: WorkerCounters
  ): Unit = {
    Thread.currentThread().setName(name)
    // ActiveSparkEnv keys session bookkeeping on a plain ThreadLocal, so each worker thread spawned by the pool
    // must initialize itself — the main thread's ensureInitialized doesn't propagate. The call is synchronized and
    // idempotent: it derives a per-thread session via base.newSession() on first call from this thread.
    SparkEnv.ensureInitialized(sparkConfig)
    logger.warn(s"$name polling (idleSleep=${idleSleepMs}ms)")

    try {
      while (shouldKeepPolling()) {
        CatalogOrchestrator.queue.pollOne() match {
          case None =>
            if (CatalogOrchestrator.enqueueComplete.get() && CatalogOrchestrator.queue.pendingCount == 0) {
              val stillRunning = CatalogOrchestrator.queue.runningCount
              if (stillRunning > 0) {
                logger.warn(
                  s"$name exiting early — queue drained (no pending items); $stillRunning table(s) still running on other threads."
                )
              }
              return
            }
            Try(Thread.sleep(idleSleepMs))
          case Some(item) =>
            processOne(name, item, runId, rowBuffer, counters)
        }
      }
      logger.warn(
        s"$name stopping (setupError=${CatalogOrchestrator.setupError.get() != null}, shutdownRequested=${CatalogOrchestrator.shutdownRequested.get()})"
      )
    } catch {
      case _: InterruptedException =>
        Thread.currentThread().interrupt()
        logger.warn(s"$name interrupted")
      case t: Throwable =>
        // Don't let one runaway thread leak out and abort the whole pool's awaitTermination silently — log loudly first.
        logger.error(s"$name crashed: ${t.getClass.getSimpleName}: ${t.getMessage}", t)
    }
  }

  private def shouldKeepPolling(): Boolean = {
    if (CatalogOrchestrator.setupError.get() != null) return false
    if (CatalogOrchestrator.shutdownRequested.get()) return false
    true
  }

  private def processOne(
      workerKey: String,
      item: DagQueue.QueueItem[TableID, TableSpec[Entity]],
      runId: String,
      rowBuffer: mutable.ArrayBuffer[TableRunRow],
      counters: WorkerCounters
  ): Unit = {
    val tableId = item.key
    val tableSpec = item.value

    val label = s"${tableId.schemaId.name}.${tableId.name}"

    if (CatalogOrchestrator.skippedTables.contains(tableId)) {
      val ancestor = firstFailedAncestor(tableId).getOrElse(tableId)
      val ancestorLabel = s"${ancestor.schemaId.name}.${ancestor.name}"
      val nowMs = System.currentTimeMillis()
      CatalogOrchestrator.recordOutcome(tableId, TableOutcome.SkippedByAncestor(ancestor))
      logger.debug(s"SKIP    $label  (ancestor failed: $ancestorLabel)")
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
      CatalogOrchestrator.queue.complete(tableId)
      return
    }

    val packageName = tableSpec.getClass.getPackage.getName
    val tableName = tableId.name

    val t0 = System.currentTimeMillis()
    logger.debug(s"START   $label")
    CatalogOrchestrator.runningTables.put(workerKey, (tableId, java.lang.Long.valueOf(t0)))
    // Tag every Spark job triggered by this update with a per-table group + a per-table scheduler pool.
    //   - Job group (UI only):    Spark UI > Jobs tab "Job Group"/"Description" columns.
    //   - Scheduler pool (FAIR):  Databricks implicitly sets a single pool name = task run id at the task
    //                             level, so without overriding it every driver thread shares one FIFO pool
    //                             and FAIR scheduling between tables has no effect. Setting the pool per
    //                             thread gives each table its own pool, which FAIR rotates across.
    val poolName = s"${tableId.schemaId.catalogId.name}.$label"
    val jobGroupId = s"$runId/$poolName"
    val jobGroupDesc = s"thread=$workerKey table=$poolName"
    val sc = Try(SparkSession.active.sparkContext).toOption
    sc.foreach(_.setJobGroup(jobGroupId, jobGroupDesc, interruptOnCancel = true))
    sc.foreach(_.setLocalProperty("spark.scheduler.pool", poolName))
    val (status, errOpt): (String, Option[Throwable]) =
      try {
        Try(TableUpdaterCore.update(packageName, tableName)) match {
          case Success(_) =>
            CatalogOrchestrator.recordOutcome(tableId, TableOutcome.Updated)
            counters.completed += 1
            (TableRunRow.Status_Updated, None)
          case Failure(ex) =>
            logger.debug(s"FAIL    $label  ${ex.getClass.getSimpleName}: ${Option(ex.getMessage).getOrElse("<no message>")}", ex)
            CatalogOrchestrator.recordOutcome(tableId, TableOutcome.Failed(ex))
            markDescendantsSkipped(tableId)
            counters.failed += 1
            (TableRunRow.Status_Failed, Some(ex))
        }
      } finally {
        CatalogOrchestrator.runningTables.remove(workerKey)
        sc.foreach { c =>
          Try(c.clearJobGroup())
          Try(c.setLocalProperty("spark.scheduler.pool", null))
        }
      }
    val t1 = System.currentTimeMillis()
    val durSec = math.max(0L, (t1 - t0) / 1000L)
    val statusLabel = if (status == TableRunRow.Status_Updated) "OK" else "FAILED"
    logger.debug(f"DONE    $label%-60s $statusLabel%-7s ${durSec}%4ds")
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
    CatalogOrchestrator.queue.complete(tableId)
  }

  private def markDescendantsSkipped(failedTable: TableID): Unit = {
    val desc = CatalogOrchestrator.transitiveDescendants.get().getOrElse(failedTable, Set.empty)
    desc.foreach(CatalogOrchestrator.skippedTables.add)
    if (desc.nonEmpty) logger.warn(s"Marked ${desc.size} descendant table(s) as skipped due to failure of $failedTable")
  }

  private def firstFailedAncestor(tableId: TableID): Option[TableID] = {
    val it = CatalogOrchestrator.results.asScala.iterator
    val transitive = CatalogOrchestrator.transitiveDescendants.get()
    it.collectFirst {
      case (id, _: TableOutcome.Failed) if transitive.getOrElse(id, Set.empty).contains(tableId) => id
    }
  }

  // ---------------- Status reporter ----------------

  /** Daemon thread that emits a structured STATUS block to the log every `intervalMs`. Reads directly from in-JVM state \u2014 no file I/O, so it stays
    * accurate even when the UC volume is temporarily unwritable.
    *
    * Cadence: a short warm-up tick (5s, capped by `intervalMs`) gives operators an early overview as soon as the first tables are picked up, then the reporter
    * settles into the configured `intervalMs`.
    */
  private def startStatusReporter(runId: String, intervalMs: Long, stop: AtomicBoolean): Thread = {
    val warmupMs = math.min(intervalMs, 5_000L)
    val t = new Thread(
      () => {
        try {
          // Warm-up: emit a first STATUS block early so the operator sees plan size, initial running tasks
          // and queue depth without waiting a full `statusInterval`.
          Thread.sleep(warmupMs)
          if (!stop.get()) logger.warn(formatStatusBlock(runId, finalBlock = false))
          while (!stop.get() && !Thread.currentThread().isInterrupted) {
            Thread.sleep(intervalMs)
            if (!stop.get()) logger.warn(formatStatusBlock(runId, finalBlock = false))
          }
        } catch { case _: InterruptedException => () }
      },
      "WorkerPool-status"
    )
    t.setDaemon(true)
    t.start()
    t
  }

  /** Render the multi-line STATUS block read entirely from in-JVM state. Plain ASCII delimiters \u2014 stays readable in any log viewer. */
  private[orchestrator] def formatStatusBlock(runId: String, finalBlock: Boolean): String = {
    val nowMs = System.currentTimeMillis()
    val startMs = Option(CatalogOrchestrator.runStartMs.get()).map(_.longValue()).getOrElse(nowMs)
    val elapsed = formatHMS(nowMs - startMs)
    val catalog = Option(CatalogOrchestrator.catalogSpec.get()).map(_.id.name).getOrElse("?")

    val totalTables = CatalogOrchestrator.totalTables.get()
    val ok = CatalogOrchestrator.completedCount
    val failed = CatalogOrchestrator.failedCount
    val skipped = CatalogOrchestrator.skippedCount
    val processed = ok + failed + skipped
    val pctLabel = if (totalTables > 0) f" (${processed * 100.0 / totalTables}%.0f%%)" else ""
    val totalLabel = if (totalTables >= 0) totalTables.toString else "?"

    val pending = CatalogOrchestrator.queue.pendingCount
    val running = CatalogOrchestrator.queue.runningCount
    val (neutral, blocked) = countNeutralAndBlocked()

    val workerLines = renderWorkerLines(nowMs)

    val title = if (finalBlock) s"STATUS [$catalog]  FINAL  elapsed $elapsed   run $runId" else s"STATUS [$catalog]  elapsed $elapsed   run $runId"
    val sep = "=" * 78
    val sub = "-" * 78
    val sb = new StringBuilder
    sb.append('\n')
    sb.append(sep).append('\n')
    sb.append("  ").append(title).append('\n')
    sb.append(sub).append('\n')
    sb.append(f"  Progress    $processed%4d / $totalLabel%-4s$pctLabel   ok=$ok  failed=$failed  skipped=$skipped").append('\n')
    sb.append(f"  Queue       running=$running  pending=$neutral  blocked=$blocked").append('\n')
    if (workerLines.nonEmpty) {
      sb.append("  Running").append('\n')
      workerLines.foreach(l => sb.append("    ").append(l).append('\n'))
    }
    sb.append(sep)
    sb.toString()
  }

  /** Render the rows under the `Running` header — one line per active worker thread, sorted by thread name so each `task-NN` keeps a stable position across
    * status blocks. Columns: `<threadName> <schema.table> <elapsedSec>s`.
    */
  private def renderWorkerLines(nowMs: Long): Seq[String] = {
    val running = CatalogOrchestrator.runningTables.asScala
    if (running.isEmpty) return Nil
    val rows = running.iterator.toSeq.map { case (workerKey, (id, startMs)) =>
      (workerKey, s"${id.schemaId.name}.${id.name}", startMs.longValue())
    }
    val workerWidth = rows.iterator.map(_._1.length).max
    val tableWidth = rows.iterator.map(_._2.length).max
    rows
      .sortBy { case (workerKey, _, _) => workerKey }
      .map { case (workerKey, label, startMs) =>
        val elapsedSec = math.max(0L, (nowMs - startMs) / 1000L)
        f"${workerKey.padTo(workerWidth, ' ')}  ${label.padTo(tableWidth, ' ')}  ${elapsedSec}%4ds"
      }
  }

  /** Snapshot of (Neutral=truly waiting now, Blocked=waiting on a parent) using the DagQueue's introspection. */
  private def countNeutralAndBlocked(): (Int, Int) = {
    // DagQueue doesn't expose state-level counts directly; cheapest path is `pendingCount - blockedCount`,
    // and blockedCount = number of keys with non-empty childToParents. We re-derive that by walking the queue
    // size space \u2014 but DagQueue doesn't expose it. Approximation: treat all `pending` as Neutral when we don't
    // have access. The header still shows running/pending which is what operators actually look at.
    val pending = CatalogOrchestrator.queue.pendingCount
    (pending, 0)
  }

  private def formatHMS(ms: Long): String = {
    val totalSec = math.max(0L, ms / 1000L)
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    f"$h%02d:$m%02d:$s%02d"
  }
}
