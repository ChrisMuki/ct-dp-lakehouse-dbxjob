package ct.dna.lakehouse.core.lakehousejob.summary

import java.sql.Timestamp

import scala.jdk.CollectionConverters._
import scala.util.Try

import ct.dna.lakehouse.core.catalog.TableFQN
import ct.dna.lakehouse.core.lakehousejob.SharedState
import ct.dna.lakehouse.core.lakehousejob.TableOutcome
import ct.dna.lakehouse.core.lakehousejob.config.SummaryConfig
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.runtime.PoolStrategy
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.utils.ExecuteOnce
import ct.dna.utils.runtime.Task
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

/** Terminal observer task: writes the per-run row to the summary Delta table and emits the final SUMMARY log line. */
final case class SummaryTask() extends Task {

  lazy val runId: String = SharedState.jobRunId
  override val name: String = getClass.getSimpleName
  override val uid: String = s"$name-$runId"

  protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce {
    logger.info(s"$name shutdown hook triggered")
  }

  override def start(): Unit = {

    SparkEnv.ensureInitialized(SharedState.sparkConfig)
    SparkEnv.setPoolStrategy(PoolStrategy.Layered("lakehouse"))

    val initialized: Boolean = SharedState.isInitialized

    val cfg = SharedState.summaryConfig

    val catalogName = SharedState.catalogSpec.id.name

    val total = SharedState.results.size()
    val updated = SharedState.completedCount
    val failed = SharedState.failedCount
    val skipped = SharedState.skippedCount
    val remaining = if (initialized) SharedState.queue.size else 0

    val status =
      if (failed > 0) "PARTIAL"
      else if (remaining > 0) "INCOMPLETE"
      else "OK"

    val nowMs = System.currentTimeMillis()
    val startMs = if (initialized) SharedState.runStartMs else nowMs
    val durationSec = (nowMs - startMs) / 1000L

    val resultsSnapshot = SharedState.results.asScala.toMap

    val updatedEntries: Seq[TableID] = resultsSnapshot.iterator
      .collect { case (id, TableOutcome.Updated) => id }
      .toSeq
      .sortBy(_.toString)

    val failedEntries: Seq[(TableID, Throwable)] = resultsSnapshot.iterator
      .collect { case (id, TableOutcome.Failed(ex)) =>
        id -> ex
      }
      .toSeq
      .sortBy { case (id, _) => id.toString }

    val skippedEntries: Seq[(TableID, TableID)] = resultsSnapshot.iterator
      .collect { case (id, TableOutcome.SkippedByAncestor(anc)) =>
        id -> anc
      }
      .toSeq
      .sortBy { case (id, _) => id.toString }

    val failedTables: String = if (failedEntries.nonEmpty) {
      failedEntries
        .take(20)
        .map { case (id, ex) => s"$id -> ${ex.getClass.getSimpleName}: ${Option(ex.getMessage).getOrElse("<no message>")}" }
        .mkString(" | ")
    } else ""

    logger.info(
      formatReport(
        runId = runId,
        catalogName = catalogName,
        status = status,
        startMs = startMs,
        endMs = nowMs,
        durationSec = durationSec,
        total = total,
        updated = updated,
        failed = failed,
        skipped = skipped,
        remaining = remaining,
        updatedEntries = updatedEntries,
        failedEntries = failedEntries,
        skippedEntries = skippedEntries
      )
    )

    // Stable single-line summary — keep field order identical to dashboard scrapers.
    logger.info(
      s"SUMMARY catalog=$catalogName runId=$runId status=$status duration=${durationSec}s recorded=$total " +
        s"updated=$updated failed=$failed skipped=$skipped queueRemaining=$remaining"
    )

    if (cfg.target.isDefined) {
      writeSummaryRow(
        cfg = cfg,
        runId = runId,
        catalogName = catalogName,
        startMs = startMs,
        endMs = nowMs,
        durationSec = durationSec,
        status = status,
        total = total,
        updated = updated,
        failed = failed,
        skipped = skipped,
        remaining = remaining,
        failedTables = if (failedTables.isEmpty) null else failedTables
      )
    } else {
      logger.info("Summary Delta write skipped (target=None)")
    }

    // Summary is a pure terminal observer: it reports the run outcome and writes the per-run Delta row, but never
    // fails its own task and never logs failures. The WorkerPool task is the single place that turns the Databricks
    // run RED and logs the failure surface. Because Summary runs with `run_if: ALL_DONE`, it still executes — and
    // stays GREEN — even after WorkerPool has failed.
  }

  private val summarySchema: StructType = StructType(
    Seq(
      StructField("run_id", StringType, nullable = true),
      StructField("catalog", StringType, nullable = true),
      StructField("started_at", TimestampType, nullable = true),
      StructField("ended_at", TimestampType, nullable = true),
      StructField("duration_seconds", LongType, nullable = true),
      StructField("status", StringType, nullable = true),
      StructField("total_recorded", IntegerType, nullable = true),
      StructField("updated", IntegerType, nullable = true),
      StructField("failed", IntegerType, nullable = true),
      StructField("skipped", IntegerType, nullable = true),
      StructField("queue_remaining", IntegerType, nullable = true),
      StructField("setup_error", StringType, nullable = true),
      StructField("failed_tables", StringType, nullable = true)
    )
  )

  private def writeSummaryRow(
      cfg: SummaryConfig,
      runId: String,
      catalogName: String,
      startMs: Long,
      endMs: Long,
      durationSec: Long,
      status: String,
      total: Int,
      updated: Int,
      failed: Int,
      skipped: Int,
      remaining: Int,
      failedTables: String
  ): Unit = {
    val tableFqn = cfg.target.map(_.fqn).getOrElse {
      logger.warn("Summary table not configured (target=None) — skipping Delta write")
      return
    }

    Try {
      val spark = SparkSession.active
      spark.sql(
        s"""CREATE TABLE IF NOT EXISTS $tableFqn (
           |  run_id STRING,
           |  catalog STRING,
           |  started_at TIMESTAMP,
           |  ended_at TIMESTAMP,
           |  duration_seconds BIGINT,
           |  status STRING,
           |  total_recorded INT,
           |  updated INT,
           |  failed INT,
           |  skipped INT,
           |  queue_remaining INT,
           |  setup_error STRING,
           |  failed_tables STRING
           |) USING DELTA""".stripMargin
      )

      val row = Row(
        runId,
        catalogName,
        new Timestamp(startMs),
        new Timestamp(endMs),
        java.lang.Long.valueOf(durationSec),
        status,
        Integer.valueOf(total),
        Integer.valueOf(updated),
        Integer.valueOf(failed),
        Integer.valueOf(skipped),
        Integer.valueOf(remaining),
        "",
        failedTables
      )
      val df = spark.createDataFrame(java.util.Arrays.asList(row), summarySchema)
      df.write.format("delta").mode("append").saveAsTable(tableFqn)
      logger.info(s"Summary row appended to $tableFqn (runId=$runId, status=$status)")
    }.recover { case t: Throwable =>
      logger.warn(s"Summary Delta write to $tableFqn failed: ${t.getClass.getSimpleName}: ${t.getMessage} (run outcome already logged above)", t)
    }
    ()
  }

  // ---- Pretty report ----

  private val MaxFailedListed: Int = 50
  private val MaxSkippedListed: Int = 50
  private val MaxPerTableListed: Int = 200

  private def idLabel(id: TableID): String = TableFQN(id.catalog, id.schema, id.name).fqn

  private[lakehousejob] def formatReport(
      runId: String,
      catalogName: String,
      status: String,
      startMs: Long,
      endMs: Long,
      durationSec: Long,
      total: Int,
      updated: Int,
      failed: Int,
      skipped: Int,
      remaining: Int,
      updatedEntries: Seq[TableID],
      failedEntries: Seq[(TableID, Throwable)],
      skippedEntries: Seq[(TableID, TableID)]
  ): String = {
    val tsFmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")
    tsFmt.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))
    val startedAt = tsFmt.format(new java.util.Date(startMs))
    val endedAt = tsFmt.format(new java.util.Date(endMs))
    val durationHuman = humanDuration(durationSec)
    val successPctStr =
      if (total <= 0) "n/a"
      else f"${(updated.toDouble / total.toDouble) * 100.0}%.1f%%"

    val sb = new StringBuilder
    sb.append('\n')
    sb.append("================================================================================\n")
    sb.append(s"  RUN REPORT  —  catalog=$catalogName  runId=$runId  status=$status\n")
    sb.append("================================================================================\n")
    sb.append(f"  Started      : $startedAt%n")
    sb.append(f"  Ended        : $endedAt%n")
    sb.append(f"  Duration     : $durationHuman ($durationSec s)%n")
    sb.append("  --------------------------------------------------------------------------------\n")
    sb.append(f"  Tables total : $total%6d%n")
    sb.append(f"    updated    : $updated%6d   ($successPctStr success)%n")
    sb.append(f"    failed     : $failed%6d%n")
    sb.append(f"    skipped    : $skipped%6d   (descendant of a failed table)%n")
    sb.append(f"  Queue remain : $remaining%6d   (not picked up before pool drained)%n")

    if (failedEntries.nonEmpty) {
      sb.append("  --------------------------------------------------------------------------------\n")
      sb.append(s"  Failed tables (${failedEntries.size})")
      if (failedEntries.size > MaxFailedListed) sb.append(s" — showing first $MaxFailedListed")
      sb.append(":\n")

      val byClass = failedEntries.groupBy { case (_, ex) => ex.getClass.getSimpleName }.toSeq.sortBy { case (_, xs) => -xs.size }
      byClass.foreach { case (cls, xs) =>
        sb.append(f"    [$cls%s] ${xs.size}%d%n")
      }

      sb.append("\n")
      failedEntries.take(MaxFailedListed).foreach { case (id, ex) =>
        val msg = Option(ex.getMessage).getOrElse("<no message>")
        sb.append(s"    - $id\n")
        sb.append(s"        ${ex.getClass.getSimpleName}: ${oneLine(msg, 240)}\n")
      }
      if (failedEntries.size > MaxFailedListed) {
        sb.append(s"    … ${failedEntries.size - MaxFailedListed} more (see the table_runs Delta table for the full list)\n")
      }
    }

    if (skippedEntries.nonEmpty) {
      sb.append("  --------------------------------------------------------------------------------\n")
      val byAncestor = skippedEntries.groupBy { case (_, anc) => anc }.toSeq.sortBy { case (_, xs) => -xs.size }
      sb.append(s"  Skipped (${skippedEntries.size}) — grouped by failed ancestor:\n")
      byAncestor.take(MaxSkippedListed).foreach { case (anc, xs) =>
        sb.append(f"    - $anc%s  ⇒ ${xs.size}%d descendant table(s)%n")
      }
      if (byAncestor.size > MaxSkippedListed) {
        sb.append(s"    … ${byAncestor.size - MaxSkippedListed} more ancestor group(s)\n")
      }
    }

    val allEntries: Seq[(TableID, String)] =
      updatedEntries.map(id => id -> "OK") ++
        failedEntries.map { case (id, _) => id -> "FAIL" } ++
        skippedEntries.map { case (id, _) => id -> "SKIP" }
    if (allEntries.nonEmpty && allEntries.size <= MaxPerTableListed) {
      sb.append("  --------------------------------------------------------------------------------\n")
      sb.append(s"  Per-table outcomes (${allEntries.size}):\n")
      val labelWidth = math.min(80, allEntries.iterator.map { case (id, _) => idLabel(id).length }.max)
      allEntries.sortBy { case (id, _) => idLabel(id) }.foreach { case (id, s) =>
        sb.append(f"    $s%-4s  ${idLabel(id).padTo(labelWidth, ' ')}%n")
      }
    } else if (allEntries.size > MaxPerTableListed) {
      sb.append("  --------------------------------------------------------------------------------\n")
      sb.append(s"  Per-table outcomes: ${allEntries.size} entries — list omitted (cap=$MaxPerTableListed); see table_runs Delta for the full record.\n")
    }

    sb.append("================================================================================")
    sb.toString
  }

  private def humanDuration(totalSec: Long): String = {
    val s = math.max(0L, totalSec)
    val h = s / 3600L
    val m = (s % 3600L) / 60L
    val sec = s % 60L
    if (h > 0) f"${h}h ${m}%02dm ${sec}%02ds"
    else if (m > 0) f"${m}m ${sec}%02ds"
    else f"${sec}s"
  }

  private def oneLine(s: String, max: Int): String = {
    val flat = s.replace('\n', ' ').replace('\r', ' ').replaceAll("\\s+", " ").trim
    if (flat.length <= max) flat else flat.take(max - 1) + "…"
  }
}
