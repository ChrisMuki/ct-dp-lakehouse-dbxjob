package ct.dna.lakehouse.sr_raw.ct_gbl_p43
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class konh_E(
  @PK _mk_org: String,
  @PK _mk_site: String,
  @PK _mk_system: String,
  @PK _mk_instance: String,
  @PK _mk_partition: String,
  @PK _mk_file: String,
  @NotNull _mk_container: String,
  @NotNull _mk_account: String,
  @NotNull _mk_created_at: Timestamp,
  @PK _lh_id_in_message: Long,
  _lh_ingest_warning: String,
  knuma_ag_string: String,
  licdt_string: String,
  knuma_sq_string: String,
  knuma_bo_string: String,
  licno_string: String,
  knumh_string: String,
  vadat_string: String,
  mandt_string: String,
  datbi_string: String,
  vakey_string: String,
  kvewe_string: String,
  knuma_sd_string: String,
  kosrt_string: String,
  knuma_pi_string: String,
  kzust_string: String,
  ernam_string: String,
  aktnr_string: String,
  kschl_string: String,
  datab_string: String,
  kappl_string: String,
  erdat_string: String,
  kotabnr_string: String
) extends Entity

object konh extends TableSpec[konh_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
