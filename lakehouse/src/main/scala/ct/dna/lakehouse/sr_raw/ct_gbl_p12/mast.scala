package ct.dna.lakehouse.sr_raw.ct_gbl_p12
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class mast_E(
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
  matnr_string: String,
  annam_string: String,
  @Decimal(13, 3) losvn_decimal_13_3: BigDecimal,
  stlnr_string: String,
  @Decimal(13, 3) losbs_decimal_13_3: BigDecimal,
  werks_string: String,
  mandt_string: String,
  cslty_string: String,
  aenam_string: String,
  aedat_string: String,
  stlan_string: String,
  stlal_string: String,
  andat_string: String
) extends Entity

object mast extends TableSpec[mast_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
