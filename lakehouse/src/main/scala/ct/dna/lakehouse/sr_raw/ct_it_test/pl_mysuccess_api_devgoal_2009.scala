package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_mysuccess_api_devgoal_2009_E(
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
    modifier_string: String,
    name_string: String,
    state_string: String,
    comment_string: String,
    category_string: String,
    lastmodified_string: String,
    id_string: String,
    userid_string: String,
    purpose2_string: String,
    purposelabel_string: String,
    purpose_string: String,
    start_string: String,
    guid_string: String,
    metric_string: String,
    flag_long: BoxedLong,
    statelabel_string: String,
    type_string: String,
    numbering_string: String,
    due_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean
) extends Entity

object pl_mysuccess_api_devgoal_2009
    extends TableSpec[pl_mysuccess_api_devgoal_2009_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
