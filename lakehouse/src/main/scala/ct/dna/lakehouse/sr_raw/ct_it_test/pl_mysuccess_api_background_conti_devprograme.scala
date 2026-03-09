package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_mysuccess_api_background_conti_devprograme_E(
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
    dev_program_string: String,
    completedyear_string: String,
    userid_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    backgroundelementid_string: String,
    div_dev_program_string: String,
    bgorderpos_string: String,
    lastmodifieddate_string: String
) extends Entity

object pl_mysuccess_api_background_conti_devprograme
    extends TableSpec[pl_mysuccess_api_background_conti_devprograme_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
