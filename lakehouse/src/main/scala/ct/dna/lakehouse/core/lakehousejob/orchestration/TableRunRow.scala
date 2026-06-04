package ct.dna.lakehouse.core.lakehousejob.orchestration

import java.io.PrintWriter
import java.io.StringWriter
import java.sql.Timestamp

import scala.jdk.CollectionConverters.SeqHasAsJava
import scala.util.Try

import ct.dna.lakehouse.core.lakehousejob.config.OrchestratorConfig
import ct.dna.utils.logging.LoggingTrait
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

/** One row in the per-table results Delta table (default `lakehouse_table_runs`). One row per (run_id, table). Status semantics:
  *
  *   - `UPDATED` — Worker successfully ran `TableUpdaterCore.update`.
  *   - `FAILED` — Worker caught an exception; `errorClass`/`errorMessage`/`errorStack` are populated.
  *   - `SKIPPED` — Table was not run because an ancestor failed; `skippedDueTo` carries the first such ancestor's id.
  *
  * Workers buffer their own outcomes in memory and append them as one batched write at task end (so we never do per-table Delta commits — see
  * [[TableRunsWriter.appendBatch]]). Summary appends an extra batch for SKIPPED rows that no Worker ever picked up (e.g. when JobSetup itself failed).
  */
private[lakehousejob] final case class TableRunRow(
    runId: String,
    catalog: String,
    schema: String,
    table: String,
    status: String,
    startedAtMs: Long,
    endedAtMs: Long,
    workerName: String,
    errorClass: String,
    errorMessage: String,
    errorStack: String,
    skippedDueTo: String
) {
  def durationSeconds: Long = math.max(0L, (endedAtMs - startedAtMs) / 1000L)
}

private[lakehousejob] object TableRunRow {

  val Status_Updated: String = "UPDATED"
  val Status_Failed: String = "FAILED"
  val Status_Skipped: String = "SKIPPED"

  /** Worker was cancelled by the WorkerPool watchdog after exceeding `OrchestratorConfig.maxTableRuntimeSeconds`. */
  val Status_TimedOut: String = "TIMED_OUT"

  def stackTraceOf(t: Throwable): String = {
    val sw = new StringWriter()
    t.printStackTrace(new PrintWriter(sw))
    sw.toString
  }
}

/** Spark-side writer for the per-table results Delta table. Wrapped in `Try.recover` so a Delta-side outage never masks the run outcome. */
private[lakehousejob] object TableRunsWriter extends LoggingTrait {

  private val schema: StructType = StructType(
    Seq(
      StructField("run_id", StringType, nullable = true),
      StructField("catalog", StringType, nullable = true),
      StructField("schema", StringType, nullable = true),
      StructField("table", StringType, nullable = true),
      StructField("status", StringType, nullable = true),
      StructField("started_at", TimestampType, nullable = true),
      StructField("ended_at", TimestampType, nullable = true),
      StructField("duration_seconds", LongType, nullable = true),
      StructField("worker_name", StringType, nullable = true),
      StructField("error_class", StringType, nullable = true),
      StructField("error_message", StringType, nullable = true),
      StructField("error_stack", StringType, nullable = true),
      StructField("skipped_due_to", StringType, nullable = true)
    )
  )

  /** Append a batch of rows. Caller owns enabling/disabling via `cfg.tableRunsEnabled`. No-op for empty input. Best-effort: a Delta failure is logged at WARN
    * and swallowed.
    */
  def appendBatch(cfg: OrchestratorConfig, rows: Seq[TableRunRow]): Unit = {
    if (rows.isEmpty) return
    if (cfg.tableRuns.isEmpty) {
      logger.warn("Table-runs table not configured (tableRuns=None) — skipping per-table Delta write")
      return
    }
    val tableFqn = cfg.tableRuns.map(_.fqn).get
    Try {
      ensureTable(tableFqn)
      val df = SparkSession.active.createDataFrame(rows.map(toRow(_)).asJava, schema)
      df.write.format("delta").mode("append").saveAsTable(tableFqn)
      logger.info(s"Appended ${rows.size} table-run row(s) to $tableFqn")
    }.recover { case t: Throwable =>
      logger.warn(s"Per-table Delta write to $tableFqn failed: ${t.getClass.getSimpleName}: ${t.getMessage} (run outcomes already logged)", t)
    }
    ()
  }

  private def ensureTable(tableFqn: String): Unit = {
    SparkSession.active.sql(
      s"""CREATE TABLE IF NOT EXISTS $tableFqn (
         |  run_id STRING,
         |  catalog STRING,
         |  schema STRING,
         |  table STRING,
         |  status STRING,
         |  started_at TIMESTAMP,
         |  ended_at TIMESTAMP,
         |  duration_seconds BIGINT,
         |  worker_name STRING,
         |  error_class STRING,
         |  error_message STRING,
         |  error_stack STRING,
         |  skipped_due_to STRING
         |) USING DELTA""".stripMargin
    )
    ()
  }

  private def toRow(r: TableRunRow): Row =
    Row(
      r.runId,
      r.catalog,
      r.schema,
      r.table,
      r.status,
      new Timestamp(r.startedAtMs),
      new Timestamp(r.endedAtMs),
      java.lang.Long.valueOf(r.durationSeconds),
      r.workerName,
      r.errorClass,
      r.errorMessage,
      r.errorStack,
      r.skippedDueTo
    )
}
