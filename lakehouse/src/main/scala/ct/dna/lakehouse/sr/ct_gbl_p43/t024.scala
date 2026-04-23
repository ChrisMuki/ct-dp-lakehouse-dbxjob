// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_p43

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_p43.{t024 => sr_raw_t024}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrT024(
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
    @PK ekgrp: String,
    eknam: String,
    ektel: String,
    ldest: String,
    telfx: String,
    tel_number: String,
    tel_extens: String,
    smtp_addr: String
) extends Entity

object t024 extends TableSpec[SrT024] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_p43.E_t024] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_p43.E_t024] = sr_raw_t024

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
    ("ekgrp", coalesce(col("ekgrp_string"), lit(""))),
    ("eknam", col("eknam_string")),
    ("ektel", col("ektel_string")),
    ("ldest", col("ldest_string")),
    ("telfx", col("telfx_string")),
    ("tel_number", col("tel_number_string")),
    ("tel_extens", col("tel_extens_string")),
    ("smtp_addr", col("smtp_addr_string"))
  )
}
