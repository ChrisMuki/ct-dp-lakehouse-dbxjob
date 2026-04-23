// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_epp

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull, Decimal}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_epp.{afvu => sr_raw_afvu}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrAfvu(
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
    @PK aufpl: String,
    @PK aplzl: String,
    slwid: String,
    usr00: String,
    usr01: String,
    usr02: String,
    usr03: String,
    @Decimal(13, 3) usr04: java.math.BigDecimal,
    use04: String,
    @Decimal(13, 3) usr05: java.math.BigDecimal,
    use05: String,
    @Decimal(13, 3) usr06: java.math.BigDecimal,
    use06: String,
    @Decimal(13, 3) usr07: java.math.BigDecimal,
    use07: String,
    usr08: String,
    usr09: String,
    usr10: String,
    usr11: String,
    vname: String,
    recid: String,
    etype: String,
    jv_otype: String,
    jv_jibcl: String,
    jv_jibsa: String,
    jv_oco: String,
    sparameter1: String,
    sparameter2: String,
    sparameter3: String,
    ilart_op: String,
    ferc_ind: String
) extends Entity

object afvu extends TableSpec[SrAfvu] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_epp.E_afvu] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_epp.E_afvu] = sr_raw_afvu

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
    ("aufpl", coalesce(col("aufpl_string"), lit(""))),
    ("aplzl", coalesce(col("aplzl_string"), lit(""))),
    ("slwid", col("slwid_string")),
    ("usr00", col("usr00_string")),
    ("usr01", col("usr01_string")),
    ("usr02", col("usr02_string")),
    ("usr03", col("usr03_string")),
    ("usr04", col("usr04_decimal_13_3")),
    ("use04", col("use04_string")),
    ("usr05", col("usr05_decimal_13_3")),
    ("use05", col("use05_string")),
    ("usr06", col("usr06_decimal_13_3")),
    ("use06", col("use06_string")),
    ("usr07", col("usr07_decimal_13_3")),
    ("use07", col("use07_string")),
    ("usr08", col("usr08_string")),
    ("usr09", col("usr09_string")),
    ("usr10", col("usr10_string")),
    ("usr11", col("usr11_string")),
    ("vname", col("vname_string")),
    ("recid", col("recid_string")),
    ("etype", col("etype_string")),
    ("jv_otype", col("jv_otype_string")),
    ("jv_jibcl", col("jv_jibcl_string")),
    ("jv_jibsa", col("jv_jibsa_string")),
    ("jv_oco", col("jv_oco_string")),
    ("sparameter1", col("sparameter1_string")),
    ("sparameter2", col("sparameter2_string")),
    ("sparameter3", col("sparameter3_string")),
    ("ilart_op", col("ilart_op_string")),
    ("ferc_ind", col("ferc_ind_string"))
  )
}
