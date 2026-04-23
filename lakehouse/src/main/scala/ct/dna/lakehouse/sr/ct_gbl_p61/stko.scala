// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_p61

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull, Decimal}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_p61.{stko => sr_raw_stko}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, hex, lit, struct}

case class SrStko(
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
    @PK stlty: String,
    @PK stlnr: String,
    @PK stlal: String,
    @PK stkoz: String,
    datuv: String,
    techv: String,
    aennr: String,
    lkenz: String,
    loekz: String,
    vgkzl: String,
    andat: String,
    annam: String,
    aedat: String,
    aenam: String,
    bmein: String,
    @Decimal(13, 3) bmeng: java.math.BigDecimal,
    cadkz: String,
    labor: String,
    ltxsp: String,
    stktx: String,
    stlst: String,
    wrkan: String,
    dvdat: String,
    dvnam: String,
    aehlp: String,
    alekz: String,
    guidx: String,
    valid_to: String,
    valid_to_rkey: String,
    ecn_to: String,
    ecn_to_rkey: String
) extends Entity

object stko extends TableSpec[SrStko] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_p61.E_stko] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_p61.E_stko] = sr_raw_stko

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
    ("stlty", coalesce(col("stlty_string"), lit(""))),
    ("stlnr", coalesce(col("stlnr_string"), lit(""))),
    ("stlal", coalesce(col("stlal_string"), lit(""))),
    ("stkoz", coalesce(col("stkoz_string"), lit(""))),
    ("datuv", col("datuv_string")),
    ("techv", col("techv_string")),
    ("aennr", col("aennr_string")),
    ("lkenz", col("lkenz_string")),
    ("loekz", col("loekz_string")),
    ("vgkzl", col("vgkzl_string")),
    ("andat", col("andat_string")),
    ("annam", col("annam_string")),
    ("aedat", col("aedat_string")),
    ("aenam", col("aenam_string")),
    ("bmein", col("bmein_string")),
    ("bmeng", col("bmeng_decimal_13_3")),
    ("cadkz", col("cadkz_string")),
    ("labor", col("labor_string")),
    ("ltxsp", col("ltxsp_string")),
    ("stktx", col("stktx_string")),
    ("stlst", col("stlst_string")),
    ("wrkan", col("wrkan_string")),
    ("dvdat", col("dvdat_string")),
    ("dvnam", col("dvnam_string")),
    ("aehlp", col("aehlp_string")),
    ("alekz", col("alekz_string")),
    ("guidx", hex(col("guidx_binary"))),
    ("valid_to", col("valid_to_string")),
    ("valid_to_rkey", col("valid_to_rkey_string")),
    ("ecn_to", col("ecn_to_string")),
    ("ecn_to_rkey", col("ecn_to_rkey_string"))
  )
}
