package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class debezium_signal_E(
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
    key_id_string: String,
    value_type_string: String,
    value_data_string: String,
    meta_ts_ms_long: BoxedLong,
    meta_source_table_string: String,
    meta_source_schema_string: String,
    meta_source_connector_string: String,
    meta_source_version_string: String,
    meta_source_ts_ms_long: BoxedLong,
    meta_source_name_string: String,
    meta_op_string: String,
    meta_source_ts_us_long: BoxedLong,
    meta_source_db_string: String,
    meta_source_commit_lsn_string: String,
    meta_source_ts_ns_long: BoxedLong,
    meta_source_snapshot_string: String
) extends Entity

object debezium_signal
    extends TableSpec[debezium_signal_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
