package ct.dna.lakehouse.core.jobs.orchestrator

import java.sql.Timestamp

import scala.jdk.CollectionConverters._
import scala.util.Try

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
  *      `OrchestratorConfig.summaryEnabled = false`.
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
      .required("orchestratorConfig")
      .withSparkConfig
      .build(task.runtimeArgs)
    SparkEnv.ensureInitialized(parsed.getSparkConfig)

    val cfg = Option(CatalogOrchestrator.orchestratorConfig.get()).getOrElse {
      throw new IllegalStateException(
        "OrchestratorConfig not set — JobSetup must run before Summary. Check the bundle's depends_on edges."
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

    val failedTables: String = if (failed > 0) {
      CatalogOrchestrator.results.asScala.toMap
        .collect { case (id, TableOutcome.Failed(ex)) => s"$id -> ${ex.getClass.getSimpleName}: ${Option(ex.getMessage).getOrElse("<no message>")}" }
        .take(20)
        .mkString(" | ")
    } else ""

    // Stable single-line summary — keep field order identical to dashboard scrapers.
    logger.info(
      s"SUMMARY catalog=$catalogName runId=$runId status=$status duration=${durationSec}s recorded=$total " +
        s"updated=$updated failed=$failed skipped=$skipped queueRemaining=$remaining " +
        s"setupError=${setupErr.map(_.getClass.getSimpleName).getOrElse("none")}"
    )
    if (failed > 0) logger.error(s"Failed tables (showing first ${math.min(failed, 20)}): $failedTables")

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
      cfg: OrchestratorConfig,
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
}
