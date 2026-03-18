// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_pbr

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
    zzwtrpla_string: String,
    vers_active_string: String,
    @Decimal(3, 1) ueeto_decimal_3_1: BigDecimal,
    @Decimal(13, 3) zzwtrqty_decimal_13_3: BigDecimal,
    @Decimal(11, 0) pps_grprt_decimal_11_0: BigDecimal,
    matnr_string: String,
    zzaeim_string: String,
    pps_hunit_out_string: String,
    @Decimal(3, 1) _sapmp_tolprmi_decimal_3_1: BigDecimal,
    zzvan_string: String,
    @Decimal(3, 0) resvp_decimal_3_0: BigDecimal,
    @Decimal(13, 3) zzexc_decimal_13_3: BigDecimal,
    pps_peg_strategy_string: String,
    iuid_type_string: String,
    @Decimal(13, 3) bstmi_decimal_13_3: BigDecimal,
    ueetk_string: String,
    lgpro_string: String,
    sgt_mmstd_date: Date,
    pps_strategy_string: String,
    diber_string: String,
    rgekz_string: String,
    zzfpproset_string: String,
    atpkz_string: String,
    shpro_string: String,
    sgt_stk_prt_string: String,
    @Decimal(13, 3) pps_conhap_out_decimal_13_3: BigDecimal,
    schgt_string: String,
    ladgr_string: String,
    zzwwashm_string: String,
    @Decimal(13, 2) vkumc_decimal_13_2: BigDecimal,
    ocmpf_string: String,
    _vso_r_fork_dir_string: String,
    prctr_string: String,
    otype_string: String,
    lfmon_string: String,
    zzpgnodyn_string: String,
    lizyk_string: String,
    sobsk_string: String,
    kzech_string: String,
    mrppp_string: String,
    vint2_string: String,
    @Decimal(5, 2) kausf_decimal_5_2: BigDecimal,
    @Decimal(9, 0) wstgh_decimal_9_0: BigDecimal,
    zzvkz_string: String,
    pps_hunit_string: String,
    sauft_string: String,
    @Decimal(11, 0) zzpgpasale_decimal_11_0: BigDecimal,
    plnnr_string: String,
    usequ_string: String,
    fsh_mg_arun_req_string: String,
    ausdt_date: Date,
    zzpkzshiph_string: String,
    @Decimal(13, 3) umlmc_decimal_13_3: BigDecimal,
    lzeih_string: String,
    objid_string: String,
    pps_fixpeg_string: String,
    loggr_string: String,
    copam_string: String,
    fsh_calendar_group_string: String,
    bwtty_string: String,
    @Decimal(13, 2) vkglg_decimal_13_2: BigDecimal,
    zznpgstqua_string: String,
    zzwwashp_string: String,
    profil_string: String,
    autru_string: String,
    kzppv_string: String,
    zzwhatbom_string: String,
    kordb_string: String,
    fhori_string: String,
    @Decimal(2, 1) abfac_decimal_2_1: BigDecimal,
    zzadfcheck_string: String,
    bwscl_string: String,
    qmatv_string: String,
    ccfix_string: String,
    @Decimal(13, 3) zzwwbqty_decimal_13_3: BigDecimal,
    pstat_string: String,
    pps_heur_id_string: String,
    miskz_string: String,
    steuc_string: String,
    prene_string: String,
    plnty_string: String,
    @Decimal(4, 2) vrbfk_decimal_4_2: BigDecimal,
    sgt_mrpsi_string: String,
    uchkz_string: String,
    indus_string: String,
    umrsl_string: String,
    pfrei_string: String,
    frtme_string: String,
    zztxtwk_string: String,
    zzdokuk_string: String,
    dplfs_string: String,
    kzpro_string: String,
    @Decimal(11, 2) zzlom_decimal_11_2: BigDecimal,
    compl_string: String,
    zzplantref1_string: String,
    zzverfp_string: String,
    zzrrptype_string: String,
    kzkup_string: String,
    @Decimal(11, 2) zzvvm_decimal_11_2: BigDecimal,
    vrbmt_string: String,
    dispr_string: String,
    zzdatuw_date: Date,
    ucmat_string: String,
    nkmpr_date: Date,
    mdach_string: String,
    @Decimal(13, 3) zzobs_decimal_13_3: BigDecimal,
    max_troc_string: String,
    kzpsp_string: String,
    @Decimal(13, 3) bwesb_decimal_13_3: BigDecimal,
    zzwtrbcl_string: String,
    rdprf_string: String,
    multiple_ekgrp_string: String,
    zzccref1_string: String,
    sbdkz_string: String,
    vrbwk_string: String,
    stlan_string: String,
    pps_atpcheck_string: String,
    stlal_string: String,
    @Decimal(13, 3) pps_conhap_decimal_13_3: BigDecimal,
    zzwtrmat_string: String,
    @Decimal(5, 2) bearz_decimal_5_2: BigDecimal,
    ref_schema_string: String,
    @Decimal(6, 3) zzqcf_decimal_6_3: BigDecimal,
    @Decimal(3, 0) plifz_decimal_3_0: BigDecimal,
    @Decimal(13, 3) zzgae_decimal_13_3: BigDecimal,
    sfcpf_string: String,
    fxhor_string: String,
    @Decimal(3, 1) _sapmp_tolprpl_decimal_3_1: BigDecimal,
    @Decimal(13, 3) bstfe_decimal_13_3: BigDecimal,
    auftl_string: String,
    xmcng_string: String,
    mmstd_date: Date,
    kzkfk_string: String,
    fsh_seaim_string: String,
    nfmat_string: String,
    ppskz_string: String,
    zzsttransit_string: String,
    @Decimal(3, 0) takzt_decimal_3_0: BigDecimal,
    _vso_r_pkgrp_string: String,
    _vso_r_lane_num_string: String,
    @Decimal(13, 3) minbe_decimal_13_3: BigDecimal,
    ffrei_string: String,
    dispo_string: String,
    gpmkz_string: String,
    sgt_mrp_atp_status_string: String,
    eprio_string: String,
    sgt_covs_string: String,
    zzgetalefp_string: String,
    sfepr_string: String,
    plvar_string: String,
    zzfnr1w_string: String,
    zzgesbsuse_string: String,
    zztargemet_string: String,
    vers_cseg_string: String,
    lgfsb_string: String,
    shzet_string: String,
    werks_string: String,
    zzplgru_string: String,
    cuobj_string: String,
    rotation_date_string: String,
    zzpkzprodh_string: String,
    @Decimal(13, 3) eislo_decimal_13_3: BigDecimal,
    @Decimal(5, 0) mpdau_decimal_5_0: BigDecimal,
    fprfm_string: String,
    mandt_string: String,
    casnr_string: String,
    zzpgstrest_string: String,
    qmata_string: String,
    cuobv_string: String,
    @Decimal(13, 3) vbamg_decimal_13_3: BigDecimal,
    dismm_string: String,
    lfrhy_string: String,
    @Decimal(5, 2) vbeaz_decimal_5_2: BigDecimal,
    zzaemp2_string: String,
    @Decimal(3, 1) lgrad_decimal_3_1: BigDecimal,
    prefe_string: String,
    stdpd_string: String,
    fxpru_string: String,
    ekgrp_string: String,
    zzshipf_string: String,
    @Decimal(11, 0) pps_giprt_decimal_11_0: BigDecimal,
    perkz_string: String,
    stawn_string: String,
    sgt_defsc_string: String,
    @Decimal(3, 1) sproz_decimal_3_1: BigDecimal,
    matgr_string: String,
    @Decimal(3, 0) zzconvh_decimal_3_0: BigDecimal,
    sgt_scope_string: String,
    convt_string: String,
    dpcbt_string: String,
    servg_string: String,
    @Decimal(13, 3) fixls_decimal_13_3: BigDecimal,
    zzabw_string: String,
    apokz_string: String,
    @Decimal(3, 0) gi_pr_time_decimal_3_0: BigDecimal,
    zzpgstrat_string: String,
    kzagl_string: String,
    beskz_string: String,
    fevor_string: String,
    @Decimal(3, 0) dplho_decimal_3_0: BigDecimal,
    kzkri_string: String,
    zzheurid_string: String,
    @Decimal(13, 3) maxls_decimal_13_3: BigDecimal,
    fsh_kzech_string: String,
    lagpr_string: String,
    zznpgstrsl_string: String,
    kzdkz_string: String,
    @Decimal(13, 3) basmg_decimal_13_3: BigDecimal,
    nf_flag_string: String,
    @Decimal(3, 0) wzeit_decimal_3_0: BigDecimal,
    iuid_relevant_string: String,
    @Decimal(11, 2) losfx_decimal_11_2: BigDecimal,
    pps_planning_type_string: String,
    sgt_chint_string: String,
    itark_string: String,
    preno_string: String,
    zzvdt_date: Date,
    ausme_string: String,
    @Decimal(3, 0) dzeit_decimal_3_0: BigDecimal,
    zzaeip_string: String,
    @Decimal(13, 3) glgmg_decimal_13_3: BigDecimal,
    zzgesmguse_string: String,
    ahdis_string: String,
    @Decimal(3, 0) zzprodh_decimal_3_0: BigDecimal,
    mtvfp_string: String,
    zzfeabw_string: String,
    herbl_string: String,
    @Decimal(13, 3) minls_decimal_13_3: BigDecimal,
    vrbdt_date: Date,
    altsl_string: String,
    lfgja_string: String,
    zzprodcent_string: String,
    @Decimal(3, 0) quazt_decimal_3_0: BigDecimal,
    megru_string: String,
    dplpu_string: String,
    aplal_string: String,
    zzfnr2w_string: String,
    @Decimal(11, 0) zzpgpasmax_decimal_11_0: BigDecimal,
    sgt_prcm_string: String,
    ssqss_string: String,
    kzdie_string: String
) extends Entity

case class E_marc_part2(
    @PK _mk_org: String,
    zzantr_string: String,
    fsh_var_group_string: String,
    zzchkmaxlz_string: String,
    @Decimal(13, 2) vktrw_decimal_13_2: BigDecimal,
    @Decimal(5, 0) maxlz_decimal_5_0: BigDecimal,
    @Decimal(5, 2) ruezt_decimal_5_2: BigDecimal,
    cons_procg_string: String,
    @Decimal(13, 3) bstrf_decimal_13_3: BigDecimal,
    mownr_string: String,
    @Decimal(11, 0) zzpgfutmax_decimal_11_0: BigDecimal,
    gpnum_string: String,
    prenc_string: String,
    mfrgr_string: String,
    @Decimal(11, 0) pps_peg_past_al_decimal_11_0: BigDecimal,
    @Decimal(13, 3) target_stock_decimal_13_3: BigDecimal,
    sgt_statc_string: String,
    xchar_string: String,
    kzaus_string: String,
    prend_date: Date,
    mcrue_string: String,
    zzwwbtyp_string: String,
    @Decimal(13, 3) losgr_decimal_13_3: BigDecimal,
    insmk_string: String,
    maabc_string: String,
    vrmod_string: String,
    @Decimal(13, 3) ltinc_decimal_13_3: BigDecimal,
    vspvb_string: String,
    vint1_string: String,
    verkz_string: String,
    herkr_string: String,
    sobsl_string: String,
    @Decimal(5, 2) vzusl_decimal_5_2: BigDecimal,
    @Decimal(5, 2) vrvez_decimal_5_2: BigDecimal,
    @Decimal(5, 2) ausss_decimal_5_2: BigDecimal,
    _casww_we_spart_string: String,
    zzwtrpwb_string: String,
    awsls_string: String,
    zzaemp1_string: String,
    periv_string: String,
    mtver_string: String,
    herkl_string: String,
    @Decimal(11, 0) zzpgfutale_decimal_11_0: BigDecimal,
    preng_date: Date,
    _vso_r_pal_vend_string: String,
    @Decimal(13, 3) bstma_decimal_13_3: BigDecimal,
    @Decimal(5, 2) tranz_decimal_5_2: BigDecimal,
    mmsta_string: String,
    kzbed_string: String,
    @Decimal(3, 0) zzshiph_decimal_3_0: BigDecimal,
    uid_iea_string: String,
    disgr_string: String,
    @Decimal(13, 3) eisbe_decimal_13_3: BigDecimal,
    disls_string: String,
    sernp_string: String,
    lvorm_string: String,
    @Decimal(13, 3) mabst_decimal_13_3: BigDecimal,
    mogru_string: String,
    min_troc_string: String,
    zzprodf_string: String,
    @Decimal(11, 0) pps_peg_fut_al_decimal_11_0: BigDecimal,
    @Decimal(3, 0) webaz_decimal_3_0: BigDecimal,
    shflg_string: String,
    zzpgstblok_string: String,
    kautb_string: String,
    zzastk_string: String,
    abcin_string: String,
    @Decimal(11, 2) zzvim_decimal_11_2: BigDecimal,
    expme_string: String,
    zzpgwoaler_string: String,
    rwpro_string: String,
    fvidk_string: String,
    @Decimal(5, 0) prfrq_decimal_5_0: BigDecimal,
    qssys_string: String,
    ncost_string: String,
    qzgtp_string: String,
    xchpf_string: String,
    strgr_string: String,
    @Decimal(11, 0) zztargedur_decimal_11_0: BigDecimal,
    fabkz_string: String,
    @Decimal(13, 3) trame_decimal_13_3: BigDecimal,
    @Decimal(3, 1) uneto_decimal_3_1: BigDecimal,
    sgt_mmstd_string: String,
    nkmpr_string: String,
    prend_string: String,
    ausdt_string: String,
    zzvdt_string: String,
    zzdatuw_string: String,
    mmstd_string: String,
    vrbdt_string: String,
    preng_string: String
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
  val zzwtrpla_string: ColumnWithName = ColumnWithName(prefix + "zzwtrpla_string")
  val vers_active_string: ColumnWithName = ColumnWithName(prefix + "vers_active_string")
  val ueeto_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "ueeto_decimal_3_1")
  val zzwtrqty_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "zzwtrqty_decimal_13_3")
  val pps_grprt_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "pps_grprt_decimal_11_0")
  val matnr_string: ColumnWithName = ColumnWithName(prefix + "matnr_string")
  val zzaeim_string: ColumnWithName = ColumnWithName(prefix + "zzaeim_string")
  val pps_hunit_out_string: ColumnWithName = ColumnWithName(prefix + "pps_hunit_out_string")
  val _sapmp_tolprmi_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "_sapmp_tolprmi_decimal_3_1")
  val zzvan_string: ColumnWithName = ColumnWithName(prefix + "zzvan_string")
  val resvp_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "resvp_decimal_3_0")
  val zzexc_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "zzexc_decimal_13_3")
  val pps_peg_strategy_string: ColumnWithName = ColumnWithName(prefix + "pps_peg_strategy_string")
  val iuid_type_string: ColumnWithName = ColumnWithName(prefix + "iuid_type_string")
  val bstmi_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bstmi_decimal_13_3")
  val ueetk_string: ColumnWithName = ColumnWithName(prefix + "ueetk_string")
  val lgpro_string: ColumnWithName = ColumnWithName(prefix + "lgpro_string")
  val sgt_mmstd_date: ColumnWithName = ColumnWithName(prefix + "sgt_mmstd_date")
  val pps_strategy_string: ColumnWithName = ColumnWithName(prefix + "pps_strategy_string")
  val diber_string: ColumnWithName = ColumnWithName(prefix + "diber_string")
  val rgekz_string: ColumnWithName = ColumnWithName(prefix + "rgekz_string")
  val zzfpproset_string: ColumnWithName = ColumnWithName(prefix + "zzfpproset_string")
  val atpkz_string: ColumnWithName = ColumnWithName(prefix + "atpkz_string")
  val shpro_string: ColumnWithName = ColumnWithName(prefix + "shpro_string")
  val sgt_stk_prt_string: ColumnWithName = ColumnWithName(prefix + "sgt_stk_prt_string")
  val pps_conhap_out_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "pps_conhap_out_decimal_13_3")
  val schgt_string: ColumnWithName = ColumnWithName(prefix + "schgt_string")
  val ladgr_string: ColumnWithName = ColumnWithName(prefix + "ladgr_string")
  val zzwwashm_string: ColumnWithName = ColumnWithName(prefix + "zzwwashm_string")
  val vkumc_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "vkumc_decimal_13_2")
  val ocmpf_string: ColumnWithName = ColumnWithName(prefix + "ocmpf_string")
  val _vso_r_fork_dir_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_fork_dir_string")
  val prctr_string: ColumnWithName = ColumnWithName(prefix + "prctr_string")
  val otype_string: ColumnWithName = ColumnWithName(prefix + "otype_string")
  val lfmon_string: ColumnWithName = ColumnWithName(prefix + "lfmon_string")
  val zzpgnodyn_string: ColumnWithName = ColumnWithName(prefix + "zzpgnodyn_string")
  val lizyk_string: ColumnWithName = ColumnWithName(prefix + "lizyk_string")
  val sobsk_string: ColumnWithName = ColumnWithName(prefix + "sobsk_string")
  val kzech_string: ColumnWithName = ColumnWithName(prefix + "kzech_string")
  val mrppp_string: ColumnWithName = ColumnWithName(prefix + "mrppp_string")
  val vint2_string: ColumnWithName = ColumnWithName(prefix + "vint2_string")
  val kausf_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "kausf_decimal_5_2")
  val wstgh_decimal_9_0: ColumnWithName = ColumnWithName(prefix + "wstgh_decimal_9_0")
  val zzvkz_string: ColumnWithName = ColumnWithName(prefix + "zzvkz_string")
  val pps_hunit_string: ColumnWithName = ColumnWithName(prefix + "pps_hunit_string")
  val sauft_string: ColumnWithName = ColumnWithName(prefix + "sauft_string")
  val zzpgpasale_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "zzpgpasale_decimal_11_0")
  val plnnr_string: ColumnWithName = ColumnWithName(prefix + "plnnr_string")
  val usequ_string: ColumnWithName = ColumnWithName(prefix + "usequ_string")
  val fsh_mg_arun_req_string: ColumnWithName = ColumnWithName(prefix + "fsh_mg_arun_req_string")
  val ausdt_date: ColumnWithName = ColumnWithName(prefix + "ausdt_date")
  val zzpkzshiph_string: ColumnWithName = ColumnWithName(prefix + "zzpkzshiph_string")
  val umlmc_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "umlmc_decimal_13_3")
  val lzeih_string: ColumnWithName = ColumnWithName(prefix + "lzeih_string")
  val objid_string: ColumnWithName = ColumnWithName(prefix + "objid_string")
  val pps_fixpeg_string: ColumnWithName = ColumnWithName(prefix + "pps_fixpeg_string")
  val loggr_string: ColumnWithName = ColumnWithName(prefix + "loggr_string")
  val copam_string: ColumnWithName = ColumnWithName(prefix + "copam_string")
  val fsh_calendar_group_string: ColumnWithName = ColumnWithName(prefix + "fsh_calendar_group_string")
  val bwtty_string: ColumnWithName = ColumnWithName(prefix + "bwtty_string")
  val vkglg_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "vkglg_decimal_13_2")
  val zznpgstqua_string: ColumnWithName = ColumnWithName(prefix + "zznpgstqua_string")
  val zzwwashp_string: ColumnWithName = ColumnWithName(prefix + "zzwwashp_string")
  val profil_string: ColumnWithName = ColumnWithName(prefix + "profil_string")
  val autru_string: ColumnWithName = ColumnWithName(prefix + "autru_string")
  val kzppv_string: ColumnWithName = ColumnWithName(prefix + "kzppv_string")
  val zzwhatbom_string: ColumnWithName = ColumnWithName(prefix + "zzwhatbom_string")
  val kordb_string: ColumnWithName = ColumnWithName(prefix + "kordb_string")
  val fhori_string: ColumnWithName = ColumnWithName(prefix + "fhori_string")
  val abfac_decimal_2_1: ColumnWithName = ColumnWithName(prefix + "abfac_decimal_2_1")
  val zzadfcheck_string: ColumnWithName = ColumnWithName(prefix + "zzadfcheck_string")
  val bwscl_string: ColumnWithName = ColumnWithName(prefix + "bwscl_string")
  val qmatv_string: ColumnWithName = ColumnWithName(prefix + "qmatv_string")
  val ccfix_string: ColumnWithName = ColumnWithName(prefix + "ccfix_string")
  val zzwwbqty_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "zzwwbqty_decimal_13_3")
  val pstat_string: ColumnWithName = ColumnWithName(prefix + "pstat_string")
  val pps_heur_id_string: ColumnWithName = ColumnWithName(prefix + "pps_heur_id_string")
  val miskz_string: ColumnWithName = ColumnWithName(prefix + "miskz_string")
  val steuc_string: ColumnWithName = ColumnWithName(prefix + "steuc_string")
  val prene_string: ColumnWithName = ColumnWithName(prefix + "prene_string")
  val plnty_string: ColumnWithName = ColumnWithName(prefix + "plnty_string")
  val vrbfk_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "vrbfk_decimal_4_2")
  val sgt_mrpsi_string: ColumnWithName = ColumnWithName(prefix + "sgt_mrpsi_string")
  val uchkz_string: ColumnWithName = ColumnWithName(prefix + "uchkz_string")
  val indus_string: ColumnWithName = ColumnWithName(prefix + "indus_string")
  val umrsl_string: ColumnWithName = ColumnWithName(prefix + "umrsl_string")
  val pfrei_string: ColumnWithName = ColumnWithName(prefix + "pfrei_string")
  val frtme_string: ColumnWithName = ColumnWithName(prefix + "frtme_string")
  val zztxtwk_string: ColumnWithName = ColumnWithName(prefix + "zztxtwk_string")
  val zzdokuk_string: ColumnWithName = ColumnWithName(prefix + "zzdokuk_string")
  val dplfs_string: ColumnWithName = ColumnWithName(prefix + "dplfs_string")
  val kzpro_string: ColumnWithName = ColumnWithName(prefix + "kzpro_string")
  val zzlom_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "zzlom_decimal_11_2")
  val compl_string: ColumnWithName = ColumnWithName(prefix + "compl_string")
  val zzplantref1_string: ColumnWithName = ColumnWithName(prefix + "zzplantref1_string")
  val zzverfp_string: ColumnWithName = ColumnWithName(prefix + "zzverfp_string")
  val zzrrptype_string: ColumnWithName = ColumnWithName(prefix + "zzrrptype_string")
  val kzkup_string: ColumnWithName = ColumnWithName(prefix + "kzkup_string")
  val zzvvm_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "zzvvm_decimal_11_2")
  val vrbmt_string: ColumnWithName = ColumnWithName(prefix + "vrbmt_string")
  val dispr_string: ColumnWithName = ColumnWithName(prefix + "dispr_string")
  val zzdatuw_date: ColumnWithName = ColumnWithName(prefix + "zzdatuw_date")
  val ucmat_string: ColumnWithName = ColumnWithName(prefix + "ucmat_string")
  val nkmpr_date: ColumnWithName = ColumnWithName(prefix + "nkmpr_date")
  val mdach_string: ColumnWithName = ColumnWithName(prefix + "mdach_string")
  val zzobs_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "zzobs_decimal_13_3")
  val max_troc_string: ColumnWithName = ColumnWithName(prefix + "max_troc_string")
  val kzpsp_string: ColumnWithName = ColumnWithName(prefix + "kzpsp_string")
  val bwesb_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bwesb_decimal_13_3")
  val zzwtrbcl_string: ColumnWithName = ColumnWithName(prefix + "zzwtrbcl_string")
  val rdprf_string: ColumnWithName = ColumnWithName(prefix + "rdprf_string")
  val multiple_ekgrp_string: ColumnWithName = ColumnWithName(prefix + "multiple_ekgrp_string")
  val zzccref1_string: ColumnWithName = ColumnWithName(prefix + "zzccref1_string")
  val sbdkz_string: ColumnWithName = ColumnWithName(prefix + "sbdkz_string")
  val vrbwk_string: ColumnWithName = ColumnWithName(prefix + "vrbwk_string")
  val stlan_string: ColumnWithName = ColumnWithName(prefix + "stlan_string")
  val pps_atpcheck_string: ColumnWithName = ColumnWithName(prefix + "pps_atpcheck_string")
  val stlal_string: ColumnWithName = ColumnWithName(prefix + "stlal_string")
  val pps_conhap_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "pps_conhap_decimal_13_3")
  val zzwtrmat_string: ColumnWithName = ColumnWithName(prefix + "zzwtrmat_string")
  val bearz_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "bearz_decimal_5_2")
  val ref_schema_string: ColumnWithName = ColumnWithName(prefix + "ref_schema_string")
  val zzqcf_decimal_6_3: ColumnWithName = ColumnWithName(prefix + "zzqcf_decimal_6_3")
  val plifz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "plifz_decimal_3_0")
  val zzgae_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "zzgae_decimal_13_3")
  val sfcpf_string: ColumnWithName = ColumnWithName(prefix + "sfcpf_string")
  val fxhor_string: ColumnWithName = ColumnWithName(prefix + "fxhor_string")
  val _sapmp_tolprpl_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "_sapmp_tolprpl_decimal_3_1")
  val bstfe_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bstfe_decimal_13_3")
  val auftl_string: ColumnWithName = ColumnWithName(prefix + "auftl_string")
  val xmcng_string: ColumnWithName = ColumnWithName(prefix + "xmcng_string")
  val mmstd_date: ColumnWithName = ColumnWithName(prefix + "mmstd_date")
  val kzkfk_string: ColumnWithName = ColumnWithName(prefix + "kzkfk_string")
  val fsh_seaim_string: ColumnWithName = ColumnWithName(prefix + "fsh_seaim_string")
  val nfmat_string: ColumnWithName = ColumnWithName(prefix + "nfmat_string")
  val ppskz_string: ColumnWithName = ColumnWithName(prefix + "ppskz_string")
  val zzsttransit_string: ColumnWithName = ColumnWithName(prefix + "zzsttransit_string")
  val takzt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "takzt_decimal_3_0")
  val _vso_r_pkgrp_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_pkgrp_string")
  val _vso_r_lane_num_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_lane_num_string")
  val minbe_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "minbe_decimal_13_3")
  val ffrei_string: ColumnWithName = ColumnWithName(prefix + "ffrei_string")
  val dispo_string: ColumnWithName = ColumnWithName(prefix + "dispo_string")
  val gpmkz_string: ColumnWithName = ColumnWithName(prefix + "gpmkz_string")
  val sgt_mrp_atp_status_string: ColumnWithName = ColumnWithName(prefix + "sgt_mrp_atp_status_string")
  val eprio_string: ColumnWithName = ColumnWithName(prefix + "eprio_string")
  val sgt_covs_string: ColumnWithName = ColumnWithName(prefix + "sgt_covs_string")
  val zzgetalefp_string: ColumnWithName = ColumnWithName(prefix + "zzgetalefp_string")
  val sfepr_string: ColumnWithName = ColumnWithName(prefix + "sfepr_string")
  val plvar_string: ColumnWithName = ColumnWithName(prefix + "plvar_string")
  val zzfnr1w_string: ColumnWithName = ColumnWithName(prefix + "zzfnr1w_string")
  val zzgesbsuse_string: ColumnWithName = ColumnWithName(prefix + "zzgesbsuse_string")
  val zztargemet_string: ColumnWithName = ColumnWithName(prefix + "zztargemet_string")
  val vers_cseg_string: ColumnWithName = ColumnWithName(prefix + "vers_cseg_string")
  val lgfsb_string: ColumnWithName = ColumnWithName(prefix + "lgfsb_string")
  val shzet_string: ColumnWithName = ColumnWithName(prefix + "shzet_string")
  val werks_string: ColumnWithName = ColumnWithName(prefix + "werks_string")
  val zzplgru_string: ColumnWithName = ColumnWithName(prefix + "zzplgru_string")
  val cuobj_string: ColumnWithName = ColumnWithName(prefix + "cuobj_string")
  val rotation_date_string: ColumnWithName = ColumnWithName(prefix + "rotation_date_string")
  val zzpkzprodh_string: ColumnWithName = ColumnWithName(prefix + "zzpkzprodh_string")
  val eislo_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "eislo_decimal_13_3")
  val mpdau_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "mpdau_decimal_5_0")
  val fprfm_string: ColumnWithName = ColumnWithName(prefix + "fprfm_string")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val casnr_string: ColumnWithName = ColumnWithName(prefix + "casnr_string")
  val zzpgstrest_string: ColumnWithName = ColumnWithName(prefix + "zzpgstrest_string")
  val qmata_string: ColumnWithName = ColumnWithName(prefix + "qmata_string")
  val cuobv_string: ColumnWithName = ColumnWithName(prefix + "cuobv_string")
  val vbamg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "vbamg_decimal_13_3")
  val dismm_string: ColumnWithName = ColumnWithName(prefix + "dismm_string")
  val lfrhy_string: ColumnWithName = ColumnWithName(prefix + "lfrhy_string")
  val vbeaz_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "vbeaz_decimal_5_2")
  val zzaemp2_string: ColumnWithName = ColumnWithName(prefix + "zzaemp2_string")
  val lgrad_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "lgrad_decimal_3_1")
  val prefe_string: ColumnWithName = ColumnWithName(prefix + "prefe_string")
  val stdpd_string: ColumnWithName = ColumnWithName(prefix + "stdpd_string")
  val fxpru_string: ColumnWithName = ColumnWithName(prefix + "fxpru_string")
  val ekgrp_string: ColumnWithName = ColumnWithName(prefix + "ekgrp_string")
  val zzshipf_string: ColumnWithName = ColumnWithName(prefix + "zzshipf_string")
  val pps_giprt_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "pps_giprt_decimal_11_0")
  val perkz_string: ColumnWithName = ColumnWithName(prefix + "perkz_string")
  val stawn_string: ColumnWithName = ColumnWithName(prefix + "stawn_string")
  val sgt_defsc_string: ColumnWithName = ColumnWithName(prefix + "sgt_defsc_string")
  val sproz_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "sproz_decimal_3_1")
  val matgr_string: ColumnWithName = ColumnWithName(prefix + "matgr_string")
  val zzconvh_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "zzconvh_decimal_3_0")
  val sgt_scope_string: ColumnWithName = ColumnWithName(prefix + "sgt_scope_string")
  val convt_string: ColumnWithName = ColumnWithName(prefix + "convt_string")
  val dpcbt_string: ColumnWithName = ColumnWithName(prefix + "dpcbt_string")
  val servg_string: ColumnWithName = ColumnWithName(prefix + "servg_string")
  val fixls_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "fixls_decimal_13_3")
  val zzabw_string: ColumnWithName = ColumnWithName(prefix + "zzabw_string")
  val apokz_string: ColumnWithName = ColumnWithName(prefix + "apokz_string")
  val gi_pr_time_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "gi_pr_time_decimal_3_0")
  val zzpgstrat_string: ColumnWithName = ColumnWithName(prefix + "zzpgstrat_string")
  val kzagl_string: ColumnWithName = ColumnWithName(prefix + "kzagl_string")
  val beskz_string: ColumnWithName = ColumnWithName(prefix + "beskz_string")
  val fevor_string: ColumnWithName = ColumnWithName(prefix + "fevor_string")
  val dplho_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "dplho_decimal_3_0")
  val kzkri_string: ColumnWithName = ColumnWithName(prefix + "kzkri_string")
  val zzheurid_string: ColumnWithName = ColumnWithName(prefix + "zzheurid_string")
  val maxls_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "maxls_decimal_13_3")
  val fsh_kzech_string: ColumnWithName = ColumnWithName(prefix + "fsh_kzech_string")
  val lagpr_string: ColumnWithName = ColumnWithName(prefix + "lagpr_string")
  val zznpgstrsl_string: ColumnWithName = ColumnWithName(prefix + "zznpgstrsl_string")
  val kzdkz_string: ColumnWithName = ColumnWithName(prefix + "kzdkz_string")
  val basmg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "basmg_decimal_13_3")
  val nf_flag_string: ColumnWithName = ColumnWithName(prefix + "nf_flag_string")
  val wzeit_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "wzeit_decimal_3_0")
  val iuid_relevant_string: ColumnWithName = ColumnWithName(prefix + "iuid_relevant_string")
  val losfx_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "losfx_decimal_11_2")
  val pps_planning_type_string: ColumnWithName = ColumnWithName(prefix + "pps_planning_type_string")
  val sgt_chint_string: ColumnWithName = ColumnWithName(prefix + "sgt_chint_string")
  val itark_string: ColumnWithName = ColumnWithName(prefix + "itark_string")
  val preno_string: ColumnWithName = ColumnWithName(prefix + "preno_string")
  val zzvdt_date: ColumnWithName = ColumnWithName(prefix + "zzvdt_date")
  val ausme_string: ColumnWithName = ColumnWithName(prefix + "ausme_string")
  val dzeit_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "dzeit_decimal_3_0")
  val zzaeip_string: ColumnWithName = ColumnWithName(prefix + "zzaeip_string")
  val glgmg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "glgmg_decimal_13_3")
  val zzgesmguse_string: ColumnWithName = ColumnWithName(prefix + "zzgesmguse_string")
  val ahdis_string: ColumnWithName = ColumnWithName(prefix + "ahdis_string")
  val zzprodh_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "zzprodh_decimal_3_0")
  val mtvfp_string: ColumnWithName = ColumnWithName(prefix + "mtvfp_string")
  val zzfeabw_string: ColumnWithName = ColumnWithName(prefix + "zzfeabw_string")
  val herbl_string: ColumnWithName = ColumnWithName(prefix + "herbl_string")
  val minls_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "minls_decimal_13_3")
  val vrbdt_date: ColumnWithName = ColumnWithName(prefix + "vrbdt_date")
  val altsl_string: ColumnWithName = ColumnWithName(prefix + "altsl_string")
  val lfgja_string: ColumnWithName = ColumnWithName(prefix + "lfgja_string")
  val zzprodcent_string: ColumnWithName = ColumnWithName(prefix + "zzprodcent_string")
  val quazt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "quazt_decimal_3_0")
  val megru_string: ColumnWithName = ColumnWithName(prefix + "megru_string")
  val dplpu_string: ColumnWithName = ColumnWithName(prefix + "dplpu_string")
  val aplal_string: ColumnWithName = ColumnWithName(prefix + "aplal_string")
  val zzfnr2w_string: ColumnWithName = ColumnWithName(prefix + "zzfnr2w_string")
  val zzpgpasmax_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "zzpgpasmax_decimal_11_0")
  val sgt_prcm_string: ColumnWithName = ColumnWithName(prefix + "sgt_prcm_string")
  val ssqss_string: ColumnWithName = ColumnWithName(prefix + "ssqss_string")
  val kzdie_string: ColumnWithName = ColumnWithName(prefix + "kzdie_string")
  val zzantr_string: ColumnWithName = ColumnWithName(prefix + "zzantr_string")
  val fsh_var_group_string: ColumnWithName = ColumnWithName(prefix + "fsh_var_group_string")
  val zzchkmaxlz_string: ColumnWithName = ColumnWithName(prefix + "zzchkmaxlz_string")
  val vktrw_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "vktrw_decimal_13_2")
  val maxlz_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "maxlz_decimal_5_0")
  val ruezt_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "ruezt_decimal_5_2")
  val cons_procg_string: ColumnWithName = ColumnWithName(prefix + "cons_procg_string")
  val bstrf_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bstrf_decimal_13_3")
  val mownr_string: ColumnWithName = ColumnWithName(prefix + "mownr_string")
  val zzpgfutmax_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "zzpgfutmax_decimal_11_0")
  val gpnum_string: ColumnWithName = ColumnWithName(prefix + "gpnum_string")
  val prenc_string: ColumnWithName = ColumnWithName(prefix + "prenc_string")
  val mfrgr_string: ColumnWithName = ColumnWithName(prefix + "mfrgr_string")
  val pps_peg_past_al_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "pps_peg_past_al_decimal_11_0")
  val target_stock_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "target_stock_decimal_13_3")
  val sgt_statc_string: ColumnWithName = ColumnWithName(prefix + "sgt_statc_string")
  val xchar_string: ColumnWithName = ColumnWithName(prefix + "xchar_string")
  val kzaus_string: ColumnWithName = ColumnWithName(prefix + "kzaus_string")
  val prend_date: ColumnWithName = ColumnWithName(prefix + "prend_date")
  val mcrue_string: ColumnWithName = ColumnWithName(prefix + "mcrue_string")
  val zzwwbtyp_string: ColumnWithName = ColumnWithName(prefix + "zzwwbtyp_string")
  val losgr_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "losgr_decimal_13_3")
  val insmk_string: ColumnWithName = ColumnWithName(prefix + "insmk_string")
  val maabc_string: ColumnWithName = ColumnWithName(prefix + "maabc_string")
  val vrmod_string: ColumnWithName = ColumnWithName(prefix + "vrmod_string")
  val ltinc_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "ltinc_decimal_13_3")
  val vspvb_string: ColumnWithName = ColumnWithName(prefix + "vspvb_string")
  val vint1_string: ColumnWithName = ColumnWithName(prefix + "vint1_string")
  val verkz_string: ColumnWithName = ColumnWithName(prefix + "verkz_string")
  val herkr_string: ColumnWithName = ColumnWithName(prefix + "herkr_string")
  val sobsl_string: ColumnWithName = ColumnWithName(prefix + "sobsl_string")
  val vzusl_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "vzusl_decimal_5_2")
  val vrvez_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "vrvez_decimal_5_2")
  val ausss_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "ausss_decimal_5_2")
  val _casww_we_spart_string: ColumnWithName = ColumnWithName(prefix + "_casww_we_spart_string")
  val zzwtrpwb_string: ColumnWithName = ColumnWithName(prefix + "zzwtrpwb_string")
  val awsls_string: ColumnWithName = ColumnWithName(prefix + "awsls_string")
  val zzaemp1_string: ColumnWithName = ColumnWithName(prefix + "zzaemp1_string")
  val periv_string: ColumnWithName = ColumnWithName(prefix + "periv_string")
  val mtver_string: ColumnWithName = ColumnWithName(prefix + "mtver_string")
  val herkl_string: ColumnWithName = ColumnWithName(prefix + "herkl_string")
  val zzpgfutale_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "zzpgfutale_decimal_11_0")
  val preng_date: ColumnWithName = ColumnWithName(prefix + "preng_date")
  val _vso_r_pal_vend_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_pal_vend_string")
  val bstma_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bstma_decimal_13_3")
  val tranz_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "tranz_decimal_5_2")
  val mmsta_string: ColumnWithName = ColumnWithName(prefix + "mmsta_string")
  val kzbed_string: ColumnWithName = ColumnWithName(prefix + "kzbed_string")
  val zzshiph_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "zzshiph_decimal_3_0")
  val uid_iea_string: ColumnWithName = ColumnWithName(prefix + "uid_iea_string")
  val disgr_string: ColumnWithName = ColumnWithName(prefix + "disgr_string")
  val eisbe_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "eisbe_decimal_13_3")
  val disls_string: ColumnWithName = ColumnWithName(prefix + "disls_string")
  val sernp_string: ColumnWithName = ColumnWithName(prefix + "sernp_string")
  val lvorm_string: ColumnWithName = ColumnWithName(prefix + "lvorm_string")
  val mabst_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "mabst_decimal_13_3")
  val mogru_string: ColumnWithName = ColumnWithName(prefix + "mogru_string")
  val min_troc_string: ColumnWithName = ColumnWithName(prefix + "min_troc_string")
  val zzprodf_string: ColumnWithName = ColumnWithName(prefix + "zzprodf_string")
  val pps_peg_fut_al_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "pps_peg_fut_al_decimal_11_0")
  val webaz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "webaz_decimal_3_0")
  val shflg_string: ColumnWithName = ColumnWithName(prefix + "shflg_string")
  val zzpgstblok_string: ColumnWithName = ColumnWithName(prefix + "zzpgstblok_string")
  val kautb_string: ColumnWithName = ColumnWithName(prefix + "kautb_string")
  val zzastk_string: ColumnWithName = ColumnWithName(prefix + "zzastk_string")
  val abcin_string: ColumnWithName = ColumnWithName(prefix + "abcin_string")
  val zzvim_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "zzvim_decimal_11_2")
  val expme_string: ColumnWithName = ColumnWithName(prefix + "expme_string")
  val zzpgwoaler_string: ColumnWithName = ColumnWithName(prefix + "zzpgwoaler_string")
  val rwpro_string: ColumnWithName = ColumnWithName(prefix + "rwpro_string")
  val fvidk_string: ColumnWithName = ColumnWithName(prefix + "fvidk_string")
  val prfrq_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "prfrq_decimal_5_0")
  val qssys_string: ColumnWithName = ColumnWithName(prefix + "qssys_string")
  val ncost_string: ColumnWithName = ColumnWithName(prefix + "ncost_string")
  val qzgtp_string: ColumnWithName = ColumnWithName(prefix + "qzgtp_string")
  val xchpf_string: ColumnWithName = ColumnWithName(prefix + "xchpf_string")
  val strgr_string: ColumnWithName = ColumnWithName(prefix + "strgr_string")
  val zztargedur_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "zztargedur_decimal_11_0")
  val fabkz_string: ColumnWithName = ColumnWithName(prefix + "fabkz_string")
  val trame_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "trame_decimal_13_3")
  val uneto_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "uneto_decimal_3_1")
  val sgt_mmstd_string: ColumnWithName = ColumnWithName(prefix + "sgt_mmstd_string")
  val nkmpr_string: ColumnWithName = ColumnWithName(prefix + "nkmpr_string")
  val prend_string: ColumnWithName = ColumnWithName(prefix + "prend_string")
  val ausdt_string: ColumnWithName = ColumnWithName(prefix + "ausdt_string")
  val zzvdt_string: ColumnWithName = ColumnWithName(prefix + "zzvdt_string")
  val zzdatuw_string: ColumnWithName = ColumnWithName(prefix + "zzdatuw_string")
  val mmstd_string: ColumnWithName = ColumnWithName(prefix + "mmstd_string")
  val vrbdt_string: ColumnWithName = ColumnWithName(prefix + "vrbdt_string")
  val preng_string: ColumnWithName = ColumnWithName(prefix + "preng_string")
}

object C_marc extends C_marc("") {
  def as(alias: String): C_marc = new C_marc(alias + ".")
}

// AUTO GENERATED:END
