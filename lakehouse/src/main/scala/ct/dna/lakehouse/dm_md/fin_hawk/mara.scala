package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.Decimal
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.sr.ct_gbl_e32.{mara => mara_e32}
import ct.dna.lakehouse.sr.ct_gbl_epp.{mara => mara_epp}
import ct.dna.lakehouse.sr.ct_gbl_ghp.{mara => mara_ghp}
import ct.dna.lakehouse.sr.ct_gbl_p12.{mara => mara_p12}
import ct.dna.lakehouse.sr.ct_gbl_p24.{mara => mara_p24}
import ct.dna.lakehouse.sr.ct_gbl_p43.{mara => mara_p43}
import ct.dna.lakehouse.sr.ct_gbl_p61.{mara => mara_p61}
import ct.dna.lakehouse.sr.ct_gbl_p64.{mara => mara_p64}
import ct.dna.lakehouse.sr.ct_gbl_p73.{mara => mara_p73}
import ct.dna.lakehouse.sr.ct_gbl_p77.{mara => mara_p77}
import ct.dna.lakehouse.sr.ct_gbl_p85.{mara => mara_p85}
import ct.dna.lakehouse.sr.ct_gbl_pbr.{mara => mara_pbr}
import ct.dna.lakehouse.sr.ct_gbl_psp.{mara => mara_psp}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

case class DmMara(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK matnr: String,
    mtart: String,
    matkl: String,
    ersda: String,
    pstat: String,
    vpsta: String,
    lvorm: String,
    meins: String,
    ferth: String,
    formt: String,
    groes: String,
    wrkst: String,
    normt: String,
    @Decimal(13, 3) brgew: java.math.BigDecimal,
    @Decimal(13, 3) ntgew: java.math.BigDecimal,
    gewei: String,
    volum: java.lang.Double,
    voleh: String,
    laeng: java.lang.Double,
    breit: java.lang.Double,
    hoehe: java.lang.Double,
    meabm: String,
    prdha: String,
    attyp: String,
    mfrpn: String,
    mfrnr: String
) extends Entity

object mara extends TableSpec[DmMara] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      mara_e32,
      mara_epp,
      mara_ghp,
      mara_p12,
      mara_p24,
      mara_p43,
      mara_p61,
      mara_p64,
      mara_p73,
      mara_p77,
      mara_p85,
      mara_pbr,
      mara_psp
    )

  private def transform(df: DataFrame): DataFrame =
    df
      .filter(col("matnr").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("matnr").as("matnr"),
        col("mtart").as("mtart"),
        col("matkl").as("matkl"),
        col("ersda").as("ersda"),
        col("pstat").as("pstat"),
        col("vpsta").as("vpsta"),
        col("lvorm").as("lvorm"),
        col("meins").as("meins"),
        col("ferth").as("ferth"),
        col("formt").as("formt"),
        col("groes").as("groes"),
        col("wrkst").as("wrkst"),
        col("normt").as("normt"),
        col("brgew").as("brgew"),
        col("ntgew").as("ntgew"),
        col("gewei").as("gewei"),
        col("volum").as("volum"),
        col("voleh").as("voleh"),
        col("laeng").as("laeng"),
        col("breit").as("breit"),
        col("hoehe").as("hoehe"),
        col("meabm").as("meabm"),
        col("prdha").as("prdha"),
        col("attyp").as("attyp"),
        col("mfrpn").as("mfrpn"),
        col("mfrnr").as("mfrnr")
      )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val sources = Seq(
      mara_e32,
      mara_epp,
      mara_ghp,
      mara_p12,
      mara_p24,
      mara_p43,
      mara_p61,
      mara_p64,
      mara_p73,
      mara_p77,
      mara_p85,
      mara_pbr,
      mara_psp
    )

    val result =
      sources
        .map(tableSpec => transform(changeFeeds(tableSpec).toDF()))
        .reduce(_.unionByName(_, allowMissingColumns = true))

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
