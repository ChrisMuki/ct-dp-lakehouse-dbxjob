package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ivanti_landesk_E(
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
    proxyid_long: BoxedLong,
    clientconfigurationname_string: String,
    pxestatus_string: String,
    prefserver_string: String,
    mclsernum_string: String,
    subnetdomainrep_long: BoxedLong,
    rebootneeded_long: BoxedLong,
    ldmsmanaged_long: BoxedLong,
    landesk_idn_long: BoxedLong,
    filesize_long: BoxedLong,
    tsrinstalled_string: String,
    filedate_string: String,
    computer_idn_long: BoxedLong,
    configuredon_string: String,
    mcinstalled_string: String,
    _sourcehost_string: String,
    clientpath_string: String,
    rebootneededon_string: String,
    proxyhostname_string: String,
    rtinventory_long: BoxedLong,
    mclkeynum_string: String,
    targetagentver_string: String,
    filepath_string: String,
    dmiinstalled_string: String,
    version_string: String,
    location_string: String,
    revision_string: String,
    agentversion_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    proxynicaddress_string: String,
    fipsenabled_string: String,
    _sourceinstance_string: String,
    coreguid_string: String
) extends Entity

object ivanti_landesk
    extends TableSpec[ivanti_landesk_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
