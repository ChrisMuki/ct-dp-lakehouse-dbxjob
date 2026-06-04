package ct.dna.lakehouse.dm_md.fin_regional_dashboard

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.Decimal
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import org.apache.spark.sql.SparkSession
// import ct.dna.lakehouse.sr_raw.dw_tx_finfxrates.{fxrates => sr_raw_fxrates}
import org.apache.spark.sql.functions._

case class DmImportTable(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    // EKBE fields
    @PK gjahr: String,
    @PK vgabe: String,
    @PK zekkn: String,
    @PK belnr: String,
    @PK ebeln: String,
    @PK ebelp: String,
    @PK buzei: String,
    bwart: String,
    budat: String,
    menge: java.lang.Double = null,
    dmbtr: java.lang.Double = null,
    wrbtr: java.lang.Double = null,
    waers: String,
    shkzg: String,
    elikz: String,
    xblnr: String,
    reewr: java.lang.Double = null,
    lsmng: java.lang.Double = null,
    lsmeh: String,
    areww: java.lang.Double = null,
    hswae: String,
    bldat: String,
    // CRR fields
    ekko_ebeln: String,
    ekko_bukrs: String,
    ekko_loekz: String,
    ekko_statu: String,
    ekko_aedat: String,
    ekko_lifnr: String,
    ekko_bsart: String,
    ekko_waers: String,
    ekko_ekorg: String,
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
    t001_waers: String,
    t001_butxt: String,
    t001_ort01: String,
    mbew_stprs: java.lang.Double = null,
    mbew_peinh: java.lang.Double = null,
    marc_stawn: String,
    marc_herkl: String,
    stawn_international: String,
    hscode: String,
    hscode_filled: java.lang.Boolean = null,
    hscode_length: java.lang.Integer = null,
    hs_code_8: String,
    hs_code_2: String,
    hs_code_3: String,
    hs_code_5: String,
    hs_code_6: String,
    hs_code_8_6: String,
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
    mara_mtart: String,
    mara_matkl: String,
    mara_wrkst: String,
    matkl_text: String,
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
    t001w_member_of_eu: java.lang.Long = null,
    // Derived fields
    fx_rate: java.lang.Double = null,
    `import_`: String,
    stprs_per_unit: java.lang.Double = null,
    dmbtr_eur: java.lang.Double = null
) extends Entity

object import_table extends TableSpec[DmImportTable] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(ekbe, customs_regional_reporting)

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    if (feeds.forall { case (_, f) => f.isUnchanged }) return Result.NoChanges

    val spark = SparkSession.active

    // --- Step 1: EKBE filtered (vgabe = '1', gjahr >= 2023) ---
    // join_month: if budat falls in the current month, shift back one month so we always
    // join against a fully-settled FX rate month (current month rates may be incomplete).
    val ekbeDf = changeFeeds(ekbe)
      .snapshot()
      .filter(col("vgabe") === "1" && col("gjahr") >= "2023")
      .withColumn("budat_month", date_trunc("month", to_date(col("budat"), "yyyyMMdd")))
      .withColumn(
        "join_month",
        when(
          col("budat_month") === date_trunc("month", current_date()),
          add_months(col("budat_month"), -1)
        ).otherwise(col("budat_month"))
      )
      .as("ekbe")

    // --- Step 2: Customs Regional Reporting ---
    val reportingDf = changeFeeds(customs_regional_reporting).snapshot().as("reporting")

    // --- Step 3: FX rates — pre-aggregate to one rate per (fcurr, month) ---
    // fxp_month = date_trunc('month', rate_date); join on month instead of exact date
    // so that a missing rate for a specific day still resolves via the month bucket.
    val fxRatesFqn = "dw_tx.fin_fxrates.fxrates"
    val fxRatesDf = spark
      .table(fxRatesFqn)
      .select(
        col("rate_date"),
        col("fcurr"),
        col("tcurr"),
        col("kurst"),
        col("final_rate")
      )
      .withColumn("fxp_month", date_trunc("month", col("rate_date")))
      .as("fxp")

    // --- Step 4: Join EKBE with CRR on _mk_system, _mk_instance, ebeln, ebelp ---
    val ekbeWithReporting = ekbeDf
      .join(
        reportingDf,
        col("ekbe._mk_system") === col("reporting._mk_system") &&
          col("ekbe._mk_instance") === col("reporting._mk_instance") &&
          col("ekbe.ebeln") === col("reporting.ekko_ebeln") &&
          col("ekbe.ebelp") === col("reporting.ekpo_ebelp"),
        "left"
      )

    // --- Step 5: Join with FX rates on fcurr + month ---
    // budat is a SAP string in yyyyMMdd format; join_month is already computed on ekbe.
    // fxp_month = date_trunc('month', rate_date) on the FX side.
    // If budat falls in the current month, join_month was shifted back one month (see Step 1)
    // to ensure we always hit a fully-settled rate month.
    val ekbeWithFx = ekbeWithReporting
      .join(
        fxRatesDf,
        col("fxp.fcurr") === col("ekbe.waers") &&
          col("fxp.fxp_month") === col("ekbe.join_month") &&
          col("fxp.kurst") === "ZAYD" &&
          col("fxp.tcurr") === "EUR",
        "left"
      )

    // --- Step 6: Compute import_ classification ---
    val withImport = ekbeWithFx
      .withColumn(
        "import_",
        when(
          col("reporting.t001w_iso_code") === col("reporting.lfa1_iso_code"),
          lit("Domestic-Trade")
        ).when(
          col("reporting.t001w_member_of_eu") === 1L &&
            col("reporting.lfa1_member_of_eu") === 1L &&
            col("reporting.t001w_iso_code") =!= col("reporting.lfa1_iso_code"),
          lit("EU-Trade")
        ).otherwise(lit("import"))
      )

    // --- Step 7: Compute stprs_per_unit ---
    val withStprs = withImport
      .withColumn(
        "stprs_per_unit",
        when(
          col("reporting.ekpo_pstyp") === "2",
          coalesce(col("reporting.mbew_stprs"), lit(0.0)) /
            when(coalesce(col("reporting.mbew_peinh"), lit(1.0)) === 0.0, lit(1.0))
              .otherwise(coalesce(col("reporting.mbew_peinh"), lit(1.0)))
        ).otherwise(
          (coalesce(col("reporting.ekpo_netpr"), lit(0.0)) /
            when(coalesce(col("reporting.ekpo_peinh"), lit(1.0)) === 0.0, lit(1.0))
              .otherwise(coalesce(col("reporting.ekpo_peinh"), lit(1.0)))) *
            coalesce(col("ekbe.menge"), lit(0.0))
        )
      )

    // --- Step 8: Compute dmbtr_EUR ---
    val withDmbtrEur = withStprs
      .withColumn(
        "fx_rate_safe",
        when(coalesce(col("fxp.final_rate"), lit(1.0)) === 0.0, lit(1.0))
          .otherwise(coalesce(col("fxp.final_rate"), lit(1.0)))
      )
      .withColumn(
        "dmbtr_EUR",
        when(
          col("ekbe.shkzg") === "H",
          when(col("ekbe.waers") =!= "EUR", (col("stprs_per_unit") / col("fx_rate_safe")) * lit(-1.0))
            .otherwise(col("stprs_per_unit") * lit(-1.0))
        ).otherwise(
          when(col("ekbe.waers") =!= "EUR", col("stprs_per_unit") / col("fx_rate_safe"))
            .otherwise(col("stprs_per_unit"))
        )
      )

    // --- Step 9: Final select ---
    val result = withDmbtrEur.select(
      col("ekbe._mk_system").as("_mk_system"),
      col("ekbe._mk_instance").as("_mk_instance"),
      col("ekbe.gjahr"),
      col("ekbe.vgabe"),
      col("ekbe.zekkn"),
      col("ekbe.belnr"),
      col("ekbe.ebeln"),
      col("ekbe.ebelp"),
      col("ekbe.buzei"),
      col("ekbe.bwart"),
      col("ekbe.budat"),
      col("ekbe.menge"),
      col("ekbe.dmbtr"),
      col("ekbe.wrbtr"),
      col("ekbe.waers"),
      col("ekbe.shkzg"),
      col("ekbe.elikz"),
      col("ekbe.xblnr"),
      col("ekbe.reewr"),
      col("ekbe.lsmng"),
      col("ekbe.lsmeh"),
      col("ekbe.areww"),
      col("ekbe.hswae"),
      col("ekbe.bldat"),
      col("reporting.ekko_ebeln"),
      col("reporting.ekko_bukrs"),
      col("reporting.ekko_loekz"),
      col("reporting.ekko_statu"),
      col("reporting.ekko_aedat"),
      col("reporting.ekko_lifnr"),
      col("reporting.ekko_bsart"),
      col("reporting.ekko_waers"),
      col("reporting.ekko_ekorg"),
      col("reporting.ekpo_ebelp"),
      col("reporting.ekpo_loekz"),
      col("reporting.ekpo_txz01"),
      col("reporting.ekpo_matnr"),
      col("reporting.ekpo_ematn"),
      col("reporting.ekpo_bukrs"),
      col("reporting.ekpo_werks"),
      col("reporting.ekpo_matkl"),
      col("reporting.ekpo_menge"),
      col("reporting.ekpo_meins"),
      col("reporting.ekpo_netpr"),
      col("reporting.ekpo_peinh"),
      col("reporting.ekpo_netwr"),
      col("reporting.ekpo_knttp"),
      col("reporting.ekpo_pstyp"),
      col("reporting.ekpo_inco1"),
      col("reporting.ekpo_inco2"),
      col("reporting.lfa1_lifnr"),
      col("reporting.lfa1_land1"),
      col("reporting.lfa1_name1"),
      col("reporting.lfa1_ort01"),
      col("reporting.lfa1_country"),
      col("reporting.lfa1_iso_code"),
      col("reporting.lfa1_eco_regions"),
      col("reporting.lfa1_subregion"),
      col("reporting.lfa1_latitude_geo_center"),
      col("reporting.lfa1_longitude_geo_center"),
      col("reporting.lfa1_member_of_eu"),
      col("reporting.t001_waers"),
      col("reporting.t001_butxt"),
      col("reporting.t001_ort01"),
      col("reporting.mbew_stprs"),
      col("reporting.mbew_peinh"),
      col("reporting.marc_stawn"),
      col("reporting.marc_herkl"),
      col("reporting.stawn_international"),
      col("reporting.hscode"),
      col("reporting.hscode_filled"),
      col("reporting.hscode_length"),
      col("reporting.hs_code_8"),
      col("reporting.hs_code_2"),
      col("reporting.hs_code_3"),
      col("reporting.hs_code_5"),
      col("reporting.hs_code_6"),
      col("reporting.hs_code_8_6"),
      col("reporting.hsc_cnkey"),
      col("reporting.hsc_level"),
      col("reporting.hsc_hs_code_length"),
      col("reporting.hsc_cn_description"),
      col("reporting.hsc_section"),
      col("reporting.hsc_chapter"),
      col("reporting.hsc_lev5"),
      col("reporting.hsc_lev6"),
      col("reporting.hsc_cn_code"),
      col("reporting.hsc_goods_code"),
      col("reporting.hsc_declareable_attribut"),
      col("reporting.hsc_declarable"),
      col("reporting.hsc_lev7"),
      col("reporting.hsc_lev8"),
      col("reporting.hsc_lev9"),
      col("reporting.hsc_lev10"),
      col("reporting.hsc_lev11"),
      col("reporting.hsc_lev12"),
      col("reporting.mara_mtart"),
      col("reporting.mara_matkl"),
      col("reporting.mara_wrkst"),
      col("reporting.matkl_text"),
      col("reporting.t001w_werks"),
      col("reporting.t001w_name1"),
      col("reporting.t001w_bwkey"),
      col("reporting.t001w_land1"),
      col("reporting.t001w_kunnr"),
      col("reporting.t001w_lifnr"),
      col("reporting.t001w_country"),
      col("reporting.t001w_iso_code"),
      col("reporting.t001w_eco_regions"),
      col("reporting.t001w_subregion"),
      col("reporting.t001w_latitude_geo_center"),
      col("reporting.t001w_longitude_geo_center"),
      col("reporting.t001w_member_of_eu"),
      coalesce(col("fxp.final_rate"), lit(0.0)).cast("double").as("fx_rate"),
      col("import_"),
      col("stprs_per_unit"),
      col("dmbtr_EUR").as("dmbtr_eur")
    )

    table.overwriteByKeys(result)
  }

  override def validate(): Unit = {
    super.validate()
    require(
      sourceTableSpecs.toSet == Set(ekbe, customs_regional_reporting),
      s"import_table sourceTableSpecs unexpected: $sourceTableSpecs"
    )
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_import_table(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val gjahr: ColumnWithName = ColumnWithName(prefix, "gjahr")
  val vgabe: ColumnWithName = ColumnWithName(prefix, "vgabe")
  val zekkn: ColumnWithName = ColumnWithName(prefix, "zekkn")
  val belnr: ColumnWithName = ColumnWithName(prefix, "belnr")
  val ebeln: ColumnWithName = ColumnWithName(prefix, "ebeln")
  val ebelp: ColumnWithName = ColumnWithName(prefix, "ebelp")
  val buzei: ColumnWithName = ColumnWithName(prefix, "buzei")
  val bwart: ColumnWithName = ColumnWithName(prefix, "bwart")
  val budat: ColumnWithName = ColumnWithName(prefix, "budat")
  val menge: ColumnWithName = ColumnWithName(prefix, "menge")
  val dmbtr: ColumnWithName = ColumnWithName(prefix, "dmbtr")
  val wrbtr: ColumnWithName = ColumnWithName(prefix, "wrbtr")
  val waers: ColumnWithName = ColumnWithName(prefix, "waers")
  val shkzg: ColumnWithName = ColumnWithName(prefix, "shkzg")
  val elikz: ColumnWithName = ColumnWithName(prefix, "elikz")
  val xblnr: ColumnWithName = ColumnWithName(prefix, "xblnr")
  val reewr: ColumnWithName = ColumnWithName(prefix, "reewr")
  val lsmng: ColumnWithName = ColumnWithName(prefix, "lsmng")
  val lsmeh: ColumnWithName = ColumnWithName(prefix, "lsmeh")
  val areww: ColumnWithName = ColumnWithName(prefix, "areww")
  val hswae: ColumnWithName = ColumnWithName(prefix, "hswae")
  val bldat: ColumnWithName = ColumnWithName(prefix, "bldat")
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
  val fx_rate: ColumnWithName = ColumnWithName(prefix, "fx_rate")
  val import_ : ColumnWithName = ColumnWithName(prefix, "import_")
  val stprs_per_unit: ColumnWithName = ColumnWithName(prefix, "stprs_per_unit")
  val dmbtr_eur: ColumnWithName = ColumnWithName(prefix, "dmbtr_eur")
}

object C_import_table extends C_import_table("") {
  def withDFAlias(alias: String): C_import_table = new C_import_table(alias)
  def withoutDFAlias: C_import_table = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_import_table = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
