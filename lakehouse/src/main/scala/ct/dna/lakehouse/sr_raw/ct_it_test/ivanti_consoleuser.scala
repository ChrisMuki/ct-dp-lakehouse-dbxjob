package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_consoleuser_E(
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
  onwednesday_long: BoxedLong,
  onmonday_long: BoxedLong,
  objectsid_string: String,
  apm_ldap_server_idn_long: BoxedLong,
  zoned_long: BoxedLong,
  onthursday_long: BoxedLong,
  contains_masked_personal_data_boolean: BoxedBoolean,
  adrights_long: BoxedLong,
  rights_long: BoxedLong,
  consoleuser_idn_long: BoxedLong,
  _sourcehost_string: String,
  onsaturday_long: BoxedLong,
  usetimefilter_long: BoxedLong,
  groups_string: String,
  starthour_string: String,
  usertype_long: BoxedLong,
  stophour_string: String,
  lastlogin_string: String,
  onsunday_long: BoxedLong,
  biospasswordwarningcheck_string: String,
  _sourcedb_string: String,
  onfriday_long: BoxedLong,
  _sourceport_long: BoxedLong,
  displayname_string: String,
  username_string: String,
  ontuesday_long: BoxedLong,
  email_string: String,
  _sourceinstance_string: String
) extends Entity

object ivanti_consoleuser extends TableSpec[ivanti_consoleuser_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
