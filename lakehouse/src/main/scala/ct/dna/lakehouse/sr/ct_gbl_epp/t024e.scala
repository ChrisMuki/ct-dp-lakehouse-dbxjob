// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_epp

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_epp.{t024e => sr_raw_t024e}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrT024e(
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
    @PK ekorg: String,
    ekotx: String,
    bukrs: String,
    txadr: String,
    txkop: String,
    txfus: String,
    txgru: String,
    kalse: String,
    mkals: String,
    bpeff: String,
    bukrs_ntr: String
) extends Entity

object t024e extends TableSpec[SrT024e] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_epp.E_t024e] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_epp.E_t024e] = sr_raw_t024e

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
    ("ekorg", coalesce(col("ekorg_string"), lit(""))),
    ("ekotx", col("ekotx_string")),
    ("bukrs", col("bukrs_string")),
    ("txadr", col("txadr_string")),
    ("txkop", col("txkop_string")),
    ("txfus", col("txfus_string")),
    ("txgru", col("txgru_string")),
    ("kalse", col("kalse_string")),
    ("mkals", col("mkals_string")),
    ("bpeff", col("bpeff_string")),
    ("bukrs_ntr", col("bukrs_ntr_string"))
  )
}
