package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_agentbehavior_E(
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
  behaviorxml_string: String,
  _sourcehost_string: String,
  syncenabled_long: BoxedLong,
  notes_string: String,
  guid_string: String,
  name_string: String,
  sourcecore_string: String,
  isdefault_long: BoxedLong,
  revision_long: BoxedLong,
  lastsaveddate_string: String,
  contains_masked_personal_data_boolean: BoxedBoolean,
  behaviortype_long: BoxedLong,
  lastsavedby_string: String,
  consoleuser_idn_long: BoxedLong,
  _sourceinstance_string: String,
  agentbehavior_idn_long: BoxedLong,
  _sourcedb_string: String,
  _sourceport_long: BoxedLong,
  coreguid_string: String
) extends Entity

object ivanti_agentbehavior extends TableSpec[ivanti_agentbehavior_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
