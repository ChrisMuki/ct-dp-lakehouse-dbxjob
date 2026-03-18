// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_p24

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_ekpo_part1(
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
    techs_string: String,
    fsh_season_string: String,
    matnr_string: String,
    ko_prctr_string: String,
    retpo_string: String,
    bukrs_string: String,
    fkber_string: String,
    reslo_string: String,
    fsh_season_year_string: String,
    pol_id_string: String,
    lblkz_string: String,
    excpe_string: String,
    @Decimal(13, 2) gnetwr_decimal_13_2: BigDecimal,
    inco3_l_string: String,
    sgt_scat_string: String,
    @Decimal(13, 3) ktmng_decimal_13_3: BigDecimal,
    prio_req_string: String,
    ehtyp_string: String,
    consnum_string: String,
    @Decimal(15, 3) mfzhi_decimal_15_3: BigDecimal,
    fsh_atp_date_string: String,
    statu_string: String,
    @Decimal(3, 0) mahn1_decimal_3_0: BigDecimal,
    @Decimal(13, 2) kzwi6_decimal_13_2: BigDecimal,
    fsh_vas_rel_string: String,
    @Decimal(15, 3) ffzhi_decimal_15_3: BigDecimal,
    disub_pspnr_string: String,
    abueb_string: String,
    cons_order_string: String,
    borgr_miss_string: String,
    @Decimal(13, 2) kzwi5_decimal_13_2: BigDecimal,
    etdrk_string: String,
    idnlf_string: String,
    spe_cq_ctrltype_string: String,
    kblnr_string: String,
    usequ_string: String,
    uebpo_string: String,
    repos_string: String,
    @Decimal(5, 0) bpumz_decimal_5_0: BigDecimal,
    berid_string: String,
    attyp_string: String,
    _bev1_nedepfree_string: String,
    knttp_string: String,
    stapo_string: String,
    stpac_string: String,
    j_1bnbm_string: String,
    srv_bas_com_string: String,
    @Decimal(13, 2) navnw_decimal_13_2: BigDecimal,
    disub_kunnr_string: String,
    sgt_rcat_string: String,
    source_key_string: String,
    advcode_string: String,
    bwtty_string: String,
    bsark_string: String,
    _bev1_negen_item_string: String,
    itcons_string: String,
    j_1bownpro_string: String,
    xconditions_string: String,
    emlif_string: String,
    anfnr_string: String,
    insnc_string: String,
    fsh_vas_prnt_id_string: String,
    lewed_string: String,
    _sapmp_gpose_string: String,
    packno_string: String,
    tzonrc_string: String,
    exsnr_string: String,
    @Decimal(5, 0) umren_decimal_5_0: BigDecimal,
    @Decimal(13, 3) ntgew_decimal_13_3: BigDecimal,
    txz01_string: String,
    @Decimal(5, 0) peinh_decimal_5_0: BigDecimal,
    kzkfg_string: String,
    @Decimal(3, 0) etfz2_decimal_3_0: BigDecimal,
    ko_pargb_string: String,
    schpr_string: String,
    umsok_string: String,
    ebeln_string: String,
    adacn_string: String,
    ebonf_string: String,
    arsps_string: String,
    lebre_string: String,
    pstyp_string: String,
    saiso_string: String,
    @Decimal(11, 2) dpamt_decimal_11_2: BigDecimal,
    audat_string: String,
    emnfr_string: String,
    fsh_transaction_string: String,
    adpri_string: String,
    abelp_string: String,
    anzsn_int: BoxedInt,
    rdprf_string: String,
    @Decimal(13, 2) kzwi1_decimal_13_2: BigDecimal,
    ccomp_string: String,
    fsh_item_string: String,
    addns_string: String,
    gewei_string: String,
    afpnr_string: String,
    ebon3_string: String,
    abdat_string: String,
    banfn_string: String,
    evers_string: String,
    @Decimal(13, 2) effwr_decimal_13_2: BigDecimal,
    creationdate_string: String,
    disub_sobkz_string: String,
    trmrisk_relevant_string: String,
    reason_code_string: String,
    xersy_string: String,
    bstyp_string: String,
    sf_txjcd_string: String,
    @Decimal(3, 0) plifz_decimal_3_0: BigDecimal,
    j_1aidatep_string: String,
    weunb_string: String,
    iprkz_string: String,
    eglkz_string: String,
    kzvbr_string: String,
    empst_string: String,
    wabwe_string: String,
    lprio_string: String,
    afnam_string: String,
    ext_rfx_item_string: String,
    disub_vbeln_string: String,
    @Decimal(13, 2) kzwi3_decimal_13_2: BigDecimal,
    geber_string: String,
    angpn_string: String,
    source_id_string: String,
    j_1aindxp_string: String,
    spe_crm_ref_item_string: String,
    apoms_string: String,
    lgort_string: String,
    kolif_string: String,
    cmpl_dlv_itm_string: String,
    diff_invoice_string: String,
    mfrpn_string: String,
    erekz_string: String,
    @Decimal(13, 2) brtwr_decimal_13_2: BigDecimal,
    abskz_string: String,
    ktpnr_string: String,
    ebelp_string: String,
    @Decimal(13, 3) cnfm_qty_decimal_13_3: BigDecimal,
    werks_string: String,
    aurel_string: String,
    @Decimal(13, 3) anzpu_decimal_13_3: BigDecimal,
    kzabs_string: String,
    _bev1_nestruccat_string: String,
    cuobj_string: String,
    nlabd_string: String,
    webre_string: String,
    upvor_string: String,
    fsh_psm_pfm_split_string: String,
    tc_aut_det_string: String,
    sktof_string: String,
    agmem_string: String,
    mandt_string: String,
    creationtime_string: String,
    tax_subject_st_string: String,
    spinf_string: String,
    @Decimal(5, 0) umrez_decimal_5_0: BigDecimal,
    j_1bmatuse_string: String,
    ebon2_string: String,
    @Decimal(3, 0) etfz1_decimal_3_0: BigDecimal,
    disub_owner_string: String,
    fplnr_string: String,
    konnr_string: String,
    xoblr_string: String,
    uptyp_string: String,
    wepos_string: String,
    stafo_string: String,
    bonus_string: String,
    serru_string: String,
    @Decimal(11, 2) netpr_decimal_11_2: BigDecimal,
    saisj_string: String,
    put_back_string: String,
    elikz_string: String,
    txjcd_string: String,
    fsh_grid_cond_rec_string: String,
    mlmaa_string: String,
    inco1_string: String,
    handoverloc_string: String,
    voleh_string: String,
    novet_string: String,
    sobkz_string: String,
    notkz_string: String,
    satnr_string: String,
    bsgru_string: String,
    bnfpo_string: String,
    adrnr_string: String,
    wrf_charstc3_string: String,
    kblpos_string: String,
    spe_ewm_dtc_string: String,
    blk_reason_txt_string: String,
    @Decimal(13, 3) menge_decimal_13_3: BigDecimal,
    nfabd_string: String,
    dpdat_string: String,
    kzfme_string: String,
    fsh_theme_string: String,
    spe_crm_fkrel_string: String,
    meins_string: String,
    blk_reason_id_string: String,
    lfret_string: String,
    inco2_l_string: String,
    iuid_relevant_string: String,
    prdat_string: String,
    agdat_string: String,
    @Decimal(13, 2) netwr_decimal_13_2: BigDecimal,
    manual_tc_reason_string: String,
    ko_pprctr_string: String,
    lgbzo_string: String,
    vsart_string: String,
    fsh_pqr_uepos_string: String,
    angnr_string: String,
    fixmg_string: String,
    @Decimal(13, 2) kzwi4_decimal_13_2: BigDecimal,
    sikgr_string: String,
    @Decimal(13, 2) kzwi2_decimal_13_2: BigDecimal,
    mwskz_string: String,
    j_1bindust_string: String,
    punei_string: String,
    ematn_string: String,
    @Decimal(3, 1) uebto_decimal_3_1: BigDecimal,
    ext_rfx_system_string: String,
    eildt_string: String,
    mfrnr_string: String,
    anfps_string: String,
    mprof_string: String,
    spe_crm_so_string: String,
    budget_pd_string: String,
    zgtyp_string: String,
    chg_srv_string: String,
    @Decimal(13, 3) abftz_decimal_13_3: BigDecimal,
    addrnum_string: String,
    arsnr_string: String,
    ean11_string: String,
    @Decimal(5, 0) bpumn_decimal_5_0: BigDecimal,
    dptyp_string: String,
    chg_fplnr_string: String,
    spe_cq_nocq_string: String,
    fipos_string: String,
    ssqss_string: String,
    labnr_string: String,
    @Decimal(3, 0) mahn2_decimal_3_0: BigDecimal,
    ekkol_string: String,
    aktnr_string: String
) extends Entity

case class E_ekpo_part2(
    @PK _mk_org: String,
    kztlf_string: String,
    fls_rsto_string: String,
    mfrgr_string: String,
    prio_urg_string: String,
    bednr_string: String,
    admoi_string: String,
    fsh_collection_string: String,
    fsh_item_group_string: String,
    lgbzo_b_string: String,
    inco2_string: String,
    loekz_string: String,
    insmk_string: String,
    aedat_string: String,
    abeln_string: String,
    prsdr_string: String,
    @Decimal(3, 0) mahnz_decimal_3_0: BigDecimal,
    @Decimal(13, 3) abmng_decimal_13_3: BigDecimal,
    @Decimal(4, 0) mhdrz_decimal_4_0: BigDecimal,
    ltsnr_string: String,
    externalreferenceid_string: String,
    nrfhg_string: String,
    @Decimal(15, 3) cqu_sar_decimal_15_3: BigDecimal,
    spe_chng_sys_string: String,
    srm_contract_id_string: String,
    spe_crm_ref_so_string: String,
    ref_item_string: String,
    drdat_string: String,
    @Decimal(5, 2) retpc_decimal_5_2: BigDecimal,
    mrpind_string: String,
    refsite_string: String,
    j_1bmatorg_string: String,
    revlv_string: String,
    druhr_string: String,
    ko_gsber_string: String,
    meprf_string: String,
    fiscal_incentive_id_string: String,
    weora_string: String,
    spe_crm_so_item_string: String,
    vrtkz_string: String,
    exlin_string: String,
    @Decimal(13, 2) zwert_decimal_13_2: BigDecimal,
    pnstat_string: String,
    kunnr_string: String,
    adrn2_string: String,
    @Decimal(3, 0) mahn3_decimal_3_0: BigDecimal,
    bwtar_string: String,
    kzbws_string: String,
    mtart_string: String,
    ext_rfx_number_string: String,
    lmein_string: String,
    spe_abgru_string: String,
    bprme_string: String,
    @Decimal(3, 0) webaz_decimal_3_0: BigDecimal,
    fiscal_incentive_string: String,
    spe_insmk_src_string: String,
    sernp_string: String,
    @Decimal(13, 3) volum_decimal_13_3: BigDecimal,
    vorab_string: String,
    disub_posnr_string: String,
    srm_contract_itm_string: String,
    @Decimal(5, 2) dppct_decimal_5_2: BigDecimal,
    fsh_ss_string: String,
    uebtk_string: String,
    infnr_string: String,
    matkl_string: String,
    wrf_charstc1_string: String,
    fistl_string: String,
    status_string: String,
    kzstu_string: String,
    kanba_string: String,
    bstae_string: String,
    @Decimal(13, 2) bonba_decimal_13_2: BigDecimal,
    wrf_charstc2_string: String,
    grant_nbr_string: String,
    @Decimal(3, 1) untto_decimal_3_1: BigDecimal,
    @Decimal(13, 3) brgew_decimal_13_3: BigDecimal,
    fmfgus_key_string: String,
    twrkz_string: String,
    fabkz_string: String,
    drunr_string: String
) extends Entity

object ekpo extends TableSpec[Joined[E_ekpo_part1, E_ekpo_part2]](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_ekpo(prefix: String) extends ColumnWithNameAccessor {
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
  val techs_string: ColumnWithName = ColumnWithName(prefix + "techs_string")
  val fsh_season_string: ColumnWithName = ColumnWithName(prefix + "fsh_season_string")
  val matnr_string: ColumnWithName = ColumnWithName(prefix + "matnr_string")
  val ko_prctr_string: ColumnWithName = ColumnWithName(prefix + "ko_prctr_string")
  val retpo_string: ColumnWithName = ColumnWithName(prefix + "retpo_string")
  val bukrs_string: ColumnWithName = ColumnWithName(prefix + "bukrs_string")
  val fkber_string: ColumnWithName = ColumnWithName(prefix + "fkber_string")
  val reslo_string: ColumnWithName = ColumnWithName(prefix + "reslo_string")
  val fsh_season_year_string: ColumnWithName = ColumnWithName(prefix + "fsh_season_year_string")
  val pol_id_string: ColumnWithName = ColumnWithName(prefix + "pol_id_string")
  val lblkz_string: ColumnWithName = ColumnWithName(prefix + "lblkz_string")
  val excpe_string: ColumnWithName = ColumnWithName(prefix + "excpe_string")
  val gnetwr_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "gnetwr_decimal_13_2")
  val inco3_l_string: ColumnWithName = ColumnWithName(prefix + "inco3_l_string")
  val sgt_scat_string: ColumnWithName = ColumnWithName(prefix + "sgt_scat_string")
  val ktmng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "ktmng_decimal_13_3")
  val prio_req_string: ColumnWithName = ColumnWithName(prefix + "prio_req_string")
  val ehtyp_string: ColumnWithName = ColumnWithName(prefix + "ehtyp_string")
  val consnum_string: ColumnWithName = ColumnWithName(prefix + "consnum_string")
  val mfzhi_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "mfzhi_decimal_15_3")
  val fsh_atp_date_string: ColumnWithName = ColumnWithName(prefix + "fsh_atp_date_string")
  val statu_string: ColumnWithName = ColumnWithName(prefix + "statu_string")
  val mahn1_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "mahn1_decimal_3_0")
  val kzwi6_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi6_decimal_13_2")
  val fsh_vas_rel_string: ColumnWithName = ColumnWithName(prefix + "fsh_vas_rel_string")
  val ffzhi_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "ffzhi_decimal_15_3")
  val disub_pspnr_string: ColumnWithName = ColumnWithName(prefix + "disub_pspnr_string")
  val abueb_string: ColumnWithName = ColumnWithName(prefix + "abueb_string")
  val cons_order_string: ColumnWithName = ColumnWithName(prefix + "cons_order_string")
  val borgr_miss_string: ColumnWithName = ColumnWithName(prefix + "borgr_miss_string")
  val kzwi5_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi5_decimal_13_2")
  val etdrk_string: ColumnWithName = ColumnWithName(prefix + "etdrk_string")
  val idnlf_string: ColumnWithName = ColumnWithName(prefix + "idnlf_string")
  val spe_cq_ctrltype_string: ColumnWithName = ColumnWithName(prefix + "spe_cq_ctrltype_string")
  val kblnr_string: ColumnWithName = ColumnWithName(prefix + "kblnr_string")
  val usequ_string: ColumnWithName = ColumnWithName(prefix + "usequ_string")
  val uebpo_string: ColumnWithName = ColumnWithName(prefix + "uebpo_string")
  val repos_string: ColumnWithName = ColumnWithName(prefix + "repos_string")
  val bpumz_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "bpumz_decimal_5_0")
  val berid_string: ColumnWithName = ColumnWithName(prefix + "berid_string")
  val attyp_string: ColumnWithName = ColumnWithName(prefix + "attyp_string")
  val _bev1_nedepfree_string: ColumnWithName = ColumnWithName(prefix + "_bev1_nedepfree_string")
  val knttp_string: ColumnWithName = ColumnWithName(prefix + "knttp_string")
  val stapo_string: ColumnWithName = ColumnWithName(prefix + "stapo_string")
  val stpac_string: ColumnWithName = ColumnWithName(prefix + "stpac_string")
  val j_1bnbm_string: ColumnWithName = ColumnWithName(prefix + "j_1bnbm_string")
  val srv_bas_com_string: ColumnWithName = ColumnWithName(prefix + "srv_bas_com_string")
  val navnw_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "navnw_decimal_13_2")
  val disub_kunnr_string: ColumnWithName = ColumnWithName(prefix + "disub_kunnr_string")
  val sgt_rcat_string: ColumnWithName = ColumnWithName(prefix + "sgt_rcat_string")
  val source_key_string: ColumnWithName = ColumnWithName(prefix + "source_key_string")
  val advcode_string: ColumnWithName = ColumnWithName(prefix + "advcode_string")
  val bwtty_string: ColumnWithName = ColumnWithName(prefix + "bwtty_string")
  val bsark_string: ColumnWithName = ColumnWithName(prefix + "bsark_string")
  val _bev1_negen_item_string: ColumnWithName = ColumnWithName(prefix + "_bev1_negen_item_string")
  val itcons_string: ColumnWithName = ColumnWithName(prefix + "itcons_string")
  val j_1bownpro_string: ColumnWithName = ColumnWithName(prefix + "j_1bownpro_string")
  val xconditions_string: ColumnWithName = ColumnWithName(prefix + "xconditions_string")
  val emlif_string: ColumnWithName = ColumnWithName(prefix + "emlif_string")
  val anfnr_string: ColumnWithName = ColumnWithName(prefix + "anfnr_string")
  val insnc_string: ColumnWithName = ColumnWithName(prefix + "insnc_string")
  val fsh_vas_prnt_id_string: ColumnWithName = ColumnWithName(prefix + "fsh_vas_prnt_id_string")
  val lewed_string: ColumnWithName = ColumnWithName(prefix + "lewed_string")
  val _sapmp_gpose_string: ColumnWithName = ColumnWithName(prefix + "_sapmp_gpose_string")
  val packno_string: ColumnWithName = ColumnWithName(prefix + "packno_string")
  val tzonrc_string: ColumnWithName = ColumnWithName(prefix + "tzonrc_string")
  val exsnr_string: ColumnWithName = ColumnWithName(prefix + "exsnr_string")
  val umren_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "umren_decimal_5_0")
  val ntgew_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "ntgew_decimal_13_3")
  val txz01_string: ColumnWithName = ColumnWithName(prefix + "txz01_string")
  val peinh_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "peinh_decimal_5_0")
  val kzkfg_string: ColumnWithName = ColumnWithName(prefix + "kzkfg_string")
  val etfz2_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "etfz2_decimal_3_0")
  val ko_pargb_string: ColumnWithName = ColumnWithName(prefix + "ko_pargb_string")
  val schpr_string: ColumnWithName = ColumnWithName(prefix + "schpr_string")
  val umsok_string: ColumnWithName = ColumnWithName(prefix + "umsok_string")
  val ebeln_string: ColumnWithName = ColumnWithName(prefix + "ebeln_string")
  val adacn_string: ColumnWithName = ColumnWithName(prefix + "adacn_string")
  val ebonf_string: ColumnWithName = ColumnWithName(prefix + "ebonf_string")
  val arsps_string: ColumnWithName = ColumnWithName(prefix + "arsps_string")
  val lebre_string: ColumnWithName = ColumnWithName(prefix + "lebre_string")
  val pstyp_string: ColumnWithName = ColumnWithName(prefix + "pstyp_string")
  val saiso_string: ColumnWithName = ColumnWithName(prefix + "saiso_string")
  val dpamt_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "dpamt_decimal_11_2")
  val audat_string: ColumnWithName = ColumnWithName(prefix + "audat_string")
  val emnfr_string: ColumnWithName = ColumnWithName(prefix + "emnfr_string")
  val fsh_transaction_string: ColumnWithName = ColumnWithName(prefix + "fsh_transaction_string")
  val adpri_string: ColumnWithName = ColumnWithName(prefix + "adpri_string")
  val abelp_string: ColumnWithName = ColumnWithName(prefix + "abelp_string")
  val anzsn_int: ColumnWithName = ColumnWithName(prefix + "anzsn_int")
  val rdprf_string: ColumnWithName = ColumnWithName(prefix + "rdprf_string")
  val kzwi1_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi1_decimal_13_2")
  val ccomp_string: ColumnWithName = ColumnWithName(prefix + "ccomp_string")
  val fsh_item_string: ColumnWithName = ColumnWithName(prefix + "fsh_item_string")
  val addns_string: ColumnWithName = ColumnWithName(prefix + "addns_string")
  val gewei_string: ColumnWithName = ColumnWithName(prefix + "gewei_string")
  val afpnr_string: ColumnWithName = ColumnWithName(prefix + "afpnr_string")
  val ebon3_string: ColumnWithName = ColumnWithName(prefix + "ebon3_string")
  val abdat_string: ColumnWithName = ColumnWithName(prefix + "abdat_string")
  val banfn_string: ColumnWithName = ColumnWithName(prefix + "banfn_string")
  val evers_string: ColumnWithName = ColumnWithName(prefix + "evers_string")
  val effwr_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "effwr_decimal_13_2")
  val creationdate_string: ColumnWithName = ColumnWithName(prefix + "creationdate_string")
  val disub_sobkz_string: ColumnWithName = ColumnWithName(prefix + "disub_sobkz_string")
  val trmrisk_relevant_string: ColumnWithName = ColumnWithName(prefix + "trmrisk_relevant_string")
  val reason_code_string: ColumnWithName = ColumnWithName(prefix + "reason_code_string")
  val xersy_string: ColumnWithName = ColumnWithName(prefix + "xersy_string")
  val bstyp_string: ColumnWithName = ColumnWithName(prefix + "bstyp_string")
  val sf_txjcd_string: ColumnWithName = ColumnWithName(prefix + "sf_txjcd_string")
  val plifz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "plifz_decimal_3_0")
  val j_1aidatep_string: ColumnWithName = ColumnWithName(prefix + "j_1aidatep_string")
  val weunb_string: ColumnWithName = ColumnWithName(prefix + "weunb_string")
  val iprkz_string: ColumnWithName = ColumnWithName(prefix + "iprkz_string")
  val eglkz_string: ColumnWithName = ColumnWithName(prefix + "eglkz_string")
  val kzvbr_string: ColumnWithName = ColumnWithName(prefix + "kzvbr_string")
  val empst_string: ColumnWithName = ColumnWithName(prefix + "empst_string")
  val wabwe_string: ColumnWithName = ColumnWithName(prefix + "wabwe_string")
  val lprio_string: ColumnWithName = ColumnWithName(prefix + "lprio_string")
  val afnam_string: ColumnWithName = ColumnWithName(prefix + "afnam_string")
  val ext_rfx_item_string: ColumnWithName = ColumnWithName(prefix + "ext_rfx_item_string")
  val disub_vbeln_string: ColumnWithName = ColumnWithName(prefix + "disub_vbeln_string")
  val kzwi3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi3_decimal_13_2")
  val geber_string: ColumnWithName = ColumnWithName(prefix + "geber_string")
  val angpn_string: ColumnWithName = ColumnWithName(prefix + "angpn_string")
  val source_id_string: ColumnWithName = ColumnWithName(prefix + "source_id_string")
  val j_1aindxp_string: ColumnWithName = ColumnWithName(prefix + "j_1aindxp_string")
  val spe_crm_ref_item_string: ColumnWithName = ColumnWithName(prefix + "spe_crm_ref_item_string")
  val apoms_string: ColumnWithName = ColumnWithName(prefix + "apoms_string")
  val lgort_string: ColumnWithName = ColumnWithName(prefix + "lgort_string")
  val kolif_string: ColumnWithName = ColumnWithName(prefix + "kolif_string")
  val cmpl_dlv_itm_string: ColumnWithName = ColumnWithName(prefix + "cmpl_dlv_itm_string")
  val diff_invoice_string: ColumnWithName = ColumnWithName(prefix + "diff_invoice_string")
  val mfrpn_string: ColumnWithName = ColumnWithName(prefix + "mfrpn_string")
  val erekz_string: ColumnWithName = ColumnWithName(prefix + "erekz_string")
  val brtwr_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "brtwr_decimal_13_2")
  val abskz_string: ColumnWithName = ColumnWithName(prefix + "abskz_string")
  val ktpnr_string: ColumnWithName = ColumnWithName(prefix + "ktpnr_string")
  val ebelp_string: ColumnWithName = ColumnWithName(prefix + "ebelp_string")
  val cnfm_qty_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "cnfm_qty_decimal_13_3")
  val werks_string: ColumnWithName = ColumnWithName(prefix + "werks_string")
  val aurel_string: ColumnWithName = ColumnWithName(prefix + "aurel_string")
  val anzpu_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "anzpu_decimal_13_3")
  val kzabs_string: ColumnWithName = ColumnWithName(prefix + "kzabs_string")
  val _bev1_nestruccat_string: ColumnWithName = ColumnWithName(prefix + "_bev1_nestruccat_string")
  val cuobj_string: ColumnWithName = ColumnWithName(prefix + "cuobj_string")
  val nlabd_string: ColumnWithName = ColumnWithName(prefix + "nlabd_string")
  val webre_string: ColumnWithName = ColumnWithName(prefix + "webre_string")
  val upvor_string: ColumnWithName = ColumnWithName(prefix + "upvor_string")
  val fsh_psm_pfm_split_string: ColumnWithName = ColumnWithName(prefix + "fsh_psm_pfm_split_string")
  val tc_aut_det_string: ColumnWithName = ColumnWithName(prefix + "tc_aut_det_string")
  val sktof_string: ColumnWithName = ColumnWithName(prefix + "sktof_string")
  val agmem_string: ColumnWithName = ColumnWithName(prefix + "agmem_string")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val creationtime_string: ColumnWithName = ColumnWithName(prefix + "creationtime_string")
  val tax_subject_st_string: ColumnWithName = ColumnWithName(prefix + "tax_subject_st_string")
  val spinf_string: ColumnWithName = ColumnWithName(prefix + "spinf_string")
  val umrez_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "umrez_decimal_5_0")
  val j_1bmatuse_string: ColumnWithName = ColumnWithName(prefix + "j_1bmatuse_string")
  val ebon2_string: ColumnWithName = ColumnWithName(prefix + "ebon2_string")
  val etfz1_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "etfz1_decimal_3_0")
  val disub_owner_string: ColumnWithName = ColumnWithName(prefix + "disub_owner_string")
  val fplnr_string: ColumnWithName = ColumnWithName(prefix + "fplnr_string")
  val konnr_string: ColumnWithName = ColumnWithName(prefix + "konnr_string")
  val xoblr_string: ColumnWithName = ColumnWithName(prefix + "xoblr_string")
  val uptyp_string: ColumnWithName = ColumnWithName(prefix + "uptyp_string")
  val wepos_string: ColumnWithName = ColumnWithName(prefix + "wepos_string")
  val stafo_string: ColumnWithName = ColumnWithName(prefix + "stafo_string")
  val bonus_string: ColumnWithName = ColumnWithName(prefix + "bonus_string")
  val serru_string: ColumnWithName = ColumnWithName(prefix + "serru_string")
  val netpr_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "netpr_decimal_11_2")
  val saisj_string: ColumnWithName = ColumnWithName(prefix + "saisj_string")
  val put_back_string: ColumnWithName = ColumnWithName(prefix + "put_back_string")
  val elikz_string: ColumnWithName = ColumnWithName(prefix + "elikz_string")
  val txjcd_string: ColumnWithName = ColumnWithName(prefix + "txjcd_string")
  val fsh_grid_cond_rec_string: ColumnWithName = ColumnWithName(prefix + "fsh_grid_cond_rec_string")
  val mlmaa_string: ColumnWithName = ColumnWithName(prefix + "mlmaa_string")
  val inco1_string: ColumnWithName = ColumnWithName(prefix + "inco1_string")
  val handoverloc_string: ColumnWithName = ColumnWithName(prefix + "handoverloc_string")
  val voleh_string: ColumnWithName = ColumnWithName(prefix + "voleh_string")
  val novet_string: ColumnWithName = ColumnWithName(prefix + "novet_string")
  val sobkz_string: ColumnWithName = ColumnWithName(prefix + "sobkz_string")
  val notkz_string: ColumnWithName = ColumnWithName(prefix + "notkz_string")
  val satnr_string: ColumnWithName = ColumnWithName(prefix + "satnr_string")
  val bsgru_string: ColumnWithName = ColumnWithName(prefix + "bsgru_string")
  val bnfpo_string: ColumnWithName = ColumnWithName(prefix + "bnfpo_string")
  val adrnr_string: ColumnWithName = ColumnWithName(prefix + "adrnr_string")
  val wrf_charstc3_string: ColumnWithName = ColumnWithName(prefix + "wrf_charstc3_string")
  val kblpos_string: ColumnWithName = ColumnWithName(prefix + "kblpos_string")
  val spe_ewm_dtc_string: ColumnWithName = ColumnWithName(prefix + "spe_ewm_dtc_string")
  val blk_reason_txt_string: ColumnWithName = ColumnWithName(prefix + "blk_reason_txt_string")
  val menge_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "menge_decimal_13_3")
  val nfabd_string: ColumnWithName = ColumnWithName(prefix + "nfabd_string")
  val dpdat_string: ColumnWithName = ColumnWithName(prefix + "dpdat_string")
  val kzfme_string: ColumnWithName = ColumnWithName(prefix + "kzfme_string")
  val fsh_theme_string: ColumnWithName = ColumnWithName(prefix + "fsh_theme_string")
  val spe_crm_fkrel_string: ColumnWithName = ColumnWithName(prefix + "spe_crm_fkrel_string")
  val meins_string: ColumnWithName = ColumnWithName(prefix + "meins_string")
  val blk_reason_id_string: ColumnWithName = ColumnWithName(prefix + "blk_reason_id_string")
  val lfret_string: ColumnWithName = ColumnWithName(prefix + "lfret_string")
  val inco2_l_string: ColumnWithName = ColumnWithName(prefix + "inco2_l_string")
  val iuid_relevant_string: ColumnWithName = ColumnWithName(prefix + "iuid_relevant_string")
  val prdat_string: ColumnWithName = ColumnWithName(prefix + "prdat_string")
  val agdat_string: ColumnWithName = ColumnWithName(prefix + "agdat_string")
  val netwr_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "netwr_decimal_13_2")
  val manual_tc_reason_string: ColumnWithName = ColumnWithName(prefix + "manual_tc_reason_string")
  val ko_pprctr_string: ColumnWithName = ColumnWithName(prefix + "ko_pprctr_string")
  val lgbzo_string: ColumnWithName = ColumnWithName(prefix + "lgbzo_string")
  val vsart_string: ColumnWithName = ColumnWithName(prefix + "vsart_string")
  val fsh_pqr_uepos_string: ColumnWithName = ColumnWithName(prefix + "fsh_pqr_uepos_string")
  val angnr_string: ColumnWithName = ColumnWithName(prefix + "angnr_string")
  val fixmg_string: ColumnWithName = ColumnWithName(prefix + "fixmg_string")
  val kzwi4_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi4_decimal_13_2")
  val sikgr_string: ColumnWithName = ColumnWithName(prefix + "sikgr_string")
  val kzwi2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi2_decimal_13_2")
  val mwskz_string: ColumnWithName = ColumnWithName(prefix + "mwskz_string")
  val j_1bindust_string: ColumnWithName = ColumnWithName(prefix + "j_1bindust_string")
  val punei_string: ColumnWithName = ColumnWithName(prefix + "punei_string")
  val ematn_string: ColumnWithName = ColumnWithName(prefix + "ematn_string")
  val uebto_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "uebto_decimal_3_1")
  val ext_rfx_system_string: ColumnWithName = ColumnWithName(prefix + "ext_rfx_system_string")
  val eildt_string: ColumnWithName = ColumnWithName(prefix + "eildt_string")
  val mfrnr_string: ColumnWithName = ColumnWithName(prefix + "mfrnr_string")
  val anfps_string: ColumnWithName = ColumnWithName(prefix + "anfps_string")
  val mprof_string: ColumnWithName = ColumnWithName(prefix + "mprof_string")
  val spe_crm_so_string: ColumnWithName = ColumnWithName(prefix + "spe_crm_so_string")
  val budget_pd_string: ColumnWithName = ColumnWithName(prefix + "budget_pd_string")
  val zgtyp_string: ColumnWithName = ColumnWithName(prefix + "zgtyp_string")
  val chg_srv_string: ColumnWithName = ColumnWithName(prefix + "chg_srv_string")
  val abftz_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "abftz_decimal_13_3")
  val addrnum_string: ColumnWithName = ColumnWithName(prefix + "addrnum_string")
  val arsnr_string: ColumnWithName = ColumnWithName(prefix + "arsnr_string")
  val ean11_string: ColumnWithName = ColumnWithName(prefix + "ean11_string")
  val bpumn_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "bpumn_decimal_5_0")
  val dptyp_string: ColumnWithName = ColumnWithName(prefix + "dptyp_string")
  val chg_fplnr_string: ColumnWithName = ColumnWithName(prefix + "chg_fplnr_string")
  val spe_cq_nocq_string: ColumnWithName = ColumnWithName(prefix + "spe_cq_nocq_string")
  val fipos_string: ColumnWithName = ColumnWithName(prefix + "fipos_string")
  val ssqss_string: ColumnWithName = ColumnWithName(prefix + "ssqss_string")
  val labnr_string: ColumnWithName = ColumnWithName(prefix + "labnr_string")
  val mahn2_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "mahn2_decimal_3_0")
  val ekkol_string: ColumnWithName = ColumnWithName(prefix + "ekkol_string")
  val aktnr_string: ColumnWithName = ColumnWithName(prefix + "aktnr_string")
  val kztlf_string: ColumnWithName = ColumnWithName(prefix + "kztlf_string")
  val fls_rsto_string: ColumnWithName = ColumnWithName(prefix + "fls_rsto_string")
  val mfrgr_string: ColumnWithName = ColumnWithName(prefix + "mfrgr_string")
  val prio_urg_string: ColumnWithName = ColumnWithName(prefix + "prio_urg_string")
  val bednr_string: ColumnWithName = ColumnWithName(prefix + "bednr_string")
  val admoi_string: ColumnWithName = ColumnWithName(prefix + "admoi_string")
  val fsh_collection_string: ColumnWithName = ColumnWithName(prefix + "fsh_collection_string")
  val fsh_item_group_string: ColumnWithName = ColumnWithName(prefix + "fsh_item_group_string")
  val lgbzo_b_string: ColumnWithName = ColumnWithName(prefix + "lgbzo_b_string")
  val inco2_string: ColumnWithName = ColumnWithName(prefix + "inco2_string")
  val loekz_string: ColumnWithName = ColumnWithName(prefix + "loekz_string")
  val insmk_string: ColumnWithName = ColumnWithName(prefix + "insmk_string")
  val aedat_string: ColumnWithName = ColumnWithName(prefix + "aedat_string")
  val abeln_string: ColumnWithName = ColumnWithName(prefix + "abeln_string")
  val prsdr_string: ColumnWithName = ColumnWithName(prefix + "prsdr_string")
  val mahnz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "mahnz_decimal_3_0")
  val abmng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "abmng_decimal_13_3")
  val mhdrz_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "mhdrz_decimal_4_0")
  val ltsnr_string: ColumnWithName = ColumnWithName(prefix + "ltsnr_string")
  val externalreferenceid_string: ColumnWithName = ColumnWithName(prefix + "externalreferenceid_string")
  val nrfhg_string: ColumnWithName = ColumnWithName(prefix + "nrfhg_string")
  val cqu_sar_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "cqu_sar_decimal_15_3")
  val spe_chng_sys_string: ColumnWithName = ColumnWithName(prefix + "spe_chng_sys_string")
  val srm_contract_id_string: ColumnWithName = ColumnWithName(prefix + "srm_contract_id_string")
  val spe_crm_ref_so_string: ColumnWithName = ColumnWithName(prefix + "spe_crm_ref_so_string")
  val ref_item_string: ColumnWithName = ColumnWithName(prefix + "ref_item_string")
  val drdat_string: ColumnWithName = ColumnWithName(prefix + "drdat_string")
  val retpc_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "retpc_decimal_5_2")
  val mrpind_string: ColumnWithName = ColumnWithName(prefix + "mrpind_string")
  val refsite_string: ColumnWithName = ColumnWithName(prefix + "refsite_string")
  val j_1bmatorg_string: ColumnWithName = ColumnWithName(prefix + "j_1bmatorg_string")
  val revlv_string: ColumnWithName = ColumnWithName(prefix + "revlv_string")
  val druhr_string: ColumnWithName = ColumnWithName(prefix + "druhr_string")
  val ko_gsber_string: ColumnWithName = ColumnWithName(prefix + "ko_gsber_string")
  val meprf_string: ColumnWithName = ColumnWithName(prefix + "meprf_string")
  val fiscal_incentive_id_string: ColumnWithName = ColumnWithName(prefix + "fiscal_incentive_id_string")
  val weora_string: ColumnWithName = ColumnWithName(prefix + "weora_string")
  val spe_crm_so_item_string: ColumnWithName = ColumnWithName(prefix + "spe_crm_so_item_string")
  val vrtkz_string: ColumnWithName = ColumnWithName(prefix + "vrtkz_string")
  val exlin_string: ColumnWithName = ColumnWithName(prefix + "exlin_string")
  val zwert_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "zwert_decimal_13_2")
  val pnstat_string: ColumnWithName = ColumnWithName(prefix + "pnstat_string")
  val kunnr_string: ColumnWithName = ColumnWithName(prefix + "kunnr_string")
  val adrn2_string: ColumnWithName = ColumnWithName(prefix + "adrn2_string")
  val mahn3_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "mahn3_decimal_3_0")
  val bwtar_string: ColumnWithName = ColumnWithName(prefix + "bwtar_string")
  val kzbws_string: ColumnWithName = ColumnWithName(prefix + "kzbws_string")
  val mtart_string: ColumnWithName = ColumnWithName(prefix + "mtart_string")
  val ext_rfx_number_string: ColumnWithName = ColumnWithName(prefix + "ext_rfx_number_string")
  val lmein_string: ColumnWithName = ColumnWithName(prefix + "lmein_string")
  val spe_abgru_string: ColumnWithName = ColumnWithName(prefix + "spe_abgru_string")
  val bprme_string: ColumnWithName = ColumnWithName(prefix + "bprme_string")
  val webaz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "webaz_decimal_3_0")
  val fiscal_incentive_string: ColumnWithName = ColumnWithName(prefix + "fiscal_incentive_string")
  val spe_insmk_src_string: ColumnWithName = ColumnWithName(prefix + "spe_insmk_src_string")
  val sernp_string: ColumnWithName = ColumnWithName(prefix + "sernp_string")
  val volum_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "volum_decimal_13_3")
  val vorab_string: ColumnWithName = ColumnWithName(prefix + "vorab_string")
  val disub_posnr_string: ColumnWithName = ColumnWithName(prefix + "disub_posnr_string")
  val srm_contract_itm_string: ColumnWithName = ColumnWithName(prefix + "srm_contract_itm_string")
  val dppct_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "dppct_decimal_5_2")
  val fsh_ss_string: ColumnWithName = ColumnWithName(prefix + "fsh_ss_string")
  val uebtk_string: ColumnWithName = ColumnWithName(prefix + "uebtk_string")
  val infnr_string: ColumnWithName = ColumnWithName(prefix + "infnr_string")
  val matkl_string: ColumnWithName = ColumnWithName(prefix + "matkl_string")
  val wrf_charstc1_string: ColumnWithName = ColumnWithName(prefix + "wrf_charstc1_string")
  val fistl_string: ColumnWithName = ColumnWithName(prefix + "fistl_string")
  val status_string: ColumnWithName = ColumnWithName(prefix + "status_string")
  val kzstu_string: ColumnWithName = ColumnWithName(prefix + "kzstu_string")
  val kanba_string: ColumnWithName = ColumnWithName(prefix + "kanba_string")
  val bstae_string: ColumnWithName = ColumnWithName(prefix + "bstae_string")
  val bonba_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "bonba_decimal_13_2")
  val wrf_charstc2_string: ColumnWithName = ColumnWithName(prefix + "wrf_charstc2_string")
  val grant_nbr_string: ColumnWithName = ColumnWithName(prefix + "grant_nbr_string")
  val untto_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "untto_decimal_3_1")
  val brgew_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "brgew_decimal_13_3")
  val fmfgus_key_string: ColumnWithName = ColumnWithName(prefix + "fmfgus_key_string")
  val twrkz_string: ColumnWithName = ColumnWithName(prefix + "twrkz_string")
  val fabkz_string: ColumnWithName = ColumnWithName(prefix + "fabkz_string")
  val drunr_string: ColumnWithName = ColumnWithName(prefix + "drunr_string")
}

object C_ekpo extends C_ekpo("") {
  def as(alias: String): C_ekpo = new C_ekpo(alias + ".")
}

// AUTO GENERATED:END
