package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ivanti_localusers_E(
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
    profilepics_long: BoxedLong,
    localusers_idn_long: BoxedLong,
    name_string: String,
    profiledocs_long: BoxedLong,
    contains_masked_personal_data_boolean: BoxedBoolean,
    computer_idn_long: BoxedLong,
    profilevids_long: BoxedLong,
    _sourcehost_string: String,
    description_string: String,
    profilemusic_long: BoxedLong,
    profiletotal_long: BoxedLong,
    profiledwnlds_long: BoxedLong,
    fullname_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    accountdisabled_string: String,
    _sourceinstance_string: String,
    passwordlastset_string: String,
    coreguid_string: String
) extends Entity

object ivanti_localusers
    extends TableSpec[ivanti_localusers_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
