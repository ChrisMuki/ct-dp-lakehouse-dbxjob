package ct.dna.lakehouse.sr_raw.iamer_norz_enterprise
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class enterprise_test_specs_E(
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
    max_failure_double: BoxedDouble,
    unit_string: String,
    x_date_utc_string: String,
    test_machine_type_string: String,
    target_double: BoxedDouble,
    spec_name_string: String,
    min_failure_double: BoxedDouble,
    spec_version_long: BoxedLong,
    test_variable_string: String,
    test_plan_string: String,
    specification_id_string: String,
    plant_string: String
) extends Entity

object enterprise_test_specs
    extends TableSpec[enterprise_test_specs_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
