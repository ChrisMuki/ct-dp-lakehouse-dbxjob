package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ld_task_machine_E(
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
    _sourcehost_string: String,
    mac_retcode_long: BoxedLong,
    ld_task_idn_long: BoxedLong,
    package_idn_long: BoxedLong,
    policystarttime_string: String,
    source_type_long: BoxedLong,
    source_idn_long: BoxedLong,
    mac_woke_up_long: BoxedLong,
    custom_message_string: String,
    apm_policy_users_idn_long: BoxedLong,
    computer_idn_long: BoxedLong,
    status_time_string: String,
    stage_long: BoxedLong,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    retcode_message_string: String,
    mac_status_long: BoxedLong,
    _sourceinstance_string: String,
    coreguid_string: String
) extends Entity

object ld_task_machine
    extends TableSpec[ld_task_machine_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
