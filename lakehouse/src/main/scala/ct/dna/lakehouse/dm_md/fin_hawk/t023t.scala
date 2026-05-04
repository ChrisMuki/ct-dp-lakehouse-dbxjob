package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.sr.ct_gbl_e32.{t023t => t023t_e32}
import ct.dna.lakehouse.sr.ct_gbl_epp.{t023t => t023t_epp}
import ct.dna.lakehouse.sr.ct_gbl_ghp.{t023t => t023t_ghp}
import ct.dna.lakehouse.sr.ct_gbl_p12.{t023t => t023t_p12}
import ct.dna.lakehouse.sr.ct_gbl_p24.{t023t => t023t_p24}
import ct.dna.lakehouse.sr.ct_gbl_p43.{t023t => t023t_p43}
import ct.dna.lakehouse.sr.ct_gbl_p61.{t023t => t023t_p61}
import ct.dna.lakehouse.sr.ct_gbl_p64.{t023t => t023t_p64}
import ct.dna.lakehouse.sr.ct_gbl_p73.{t023t => t023t_p73}
import ct.dna.lakehouse.sr.ct_gbl_p77.{t023t => t023t_p77}
import ct.dna.lakehouse.sr.ct_gbl_p85.{t023t => t023t_p85}
import ct.dna.lakehouse.sr.ct_gbl_pbr.{t023t => t023t_pbr}
import ct.dna.lakehouse.sr.ct_gbl_psp.{t023t => t023t_psp}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

case class DmT023T(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK matkl: String,
    @PK spras: String,
    wgbez: String
) extends Entity

object t023t extends TableSpec[DmT023T] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      t023t_e32,
      t023t_epp,
      t023t_ghp,
      t023t_p12,
      t023t_p24,
      t023t_p43,
      t023t_p61,
      t023t_p64,
      t023t_p73,
      t023t_p77,
      t023t_p85,
      t023t_pbr,
      t023t_psp
    )

  private def transformT023T(df: DataFrame): DataFrame =
    df.filter(col("spras") === "D" || col("spras") === "E")
      .groupBy(col("_mk_system"), col("_mk_instance"), col("matkl"))
      .agg(
        concat_ws(
          ";",
          max(when(col("spras") === "D", col("spras"))),
          max(when(col("spras") === "E", col("spras")))
        ).as("spras"),
        concat_ws(
          "~~",
          max(when(col("spras") === "D", col("wgbez"))),
          max(when(col("spras") === "E", col("wgbez")))
        ).as("wgbez")
      )
      .select(
        col("_mk_system").as("_mk_system"),
        col("_mk_instance").as("_mk_instance"),
        col("matkl").as("matkl"),
        col("spras"),
        col("wgbez")
      )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val sources = Seq(
      t023t_e32,
      t023t_epp,
      t023t_ghp,
      t023t_p12,
      t023t_p24,
      t023t_p43,
      t023t_p61,
      t023t_p64,
      t023t_p73,
      t023t_p77,
      t023t_p85,
      t023t_pbr,
      t023t_psp
    )

    val result = sources
      .map(tableSpec => transformT023T(changeFeeds(tableSpec).toDF()))
      .reduce(_.unionByName(_))

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
