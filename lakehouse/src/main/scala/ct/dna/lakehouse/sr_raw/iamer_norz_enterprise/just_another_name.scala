package ct.dna.lakehouse.sr_raw.iamer_norz_enterprise
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class just_another_name_E(
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
    sample_id_string: String,
    retest_number_long: BoxedLong,
    batch_number_long: BoxedLong,
    sample_name_string: String,
    status_symbol_string: String,
    batch_id_string: String,
    batch_result_time_utc_string: String,
    order_id_string: String,
    plant_string: String,
    result_id_string: String,
    instrument_id_string: String
) extends Entity

object just_another_name
    extends TableSpec[just_another_name_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
