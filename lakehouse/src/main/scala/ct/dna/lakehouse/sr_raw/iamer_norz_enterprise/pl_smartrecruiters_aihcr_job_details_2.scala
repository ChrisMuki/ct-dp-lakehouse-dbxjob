package ct.dna.lakehouse.sr_raw.iamer_norz_enterprise
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_smartrecruiters_aihcr_job_details_2_E(
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
    job_ref_id_string: String,
    job_id_string: String,
    entry_level_string: String,
    expires_on_dd_mm_yyyy_string: String
) extends Entity

object pl_smartrecruiters_aihcr_job_details_2
    extends TableSpec[pl_smartrecruiters_aihcr_job_details_2_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
