package ct.dna.lakehouse.core.jobs.orchestrator

import java.sql.Timestamp

import scala.jdk.CollectionConverters._
import scala.util.Try

import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.logging.PrintlnAppender
import ct.dna.utils.runtime.Configuration
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

/** Body of the "Summary" task ([[OrchestratorTask.Summary]]).
  *
  * Terminal observer. Runs with Databricks `run_if: ALL_DONE`, i.e. after every Worker and the Monitor have completed regardless of outcome. Two
  * responsibilities:
  *
  *   1. Append a per-run row to the configurable summary Delta table (`<summaryCatalog>.<summarySchema>.<summaryTable>`). The catalog and schema default to the
  *      deployment's `volumeCatalog` / `volumeSchema` so no extra Unity Catalog provisioning is required for the typical case. Disabled when
  *      `MonitoringConfig.summaryEnabled = false`.
  *   1. Emit the stable single-line `SUMMARY` log row consumed by grep / dashboard tooling and re-raise any `JobSetup` error so the task itself fails — giving
  *      operators a single failure surface to alert on.
  *
  * The Delta write is wrapped in `Try.recover` so a summary-table outage never masks the actual run outcome.
  */
private[orchestrator] object SummaryTaskRunner extends LoggingTrait {

  /** Schema of the summary table. Kept here (not in a SQL DDL string) so `CREATE TABLE IF NOT EXISTS` works without DBR-version-specific syntax and the column
    * list survives Delta schema evolution.
    */
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

  def run(task: OrchestratorTask.Summary): Unit = {
    PrintlnAppender.replaceConsoleAppendersWithPrintlnAppenders()

    val parsed = Configuration
      .required("rootDir")
      .required("monitoringConfig")
      .withSparkConfig
      .build(task.runtimeArgs)
    SparkEnv.ensureInitialized(parsed.getSparkConfig)

    val cfg = Option(CatalogOrchestrator.monitoringConfig.get()).getOrElse {
      throw new IllegalStateException(
        "MonitoringConfig not set — JobSetup must run before Summary. Check the bundle's depends_on edges."
      )
    }
    val catalogName = Option(CatalogOrchestrator.catalogSpec.get()).map(_.id.name).getOrElse("<unknown>")
    val runId = Option(CatalogOrchestrator.runId.get()).getOrElse(task.runId)

    val total = CatalogOrchestrator.results.size()
    val updated = CatalogOrchestrator.completedCount
    val failed = CatalogOrchestrator.failedCount
    val skipped = CatalogOrchestrator.skippedCount
    val remaining = CatalogOrchestrator.queue.size
    val setupErr = Option(CatalogOrchestrator.setupError.get())
    val status =
      if (setupErr.isDefined) "SETUP_FAILED"
      else if (failed > 0) "PARTIAL"
      else if (remaining > 0) "INCOMPLETE"
      else "OK"

    val nowMs = System.currentTimeMillis()
    val startMs = Option(CatalogOrchestrator.runStartMs.get()).map(_.longValue()).getOrElse(nowMs)
    val durationSec = (nowMs - startMs) / 1000L

    val resultsSnapshot = CatalogOrchestrator.results.asScala.toMap

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

    // Multi-line, operator-friendly report. Emitted BEFORE the stable single-line SUMMARY so dashboard scrapers still match.
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
        setupErr = setupErr,
        updatedEntries = updatedEntries,
        failedEntries = failedEntries,
        skippedEntries = skippedEntries
      )
    )

    // Stable single-line summary — keep field order identical to dashboard scrapers.
    logger.info(
      s"SUMMARY catalog=$catalogName runId=$runId status=$status duration=${durationSec}s recorded=$total " +
        s"updated=$updated failed=$failed skipped=$skipped queueRemaining=$remaining " +
        s"setupError=${setupErr.map(_.getClass.getSimpleName).getOrElse("none")}"
    )

    if (cfg.summaryEnabled) {
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
        setupErrName = setupErr.map(_.getClass.getSimpleName).orNull,
        failedTables = if (failedTables.isEmpty) null else failedTables
      )
    } else {
      logger.info(s"Summary Delta write skipped (summaryEnabled=false)")
    }

    // Re-raise a JobSetup error so the Summary task itself fails — gives operators a single failure surface to alert on.
    setupErr.foreach { t =>
      throw new RuntimeException(s"JobSetup failed earlier in this run: ${t.getMessage}", t)
    }
  }

  private def writeSummaryRow(
      cfg: MonitoringConfig,
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
      setupErrName: String,
      failedTables: String
  ): Unit = {
    // The deployment-side `AssetDirectory` is responsible for filling these from `volumeCatalog` / `volumeSchema`
    // before the orchestrator config is serialised, so by the time we get here both should be non-empty for any real run.
    val catalog = cfg.summaryCatalog.getOrElse {
      logger.warn("Summary catalog not configured (summaryCatalog=None) — skipping Delta write")
      return
    }
    val schema = cfg.summarySchema.getOrElse {
      logger.warn("Summary schema not configured (summarySchema=None) — skipping Delta write")
      return
    }
    val tableFqn = s"$catalog.$schema.${cfg.summaryTable}"

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
        setupErrName,
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

  private def idLabel(id: TableID): String = s"${id.schemaId.catalogId.name}.${id.schemaId.name}.${id.name}"

  /** Build a human-friendly multi-line report. Stable single-line `SUMMARY` is logged separately for scrapers. */
  private[orchestrator] def formatReport(
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
      setupErr: Option[Throwable],
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
    setupErr.foreach { t =>
      sb.append("  --------------------------------------------------------------------------------\n")
      sb.append(s"  SETUP ERROR  : ${t.getClass.getName}: ${Option(t.getMessage).getOrElse("<no message>")}\n")
    }

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

    // Compact per-table list: one line per table, status prefix. Useful when the catalog is small enough
    // (< MaxPerTableListed); for larger catalogs we already have the failure/skip sections above and the
    // full record lives in the table_runs Delta table.
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
