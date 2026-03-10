package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_networkadapter_E(
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
  filever_string: String,
  networkadapter_idn_long: BoxedLong,
  datarate_string: String,
  rssi_long: BoxedLong,
  ssid_string: String,
  physicaladdress_string: String,
  bssid_string: String,
  drivername_string: String,
  adapterno_long: BoxedLong,
  filedate_string: String,
  memory_string: String,
  duplex_string: String,
  _sourcehost_string: String,
  description_string: String,
  filesize_long: BoxedLong,
  active_string: String,
  computer_idn_long: BoxedLong,
  fragthreshold_long: BoxedLong,
  linkstatus_string: String,
  connectortype_string: String,
  _sourcedb_string: String,
  _sourceport_long: BoxedLong,
  _sourceinstance_string: String,
  networktype_string: String,
  filedesc_string: String,
  rtsthreshold_long: BoxedLong,
  vendor_string: String,
  coreguid_string: String
) extends Entity

object ivanti_networkadapter extends TableSpec[ivanti_networkadapter_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
