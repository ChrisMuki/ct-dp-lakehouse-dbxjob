package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class tcp_E(
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
    websharing_string: String,
    support_string: String,
    installed_string: String,
    subnetbroadcastaddr_string: String,
    networksoftware_idn_long: BoxedLong,
    namesvraddr_string: String,
    ipsecpolicy_string: String,
    version_string: String,
    netbiosres_string: String,
    domain_string: String,
    hostname_string: String,
    iprouting_string: String,
    defgtwyaddr_string: String,
    subnetmask_string: String,
    active_string: String,
    multihoming_string: String,
    winsproxyenabled_string: String,
    computer_idn_long: BoxedLong,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    address_string: String,
    _sourceinstance_string: String,
    defgtwymacaddr_string: String,
    coreguid_string: String
) extends Entity

object tcp
    extends TableSpec[tcp_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
