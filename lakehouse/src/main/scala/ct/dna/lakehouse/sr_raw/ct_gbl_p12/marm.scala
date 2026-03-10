package ct.dna.lakehouse.sr_raw.ct_gbl_p12
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class marm_E(
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
  bflme_marm_string: String,
  mesrt_string: String,
  gewei_string: String,
  ty2tq_string: String,
  voleh_string: String,
  meinh_string: String,
  @Decimal(3, 0) nest_ftr_decimal_3_0: BigDecimal,
  ean11_string: String,
  msehi_string: String,
  @Decimal(13, 3) volum_decimal_13_3: BigDecimal,
  @Decimal(15, 3) capause_decimal_15_3: BigDecimal,
  @Decimal(13, 3) brgew_decimal_13_3: BigDecimal,
  kzwso_string: String,
  xfhdw_string: String,
  @Decimal(13, 3) laeng_decimal_13_3: BigDecimal,
  @Decimal(5, 0) umren_decimal_5_0: BigDecimal,
  @Decimal(13, 3) breit_decimal_13_3: BigDecimal,
  mesub_string: String,
  @Decimal(13, 3) hoehe_decimal_13_3: BigDecimal,
  pcbut_string: String,
  max_stack_int: BoxedInt,
  xbeww_string: String,
  atinn_string: String,
  mandt_string: String,
  gtin_variant_string: String,
  @Decimal(5, 0) umrez_decimal_5_0: BigDecimal,
  eannr_string: String,
  meabm_string: String,
  numtp_string: String
) extends Entity

object marm extends TableSpec[marm_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
