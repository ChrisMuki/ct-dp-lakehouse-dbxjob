package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class milueft_E(
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
    meta_ts_ms_long: BoxedLong,
    value_spezen_long: BoxedLong,
    value_zusatz3_long: BoxedLong,
    meta_source_version_string: String,
    value_rotorabstand_long: BoxedLong,
    value_absrelz_string: String,
    meta_source_change_lsn_string: String,
    value_absrelh_string: String,
    value_drehz2_long: BoxedLong,
    value_ueberrollungen_long: BoxedLong,
    value_sstellg_long: BoxedLong,
    meta_source_schema_string: String,
    meta_source_db_string: String,
    value_zusatz1_long: BoxedLong,
    value_absrelt_string: String,
    value_sdrkstufe_string: String,
    value_verkn2_string: String,
    meta_source_ts_us_long: BoxedLong,
    value_verkn1_string: String,
    value_name_string: String,
    meta_source_table_string: String,
    value_datum_long: BoxedLong,
    value_drehz1_long: BoxedLong,
    value_zusatz2_long: BoxedLong,
    value_lueftart_string: String,
    value_drehztol_long: BoxedLong,
    value_temperatur_long: BoxedLong,
    meta_source_connector_string: String,
    value_absrele_string: String,
    value_leistungsoll_long: BoxedLong,
    value_heathistory_long: BoxedLong,
    value_solltemp_long: BoxedLong,
    meta_source_name_string: String,
    value_arbeitsg_string: String,
    meta_source_commit_lsn_string: String,
    value_lueftzeit_long: BoxedLong,
    meta_source_ts_ms_long: BoxedLong,
    meta_source_event_serial_no_long: BoxedLong,
    meta_op_string: String,
    meta_source_snapshot_string: String,
    meta_source_ts_ns_long: BoxedLong,
    value_rowguid_string: String,
    key_twerkst_string: String,
    key_lueftnr_long: BoxedLong,
    value_zeit_long: BoxedLong,
    value_verknart_string: String
) extends Entity

object milueft
    extends TableSpec[milueft_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
