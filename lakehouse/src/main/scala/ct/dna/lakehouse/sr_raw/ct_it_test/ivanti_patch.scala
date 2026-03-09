package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ivanti_patch_E(
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
    sha1_string: String,
    name_string: String,
    uniquefilename_string: String,
    familyname_string: String,
    sha256_string: String,
    filesize_long: BoxedLong,
    ignore_long: BoxedLong,
    comments_string: String,
    _sourceinstance_string: String,
    commands_string: String,
    hash_string: String,
    download_long: BoxedLong,
    _sourcehost_string: String,
    vulnerability_idn_long: BoxedLong,
    reboot_long: BoxedLong,
    supercededbyvulid_string: String,
    inpatchdir_long: BoxedLong,
    canrunsilent_long: BoxedLong,
    guid_string: String,
    discardperiodhours_long: BoxedLong,
    uninstallable_long: BoxedLong,
    patch_idn_long: BoxedLong,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    url_string: String,
    vul_id_string: String,
    familyid_long: BoxedLong,
    coreguid_string: String
) extends Entity

object ivanti_patch
    extends TableSpec[ivanti_patch_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
