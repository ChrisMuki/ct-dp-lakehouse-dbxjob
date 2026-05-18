package ct.dna.lakehouse.tools.sap_currency

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object CurrencyUtils {

  /** Prepare TCURX (clean + normalize) */
  def prepareTcurx(
      tcurxDf: DataFrame,
      currencyCol: String = "currkey",
      currdecCol: String = "currdec"
  ): DataFrame = {

    val cleaned = tcurxDf
      .filter(col(currencyCol).isNotNull && col(currdecCol).isNotNull)
      .withColumn(
        "__currency_norm",
        upper(trim(regexp_replace(col(currencyCol), "\\.", "")))
      )
      .filter(length(col("__currency_norm")) === 3)
      .select(
        col("_mk_system").as("__tcurx_system"),
        col("__currency_norm"),
        col(currdecCol).as("__tcurx_currdec")
      )

    val conflicts = cleaned
      .groupBy("__tcurx_system", "__currency_norm")
      .agg(countDistinct("__tcurx_currdec").as("distinct_cnt"))
      .filter(col("distinct_cnt") > 1)

    if (conflicts.take(1).nonEmpty) {
      val sample = conflicts.limit(10).collect().mkString("\n")
      throw new RuntimeException(
        s"""
           |TCURX validation failed: conflicting currdec for same currency.
           |
           |Sample conflicts:
           |$sample
         """.stripMargin
      )
    }

    cleaned.dropDuplicates("__tcurx_system", "__currency_norm")
  }

  /** Apply TCURX correction in-place for amount columns */
  def applyCurrencyCorrectionMulti(
      df: DataFrame,
      tcurx: DataFrame,
      currencyCol: String,
      amountCols: Seq[String]
  ): DataFrame = {

    val normalized = df.withColumn(
      "__currency_norm",
      upper(trim(regexp_replace(col(currencyCol), "\\.", "")))
    )

    val tcurxForJoin = broadcast(
      tcurx.select(
        col("__tcurx_system"),
        col("__currency_norm").as("__tcurx_currency"),
        col("__tcurx_currdec")
      )
    )

    val joined = normalized.join(
      tcurxForJoin,
      col("__currency_norm") === col("__tcurx_currency") &&
        col("_mk_system") === col("__tcurx_system"),
      "left"
    )

    val factor =
      expr("CAST(POWER(10, 2 - COALESCE(__tcurx_currdec, 2)) AS DECIMAL(18,6))")

    val updated = amountCols.foldLeft(joined) { (acc, amountCol) =>
      acc.withColumn(
        amountCol,
        when(
          col(amountCol).isNotNull,
          (col(amountCol) * factor).cast("decimal(18,2)")
        ).otherwise(col(amountCol))
      )
    }

    updated.drop(
      "__tcurx_system",
      "__tcurx_currency",
      "__tcurx_currdec",
      "__currency_norm"
    )
  }
}
