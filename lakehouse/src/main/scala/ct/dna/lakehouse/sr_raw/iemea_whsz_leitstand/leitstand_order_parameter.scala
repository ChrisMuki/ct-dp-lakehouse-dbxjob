package ct.dna.lakehouse.sr_raw.iemea_whsz_leitstand
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class leitstand_order_parameter_E(
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
    ext_dse_temperatur3_c_long: BoxedLong,
    grnztemp_c_long: BoxedLong,
    ext_dse_temperatur4_c_long: BoxedLong,
    auftragsid_long: BoxedLong,
    w2temp1_c_long: BoxedLong,
    ext_dse_drucksoll_bar_long: BoxedLong,
    kz1temp_c_long: BoxedLong,
    w1temp1_c_long: BoxedLong,
    ext_dse_temperatur1_c_long: BoxedLong,
    w2temp2_c_long: BoxedLong,
    ext_dse_temperatur2_c_long: BoxedLong,
    kz3temp_c_long: BoxedLong,
    ext_dse_drehzahlsoll_long: BoxedLong,
    ext_dse_drehz_max_long: BoxedLong,
    ext_dse_drehz_medium_long: BoxedLong,
    w1temp2_c_long: BoxedLong,
    plant_string: String,
    fuellfaktor_double: BoxedDouble,
    ml_long: BoxedLong,
    ext_dse_felldicke_long: BoxedLong,
    kz2temp_c_long: BoxedLong
) extends Entity

object leitstand_order_parameter
    extends TableSpec[leitstand_order_parameter_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
