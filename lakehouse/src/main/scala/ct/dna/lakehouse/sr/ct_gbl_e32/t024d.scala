// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_e32

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_e32.{t024d => sr_raw_t024d}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrT024d(
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
    @PK werks: String,
    @PK dispo: String,
    dsnam: String,
    dstel: String,
    ekgrp: String,
    mempf: String,
    gsber: String,
    prctr: String,
    usrtyp: String,
    usrkey: String
) extends Entity

object t024d extends TableSpec[SrT024d] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_e32.E_t024d] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_e32.E_t024d] = sr_raw_t024d

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
    ("werks", coalesce(col("werks_string"), lit(""))),
    ("dispo", coalesce(col("dispo_string"), lit(""))),
    ("dsnam", col("dsnam_string")),
    ("dstel", col("dstel_string")),
    ("ekgrp", col("ekgrp_string")),
    ("mempf", col("mempf_string")),
    ("gsber", col("gsber_string")),
    ("prctr", col("prctr_string")),
    ("usrtyp", col("usrtyp_string")),
    ("usrkey", col("usrkey_string"))
  )
}
