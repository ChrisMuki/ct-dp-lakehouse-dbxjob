package ct.dna.lakehouse.sr_raw.iemea_whsz_leitstand
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class leitstand_order_dosierteil_E(
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
    anteil_string: String,
    auftragsid_long: BoxedLong,
    material_id_long: BoxedLong,
    menge_kg_string: String,
    plant_string: String,
    waagennr_long: BoxedLong,
    ml_long: BoxedLong,
    material_code_string: String,
    istvorstufe_string: String,
    teilcharge_long: BoxedLong,
    reihenfolge_long: BoxedLong
) extends Entity

object leitstand_order_dosierteil
    extends TableSpec[leitstand_order_dosierteil_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
