package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_compsystem_E(
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
  cmmslimport_long: BoxedLong,
  ipmiversion_string: String,
  openmanageadministratorversion_string: String,
  cmmlocation_string: String,
  expressservicecode_string: String,
  sdrversion_string: String,
  openmanagesupport_string: String,
  modeldetail_string: String,
  model_string: String,
  servicetag_string: String,
  computer_idn_long: BoxedLong,
  agentlessinstall_string: String,
  chassistype_string: String,
  _sourcehost_string: String,
  sdrtimestamp_long: BoxedLong,
  cmmguid_string: String,
  supportenddate_string: String,
  esmversion_string: String,
  systemtype_string: String,
  bootmode_string: String,
  sdrpackage_string: String,
  hasbattery_string: String,
  assettag_string: String,
  modelnumber_string: String,
  backplaneversion_string: String,
  boardname_string: String,
  salesordernumber_string: String,
  manufacturedate_string: String,
  manufacturer_string: String,
  sdrfile_string: String,
  modelnum_string: String,
  amtversion_string: String,
  _sourcedb_string: String,
  _sourceport_long: BoxedLong,
  systemversion_string: String,
  cmmsnmpport_long: BoxedLong,
  _sourceinstance_string: String,
  chassisname_string: String,
  platformversion_string: String,
  firmwareversion_string: String,
  serialnum_string: String,
  willautorenew_long: BoxedLong,
  bmcfirmwareversion_string: String,
  openmanagesupportedcomponenets_string: String,
  ibmmodel_string: String,
  coreguid_string: String,
  systemserialnumber_string: String
) extends Entity

object ivanti_compsystem extends TableSpec[ivanti_compsystem_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
