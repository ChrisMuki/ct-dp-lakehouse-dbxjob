package ct.dna.lakehouse.sr_raw.iemea_whsz_leitstand
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
    motorleistung_kw_long: BoxedLong,
    abdichtung_ws_rechts_long: BoxedLong,
    status1_long: BoxedLong,
    drehmoment_nm_long: BoxedLong,
    auftragsid_long: BoxedLong,
    larger_ms_rechts_long: BoxedLong,
    larger_ws_links_long: BoxedLong,
    temperatur_c_double: BoxedDouble,
    energie_kwh_double: BoxedDouble,
    tempzone3_long: BoxedLong,
    fnc1_long: BoxedLong,
    charge_long: BoxedLong,
    motorstrom_amps_long: BoxedLong,
    abrufwaagen_long: BoxedLong,
    time_utc_string: String,
    abdichtung_ms_links_long: BoxedLong,
    ml_long: BoxedLong,
    drehzahl_double: BoxedDouble,
    larger_ws_rechts_long: BoxedLong,
    abdichtung_ms_rechts_long: BoxedLong,
    fnc3_long: BoxedLong,
    stempeldruck_bar_double: BoxedDouble,
    stempelweg_mm_long: BoxedLong,
    tempzone1_long: BoxedLong,
    larger_ms_links_long: BoxedLong,
    rezlz_long: BoxedLong,
    leistung_kw_double: BoxedDouble,
    tempzone2_long: BoxedLong,
    zeit_string: String,
    fnc2_long: BoxedLong,
    ueberrollung_double: BoxedDouble,
    abdichtung_ws_links_long: BoxedLong,
    status2_long: BoxedLong,
    plant_string: String
) extends Entity

object leitstand_rubber_mixer_ts
    extends TableSpec[leitstand_rubber_mixer_ts_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
