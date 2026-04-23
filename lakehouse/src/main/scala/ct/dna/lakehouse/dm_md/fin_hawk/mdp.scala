package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.{ChangeFeed, Result, Table}
import ct.dna.lakehouse.core.framework.origin.Updated
import ct.dna.lakehouse.core.model.Entity.{Decimal, PK}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.dm_md.fin_hawk.{marc => dm_marc}
import ct.dna.lakehouse.dm_md.fin_hawk.{mdm => dm_mdm}
import ct.dna.lakehouse.dm_md.fin_hawk.{mara => dm_mara}
import ct.dna.lakehouse.dm_md.fin_hawk.{t023t => dm_t023t}
import ct.dna.lakehouse.dm_md.fin_hawk.{t001w => dm_t001w}
import ct.dna.lakehouse.dm_md.fin_hawk.{t001k => dm_t001k}
import ct.dna.lakehouse.dm_md.fin_hawk.{t001 => dm_t001}
import ct.dna.lakehouse.dm_md.fin_hawk.{makt => dm_makt}
// TODO: Uncomment once available in sr_raw
// import ct.dna.lakehouse.sr_raw.ct_it_test.{countries_ww_fixed => countries_ww}
// import ct.dna.lakehouse.sr_raw.ct_it_test.{sap_systems => sap_systems}
import ct.dna.lakehouse.core.runtime.SparkEnv
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame

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
      dm_makt
      // TODO: Uncomment once available in sr_raw
      // countries_ww,
      // sap_systems
    )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val marcDf = changeFeeds(dm_marc).toDF().alias("marc")
    val mdmDf = changeFeeds(dm_mdm).toDF().alias("mdm")
    val maraDf = changeFeeds(dm_mara).toDF().alias("mara")
    val t023tDf = changeFeeds(dm_t023t).toDF().alias("t023t")
    val t001wDf = changeFeeds(dm_t001w).toDF().alias("t001w")
    val t001kDf = changeFeeds(dm_t001k).toDF().alias("t001k")
    val t001Df = changeFeeds(dm_t001).toDF().alias("t001")
    val maktDf = changeFeeds(dm_makt).toDF().alias("makt")
    val spark = SparkSession.active

    def sameSchemaTable(sourceFqn: String, tableName: String): String = {
      val parts = sourceFqn.split("\\.", 3)
      if (parts.length == 3) s"${parts(0)}.${parts(1)}.$tableName" else tableName
    }

    def readLookup(sourceFqn: String, fallbackTableName: String): DataFrame = {
      val fallbackFqn = sameSchemaTable(sourceFqn, fallbackTableName)
      val sourceDf = if (spark.catalog.tableExists(sourceFqn)) Some(spark.table(sourceFqn)) else None
      val fallbackDf = if (spark.catalog.tableExists(fallbackFqn)) Some(spark.table(fallbackFqn)) else None

      (sourceDf, fallbackDf) match {
        case (Some(source), Some(fallback)) => if (source.limit(1).count() > 0) source else fallback
        case (Some(source), None)           => source
        case (None, Some(fallback))         => fallback
        case (None, None) =>
          throw new IllegalStateException(s"Lookup table not found: $sourceFqn or $fallbackFqn")
      }
    }

    // TODO: Uncomment once available in sr_raw
    // val countriesSourceFqn = SparkEnv.idResolver.asSourceFQN(countries_ww).fqn
    // val sapSystemsSourceFqn = SparkEnv.idResolver.asSourceFQN(sap_systems).fqn

    // val countriesDf = readLookup(countriesSourceFqn, "countries_ww_fixed").alias("country_ww")
    // val sapSystemsDf = readLookup(sapSystemsSourceFqn, "sap_systems").alias("sap_systems")

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
      // TODO: Uncomment once available in sr_raw
      // .join(
      //   countriesDf.alias("country_ww_werks"),
      //   col("t001w.land1") === col("country_ww_werks.`alpha_2_string`"),
      //   "left"
      // )
      // .join(
      //   countriesDf.alias("country_ww_cu"),
      //   col("t001.land1") === col("country_ww_cu.`alpha_2_string`"),
      //   "left"
      // )
      .join(
        maktDf,
        col("marc._mk_system") === col("makt._mk_system") &&
          col("marc._mk_instance") === col("makt._mk_instance") &&
          col("marc.matnr") === col("makt.matnr"),
        "left"
      )
    // TODO: Uncomment once available in sr_raw
    // .join(
    //   sapSystemsDf,
    //   concat_ws(
    //     "",
    //     trim(coalesce(col("marc._mk_system"), lit(""))),
    //     trim(coalesce(col("marc._mk_instance"), lit("")))
    //   ) === trim(coalesce(col("sap_systems.sap_source_string"), lit(""))),
    //   "left"
    // )

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
        concat(col("marc._mk_system"), col("marc._mk_instance"), lit("_"), col("marc.matnr"))
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
      lit(null).cast("string").as("werks_country_name"), // TODO: Replace with col("country_ww_werks.name_string") once available in sr_raw
      col("t001w.name1").as("werks_name"),
      concat_ws(" - ", col("marc.werks"), col("t001w.name1")).as("plant_code_name"),
      col("t001.bukrs").as("company_code"),
      col("t001.butxt").as("company_name"),
      col("t001.land1").as("company_country"),
      concat_ws(" - ", col("t001.bukrs"), col("t001.butxt")).as("company_code_name"),
      // TODO: Replace lit(null) columns below with actual sr_raw columns once available
      lit(null).cast("string").as("cu_country_name"), // col("country_ww_cu.name_string")
      lit(null).cast("long").as("werks_member_of_eu"), // col("country_ww_werks.member_of_eu_string").cast("long")
      lit(null).cast("long").as("cu_member_of_eu"), // col("country_ww_cu.member_of_eu_string").cast("long")
      lit(null).cast("string").as("sap_source"), // col("sap_systems.sap_source_string")
      lit(null).cast("string").as("sys_name"), // col("sap_systems.sys_name_string")
      lit(null).cast("string").as("ssid"), // col("sap_systems.ssid_string")
      lit(null).cast("string").as("sys_type"), // col("sap_systems.sys_type_string")
      lit(null).cast("string").as("group_sector"), // col("sap_systems.group_sector_string")
      lit(null).cast("boolean").as("active") // col("sap_systems.active_string").cast("boolean")
    )

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
