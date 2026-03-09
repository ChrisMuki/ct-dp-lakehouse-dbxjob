package ct.dna.lakehouse.sr_raw.iemea_whsz_leitstand
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class leitstand_order_bbw_paletten_E(
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
    palette_string: String,
    auftragsid_long: BoxedLong,
    ml_long: BoxedLong,
    cvon_string: String,
    scantime_utc_string: String,
    cbis_string: String,
    gewicht_verbraucht_g_string: String,
    bezeichnung_string: String,
    gewicht_g_string: String,
    scanok_string: String,
    plant_string: String,
    rezeptur_string: String,
    ian_string: String,
    scantime_string: String,
    fa_string: String
) extends Entity

object leitstand_order_bbw_paletten
    extends TableSpec[leitstand_order_bbw_paletten_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
