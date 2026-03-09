package ct.dna.lakehouse.sr_raw.iamer_norz_enterprise
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class enterprise_orders_E(
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
    date_ordered_utc_string: String,
    compound_string: String,
    number_of_batches_long: BoxedLong,
    date_produced_utc_string: String,
    order_id_string: String,
    specification_id_string: String,
    plant_string: String,
    x_date_utc_string: String,
    order_name_string: String
) extends Entity

object enterprise_orders
    extends TableSpec[enterprise_orders_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
