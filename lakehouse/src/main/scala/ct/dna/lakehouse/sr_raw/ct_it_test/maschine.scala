package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class maschine_E(
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
    value_einheit_string: String,
    meta_source_version_string: String,
    meta_source_table_string: String,
    value_visible_boolean: BoxedBoolean,
    value_zn1modus_string: String,
    value_maschinea_string: String,
    meta_source_name_string: String,
    value_rowguid_string: String,
    value_maschnr_long: BoxedLong,
    value_zaehlcode_long: BoxedLong,
    value_zustgr_long: BoxedLong,
    value_permanent_boolean: BoxedBoolean,
    value_r_stzeit_long: BoxedLong,
    meta_ts_ms_long: BoxedLong,
    value_maschkurz_string: String,
    value_maschinfo4_string: String,
    meta_source_connector_string: String,
    value_maschpos_long: BoxedLong,
    value_maschinfo3_string: String,
    meta_source_commit_lsn_string: String,
    meta_source_ts_ms_long: BoxedLong,
    meta_op_string: String,
    value_maschine_string: String,
    value_zn2modus_string: String,
    meta_source_snapshot_string: String,
    value_maschinfo2_string: String,
    value_dauerjezyklus_long: BoxedLong,
    value_maschgr_long: BoxedLong,
    value_maschend_string: String,
    value_einheitbeize_boolean: BoxedBoolean,
    value_artikelnrformat_string: String,
    value_teiligkeit_string: String,
    value_einhart_string: String,
    value_maschkenn_string: String,
    meta_source_schema_string: String,
    value_verarbeitungspriorisierung_long: BoxedLong,
    value_maschinfo1_string: String,
    value_bernr_long: BoxedLong,
    meta_source_db_string: String,
    key_maschnr_long: BoxedLong,
    meta_source_ts_us_long: BoxedLong,
    meta_source_ts_ns_long: BoxedLong
) extends Entity

object maschine
    extends TableSpec[maschine_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
