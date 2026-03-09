package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_mysuccess_api_folocation_E(
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
    customstring2_string: String,
    enddate_string: String,
    externalcode_string: String,
    timezone_string: String,
    name_string: String,
    createdby_string: String,
    lastmodifiedby_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    objectid_string: String,
    createddatetime_string: String,
    internalcode_string: String,
    lastmodifiedon_string: String,
    createdon_string: String,
    startdate_string: String,
    entityoid_string: String,
    customstring3_string: String,
    lastmodifieddatetime_string: String,
    locationgroup_string: String,
    status_string: String
) extends Entity

object pl_mysuccess_api_folocation
    extends TableSpec[pl_mysuccess_api_folocation_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
