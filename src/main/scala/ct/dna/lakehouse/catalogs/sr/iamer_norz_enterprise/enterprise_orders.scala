package ct.dna.lakehouse.catalogs.sr.iamer_norz_enterprise

import ct.dna.utils.spark.entity.Entity
import ct.dna.utils.spark.entity.pk
import ct.dna.lakehouse.metastore.SRTableDef

case class enterprise_orders_Entitiy(
    @pk _mk_org_String: String,
    @pk _mk_site_String: String,
    @pk _mk_system_String: String,
    @pk _mk_instance_String: String,
    @pk _mk_partition_String: String,
    @pk _mk_file_String: String,
    _mk_container_String: String,
    _mk_account_String: String,
    _mk_createdAt_String: String,
    @pk _lh_id_in_message_Long: Long,
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
