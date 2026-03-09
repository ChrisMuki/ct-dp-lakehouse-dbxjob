package ct.dna.lakehouse.sr_raw.iemea_whsz_leitstand
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class leitstand_order_paletten_E(
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
    basepalettenid_string: String,
    charge1_gewicht_g_string: String,
    wmobjectid_string: String,
    auftragsid_long: BoxedLong,
    charge3_nr_string: String,
    dtm_string: String,
    charge3_gewicht_g_string: String,
    ablagetemperatur_string: String,
    plant_string: String,
    charge2_nr_string: String,
    palette_string: String,
    dtm_utc_string: String,
    ml_long: BoxedLong,
    charge4_nr_string: String,
    charge4_gewicht_g_string: String,
    charge6_nr_string: String,
    charge2_gewicht_g_string: String,
    storein_lfname_string: String,
    charge1_nr_string: String,
    charge6_gewicht_g_string: String,
    charge5_gewicht_g_string: String,
    gewicht_g_string: String,
    charge5_nr_string: String,
    tara_g_string: String,
    bookingtime_string: String
) extends Entity

object leitstand_order_paletten
    extends TableSpec[leitstand_order_paletten_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
