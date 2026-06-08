package ct.dna.lakehouse.dm_md.fin_redb

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.Decimal
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.dm_md.fin_hawk.{mdp => hawk_mdp}
import ct.dna.lakehouse.dm_md.fin_redb.{t134t => dm_t134t}
import ct.dna.lakehouse.sr_raw.mn_gbl_spcustoms.{hs_codes_regional => sr_raw_hscode}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

case class DmCustomsRegionalReporting(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    // EKKO - Purchasing Header
    @PK ekko_ebeln: String,
    ekko_bukrs: String,
    ekko_loekz: String,
    ekko_statu: String,
    ekko_aedat: String,
    ekko_lifnr: String,
    ekko_bsart: String,
    ekko_waers: String,
    ekko_ekorg: String,
    // EKPO - Purchasing Position
    ekpo_ebelp: String,
    ekpo_loekz: String,
    ekpo_txz01: String,
    ekpo_matnr: String,
    ekpo_ematn: String,
    ekpo_bukrs: String,
    ekpo_werks: String,
    ekpo_matkl: String,
    ekpo_menge: java.lang.Double = null,
    ekpo_meins: String,
    ekpo_netpr: java.lang.Double = null,
    ekpo_peinh: java.lang.Double = null,
    ekpo_netwr: java.lang.Double = null,
    ekpo_knttp: String,
    ekpo_pstyp: String,
    ekpo_inco1: String,
    ekpo_inco2: String,
    // LFA1 - Vendor
    lfa1_lifnr: String,
    lfa1_land1: String,
    lfa1_name1: String,
    lfa1_ort01: String,
    lfa1_country: String,
    lfa1_iso_code: String,
    lfa1_eco_regions: String,
    lfa1_subregion: String,
    @Decimal(19, 17) lfa1_latitude_geo_center: java.math.BigDecimal,
    @Decimal(21, 18) lfa1_longitude_geo_center: java.math.BigDecimal,
    lfa1_member_of_eu: java.lang.Long = null,
    // T001 - Company Info
    t001_waers: String,
    t001_butxt: String,
    t001_ort01: String,
    // MBEW
    mbew_stprs: java.lang.Double = null, // to handle nulls
    mbew_peinh: java.lang.Double = null, // to handle nulls
    // MARC
    marc_stawn: String,
    marc_herkl: String,
    stawn_international: String,
    // Material Data Plant
    hscode: String,
    hscode_filled: java.lang.Boolean = null,
    hscode_length: java.lang.Integer = null,
    hs_code_8: String,
    hs_code_2: String,
    hs_code_3: String,
    hs_code_5: String,
    hs_code_6: String,
    hs_code_8_6: String,
    // HS Code Description
    hsc_cnkey: String,
    hsc_level: String,
    hsc_hs_code_length: String,
    hsc_cn_description: String,
    hsc_section: String,
    hsc_chapter: String,
    hsc_lev5: String,
    hsc_lev6: String,
    hsc_cn_code: String,
    hsc_goods_code: String,
    hsc_declareable_attribut: String,
    hsc_declarable: String,
    hsc_lev7: String,
    hsc_lev8: String,
    hsc_lev9: String,
    hsc_lev10: String,
    hsc_lev11: String,
    hsc_lev12: String,
    // MARA
    mara_mtart: String,
    mara_matkl: String,
    mara_wrkst: String,
    // T023T
    matkl_text: String,
    // T134T - Material Type Description
    t134t_mtbez: String,
    // T001W - Plant Info
    t001w_werks: String,
    t001w_name1: String,
    t001w_bwkey: String,
    t001w_land1: String,
    t001w_kunnr: String,
    t001w_lifnr: String,
    t001w_country: String,
    t001w_iso_code: String,
    t001w_eco_regions: String,
    t001w_subregion: String,
    @Decimal(19, 17) t001w_latitude_geo_center: java.math.BigDecimal,
    @Decimal(21, 18) t001w_longitude_geo_center: java.math.BigDecimal,
    t001w_member_of_eu: java.lang.Long = null
) extends Entity

object customs_regional_reporting extends TableSpec[DmCustomsRegionalReporting] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ekko,
      ekpo,
      lfa1,
      t001,
      marc,
      mara,
      t023t,
      dm_t134t,
      mbew,
      t001w,
      sr_raw_hscode,
      hawk_mdp
    )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    if (feeds.forall { case (_, f) => f.isUnchanged }) return Result.NoChanges

    val ekkoDf = changeFeeds(ekko).snapshot().as("ekko")
    val ekpoDf = changeFeeds(ekpo).snapshot().as("ekpo")
    val lfa1Df = changeFeeds(lfa1).snapshot().as("lfa1")
    // t001, t023t and sr_raw_hscode are small dimension/lookup tables — broadcasting
    // them avoids per-join shuffles and lets the planner fuse them into map-side stages.
    val t001Df = broadcast(changeFeeds(t001).snapshot()).as("t001")
    val marcDf = changeFeeds(marc).snapshot().as("marc")
    val maraDf = changeFeeds(mara).snapshot().as("mara")
    val t023tDf = broadcast(changeFeeds(t023t).snapshot()).as("t023t")
    val t134tDf = broadcast(changeFeeds(dm_t134t).snapshot()).as("t134t")
    val mbewDf = changeFeeds(mbew).snapshot().as("mbew")
    val t001wDf = changeFeeds(t001w).snapshot().as("t001w")

    // EKKO LEFT JOIN EKPO ON (_mk_system, _mk_instance) AND ebeln
    val ekkoEkpo = ekkoDf
      .join(
        ekpoDf,
        col("ekko._mk_system") === col("ekpo._mk_system") &&
          col("ekko._mk_instance") === col("ekpo._mk_instance") &&
          col("ekko.ebeln") === col("ekpo.ebeln"),
        "left"
      )

    // LEFT JOIN LFA1 ON (_mk_system, _mk_instance) AND lifnr
    val withLfa1 = ekkoEkpo
      .join(
        lfa1Df,
        col("ekko._mk_system") === col("lfa1._mk_system") &&
          col("ekko._mk_instance") === col("lfa1._mk_instance") &&
          col("ekko.lifnr") === col("lfa1.lifnr"),
        "left"
      )

    // LEFT JOIN T001 ON (_mk_system, _mk_instance) AND bukrs
    val withT001 = withLfa1
      .join(
        t001Df,
        col("ekko._mk_system") === col("t001._mk_system") &&
          col("ekko._mk_instance") === col("t001._mk_instance") &&
          col("ekko.bukrs") === col("t001.bukrs"),
        "left"
      )

    // LEFT JOIN MARC ON ekpo.(_mk_system, _mk_instance), ekpo.werks, ekpo.matnr
    val withMarc = withT001
      .join(
        marcDf,
        col("ekpo._mk_system") === col("marc._mk_system") &&
          col("ekpo._mk_instance") === col("marc._mk_instance") &&
          col("ekpo.werks") === col("marc.werks") &&
          col("ekpo.matnr") === col("marc.matnr"),
        "left"
      )

    // LEFT JOIN MARA ON marc.(_mk_system, _mk_instance), marc.matnr
    val withMara = withMarc
      .join(
        maraDf,
        col("marc._mk_system") === col("mara._mk_system") &&
          col("marc._mk_instance") === col("mara._mk_instance") &&
          col("marc.matnr") === col("mara.matnr"),
        "left"
      )

    // LEFT JOIN T023T ON mara.(_mk_system, _mk_instance), mara.matkl
    val withT023t = withMara
      .join(
        t023tDf,
        col("mara._mk_system") === col("t023t._mk_system") &&
          col("mara._mk_instance") === col("t023t._mk_instance") &&
          col("mara.matkl") === col("t023t.matkl"),
        "left"
      )

    // LEFT JOIN T134T ON mara.(_mk_system, _mk_instance), mara.mtart
    val withT134t = withT023t
      .join(
        t134tDf,
        col("mara._mk_system") === col("t134t._mk_system") &&
          col("mara._mk_instance") === col("t134t._mk_instance") &&
          col("mara.mtart") === col("t134t.mtart"),
        "left"
      )

    // LEFT JOIN MBEW ON ekpo.(_mk_system, _mk_instance), ekpo.werks = mbew.bwkey, ekpo.matnr, ekpo.bwtar
    val withMbew = withT134t
      .join(
        mbewDf,
        col("ekpo._mk_system") === col("mbew._mk_system") &&
          col("ekpo._mk_instance") === col("mbew._mk_instance") &&
          col("ekpo.werks") === col("mbew.bwkey") &&
          col("ekpo.matnr") === col("mbew.matnr") &&
          col("ekpo.bwtar") === col("mbew.bwtar"),
        "left"
      )

    // LEFT JOIN T001W ON ekpo.(_mk_system, _mk_instance), ekpo.werks
    val withT001w = withMbew
      .join(
        t001wDf,
        col("ekpo._mk_system") === col("t001w._mk_system") &&
          col("ekpo._mk_instance") === col("t001w._mk_instance") &&
          col("ekpo.werks") === col("t001w.werks"),
        "left"
      )

    // Read material_data_plant from the hawk mdp DM table via the change feed framework.
    val materialDataPlant = changeFeeds(hawk_mdp).snapshot().as("mdp")

    // LEFT JOIN material_data_plant ON marc.(_mk_system, _mk_instance, werks, matnr)
    val withMdp = withT001w
      .join(
        materialDataPlant,
        col("marc._mk_system") === col("mdp._mk_system") &&
          col("marc._mk_instance") === col("mdp._mk_instance") &&
          col("marc.werks") === col("mdp.werks") &&
          col("marc.matnr") === col("mdp.matnr"),
        "left"
      )

    // `sr_raw_hscode` is a Loaded reference table — the same `cn_code_string` can
    // appear in multiple rows across different ingests/files. Dedupe to one row per
    // `cn_code_string` (most recent ingest) to prevent
    // `DELTA_MULTIPLE_SOURCE_ROW_MATCHING_TARGET_ROW_IN_MERGE`.
    val hscodeDf = broadcast(
      changeFeeds(sr_raw_hscode)
        .snapshot()
        .withColumn(
          "_rn",
          row_number().over(
            Window
              .partitionBy(col("cn_code_string"))
              .orderBy(col("_mk_created_at").desc_nulls_last, col("_lh_id_in_message").desc_nulls_last)
          )
        )
        .filter(col("_rn") === 1)
        .drop("_rn")
    ).as("hsc")

    // LEFT JOIN hscode ON material_data_plant.hscode = hsc.cn_code
    val withHsc = withMdp
      .join(
        hscodeDf,
        col("mdp.hscode") === col("hsc.cn_code_string"),
        "left"
      )

    // Derived HS Code columns
    val hscodeClean = regexp_replace(
      regexp_replace(
        regexp_replace(
          regexp_replace(col("mdp.hscode"), "\\t", ""),
          "\\n",
          ""
        ),
        "\\r",
        ""
      ),
      " ",
      ""
    )

    val withDerived = withHsc
      .withColumn("hs_code_8", substring(col("mdp.hscode"), 1, 8))
      .withColumn("hs_code_2", substring(hscodeClean, 1, 2))
      .withColumn("hs_code_3", substring(hscodeClean, 1, 3))
      .withColumn("hs_code_5", substring(hscodeClean, 1, 5))
      .withColumn("hs_code_6", substring(hscodeClean, 1, 6))
      .withColumn(
        "hs_code_8_6",
        when(length(hscodeClean) < 6, hscodeClean).otherwise(substring(hscodeClean, 1, 6))
      )

    val result = withDerived.select(
      col("ekko._mk_system").as("_mk_system"),
      col("ekko._mk_instance").as("_mk_instance"),
      // EKKO
      col("ekko.ebeln").as("ekko_ebeln"),
      col("ekko.bukrs").as("ekko_bukrs"),
      col("ekko.loekz").as("ekko_loekz"),
      col("ekko.statu").as("ekko_statu"),
      col("ekko.aedat").as("ekko_aedat"),
      col("ekko.lifnr").as("ekko_lifnr"),
      col("ekko.bsart").as("ekko_bsart"),
      col("ekko.waers").as("ekko_waers"),
      col("ekko.ekorg").as("ekko_ekorg"),
      // EKPO
      col("ekpo.ebelp").as("ekpo_ebelp"),
      col("ekpo.loekz").as("ekpo_loekz"),
      col("ekpo.txz01").as("ekpo_txz01"),
      col("ekpo.matnr").as("ekpo_matnr"),
      col("ekpo.ematn").as("ekpo_ematn"),
      col("ekpo.bukrs").as("ekpo_bukrs"),
      col("ekpo.werks").as("ekpo_werks"),
      col("ekpo.matkl").as("ekpo_matkl"),
      col("ekpo.menge").as("ekpo_menge"),
      col("ekpo.meins").as("ekpo_meins"),
      col("ekpo.netpr").as("ekpo_netpr"),
      col("ekpo.peinh").as("ekpo_peinh"),
      col("ekpo.netwr").as("ekpo_netwr"),
      col("ekpo.knttp").as("ekpo_knttp"),
      col("ekpo.pstyp").as("ekpo_pstyp"),
      col("ekpo.inco1").as("ekpo_inco1"),
      col("ekpo.inco2").as("ekpo_inco2"),
      // LFA1
      col("lfa1.lifnr").as("lfa1_lifnr"),
      col("lfa1.land1").as("lfa1_land1"),
      col("lfa1.name1").as("lfa1_name1"),
      col("lfa1.ort01").as("lfa1_ort01"),
      col("lfa1.country").as("lfa1_country"),
      col("lfa1.iso_code").as("lfa1_iso_code"),
      col("lfa1.eco_regions").as("lfa1_eco_regions"),
      col("lfa1.subregion").as("lfa1_subregion"),
      col("lfa1.latitude_geo_center").as("lfa1_latitude_geo_center"),
      col("lfa1.longitude_geo_center").as("lfa1_longitude_geo_center"),
      col("lfa1.member_of_eu").as("lfa1_member_of_eu"),
      // T001
      col("t001.waers").as("t001_waers"),
      col("t001.butxt").as("t001_butxt"),
      col("t001.ort01").as("t001_ort01"),
      // MBEW
      col("mbew.stprs").as("mbew_stprs"),
      col("mbew.peinh").as("mbew_peinh"),
      // MARC
      when(col("marc.stawn").isNull || col("marc.stawn") === "", lit("No Entry")).otherwise(col("marc.stawn")).as("marc_stawn"),
      col("marc.herkl").as("marc_herkl"),
      when(col("mdp.hscode").isNull || col("mdp.hscode") === "", lit("No Entry")).otherwise(col("mdp.hscode")).as("stawn_international"),
      // Material Data Plant
      when(col("mdp.hscode").isNull || col("mdp.hscode") === "", lit("No Entry")).otherwise(col("mdp.hscode")).as("hscode"),
      col("mdp.hscode_filled").as("hscode_filled"),
      col("mdp.hscode_length").as("hscode_length"),
      when(col("hs_code_8").isNull || col("hs_code_8") === "", lit("No Entry")).otherwise(col("hs_code_8")).as("hs_code_8"),
      when(col("hs_code_2").isNull || col("hs_code_2") === "", lit("No Entry")).otherwise(col("hs_code_2")).as("hs_code_2"),
      when(col("hs_code_3").isNull || col("hs_code_3") === "", lit("No Entry")).otherwise(col("hs_code_3")).as("hs_code_3"),
      when(col("hs_code_5").isNull || col("hs_code_5") === "", lit("No Entry")).otherwise(col("hs_code_5")).as("hs_code_5"),
      when(col("hs_code_6").isNull || col("hs_code_6") === "", lit("No Entry")).otherwise(col("hs_code_6")).as("hs_code_6"),
      when(col("hs_code_8_6").isNull || col("hs_code_8_6") === "", lit("No Entry")).otherwise(col("hs_code_8_6")).as("hs_code_8_6"),
      // HS Code Description
      col("hsc.cnkey_string").as("hsc_cnkey"),
      col("hsc.level_string").as("hsc_level"),
      col("hsc.hs_code_length_string").as("hsc_hs_code_length"),
      col("hsc.cn_description_string").as("hsc_cn_description"),
      col("hsc.section_string").as("hsc_section"),
      col("hsc.chapter_string").as("hsc_chapter"),
      col("hsc.lev5_string").as("hsc_lev5"),
      col("hsc.lev6_string").as("hsc_lev6"),
      col("hsc.cn_code_string").as("hsc_cn_code"),
      col("hsc.goods_code_string").as("hsc_goods_code"),
      col("hsc.declareable_attribut_string").as("hsc_declareable_attribut"),
      col("hsc.declarable_string").as("hsc_declarable"),
      col("hsc.lev7_string").as("hsc_lev7"),
      col("hsc.lev8_string").as("hsc_lev8"),
      col("hsc.lev9_string").as("hsc_lev9"),
      col("hsc.lev10_string").as("hsc_lev10"),
      col("hsc.lev11_string").as("hsc_lev11"),
      col("hsc.lev12_string").as("hsc_lev12"),
      // MARA
      col("mara.mtart").as("mara_mtart"),
      col("mara.matkl").as("mara_matkl"),
      col("mara.wrkst").as("mara_wrkst"),
      // T023T
      col("t023t.wgbez").as("matkl_text"),
      // T134T
      col("t134t.mtbez").as("t134t_mtbez"),
      // T001W
      col("t001w.werks").as("t001w_werks"),
      col("t001w.name1").as("t001w_name1"),
      col("t001w.bwkey").as("t001w_bwkey"),
      col("t001w.land1").as("t001w_land1"),
      col("t001w.kunnr").as("t001w_kunnr"),
      col("t001w.lifnr").as("t001w_lifnr"),
      col("t001w.country").as("t001w_country"),
      col("t001w.iso_code").as("t001w_iso_code"),
      col("t001w.eco_regions").as("t001w_eco_regions"),
      col("t001w.subregion").as("t001w_subregion"),
      col("t001w.latitude_geo_center").as("t001w_latitude_geo_center"),
      col("t001w.longitude_geo_center").as("t001w_longitude_geo_center"),
      col("t001w.member_of_eu").as("t001w_member_of_eu")
    )

    table.overwriteByKeys(result)
  }

  override def validate(): Unit = {
    super.validate()
    val expected = Set(ekko, ekpo, lfa1, t001, marc, mara, t023t, dm_t134t, mbew, t001w, sr_raw_hscode, hawk_mdp)
    require(
      sourceTableSpecs.toSet == expected,
      s"customs_regional_reporting sourceTableSpecs unexpected: $sourceTableSpecs"
    )
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_customs_regional_reporting(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val ekko_ebeln: ColumnWithName = ColumnWithName(prefix, "ekko_ebeln")
  val ekko_bukrs: ColumnWithName = ColumnWithName(prefix, "ekko_bukrs")
  val ekko_loekz: ColumnWithName = ColumnWithName(prefix, "ekko_loekz")
  val ekko_statu: ColumnWithName = ColumnWithName(prefix, "ekko_statu")
  val ekko_aedat: ColumnWithName = ColumnWithName(prefix, "ekko_aedat")
  val ekko_lifnr: ColumnWithName = ColumnWithName(prefix, "ekko_lifnr")
  val ekko_bsart: ColumnWithName = ColumnWithName(prefix, "ekko_bsart")
  val ekko_waers: ColumnWithName = ColumnWithName(prefix, "ekko_waers")
  val ekko_ekorg: ColumnWithName = ColumnWithName(prefix, "ekko_ekorg")
  val ekpo_ebelp: ColumnWithName = ColumnWithName(prefix, "ekpo_ebelp")
  val ekpo_loekz: ColumnWithName = ColumnWithName(prefix, "ekpo_loekz")
  val ekpo_txz01: ColumnWithName = ColumnWithName(prefix, "ekpo_txz01")
  val ekpo_matnr: ColumnWithName = ColumnWithName(prefix, "ekpo_matnr")
  val ekpo_ematn: ColumnWithName = ColumnWithName(prefix, "ekpo_ematn")
  val ekpo_bukrs: ColumnWithName = ColumnWithName(prefix, "ekpo_bukrs")
  val ekpo_werks: ColumnWithName = ColumnWithName(prefix, "ekpo_werks")
  val ekpo_matkl: ColumnWithName = ColumnWithName(prefix, "ekpo_matkl")
  val ekpo_menge: ColumnWithName = ColumnWithName(prefix, "ekpo_menge")
  val ekpo_meins: ColumnWithName = ColumnWithName(prefix, "ekpo_meins")
  val ekpo_netpr: ColumnWithName = ColumnWithName(prefix, "ekpo_netpr")
  val ekpo_peinh: ColumnWithName = ColumnWithName(prefix, "ekpo_peinh")
  val ekpo_netwr: ColumnWithName = ColumnWithName(prefix, "ekpo_netwr")
  val ekpo_knttp: ColumnWithName = ColumnWithName(prefix, "ekpo_knttp")
  val ekpo_pstyp: ColumnWithName = ColumnWithName(prefix, "ekpo_pstyp")
  val ekpo_inco1: ColumnWithName = ColumnWithName(prefix, "ekpo_inco1")
  val ekpo_inco2: ColumnWithName = ColumnWithName(prefix, "ekpo_inco2")
  val lfa1_lifnr: ColumnWithName = ColumnWithName(prefix, "lfa1_lifnr")
  val lfa1_land1: ColumnWithName = ColumnWithName(prefix, "lfa1_land1")
  val lfa1_name1: ColumnWithName = ColumnWithName(prefix, "lfa1_name1")
  val lfa1_ort01: ColumnWithName = ColumnWithName(prefix, "lfa1_ort01")
  val lfa1_country: ColumnWithName = ColumnWithName(prefix, "lfa1_country")
  val lfa1_iso_code: ColumnWithName = ColumnWithName(prefix, "lfa1_iso_code")
  val lfa1_eco_regions: ColumnWithName = ColumnWithName(prefix, "lfa1_eco_regions")
  val lfa1_subregion: ColumnWithName = ColumnWithName(prefix, "lfa1_subregion")
  val lfa1_latitude_geo_center: ColumnWithName = ColumnWithName(prefix, "lfa1_latitude_geo_center")
  val lfa1_longitude_geo_center: ColumnWithName = ColumnWithName(prefix, "lfa1_longitude_geo_center")
  val lfa1_member_of_eu: ColumnWithName = ColumnWithName(prefix, "lfa1_member_of_eu")
  val t001_waers: ColumnWithName = ColumnWithName(prefix, "t001_waers")
  val t001_butxt: ColumnWithName = ColumnWithName(prefix, "t001_butxt")
  val t001_ort01: ColumnWithName = ColumnWithName(prefix, "t001_ort01")
  val mbew_stprs: ColumnWithName = ColumnWithName(prefix, "mbew_stprs")
  val mbew_peinh: ColumnWithName = ColumnWithName(prefix, "mbew_peinh")
  val marc_stawn: ColumnWithName = ColumnWithName(prefix, "marc_stawn")
  val marc_herkl: ColumnWithName = ColumnWithName(prefix, "marc_herkl")
  val stawn_international: ColumnWithName = ColumnWithName(prefix, "stawn_international")
  val hscode: ColumnWithName = ColumnWithName(prefix, "hscode")
  val hscode_filled: ColumnWithName = ColumnWithName(prefix, "hscode_filled")
  val hscode_length: ColumnWithName = ColumnWithName(prefix, "hscode_length")
  val hs_code_8: ColumnWithName = ColumnWithName(prefix, "hs_code_8")
  val hs_code_2: ColumnWithName = ColumnWithName(prefix, "hs_code_2")
  val hs_code_3: ColumnWithName = ColumnWithName(prefix, "hs_code_3")
  val hs_code_5: ColumnWithName = ColumnWithName(prefix, "hs_code_5")
  val hs_code_6: ColumnWithName = ColumnWithName(prefix, "hs_code_6")
  val hs_code_8_6: ColumnWithName = ColumnWithName(prefix, "hs_code_8_6")
  val hsc_cnkey: ColumnWithName = ColumnWithName(prefix, "hsc_cnkey")
  val hsc_level: ColumnWithName = ColumnWithName(prefix, "hsc_level")
  val hsc_hs_code_length: ColumnWithName = ColumnWithName(prefix, "hsc_hs_code_length")
  val hsc_cn_description: ColumnWithName = ColumnWithName(prefix, "hsc_cn_description")
  val hsc_section: ColumnWithName = ColumnWithName(prefix, "hsc_section")
  val hsc_chapter: ColumnWithName = ColumnWithName(prefix, "hsc_chapter")
  val hsc_lev5: ColumnWithName = ColumnWithName(prefix, "hsc_lev5")
  val hsc_lev6: ColumnWithName = ColumnWithName(prefix, "hsc_lev6")
  val hsc_cn_code: ColumnWithName = ColumnWithName(prefix, "hsc_cn_code")
  val hsc_goods_code: ColumnWithName = ColumnWithName(prefix, "hsc_goods_code")
  val hsc_declareable_attribut: ColumnWithName = ColumnWithName(prefix, "hsc_declareable_attribut")
  val hsc_declarable: ColumnWithName = ColumnWithName(prefix, "hsc_declarable")
  val hsc_lev7: ColumnWithName = ColumnWithName(prefix, "hsc_lev7")
  val hsc_lev8: ColumnWithName = ColumnWithName(prefix, "hsc_lev8")
  val hsc_lev9: ColumnWithName = ColumnWithName(prefix, "hsc_lev9")
  val hsc_lev10: ColumnWithName = ColumnWithName(prefix, "hsc_lev10")
  val hsc_lev11: ColumnWithName = ColumnWithName(prefix, "hsc_lev11")
  val hsc_lev12: ColumnWithName = ColumnWithName(prefix, "hsc_lev12")
  val mara_mtart: ColumnWithName = ColumnWithName(prefix, "mara_mtart")
  val mara_matkl: ColumnWithName = ColumnWithName(prefix, "mara_matkl")
  val mara_wrkst: ColumnWithName = ColumnWithName(prefix, "mara_wrkst")
  val matkl_text: ColumnWithName = ColumnWithName(prefix, "matkl_text")
  val t134t_mtbez: ColumnWithName = ColumnWithName(prefix, "t134t_mtbez")
  val t001w_werks: ColumnWithName = ColumnWithName(prefix, "t001w_werks")
  val t001w_name1: ColumnWithName = ColumnWithName(prefix, "t001w_name1")
  val t001w_bwkey: ColumnWithName = ColumnWithName(prefix, "t001w_bwkey")
  val t001w_land1: ColumnWithName = ColumnWithName(prefix, "t001w_land1")
  val t001w_kunnr: ColumnWithName = ColumnWithName(prefix, "t001w_kunnr")
  val t001w_lifnr: ColumnWithName = ColumnWithName(prefix, "t001w_lifnr")
  val t001w_country: ColumnWithName = ColumnWithName(prefix, "t001w_country")
  val t001w_iso_code: ColumnWithName = ColumnWithName(prefix, "t001w_iso_code")
  val t001w_eco_regions: ColumnWithName = ColumnWithName(prefix, "t001w_eco_regions")
  val t001w_subregion: ColumnWithName = ColumnWithName(prefix, "t001w_subregion")
  val t001w_latitude_geo_center: ColumnWithName = ColumnWithName(prefix, "t001w_latitude_geo_center")
  val t001w_longitude_geo_center: ColumnWithName = ColumnWithName(prefix, "t001w_longitude_geo_center")
  val t001w_member_of_eu: ColumnWithName = ColumnWithName(prefix, "t001w_member_of_eu")
}

object C_customs_regional_reporting extends C_customs_regional_reporting("") {
  def withDFAlias(alias: String): C_customs_regional_reporting = new C_customs_regional_reporting(alias)
  def withoutDFAlias: C_customs_regional_reporting = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_customs_regional_reporting = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
