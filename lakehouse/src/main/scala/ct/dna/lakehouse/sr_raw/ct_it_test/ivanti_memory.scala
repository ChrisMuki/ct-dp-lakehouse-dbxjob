package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_memory_E(
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
  storagetotal_long: BoxedLong,
  storageavail_long: BoxedLong,
  ramtotal_long: BoxedLong,
  emsavail_long: BoxedLong,
  bytestotal_long: BoxedLong,
  convavail_long: BoxedLong,
  numcards_long: BoxedLong,
  virtavail_long: BoxedLong,
  virttotal_long: BoxedLong,
  l2total_long: BoxedLong,
  maxmem_long: BoxedLong,
  _sourceinstance_string: String,
  pageavail2_long: BoxedLong,
  convtotal_long: BoxedLong,
  xmstotal_long: BoxedLong,
  pagemaxsize_long: BoxedLong,
  exttotal_long: BoxedLong,
  numslots_long: BoxedLong,
  emstotal_long: BoxedLong,
  sysheapsize_long: BoxedLong,
  pageavail_long: BoxedLong,
  memdynamic_long: BoxedLong,
  extavail_long: BoxedLong,
  virtstatus_string: String,
  ramavail_long: BoxedLong,
  computer_idn_long: BoxedLong,
  _sourcedb_string: String,
  pagetotal_long: BoxedLong,
  xmsavail_long: BoxedLong,
  _sourceport_long: BoxedLong,
  virtvol_string: String,
  bytesavail_long: BoxedLong,
  coreguid_string: String
) extends Entity

object ivanti_memory extends TableSpec[ivanti_memory_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
