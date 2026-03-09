package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ivanti_osnt_E(
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
    releaseid_string: String,
    ubr_string: String,
    buildlab_string: String,
    operating_system_idn_long: BoxedLong,
    language_string: String,
    installdate_string: String,
    currentbuild_string: String,
    updchannel_string: String,
    computer_idn_long: BoxedLong,
    muilang_string: String,
    _sourceinstance_string: String,
    systemroot_string: String,
    servicepack_string: String,
    _sourcehost_string: String,
    lastbootuptime_string: String,
    wimregistered_string: String,
    server_string: String,
    dispversion_string: String,
    currentversion_string: String,
    currenttype_string: String,
    serialnumber_string: String,
    regorg_string: String,
    registeredowner_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    coreguid_string: String
) extends Entity

object ivanti_osnt
    extends TableSpec[ivanti_osnt_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
