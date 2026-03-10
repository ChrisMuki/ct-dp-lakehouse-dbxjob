package ct.dna.lakehouse.sr_raw.ct_gbl_epp
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class knmt_E(
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
  ernam_string: String,
  sortl_string: String,
  rdprf_string: String,
  j_1btxsdc_string: String,
  lprio_string: String,
  kdmat_string: String,
  megru_string: String,
  @Decimal(5, 0) umvkz_t_decimal_5_0: BigDecimal,
  kztlf_string: String,
  vrkme_t_string: String,
  kunnr_string: String,
  vkorg_string: String,
  matnr_string: String,
  vwpos_string: String,
  werks_string: String,
  vtweg_string: String,
  chspl_string: String,
  postx_string: String,
  mandt_string: String,
  guid_binary: Array[Byte],
  @Decimal(1, 0) antlf_decimal_1_0: BigDecimal,
  meins_string: String,
  @Decimal(3, 1) uebto_decimal_3_1: BigDecimal,
  @Decimal(5, 0) umvkn_t_decimal_5_0: BigDecimal,
  @Decimal(13, 3) minlf_decimal_13_3: BigDecimal,
  uebtk_string: String,
  @Decimal(3, 1) untto_decimal_3_1: BigDecimal,
  erdat_string: String
) extends Entity

object knmt extends TableSpec[knmt_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
