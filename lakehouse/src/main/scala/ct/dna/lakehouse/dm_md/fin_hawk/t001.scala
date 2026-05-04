package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.sr.ct_gbl_e32.{t001 => t001_e32}
import ct.dna.lakehouse.sr.ct_gbl_epp.{t001 => t001_epp}
import ct.dna.lakehouse.sr.ct_gbl_ghp.{t001 => t001_ghp}
import ct.dna.lakehouse.sr.ct_gbl_p12.{t001 => t001_p12}
import ct.dna.lakehouse.sr.ct_gbl_p24.{t001 => t001_p24}
import ct.dna.lakehouse.sr.ct_gbl_p43.{t001 => t001_p43}
import ct.dna.lakehouse.sr.ct_gbl_p61.{t001 => t001_p61}
import ct.dna.lakehouse.sr.ct_gbl_p64.{t001 => t001_p64}
import ct.dna.lakehouse.sr.ct_gbl_p73.{t001 => t001_p73}
import ct.dna.lakehouse.sr.ct_gbl_p77.{t001 => t001_p77}
import ct.dna.lakehouse.sr.ct_gbl_p85.{t001 => t001_p85}
import ct.dna.lakehouse.sr.ct_gbl_pbr.{t001 => t001_pbr}
import ct.dna.lakehouse.sr.ct_gbl_psp.{t001 => t001_psp}
import org.apache.spark.sql.functions._

case class DmT001(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK bukrs: String,
    butxt: String,
    land1: String,
    ort01: String
) extends Entity

object t001 extends TableSpec[DmT001] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      t001_e32,
      t001_epp,
      t001_ghp,
      t001_p12,
      t001_p24,
      t001_p43,
      t001_p61,
      t001_p64,
      t001_p73,
      t001_p77,
      t001_p85,
      t001_pbr,
      t001_psp
    )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val sources = Seq(
      t001_e32,
      t001_epp,
      t001_ghp,
      t001_p12,
      t001_p24,
      t001_p43,
      t001_p61,
      t001_p64,
      t001_p73,
      t001_p77,
      t001_p85,
      t001_pbr,
      t001_psp
    )

    val result = sources
      .map(tableSpec =>
        changeFeeds(tableSpec)
          .toDF()
          .select("_mk_system", "_mk_instance", "bukrs", "butxt", "land1", "ort01")
      )
      .reduce(_.unionByName(_))
      .filter(col("bukrs").isNotNull)
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
