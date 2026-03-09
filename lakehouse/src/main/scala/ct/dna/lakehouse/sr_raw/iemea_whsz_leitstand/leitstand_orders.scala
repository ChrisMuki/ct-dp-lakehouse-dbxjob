package ct.dna.lakehouse.sr_raw.iemea_whsz_leitstand
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class leitstand_orders_E(
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
    ablagerfrist_string: String,
    knd_abnehmer_string: String,
    auftragsid_long: BoxedLong,
    fa_long: BoxedLong,
    end_time_utc_string: String,
    ian_long: BoxedLong,
    fa_netz_string: String,
    rk_dosierteil_long: BoxedLong,
    bezeichnung_string: String,
    chemiker_string: String,
    pool_text_string: String,
    knd_warenempfaenger_string: String,
    reihenfolge_string: String,
    create_time_string: String,
    start_time_string: String,
    pool_string: String,
    av_mischteil_schritte_long: BoxedLong,
    auftragstreu_string: String,
    lagerfrist_string: String,
    rk_long: BoxedLong,
    ian_unter_string: String,
    materialnr_string: String,
    kurzbezeichnung_long: BoxedLong,
    knd_auftragsnr_string: String,
    start_time_utc_string: String,
    ml_long: BoxedLong,
    blockfahrweise_string: String,
    rk_mbk_string: String,
    knd_ve_string: String,
    dichte_string: String,
    rezeptur_long: BoxedLong,
    artikelnr_string: String,
    ateil_string: String,
    reinigung_string: String,
    verkettung_string: String,
    walzenauftrag_string: String,
    knd_name_string: String,
    chargen_gewicht_soll_kg_string: String,
    chargen_soll_long: BoxedLong,
    end_time_string: String,
    shore_string: String,
    peroxid_string: String,
    status_string: String,
    lebensmittel_string: String,
    knd_artikelnr_string: String,
    butyl_string: String,
    kautschuk_string: String,
    baseauftragsid_long: BoxedLong,
    knd_bestellnr_string: String,
    knd_debitor_string: String,
    plant_string: String,
    mmz_string: String
) extends Entity

object leitstand_orders
    extends TableSpec[leitstand_orders_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
