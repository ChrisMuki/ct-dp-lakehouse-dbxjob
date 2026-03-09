package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class patchhistory_E(
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
    patch_string: String,
    _sourcehost_string: String,
    currentuser_string: String,
    patchhistory_idn_long: BoxedLong,
    workflowstatusid_long: BoxedLong,
    actiondate_string: String,
    message_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    _sourceinstance_string: String,
    type_long: BoxedLong,
    computer_idn_long: BoxedLong,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    actioncode_long: BoxedLong
) extends Entity

object patchhistory
    extends TableSpec[patchhistory_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
