package ct.dna.lakehouse.sr_raw.iemea_whsz_leitstand
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class leitstand_boms_E(
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
    gewicht_kowa_kg_string: String,
    lot_string: String,
    silonr_long: BoxedLong,
    gewicht_tara_kg_string: String,
    auftragsid_long: BoxedLong,
    scanid_string: String,
    zeit_entleering_s_string: String,
    verweignung_extern_string: String,
    material_id_long: BoxedLong,
    zeit_fertig_utc_string: String,
    gewicht_ist_kg_string: String,
    plc_status_long: BoxedLong,
    zeit_dosierung_s_string: String,
    charge_long: BoxedLong,
    plant_string: String,
    material_text_string: String,
    zeit_fertig_string: String,
    plc_id_long: BoxedLong,
    waagennr_long: BoxedLong,
    ml_long: BoxedLong,
    gewicht_soll_kg_string: String,
    material_code_string: String
) extends Entity

object leitstand_boms
    extends TableSpec[leitstand_boms_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
