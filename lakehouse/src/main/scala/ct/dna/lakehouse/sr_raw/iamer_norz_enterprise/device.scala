package ct.dna.lakehouse.sr_raw.iamer_norz_enterprise
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class device_E(
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
    hostname_string: String,
    device_policies_device_control_policy_id_string: String,
    mac_address_string: String,
    platform_name_string: String,
    last_seen_string: String,
    system_product_name_string: String,
    os_product_name_string: String,
    device_policies_firewall_policy_id_string: String,
    local_ip_string: String,
    device_policies_sensor_update_policy_id_string: String,
    first_seen_string: String,
    reduced_functionality_mode_string: String,
    os_version_string: String,
    status_string: String,
    ou_string: String,
    device_policies_prevention_policy_id_string: String,
    system_manufacturer_string: String,
    agent_version_string: String,
    device_id_string: String,
    os_build_string: String,
    cid_string: String,
    kernel_version_string: String,
    product_type_desc_string: String,
    connection_ip_string: String,
    machine_domain_string: String,
    tags_string: String
) extends Entity

object device
    extends TableSpec[device_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
