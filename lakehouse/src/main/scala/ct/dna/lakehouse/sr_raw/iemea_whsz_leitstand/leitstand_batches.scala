package ct.dna.lakehouse.sr_raw.iemea_whsz_leitstand
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class leitstand_batches_E(
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
    start_time_string: String,
    sattel_auf_s_string: String,
    energie_kwh_string: String,
    start_time_utc_string: String,
    auftragsid_long: BoxedLong,
    ml_long: BoxedLong,
    wartezeit_s_string: String,
    end_time_string: String,
    oelmenge_kg_string: String,
    gewicht_ist_kg_string: String,
    charge_long: BoxedLong,
    plant_string: String,
    gewicht_soll_kg_string: String,
    end_time_utc_string: String,
    mischzeit_s_string: String,
    mischzeit_stempel_s_string: String
) extends Entity

object leitstand_batches
    extends TableSpec[leitstand_batches_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
