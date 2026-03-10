package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_package_E(
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
  command_line_string: String,
  discardperiodhours_long: BoxedLong,
  heal_options_long: BoxedLong,
  shortcutpath_string: String,
  revision_long: BoxedLong,
  runmsiexecdirectly_long: BoxedLong,
  install_long: BoxedLong,
  contains_masked_personal_data_boolean: BoxedBoolean,
  consoleuser_idn_long: BoxedLong,
  disablewow64redirect_long: BoxedLong,
  desktopshortcut_long: BoxedLong,
  settings_string: String,
  timeoutperiod_long: BoxedLong,
  _sourcehost_string: String,
  description_string: String,
  uninstall_idn_long: BoxedLong,
  password_string: String,
  package_files_hash_idn_long: BoxedLong,
  enableloggedoffuserinstall_long: BoxedLong,
  syncenabled_long: BoxedLong,
  returncodetemplate_idn_long: BoxedLong,
  legacyapmname_string: String,
  shortcutname_string: String,
  background_long: BoxedLong,
  uacelevation_long: BoxedLong,
  notes_string: String,
  guid_string: String,
  package_idn_long: BoxedLong,
  name_string: String,
  islandeskvirtualapp2_long: BoxedLong,
  package_guid_string: String,
  legacyapmcmdline_string: String,
  sourcecore_string: String,
  category_idn_long: BoxedLong,
  lastsaveddate_string: String,
  disableclientqueue_long: BoxedLong,
  enablevirtualappshortcuts_long: BoxedLong,
  type_long: BoxedLong,
  legacyapmguid_string: String,
  pre_req_query_idn_long: BoxedLong,
  lastsavedby_string: String,
  _sourcedb_string: String,
  _sourceport_long: BoxedLong,
  timeoutenabled_long: BoxedLong,
  username_string: String,
  _sourceinstance_string: String,
  runas_long: BoxedLong
) extends Entity

object ivanti_package extends TableSpec[ivanti_package_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
