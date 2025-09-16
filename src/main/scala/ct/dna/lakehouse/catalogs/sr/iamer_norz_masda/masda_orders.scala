package ct.dna.lakehouse.catalogs.sr.iamer_norz_masda

import ct.dna.utils.spark.entity.Entity
import ct.dna.utils.spark.entity.pk
import ct.dna.lakehouse.metastore.SRTableDef

case class masda_orders_Entity(
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
    _lh_ingest_warning_Json: String,
    unix_run_num_String: String,
    spec_name_String: String,
    mixer_num_Long: Long,
    mes_spec_num_Long: Long,
    run_start_time_utc_String: String,
    plant_String: String,
    initial_unix_run_num_String: String,
    run_start_time_String: String
) extends Entity

object masda_orders extends SRTableDef[masda_orders_Entity]
