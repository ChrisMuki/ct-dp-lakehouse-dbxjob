// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_e32

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull, Decimal}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_e32.{mchb => sr_raw_mchb}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrMchb(
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
    @PK werks: String,
    @PK lgort: String,
    @PK charg: String,
    lvorm: String,
    ersda: String,
    ernam: String,
    laeda: String,
    aenam: String,
    lfgja: String,
    lfmon: String,
    sperc: String,
    @Decimal(13, 3) clabs: java.math.BigDecimal,
    @Decimal(13, 3) cumlm: java.math.BigDecimal,
    @Decimal(13, 3) cinsm: java.math.BigDecimal,
    @Decimal(13, 3) ceinm: java.math.BigDecimal,
    @Decimal(13, 3) cspem: java.math.BigDecimal,
    @Decimal(13, 3) cretm: java.math.BigDecimal,
    @Decimal(13, 3) cvmla: java.math.BigDecimal,
    @Decimal(13, 3) cvmum: java.math.BigDecimal,
    @Decimal(13, 3) cvmin: java.math.BigDecimal,
    @Decimal(13, 3) cvmei: java.math.BigDecimal,
    @Decimal(13, 3) cvmsp: java.math.BigDecimal,
    @Decimal(13, 3) cvmre: java.math.BigDecimal,
    kzicl: String,
    kzicq: String,
    kzice: String,
    kzics: String,
    kzvcl: String,
    kzvcq: String,
    kzvce: String,
    kzvcs: String,
    herkl: String,
    chdll: String,
    chjin: String,
    chrue: String,
    sgt_scat: String
) extends Entity

object mchb extends TableSpec[SrMchb] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_e32.E_mchb] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_e32.E_mchb] = sr_raw_mchb

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
    ("werks", coalesce(col("werks_string"), lit(""))),
    ("lgort", coalesce(col("lgort_string"), lit(""))),
    ("charg", coalesce(col("charg_string"), lit(""))),
    ("lvorm", col("lvorm_string")),
    ("ersda", col("ersda_string")),
    ("ernam", col("ernam_string")),
    ("laeda", col("laeda_string")),
    ("aenam", col("aenam_string")),
    ("lfgja", col("lfgja_string")),
    ("lfmon", col("lfmon_string")),
    ("sperc", col("sperc_string")),
    ("clabs", col("clabs_decimal_13_3")),
    ("cumlm", col("cumlm_decimal_13_3")),
    ("cinsm", col("cinsm_decimal_13_3")),
    ("ceinm", col("ceinm_decimal_13_3")),
    ("cspem", col("cspem_decimal_13_3")),
    ("cretm", col("cretm_decimal_13_3")),
    ("cvmla", col("cvmla_decimal_13_3")),
    ("cvmum", col("cvmum_decimal_13_3")),
    ("cvmin", col("cvmin_decimal_13_3")),
    ("cvmei", col("cvmei_decimal_13_3")),
    ("cvmsp", col("cvmsp_decimal_13_3")),
    ("cvmre", col("cvmre_decimal_13_3")),
    ("kzicl", col("kzicl_string")),
    ("kzicq", col("kzicq_string")),
    ("kzice", col("kzice_string")),
    ("kzics", col("kzics_string")),
    ("kzvcl", col("kzvcl_string")),
    ("kzvcq", col("kzvcq_string")),
    ("kzvce", col("kzvce_string")),
    ("kzvcs", col("kzvcs_string")),
    ("herkl", col("herkl_string")),
    ("chdll", col("chdll_string")),
    ("chjin", col("chjin_string")),
    ("chrue", col("chrue_string")),
    ("sgt_scat", col("sgt_scat_string"))
  )
}
