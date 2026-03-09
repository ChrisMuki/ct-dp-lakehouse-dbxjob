package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ivanti_swpkgmanager_E(
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
    license_string: String,
    publisher_string: String,
    pkgsize_string: String,
    name_string: String,
    category_string: String,
    buildtime_string: String,
    installformattime_string: String,
    computer_idn_long: BoxedLong,
    version_string: String,
    installtime_string: String,
    _sourceinstance_string: String,
    _sourcehost_string: String,
    description_string: String,
    _sourcedb_string: String,
    swpkgmanager_idn_long: BoxedLong,
    _sourceport_long: BoxedLong,
    release_string: String,
    coreguid_string: String
) extends Entity

object ivanti_swpkgmanager
    extends TableSpec[ivanti_swpkgmanager_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
