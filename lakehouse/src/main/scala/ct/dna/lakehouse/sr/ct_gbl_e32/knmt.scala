// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_e32

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull, Decimal}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_e32.{knmt => sr_raw_knmt}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, hex, lit, struct}

case class SrKnmt(
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
    @PK vkorg: String,
    @PK vtweg: String,
    @PK kunnr: String,
    @PK matnr: String,
    ernam: String,
    erdat: String,
    sortl: String,
    kdmat: String,
    postx: String,
    lprio: String,
    @Decimal(13, 3) minlf: java.math.BigDecimal,
    meins: String,
    chspl: String,
    kztlf: String,
    @Decimal(1, 0) antlf: java.math.BigDecimal,
    @Decimal(3, 1) untto: java.math.BigDecimal,
    @Decimal(3, 1) uebto: java.math.BigDecimal,
    uebtk: String,
    werks: String,
    rdprf: String,
    megru: String,
    j_1btxsdc: String,
    vwpos: String,
    vrkme_t: String,
    @Decimal(5, 0) umvkn_t: java.math.BigDecimal,
    @Decimal(5, 0) umvkz_t: java.math.BigDecimal,
    guid: String,
    zm_herst: String,
    zm_breihe: String,
    zm_modell: String,
    zm_teil: String
) extends Entity

object knmt extends TableSpec[SrKnmt] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_e32.E_knmt] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_e32.E_knmt] = sr_raw_knmt

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
    ("vkorg", coalesce(col("vkorg_string"), lit(""))),
    ("vtweg", coalesce(col("vtweg_string"), lit(""))),
    ("kunnr", coalesce(col("kunnr_string"), lit(""))),
    ("matnr", coalesce(col("matnr_string"), lit(""))),
    ("ernam", col("ernam_string")),
    ("erdat", col("erdat_string")),
    ("sortl", col("sortl_string")),
    ("kdmat", col("kdmat_string")),
    ("postx", col("postx_string")),
    ("lprio", col("lprio_string")),
    ("minlf", col("minlf_decimal_13_3")),
    ("meins", col("meins_string")),
    ("chspl", col("chspl_string")),
    ("kztlf", col("kztlf_string")),
    ("antlf", col("antlf_decimal_1_0")),
    ("untto", col("untto_decimal_3_1")),
    ("uebto", col("uebto_decimal_3_1")),
    ("uebtk", col("uebtk_string")),
    ("werks", col("werks_string")),
    ("rdprf", col("rdprf_string")),
    ("megru", col("megru_string")),
    ("j_1btxsdc", col("j_1btxsdc_string")),
    ("vwpos", col("vwpos_string")),
    ("vrkme_t", col("vrkme_t_string")),
    ("umvkn_t", col("umvkn_t_decimal_5_0")),
    ("umvkz_t", col("umvkz_t_decimal_5_0")),
    ("guid", hex(col("guid_binary"))),
    ("zm_herst", col("zm_herst_string")),
    ("zm_breihe", col("zm_breihe_string")),
    ("zm_modell", col("zm_modell_string")),
    ("zm_teil", col("zm_teil_string"))
  )
}
