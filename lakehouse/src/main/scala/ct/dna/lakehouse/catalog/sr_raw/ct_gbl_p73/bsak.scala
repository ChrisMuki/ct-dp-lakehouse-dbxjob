// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_p73

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_bsak(
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
    bukrs_string: String,
    maber_string: String,
    augbl_string: String,
    zterm_string: String,
    egrup_string: String,
    rstgr_string: String,
    @Decimal(3, 0) zbd1t_decimal_3_0: BigDecimal,
    @Decimal(13, 2) penlc2_decimal_13_2: BigDecimal,
    lzbkz_string: String,
    rebzt_string: String,
    fkber_string: String,
    cpudt_string: String,
    lnran_string: String,
    @Decimal(13, 2) skfbt_decimal_13_2: BigDecimal,
    zfbdt_string: String,
    saknr_string: String,
    xarch_string: String,
    belnr_string: String,
    mwsk1_string: String,
    vertn_string: String,
    pendays_int: BoxedInt,
    filkd_string: String,
    prctr_string: String,
    @Decimal(13, 2) dmb33_decimal_13_2: BigDecimal,
    dtws1_string: String,
    kontt_string: String,
    zbfix_string: String,
    kontl_string: String,
    kblnr_string: String,
    zlsch_string: String,
    mschl_string: String,
    @Decimal(13, 2) mwst3_decimal_13_2: BigDecimal,
    xegdr_string: String,
    xblnr_string: String,
    @Decimal(13, 2) dmbtr_decimal_13_2: BigDecimal,
    @Decimal(13, 2) dmb32_decimal_13_2: BigDecimal,
    @Decimal(13, 2) dmbt1_decimal_13_2: BigDecimal,
    @Decimal(3, 0) zbd2t_decimal_3_0: BigDecimal,
    fkont_string: String,
    @Decimal(13, 2) dmbe3_decimal_13_2: BigDecimal,
    @Decimal(13, 2) dmbt3_decimal_13_2: BigDecimal,
    samnr_string: String,
    aufnr_string: String,
    bstat_string: String,
    buzei_string: String,
    @Decimal(13, 2) bdif3_decimal_13_2: BigDecimal,
    @Decimal(13, 2) wrbt3_decimal_13_2: BigDecimal,
    egbld_string: String,
    qsznr_string: String,
    @Decimal(13, 2) dmbe2_decimal_13_2: BigDecimal,
    ebeln_string: String,
    mwsk2_string: String,
    nplnr_string: String,
    diekz_string: String,
    sgtxt_string: String,
    xstov_string: String,
    uzawe_string: String,
    propmano_string: String,
    kidno_string: String,
    secco_string: String,
    imkey_string: String,
    @Decimal(5, 3) zbd1p_decimal_5_3: BigDecimal,
    projk_string: String,
    @Decimal(13, 2) wrbtr_decimal_13_2: BigDecimal,
    vbewa_string: String,
    stceg_string: String,
    pycur_string: String,
    anln1_string: String,
    aplzl_string: String,
    qsskz_string: String,
    inwardno_hd_string: String,
    @Decimal(13, 2) dmb21_decimal_13_2: BigDecimal,
    xref3_string: String,
    @Decimal(13, 2) bdif2_decimal_13_2: BigDecimal,
    geber_string: String,
    @Decimal(13, 2) kzbtr_decimal_13_2: BigDecimal,
    intreno_string: String,
    ebelp_string: String,
    rebzj_string: String,
    @Decimal(13, 2) sknto_decimal_13_2: BigDecimal,
    xcpdd_string: String,
    anfbj_string: String,
    dtws3_string: String,
    @Decimal(13, 2) wmwst_decimal_13_2: BigDecimal,
    @Decimal(13, 2) mwsts_decimal_13_2: BigDecimal,
    mandt_string: String,
    mansp_string: String,
    xragl_string: String,
    @Decimal(13, 2) ppdif3_decimal_13_2: BigDecimal,
    hkont_string: String,
    empfb_string: String,
    zinkz_string: String,
    vname_string: String,
    @Decimal(13, 2) qsshb_decimal_13_2: BigDecimal,
    vbund_string: String,
    @Decimal(13, 2) pswbt_decimal_13_2: BigDecimal,
    blart_string: String,
    @Decimal(13, 2) penfc_decimal_13_2: BigDecimal,
    @Decimal(3, 0) zbd3t_decimal_3_0: BigDecimal,
    btype_string: String,
    hktid_string: String,
    xanet_string: String,
    @Decimal(13, 2) penlc3_decimal_13_2: BigDecimal,
    xref2_string: String,
    kblpos_string: String,
    aufpl_string: String,
    projn_string: String,
    @Decimal(13, 2) ppdif2_decimal_13_2: BigDecimal,
    @Decimal(13, 2) wskto_decimal_13_2: BigDecimal,
    madat_string: String,
    zolld_string: String,
    umskz_string: String,
    @Decimal(13, 2) qbshb_decimal_13_2: BigDecimal,
    @Decimal(13, 2) sknt2_decimal_13_2: BigDecimal,
    xref1_string: String,
    @Decimal(13, 2) qsfbt_decimal_13_2: BigDecimal,
    zollt_string: String,
    gjahr_string: String,
    buzid_string: String,
    @Decimal(13, 2) dmbt2_decimal_13_2: BigDecimal,
    xnetb_string: String,
    eglld_string: String,
    anln2_string: String,
    mwskz_string: String,
    landl_string: String,
    vertt_string: String,
    @Decimal(13, 2) pyamt_decimal_13_2: BigDecimal,
    @Decimal(13, 2) sknt3_decimal_13_2: BigDecimal,
    augdt_string: String,
    budget_pd_string: String,
    bschl_string: String,
    bvtyp_string: String,
    @Decimal(5, 3) zbd2p_decimal_5_3: BigDecimal,
    lotkz_string: String,
    pswsl_string: String,
    xpypr_string: String,
    fipos_string: String,
    uebgdat_string: String,
    xinve_string: String,
    anfbu_string: String,
    xnegp_string: String,
    @Decimal(13, 2) dmb31_decimal_13_2: BigDecimal,
    dtws4_string: String,
    xzahl_string: String,
    @Decimal(13, 2) wrbt2_decimal_13_2: BigDecimal,
    penrc_string: String,
    rebzz_string: String,
    auggj_string: String,
    monat_string: String,
    @Decimal(13, 2) bdiff_decimal_13_2: BigDecimal,
    zlspr_string: String,
    umsks_string: String,
    lifnr_string: String,
    @Decimal(13, 2) mwst2_decimal_13_2: BigDecimal,
    srtype_string: String,
    mwsk3_string: String,
    xesrd_string: String,
    dabrz_string: String,
    hbkid_string: String,
    rebzg_string: String,
    @Decimal(13, 2) ppdiff_decimal_13_2: BigDecimal,
    shkzg_string: String,
    anfbn_string: String,
    zuonr_string: String,
    zumsk_string: String,
    bldat_string: String,
    gsber_string: String,
    @Decimal(13, 2) dmb23_decimal_13_2: BigDecimal,
    dtws2_string: String,
    budat_string: String,
    grant_nbr_string: String,
    kostl_string: String,
    fistl_string: String,
    waers_string: String,
    zekkn_string: String,
    @Decimal(13, 2) dmb22_decimal_13_2: BigDecimal,
    @Decimal(13, 2) wrbt1_decimal_13_2: BigDecimal,
    pprct_string: String,
    @Decimal(13, 2) penlc1_decimal_13_2: BigDecimal,
    gmvkz_string: String,
    bupla_string: String,
    manst_string: String,
    inwarddt_hd_string: String
) extends Entity

object bsak extends TableSpec[E_bsak](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_bsak(prefix: String) extends ColumnWithNameAccessor {
  val _mk_org: ColumnWithName = ColumnWithName(prefix + "_mk_org")
  val _mk_site: ColumnWithName = ColumnWithName(prefix + "_mk_site")
  val _mk_system: ColumnWithName = ColumnWithName(prefix + "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix + "_mk_instance")
  val _mk_partition: ColumnWithName = ColumnWithName(prefix + "_mk_partition")
  val _mk_file: ColumnWithName = ColumnWithName(prefix + "_mk_file")
  val _mk_container: ColumnWithName = ColumnWithName(prefix + "_mk_container")
  val _mk_account: ColumnWithName = ColumnWithName(prefix + "_mk_account")
  val _mk_created_at: ColumnWithName = ColumnWithName(prefix + "_mk_created_at")
  val _lh_id_in_message: ColumnWithName = ColumnWithName(prefix + "_lh_id_in_message")
  val _lh_ingest_warning: ColumnWithName = ColumnWithName(prefix + "_lh_ingest_warning")
  val bukrs_string: ColumnWithName = ColumnWithName(prefix + "bukrs_string")
  val maber_string: ColumnWithName = ColumnWithName(prefix + "maber_string")
  val augbl_string: ColumnWithName = ColumnWithName(prefix + "augbl_string")
  val zterm_string: ColumnWithName = ColumnWithName(prefix + "zterm_string")
  val egrup_string: ColumnWithName = ColumnWithName(prefix + "egrup_string")
  val rstgr_string: ColumnWithName = ColumnWithName(prefix + "rstgr_string")
  val zbd1t_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "zbd1t_decimal_3_0")
  val penlc2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "penlc2_decimal_13_2")
  val lzbkz_string: ColumnWithName = ColumnWithName(prefix + "lzbkz_string")
  val rebzt_string: ColumnWithName = ColumnWithName(prefix + "rebzt_string")
  val fkber_string: ColumnWithName = ColumnWithName(prefix + "fkber_string")
  val cpudt_string: ColumnWithName = ColumnWithName(prefix + "cpudt_string")
  val lnran_string: ColumnWithName = ColumnWithName(prefix + "lnran_string")
  val skfbt_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "skfbt_decimal_13_2")
  val zfbdt_string: ColumnWithName = ColumnWithName(prefix + "zfbdt_string")
  val saknr_string: ColumnWithName = ColumnWithName(prefix + "saknr_string")
  val xarch_string: ColumnWithName = ColumnWithName(prefix + "xarch_string")
  val belnr_string: ColumnWithName = ColumnWithName(prefix + "belnr_string")
  val mwsk1_string: ColumnWithName = ColumnWithName(prefix + "mwsk1_string")
  val vertn_string: ColumnWithName = ColumnWithName(prefix + "vertn_string")
  val pendays_int: ColumnWithName = ColumnWithName(prefix + "pendays_int")
  val filkd_string: ColumnWithName = ColumnWithName(prefix + "filkd_string")
  val prctr_string: ColumnWithName = ColumnWithName(prefix + "prctr_string")
  val dmb33_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmb33_decimal_13_2")
  val dtws1_string: ColumnWithName = ColumnWithName(prefix + "dtws1_string")
  val kontt_string: ColumnWithName = ColumnWithName(prefix + "kontt_string")
  val zbfix_string: ColumnWithName = ColumnWithName(prefix + "zbfix_string")
  val kontl_string: ColumnWithName = ColumnWithName(prefix + "kontl_string")
  val kblnr_string: ColumnWithName = ColumnWithName(prefix + "kblnr_string")
  val zlsch_string: ColumnWithName = ColumnWithName(prefix + "zlsch_string")
  val mschl_string: ColumnWithName = ColumnWithName(prefix + "mschl_string")
  val mwst3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "mwst3_decimal_13_2")
  val xegdr_string: ColumnWithName = ColumnWithName(prefix + "xegdr_string")
  val xblnr_string: ColumnWithName = ColumnWithName(prefix + "xblnr_string")
  val dmbtr_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmbtr_decimal_13_2")
  val dmb32_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmb32_decimal_13_2")
  val dmbt1_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmbt1_decimal_13_2")
  val zbd2t_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "zbd2t_decimal_3_0")
  val fkont_string: ColumnWithName = ColumnWithName(prefix + "fkont_string")
  val dmbe3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmbe3_decimal_13_2")
  val dmbt3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmbt3_decimal_13_2")
  val samnr_string: ColumnWithName = ColumnWithName(prefix + "samnr_string")
  val aufnr_string: ColumnWithName = ColumnWithName(prefix + "aufnr_string")
  val bstat_string: ColumnWithName = ColumnWithName(prefix + "bstat_string")
  val buzei_string: ColumnWithName = ColumnWithName(prefix + "buzei_string")
  val bdif3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "bdif3_decimal_13_2")
  val wrbt3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "wrbt3_decimal_13_2")
  val egbld_string: ColumnWithName = ColumnWithName(prefix + "egbld_string")
  val qsznr_string: ColumnWithName = ColumnWithName(prefix + "qsznr_string")
  val dmbe2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmbe2_decimal_13_2")
  val ebeln_string: ColumnWithName = ColumnWithName(prefix + "ebeln_string")
  val mwsk2_string: ColumnWithName = ColumnWithName(prefix + "mwsk2_string")
  val nplnr_string: ColumnWithName = ColumnWithName(prefix + "nplnr_string")
  val diekz_string: ColumnWithName = ColumnWithName(prefix + "diekz_string")
  val sgtxt_string: ColumnWithName = ColumnWithName(prefix + "sgtxt_string")
  val xstov_string: ColumnWithName = ColumnWithName(prefix + "xstov_string")
  val uzawe_string: ColumnWithName = ColumnWithName(prefix + "uzawe_string")
  val propmano_string: ColumnWithName = ColumnWithName(prefix + "propmano_string")
  val kidno_string: ColumnWithName = ColumnWithName(prefix + "kidno_string")
  val secco_string: ColumnWithName = ColumnWithName(prefix + "secco_string")
  val imkey_string: ColumnWithName = ColumnWithName(prefix + "imkey_string")
  val zbd1p_decimal_5_3: ColumnWithName = ColumnWithName(prefix + "zbd1p_decimal_5_3")
  val projk_string: ColumnWithName = ColumnWithName(prefix + "projk_string")
  val wrbtr_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "wrbtr_decimal_13_2")
  val vbewa_string: ColumnWithName = ColumnWithName(prefix + "vbewa_string")
  val stceg_string: ColumnWithName = ColumnWithName(prefix + "stceg_string")
  val pycur_string: ColumnWithName = ColumnWithName(prefix + "pycur_string")
  val anln1_string: ColumnWithName = ColumnWithName(prefix + "anln1_string")
  val aplzl_string: ColumnWithName = ColumnWithName(prefix + "aplzl_string")
  val qsskz_string: ColumnWithName = ColumnWithName(prefix + "qsskz_string")
  val inwardno_hd_string: ColumnWithName = ColumnWithName(prefix + "inwardno_hd_string")
  val dmb21_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmb21_decimal_13_2")
  val xref3_string: ColumnWithName = ColumnWithName(prefix + "xref3_string")
  val bdif2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "bdif2_decimal_13_2")
  val geber_string: ColumnWithName = ColumnWithName(prefix + "geber_string")
  val kzbtr_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzbtr_decimal_13_2")
  val intreno_string: ColumnWithName = ColumnWithName(prefix + "intreno_string")
  val ebelp_string: ColumnWithName = ColumnWithName(prefix + "ebelp_string")
  val rebzj_string: ColumnWithName = ColumnWithName(prefix + "rebzj_string")
  val sknto_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "sknto_decimal_13_2")
  val xcpdd_string: ColumnWithName = ColumnWithName(prefix + "xcpdd_string")
  val anfbj_string: ColumnWithName = ColumnWithName(prefix + "anfbj_string")
  val dtws3_string: ColumnWithName = ColumnWithName(prefix + "dtws3_string")
  val wmwst_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "wmwst_decimal_13_2")
  val mwsts_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "mwsts_decimal_13_2")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val mansp_string: ColumnWithName = ColumnWithName(prefix + "mansp_string")
  val xragl_string: ColumnWithName = ColumnWithName(prefix + "xragl_string")
  val ppdif3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "ppdif3_decimal_13_2")
  val hkont_string: ColumnWithName = ColumnWithName(prefix + "hkont_string")
  val empfb_string: ColumnWithName = ColumnWithName(prefix + "empfb_string")
  val zinkz_string: ColumnWithName = ColumnWithName(prefix + "zinkz_string")
  val vname_string: ColumnWithName = ColumnWithName(prefix + "vname_string")
  val qsshb_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "qsshb_decimal_13_2")
  val vbund_string: ColumnWithName = ColumnWithName(prefix + "vbund_string")
  val pswbt_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "pswbt_decimal_13_2")
  val blart_string: ColumnWithName = ColumnWithName(prefix + "blart_string")
  val penfc_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "penfc_decimal_13_2")
  val zbd3t_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "zbd3t_decimal_3_0")
  val btype_string: ColumnWithName = ColumnWithName(prefix + "btype_string")
  val hktid_string: ColumnWithName = ColumnWithName(prefix + "hktid_string")
  val xanet_string: ColumnWithName = ColumnWithName(prefix + "xanet_string")
  val penlc3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "penlc3_decimal_13_2")
  val xref2_string: ColumnWithName = ColumnWithName(prefix + "xref2_string")
  val kblpos_string: ColumnWithName = ColumnWithName(prefix + "kblpos_string")
  val aufpl_string: ColumnWithName = ColumnWithName(prefix + "aufpl_string")
  val projn_string: ColumnWithName = ColumnWithName(prefix + "projn_string")
  val ppdif2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "ppdif2_decimal_13_2")
  val wskto_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "wskto_decimal_13_2")
  val madat_string: ColumnWithName = ColumnWithName(prefix + "madat_string")
  val zolld_string: ColumnWithName = ColumnWithName(prefix + "zolld_string")
  val umskz_string: ColumnWithName = ColumnWithName(prefix + "umskz_string")
  val qbshb_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "qbshb_decimal_13_2")
  val sknt2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "sknt2_decimal_13_2")
  val xref1_string: ColumnWithName = ColumnWithName(prefix + "xref1_string")
  val qsfbt_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "qsfbt_decimal_13_2")
  val zollt_string: ColumnWithName = ColumnWithName(prefix + "zollt_string")
  val gjahr_string: ColumnWithName = ColumnWithName(prefix + "gjahr_string")
  val buzid_string: ColumnWithName = ColumnWithName(prefix + "buzid_string")
  val dmbt2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmbt2_decimal_13_2")
  val xnetb_string: ColumnWithName = ColumnWithName(prefix + "xnetb_string")
  val eglld_string: ColumnWithName = ColumnWithName(prefix + "eglld_string")
  val anln2_string: ColumnWithName = ColumnWithName(prefix + "anln2_string")
  val mwskz_string: ColumnWithName = ColumnWithName(prefix + "mwskz_string")
  val landl_string: ColumnWithName = ColumnWithName(prefix + "landl_string")
  val vertt_string: ColumnWithName = ColumnWithName(prefix + "vertt_string")
  val pyamt_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "pyamt_decimal_13_2")
  val sknt3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "sknt3_decimal_13_2")
  val augdt_string: ColumnWithName = ColumnWithName(prefix + "augdt_string")
  val budget_pd_string: ColumnWithName = ColumnWithName(prefix + "budget_pd_string")
  val bschl_string: ColumnWithName = ColumnWithName(prefix + "bschl_string")
  val bvtyp_string: ColumnWithName = ColumnWithName(prefix + "bvtyp_string")
  val zbd2p_decimal_5_3: ColumnWithName = ColumnWithName(prefix + "zbd2p_decimal_5_3")
  val lotkz_string: ColumnWithName = ColumnWithName(prefix + "lotkz_string")
  val pswsl_string: ColumnWithName = ColumnWithName(prefix + "pswsl_string")
  val xpypr_string: ColumnWithName = ColumnWithName(prefix + "xpypr_string")
  val fipos_string: ColumnWithName = ColumnWithName(prefix + "fipos_string")
  val uebgdat_string: ColumnWithName = ColumnWithName(prefix + "uebgdat_string")
  val xinve_string: ColumnWithName = ColumnWithName(prefix + "xinve_string")
  val anfbu_string: ColumnWithName = ColumnWithName(prefix + "anfbu_string")
  val xnegp_string: ColumnWithName = ColumnWithName(prefix + "xnegp_string")
  val dmb31_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmb31_decimal_13_2")
  val dtws4_string: ColumnWithName = ColumnWithName(prefix + "dtws4_string")
  val xzahl_string: ColumnWithName = ColumnWithName(prefix + "xzahl_string")
  val wrbt2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "wrbt2_decimal_13_2")
  val penrc_string: ColumnWithName = ColumnWithName(prefix + "penrc_string")
  val rebzz_string: ColumnWithName = ColumnWithName(prefix + "rebzz_string")
  val auggj_string: ColumnWithName = ColumnWithName(prefix + "auggj_string")
  val monat_string: ColumnWithName = ColumnWithName(prefix + "monat_string")
  val bdiff_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "bdiff_decimal_13_2")
  val zlspr_string: ColumnWithName = ColumnWithName(prefix + "zlspr_string")
  val umsks_string: ColumnWithName = ColumnWithName(prefix + "umsks_string")
  val lifnr_string: ColumnWithName = ColumnWithName(prefix + "lifnr_string")
  val mwst2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "mwst2_decimal_13_2")
  val srtype_string: ColumnWithName = ColumnWithName(prefix + "srtype_string")
  val mwsk3_string: ColumnWithName = ColumnWithName(prefix + "mwsk3_string")
  val xesrd_string: ColumnWithName = ColumnWithName(prefix + "xesrd_string")
  val dabrz_string: ColumnWithName = ColumnWithName(prefix + "dabrz_string")
  val hbkid_string: ColumnWithName = ColumnWithName(prefix + "hbkid_string")
  val rebzg_string: ColumnWithName = ColumnWithName(prefix + "rebzg_string")
  val ppdiff_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "ppdiff_decimal_13_2")
  val shkzg_string: ColumnWithName = ColumnWithName(prefix + "shkzg_string")
  val anfbn_string: ColumnWithName = ColumnWithName(prefix + "anfbn_string")
  val zuonr_string: ColumnWithName = ColumnWithName(prefix + "zuonr_string")
  val zumsk_string: ColumnWithName = ColumnWithName(prefix + "zumsk_string")
  val bldat_string: ColumnWithName = ColumnWithName(prefix + "bldat_string")
  val gsber_string: ColumnWithName = ColumnWithName(prefix + "gsber_string")
  val dmb23_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmb23_decimal_13_2")
  val dtws2_string: ColumnWithName = ColumnWithName(prefix + "dtws2_string")
  val budat_string: ColumnWithName = ColumnWithName(prefix + "budat_string")
  val grant_nbr_string: ColumnWithName = ColumnWithName(prefix + "grant_nbr_string")
  val kostl_string: ColumnWithName = ColumnWithName(prefix + "kostl_string")
  val fistl_string: ColumnWithName = ColumnWithName(prefix + "fistl_string")
  val waers_string: ColumnWithName = ColumnWithName(prefix + "waers_string")
  val zekkn_string: ColumnWithName = ColumnWithName(prefix + "zekkn_string")
  val dmb22_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "dmb22_decimal_13_2")
  val wrbt1_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "wrbt1_decimal_13_2")
  val pprct_string: ColumnWithName = ColumnWithName(prefix + "pprct_string")
  val penlc1_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "penlc1_decimal_13_2")
  val gmvkz_string: ColumnWithName = ColumnWithName(prefix + "gmvkz_string")
  val bupla_string: ColumnWithName = ColumnWithName(prefix + "bupla_string")
  val manst_string: ColumnWithName = ColumnWithName(prefix + "manst_string")
  val inwarddt_hd_string: ColumnWithName = ColumnWithName(prefix + "inwarddt_hd_string")
}

object C_bsak extends C_bsak("") {
  def as(alias: String): C_bsak = new C_bsak(alias + ".")
}

// AUTO GENERATED:END
