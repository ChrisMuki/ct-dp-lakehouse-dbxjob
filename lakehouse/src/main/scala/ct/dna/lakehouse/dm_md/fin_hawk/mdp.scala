package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.dm_md.fin_hawk.{makt => dm_makt}
import ct.dna.lakehouse.dm_md.fin_hawk.{mara => dm_mara}
import ct.dna.lakehouse.dm_md.fin_hawk.{marc => dm_marc}
import ct.dna.lakehouse.dm_md.fin_hawk.{mdm => dm_mdm}
import ct.dna.lakehouse.dm_md.fin_hawk.{t001 => dm_t001}
import ct.dna.lakehouse.dm_md.fin_hawk.{t001k => dm_t001k}
import ct.dna.lakehouse.dm_md.fin_hawk.{t001w => dm_t001w}
import ct.dna.lakehouse.dm_md.fin_hawk.{t023t => dm_t023t}
import ct.dna.lakehouse.sr_raw.mn_gbl_spcustoms.countries_ww
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

case class DmMdp(
    _mk_system: String,
    _mk_instance: String,
    @PK key_column: String,
    matnr: String,
    maktx: String,
    lvorm_plant: String,
    werks: String,
    mfrnr: String,
    mfrpn: String,
    stawn: String,
    steuc: String,
    herkl: String,
    stawn_sap: String,
    steuc_sap: String,
    hscode: String,
    hscode_filled: java.lang.Boolean,
    hscode_length: java.lang.Integer,
    matkl: String,
    wgbez: String,
    mtart: String,
    lvorm: String,
    werks_country: String,
    werks_country_name: String,
    werks_name: String,
    plant_code_name: String,
    company_code: String,
    company_name: String,
    company_country: String,
    company_code_name: String,
    cu_country_name: String,
    werks_member_of_eu: java.lang.Long,
    cu_member_of_eu: java.lang.Long,
    sap_source: String,
    sys_name: String,
    ssid: String,
    sys_type: String,
    group_sector: String,
    active: java.lang.Boolean
) extends Entity

object mdp extends TableSpec[DmMdp] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      dm_marc,
      dm_mdm,
      dm_mara,
      dm_t023t,
      dm_t001w,
      dm_t001k,
      dm_t001,
      dm_makt,
      countries_ww
    )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    // Nothing changed in any source → skip the run entirely.
    if (feeds.forall { case (_, f) => f.isUnchanged }) return Result.NoChanges

    val marcDf = changeFeeds(dm_marc).toDF().alias("marc")
    val mdmDf = changeFeeds(dm_mdm).toDF().alias("mdm")
    val maraDf = changeFeeds(dm_mara).toDF().alias("mara")
    // The dimension tables below are small relative to marc/mdm/mara — broadcasting
    // them avoids the per-join shuffle and lets the planner fuse them into a single
    // map-side stage on the marc side.
    val t023tDf = broadcast(changeFeeds(dm_t023t).toDF()).alias("t023t")
    val t001wDf = broadcast(changeFeeds(dm_t001w).toDF()).alias("t001w")
    val t001kDf = broadcast(changeFeeds(dm_t001k).toDF()).alias("t001k")
    val t001Df = broadcast(changeFeeds(dm_t001).toDF()).alias("t001")
    val maktDf = broadcast(changeFeeds(dm_makt).toDF()).alias("makt")

    // `countries_ww` is a global Loaded reference table whose business PK is
    // `(_mk_instance, _mk_partition, _mk_file, _lh_id_in_message)` — the same `alpha_2_string`
    // can legitimately appear in multiple rows (different ingests / files / instances). The
    // joins below use `alpha_2_string` as the join key, so we must dedupe to one row per
    // `alpha_2_string` to keep the MERGE source unambiguous (otherwise we hit
    // `DELTA_MULTIPLE_SOURCE_ROW_MATCHING_TARGET_ROW_IN_MERGE`). We pick the row from the
    // most recently created file deterministically.
    val countriesDf = broadcast(
      changeFeeds(countries_ww)
        .toDF()
        .withColumn(
          "_rn",
          row_number().over(
            Window
              .partitionBy(col("alpha_2_string"))
              .orderBy(col("_mk_created_at").desc_nulls_last, col("_lh_id_in_message").desc_nulls_last)
          )
        )
        .filter(col("_rn") === 1)
        .drop("_rn")
        .select("alpha_2_string", "name_string", "member_of_eu_long")
    )

    val joined = marcDf
      .join(
        mdmDf,
        col("marc._mk_system") === col("mdm._mk_system") &&
          col("marc._mk_instance") === col("mdm._mk_instance") &&
          col("marc.matnr") === col("mdm.matnr"),
        "left"
      )
      .join(
        maraDf,
        col("marc._mk_system") === col("mara._mk_system") &&
          col("marc._mk_instance") === col("mara._mk_instance") &&
          col("marc.matnr") === col("mara.matnr"),
        "left"
      )
      .join(
        t023tDf,
        col("marc._mk_system") === col("t023t._mk_system") &&
          col("marc._mk_instance") === col("t023t._mk_instance") &&
          col("mara.matkl") === col("t023t.matkl"),
        "left"
      )
      .join(
        t001wDf,
        col("marc._mk_system") === col("t001w._mk_system") &&
          col("marc._mk_instance") === col("t001w._mk_instance") &&
          col("marc.werks") === col("t001w.werks"),
        "left"
      )
      .join(
        t001kDf,
        col("t001w._mk_system") === col("t001k._mk_system") &&
          col("t001w._mk_instance") === col("t001k._mk_instance") &&
          col("t001w.bwkey") === col("t001k.bwkey"),
        "left"
      )
      .join(
        t001Df,
        col("t001k._mk_system") === col("t001._mk_system") &&
          col("t001k._mk_instance") === col("t001._mk_instance") &&
          col("t001k.bukrs") === col("t001.bukrs"),
        "left"
      )
      .join(
        countriesDf.alias("country_ww_werks"),
        col("t001w.land1") === col("country_ww_werks.`alpha_2_string`"),
        "left"
      )
      .join(
        countriesDf.alias("country_ww_cu"),
        col("t001.land1") === col("country_ww_cu.`alpha_2_string`"),
        "left"
      )
      .join(
        maktDf,
        col("marc._mk_system") === col("makt._mk_system") &&
          col("marc._mk_instance") === col("makt._mk_instance") &&
          col("marc.matnr") === col("makt.matnr"),
        "left"
      )

    val cleaned = joined
      .withColumn(
        "stawn_clean",
        when(
          col("marc.stawn").like("00%0") || col("marc.steuc").like("99%9") || col("marc.stawn").like("NN"),
          lit("")
        ).otherwise(col("marc.stawn"))
      )
      .withColumn(
        "steuc_clean",
        when(
          col("marc.steuc").like("00%0") || col("marc.steuc").like("99%9") || col("marc.steuc").like("NN"),
          lit("")
        ).otherwise(col("marc.steuc"))
      )
      .withColumn(
        "hscode",
        when(col("stawn_clean") === "", col("steuc_clean")).otherwise(col("stawn_clean"))
      )
      .withColumn(
        "hscode_filled",
        when(col("hscode") === "", lit(false)).otherwise(lit(true))
      )
      .withColumn("hscode_length", length(col("hscode")))
      .withColumn(
        "key_column",
        concat(
          col("marc._mk_system"),
          col("marc._mk_instance"),
          lit("_"),
          col("marc.matnr"),
          lit("_"),
          col("marc.werks")
        )
      )

    val result = cleaned.select(
      col("marc._mk_system").as("_mk_system"),
      col("marc._mk_instance").as("_mk_instance"),
      col("key_column"),
      col("marc.matnr").as("matnr"),
      col("makt.maktx").as("maktx"),
      col("marc.lvorm_plant").as("lvorm_plant"),
      col("marc.werks").as("werks"),
      col("mara.mfrnr").as("mfrnr"),
      col("mara.mfrpn").as("mfrpn"),
      col("stawn_clean").as("stawn"),
      col("steuc_clean").as("steuc"),
      col("marc.herkl").as("herkl"),
      col("marc.stawn_sap").as("stawn_sap"),
      col("marc.steuc_sap").as("steuc_sap"),
      col("hscode"),
      col("hscode_filled"),
      col("hscode_length"),
      col("mara.matkl").as("matkl"),
      col("t023t.wgbez").as("wgbez"),
      col("mdm.mtart").as("mtart"),
      col("mdm.lvorm").as("lvorm"),
      col("t001w.land1").as("werks_country"),
      col("country_ww_werks.name_string").as("werks_country_name"),
      col("t001w.name1").as("werks_name"),
      concat_ws(" - ", col("marc.werks"), col("t001w.name1")).as("plant_code_name"),
      col("t001.bukrs").as("company_code"),
      col("t001.butxt").as("company_name"),
      col("t001.land1").as("company_country"),
      concat_ws(" - ", col("t001.bukrs"), col("t001.butxt")).as("company_code_name"),
      // TODO: Replace lit(null) columns below with actual sr_raw columns once available
      col("country_ww_cu.name_string").as("cu_country_name"),
      col("country_ww_werks.member_of_eu_long").as("werks_member_of_eu"),
      col("country_ww_cu.member_of_eu_long").as("cu_member_of_eu"),
      lit(null).cast("string").as("sap_source"), // col("sap_systems.sap_source_string")
      lit(null).cast("string").as("sys_name"), // col("sap_systems.sys_name_string")
      lit(null).cast("string").as("ssid"), // col("sap_systems.ssid_string")
      lit(null).cast("string").as("sys_type"), // col("sap_systems.sys_type_string")
      lit(null).cast("string").as("group_sector"), // col("sap_systems.group_sector_string")
      lit(null).cast("boolean").as("active") // col("sap_systems.active_string").cast("boolean")
    )

    table.overwriteByKeys(result)
  }

  override def validate(): Unit = {
    super.validate()
    val expected = Set(dm_marc, dm_mdm, dm_mara, dm_t023t, dm_t001w, dm_t001k, dm_t001, dm_makt, countries_ww)
    require(
      sourceTableSpecs.toSet == expected,
      s"mdp sourceTableSpecs unexpected: $sourceTableSpecs"
    )
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_mdp(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val key_column: ColumnWithName = ColumnWithName(prefix, "key_column")
  val matnr: ColumnWithName = ColumnWithName(prefix, "matnr")
  val maktx: ColumnWithName = ColumnWithName(prefix, "maktx")
  val lvorm_plant: ColumnWithName = ColumnWithName(prefix, "lvorm_plant")
  val werks: ColumnWithName = ColumnWithName(prefix, "werks")
  val mfrnr: ColumnWithName = ColumnWithName(prefix, "mfrnr")
  val mfrpn: ColumnWithName = ColumnWithName(prefix, "mfrpn")
  val stawn: ColumnWithName = ColumnWithName(prefix, "stawn")
  val steuc: ColumnWithName = ColumnWithName(prefix, "steuc")
  val herkl: ColumnWithName = ColumnWithName(prefix, "herkl")
  val stawn_sap: ColumnWithName = ColumnWithName(prefix, "stawn_sap")
  val steuc_sap: ColumnWithName = ColumnWithName(prefix, "steuc_sap")
  val hscode: ColumnWithName = ColumnWithName(prefix, "hscode")
  val hscode_filled: ColumnWithName = ColumnWithName(prefix, "hscode_filled")
  val hscode_length: ColumnWithName = ColumnWithName(prefix, "hscode_length")
  val matkl: ColumnWithName = ColumnWithName(prefix, "matkl")
  val wgbez: ColumnWithName = ColumnWithName(prefix, "wgbez")
  val mtart: ColumnWithName = ColumnWithName(prefix, "mtart")
  val lvorm: ColumnWithName = ColumnWithName(prefix, "lvorm")
  val werks_country: ColumnWithName = ColumnWithName(prefix, "werks_country")
  val werks_country_name: ColumnWithName = ColumnWithName(prefix, "werks_country_name")
  val werks_name: ColumnWithName = ColumnWithName(prefix, "werks_name")
  val plant_code_name: ColumnWithName = ColumnWithName(prefix, "plant_code_name")
  val company_code: ColumnWithName = ColumnWithName(prefix, "company_code")
  val company_name: ColumnWithName = ColumnWithName(prefix, "company_name")
  val company_country: ColumnWithName = ColumnWithName(prefix, "company_country")
  val company_code_name: ColumnWithName = ColumnWithName(prefix, "company_code_name")
  val cu_country_name: ColumnWithName = ColumnWithName(prefix, "cu_country_name")
  val werks_member_of_eu: ColumnWithName = ColumnWithName(prefix, "werks_member_of_eu")
  val cu_member_of_eu: ColumnWithName = ColumnWithName(prefix, "cu_member_of_eu")
  val sap_source: ColumnWithName = ColumnWithName(prefix, "sap_source")
  val sys_name: ColumnWithName = ColumnWithName(prefix, "sys_name")
  val ssid: ColumnWithName = ColumnWithName(prefix, "ssid")
  val sys_type: ColumnWithName = ColumnWithName(prefix, "sys_type")
  val group_sector: ColumnWithName = ColumnWithName(prefix, "group_sector")
  val active: ColumnWithName = ColumnWithName(prefix, "active")
}

object C_mdp extends C_mdp("") {
  def withDFAlias(alias: String): C_mdp = new C_mdp(alias)
  def withoutDFAlias: C_mdp = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_mdp = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
