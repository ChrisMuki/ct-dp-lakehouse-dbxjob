package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.{ChangeFeed, Result, Table}
import ct.dna.lakehouse.core.framework.origin.Updated
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.sr.ct_gbl_e32.{t001w => t001w_e32}
import ct.dna.lakehouse.sr.ct_gbl_epp.{t001w => t001w_epp}
import ct.dna.lakehouse.sr.ct_gbl_ghp.{t001w => t001w_ghp}
import ct.dna.lakehouse.sr.ct_gbl_p12.{t001w => t001w_p12}
import ct.dna.lakehouse.sr.ct_gbl_p24.{t001w => t001w_p24}
import ct.dna.lakehouse.sr.ct_gbl_p43.{t001w => t001w_p43}
import ct.dna.lakehouse.sr.ct_gbl_p61.{t001w => t001w_p61}
import ct.dna.lakehouse.sr.ct_gbl_p64.{t001w => t001w_p64}
import ct.dna.lakehouse.sr.ct_gbl_p73.{t001w => t001w_p73}
import ct.dna.lakehouse.sr.ct_gbl_p77.{t001w => t001w_p77}
import ct.dna.lakehouse.sr.ct_gbl_p85.{t001w => t001w_p85}
import ct.dna.lakehouse.sr.ct_gbl_pbr.{t001w => t001w_pbr}
import ct.dna.lakehouse.sr.ct_gbl_psp.{t001w => t001w_psp}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

case class DmT001W(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK werks: String,
    name1: String,
    bwkey: String,
    land1: String,
    kunnr: String,
    lifnr: String
) extends Entity

object t001w extends TableSpec[DmT001W] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      t001w_e32,
      t001w_epp,
      t001w_ghp,
      t001w_p12,
      t001w_p24,
      t001w_p43,
      t001w_p61,
      t001w_p64,
      t001w_p73,
      t001w_p77,
      t001w_p85,
      t001w_pbr,
      t001w_psp
    )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val sources = Seq(
      t001w_e32,
      t001w_epp,
      t001w_ghp,
      t001w_p12,
      t001w_p24,
      t001w_p43,
      t001w_p61,
      t001w_p64,
      t001w_p73,
      t001w_p77,
      t001w_p85,
      t001w_pbr,
      t001w_psp
    )

    val result = sources
      .map(tableSpec =>
        changeFeeds(tableSpec)
          .toDF()
          .select("_mk_system", "_mk_instance", "werks", "name1", "bwkey", "land1", "kunnr", "lifnr")
      )
      .reduce(_.unionByName(_))
      .filter(col("werks").isNotNull)
      .distinct()

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
