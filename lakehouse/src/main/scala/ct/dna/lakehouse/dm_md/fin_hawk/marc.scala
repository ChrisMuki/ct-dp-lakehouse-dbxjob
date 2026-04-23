package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.{ChangeFeed, Result, Table}
import ct.dna.lakehouse.core.framework.origin.Updated
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.sr.ct_gbl_e32.{marc => marc_e32}
import ct.dna.lakehouse.sr.ct_gbl_epp.{marc => marc_epp}
import ct.dna.lakehouse.sr.ct_gbl_ghp.{marc => marc_ghp}
import ct.dna.lakehouse.sr.ct_gbl_p12.{marc => marc_p12}
import ct.dna.lakehouse.sr.ct_gbl_p24.{marc => marc_p24}
import ct.dna.lakehouse.sr.ct_gbl_p43.{marc => marc_p43}
import ct.dna.lakehouse.sr.ct_gbl_p61.{marc => marc_p61}
import ct.dna.lakehouse.sr.ct_gbl_p64.{marc => marc_p64}
import ct.dna.lakehouse.sr.ct_gbl_p73.{marc => marc_p73}
import ct.dna.lakehouse.sr.ct_gbl_p77.{marc => marc_p77}
import ct.dna.lakehouse.sr.ct_gbl_p85.{marc => marc_p85}
import ct.dna.lakehouse.sr.ct_gbl_pbr.{marc => marc_pbr}
import ct.dna.lakehouse.sr.ct_gbl_psp.{marc => marc_psp}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

case class DmMarc(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK matnr: String,
    @PK werks: String,
    lvorm_plant: String,
    stawn: String,
    steuc: String,
    herkl: String,
    stawn_sap: String,
    steuc_sap: String
) extends Entity

object marc extends TableSpec[DmMarc] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      marc_e32,
      marc_epp,
      marc_ghp,
      marc_p12,
      marc_p24,
      marc_p43,
      marc_p61,
      marc_p64,
      marc_p73,
      marc_p77,
      marc_p85,
      marc_pbr,
      marc_psp
    )

  private def transform(df: DataFrame): DataFrame =
    df
      .filter(col("matnr").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("matnr").as("matnr"),
        col("lvorm").as("lvorm_plant"),
        col("werks").as("werks"),
        regexp_replace(col("stawn"), "\\.|\\s", "").as("stawn"),
        regexp_replace(col("steuc"), "\\.|\\s", "").as("steuc"),
        col("herkl").as("herkl"),
        col("stawn").as("stawn_sap"),
        col("steuc").as("steuc_sap")
      )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val sources = Seq(
      marc_e32,
      marc_epp,
      marc_ghp,
      marc_p12,
      marc_p24,
      marc_p43,
      marc_p61,
      marc_p64,
      marc_p73,
      marc_p77,
      marc_p85,
      marc_pbr,
      marc_psp
    )

    val result = sources
      .map(tableSpec => transform(changeFeeds(tableSpec).toDF()))
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
