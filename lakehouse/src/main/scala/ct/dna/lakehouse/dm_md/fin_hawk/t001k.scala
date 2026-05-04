package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.sr.ct_gbl_e32.{t001k => t001k_e32}
import ct.dna.lakehouse.sr.ct_gbl_epp.{t001k => t001k_epp}
import ct.dna.lakehouse.sr.ct_gbl_ghp.{t001k => t001k_ghp}
import ct.dna.lakehouse.sr.ct_gbl_p12.{t001k => t001k_p12}
import ct.dna.lakehouse.sr.ct_gbl_p24.{t001k => t001k_p24}
import ct.dna.lakehouse.sr.ct_gbl_p43.{t001k => t001k_p43}
import ct.dna.lakehouse.sr.ct_gbl_p61.{t001k => t001k_p61}
import ct.dna.lakehouse.sr.ct_gbl_p64.{t001k => t001k_p64}
import ct.dna.lakehouse.sr.ct_gbl_p73.{t001k => t001k_p73}
import ct.dna.lakehouse.sr.ct_gbl_p77.{t001k => t001k_p77}
import ct.dna.lakehouse.sr.ct_gbl_p85.{t001k => t001k_p85}
import ct.dna.lakehouse.sr.ct_gbl_pbr.{t001k => t001k_pbr}
import ct.dna.lakehouse.sr.ct_gbl_psp.{t001k => t001k_psp}
import org.apache.spark.sql.functions._

case class DmT001K(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK bwkey: String,
    bukrs: String
) extends Entity

object t001k extends TableSpec[DmT001K] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      t001k_e32,
      t001k_epp,
      t001k_ghp,
      t001k_p12,
      t001k_p24,
      t001k_p43,
      t001k_p61,
      t001k_p64,
      t001k_p73,
      t001k_p77,
      t001k_p85,
      t001k_pbr,
      t001k_psp
    )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val sources = Seq(
      t001k_e32,
      t001k_epp,
      t001k_ghp,
      t001k_p12,
      t001k_p24,
      t001k_p43,
      t001k_p61,
      t001k_p64,
      t001k_p73,
      t001k_p77,
      t001k_p85,
      t001k_pbr,
      t001k_psp
    )

    val result = sources
      .map(tableSpec =>
        changeFeeds(tableSpec)
          .toDF()
          .select("_mk_system", "_mk_instance", "bwkey", "bukrs")
      )
      .reduce(_.unionByName(_))
      .filter(col("bwkey").isNotNull)
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
