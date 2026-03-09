package ct.dna.lakehouse.sr_raw.iemea_whsz_leitstand
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class leitstand_order_mischteil_schritte_E(
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
    fparam2_notizen_string: String,
    auftragsid_long: BoxedLong,
    fcode_long: BoxedLong,
    fparam2_long: BoxedLong,
    fparam1_notizen_string: String,
    fparam1_long: BoxedLong,
    plant_string: String,
    functionnummer_long: BoxedLong,
    function_name_string: String,
    fmode_long: BoxedLong,
    schrittnummer_long: BoxedLong,
    ml_long: BoxedLong
) extends Entity

object leitstand_order_mischteil_schritte
    extends TableSpec[leitstand_order_mischteil_schritte_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
