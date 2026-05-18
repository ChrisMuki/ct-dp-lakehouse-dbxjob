package ct.dna.lakehouse.dw_tx.fin_fxrates

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.Decimal
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.sr.ct_gbl_ghp.{tcurr => src_tcurr, tcurf => src_tcurf}

import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

case class fx_conversion_rates(
    @PK rate_date: java.sql.Date,
    @PK fcurr: String,
    @PK tcurr: String,
    @PK kurst: String,
    @Decimal(38, 10) final_rate: java.math.BigDecimal
) extends Entity

object fxrates extends TableSpec[fx_conversion_rates] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(src_tcurr, src_tcurf)

  private val validKurst = Seq("MGR", "ZAYD", "ZMEN", "P")
  private val rateDecimalType = "decimal(38,10)"
  private val targetCurrency = "EUR"
  private val ghpMinimumRateDate = 20001231
  private val oneRate = lit(1).cast(rateDecimalType)

  // Archive is now a Unity Catalog table, but not part of changeFeeds because it has no @PK.
  private val archiveTableName = "sr_raw.mn_gbl_spcentral.fxarchive"

  private def pick(df: DataFrame, names: Seq[String], asName: String): Column = {
    val resolved = names.find(df.columns.contains).getOrElse {
      throw new IllegalArgumentException(
        s"Missing required column for '$asName'. Tried: ${names.mkString(", ")}"
      )
    }

    col(resolved).as(asName)
  }

  private def prepareTcurr(df: DataFrame): DataFrame =
    df.select(
      pick(df, Seq("mandt_string", "mandt"), "mandt"),
      pick(df, Seq("kurst_string", "kurst"), "kurst"),
      pick(df, Seq("fcurr_string", "fcurr"), "fcurr"),
      pick(df, Seq("tcurr_string", "tcurr"), "tcurr"),
      pick(df, Seq("ukurs_decimal_9_5", "ukurs_decimal", "ukurs"), "ukurs"),
      pick(df, Seq("gdatu_string", "gdatu"), "gdatu")
    )

  private def prepareTcurf(df: DataFrame): DataFrame =
    df.select(
      pick(df, Seq("mandt_string", "mandt"), "mandt"),
      pick(df, Seq("kurst_string", "kurst"), "kurst"),
      pick(df, Seq("fcurr_string", "fcurr"), "fcurr"),
      pick(df, Seq("tcurr_string", "tcurr"), "tcurr"),
      pick(df, Seq("ffact_decimal_9_0", "ffact_decimal", "ffact"), "ffact"),
      pick(df, Seq("tfact_decimal_9_0", "tfact_decimal", "tfact"), "tfact"),
      pick(df, Seq("gdatu_string", "gdatu"), "gdatu")
    )

  // Convert archive UC table into the same normalized TCURR shape.
  // TCURF factor logic is applied later on the combined TCURR data.
  private def prepareArchiveAsTcurr(archiveRaw: DataFrame): DataFrame =
    archiveRaw
      .select(
        lit("100").as("mandt"), // Archive table does not contain MANDT; source system uses client 100.
        col("kurst_string").as("kurst"),
        col("fcurr_string").as("fcurr"),
        col("tcurr_string").as("tcurr"),
        coalesce(
          col("ukurs_double").cast(rateDecimalType),
          col("ukurs_long").cast(rateDecimalType)
        ).as("ukurs"),
        col("gdatu_long").cast("string").as("gdatu")
      )
      .where(
        col("gdatu").isNotNull &&
          col("fcurr").isNotNull &&
          col("tcurr").isNotNull &&
          col("kurst").isin(validKurst: _*) &&
          col("ukurs").isNotNull
      )

  private def transformPreparedTcurr(
      tcurrPrepared: DataFrame,
      tcurfRaw: DataFrame
  ): DataFrame = {
    val tcurr = tcurrPrepared.alias("tcurr")
    val tcurf = prepareTcurf(tcurfRaw).alias("tcurf")

    val base = tcurr
      .join(
        tcurf,
        col("tcurr.mandt") === col("tcurf.mandt") &&
          col("tcurr.kurst") === col("tcurf.kurst") &&
          col("tcurr.fcurr") === col("tcurf.fcurr") &&
          col("tcurr.tcurr") === col("tcurf.tcurr") &&
          col("tcurf.gdatu").cast("int") >= col("tcurr.gdatu").cast("int"),
        "left"
      )
      .where(
        col("tcurr.ukurs").cast(rateDecimalType) =!= lit(0) &&
          col("tcurr.kurst").isin(validKurst: _*) &&
          col("tcurr.tcurr") === lit(targetCurrency)
      )
      .select(
        col("tcurr.mandt").as("mandt"),
        col("tcurr.kurst").as("kurst"),
        col("tcurr.fcurr").as("fcurr"),
        col("tcurr.tcurr").as("tcurr"),
        col("tcurr.ukurs").cast(rateDecimalType).as("ukurs"),
        coalesce(col("tcurf.ffact").cast(rateDecimalType), oneRate).as("ffact"),
        coalesce(col("tcurf.tfact").cast(rateDecimalType), oneRate).as("tfact"),
        (lit(99999999) - col("tcurr.gdatu").cast("int")).as("gdate"),
        row_number()
          .over(
            Window
              .partitionBy(
                col("tcurr.mandt"),
                col("tcurr.kurst"),
                col("tcurr.fcurr"),
                col("tcurr.tcurr"),
                col("tcurr.gdatu")
              )
              .orderBy(col("tcurf.gdatu").cast("int").asc_nulls_last)
          )
          .as("rownum")
      )

    val dedup = base.where(col("rownum") === 1)

    val eurRates = dedup
      .select(
        to_date(lpad(col("gdate").cast("string"), 8, "0"), "yyyyMMdd").as("rate_date"),
        col("fcurr"),
        col("tcurr"),
        col("kurst"),
        (
          try_divide(col("tfact"), col("ffact")) *
            when(
              col("ukurs") < 0,
              try_divide(oneRate, abs(col("ukurs")))
            ).otherwise(col("ukurs"))
        ).cast(rateDecimalType).as("final_rate")
      )
      .where(col("final_rate").isNotNull)

    val eurInverse = eurRates
      .select(
        col("rate_date"),
        lit(targetCurrency).as("fcurr"),
        col("fcurr").as("tcurr"),
        col("kurst"),
        try_divide(oneRate, col("final_rate"))
          .cast(rateDecimalType)
          .as("final_rate")
      )
      .where(col("final_rate").isNotNull)

    val sourceRates = eurRates.alias("source")
    val targetRates = eurRates.alias("target")

    val crossRates = sourceRates
      .join(
        targetRates,
        col("source.rate_date") === col("target.rate_date") &&
          col("source.kurst") === col("target.kurst") &&
          col("source.fcurr") =!= col("target.fcurr"),
        "inner"
      )
      .select(
        col("source.rate_date").as("rate_date"),
        col("source.fcurr").as("fcurr"),
        col("target.fcurr").as("tcurr"),
        col("source.kurst").as("kurst"),
        try_divide(col("source.final_rate"), col("target.final_rate"))
          .cast(rateDecimalType)
          .as("final_rate")
      )
      .where(col("final_rate").isNotNull)

    val identityRates = eurRates
      .select(
        col("rate_date"),
        col("fcurr"),
        col("fcurr").as("tcurr"),
        col("kurst"),
        oneRate.as("final_rate")
      )
      .distinct()

    val targetIdentityRates = eurRates
      .select(
        col("rate_date"),
        lit(targetCurrency).as("fcurr"),
        lit(targetCurrency).as("tcurr"),
        col("kurst"),
        oneRate.as("final_rate")
      )
      .distinct()

    eurRates
      .unionByName(crossRates)
      .unionByName(identityRates)
      .unionByName(targetIdentityRates)
      .unionByName(eurInverse)
      .select("rate_date", "fcurr", "tcurr", "kurst", "final_rate")
      .dropDuplicates("rate_date", "fcurr", "tcurr", "kurst")
  }

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    if (feeds.forall { case (_, feed) => feed.isUnchanged }) return Result.NoChanges

    val tcurrDf = changeFeeds(src_tcurr).toDF()
    val tcurfDf = changeFeeds(src_tcurf).toDF()

    val ghpTcurr = prepareTcurr(tcurrDf)
      .where(
        col("gdatu").isNotNull &&
          (lit(99999999) - col("gdatu").cast("int")) > lit(ghpMinimumRateDate)
      )

    val archiveTcurr = prepareArchiveAsTcurr(
      SparkSession.active.table(archiveTableName)
    )

    val combinedTcurr = ghpTcurr.unionByName(archiveTcurr)

    val result = transformPreparedTcurr(combinedTcurr, tcurfDf)

    val target = C_fxrates.withDFAlias("target")

    val source = new {
      val rate_date = ColumnWithName("rate_date").withDFAlias("source")
      val fcurr = ColumnWithName("fcurr").withDFAlias("source")
      val tcurr = ColumnWithName("tcurr").withDFAlias("source")
      val kurst = ColumnWithName("kurst").withDFAlias("source")
      val final_rate = ColumnWithName("final_rate").withDFAlias("source")
    }

    table
      .merge(
        result,
        source.rate_date === target.rate_date &&
          source.fcurr === target.fcurr &&
          source.tcurr === target.tcurr &&
          source.kurst === target.kurst
      )
      .whenMatched()
      .update(
        C_fxrates.final_rate -> source.final_rate
      )
      .whenNotMatched()
      .insert(
        C_fxrates.rate_date -> source.rate_date,
        C_fxrates.fcurr -> source.fcurr,
        C_fxrates.tcurr -> source.tcurr,
        C_fxrates.kurst -> source.kurst,
        C_fxrates.final_rate -> source.final_rate
      )
      .whenNotMatchedBySource()
      .delete()
      .execute()

    Result.Merged
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_fxrates(prefix: String) extends ColumnWithNameAccessor {
  val rate_date: ColumnWithName = ColumnWithName(prefix, "rate_date")
  val fcurr: ColumnWithName = ColumnWithName(prefix, "fcurr")
  val tcurr: ColumnWithName = ColumnWithName(prefix, "tcurr")
  val kurst: ColumnWithName = ColumnWithName(prefix, "kurst")
  val final_rate: ColumnWithName = ColumnWithName(prefix, "final_rate")
}

object C_fxrates extends C_fxrates("") {
  def withDFAlias(alias: String): C_fxrates = new C_fxrates(alias)
  def withoutDFAlias: C_fxrates = this
}
// COLUMN ACCESSOR AUTO GENERATED:END
