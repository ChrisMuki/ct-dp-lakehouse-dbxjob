package ct.dna.lakehouse.sr_raw.ct_gbl_p43
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class mkal_E(
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
  @Decimal(13, 3) bstmi_decimal_13_3: BigDecimal,
  @Decimal(3, 0) ewahr_decimal_3_0: BigDecimal,
  plnnr_string: String,
  elpro_string: String,
  matko_string: String,
  sobsl_string: String,
  @Decimal(13, 3) bstma_decimal_13_3: BigDecimal,
  bdatu_string: String,
  plnnm_string: String,
  rgekz_string: String,
  prfg_f_string: String,
  text1_string: String,
  pltym_string: String,
  prvbe_string: String,
  plnty_string: String,
  pltyg_string: String,
  alnam_string: String,
  mdv01_string: String,
  ucmat_string: String,
  stlan_string: String,
  stlal_string: String,
  adatu_string: String,
  prfg_g_string: String,
  csplt_string: String,
  plnng_string: String,
  alnal_string: String,
  werks_string: String,
  mandt_string: String,
  alnag_string: String,
  serkz_string: String,
  prfg_s_string: String,
  verid_string: String,
  beskz_string: String,
  alort_string: String,
  mdv02_string: String,
  prdat_string: String,
  @Decimal(13, 3) losgr_decimal_13_3: BigDecimal,
  verto_string: String,
  prfg_r_string: String,
  mksp_string: String,
  ppeguid_binary: Array[Byte]
) extends Entity

object mkal extends TableSpec[mkal_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
