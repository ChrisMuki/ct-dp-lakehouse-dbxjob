// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_p12

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_p12.{t023t => sr_raw_t023t}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrT023t(
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
    @PK spras: String,
    @PK matkl: String,
    wgbez: String,
    wgbez60: String
) extends Entity

object t023t extends TableSpec[SrT023t] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_p12.E_t023t] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_p12.E_t023t] = sr_raw_t023t

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
    ("spras", coalesce(col("spras_string"), lit(""))),
    ("matkl", coalesce(col("matkl_string"), lit(""))),
    ("wgbez", col("wgbez_string")),
    ("wgbez60", col("wgbez60_string"))
  )
}
