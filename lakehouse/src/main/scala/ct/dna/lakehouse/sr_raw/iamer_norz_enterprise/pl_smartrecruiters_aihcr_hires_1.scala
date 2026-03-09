package ct.dna.lakehouse.sr_raw.iamer_norz_enterprise
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_smartrecruiters_aihcr_hires_1_E(
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
    hired_position_hire_date_string: String,
    job_ref_id_string: String,
    application_field_candidate_source_string: String,
    continental_location_epr_string: String,
    job_id_string: String,
    hired_position_actual_start_date_string: String,
    hired_position_target_start_date_string: String,
    application_id_string: String,
    candidate_id_string: String,
    application_field_start_date_string: String
) extends Entity

object pl_smartrecruiters_aihcr_hires_1
    extends TableSpec[pl_smartrecruiters_aihcr_hires_1_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
