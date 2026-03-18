// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_ghp

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_marc_part1(
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
    uomgr_string: String,
    scm_sft_lock_string: String,
    @Decimal(3, 1) _sapmp_tolprmi_decimal_3_1: BigDecimal,
    @Decimal(13, 3) scm_repsafty_v_decimal_13_3: BigDecimal,
    @Decimal(3, 1) ueeto_decimal_3_1: BigDecimal,
    matnr_string: String,
    yystk_indicator_string: String,
    @Decimal(11, 0) scm_giprt_decimal_11_0: BigDecimal,
    yyprodsts_string: String,
    @Decimal(3, 0) resvp_decimal_3_0: BigDecimal,
    iuid_type_string: String,
    @Decimal(13, 3) bstmi_decimal_13_3: BigDecimal,
    lgpro_string: String,
    ueetk_string: String,
    yystdpkmsg_string: String,
    zzprojn_string: String,
    ladgr_string: String,
    diber_string: String,
    rgekz_string: String,
    atpkz_string: String,
    sgt_stk_prt_string: String,
    zzstdpkmsg_string: String,
    schgt_string: String,
    @Decimal(13, 6) zzqcf_decimal_13_6: BigDecimal,
    shpro_string: String,
    @Decimal(13, 2) vkumc_decimal_13_2: BigDecimal,
    ocmpf_string: String,
    scm_ges_bst_use_string: String,
    _vso_r_fork_dir_string: String,
    prctr_string: String,
    otype_string: String,
    lizyk_string: String,
    sobsk_string: String,
    @Decimal(13, 3) scm_ppsaftystk_decimal_13_3: BigDecimal,
    kzech_string: String,
    mrppp_string: String,
    lfmon_string: String,
    dummy_plnt_incl_eew_ps_string: String,
    vint2_string: String,
    temp_uom_string: String,
    @Decimal(5, 2) kausf_decimal_5_2: BigDecimal,
    @Decimal(9, 0) wstgh_decimal_9_0: BigDecimal,
    plnnr_string: String,
    objid_string: String,
    usequ_string: String,
    fsh_mg_arun_req_string: String,
    @Decimal(13, 3) umlmc_decimal_13_3: BigDecimal,
    lzeih_string: String,
    sauft_string: String,
    @Decimal(13, 3) scm_reord_v_decimal_13_3: BigDecimal,
    loggr_string: String,
    copam_string: String,
    fsh_calendar_group_string: String,
    @Decimal(3, 2) scm_scost_prcnt_decimal_3_2: BigDecimal,
    bwtty_string: String,
    @Decimal(13, 2) vkglg_decimal_13_2: BigDecimal,
    kordb_string: String,
    @Decimal(13, 3) scm_ndcostwe_decimal_13_3: BigDecimal,
    sgt_mmstd_string: String,
    @Decimal(13, 3) yyordintr_decimal_13_3: BigDecimal,
    nkmpr_string: String,
    profil_string: String,
    autru_string: String,
    kzppv_string: String,
    fhori_string: String,
    scm_hunit_string: String,
    @Decimal(13, 3) yymin_ord_decimal_13_3: BigDecimal,
    bwscl_string: String,
    qmatv_string: String,
    ccfix_string: String,
    prend_string: String,
    pstat_string: String,
    miskz_string: String,
    steuc_string: String,
    prene_string: String,
    plnty_string: String,
    @Decimal(4, 2) vrbfk_decimal_4_2: BigDecimal,
    @Decimal(11, 0) scm_peg_past_alert_decimal_11_0: BigDecimal,
    @Decimal(2, 1) abfac_decimal_2_1: BigDecimal,
    @Decimal(11, 0) scm_thruput_time_decimal_11_0: BigDecimal,
    sgt_mrpsi_string: String,
    indus_string: String,
    compl_string: String,
    excise_tax_rlvnce_string: String,
    frtme_string: String,
    dplfs_string: String,
    kzpro_string: String,
    esppflg_string: String,
    uchkz_string: String,
    ausdt_string: String,
    @Decimal(15, 3) scm_min_pass_amount_decimal_15_3: BigDecimal,
    scm_hunit_out_string: String,
    umrsl_string: String,
    pfrei_string: String,
    @Decimal(11, 0) scm_grprt_decimal_11_0: BigDecimal,
    mdach_string: String,
    scm_res_net_name_string: String,
    kzkup_string: String,
    scm_shelf_life_loc_string: String,
    @Decimal(13, 3) scm_sspen_decimal_13_3: BigDecimal,
    vrbmt_string: String,
    dispr_string: String,
    ucmat_string: String,
    scm_package_id_string: String,
    sbdkz_string: String,
    scm_peg_wo_alert_fst_string: String,
    kzpsp_string: String,
    @Decimal(7, 2) temp_ctrl_min_decimal_7_2: BigDecimal,
    @Decimal(11, 0) scm_shelf_life_dur_decimal_11_0: BigDecimal,
    @Decimal(13, 3) bwesb_decimal_13_3: BigDecimal,
    rdprf_string: String,
    multiple_ekgrp_string: String,
    @Decimal(11, 0) scm_reord_dur_decimal_11_0: BigDecimal,
    max_troc_string: String,
    @Decimal(13, 3) scm_proc_cost_decimal_13_3: BigDecimal,
    vrbwk_string: String,
    yyplantpn_string: String,
    stlan_string: String,
    zzplantref_string: String,
    scm_ges_mng_use_string: String,
    @Decimal(13, 3) yymax_ord_decimal_13_3: BigDecimal,
    jitprodnconfprofile_string: String,
    @Decimal(5, 2) bearz_decimal_5_2: BigDecimal,
    @Decimal(13, 3) scm_repsafty_decimal_13_3: BigDecimal,
    yfamly_string: String,
    @Decimal(11, 0) scm_maturity_dur_decimal_11_0: BigDecimal,
    stlal_string: String,
    _sttpec_servalid_string: String,
    ref_schema_string: String,
    @Decimal(3, 0) plifz_decimal_3_0: BigDecimal,
    sfcpf_string: String,
    fxhor_string: String,
    scm_stra1_string: String,
    yypickstrg_string: String,
    @Decimal(3, 1) _sapmp_tolprpl_decimal_3_1: BigDecimal,
    @Decimal(13, 3) bstfe_decimal_13_3: BigDecimal,
    auftl_string: String,
    xmcng_string: String,
    kzkfk_string: String,
    fsh_seaim_string: String,
    nfmat_string: String,
    ppskz_string: String,
    @Decimal(3, 0) takzt_decimal_3_0: BigDecimal,
    _vso_r_pkgrp_string: String,
    _vso_r_lane_num_string: String,
    @Decimal(13, 3) minbe_decimal_13_3: BigDecimal,
    ffrei_string: String,
    dispo_string: String,
    yysort2_string: String,
    gpmkz_string: String,
    sgt_mrp_atp_status_string: String,
    eprio_string: String,
    sgt_covs_string: String,
    @Decimal(13, 3) yyavg_daily_cns_decimal_13_3: BigDecimal,
    yystdpkuom_string: String,
    lgfsb_string: String,
    @Decimal(13, 3) scm_maxstock_v_decimal_13_3: BigDecimal,
    sfepr_string: String,
    plvar_string: String,
    shzet_string: String,
    werks_string: String,
    @Decimal(11, 0) scm_shlf_lfe_req_max_decimal_11_0: BigDecimal,
    cuobj_string: String,
    rotation_date_string: String,
    @Decimal(13, 3) eislo_decimal_13_3: BigDecimal,
    @Decimal(5, 0) mpdau_decimal_5_0: BigDecimal,
    fprfm_string: String,
    mandt_string: String,
    yystawn_dte_string: String,
    casnr_string: String,
    cuobv_string: String,
    @Decimal(13, 3) vbamg_decimal_13_3: BigDecimal,
    dismm_string: String,
    lfrhy_string: String,
    @Decimal(5, 2) vbeaz_decimal_5_2: BigDecimal,
    @Decimal(3, 1) lgrad_decimal_3_1: BigDecimal,
    qmata_string: String,
    @Decimal(13, 3) scm_conhap_out_decimal_13_3: BigDecimal,
    prefe_string: String,
    stdpd_string: String,
    fxpru_string: String,
    ekgrp_string: String,
    @Decimal(13, 3) scm_safty_v_decimal_13_3: BigDecimal,
    scm_peg_strategy_string: String,
    perkz_string: String,
    yystoreclass_string: String,
    @Decimal(3, 0) scm_reldt_decimal_3_0: BigDecimal,
    stawn_string: String,
    @Decimal(11, 0) scm_target_dur_decimal_11_0: BigDecimal,
    sgt_defsc_string: String,
    @Decimal(3, 1) sproz_decimal_3_1: BigDecimal,
    matgr_string: String,
    scm_matlocid_guid16_binary: Array[Byte],
    scm_whatbom_string: String,
    sgt_scope_string: String,
    convt_string: String,
    dpcbt_string: String,
    servg_string: String,
    @Decimal(13, 3) fixls_decimal_13_3: BigDecimal,
    apokz_string: String,
    @Decimal(13, 3) maxls_decimal_13_3: BigDecimal,
    @Decimal(3, 0) gi_pr_time_decimal_3_0: BigDecimal,
    kzagl_string: String,
    beskz_string: String,
    fevor_string: String,
    @Decimal(3, 0) dplho_decimal_3_0: BigDecimal,
    mmstd_string: String,
    kzkri_string: String,
    fsh_kzech_string: String,
    lagpr_string: String,
    scm_fixpeg_prod_set_string: String,
    @Decimal(13, 3) basmg_decimal_13_3: BigDecimal,
    nf_flag_string: String,
    @Decimal(3, 0) wzeit_decimal_3_0: BigDecimal,
    iuid_relevant_string: String,
    @Decimal(11, 2) losfx_decimal_11_2: BigDecimal,
    @Decimal(13, 3) scm_ppsaftystk_v_decimal_13_3: BigDecimal,
    kzdkz_string: String,
    scm_heur_id_string: String,
    sgt_chint_string: String,
    itark_string: String,
    preno_string: String,
    ausme_string: String,
    @Decimal(3, 0) dzeit_decimal_3_0: BigDecimal,
    ahdis_string: String,
    mtvfp_string: String,
    herbl_string: String,
    @Decimal(7, 2) temp_ctrl_max_decimal_7_2: BigDecimal,
    @Decimal(13, 3) glgmg_decimal_13_3: BigDecimal,
    vrbdt_string: String,
    zzccref_string: String,
    @Decimal(13, 3) minls_decimal_13_3: BigDecimal,
    yypkmatnr_string: String,
    @Decimal(13, 3) yystdpkqty_decimal_13_3: BigDecimal,
    @Decimal(13, 3) _cwm_trame_decimal_13_3: BigDecimal,
    altsl_string: String,
    yyppkey_string: String,
    lfgja_string: String,
    @Decimal(11, 0) scm_peg_future_alert_decimal_11_0: BigDecimal,
    @Decimal(13, 3) scm_conhap_decimal_13_3: BigDecimal,
    @Decimal(3, 0) quazt_decimal_3_0: BigDecimal,
    megru_string: String
) extends Entity

case class E_marc_part2(
    @PK _mk_org: String,
    dplpu_string: String,
    @Decimal(13, 3) yykanban_size_decimal_13_3: BigDecimal,
    aplal_string: String,
    sgt_prcm_string: String,
    ssqss_string: String,
    yysort1_string: String,
    kzdie_string: String,
    fsh_var_group_string: String,
    scm_iunit_string: String,
    @Decimal(13, 2) vktrw_decimal_13_2: BigDecimal,
    @Decimal(5, 0) maxlz_decimal_5_0: BigDecimal,
    @Decimal(5, 2) ruezt_decimal_5_2: BigDecimal,
    cons_procg_string: String,
    @Decimal(13, 3) bstrf_decimal_13_3: BigDecimal,
    mownr_string: String,
    gpnum_string: String,
    prenc_string: String,
    conf_gmsync_string: String,
    mfrgr_string: String,
    mcrue_string: String,
    @Decimal(13, 3) target_stock_decimal_13_3: BigDecimal,
    sgt_statc_string: String,
    xchar_string: String,
    kzaus_string: String,
    scm_tpop_string: String,
    scm_lsuom_string: String,
    scm_get_alerts_string: String,
    insmk_string: String,
    maabc_string: String,
    vrmod_string: String,
    @Decimal(13, 3) ltinc_decimal_13_3: BigDecimal,
    vspvb_string: String,
    @Decimal(13, 3) losgr_decimal_13_3: BigDecimal,
    scm_tstrid_string: String,
    vint1_string: String,
    verkz_string: String,
    preng_string: String,
    herkr_string: String,
    @Decimal(13, 3) zzordintr_decimal_13_3: BigDecimal,
    sobsl_string: String,
    arun_fix_batch_string: String,
    @Decimal(5, 2) vzusl_decimal_5_2: BigDecimal,
    @Decimal(5, 2) ausss_decimal_5_2: BigDecimal,
    @Decimal(5, 2) vrvez_decimal_5_2: BigDecimal,
    scm_rrp_type_string: String,
    _vso_r_pal_vend_string: String,
    awsls_string: String,
    periv_string: String,
    mtver_string: String,
    herkl_string: String,
    @Decimal(11, 0) scm_shlf_lfe_req_min_decimal_11_0: BigDecimal,
    @Decimal(13, 3) bstma_decimal_13_3: BigDecimal,
    @Decimal(5, 2) tranz_decimal_5_2: BigDecimal,
    scm_intsrc_prof_string: String,
    mmsta_string: String,
    kzbed_string: String,
    uid_iea_string: String,
    disgr_string: String,
    @Decimal(13, 3) eisbe_decimal_13_3: BigDecimal,
    disls_string: String,
    sfty_stk_meth_string: String,
    @Decimal(13, 3) scm_coninp_decimal_13_3: BigDecimal,
    @Decimal(13, 3) scm_ndcostwa_decimal_13_3: BigDecimal,
    sernp_string: String,
    lvorm_string: String,
    @Decimal(13, 3) mabst_decimal_13_3: BigDecimal,
    mogru_string: String,
    min_troc_string: String,
    scm_profid_string: String,
    scm_prio_int: BoxedInt,
    @Decimal(3, 0) webaz_decimal_3_0: BigDecimal,
    shflg_string: String,
    expme_string: String,
    kautb_string: String,
    @Decimal(13, 3) _cwm_bwesb_decimal_13_3: BigDecimal,
    abcin_string: String,
    scm_matlocid_guid22_string: String,
    @Decimal(13, 3) _cwm_umlmc_decimal_13_3: BigDecimal,
    rwpro_string: String,
    fvidk_string: String,
    @Decimal(5, 0) prfrq_decimal_5_0: BigDecimal,
    qssys_string: String,
    ncost_string: String,
    qzgtp_string: String,
    xchpf_string: String,
    @Decimal(13, 3) scm_scost_decimal_13_3: BigDecimal,
    yymove_velocity_string: String,
    @Decimal(3, 0) yyvap_decimal_3_0: BigDecimal,
    fabkz_string: String,
    scm_rrp_sel_group_string: String,
    strgr_string: String,
    @Decimal(13, 3) trame_decimal_13_3: BigDecimal,
    @Decimal(3, 1) uneto_decimal_3_1: BigDecimal
) extends Entity

object marc extends TableSpec[Joined[E_marc_part1, E_marc_part2]](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_marc(prefix: String) extends ColumnWithNameAccessor {
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
  val uomgr_string: ColumnWithName = ColumnWithName(prefix + "uomgr_string")
  val scm_sft_lock_string: ColumnWithName = ColumnWithName(prefix + "scm_sft_lock_string")
  val _sapmp_tolprmi_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "_sapmp_tolprmi_decimal_3_1")
  val scm_repsafty_v_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_repsafty_v_decimal_13_3")
  val ueeto_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "ueeto_decimal_3_1")
  val matnr_string: ColumnWithName = ColumnWithName(prefix + "matnr_string")
  val yystk_indicator_string: ColumnWithName = ColumnWithName(prefix + "yystk_indicator_string")
  val scm_giprt_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_giprt_decimal_11_0")
  val yyprodsts_string: ColumnWithName = ColumnWithName(prefix + "yyprodsts_string")
  val resvp_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "resvp_decimal_3_0")
  val iuid_type_string: ColumnWithName = ColumnWithName(prefix + "iuid_type_string")
  val bstmi_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bstmi_decimal_13_3")
  val lgpro_string: ColumnWithName = ColumnWithName(prefix + "lgpro_string")
  val ueetk_string: ColumnWithName = ColumnWithName(prefix + "ueetk_string")
  val yystdpkmsg_string: ColumnWithName = ColumnWithName(prefix + "yystdpkmsg_string")
  val zzprojn_string: ColumnWithName = ColumnWithName(prefix + "zzprojn_string")
  val ladgr_string: ColumnWithName = ColumnWithName(prefix + "ladgr_string")
  val diber_string: ColumnWithName = ColumnWithName(prefix + "diber_string")
  val rgekz_string: ColumnWithName = ColumnWithName(prefix + "rgekz_string")
  val atpkz_string: ColumnWithName = ColumnWithName(prefix + "atpkz_string")
  val sgt_stk_prt_string: ColumnWithName = ColumnWithName(prefix + "sgt_stk_prt_string")
  val zzstdpkmsg_string: ColumnWithName = ColumnWithName(prefix + "zzstdpkmsg_string")
  val schgt_string: ColumnWithName = ColumnWithName(prefix + "schgt_string")
  val zzqcf_decimal_13_6: ColumnWithName = ColumnWithName(prefix + "zzqcf_decimal_13_6")
  val shpro_string: ColumnWithName = ColumnWithName(prefix + "shpro_string")
  val vkumc_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "vkumc_decimal_13_2")
  val ocmpf_string: ColumnWithName = ColumnWithName(prefix + "ocmpf_string")
  val scm_ges_bst_use_string: ColumnWithName = ColumnWithName(prefix + "scm_ges_bst_use_string")
  val _vso_r_fork_dir_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_fork_dir_string")
  val prctr_string: ColumnWithName = ColumnWithName(prefix + "prctr_string")
  val otype_string: ColumnWithName = ColumnWithName(prefix + "otype_string")
  val lizyk_string: ColumnWithName = ColumnWithName(prefix + "lizyk_string")
  val sobsk_string: ColumnWithName = ColumnWithName(prefix + "sobsk_string")
  val scm_ppsaftystk_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_ppsaftystk_decimal_13_3")
  val kzech_string: ColumnWithName = ColumnWithName(prefix + "kzech_string")
  val mrppp_string: ColumnWithName = ColumnWithName(prefix + "mrppp_string")
  val lfmon_string: ColumnWithName = ColumnWithName(prefix + "lfmon_string")
  val dummy_plnt_incl_eew_ps_string: ColumnWithName = ColumnWithName(prefix + "dummy_plnt_incl_eew_ps_string")
  val vint2_string: ColumnWithName = ColumnWithName(prefix + "vint2_string")
  val temp_uom_string: ColumnWithName = ColumnWithName(prefix + "temp_uom_string")
  val kausf_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "kausf_decimal_5_2")
  val wstgh_decimal_9_0: ColumnWithName = ColumnWithName(prefix + "wstgh_decimal_9_0")
  val plnnr_string: ColumnWithName = ColumnWithName(prefix + "plnnr_string")
  val objid_string: ColumnWithName = ColumnWithName(prefix + "objid_string")
  val usequ_string: ColumnWithName = ColumnWithName(prefix + "usequ_string")
  val fsh_mg_arun_req_string: ColumnWithName = ColumnWithName(prefix + "fsh_mg_arun_req_string")
  val umlmc_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "umlmc_decimal_13_3")
  val lzeih_string: ColumnWithName = ColumnWithName(prefix + "lzeih_string")
  val sauft_string: ColumnWithName = ColumnWithName(prefix + "sauft_string")
  val scm_reord_v_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_reord_v_decimal_13_3")
  val loggr_string: ColumnWithName = ColumnWithName(prefix + "loggr_string")
  val copam_string: ColumnWithName = ColumnWithName(prefix + "copam_string")
  val fsh_calendar_group_string: ColumnWithName = ColumnWithName(prefix + "fsh_calendar_group_string")
  val scm_scost_prcnt_decimal_3_2: ColumnWithName = ColumnWithName(prefix + "scm_scost_prcnt_decimal_3_2")
  val bwtty_string: ColumnWithName = ColumnWithName(prefix + "bwtty_string")
  val vkglg_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "vkglg_decimal_13_2")
  val kordb_string: ColumnWithName = ColumnWithName(prefix + "kordb_string")
  val scm_ndcostwe_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_ndcostwe_decimal_13_3")
  val sgt_mmstd_string: ColumnWithName = ColumnWithName(prefix + "sgt_mmstd_string")
  val yyordintr_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "yyordintr_decimal_13_3")
  val nkmpr_string: ColumnWithName = ColumnWithName(prefix + "nkmpr_string")
  val profil_string: ColumnWithName = ColumnWithName(prefix + "profil_string")
  val autru_string: ColumnWithName = ColumnWithName(prefix + "autru_string")
  val kzppv_string: ColumnWithName = ColumnWithName(prefix + "kzppv_string")
  val fhori_string: ColumnWithName = ColumnWithName(prefix + "fhori_string")
  val scm_hunit_string: ColumnWithName = ColumnWithName(prefix + "scm_hunit_string")
  val yymin_ord_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "yymin_ord_decimal_13_3")
  val bwscl_string: ColumnWithName = ColumnWithName(prefix + "bwscl_string")
  val qmatv_string: ColumnWithName = ColumnWithName(prefix + "qmatv_string")
  val ccfix_string: ColumnWithName = ColumnWithName(prefix + "ccfix_string")
  val prend_string: ColumnWithName = ColumnWithName(prefix + "prend_string")
  val pstat_string: ColumnWithName = ColumnWithName(prefix + "pstat_string")
  val miskz_string: ColumnWithName = ColumnWithName(prefix + "miskz_string")
  val steuc_string: ColumnWithName = ColumnWithName(prefix + "steuc_string")
  val prene_string: ColumnWithName = ColumnWithName(prefix + "prene_string")
  val plnty_string: ColumnWithName = ColumnWithName(prefix + "plnty_string")
  val vrbfk_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "vrbfk_decimal_4_2")
  val scm_peg_past_alert_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_peg_past_alert_decimal_11_0")
  val abfac_decimal_2_1: ColumnWithName = ColumnWithName(prefix + "abfac_decimal_2_1")
  val scm_thruput_time_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_thruput_time_decimal_11_0")
  val sgt_mrpsi_string: ColumnWithName = ColumnWithName(prefix + "sgt_mrpsi_string")
  val indus_string: ColumnWithName = ColumnWithName(prefix + "indus_string")
  val compl_string: ColumnWithName = ColumnWithName(prefix + "compl_string")
  val excise_tax_rlvnce_string: ColumnWithName = ColumnWithName(prefix + "excise_tax_rlvnce_string")
  val frtme_string: ColumnWithName = ColumnWithName(prefix + "frtme_string")
  val dplfs_string: ColumnWithName = ColumnWithName(prefix + "dplfs_string")
  val kzpro_string: ColumnWithName = ColumnWithName(prefix + "kzpro_string")
  val esppflg_string: ColumnWithName = ColumnWithName(prefix + "esppflg_string")
  val uchkz_string: ColumnWithName = ColumnWithName(prefix + "uchkz_string")
  val ausdt_string: ColumnWithName = ColumnWithName(prefix + "ausdt_string")
  val scm_min_pass_amount_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "scm_min_pass_amount_decimal_15_3")
  val scm_hunit_out_string: ColumnWithName = ColumnWithName(prefix + "scm_hunit_out_string")
  val umrsl_string: ColumnWithName = ColumnWithName(prefix + "umrsl_string")
  val pfrei_string: ColumnWithName = ColumnWithName(prefix + "pfrei_string")
  val scm_grprt_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_grprt_decimal_11_0")
  val mdach_string: ColumnWithName = ColumnWithName(prefix + "mdach_string")
  val scm_res_net_name_string: ColumnWithName = ColumnWithName(prefix + "scm_res_net_name_string")
  val kzkup_string: ColumnWithName = ColumnWithName(prefix + "kzkup_string")
  val scm_shelf_life_loc_string: ColumnWithName = ColumnWithName(prefix + "scm_shelf_life_loc_string")
  val scm_sspen_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_sspen_decimal_13_3")
  val vrbmt_string: ColumnWithName = ColumnWithName(prefix + "vrbmt_string")
  val dispr_string: ColumnWithName = ColumnWithName(prefix + "dispr_string")
  val ucmat_string: ColumnWithName = ColumnWithName(prefix + "ucmat_string")
  val scm_package_id_string: ColumnWithName = ColumnWithName(prefix + "scm_package_id_string")
  val sbdkz_string: ColumnWithName = ColumnWithName(prefix + "sbdkz_string")
  val scm_peg_wo_alert_fst_string: ColumnWithName = ColumnWithName(prefix + "scm_peg_wo_alert_fst_string")
  val kzpsp_string: ColumnWithName = ColumnWithName(prefix + "kzpsp_string")
  val temp_ctrl_min_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "temp_ctrl_min_decimal_7_2")
  val scm_shelf_life_dur_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_shelf_life_dur_decimal_11_0")
  val bwesb_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bwesb_decimal_13_3")
  val rdprf_string: ColumnWithName = ColumnWithName(prefix + "rdprf_string")
  val multiple_ekgrp_string: ColumnWithName = ColumnWithName(prefix + "multiple_ekgrp_string")
  val scm_reord_dur_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_reord_dur_decimal_11_0")
  val max_troc_string: ColumnWithName = ColumnWithName(prefix + "max_troc_string")
  val scm_proc_cost_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_proc_cost_decimal_13_3")
  val vrbwk_string: ColumnWithName = ColumnWithName(prefix + "vrbwk_string")
  val yyplantpn_string: ColumnWithName = ColumnWithName(prefix + "yyplantpn_string")
  val stlan_string: ColumnWithName = ColumnWithName(prefix + "stlan_string")
  val zzplantref_string: ColumnWithName = ColumnWithName(prefix + "zzplantref_string")
  val scm_ges_mng_use_string: ColumnWithName = ColumnWithName(prefix + "scm_ges_mng_use_string")
  val yymax_ord_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "yymax_ord_decimal_13_3")
  val jitprodnconfprofile_string: ColumnWithName = ColumnWithName(prefix + "jitprodnconfprofile_string")
  val bearz_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "bearz_decimal_5_2")
  val scm_repsafty_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_repsafty_decimal_13_3")
  val yfamly_string: ColumnWithName = ColumnWithName(prefix + "yfamly_string")
  val scm_maturity_dur_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_maturity_dur_decimal_11_0")
  val stlal_string: ColumnWithName = ColumnWithName(prefix + "stlal_string")
  val _sttpec_servalid_string: ColumnWithName = ColumnWithName(prefix + "_sttpec_servalid_string")
  val ref_schema_string: ColumnWithName = ColumnWithName(prefix + "ref_schema_string")
  val plifz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "plifz_decimal_3_0")
  val sfcpf_string: ColumnWithName = ColumnWithName(prefix + "sfcpf_string")
  val fxhor_string: ColumnWithName = ColumnWithName(prefix + "fxhor_string")
  val scm_stra1_string: ColumnWithName = ColumnWithName(prefix + "scm_stra1_string")
  val yypickstrg_string: ColumnWithName = ColumnWithName(prefix + "yypickstrg_string")
  val _sapmp_tolprpl_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "_sapmp_tolprpl_decimal_3_1")
  val bstfe_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bstfe_decimal_13_3")
  val auftl_string: ColumnWithName = ColumnWithName(prefix + "auftl_string")
  val xmcng_string: ColumnWithName = ColumnWithName(prefix + "xmcng_string")
  val kzkfk_string: ColumnWithName = ColumnWithName(prefix + "kzkfk_string")
  val fsh_seaim_string: ColumnWithName = ColumnWithName(prefix + "fsh_seaim_string")
  val nfmat_string: ColumnWithName = ColumnWithName(prefix + "nfmat_string")
  val ppskz_string: ColumnWithName = ColumnWithName(prefix + "ppskz_string")
  val takzt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "takzt_decimal_3_0")
  val _vso_r_pkgrp_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_pkgrp_string")
  val _vso_r_lane_num_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_lane_num_string")
  val minbe_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "minbe_decimal_13_3")
  val ffrei_string: ColumnWithName = ColumnWithName(prefix + "ffrei_string")
  val dispo_string: ColumnWithName = ColumnWithName(prefix + "dispo_string")
  val yysort2_string: ColumnWithName = ColumnWithName(prefix + "yysort2_string")
  val gpmkz_string: ColumnWithName = ColumnWithName(prefix + "gpmkz_string")
  val sgt_mrp_atp_status_string: ColumnWithName = ColumnWithName(prefix + "sgt_mrp_atp_status_string")
  val eprio_string: ColumnWithName = ColumnWithName(prefix + "eprio_string")
  val sgt_covs_string: ColumnWithName = ColumnWithName(prefix + "sgt_covs_string")
  val yyavg_daily_cns_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "yyavg_daily_cns_decimal_13_3")
  val yystdpkuom_string: ColumnWithName = ColumnWithName(prefix + "yystdpkuom_string")
  val lgfsb_string: ColumnWithName = ColumnWithName(prefix + "lgfsb_string")
  val scm_maxstock_v_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_maxstock_v_decimal_13_3")
  val sfepr_string: ColumnWithName = ColumnWithName(prefix + "sfepr_string")
  val plvar_string: ColumnWithName = ColumnWithName(prefix + "plvar_string")
  val shzet_string: ColumnWithName = ColumnWithName(prefix + "shzet_string")
  val werks_string: ColumnWithName = ColumnWithName(prefix + "werks_string")
  val scm_shlf_lfe_req_max_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_shlf_lfe_req_max_decimal_11_0")
  val cuobj_string: ColumnWithName = ColumnWithName(prefix + "cuobj_string")
  val rotation_date_string: ColumnWithName = ColumnWithName(prefix + "rotation_date_string")
  val eislo_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "eislo_decimal_13_3")
  val mpdau_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "mpdau_decimal_5_0")
  val fprfm_string: ColumnWithName = ColumnWithName(prefix + "fprfm_string")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val yystawn_dte_string: ColumnWithName = ColumnWithName(prefix + "yystawn_dte_string")
  val casnr_string: ColumnWithName = ColumnWithName(prefix + "casnr_string")
  val cuobv_string: ColumnWithName = ColumnWithName(prefix + "cuobv_string")
  val vbamg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "vbamg_decimal_13_3")
  val dismm_string: ColumnWithName = ColumnWithName(prefix + "dismm_string")
  val lfrhy_string: ColumnWithName = ColumnWithName(prefix + "lfrhy_string")
  val vbeaz_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "vbeaz_decimal_5_2")
  val lgrad_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "lgrad_decimal_3_1")
  val qmata_string: ColumnWithName = ColumnWithName(prefix + "qmata_string")
  val scm_conhap_out_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_conhap_out_decimal_13_3")
  val prefe_string: ColumnWithName = ColumnWithName(prefix + "prefe_string")
  val stdpd_string: ColumnWithName = ColumnWithName(prefix + "stdpd_string")
  val fxpru_string: ColumnWithName = ColumnWithName(prefix + "fxpru_string")
  val ekgrp_string: ColumnWithName = ColumnWithName(prefix + "ekgrp_string")
  val scm_safty_v_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_safty_v_decimal_13_3")
  val scm_peg_strategy_string: ColumnWithName = ColumnWithName(prefix + "scm_peg_strategy_string")
  val perkz_string: ColumnWithName = ColumnWithName(prefix + "perkz_string")
  val yystoreclass_string: ColumnWithName = ColumnWithName(prefix + "yystoreclass_string")
  val scm_reldt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "scm_reldt_decimal_3_0")
  val stawn_string: ColumnWithName = ColumnWithName(prefix + "stawn_string")
  val scm_target_dur_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_target_dur_decimal_11_0")
  val sgt_defsc_string: ColumnWithName = ColumnWithName(prefix + "sgt_defsc_string")
  val sproz_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "sproz_decimal_3_1")
  val matgr_string: ColumnWithName = ColumnWithName(prefix + "matgr_string")
  val scm_matlocid_guid16_binary: ColumnWithName = ColumnWithName(prefix + "scm_matlocid_guid16_binary")
  val scm_whatbom_string: ColumnWithName = ColumnWithName(prefix + "scm_whatbom_string")
  val sgt_scope_string: ColumnWithName = ColumnWithName(prefix + "sgt_scope_string")
  val convt_string: ColumnWithName = ColumnWithName(prefix + "convt_string")
  val dpcbt_string: ColumnWithName = ColumnWithName(prefix + "dpcbt_string")
  val servg_string: ColumnWithName = ColumnWithName(prefix + "servg_string")
  val fixls_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "fixls_decimal_13_3")
  val apokz_string: ColumnWithName = ColumnWithName(prefix + "apokz_string")
  val maxls_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "maxls_decimal_13_3")
  val gi_pr_time_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "gi_pr_time_decimal_3_0")
  val kzagl_string: ColumnWithName = ColumnWithName(prefix + "kzagl_string")
  val beskz_string: ColumnWithName = ColumnWithName(prefix + "beskz_string")
  val fevor_string: ColumnWithName = ColumnWithName(prefix + "fevor_string")
  val dplho_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "dplho_decimal_3_0")
  val mmstd_string: ColumnWithName = ColumnWithName(prefix + "mmstd_string")
  val kzkri_string: ColumnWithName = ColumnWithName(prefix + "kzkri_string")
  val fsh_kzech_string: ColumnWithName = ColumnWithName(prefix + "fsh_kzech_string")
  val lagpr_string: ColumnWithName = ColumnWithName(prefix + "lagpr_string")
  val scm_fixpeg_prod_set_string: ColumnWithName = ColumnWithName(prefix + "scm_fixpeg_prod_set_string")
  val basmg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "basmg_decimal_13_3")
  val nf_flag_string: ColumnWithName = ColumnWithName(prefix + "nf_flag_string")
  val wzeit_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "wzeit_decimal_3_0")
  val iuid_relevant_string: ColumnWithName = ColumnWithName(prefix + "iuid_relevant_string")
  val losfx_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "losfx_decimal_11_2")
  val scm_ppsaftystk_v_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_ppsaftystk_v_decimal_13_3")
  val kzdkz_string: ColumnWithName = ColumnWithName(prefix + "kzdkz_string")
  val scm_heur_id_string: ColumnWithName = ColumnWithName(prefix + "scm_heur_id_string")
  val sgt_chint_string: ColumnWithName = ColumnWithName(prefix + "sgt_chint_string")
  val itark_string: ColumnWithName = ColumnWithName(prefix + "itark_string")
  val preno_string: ColumnWithName = ColumnWithName(prefix + "preno_string")
  val ausme_string: ColumnWithName = ColumnWithName(prefix + "ausme_string")
  val dzeit_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "dzeit_decimal_3_0")
  val ahdis_string: ColumnWithName = ColumnWithName(prefix + "ahdis_string")
  val mtvfp_string: ColumnWithName = ColumnWithName(prefix + "mtvfp_string")
  val herbl_string: ColumnWithName = ColumnWithName(prefix + "herbl_string")
  val temp_ctrl_max_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "temp_ctrl_max_decimal_7_2")
  val glgmg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "glgmg_decimal_13_3")
  val vrbdt_string: ColumnWithName = ColumnWithName(prefix + "vrbdt_string")
  val zzccref_string: ColumnWithName = ColumnWithName(prefix + "zzccref_string")
  val minls_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "minls_decimal_13_3")
  val yypkmatnr_string: ColumnWithName = ColumnWithName(prefix + "yypkmatnr_string")
  val yystdpkqty_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "yystdpkqty_decimal_13_3")
  val _cwm_trame_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_cwm_trame_decimal_13_3")
  val altsl_string: ColumnWithName = ColumnWithName(prefix + "altsl_string")
  val yyppkey_string: ColumnWithName = ColumnWithName(prefix + "yyppkey_string")
  val lfgja_string: ColumnWithName = ColumnWithName(prefix + "lfgja_string")
  val scm_peg_future_alert_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_peg_future_alert_decimal_11_0")
  val scm_conhap_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_conhap_decimal_13_3")
  val quazt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "quazt_decimal_3_0")
  val megru_string: ColumnWithName = ColumnWithName(prefix + "megru_string")
  val dplpu_string: ColumnWithName = ColumnWithName(prefix + "dplpu_string")
  val yykanban_size_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "yykanban_size_decimal_13_3")
  val aplal_string: ColumnWithName = ColumnWithName(prefix + "aplal_string")
  val sgt_prcm_string: ColumnWithName = ColumnWithName(prefix + "sgt_prcm_string")
  val ssqss_string: ColumnWithName = ColumnWithName(prefix + "ssqss_string")
  val yysort1_string: ColumnWithName = ColumnWithName(prefix + "yysort1_string")
  val kzdie_string: ColumnWithName = ColumnWithName(prefix + "kzdie_string")
  val fsh_var_group_string: ColumnWithName = ColumnWithName(prefix + "fsh_var_group_string")
  val scm_iunit_string: ColumnWithName = ColumnWithName(prefix + "scm_iunit_string")
  val vktrw_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "vktrw_decimal_13_2")
  val maxlz_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "maxlz_decimal_5_0")
  val ruezt_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "ruezt_decimal_5_2")
  val cons_procg_string: ColumnWithName = ColumnWithName(prefix + "cons_procg_string")
  val bstrf_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bstrf_decimal_13_3")
  val mownr_string: ColumnWithName = ColumnWithName(prefix + "mownr_string")
  val gpnum_string: ColumnWithName = ColumnWithName(prefix + "gpnum_string")
  val prenc_string: ColumnWithName = ColumnWithName(prefix + "prenc_string")
  val conf_gmsync_string: ColumnWithName = ColumnWithName(prefix + "conf_gmsync_string")
  val mfrgr_string: ColumnWithName = ColumnWithName(prefix + "mfrgr_string")
  val mcrue_string: ColumnWithName = ColumnWithName(prefix + "mcrue_string")
  val target_stock_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "target_stock_decimal_13_3")
  val sgt_statc_string: ColumnWithName = ColumnWithName(prefix + "sgt_statc_string")
  val xchar_string: ColumnWithName = ColumnWithName(prefix + "xchar_string")
  val kzaus_string: ColumnWithName = ColumnWithName(prefix + "kzaus_string")
  val scm_tpop_string: ColumnWithName = ColumnWithName(prefix + "scm_tpop_string")
  val scm_lsuom_string: ColumnWithName = ColumnWithName(prefix + "scm_lsuom_string")
  val scm_get_alerts_string: ColumnWithName = ColumnWithName(prefix + "scm_get_alerts_string")
  val insmk_string: ColumnWithName = ColumnWithName(prefix + "insmk_string")
  val maabc_string: ColumnWithName = ColumnWithName(prefix + "maabc_string")
  val vrmod_string: ColumnWithName = ColumnWithName(prefix + "vrmod_string")
  val ltinc_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "ltinc_decimal_13_3")
  val vspvb_string: ColumnWithName = ColumnWithName(prefix + "vspvb_string")
  val losgr_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "losgr_decimal_13_3")
  val scm_tstrid_string: ColumnWithName = ColumnWithName(prefix + "scm_tstrid_string")
  val vint1_string: ColumnWithName = ColumnWithName(prefix + "vint1_string")
  val verkz_string: ColumnWithName = ColumnWithName(prefix + "verkz_string")
  val preng_string: ColumnWithName = ColumnWithName(prefix + "preng_string")
  val herkr_string: ColumnWithName = ColumnWithName(prefix + "herkr_string")
  val zzordintr_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "zzordintr_decimal_13_3")
  val sobsl_string: ColumnWithName = ColumnWithName(prefix + "sobsl_string")
  val arun_fix_batch_string: ColumnWithName = ColumnWithName(prefix + "arun_fix_batch_string")
  val vzusl_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "vzusl_decimal_5_2")
  val ausss_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "ausss_decimal_5_2")
  val vrvez_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "vrvez_decimal_5_2")
  val scm_rrp_type_string: ColumnWithName = ColumnWithName(prefix + "scm_rrp_type_string")
  val _vso_r_pal_vend_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_pal_vend_string")
  val awsls_string: ColumnWithName = ColumnWithName(prefix + "awsls_string")
  val periv_string: ColumnWithName = ColumnWithName(prefix + "periv_string")
  val mtver_string: ColumnWithName = ColumnWithName(prefix + "mtver_string")
  val herkl_string: ColumnWithName = ColumnWithName(prefix + "herkl_string")
  val scm_shlf_lfe_req_min_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_shlf_lfe_req_min_decimal_11_0")
  val bstma_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bstma_decimal_13_3")
  val tranz_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "tranz_decimal_5_2")
  val scm_intsrc_prof_string: ColumnWithName = ColumnWithName(prefix + "scm_intsrc_prof_string")
  val mmsta_string: ColumnWithName = ColumnWithName(prefix + "mmsta_string")
  val kzbed_string: ColumnWithName = ColumnWithName(prefix + "kzbed_string")
  val uid_iea_string: ColumnWithName = ColumnWithName(prefix + "uid_iea_string")
  val disgr_string: ColumnWithName = ColumnWithName(prefix + "disgr_string")
  val eisbe_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "eisbe_decimal_13_3")
  val disls_string: ColumnWithName = ColumnWithName(prefix + "disls_string")
  val sfty_stk_meth_string: ColumnWithName = ColumnWithName(prefix + "sfty_stk_meth_string")
  val scm_coninp_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_coninp_decimal_13_3")
  val scm_ndcostwa_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_ndcostwa_decimal_13_3")
  val sernp_string: ColumnWithName = ColumnWithName(prefix + "sernp_string")
  val lvorm_string: ColumnWithName = ColumnWithName(prefix + "lvorm_string")
  val mabst_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "mabst_decimal_13_3")
  val mogru_string: ColumnWithName = ColumnWithName(prefix + "mogru_string")
  val min_troc_string: ColumnWithName = ColumnWithName(prefix + "min_troc_string")
  val scm_profid_string: ColumnWithName = ColumnWithName(prefix + "scm_profid_string")
  val scm_prio_int: ColumnWithName = ColumnWithName(prefix + "scm_prio_int")
  val webaz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "webaz_decimal_3_0")
  val shflg_string: ColumnWithName = ColumnWithName(prefix + "shflg_string")
  val expme_string: ColumnWithName = ColumnWithName(prefix + "expme_string")
  val kautb_string: ColumnWithName = ColumnWithName(prefix + "kautb_string")
  val _cwm_bwesb_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_cwm_bwesb_decimal_13_3")
  val abcin_string: ColumnWithName = ColumnWithName(prefix + "abcin_string")
  val scm_matlocid_guid22_string: ColumnWithName = ColumnWithName(prefix + "scm_matlocid_guid22_string")
  val _cwm_umlmc_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_cwm_umlmc_decimal_13_3")
  val rwpro_string: ColumnWithName = ColumnWithName(prefix + "rwpro_string")
  val fvidk_string: ColumnWithName = ColumnWithName(prefix + "fvidk_string")
  val prfrq_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "prfrq_decimal_5_0")
  val qssys_string: ColumnWithName = ColumnWithName(prefix + "qssys_string")
  val ncost_string: ColumnWithName = ColumnWithName(prefix + "ncost_string")
  val qzgtp_string: ColumnWithName = ColumnWithName(prefix + "qzgtp_string")
  val xchpf_string: ColumnWithName = ColumnWithName(prefix + "xchpf_string")
  val scm_scost_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "scm_scost_decimal_13_3")
  val yymove_velocity_string: ColumnWithName = ColumnWithName(prefix + "yymove_velocity_string")
  val yyvap_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "yyvap_decimal_3_0")
  val fabkz_string: ColumnWithName = ColumnWithName(prefix + "fabkz_string")
  val scm_rrp_sel_group_string: ColumnWithName = ColumnWithName(prefix + "scm_rrp_sel_group_string")
  val strgr_string: ColumnWithName = ColumnWithName(prefix + "strgr_string")
  val trame_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "trame_decimal_13_3")
  val uneto_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "uneto_decimal_3_1")
}

object C_marc extends C_marc("") {
  def as(alias: String): C_marc = new C_marc(alias + ".")
}

// AUTO GENERATED:END
