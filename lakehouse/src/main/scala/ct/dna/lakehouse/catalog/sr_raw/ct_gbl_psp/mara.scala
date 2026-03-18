// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_psp

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_mara_part1(
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
    _cwm_tarum_string: String,
    @Decimal(7, 2) _sapmp_kadu_decimal_7_2: BigDecimal,
    liqdt_string: String,
    matnr_string: String,
    mcond_string: String,
    formt_string: String,
    gds_relevant_string: String,
    _dsd_sv_cnt_grp_string: String,
    whmatgr_string: String,
    size1_string: String,
    tragr_string: String,
    inhme_string: String,
    xgchp_string: String,
    tare_var_string: String,
    magrv_string: String,
    vpsta_string: String,
    rdmhd_string: String,
    begru_string: String,
    rmatp_string: String,
    fiber_part5_string: String,
    @Decimal(15, 3) maxh_decimal_15_3: BigDecimal,
    @Decimal(15, 3) maxl_decimal_15_3: BigDecimal,
    _bev1_luldegrp_string: String,
    _cwm_tara_string: String,
    ernam_string: String,
    prdha_string: String,
    logistical_mat_category_string: String,
    _vso_r_bot_ind_string: String,
    _vso_r_kzgvh_ind_string: String,
    hutyp_string: String,
    free_char_string: String,
    allow_pmat_igno_string: String,
    @Decimal(3, 1) volto_decimal_3_1: BigDecimal,
    ric_id_long: BoxedLong,
    raube_string: String,
    cadkz_string: String,
    _sttpec_syncchg_string: String,
    ekwsl_string: String,
    @Decimal(7, 2) _sapmp_spbi_decimal_7_2: BigDecimal,
    @Decimal(7, 2) _sapmp_kedu_decimal_7_2: BigDecimal,
    @Decimal(7, 2) _sapmp_fbdk_decimal_7_2: BigDecimal,
    scm_matid_guid16_binary: Array[Byte],
    entar_string: String,
    attyp_string: String,
    @Decimal(4, 0) _sapmp_brad_decimal_4_0: BigDecimal,
    @Decimal(7, 2) _sapmp_mifrr_decimal_7_2: BigDecimal,
    scm_drp_gl_stock_string: String,
    @Decimal(3, 0) fuelg_decimal_3_0: BigDecimal,
    mstav_string: String,
    _dsd_sl_toltyp_string: String,
    @Decimal(5, 0) vpreh_decimal_5_0: BigDecimal,
    som_cycle_string: String,
    _cwm_valum_string: String,
    @Decimal(13, 3) _vso_r_pal_min_h_decimal_13_3: BigDecimal,
    etiag_string: String,
    blatt_string: String,
    bwscl_string: String,
    _vso_r_tilt_ind_string: String,
    pstat_string: String,
    mtpos_mara_string: String,
    bstat_string: String,
    _vso_r_no_p_gvh_string: String,
    @Decimal(13, 3) wesch_decimal_13_3: BigDecimal,
    @Decimal(13, 3) laeng_decimal_13_3: BigDecimal,
    varid_binary: Array[Byte],
    zeivr_string: String,
    compl_string: String,
    animal_origin_string: String,
    fiber_code4_string: String,
    kzumw_string: String,
    @Decimal(13, 3) ntgew_decimal_13_3: BigDecimal,
    @Decimal(13, 3) breit_decimal_13_3: BigDecimal,
    _dsd_vc_group_string: String,
    scm_restrict_invbal_string: String,
    kzkfg_string: String,
    fiber_code5_string: String,
    chml_cmplnc_rlvnce_ind_string: String,
    @Decimal(15, 3) maxb_decimal_15_3: BigDecimal,
    height_nonflat_uom_string: String,
    ervoe_string: String,
    saiso_string: String,
    @Decimal(13, 3) ergew_decimal_13_3: BigDecimal,
    qqtimeuom_string: String,
    @Decimal(13, 3) _vso_r_pal_ovr_d_decimal_13_3: BigDecimal,
    kzkup_string: String,
    @Decimal(13, 3) hoehe_decimal_13_3: BigDecimal,
    imatn_string: String,
    @Decimal(15, 0) _sttpec_synctime_decimal_15_0: BigDecimal,
    groes_string: String,
    fsh_mg_at2_string: String,
    nsnid_string: String,
    _cwm_xcwmat_string: String,
    behvo_string: String,
    _sapmp_rili_string: String,
    bflme_string: String,
    dg_pack_status_string: String,
    gewei_string: String,
    psm_code_string: String,
    created_at_time_string: String,
    xgrdt_string: String,
    fsh_mg_at3_string: String,
    przus_string: String,
    @Decimal(11, 0) scm_maturity_dur_decimal_11_0: BigDecimal,
    ferth_string: String,
    som_cycle_rule_string: String,
    _vso_r_stack_no_string: String,
    serlv_string: String,
    cuobf_string: String,
    @Decimal(5, 2) overhang_tresh_decimal_5_2: BigDecimal,
    @Decimal(7, 2) _sapmp_trad_decimal_7_2: BigDecimal,
    fsh_seaim_string: String,
    iprkz_string: String,
    picnum_string: String,
    ipmipproduct_string: String,
    size2_atinn_string: String,
    anp_string: String,
    fsh_mg_at1_string: String,
    fiber_code2_string: String,
    color_string: String,
    rbnrm_string: String,
    medium_string: String,
    mfrpn_string: String,
    kzrev_string: String,
    @Decimal(13, 3) inhal_decimal_13_3: BigDecimal,
    mo_profile_id_string: String,
    fiber_part1_string: String,
    sprof_string: String,
    @Decimal(13, 3) height_nonflat_decimal_13_3: BigDecimal,
    _bev1_nestruccat_string: String,
    prod_shape_string: String,
    _sapmp_aho_string: String,
    qgrp_string: String,
    etifo_string: String,
    _bev1_luleinh_string: String,
    kosch_string: String,
    zeinr_string: String,
    @Decimal(3, 0) mhdlp_decimal_3_0: BigDecimal,
    @Decimal(11, 0) scm_shlf_lfe_req_max_decimal_11_0: BigDecimal,
    mstae_string: String,
    gtin_variant_string: String,
    saity_string: String,
    _vso_r_quan_unit_string: String,
    ergei_string: String,
    ersda_string: String,
    mandt_string: String,
    kzwsm_string: String,
    eannr_string: String,
    pmata_string: String,
    extwg_string: String,
    sgt_covsa_string: String,
    @Decimal(3, 1) gewto_decimal_3_1: BigDecimal,
    kzgvh_string: String,
    saisj_string: String,
    blanz_string: String,
    whstc_string: String,
    satnr_string: String,
    gennr_string: String,
    aeklk_string: String,
    scm_kitcomp_string: String,
    sgt_scope_string: String,
    srv_dura_uom_string: String,
    dpcbt_string: String,
    sled_bbd_string: String,
    voleh_string: String,
    _vso_r_pal_ind_string: String,
    @Decimal(3, 1) maxc_tol_decimal_3_1: BigDecimal,
    mstdv_string: String,
    bwvor_string: String,
    fiber_part3_string: String,
    zzgl_dts_tcs_fcode_string: String,
    rmatp_pb_string: String,
    etiar_string: String,
    meins_string: String,
    cwqproc_string: String,
    fashgrd_string: String,
    ovlpn_string: String,
    fiber_part2_string: String,
    care_code_string: String,
    matnr_external_string: String,
    last_changed_time_string: String,
    pilferable_string: String,
    profl_string: String,
    @Decimal(13, 3) _sapmp_sptr_decimal_13_3: BigDecimal,
    hazmat_string: String,
    stfak_int: BoxedInt,
    retdelc_string: String,
    @Decimal(4, 0) mhdhb_decimal_4_0: BigDecimal,
    @Decimal(13, 3) _vso_r_pal_b_ht_decimal_13_3: BigDecimal,
    spart_string: String,
    serial_string: String,
    vabme_string: String,
    scm_puom_string: String,
    @Decimal(13, 3) inhbr_decimal_13_3: BigDecimal,
    mbrsh_string: String,
    mfrnr_string: String,
    mprof_string: String,
    _cwm_tolgr_string: String,
    ean11_string: String,
    msbookpartno_string: String,
    srv_escal_string: String,
    _sttpec_country_ref_string: String,
    mstde_string: String,
    sgt_stat_string: String,
    @Decimal(13, 3) _vso_r_pal_ovr_w_decimal_13_3: BigDecimal,
    _sttpec_syncact_string: String,
    size2_string: String,
    meabm_string: String,
    scm_matid_guid22_string: String,
    fsh_sealv_string: String,
    fiber_code1_string: String,
    numtp_string: String,
    iloos_string: String,
    cwqtolgr_string: String,
    srv_serwi_string: String,
    sgt_csgr_string: String,
    cmrel_string: String,
    mfrgr_string: String,
    logunit_string: String,
    vhart_string: String,
    scm_excl_expedite_string: String,
    packcode_string: String,
    _vso_r_top_ind_string: String,
    @Decimal(15, 3) maxc_decimal_15_3: BigDecimal,
    aenam_string: String,
    size1_atinn_string: String,
    _vso_r_stack_ind_string: String,
    color_atinn_string: String,
    aeszn_string: String,
    @Decimal(4, 0) mhdrz_decimal_4_0: BigDecimal,
    cmeth_string: String,
    loglev_reto_string: String,
    cwqrel_string: String,
    bmatn_string: String,
    bstme_string: String,
    fsh_sc_mid_string: String,
    kzeff_string: String,
    nrfhg_string: String,
    bbtyp_string: String,
    zeifo_string: String,
    scm_prod_paoopt_string: String,
    maxdim_uom_string: String,
    matfi_string: String
) extends Entity

case class E_mara_part2(
    @PK _mk_org: String,
    adspc_spc_string: String,
    @Decimal(5, 2) bridge_max_slope_decimal_5_2: BigDecimal,
    ihivi_string: String,
    weora_string: String,
    herkl_string: String,
    commodity_string: String,
    qmpur_string: String,
    fiber_code3_string: String,
    bismt_string: String,
    hutyp_dflt_string: String,
    @Decimal(11, 0) scm_shlf_lfe_req_min_decimal_11_0: BigDecimal,
    mtart_string: String,
    stoff_string: String,
    @Decimal(13, 3) srv_dura_decimal_13_3: BigDecimal,
    zeiar_string: String,
    kunnr_string: String,
    tempb_string: String,
    labor_string: String,
    _sttpec_sertype_int: BoxedInt,
    @Decimal(5, 2) bridge_tresh_decimal_5_2: BigDecimal,
    _sapmp_fbak_string: String,
    @Decimal(13, 3) volum_decimal_13_3: BigDecimal,
    wrkst_string: String,
    identification_tag_type_string: String,
    mlgut_string: String,
    taklv_string: String,
    hndlcode_string: String,
    normt_string: String,
    kznfm_string: String,
    sgt_rel_string: String,
    textile_comp_ind_string: String,
    lvorm_string: String,
    @Decimal(13, 3) ervol_decimal_13_3: BigDecimal,
    @Decimal(7, 2) _sapmp_fbhk_decimal_7_2: BigDecimal,
    som_tc_schema_string: String,
    _sttpec_prdcat_string: String,
    @Decimal(3, 0) qqtime_decimal_3_0: BigDecimal,
    scm_bod_deplvl_string: String,
    matkl_string: String,
    ps_smartform_string: String,
    sales_material_string: String,
    laeda_string: String,
    adprof_string: String,
    datab_string: String,
    disst_string: String,
    xchpf_string: String,
    @Decimal(13, 3) brgew_decimal_13_3: BigDecimal,
    _sapmp_abmein_string: String,
    brand_id_string: String,
    icfa_string: String,
    plgtp_string: String,
    dummy_prd_incl_eew_ps_string: String,
    @Decimal(5, 2) _sapmp_kadp_decimal_5_2: BigDecimal,
    fiber_part4_string: String,
    @Decimal(13, 3) _vso_r_tol_b_ht_decimal_13_3: BigDecimal
) extends Entity

object mara extends TableSpec[Joined[E_mara_part1, E_mara_part2]](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_mara(prefix: String) extends ColumnWithNameAccessor {
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
  val _cwm_tarum_string: ColumnWithName = ColumnWithName(prefix + "_cwm_tarum_string")
  val _sapmp_kadu_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "_sapmp_kadu_decimal_7_2")
  val liqdt_string: ColumnWithName = ColumnWithName(prefix + "liqdt_string")
  val matnr_string: ColumnWithName = ColumnWithName(prefix + "matnr_string")
  val mcond_string: ColumnWithName = ColumnWithName(prefix + "mcond_string")
  val formt_string: ColumnWithName = ColumnWithName(prefix + "formt_string")
  val gds_relevant_string: ColumnWithName = ColumnWithName(prefix + "gds_relevant_string")
  val _dsd_sv_cnt_grp_string: ColumnWithName = ColumnWithName(prefix + "_dsd_sv_cnt_grp_string")
  val whmatgr_string: ColumnWithName = ColumnWithName(prefix + "whmatgr_string")
  val size1_string: ColumnWithName = ColumnWithName(prefix + "size1_string")
  val tragr_string: ColumnWithName = ColumnWithName(prefix + "tragr_string")
  val inhme_string: ColumnWithName = ColumnWithName(prefix + "inhme_string")
  val xgchp_string: ColumnWithName = ColumnWithName(prefix + "xgchp_string")
  val tare_var_string: ColumnWithName = ColumnWithName(prefix + "tare_var_string")
  val magrv_string: ColumnWithName = ColumnWithName(prefix + "magrv_string")
  val vpsta_string: ColumnWithName = ColumnWithName(prefix + "vpsta_string")
  val rdmhd_string: ColumnWithName = ColumnWithName(prefix + "rdmhd_string")
  val begru_string: ColumnWithName = ColumnWithName(prefix + "begru_string")
  val rmatp_string: ColumnWithName = ColumnWithName(prefix + "rmatp_string")
  val fiber_part5_string: ColumnWithName = ColumnWithName(prefix + "fiber_part5_string")
  val maxh_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "maxh_decimal_15_3")
  val maxl_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "maxl_decimal_15_3")
  val _bev1_luldegrp_string: ColumnWithName = ColumnWithName(prefix + "_bev1_luldegrp_string")
  val _cwm_tara_string: ColumnWithName = ColumnWithName(prefix + "_cwm_tara_string")
  val ernam_string: ColumnWithName = ColumnWithName(prefix + "ernam_string")
  val prdha_string: ColumnWithName = ColumnWithName(prefix + "prdha_string")
  val logistical_mat_category_string: ColumnWithName = ColumnWithName(prefix + "logistical_mat_category_string")
  val _vso_r_bot_ind_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_bot_ind_string")
  val _vso_r_kzgvh_ind_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_kzgvh_ind_string")
  val hutyp_string: ColumnWithName = ColumnWithName(prefix + "hutyp_string")
  val free_char_string: ColumnWithName = ColumnWithName(prefix + "free_char_string")
  val allow_pmat_igno_string: ColumnWithName = ColumnWithName(prefix + "allow_pmat_igno_string")
  val volto_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "volto_decimal_3_1")
  val ric_id_long: ColumnWithName = ColumnWithName(prefix + "ric_id_long")
  val raube_string: ColumnWithName = ColumnWithName(prefix + "raube_string")
  val cadkz_string: ColumnWithName = ColumnWithName(prefix + "cadkz_string")
  val _sttpec_syncchg_string: ColumnWithName = ColumnWithName(prefix + "_sttpec_syncchg_string")
  val ekwsl_string: ColumnWithName = ColumnWithName(prefix + "ekwsl_string")
  val _sapmp_spbi_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "_sapmp_spbi_decimal_7_2")
  val _sapmp_kedu_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "_sapmp_kedu_decimal_7_2")
  val _sapmp_fbdk_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "_sapmp_fbdk_decimal_7_2")
  val scm_matid_guid16_binary: ColumnWithName = ColumnWithName(prefix + "scm_matid_guid16_binary")
  val entar_string: ColumnWithName = ColumnWithName(prefix + "entar_string")
  val attyp_string: ColumnWithName = ColumnWithName(prefix + "attyp_string")
  val _sapmp_brad_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "_sapmp_brad_decimal_4_0")
  val _sapmp_mifrr_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "_sapmp_mifrr_decimal_7_2")
  val scm_drp_gl_stock_string: ColumnWithName = ColumnWithName(prefix + "scm_drp_gl_stock_string")
  val fuelg_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "fuelg_decimal_3_0")
  val mstav_string: ColumnWithName = ColumnWithName(prefix + "mstav_string")
  val _dsd_sl_toltyp_string: ColumnWithName = ColumnWithName(prefix + "_dsd_sl_toltyp_string")
  val vpreh_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "vpreh_decimal_5_0")
  val som_cycle_string: ColumnWithName = ColumnWithName(prefix + "som_cycle_string")
  val _cwm_valum_string: ColumnWithName = ColumnWithName(prefix + "_cwm_valum_string")
  val _vso_r_pal_min_h_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_vso_r_pal_min_h_decimal_13_3")
  val etiag_string: ColumnWithName = ColumnWithName(prefix + "etiag_string")
  val blatt_string: ColumnWithName = ColumnWithName(prefix + "blatt_string")
  val bwscl_string: ColumnWithName = ColumnWithName(prefix + "bwscl_string")
  val _vso_r_tilt_ind_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_tilt_ind_string")
  val pstat_string: ColumnWithName = ColumnWithName(prefix + "pstat_string")
  val mtpos_mara_string: ColumnWithName = ColumnWithName(prefix + "mtpos_mara_string")
  val bstat_string: ColumnWithName = ColumnWithName(prefix + "bstat_string")
  val _vso_r_no_p_gvh_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_no_p_gvh_string")
  val wesch_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "wesch_decimal_13_3")
  val laeng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "laeng_decimal_13_3")
  val varid_binary: ColumnWithName = ColumnWithName(prefix + "varid_binary")
  val zeivr_string: ColumnWithName = ColumnWithName(prefix + "zeivr_string")
  val compl_string: ColumnWithName = ColumnWithName(prefix + "compl_string")
  val animal_origin_string: ColumnWithName = ColumnWithName(prefix + "animal_origin_string")
  val fiber_code4_string: ColumnWithName = ColumnWithName(prefix + "fiber_code4_string")
  val kzumw_string: ColumnWithName = ColumnWithName(prefix + "kzumw_string")
  val ntgew_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "ntgew_decimal_13_3")
  val breit_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "breit_decimal_13_3")
  val _dsd_vc_group_string: ColumnWithName = ColumnWithName(prefix + "_dsd_vc_group_string")
  val scm_restrict_invbal_string: ColumnWithName = ColumnWithName(prefix + "scm_restrict_invbal_string")
  val kzkfg_string: ColumnWithName = ColumnWithName(prefix + "kzkfg_string")
  val fiber_code5_string: ColumnWithName = ColumnWithName(prefix + "fiber_code5_string")
  val chml_cmplnc_rlvnce_ind_string: ColumnWithName = ColumnWithName(prefix + "chml_cmplnc_rlvnce_ind_string")
  val maxb_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "maxb_decimal_15_3")
  val height_nonflat_uom_string: ColumnWithName = ColumnWithName(prefix + "height_nonflat_uom_string")
  val ervoe_string: ColumnWithName = ColumnWithName(prefix + "ervoe_string")
  val saiso_string: ColumnWithName = ColumnWithName(prefix + "saiso_string")
  val ergew_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "ergew_decimal_13_3")
  val qqtimeuom_string: ColumnWithName = ColumnWithName(prefix + "qqtimeuom_string")
  val _vso_r_pal_ovr_d_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_vso_r_pal_ovr_d_decimal_13_3")
  val kzkup_string: ColumnWithName = ColumnWithName(prefix + "kzkup_string")
  val hoehe_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "hoehe_decimal_13_3")
  val imatn_string: ColumnWithName = ColumnWithName(prefix + "imatn_string")
  val _sttpec_synctime_decimal_15_0: ColumnWithName = ColumnWithName(prefix + "_sttpec_synctime_decimal_15_0")
  val groes_string: ColumnWithName = ColumnWithName(prefix + "groes_string")
  val fsh_mg_at2_string: ColumnWithName = ColumnWithName(prefix + "fsh_mg_at2_string")
  val nsnid_string: ColumnWithName = ColumnWithName(prefix + "nsnid_string")
  val _cwm_xcwmat_string: ColumnWithName = ColumnWithName(prefix + "_cwm_xcwmat_string")
  val behvo_string: ColumnWithName = ColumnWithName(prefix + "behvo_string")
  val _sapmp_rili_string: ColumnWithName = ColumnWithName(prefix + "_sapmp_rili_string")
  val bflme_string: ColumnWithName = ColumnWithName(prefix + "bflme_string")
  val dg_pack_status_string: ColumnWithName = ColumnWithName(prefix + "dg_pack_status_string")
  val gewei_string: ColumnWithName = ColumnWithName(prefix + "gewei_string")
  val psm_code_string: ColumnWithName = ColumnWithName(prefix + "psm_code_string")
  val created_at_time_string: ColumnWithName = ColumnWithName(prefix + "created_at_time_string")
  val xgrdt_string: ColumnWithName = ColumnWithName(prefix + "xgrdt_string")
  val fsh_mg_at3_string: ColumnWithName = ColumnWithName(prefix + "fsh_mg_at3_string")
  val przus_string: ColumnWithName = ColumnWithName(prefix + "przus_string")
  val scm_maturity_dur_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_maturity_dur_decimal_11_0")
  val ferth_string: ColumnWithName = ColumnWithName(prefix + "ferth_string")
  val som_cycle_rule_string: ColumnWithName = ColumnWithName(prefix + "som_cycle_rule_string")
  val _vso_r_stack_no_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_stack_no_string")
  val serlv_string: ColumnWithName = ColumnWithName(prefix + "serlv_string")
  val cuobf_string: ColumnWithName = ColumnWithName(prefix + "cuobf_string")
  val overhang_tresh_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "overhang_tresh_decimal_5_2")
  val _sapmp_trad_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "_sapmp_trad_decimal_7_2")
  val fsh_seaim_string: ColumnWithName = ColumnWithName(prefix + "fsh_seaim_string")
  val iprkz_string: ColumnWithName = ColumnWithName(prefix + "iprkz_string")
  val picnum_string: ColumnWithName = ColumnWithName(prefix + "picnum_string")
  val ipmipproduct_string: ColumnWithName = ColumnWithName(prefix + "ipmipproduct_string")
  val size2_atinn_string: ColumnWithName = ColumnWithName(prefix + "size2_atinn_string")
  val anp_string: ColumnWithName = ColumnWithName(prefix + "anp_string")
  val fsh_mg_at1_string: ColumnWithName = ColumnWithName(prefix + "fsh_mg_at1_string")
  val fiber_code2_string: ColumnWithName = ColumnWithName(prefix + "fiber_code2_string")
  val color_string: ColumnWithName = ColumnWithName(prefix + "color_string")
  val rbnrm_string: ColumnWithName = ColumnWithName(prefix + "rbnrm_string")
  val medium_string: ColumnWithName = ColumnWithName(prefix + "medium_string")
  val mfrpn_string: ColumnWithName = ColumnWithName(prefix + "mfrpn_string")
  val kzrev_string: ColumnWithName = ColumnWithName(prefix + "kzrev_string")
  val inhal_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "inhal_decimal_13_3")
  val mo_profile_id_string: ColumnWithName = ColumnWithName(prefix + "mo_profile_id_string")
  val fiber_part1_string: ColumnWithName = ColumnWithName(prefix + "fiber_part1_string")
  val sprof_string: ColumnWithName = ColumnWithName(prefix + "sprof_string")
  val height_nonflat_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "height_nonflat_decimal_13_3")
  val _bev1_nestruccat_string: ColumnWithName = ColumnWithName(prefix + "_bev1_nestruccat_string")
  val prod_shape_string: ColumnWithName = ColumnWithName(prefix + "prod_shape_string")
  val _sapmp_aho_string: ColumnWithName = ColumnWithName(prefix + "_sapmp_aho_string")
  val qgrp_string: ColumnWithName = ColumnWithName(prefix + "qgrp_string")
  val etifo_string: ColumnWithName = ColumnWithName(prefix + "etifo_string")
  val _bev1_luleinh_string: ColumnWithName = ColumnWithName(prefix + "_bev1_luleinh_string")
  val kosch_string: ColumnWithName = ColumnWithName(prefix + "kosch_string")
  val zeinr_string: ColumnWithName = ColumnWithName(prefix + "zeinr_string")
  val mhdlp_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "mhdlp_decimal_3_0")
  val scm_shlf_lfe_req_max_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_shlf_lfe_req_max_decimal_11_0")
  val mstae_string: ColumnWithName = ColumnWithName(prefix + "mstae_string")
  val gtin_variant_string: ColumnWithName = ColumnWithName(prefix + "gtin_variant_string")
  val saity_string: ColumnWithName = ColumnWithName(prefix + "saity_string")
  val _vso_r_quan_unit_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_quan_unit_string")
  val ergei_string: ColumnWithName = ColumnWithName(prefix + "ergei_string")
  val ersda_string: ColumnWithName = ColumnWithName(prefix + "ersda_string")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val kzwsm_string: ColumnWithName = ColumnWithName(prefix + "kzwsm_string")
  val eannr_string: ColumnWithName = ColumnWithName(prefix + "eannr_string")
  val pmata_string: ColumnWithName = ColumnWithName(prefix + "pmata_string")
  val extwg_string: ColumnWithName = ColumnWithName(prefix + "extwg_string")
  val sgt_covsa_string: ColumnWithName = ColumnWithName(prefix + "sgt_covsa_string")
  val gewto_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "gewto_decimal_3_1")
  val kzgvh_string: ColumnWithName = ColumnWithName(prefix + "kzgvh_string")
  val saisj_string: ColumnWithName = ColumnWithName(prefix + "saisj_string")
  val blanz_string: ColumnWithName = ColumnWithName(prefix + "blanz_string")
  val whstc_string: ColumnWithName = ColumnWithName(prefix + "whstc_string")
  val satnr_string: ColumnWithName = ColumnWithName(prefix + "satnr_string")
  val gennr_string: ColumnWithName = ColumnWithName(prefix + "gennr_string")
  val aeklk_string: ColumnWithName = ColumnWithName(prefix + "aeklk_string")
  val scm_kitcomp_string: ColumnWithName = ColumnWithName(prefix + "scm_kitcomp_string")
  val sgt_scope_string: ColumnWithName = ColumnWithName(prefix + "sgt_scope_string")
  val srv_dura_uom_string: ColumnWithName = ColumnWithName(prefix + "srv_dura_uom_string")
  val dpcbt_string: ColumnWithName = ColumnWithName(prefix + "dpcbt_string")
  val sled_bbd_string: ColumnWithName = ColumnWithName(prefix + "sled_bbd_string")
  val voleh_string: ColumnWithName = ColumnWithName(prefix + "voleh_string")
  val _vso_r_pal_ind_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_pal_ind_string")
  val maxc_tol_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "maxc_tol_decimal_3_1")
  val mstdv_string: ColumnWithName = ColumnWithName(prefix + "mstdv_string")
  val bwvor_string: ColumnWithName = ColumnWithName(prefix + "bwvor_string")
  val fiber_part3_string: ColumnWithName = ColumnWithName(prefix + "fiber_part3_string")
  val zzgl_dts_tcs_fcode_string: ColumnWithName = ColumnWithName(prefix + "zzgl_dts_tcs_fcode_string")
  val rmatp_pb_string: ColumnWithName = ColumnWithName(prefix + "rmatp_pb_string")
  val etiar_string: ColumnWithName = ColumnWithName(prefix + "etiar_string")
  val meins_string: ColumnWithName = ColumnWithName(prefix + "meins_string")
  val cwqproc_string: ColumnWithName = ColumnWithName(prefix + "cwqproc_string")
  val fashgrd_string: ColumnWithName = ColumnWithName(prefix + "fashgrd_string")
  val ovlpn_string: ColumnWithName = ColumnWithName(prefix + "ovlpn_string")
  val fiber_part2_string: ColumnWithName = ColumnWithName(prefix + "fiber_part2_string")
  val care_code_string: ColumnWithName = ColumnWithName(prefix + "care_code_string")
  val matnr_external_string: ColumnWithName = ColumnWithName(prefix + "matnr_external_string")
  val last_changed_time_string: ColumnWithName = ColumnWithName(prefix + "last_changed_time_string")
  val pilferable_string: ColumnWithName = ColumnWithName(prefix + "pilferable_string")
  val profl_string: ColumnWithName = ColumnWithName(prefix + "profl_string")
  val _sapmp_sptr_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_sptr_decimal_13_3")
  val hazmat_string: ColumnWithName = ColumnWithName(prefix + "hazmat_string")
  val stfak_int: ColumnWithName = ColumnWithName(prefix + "stfak_int")
  val retdelc_string: ColumnWithName = ColumnWithName(prefix + "retdelc_string")
  val mhdhb_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "mhdhb_decimal_4_0")
  val _vso_r_pal_b_ht_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_vso_r_pal_b_ht_decimal_13_3")
  val spart_string: ColumnWithName = ColumnWithName(prefix + "spart_string")
  val serial_string: ColumnWithName = ColumnWithName(prefix + "serial_string")
  val vabme_string: ColumnWithName = ColumnWithName(prefix + "vabme_string")
  val scm_puom_string: ColumnWithName = ColumnWithName(prefix + "scm_puom_string")
  val inhbr_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "inhbr_decimal_13_3")
  val mbrsh_string: ColumnWithName = ColumnWithName(prefix + "mbrsh_string")
  val mfrnr_string: ColumnWithName = ColumnWithName(prefix + "mfrnr_string")
  val mprof_string: ColumnWithName = ColumnWithName(prefix + "mprof_string")
  val _cwm_tolgr_string: ColumnWithName = ColumnWithName(prefix + "_cwm_tolgr_string")
  val ean11_string: ColumnWithName = ColumnWithName(prefix + "ean11_string")
  val msbookpartno_string: ColumnWithName = ColumnWithName(prefix + "msbookpartno_string")
  val srv_escal_string: ColumnWithName = ColumnWithName(prefix + "srv_escal_string")
  val _sttpec_country_ref_string: ColumnWithName = ColumnWithName(prefix + "_sttpec_country_ref_string")
  val mstde_string: ColumnWithName = ColumnWithName(prefix + "mstde_string")
  val sgt_stat_string: ColumnWithName = ColumnWithName(prefix + "sgt_stat_string")
  val _vso_r_pal_ovr_w_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_vso_r_pal_ovr_w_decimal_13_3")
  val _sttpec_syncact_string: ColumnWithName = ColumnWithName(prefix + "_sttpec_syncact_string")
  val size2_string: ColumnWithName = ColumnWithName(prefix + "size2_string")
  val meabm_string: ColumnWithName = ColumnWithName(prefix + "meabm_string")
  val scm_matid_guid22_string: ColumnWithName = ColumnWithName(prefix + "scm_matid_guid22_string")
  val fsh_sealv_string: ColumnWithName = ColumnWithName(prefix + "fsh_sealv_string")
  val fiber_code1_string: ColumnWithName = ColumnWithName(prefix + "fiber_code1_string")
  val numtp_string: ColumnWithName = ColumnWithName(prefix + "numtp_string")
  val iloos_string: ColumnWithName = ColumnWithName(prefix + "iloos_string")
  val cwqtolgr_string: ColumnWithName = ColumnWithName(prefix + "cwqtolgr_string")
  val srv_serwi_string: ColumnWithName = ColumnWithName(prefix + "srv_serwi_string")
  val sgt_csgr_string: ColumnWithName = ColumnWithName(prefix + "sgt_csgr_string")
  val cmrel_string: ColumnWithName = ColumnWithName(prefix + "cmrel_string")
  val mfrgr_string: ColumnWithName = ColumnWithName(prefix + "mfrgr_string")
  val logunit_string: ColumnWithName = ColumnWithName(prefix + "logunit_string")
  val vhart_string: ColumnWithName = ColumnWithName(prefix + "vhart_string")
  val scm_excl_expedite_string: ColumnWithName = ColumnWithName(prefix + "scm_excl_expedite_string")
  val packcode_string: ColumnWithName = ColumnWithName(prefix + "packcode_string")
  val _vso_r_top_ind_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_top_ind_string")
  val maxc_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "maxc_decimal_15_3")
  val aenam_string: ColumnWithName = ColumnWithName(prefix + "aenam_string")
  val size1_atinn_string: ColumnWithName = ColumnWithName(prefix + "size1_atinn_string")
  val _vso_r_stack_ind_string: ColumnWithName = ColumnWithName(prefix + "_vso_r_stack_ind_string")
  val color_atinn_string: ColumnWithName = ColumnWithName(prefix + "color_atinn_string")
  val aeszn_string: ColumnWithName = ColumnWithName(prefix + "aeszn_string")
  val mhdrz_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "mhdrz_decimal_4_0")
  val cmeth_string: ColumnWithName = ColumnWithName(prefix + "cmeth_string")
  val loglev_reto_string: ColumnWithName = ColumnWithName(prefix + "loglev_reto_string")
  val cwqrel_string: ColumnWithName = ColumnWithName(prefix + "cwqrel_string")
  val bmatn_string: ColumnWithName = ColumnWithName(prefix + "bmatn_string")
  val bstme_string: ColumnWithName = ColumnWithName(prefix + "bstme_string")
  val fsh_sc_mid_string: ColumnWithName = ColumnWithName(prefix + "fsh_sc_mid_string")
  val kzeff_string: ColumnWithName = ColumnWithName(prefix + "kzeff_string")
  val nrfhg_string: ColumnWithName = ColumnWithName(prefix + "nrfhg_string")
  val bbtyp_string: ColumnWithName = ColumnWithName(prefix + "bbtyp_string")
  val zeifo_string: ColumnWithName = ColumnWithName(prefix + "zeifo_string")
  val scm_prod_paoopt_string: ColumnWithName = ColumnWithName(prefix + "scm_prod_paoopt_string")
  val maxdim_uom_string: ColumnWithName = ColumnWithName(prefix + "maxdim_uom_string")
  val matfi_string: ColumnWithName = ColumnWithName(prefix + "matfi_string")
  val adspc_spc_string: ColumnWithName = ColumnWithName(prefix + "adspc_spc_string")
  val bridge_max_slope_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "bridge_max_slope_decimal_5_2")
  val ihivi_string: ColumnWithName = ColumnWithName(prefix + "ihivi_string")
  val weora_string: ColumnWithName = ColumnWithName(prefix + "weora_string")
  val herkl_string: ColumnWithName = ColumnWithName(prefix + "herkl_string")
  val commodity_string: ColumnWithName = ColumnWithName(prefix + "commodity_string")
  val qmpur_string: ColumnWithName = ColumnWithName(prefix + "qmpur_string")
  val fiber_code3_string: ColumnWithName = ColumnWithName(prefix + "fiber_code3_string")
  val bismt_string: ColumnWithName = ColumnWithName(prefix + "bismt_string")
  val hutyp_dflt_string: ColumnWithName = ColumnWithName(prefix + "hutyp_dflt_string")
  val scm_shlf_lfe_req_min_decimal_11_0: ColumnWithName = ColumnWithName(prefix + "scm_shlf_lfe_req_min_decimal_11_0")
  val mtart_string: ColumnWithName = ColumnWithName(prefix + "mtart_string")
  val stoff_string: ColumnWithName = ColumnWithName(prefix + "stoff_string")
  val srv_dura_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "srv_dura_decimal_13_3")
  val zeiar_string: ColumnWithName = ColumnWithName(prefix + "zeiar_string")
  val kunnr_string: ColumnWithName = ColumnWithName(prefix + "kunnr_string")
  val tempb_string: ColumnWithName = ColumnWithName(prefix + "tempb_string")
  val labor_string: ColumnWithName = ColumnWithName(prefix + "labor_string")
  val _sttpec_sertype_int: ColumnWithName = ColumnWithName(prefix + "_sttpec_sertype_int")
  val bridge_tresh_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "bridge_tresh_decimal_5_2")
  val _sapmp_fbak_string: ColumnWithName = ColumnWithName(prefix + "_sapmp_fbak_string")
  val volum_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "volum_decimal_13_3")
  val wrkst_string: ColumnWithName = ColumnWithName(prefix + "wrkst_string")
  val identification_tag_type_string: ColumnWithName = ColumnWithName(prefix + "identification_tag_type_string")
  val mlgut_string: ColumnWithName = ColumnWithName(prefix + "mlgut_string")
  val taklv_string: ColumnWithName = ColumnWithName(prefix + "taklv_string")
  val hndlcode_string: ColumnWithName = ColumnWithName(prefix + "hndlcode_string")
  val normt_string: ColumnWithName = ColumnWithName(prefix + "normt_string")
  val kznfm_string: ColumnWithName = ColumnWithName(prefix + "kznfm_string")
  val sgt_rel_string: ColumnWithName = ColumnWithName(prefix + "sgt_rel_string")
  val textile_comp_ind_string: ColumnWithName = ColumnWithName(prefix + "textile_comp_ind_string")
  val lvorm_string: ColumnWithName = ColumnWithName(prefix + "lvorm_string")
  val ervol_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "ervol_decimal_13_3")
  val _sapmp_fbhk_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "_sapmp_fbhk_decimal_7_2")
  val som_tc_schema_string: ColumnWithName = ColumnWithName(prefix + "som_tc_schema_string")
  val _sttpec_prdcat_string: ColumnWithName = ColumnWithName(prefix + "_sttpec_prdcat_string")
  val qqtime_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "qqtime_decimal_3_0")
  val scm_bod_deplvl_string: ColumnWithName = ColumnWithName(prefix + "scm_bod_deplvl_string")
  val matkl_string: ColumnWithName = ColumnWithName(prefix + "matkl_string")
  val ps_smartform_string: ColumnWithName = ColumnWithName(prefix + "ps_smartform_string")
  val sales_material_string: ColumnWithName = ColumnWithName(prefix + "sales_material_string")
  val laeda_string: ColumnWithName = ColumnWithName(prefix + "laeda_string")
  val adprof_string: ColumnWithName = ColumnWithName(prefix + "adprof_string")
  val datab_string: ColumnWithName = ColumnWithName(prefix + "datab_string")
  val disst_string: ColumnWithName = ColumnWithName(prefix + "disst_string")
  val xchpf_string: ColumnWithName = ColumnWithName(prefix + "xchpf_string")
  val brgew_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "brgew_decimal_13_3")
  val _sapmp_abmein_string: ColumnWithName = ColumnWithName(prefix + "_sapmp_abmein_string")
  val brand_id_string: ColumnWithName = ColumnWithName(prefix + "brand_id_string")
  val icfa_string: ColumnWithName = ColumnWithName(prefix + "icfa_string")
  val plgtp_string: ColumnWithName = ColumnWithName(prefix + "plgtp_string")
  val dummy_prd_incl_eew_ps_string: ColumnWithName = ColumnWithName(prefix + "dummy_prd_incl_eew_ps_string")
  val _sapmp_kadp_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "_sapmp_kadp_decimal_5_2")
  val fiber_part4_string: ColumnWithName = ColumnWithName(prefix + "fiber_part4_string")
  val _vso_r_tol_b_ht_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_vso_r_tol_b_ht_decimal_13_3")
}

object C_mara extends C_mara("") {
  def as(alias: String): C_mara = new C_mara(alias + ".")
}

// AUTO GENERATED:END
