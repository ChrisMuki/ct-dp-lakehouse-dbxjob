package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_smartrecruiters_aihcr_job_details_1_E(
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
    job_approval_pending_days_string: String,
    job_country_string: String,
    job_status_string: String,
    consolidation_unit_string: String,
    number_of_positions_string: String,
    job_status_cancelled_date_string: String,
    job_title_string: String,
    change_of_contract_string: String,
    job_experience_level_string: String,
    time_in_job_status_created_string: String,
    time_in_job_status_sourcing_string: String,
    job_ref_id_string: String,
    job_creation_date_string: String,
    staffing_type_string: String,
    sourcing_string: String,
    working_time_string: String,
    business_area_central_function_1_string: String,
    cost_budget_string: String,
    job_approval_status_change_date_string: String,
    function_string: String,
    time_in_job_status_on_hold_string: String,
    employment_type_string: String,
    evergreen_position_string: String,
    job_approval_status_string: String,
    cost_center_string: String,
    global_salary_type_string: String,
    job_status_sourcing_date_string: String,
    replacement_for_string: String,
    shopfloor_job_profile_string: String,
    job_id_string: String,
    time_in_job_status_cancelled_string: String,
    functional_area_string: String,
    legal_entity_string: String,
    job_status_filled_date_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    time_in_job_status_offer_string: String,
    contract_type_string: String,
    leadership_level_string: String,
    leaving_date_dd_mm_yyyy_string: String,
    job_status_on_hold_date_string: String,
    time_in_job_status_interview_string: String,
    time_in_job_status_filled_string: String,
    business_area_central_function_string: String,
    working_time_in_string: String
) extends Entity

object pl_smartrecruiters_aihcr_job_details_1
    extends TableSpec[pl_smartrecruiters_aihcr_job_details_1_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
