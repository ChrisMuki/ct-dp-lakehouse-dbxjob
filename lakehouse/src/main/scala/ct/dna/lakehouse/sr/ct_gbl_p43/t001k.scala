// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_p43

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull, Decimal}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_p43.{t001k => sr_raw_t001k}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrT001k(
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
    @PK bwkey: String,
    bukrs: String,
    bwmod: String,
    xbkng: String,
    mlbwa: String,
    mlbwv: String,
    xvkbw: String,
    erklaerkom: String,
    uprof: String,
    wbpro: String,
    mlast: String,
    mlasv: String,
    @Decimal(5, 2) bdifp: java.math.BigDecimal,
    xlbpd: String,
    xewrx: String,
    x2fdo: String,
    prsfr: String,
    mlccs: String,
    xefre: String,
    efrej: String,
    _fmp_prsfr: String,
    _fmp_prfrgr: String
) extends Entity

object t001k extends TableSpec[SrT001k] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_p43.E_t001k] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_p43.E_t001k] = sr_raw_t001k

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
    ("bwkey", coalesce(col("bwkey_string"), lit(""))),
    ("bukrs", col("bukrs_string")),
    ("bwmod", col("bwmod_string")),
    ("xbkng", col("xbkng_string")),
    ("mlbwa", col("mlbwa_string")),
    ("mlbwv", col("mlbwv_string")),
    ("xvkbw", col("xvkbw_string")),
    ("erklaerkom", col("erklaerkom_string")),
    ("uprof", col("uprof_string")),
    ("wbpro", col("wbpro_string")),
    ("mlast", col("mlast_string")),
    ("mlasv", col("mlasv_string")),
    ("bdifp", col("bdifp_decimal_5_2")),
    ("xlbpd", col("xlbpd_string")),
    ("xewrx", col("xewrx_string")),
    ("x2fdo", col("x2fdo_string")),
    ("prsfr", col("prsfr_string")),
    ("mlccs", col("mlccs_string")),
    ("xefre", col("xefre_string")),
    ("efrej", col("efrej_string")),
    ("_fmp_prsfr", col("_fmp_prsfr_string")),
    ("_fmp_prfrgr", col("_fmp_prfrgr_string"))
  )
}
