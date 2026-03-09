package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class motherboard_E(
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
    busspeed_long: BoxedLong,
    maxcpu_long: BoxedLong,
    pcislots_long: BoxedLong,
    numslots_string: String,
    computer_idn_long: BoxedLong,
    pcislotsavail_long: BoxedLong,
    opgrpindex_long: BoxedLong,
    _sourceinstance_string: String,
    frugrpindex_long: BoxedLong,
    manufacturer_string: String,
    chipset_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    mbversion_string: String,
    serialnum_string: String,
    productname_string: String,
    coreguid_string: String
) extends Entity

object motherboard
    extends TableSpec[motherboard_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
