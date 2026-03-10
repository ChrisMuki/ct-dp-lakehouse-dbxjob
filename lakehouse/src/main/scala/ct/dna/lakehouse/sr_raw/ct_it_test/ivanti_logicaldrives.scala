package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_logicaldrives_E(
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
  storagetotal_long: BoxedLong,
  storageavail_long: BoxedLong,
  drivetype_string: String,
  scsi_id_string: String,
  volumelabel_string: String,
  percentfreespace_long: BoxedLong,
  filecnt_long: BoxedLong,
  computer_idn_long: BoxedLong,
  serialnumber_string: String,
  blocksize_long: BoxedLong,
  logicaldrives_idn_long: BoxedLong,
  _sourceinstance_string: String,
  _sourcehost_string: String,
  mediatype_long: BoxedLong,
  initdate_string: String,
  description_string: String,
  foldercnt_long: BoxedLong,
  lastbackupdate_string: String,
  _sourcedb_string: String,
  _sourceport_long: BoxedLong,
  driveletter_string: String,
  removable_string: String,
  filesystem_string: String,
  coreguid_string: String
) extends Entity

object ivanti_logicaldrives extends TableSpec[ivanti_logicaldrives_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
