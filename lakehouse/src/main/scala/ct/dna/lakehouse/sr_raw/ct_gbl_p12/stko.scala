package ct.dna.lakehouse.sr_raw.ct_gbl_p12
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class stko_E(
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
  annam_string: String,
  stktx_string: String,
  dvnam_string: String,
  mandt_string: String,
  zzaenr_string: String,
  aehlp_string: String,
  @Decimal(13, 3) bmeng_decimal_13_3: BigDecimal,
  stlst_string: String,
  zzkindex_string: String,
  techv_string: String,
  zzmeins_string: String,
  guidx_binary: Array[Byte],
  dvdat_string: String,
  stlty_string: String,
  cadkz_string: String,
  zzeco_string: String,
  zztemp_string: String,
  vgkzl_string: String,
  alekz_string: String,
  zzkunde_string: String,
  datuv_string: String,
  stlnr_string: String,
  stlal_string: String,
  andat_string: String,
  @Decimal(13, 3) zzgewicht_decimal_13_3: BigDecimal,
  zserst_string: String,
  zzfertv_string: String,
  zznorm_string: String,
  zzstand_string: String,
  stkoz_string: String,
  lkenz_string: String,
  bmein_string: String,
  ecn_to_rkey_string: String,
  zzzeit_string: String,
  zzindex_string: String,
  valid_to_string: String,
  zzkennz_string: String,
  aenam_string: String,
  ecn_to_string: String,
  loekz_string: String,
  aedat_string: String,
  ltxsp_string: String,
  valid_to_rkey_string: String,
  labor_string: String,
  aennr_string: String,
  wrkan_string: String
) extends Entity

object stko extends TableSpec[stko_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
