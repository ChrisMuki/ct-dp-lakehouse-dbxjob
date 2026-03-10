package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_operating_system_E(
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
  prebootos_string: String,
  operating_system_idn_long: BoxedLong,
  lastunlockdate_string: String,
  lastlockdate_string: String,
  laststartuptime_string: String,
  daysstartup_long: BoxedLong,
  computer_idn_long: BoxedLong,
  _sourceinstance_string: String,
  version_string: String,
  architecture_string: String,
  _sourcedb_string: String,
  _sourceport_long: BoxedLong,
  lastlogoffdate_string: String,
  ostype_string: String,
  lastlogondate_string: String,
  coreguid_string: String
) extends Entity

object ivanti_operating_system extends TableSpec[ivanti_operating_system_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
