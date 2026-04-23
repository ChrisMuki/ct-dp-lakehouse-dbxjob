// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_epp

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_epp.{konh => sr_raw_konh}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrKonh(
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
    @PK knumh: String,
    ernam: String,
    erdat: String,
    kvewe: String,
    kotabnr: String,
    kappl: String,
    kschl: String,
    vakey: String,
    datab: String,
    datbi: String,
    kosrt: String,
    kzust: String,
    knuma_pi: String,
    knuma_ag: String,
    knuma_sq: String,
    knuma_sd: String,
    aktnr: String,
    knuma_bo: String,
    licno: String,
    licdt: String,
    vadat: String
) extends Entity

object konh extends TableSpec[SrKonh] with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_epp.E_konh] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_epp.E_konh] = sr_raw_konh

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
    ("knumh", coalesce(col("knumh_string"), lit(""))),
    ("ernam", col("ernam_string")),
    ("erdat", col("erdat_string")),
    ("kvewe", col("kvewe_string")),
    ("kotabnr", col("kotabnr_string")),
    ("kappl", col("kappl_string")),
    ("kschl", col("kschl_string")),
    ("vakey", col("vakey_string")),
    ("datab", col("datab_string")),
    ("datbi", col("datbi_string")),
    ("kosrt", col("kosrt_string")),
    ("kzust", col("kzust_string")),
    ("knuma_pi", col("knuma_pi_string")),
    ("knuma_ag", col("knuma_ag_string")),
    ("knuma_sq", col("knuma_sq_string")),
    ("knuma_sd", col("knuma_sd_string")),
    ("aktnr", col("aktnr_string")),
    ("knuma_bo", col("knuma_bo_string")),
    ("licno", col("licno_string")),
    ("licdt", col("licdt_string")),
    ("vadat", col("vadat_string"))
  )
}
