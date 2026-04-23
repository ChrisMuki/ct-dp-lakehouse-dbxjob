package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.{ChangeFeed, Result, Table}
import ct.dna.lakehouse.core.framework.origin.Updated
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.sr.ct_gbl_e32.{makt => makt_e32}
import ct.dna.lakehouse.sr.ct_gbl_epp.{makt => makt_epp}
import ct.dna.lakehouse.sr.ct_gbl_ghp.{makt => makt_ghp}
import ct.dna.lakehouse.sr.ct_gbl_p12.{makt => makt_p12}
import ct.dna.lakehouse.sr.ct_gbl_p24.{makt => makt_p24}
import ct.dna.lakehouse.sr.ct_gbl_p43.{makt => makt_p43}
import ct.dna.lakehouse.sr.ct_gbl_p61.{makt => makt_p61}
import ct.dna.lakehouse.sr.ct_gbl_p64.{makt => makt_p64}
import ct.dna.lakehouse.sr.ct_gbl_p73.{makt => makt_p73}
import ct.dna.lakehouse.sr.ct_gbl_p77.{makt => makt_p77}
import ct.dna.lakehouse.sr.ct_gbl_p85.{makt => makt_p85}
import ct.dna.lakehouse.sr.ct_gbl_pbr.{makt => makt_pbr}
import ct.dna.lakehouse.sr.ct_gbl_psp.{makt => makt_psp}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

case class DmMakt(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK matnr: String,
    @PK spras: String,
    maktx: String
) extends Entity

object makt extends TableSpec[DmMakt] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      makt_e32,
      makt_epp,
      makt_ghp,
      makt_p12,
      makt_p24,
      makt_p43,
      makt_p61,
      makt_p64,
      makt_p73,
      makt_p77,
      makt_p85,
      makt_pbr,
      makt_psp
    )

  private def transformMakt(df: DataFrame): DataFrame =
    df.filter(col("spras") === "D" || col("spras") === "E")
      .groupBy(col("_mk_system"), col("_mk_instance"), col("matnr"))
      .agg(
        concat_ws(";", max(when(col("spras") === "D", col("spras"))), max(when(col("spras") === "E", col("spras")))).as("spras"),
        concat_ws("~~", max(when(col("spras") === "D", col("maktx"))), max(when(col("spras") === "E", col("maktx")))).as("maktx")
      )
      .select(
        col("_mk_system").as("_mk_system"),
        col("_mk_instance").as("_mk_instance"),
        col("matnr").as("matnr"),
        col("spras"),
        col("maktx")
      )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val sources = Seq(
      makt_e32,
      makt_epp,
      makt_ghp,
      makt_p12,
      makt_p24,
      makt_p43,
      makt_p61,
      makt_p64,
      makt_p73,
      makt_p77,
      makt_p85,
      makt_pbr,
      makt_psp
    )

    val result = sources
      .map(tableSpec => transformMakt(changeFeeds(tableSpec).toDF()))
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
