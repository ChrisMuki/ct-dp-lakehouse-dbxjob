package ct.dna.lakehouse.dm_md.fin_redb

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.Decimal
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import org.apache.spark.sql.functions._

case class DmImportReportAllFinal(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK gjahr: String,
    @PK belnr: String,
    @PK ebeln: String,
    @PK ebelp: String,
    @PK buzei: String,
    bldat: String,
    ekbe_budat: String,
    bwart: String,
    dmbtr: java.lang.Double = null,
    dmbtr_eur: java.lang.Double = null,
    ekko_aedat: String,
    ekko_bsart: String,
    ekko_bukrs: String,
    ekko_ebeln: String,
    ekorg: String,
    ekko_waers: String,
    bukrs: String,
    ekpo_ebelp: String,
    matkl: String,
    matnr: String,
    meins: String,
    ekpo_menge: java.lang.Double = null,
    netpr: java.lang.Double = null,
    ekpo_peinh: java.lang.Double = null,
    txz01: String,
    ekpo_werks: String,
    ekbe_elikz: String,
    fx_rate: java.lang.Double = null,
    hscode_filled: java.lang.Boolean = null,
    hscode_length: java.lang.Integer = null,
    `import_`: String,
    lfa1_country: String,
    lfa1_iso_code: String,
    lifnr_land: String,
    lfa1_lifnr: String,
    @Decimal(19, 17) lfa1_latitude_geo_center: java.math.BigDecimal,
    @Decimal(21, 18) lfa1_longitude_geo_center: java.math.BigDecimal,
    lfa1_member_of_eu: java.lang.Long = null,
    lifnr_name1: String,
    lifnr_ort01: String,
    lfa1_subregion: String,
    marc_herkl: String,
    peinh: java.lang.Double = null,
    ekbe_menge: java.lang.Double = null,
    stprs_per_unit: java.lang.Double = null,
    t001_waers: String,
    t001_butxt: String,
    t001_ort01: String,
    t001w_country: String,
    t001w_iso_code: String,
    werks_land: String,
    @Decimal(19, 17) t001w_latitude_geo_center: java.math.BigDecimal,
    @Decimal(21, 18) t001w_longitude_geo_center: java.math.BigDecimal,
    t001w_member_of_eu: java.lang.Long = null,
    werks_name1: String,
    t001w_subregion: String,
    ekbe_waers: String,
    wrbtr: java.lang.Double = null,
    inco2: String,
    cnkey: String,
    level: String,
    hs_code_length: String,
    cn_description: String,
    cn_code: String,
    goods_code: String,
    declareable_attribut: String,
    declarable: String,
    lev6: String,
    lev7: String,
    lev8: String,
    lev9: String,
    lev10: String,
    lev11: String,
    lev12: String,
    hs_code_2: String,
    hs_code_3: String,
    hs_code_5: String,
    hs_code_6: String,
    hs_code_8_6: String,
    stawn_international: String,
    stawn: String,
    hscode: String,
    matkl_text: String,
    lfa1_eco_regions: String,
    hs_code_8: String,
    mara_matkl: String,
    mara_mtart: String,
    mara_wrkst: String,
    t001w_eco_regions: String
) extends Entity

object import_report_all_final extends TableSpec[DmImportReportAllFinal] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(import_table)

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    if (feeds.forall { case (_, f) => f.isUnchanged }) return Result.NoChanges

    val src = changeFeeds(import_table).snapshot().as("src")

    val result = src.select(
      col("src._mk_system").as("_mk_system"),
      col("src._mk_instance").as("_mk_instance"),
      col("src.gjahr").as("gjahr"),
      col("src.belnr").as("belnr"),
      col("src.ebeln").as("ebeln"),
      col("src.ebelp").as("ebelp"),
      col("src.buzei").as("buzei"),
      col("src.bldat").as("bldat"),
      col("src.budat").as("ekbe_budat"),
      col("src.bwart").as("bwart"),
      col("src.dmbtr").as("dmbtr"),
      col("src.dmbtr_eur").as("dmbtr_eur"),
      col("src.ekko_aedat").as("ekko_aedat"),
      col("src.ekko_bsart").as("ekko_bsart"),
      col("src.ekko_bukrs").as("ekko_bukrs"),
      col("src.ekko_ebeln").as("ekko_ebeln"),
      col("src.ekko_ekorg").as("ekorg"),
      col("src.ekko_waers").as("ekko_waers"),
      col("src.ekpo_bukrs").as("bukrs"),
      col("src.ekpo_ebelp").as("ekpo_ebelp"),
      col("src.ekpo_matkl").as("matkl"),
      col("src.ekpo_matnr").as("matnr"),
      col("src.ekpo_meins").as("meins"),
      col("src.ekpo_menge").as("ekpo_menge"),
      col("src.ekpo_netpr").as("netpr"),
      col("src.ekpo_peinh").as("ekpo_peinh"),
      col("src.ekpo_txz01").as("txz01"),
      col("src.ekpo_werks").as("ekpo_werks"),
      col("src.elikz").as("ekbe_elikz"),
      col("src.fx_rate"),
      col("src.hscode_filled").as("hscode_filled"),
      col("src.hscode_length").as("hscode_length"),
      col("src.import_").as("import_"),
      col("src.lfa1_country").as("lfa1_country"),
      col("src.lfa1_iso_code").as("lfa1_iso_code"),
      col("src.lfa1_land1").as("lifnr_land"),
      col("src.lfa1_lifnr").as("lfa1_lifnr"),
      col("src.lfa1_latitude_geo_center").as("lfa1_latitude_geo_center"),
      col("src.lfa1_longitude_geo_center").as("lfa1_longitude_geo_center"),
      col("src.lfa1_member_of_eu").as("lfa1_member_of_eu"),
      col("src.lfa1_name1").as("lifnr_name1"),
      col("src.lfa1_ort01").as("lifnr_ort01"),
      col("src.lfa1_subregion").as("lfa1_subregion"),
      col("src.marc_herkl").as("marc_herkl"),
      col("src.mbew_peinh").as("peinh"),
      col("src.menge").as("ekbe_menge"),
      col("src.stprs_per_unit").as("stprs_per_unit"),
      col("src.t001_waers").as("t001_waers"),
      col("src.t001_butxt").as("t001_butxt"),
      col("src.t001_ort01").as("t001_ort01"),
      col("src.t001w_country").as("t001w_country"),
      col("src.t001w_iso_code").as("t001w_iso_code"),
      col("src.t001w_land1").as("werks_land"),
      col("src.t001w_latitude_geo_center").as("t001w_latitude_geo_center"),
      col("src.t001w_longitude_geo_center").as("t001w_longitude_geo_center"),
      col("src.t001w_member_of_eu").as("t001w_member_of_eu"),
      col("src.t001w_name1").as("werks_name1"),
      col("src.t001w_subregion").as("t001w_subregion"),
      col("src.waers").as("ekbe_waers"),
      col("src.wrbtr").as("wrbtr"),
      col("src.ekpo_inco2").as("inco2"),
      col("src.hsc_cnkey").as("cnkey"),
      col("src.hsc_level").as("level"),
      col("src.hsc_hs_code_length").as("hs_code_length"),
      col("src.hsc_cn_description").as("cn_description"),
      col("src.hsc_cn_code").as("cn_code"),
      col("src.hsc_goods_code").as("goods_code"),
      col("src.hsc_declareable_attribut").as("declareable_attribut"),
      col("src.hsc_declarable").as("declarable"),
      col("src.hsc_lev6").as("lev6"),
      col("src.hsc_lev7").as("lev7"),
      col("src.hsc_lev8").as("lev8"),
      col("src.hsc_lev9").as("lev9"),
      col("src.hsc_lev10").as("lev10"),
      col("src.hsc_lev11").as("lev11"),
      col("src.hsc_lev12").as("lev12"),
      when(col("src.hs_code_2").isNull || trim(col("src.hs_code_2")) === "", lit("No Entry")).otherwise(col("src.hs_code_2")).as("hs_code_2"),
      when(col("src.hs_code_3").isNull || trim(col("src.hs_code_3")) === "", lit("No Entry")).otherwise(col("src.hs_code_3")).as("hs_code_3"),
      when(col("src.hs_code_5").isNull || trim(col("src.hs_code_5")) === "", lit("No Entry")).otherwise(col("src.hs_code_5")).as("hs_code_5"),
      when(col("src.hs_code_6").isNull || trim(col("src.hs_code_6")) === "", lit("No Entry")).otherwise(col("src.hs_code_6")).as("hs_code_6"),
      when(col("src.hs_code_8_6").isNull || trim(col("src.hs_code_8_6")) === "", lit("No Entry")).otherwise(col("src.hs_code_8_6")).as("hs_code_8_6"),
      when(col("src.hscode").isNull || trim(col("src.hscode")) === "", lit("No Entry")).otherwise(col("src.hscode")).as("stawn_international"),
      when(col("src.marc_stawn").isNull || trim(col("src.marc_stawn")) === "", lit("No Entry")).otherwise(col("src.marc_stawn")).as("stawn"),
      when(col("src.hscode").isNull || trim(col("src.hscode")) === "", lit("No Entry")).otherwise(col("src.hscode")).as("hscode"),
      when(col("src.matkl_text").isNull || trim(col("src.matkl_text")) === "", lit("No Entry")).otherwise(col("src.matkl_text")).as("matkl_text"),
      when(col("src.lfa1_eco_regions").isNull || trim(col("src.lfa1_eco_regions")) === "", lit("No Entry"))
        .otherwise(col("src.lfa1_eco_regions"))
        .as("lfa1_eco_regions"),
      when(col("src.hs_code_8").isNull || trim(col("src.hs_code_8")) === "", lit("No Entry")).otherwise(col("src.hs_code_8")).as("hs_code_8"),
      when(col("src.mara_matkl").isNull || trim(col("src.mara_matkl")) === "", lit("No Entry")).otherwise(col("src.mara_matkl")).as("mara_matkl"),
      when(col("src.mara_mtart").isNull || trim(col("src.mara_mtart")) === "", lit("No Entry")).otherwise(col("src.mara_mtart")).as("mara_mtart"),
      when(col("src.mara_wrkst").isNull || trim(col("src.mara_wrkst")) === "", lit("No Entry")).otherwise(col("src.mara_wrkst")).as("mara_wrkst"),
      when(col("src.t001w_eco_regions").isNull || trim(col("src.t001w_eco_regions")) === "", lit("No Entry"))
        .otherwise(col("src.t001w_eco_regions"))
        .as("t001w_eco_regions")
    )

    table.overwriteByKeys(result)
  }

  override def validate(): Unit = {
    super.validate()
    require(
      sourceTableSpecs.toSet == Set(import_table),
      s"import_report_all_final sourceTableSpecs unexpected: $sourceTableSpecs"
    )
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_import_report_all_final(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val gjahr: ColumnWithName = ColumnWithName(prefix, "gjahr")
  val belnr: ColumnWithName = ColumnWithName(prefix, "belnr")
  val ebeln: ColumnWithName = ColumnWithName(prefix, "ebeln")
  val ebelp: ColumnWithName = ColumnWithName(prefix, "ebelp")
  val buzei: ColumnWithName = ColumnWithName(prefix, "buzei")
  val bldat: ColumnWithName = ColumnWithName(prefix, "bldat")
  val ekbe_budat: ColumnWithName = ColumnWithName(prefix, "ekbe_budat")
  val bwart: ColumnWithName = ColumnWithName(prefix, "bwart")
  val dmbtr: ColumnWithName = ColumnWithName(prefix, "dmbtr")
  val dmbtr_eur: ColumnWithName = ColumnWithName(prefix, "dmbtr_eur")
  val ekko_aedat: ColumnWithName = ColumnWithName(prefix, "ekko_aedat")
  val ekko_bsart: ColumnWithName = ColumnWithName(prefix, "ekko_bsart")
  val ekko_bukrs: ColumnWithName = ColumnWithName(prefix, "ekko_bukrs")
  val ekko_ebeln: ColumnWithName = ColumnWithName(prefix, "ekko_ebeln")
  val ekorg: ColumnWithName = ColumnWithName(prefix, "ekorg")
  val ekko_waers: ColumnWithName = ColumnWithName(prefix, "ekko_waers")
  val bukrs: ColumnWithName = ColumnWithName(prefix, "bukrs")
  val ekpo_ebelp: ColumnWithName = ColumnWithName(prefix, "ekpo_ebelp")
  val matkl: ColumnWithName = ColumnWithName(prefix, "matkl")
  val matnr: ColumnWithName = ColumnWithName(prefix, "matnr")
  val meins: ColumnWithName = ColumnWithName(prefix, "meins")
  val ekpo_menge: ColumnWithName = ColumnWithName(prefix, "ekpo_menge")
  val netpr: ColumnWithName = ColumnWithName(prefix, "netpr")
  val ekpo_peinh: ColumnWithName = ColumnWithName(prefix, "ekpo_peinh")
  val txz01: ColumnWithName = ColumnWithName(prefix, "txz01")
  val ekpo_werks: ColumnWithName = ColumnWithName(prefix, "ekpo_werks")
  val ekbe_elikz: ColumnWithName = ColumnWithName(prefix, "ekbe_elikz")
  val fx_rate: ColumnWithName = ColumnWithName(prefix, "fx_rate")
  val hscode_filled: ColumnWithName = ColumnWithName(prefix, "hscode_filled")
  val hscode_length: ColumnWithName = ColumnWithName(prefix, "hscode_length")
  val import_ : ColumnWithName = ColumnWithName(prefix, "import_")
  val lfa1_country: ColumnWithName = ColumnWithName(prefix, "lfa1_country")
  val lfa1_iso_code: ColumnWithName = ColumnWithName(prefix, "lfa1_iso_code")
  val lifnr_land: ColumnWithName = ColumnWithName(prefix, "lifnr_land")
  val lfa1_lifnr: ColumnWithName = ColumnWithName(prefix, "lfa1_lifnr")
  val lfa1_latitude_geo_center: ColumnWithName = ColumnWithName(prefix, "lfa1_latitude_geo_center")
  val lfa1_longitude_geo_center: ColumnWithName = ColumnWithName(prefix, "lfa1_longitude_geo_center")
  val lfa1_member_of_eu: ColumnWithName = ColumnWithName(prefix, "lfa1_member_of_eu")
  val lifnr_name1: ColumnWithName = ColumnWithName(prefix, "lifnr_name1")
  val lifnr_ort01: ColumnWithName = ColumnWithName(prefix, "lifnr_ort01")
  val lfa1_subregion: ColumnWithName = ColumnWithName(prefix, "lfa1_subregion")
  val marc_herkl: ColumnWithName = ColumnWithName(prefix, "marc_herkl")
  val peinh: ColumnWithName = ColumnWithName(prefix, "peinh")
  val ekbe_menge: ColumnWithName = ColumnWithName(prefix, "ekbe_menge")
  val stprs_per_unit: ColumnWithName = ColumnWithName(prefix, "stprs_per_unit")
  val t001_waers: ColumnWithName = ColumnWithName(prefix, "t001_waers")
  val t001_butxt: ColumnWithName = ColumnWithName(prefix, "t001_butxt")
  val t001_ort01: ColumnWithName = ColumnWithName(prefix, "t001_ort01")
  val t001w_country: ColumnWithName = ColumnWithName(prefix, "t001w_country")
  val t001w_iso_code: ColumnWithName = ColumnWithName(prefix, "t001w_iso_code")
  val werks_land: ColumnWithName = ColumnWithName(prefix, "werks_land")
  val t001w_latitude_geo_center: ColumnWithName = ColumnWithName(prefix, "t001w_latitude_geo_center")
  val t001w_longitude_geo_center: ColumnWithName = ColumnWithName(prefix, "t001w_longitude_geo_center")
  val t001w_member_of_eu: ColumnWithName = ColumnWithName(prefix, "t001w_member_of_eu")
  val werks_name1: ColumnWithName = ColumnWithName(prefix, "werks_name1")
  val t001w_subregion: ColumnWithName = ColumnWithName(prefix, "t001w_subregion")
  val ekbe_waers: ColumnWithName = ColumnWithName(prefix, "ekbe_waers")
  val wrbtr: ColumnWithName = ColumnWithName(prefix, "wrbtr")
  val inco2: ColumnWithName = ColumnWithName(prefix, "inco2")
  val cnkey: ColumnWithName = ColumnWithName(prefix, "cnkey")
  val level: ColumnWithName = ColumnWithName(prefix, "level")
  val hs_code_length: ColumnWithName = ColumnWithName(prefix, "hs_code_length")
  val cn_description: ColumnWithName = ColumnWithName(prefix, "cn_description")
  val cn_code: ColumnWithName = ColumnWithName(prefix, "cn_code")
  val goods_code: ColumnWithName = ColumnWithName(prefix, "goods_code")
  val declareable_attribut: ColumnWithName = ColumnWithName(prefix, "declareable_attribut")
  val declarable: ColumnWithName = ColumnWithName(prefix, "declarable")
  val lev6: ColumnWithName = ColumnWithName(prefix, "lev6")
  val lev7: ColumnWithName = ColumnWithName(prefix, "lev7")
  val lev8: ColumnWithName = ColumnWithName(prefix, "lev8")
  val lev9: ColumnWithName = ColumnWithName(prefix, "lev9")
  val lev10: ColumnWithName = ColumnWithName(prefix, "lev10")
  val lev11: ColumnWithName = ColumnWithName(prefix, "lev11")
  val lev12: ColumnWithName = ColumnWithName(prefix, "lev12")
  val hs_code_2: ColumnWithName = ColumnWithName(prefix, "hs_code_2")
  val hs_code_3: ColumnWithName = ColumnWithName(prefix, "hs_code_3")
  val hs_code_5: ColumnWithName = ColumnWithName(prefix, "hs_code_5")
  val hs_code_6: ColumnWithName = ColumnWithName(prefix, "hs_code_6")
  val hs_code_8_6: ColumnWithName = ColumnWithName(prefix, "hs_code_8_6")
  val stawn_international: ColumnWithName = ColumnWithName(prefix, "stawn_international")
  val stawn: ColumnWithName = ColumnWithName(prefix, "stawn")
  val hscode: ColumnWithName = ColumnWithName(prefix, "hscode")
  val matkl_text: ColumnWithName = ColumnWithName(prefix, "matkl_text")
  val lfa1_eco_regions: ColumnWithName = ColumnWithName(prefix, "lfa1_eco_regions")
  val hs_code_8: ColumnWithName = ColumnWithName(prefix, "hs_code_8")
  val mara_matkl: ColumnWithName = ColumnWithName(prefix, "mara_matkl")
  val mara_mtart: ColumnWithName = ColumnWithName(prefix, "mara_mtart")
  val mara_wrkst: ColumnWithName = ColumnWithName(prefix, "mara_wrkst")
  val t001w_eco_regions: ColumnWithName = ColumnWithName(prefix, "t001w_eco_regions")
}

object C_import_report_all_final extends C_import_report_all_final("") {
  def withDFAlias(alias: String): C_import_report_all_final = new C_import_report_all_final(alias)
  def withoutDFAlias: C_import_report_all_final = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_import_report_all_final = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
