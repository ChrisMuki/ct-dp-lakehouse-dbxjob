package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ivanti_boundadapter_E(
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
    ipaddress_string: String,
    primarydhcp_string: String,
    dhcpenabled_string: String,
    leaseexpires_string: String,
    firewallenabled_string: String,
    secondarydhcp_string: String,
    networksoftware_idn_long: BoxedLong,
    driverversion_string: String,
    secondarydns_string: String,
    subnetmask_string: String,
    dhcpserver_string: String,
    networkid_string: String,
    hidden_string: String,
    ipv6networkid_string: String,
    secondarywins_string: String,
    _sourcehost_string: String,
    description_string: String,
    primarydns_string: String,
    dhcpv6server_string: String,
    adapterno_long: BoxedLong,
    defaultgatewaymac_string: String,
    defaultgateway_string: String,
    ipv6address_string: String,
    physaddress_string: String,
    computer_idn_long: BoxedLong,
    leaseobtained_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    _sourceinstance_string: String,
    boundadapter_idn_long: BoxedLong,
    status_string: String,
    primarywins_string: String,
    coreguid_string: String
) extends Entity

object ivanti_boundadapter
    extends TableSpec[ivanti_boundadapter_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
