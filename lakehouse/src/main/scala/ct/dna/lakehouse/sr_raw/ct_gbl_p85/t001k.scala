package ct.dna.lakehouse.sr_raw.ct_gbl_p85
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class t001k_E(
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
  xbkng_string: String,
  _fmp_prsfr_string: String,
  _fmp_prfrgr_string: String,
  mlccs_string: String,
  mandt_string: String,
  prsfr_string: String,
  mlbwv_string: String,
  xefre_string: String,
  xewrx_string: String,
  wbpro_string: String,
  uprof_string: String,
  xlbpd_string: String,
  xvkbw_string: String,
  bukrs_string: String,
  bwmod_string: String,
  mlast_string: String,
  x2fdo_string: String,
  @Decimal(5, 2) bdifp_decimal_5_2: BigDecimal,
  bwkey_string: String,
  efrej_string: String,
  mlbwa_string: String,
  mlasv_string: String,
  erklaerkom_string: String
) extends Entity

object t001k extends TableSpec[t001k_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
