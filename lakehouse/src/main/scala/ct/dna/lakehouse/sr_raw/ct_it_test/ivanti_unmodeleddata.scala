package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ivanti_unmodeleddata_E(
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
    unmodeleddata_idn_long: BoxedLong,
    datablob_string: String,
    dataint_long: BoxedLong,
    metaobjattrrelations_idn_long: BoxedLong,
    computer_idn_long: BoxedLong,
    pkeyval_string: String,
    datadate_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    _sourceinstance_string: String,
    datastring_string: String,
    coreguid_string: String
) extends Entity

object ivanti_unmodeleddata
    extends TableSpec[ivanti_unmodeleddata_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
