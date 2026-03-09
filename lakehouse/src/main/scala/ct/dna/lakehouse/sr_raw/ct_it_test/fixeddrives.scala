package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class fixeddrives_E(
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
    deviceid_string: String,
    _sourcehost_string: String,
    storagetotal_long: BoxedLong,
    name_string: String,
    capabilities_long: BoxedLong,
    bytespersect_long: BoxedLong,
    manufacturer_string: String,
    sed_string: String,
    vendordesc_string: String,
    driveno_long: BoxedLong,
    sectors_long: BoxedLong,
    _sourceinstance_string: String,
    partitions_long: BoxedLong,
    trackspercylinder_long: BoxedLong,
    cmosramtype_string: String,
    drivetype_string: String,
    totaltracks_long: BoxedLong,
    mediatype_string: String,
    cylinders_long: BoxedLong,
    caption_string: String,
    bustype_string: String,
    diskindex_long: BoxedLong,
    fde_string: String,
    fixeddrives_idn_long: BoxedLong,
    heads_long: BoxedLong,
    model_string: String,
    totalsectors_long: BoxedLong,
    healthstatus_string: String,
    computer_idn_long: BoxedLong,
    interface_string: String,
    firmwarerev_string: String,
    serialnumber_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    coreguid_string: String
) extends Entity

object fixeddrives
    extends TableSpec[fixeddrives_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
