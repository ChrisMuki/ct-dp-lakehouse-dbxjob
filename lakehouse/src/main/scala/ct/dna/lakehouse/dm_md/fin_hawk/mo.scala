package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.dm_md.fin_hawk.{marc => dm_marc}
import ct.dna.lakehouse.dm_md.fin_hawk.{mdm => dm_mdm}
import ct.dna.lakehouse.dm_md.fin_hawk.{t023t => dm_t023t}
import org.apache.spark.sql.functions._

case class DmMo(
    _mk_system: String,
    _mk_instance: String,
    @PK key_column: String,
    matnr: String,
    mtart: String,
    matkl: String,
    lvorm: String,
    wgbez: String,
    has_hscode: Boolean,
    number_of_distinct_hscodes: Long
) extends Entity

object mo extends TableSpec[DmMo] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(dm_mdm, dm_marc, dm_t023t)

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val maraDf = changeFeeds(dm_mdm).toDF().alias("mara")
    val marcDf = changeFeeds(dm_marc).toDF().alias("marc")
    val t023tDf = changeFeeds(dm_t023t).toDF().alias("t023t")

    val joined = maraDf
      .join(
        marcDf,
        col("mara._mk_system") === col("marc._mk_system") &&
          col("mara._mk_instance") === col("marc._mk_instance") &&
          col("mara.matnr") === col("marc.matnr"),
        "left"
      )
      .join(
        t023tDf,
        col("mara._mk_system") === col("t023t._mk_system") &&
          col("mara._mk_instance") === col("t023t._mk_instance") &&
          col("mara.matkl") === col("t023t.matkl"),
        "left"
      )

    val aggregated = joined
      .groupBy(
        col("mara._mk_system"),
        col("mara._mk_instance"),
        col("mara.matnr"),
        col("mara.mtart"),
        col("mara.matkl"),
        col("mara.lvorm"),
        col("t023t.wgbez")
      )
      .agg(
        sum(when(col("marc.stawn") =!= "", 1).otherwise(0)).as("hscode_count"),
        countDistinct(when(col("marc.stawn") =!= "", col("marc.stawn"))).as("distinct_hscode_count")
      )
      .withColumn("has_hscode", col("hscode_count") > 0)
      .withColumn(
        "number_of_distinct_hscodes",
        when(col("hscode_count") === 0, lit(0L)).otherwise(col("distinct_hscode_count"))
      )
      .withColumn(
        "key_column",
        concat(col("mara._mk_system"), col("mara._mk_instance"), lit("_"), col("mara.matnr"))
      )

    val result = aggregated.select(
      col("mara._mk_system").as("_mk_system"),
      col("mara._mk_instance").as("_mk_instance"),
      col("key_column"),
      col("mara.matnr").as("matnr"),
      col("mara.mtart").as("mtart"),
      col("mara.matkl").as("matkl"),
      col("mara.lvorm").as("lvorm"),
      col("t023t.wgbez").as("wgbez"),
      col("has_hscode"),
      col("number_of_distinct_hscodes")
    )

    table
      .merge(result, lit(false))
      .whenNotMatched()
      .insertAll()
      .whenNotMatchedBySource()
      .delete()
      .execute()

    Result.Merged
  }
}
