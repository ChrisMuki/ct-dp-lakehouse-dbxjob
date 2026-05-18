package ct.dna.lakehouse.tools.fx_rates

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

/** As-of lookup helpers for the `dw_tx.fin_fxrates.fxrates` table.
  *
  * For a request `(rate_date, fcurr, tcurr, kurst)` the lookup returns the `final_rate` from the row in `fxrates` with the greatest `rate_date <=` the
  * requested date for that `(fcurr, tcurr, kurst)`. If no such row exists, `final_rate` is `null`. When `fcurr == tcurr`, `final_rate` is `1` regardless of
  * whether a row exists.
  *
  * The DataFrame API is the hot path. The scalar wrapper is a thin convenience for ad-hoc / driver-side use and triggers a Spark action.
  */
object FxRateLookup {

  private val rateDecimalType = "decimal(38,10)"
  private val oneRate = lit(1).cast(rateDecimalType)

  /** Enrich `requestsDf` with an as-of `final_rate` column from `fxratesDf`.
    *
    * The caller supplies the fxrates DataFrame, typically:
    * {{{
    *   val fxratesDf = SparkSession.active.table("dw_tx.fin_fxrates.fxrates")
    * }}}
    *
    * @param requestsDf
    *   DataFrame containing at least the four request columns. All input columns are preserved; only `outputCol` is appended.
    * @param fxratesDf
    *   DataFrame matching the `fx_conversion_rates` schema with columns `rate_date`, `fcurr`, `tcurr`, `kurst`, `final_rate`.
    * @param requestDateCol
    *   Name of the date column in `requestsDf` (must be `DATE`).
    * @param fcurrCol
    *   Name of the from-currency column in `requestsDf`.
    * @param tcurrCol
    *   Name of the to-currency column in `requestsDf`.
    * @param kurstCol
    *   Name of the rate-type column in `requestsDf`.
    * @param outputCol
    *   Name of the column to append with the looked-up `final_rate` (`decimal(38,10)`).
    */
  def lookupFinalRate(
      requestsDf: DataFrame,
      fxratesDf: DataFrame,
      requestDateCol: String = "rate_date",
      fcurrCol: String = "fcurr",
      tcurrCol: String = "tcurr",
      kurstCol: String = "kurst",
      outputCol: String = "final_rate"
  ): DataFrame = {

    val originalCols = requestsDf.columns.toSeq
    val rowKey = "__fxlookup_row_id"

    val requests = requestsDf
      .withColumn(rowKey, monotonically_increasing_id())
      .alias("req")

    val rates = fxratesDf
      .select(
        col("rate_date").as("__fx_rate_date"),
        col("fcurr").as("__fx_fcurr"),
        col("tcurr").as("__fx_tcurr"),
        col("kurst").as("__fx_kurst"),
        col("final_rate").cast(rateDecimalType).as("__fx_final_rate")
      )

    val joined = requests
      .join(
        rates,
        col(s"req.$fcurrCol") === col("__fx_fcurr") &&
          col(s"req.$tcurrCol") === col("__fx_tcurr") &&
          col(s"req.$kurstCol") === col("__fx_kurst") &&
          col("__fx_rate_date") <= col(s"req.$requestDateCol"),
        "left"
      )

    val window = Window
      .partitionBy(col(rowKey))
      .orderBy(col("__fx_rate_date").desc_nulls_last)

    val ranked = joined
      .withColumn("__fx_rownum", row_number().over(window))
      .where(col("__fx_rownum") === 1)

    val withRate = ranked.withColumn(
      outputCol,
      when(
        col(s"req.$fcurrCol") === col(s"req.$tcurrCol"),
        oneRate
      ).otherwise(col("__fx_final_rate"))
    )

    val projection = originalCols.map(c => col(s"req.$c")) :+ col(outputCol)
    withRate.select(projection: _*)
  }

  /** Scalar as-of lookup of `final_rate`. Triggers a Spark action; intended for ad-hoc / driver-side use, not row-by-row inside a Spark job.
    *
    * @return
    *   `Some(rate)` when an as-of row exists or `fcurr == tcurr`, otherwise `None`.
    */
  def lookupFinalRate(
      fxratesDf: DataFrame,
      date: java.sql.Date,
      fcurr: String,
      tcurr: String,
      kurst: String
  ): Option[java.math.BigDecimal] = {
    val spark: SparkSession = fxratesDf.sparkSession
    import spark.implicits._

    val requestsDf = Seq((date, fcurr, tcurr, kurst))
      .toDF("rate_date", "fcurr", "tcurr", "kurst")

    val row = lookupFinalRate(requestsDf, fxratesDf)
      .select(col("final_rate"))
      .head()

    if (row.isNullAt(0)) None
    else Option(row.getDecimal(0))
  }
}
