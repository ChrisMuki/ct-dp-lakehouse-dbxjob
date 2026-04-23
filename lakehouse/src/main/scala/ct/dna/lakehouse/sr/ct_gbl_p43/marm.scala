// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_p43

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull, Decimal}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_p43.{marm => sr_raw_marm}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrMarm(
    @NotNull _mk_org: String,
    @NotNull _mk_site: String,
    @NotNull _mk_system: String,
    @NotNull _mk_instance: String,
    @NotNull _mk_partition: String,
    @NotNull _mk_file: String,
    @NotNull _mk_container: String,
    @NotNull _mk_account: String,
    @NotNull _mk_created_at: Timestamp,
    _lh_id_in_message: Long,
    _lh_ingest_warning: String,
    mandt: String,
    @PK matnr: String,
    @PK meinh: String,
    @Decimal(5, 0) umrez: java.math.BigDecimal,
    @Decimal(5, 0) umren: java.math.BigDecimal,
    eannr: String,
    ean11: String,
    numtp: String,
    @Decimal(13, 3) laeng: java.math.BigDecimal,
    @Decimal(13, 3) breit: java.math.BigDecimal,
    @Decimal(13, 3) hoehe: java.math.BigDecimal,
    meabm: String,
    @Decimal(13, 3) volum: java.math.BigDecimal,
    voleh: String,
    @Decimal(13, 3) brgew: java.math.BigDecimal,
    gewei: String,
    mesub: String,
    atinn: String,
    mesrt: String,
    xfhdw: String,
    xbeww: String,
    kzwso: String,
    msehi: String,
    bflme_marm: String,
    gtin_variant: String,
    @Decimal(3, 0) nest_ftr: java.math.BigDecimal,
    max_stack: Int,
    @Decimal(15, 3) capause: java.math.BigDecimal,
    ty2tq: String,
    pcbut: String
) extends Entity

object marm extends TableSpec[SrMarm] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_p43.E_marm] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_p43.E_marm] = sr_raw_marm

  def sequenceBy: Column = struct(col("_mk_created_at"), col("_lh_id_in_message"))

  lazy val preApplyMapping: Seq[(String, Column)] = Seq(
    ("_mk_org", col("_mk_org")),
    ("_mk_site", col("_mk_site")),
    ("_mk_system", col("_mk_system")),
    ("_mk_instance", col("_mk_instance")),
    ("_mk_partition", col("_mk_partition")),
    ("_mk_file", col("_mk_file")),
    ("_mk_container", col("_mk_container")),
    ("_mk_account", col("_mk_account")),
    ("_mk_created_at", col("_mk_created_at")),
    ("_lh_id_in_message", col("_lh_id_in_message")),
    ("_lh_ingest_warning", col("_lh_ingest_warning")),
    ("mandt", col("mandt_string")),
    ("matnr", coalesce(col("matnr_string"), lit(""))),
    ("meinh", coalesce(col("meinh_string"), lit(""))),
    ("umrez", col("umrez_decimal_5_0")),
    ("umren", col("umren_decimal_5_0")),
    ("eannr", col("eannr_string")),
    ("ean11", col("ean11_string")),
    ("numtp", col("numtp_string")),
    ("laeng", col("laeng_decimal_13_3")),
    ("breit", col("breit_decimal_13_3")),
    ("hoehe", col("hoehe_decimal_13_3")),
    ("meabm", col("meabm_string")),
    ("volum", col("volum_decimal_13_3")),
    ("voleh", col("voleh_string")),
    ("brgew", col("brgew_decimal_13_3")),
    ("gewei", col("gewei_string")),
    ("mesub", col("mesub_string")),
    ("atinn", col("atinn_string")),
    ("mesrt", col("mesrt_string")),
    ("xfhdw", col("xfhdw_string")),
    ("xbeww", col("xbeww_string")),
    ("kzwso", col("kzwso_string")),
    ("msehi", col("msehi_string")),
    ("bflme_marm", col("bflme_marm_string")),
    ("gtin_variant", col("gtin_variant_string")),
    ("nest_ftr", col("nest_ftr_decimal_3_0")),
    ("max_stack", coalesce(col("max_stack_int"), lit(0))),
    ("capause", col("capause_decimal_15_3")),
    ("ty2tq", col("ty2tq_string")),
    ("pcbut", col("pcbut_string"))
  )
}
