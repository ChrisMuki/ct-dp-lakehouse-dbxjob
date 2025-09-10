package ct.dna.lakehouse.catalogs.sr.iamer_norz_enterprise

import ct.dna.lakehouse.metastore.Entity
import ct.dna.lakehouse.metastore.Entity.key
import ct.dna.lakehouse.metastore.SRTableDef

case class enterprise_orders_Entitiy(
    @key _mk_org_String: String,
    @key _mk_site_String: String,
    @key _mk_system_String: String,
    @key _mk_instance_String: String,
    @key _mk_partition_String: String,
    @key _mk_file_String: String,
    _mk_container_String: String,
    _mk_account_String: String,
    _mk_createdAt_String: String,
    @key _lh_id_in_message_Long: Long,
    compound_String: String,
    plant_String: String,
    x_date_utc_String: String,
    date_ordered_utc_String: String,
    order_id_String: String,
    order_name_String: String,
    date_produced_utc_String: String,
    number_of_batches_Long: Long,
    specification_id_String: String
) extends Entity

object enterprise_orders extends SRTableDef[enterprise_orders_Entitiy]
