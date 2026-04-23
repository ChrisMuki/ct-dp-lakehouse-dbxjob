package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.{ChangeFeed, Result, Table}
import ct.dna.lakehouse.core.framework.origin.Updated
import ct.dna.lakehouse.core.model.Entity.{Decimal, PK}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.dm_md.fin_hawk.{mara => dm_mara}
import ct.dna.lakehouse.dm_md.fin_hawk.{makt => dm_makt}
import ct.dna.lakehouse.dm_md.fin_hawk.{t023t => dm_t023t}
import org.apache.spark.sql.functions._

case class DmMdm(
    _mk_system: String,
    _mk_instance: String,
    @PK key_column: String,
    matnr: String,
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
    mfrnr: String,
    spras: String,
    maktx: String,
    spras_t023t: String,
    wgbez: String
) extends Entity

object mdm extends TableSpec[DmMdm] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(dm_mara, dm_makt, dm_t023t)

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val maraDf = changeFeeds(dm_mara).toDF().alias("mara")
    val maktDf = changeFeeds(dm_makt).toDF().alias("makt")
    val t023tDf = changeFeeds(dm_t023t).toDF().alias("t023t")

    val joined = maraDf
      .join(
        maktDf,
        col("mara._mk_instance") === col("makt._mk_instance") &&
          col("mara._mk_system") === col("makt._mk_system") &&
          col("mara.matnr") === col("makt.matnr"),
        "left"
      )
      .join(
        t023tDf,
        col("mara._mk_instance") === col("t023t._mk_instance") &&
          col("mara._mk_system") === col("t023t._mk_system") &&
          col("mara.matkl") === col("t023t.matkl"),
        "left"
      )
      .select(
        col("mara._mk_system").as("_mk_system"),
        col("mara._mk_instance").as("_mk_instance"),
        concat(col("mara._mk_system"), col("mara._mk_instance"), lit("_"), col("mara.matnr")).as("key_column"),
        col("mara.matnr").as("matnr"),
        col("mara.mtart").as("mtart"),
        col("mara.matkl").as("matkl"),
        col("mara.ersda").as("ersda"),
        col("mara.pstat").as("pstat"),
        col("mara.vpsta").as("vpsta"),
        col("mara.lvorm").as("lvorm"),
        col("mara.meins").as("meins"),
        col("mara.ferth").as("ferth"),
        col("mara.formt").as("formt"),
        col("mara.groes").as("groes"),
        col("mara.wrkst").as("wrkst"),
        col("mara.normt").as("normt"),
        col("mara.brgew").as("brgew"),
        col("mara.ntgew").as("ntgew"),
        col("mara.gewei").as("gewei"),
        col("mara.volum").as("volum"),
        col("mara.voleh").as("voleh"),
        col("mara.laeng").as("laeng"),
        col("mara.breit").as("breit"),
        col("mara.hoehe").as("hoehe"),
        col("mara.meabm").as("meabm"),
        col("mara.prdha").as("prdha"),
        col("mara.attyp").as("attyp"),
        col("mara.mfrpn").as("mfrpn"),
        col("mara.mfrnr").as("mfrnr"),
        col("makt.spras").as("spras"),
        col("makt.maktx").as("maktx"),
        col("t023t.spras").as("spras_t023t"),
        col("t023t.wgbez").as("wgbez")
      )

    table
      .merge(joined, lit(false))
      .whenNotMatched()
      .insertAll()
      .whenNotMatchedBySource()
      .delete()
      .execute()

    Result.Merged
  }
}
