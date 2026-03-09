package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class leitstand_rubber_mixer_ts_E(
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
    value_record_timestamp_string: String,
    value_abrufwaagen_long: BoxedLong,
    meta_source_connector_string: String,
    value_fnc1_long: BoxedLong,
    value_status1_long: BoxedLong,
    meta_source_ts_ms_long: BoxedLong,
    value_abdichtung_ws_rechts_long: BoxedLong,
    value_rezlz_long: BoxedLong,
    value_motorstrom_amps_long: BoxedLong,
    value_auftragsid_long: BoxedLong,
    meta_ts_ms_long: BoxedLong,
    value_tempzone2_long: BoxedLong,
    value_larger_ms_links_long: BoxedLong,
    value_stempelweg_mm_long: BoxedLong,
    value_time_utc_string: String,
    key_record_id_long: BoxedLong,
    value_temperatur_c_string: String,
    meta_source_version_string: String,
    value_abdichtung_ws_links_long: BoxedLong,
    meta_source_table_string: String,
    value_plant_string: String,
    value_abdichtung_ms_links_long: BoxedLong,
    value_drehmoment_nm_long: BoxedLong,
    value_drehzahl_string: String,
    meta_source_name_string: String,
    value_larger_ms_rechts_long: BoxedLong,
    value_ml_string: String,
    meta_op_string: String,
    meta_source_snapshot_string: String,
    value_charge_long: BoxedLong,
    value_larger_ws_links_long: BoxedLong,
    value_larger_ws_rechts_long: BoxedLong,
    value_ueberrollung_string: String,
    value_fnc2_long: BoxedLong,
    value_stempeldruck_bar_string: String,
    value_energie_kwh_string: String,
    meta_source_lsn_long: BoxedLong,
    value_zeit_long: BoxedLong,
    value_abdichtung_ms_rechts_long: BoxedLong,
    value_status2_long: BoxedLong,
    value_tempzone3_long: BoxedLong,
    value_leistung_kw_string: String,
    meta_source_schema_string: String,
    meta_source_txid_long: BoxedLong,
    value_fnc4_long: BoxedLong,
    value_tempzone1_long: BoxedLong,
    value_fnc3_long: BoxedLong,
    meta_source_db_string: String,
    meta_source_sequence_string: String,
    value_motorleistung_kw_long: BoxedLong,
    meta_source_ts_us_long: BoxedLong,
    meta_source_ts_ns_long: BoxedLong
) extends Entity

object leitstand_rubber_mixer_ts
    extends TableSpec[leitstand_rubber_mixer_ts_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
