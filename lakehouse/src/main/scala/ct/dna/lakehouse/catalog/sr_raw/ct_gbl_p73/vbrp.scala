// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_p73

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_vbrp_part1(
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
    fsh_season_string: String,
    paobjnr_string: String,
    kvgr1_string: String,
    aubel_string: String,
    vgpos_ex_string: String,
    kowrr_string: String,
    delco_string: String,
    matnr_string: String,
    kvgr2_string: String,
    edatu_string: String,
    prsfd_string: String,
    fkber_string: String,
    abrvw_string: String,
    @Decimal(15, 2) netwr_decimal_15_2: BigDecimal,
    kdkg1_string: String,
    fsh_season_year_string: String,
    dp_gjahr_string: String,
    claims_taxation_string: String,
    kvgr4_string: String,
    erzet_string: String,
    sgt_scat_string: String,
    aplzl_oaa_string: String,
    j_1btaxlw2_string: String,
    perop_end_string: String,
    spara_string: String,
    dpcnr_string: String,
    atpkz_string: String,
    @Decimal(9, 5) kursk_decimal_9_5: BigDecimal,
    taxm5_string: String,
    ernam_string: String,
    msr_id_string: String,
    prctr_string: String,
    contnbr_string: String,
    pargb_string: String,
    vertn_string: String,
    dp_buzei_string: String,
    vkbur_string: String,
    j_3gpmaufv_string: String,
    @Decimal(13, 2) kzwi6_decimal_13_2: BigDecimal,
    contitm_string: String,
    provg_string: String,
    mprok_string: String,
    mvgr2_string: String,
    autyp_string: String,
    augru_auft_string: String,
    @Decimal(13, 2) kzwi5_decimal_13_2: BigDecimal,
    dp_bukrs_string: String,
    pprctr_string: String,
    @Decimal(13, 2) wavwr_decimal_13_2: BigDecimal,
    uvall_string: String,
    sgt_rcat_string: String,
    vstel_string: String,
    vgtyp_ex_string: String,
    knuma_ag_string: String,
    uepos_string: String,
    bemot_string: String,
    fpltr_string: String,
    j_1btaxlw1_string: String,
    aufnr_string: String,
    @Decimal(15, 3) ntgew_decimal_15_3: BigDecimal,
    pospa_string: String,
    packno_string: String,
    @Decimal(13, 2) mwsbp_decimal_13_2: BigDecimal,
    @Decimal(9, 5) stcur_decimal_9_5: BigDecimal,
    bosfar_string: String,
    lland_auft_string: String,
    j_3getypa_string: String,
    arktx_string: String,
    taxm2_string: String,
    sgtxt_string: String,
    mvgr3_string: String,
    audat_string: String,
    regio_auft_string: String,
    vgtyp_string: String,
    taxm9_string: String,
    @Decimal(13, 2) kzwi1_decimal_13_2: BigDecimal,
    @Decimal(15, 2) brtwr_decimal_15_2: BigDecimal,
    taxm7_string: String,
    @Decimal(9, 5) akkur_decimal_9_5: BigDecimal,
    kokrs_string: String,
    j_1btxsdc_string: String,
    j_1atxrel_string: String,
    aplzl_string: String,
    vbeln_string: String,
    vgtyp_ext_string: String,
    kurrf_dat_orig_string: String,
    abrbg_string: String,
    upmat_string: String,
    gewei_string: String,
    vgbel_ex_string: String,
    j_1aidatep_string: String,
    aland_string: String,
    charg_string: String,
    compreas_string: String,
    j_1btaxlw5_string: String,
    aupos_string: String,
    bzirk_auft_string: String,
    prodh_string: String,
    @Decimal(15, 3) brgew_decimal_15_3: BigDecimal,
    pmatn_string: String,
    abfor_string: String,
    vkorg_auft_string: String,
    prosa_string: String,
    lgort_string: String,
    j_1aindxp_string: String,
    kdgrp_auft_string: String,
    @Decimal(13, 2) kzwi3_decimal_13_2: BigDecimal,
    @Decimal(5, 0) umvkn_decimal_5_0: BigDecimal,
    fund_usage_item_binary: Array[Byte],
    taxm6_string: String,
    mvgr5_string: String,
    posnv_string: String,
    posar_string: String,
    werks_string: String,
    j_3gpmaufe_string: String,
    logsys_string: String,
    fonds_string: String,
    sktof_string: String,
    taxm1_string: String,
    cuobj_string: String,
    kvgr3_string: String,
    kursk_dat_string: String,
    mandt_string: String,
    taxm8_string: String,
    kondm_string: String,
    @Decimal(15, 3) volum_decimal_15_3: BigDecimal,
    vtweg_auft_string: String,
    prefe_string: String,
    eannr_string: String,
    kdkg3_string: String,
    rrrel_string: String,
    fplnr_string: String,
    j_1aregio_string: String,
    plaufz_string: String,
    stafo_string: String,
    bonus_string: String,
    wkcty_string: String,
    konda_auft_string: String,
    dpnrb_string: String,
    voleh_string: String,
    txjcd_string: String,
    vkaus_string: String,
    @Decimal(13, 3) smeng_decimal_13_3: BigDecimal,
    fareg_string: String,
    rplnr_string: String,
    wrf_charstc3_string: String,
    aufpl_string: String,
    perop_beg_string: String,
    uepvw_string: String,
    msr_refund_code_string: String,
    kzfme_string: String,
    fsh_theme_string: String,
    meins_string: String,
    kdkg4_string: String,
    farr_reltype_string: String,
    j_1agicd_string: String,
    msr_ret_reason_string: String,
    mvgr4_string: String,
    @Decimal(13, 3) fkimg_decimal_13_3: BigDecimal,
    wgru1_string: String,
    fbuda_string: String,
    j_1btaxlw4_string: String,
    nrab_knumh_string: String,
    koupd_string: String,
    plauez_string: String,
    @Decimal(13, 2) kzwi4_decimal_13_2: BigDecimal,
    @Decimal(13, 2) kzwi2_decimal_13_2: BigDecimal,
    spart_string: String,
    mwskz_string: String,
    vertt_string: String,
    cuobj_ch_string: String,
    @Decimal(11, 2) cmpre_decimal_11_2: BigDecimal,
    j_1adtyp_string: String,
    zzfiresgnup_string: String,
    j_3gbelnri_string: String,
    j_1arfz_string: String,
    budget_pd_string: String,
    ean11_string: String,
    pltyp_auft_string: String,
    ktgrm_string: String,
    ps_psp_pnr_string: String,
    matwa_string: String,
    sernr_string: String,
    dispute_case_binary: Array[Byte],
    auref_string: String,
    zz_eudr_val_string: String,
    wminr_string: String,
    aktnr_string: String,
    pstyv_string: String,
    cmpre_flt_double: BoxedDouble,
    wgru2_string: String,
    @Decimal(13, 3) fklmg_decimal_13_3: BigDecimal,
    xchar_string: String,
    fsh_collection_string: String,
    @Decimal(5, 0) umvkz_decimal_5_0: BigDecimal,
    taxm4_string: String,
    aufpl_oaa_string: String,
    mvgr1_string: String,
    @Decimal(13, 2) nrab_value_decimal_13_2: BigDecimal,
    uvprs_string: String,
    dp_belnr_string: String,
    j_1btaxlw3_string: String,
    @Decimal(13, 3) lmeng_decimal_13_3: BigDecimal,
    stadat_string: String,
    campaign_binary: Array[Byte],
    prsdt_string: String,
    kdkg2_string: String,
    @Decimal(13, 2) skfbp_decimal_13_2: BigDecimal,
    vbelv_string: String,
    wkcou_string: String,
    taxm3_string: String,
    vkgrp_string: String,
    zz_eudr_ref_string: String,
    dcpnr_string: String,
    knuma_pi_string: String,
    wktps_string: String,
    vgbel_string: String,
    j_3getype_string: String,
    shkzg_string: String,
    bwtar_string: String,
    cmpnt_string: String,
    j_1bcfop_string: String,
    kdkg5_string: String,
    uecha_string: String,
    prs_work_period_string: String,
    wktnr_string: String,
    gsber_string: String,
    ukonm_string: String,
    @Decimal(13, 2) bonba_decimal_13_2: BigDecimal,
    wkreg_string: String,
    matkl_string: String,
    abges_double: BoxedDouble,
    wrf_charstc1_string: String,
    kostl_string: String,
    wrf_charstc2_string: String,
    grant_nbr_string: String,
    kvgr5_string: String,
    fistl_string: String,
    posnr_string: String,
    fmfgus_key_string: String,
    vrkme_string: String,
    erdat_string: String
) extends Entity

case class E_vbrp_part2(
    @PK _mk_org: String,
    j_3gorgueb_string: String,
    vgpos_string: String
) extends Entity

object vbrp extends TableSpec[Joined[E_vbrp_part1, E_vbrp_part2]](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_vbrp(prefix: String) extends ColumnWithNameAccessor {
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
  val fsh_season_string: ColumnWithName = ColumnWithName(prefix + "fsh_season_string")
  val paobjnr_string: ColumnWithName = ColumnWithName(prefix + "paobjnr_string")
  val kvgr1_string: ColumnWithName = ColumnWithName(prefix + "kvgr1_string")
  val aubel_string: ColumnWithName = ColumnWithName(prefix + "aubel_string")
  val vgpos_ex_string: ColumnWithName = ColumnWithName(prefix + "vgpos_ex_string")
  val kowrr_string: ColumnWithName = ColumnWithName(prefix + "kowrr_string")
  val delco_string: ColumnWithName = ColumnWithName(prefix + "delco_string")
  val matnr_string: ColumnWithName = ColumnWithName(prefix + "matnr_string")
  val kvgr2_string: ColumnWithName = ColumnWithName(prefix + "kvgr2_string")
  val edatu_string: ColumnWithName = ColumnWithName(prefix + "edatu_string")
  val prsfd_string: ColumnWithName = ColumnWithName(prefix + "prsfd_string")
  val fkber_string: ColumnWithName = ColumnWithName(prefix + "fkber_string")
  val abrvw_string: ColumnWithName = ColumnWithName(prefix + "abrvw_string")
  val netwr_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "netwr_decimal_15_2")
  val kdkg1_string: ColumnWithName = ColumnWithName(prefix + "kdkg1_string")
  val fsh_season_year_string: ColumnWithName = ColumnWithName(prefix + "fsh_season_year_string")
  val dp_gjahr_string: ColumnWithName = ColumnWithName(prefix + "dp_gjahr_string")
  val claims_taxation_string: ColumnWithName = ColumnWithName(prefix + "claims_taxation_string")
  val kvgr4_string: ColumnWithName = ColumnWithName(prefix + "kvgr4_string")
  val erzet_string: ColumnWithName = ColumnWithName(prefix + "erzet_string")
  val sgt_scat_string: ColumnWithName = ColumnWithName(prefix + "sgt_scat_string")
  val aplzl_oaa_string: ColumnWithName = ColumnWithName(prefix + "aplzl_oaa_string")
  val j_1btaxlw2_string: ColumnWithName = ColumnWithName(prefix + "j_1btaxlw2_string")
  val perop_end_string: ColumnWithName = ColumnWithName(prefix + "perop_end_string")
  val spara_string: ColumnWithName = ColumnWithName(prefix + "spara_string")
  val dpcnr_string: ColumnWithName = ColumnWithName(prefix + "dpcnr_string")
  val atpkz_string: ColumnWithName = ColumnWithName(prefix + "atpkz_string")
  val kursk_decimal_9_5: ColumnWithName = ColumnWithName(prefix + "kursk_decimal_9_5")
  val taxm5_string: ColumnWithName = ColumnWithName(prefix + "taxm5_string")
  val ernam_string: ColumnWithName = ColumnWithName(prefix + "ernam_string")
  val msr_id_string: ColumnWithName = ColumnWithName(prefix + "msr_id_string")
  val prctr_string: ColumnWithName = ColumnWithName(prefix + "prctr_string")
  val contnbr_string: ColumnWithName = ColumnWithName(prefix + "contnbr_string")
  val pargb_string: ColumnWithName = ColumnWithName(prefix + "pargb_string")
  val vertn_string: ColumnWithName = ColumnWithName(prefix + "vertn_string")
  val dp_buzei_string: ColumnWithName = ColumnWithName(prefix + "dp_buzei_string")
  val vkbur_string: ColumnWithName = ColumnWithName(prefix + "vkbur_string")
  val j_3gpmaufv_string: ColumnWithName = ColumnWithName(prefix + "j_3gpmaufv_string")
  val kzwi6_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi6_decimal_13_2")
  val contitm_string: ColumnWithName = ColumnWithName(prefix + "contitm_string")
  val provg_string: ColumnWithName = ColumnWithName(prefix + "provg_string")
  val mprok_string: ColumnWithName = ColumnWithName(prefix + "mprok_string")
  val mvgr2_string: ColumnWithName = ColumnWithName(prefix + "mvgr2_string")
  val autyp_string: ColumnWithName = ColumnWithName(prefix + "autyp_string")
  val augru_auft_string: ColumnWithName = ColumnWithName(prefix + "augru_auft_string")
  val kzwi5_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi5_decimal_13_2")
  val dp_bukrs_string: ColumnWithName = ColumnWithName(prefix + "dp_bukrs_string")
  val pprctr_string: ColumnWithName = ColumnWithName(prefix + "pprctr_string")
  val wavwr_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "wavwr_decimal_13_2")
  val uvall_string: ColumnWithName = ColumnWithName(prefix + "uvall_string")
  val sgt_rcat_string: ColumnWithName = ColumnWithName(prefix + "sgt_rcat_string")
  val vstel_string: ColumnWithName = ColumnWithName(prefix + "vstel_string")
  val vgtyp_ex_string: ColumnWithName = ColumnWithName(prefix + "vgtyp_ex_string")
  val knuma_ag_string: ColumnWithName = ColumnWithName(prefix + "knuma_ag_string")
  val uepos_string: ColumnWithName = ColumnWithName(prefix + "uepos_string")
  val bemot_string: ColumnWithName = ColumnWithName(prefix + "bemot_string")
  val fpltr_string: ColumnWithName = ColumnWithName(prefix + "fpltr_string")
  val j_1btaxlw1_string: ColumnWithName = ColumnWithName(prefix + "j_1btaxlw1_string")
  val aufnr_string: ColumnWithName = ColumnWithName(prefix + "aufnr_string")
  val ntgew_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "ntgew_decimal_15_3")
  val pospa_string: ColumnWithName = ColumnWithName(prefix + "pospa_string")
  val packno_string: ColumnWithName = ColumnWithName(prefix + "packno_string")
  val mwsbp_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "mwsbp_decimal_13_2")
  val stcur_decimal_9_5: ColumnWithName = ColumnWithName(prefix + "stcur_decimal_9_5")
  val bosfar_string: ColumnWithName = ColumnWithName(prefix + "bosfar_string")
  val lland_auft_string: ColumnWithName = ColumnWithName(prefix + "lland_auft_string")
  val j_3getypa_string: ColumnWithName = ColumnWithName(prefix + "j_3getypa_string")
  val arktx_string: ColumnWithName = ColumnWithName(prefix + "arktx_string")
  val taxm2_string: ColumnWithName = ColumnWithName(prefix + "taxm2_string")
  val sgtxt_string: ColumnWithName = ColumnWithName(prefix + "sgtxt_string")
  val mvgr3_string: ColumnWithName = ColumnWithName(prefix + "mvgr3_string")
  val audat_string: ColumnWithName = ColumnWithName(prefix + "audat_string")
  val regio_auft_string: ColumnWithName = ColumnWithName(prefix + "regio_auft_string")
  val vgtyp_string: ColumnWithName = ColumnWithName(prefix + "vgtyp_string")
  val taxm9_string: ColumnWithName = ColumnWithName(prefix + "taxm9_string")
  val kzwi1_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi1_decimal_13_2")
  val brtwr_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "brtwr_decimal_15_2")
  val taxm7_string: ColumnWithName = ColumnWithName(prefix + "taxm7_string")
  val akkur_decimal_9_5: ColumnWithName = ColumnWithName(prefix + "akkur_decimal_9_5")
  val kokrs_string: ColumnWithName = ColumnWithName(prefix + "kokrs_string")
  val j_1btxsdc_string: ColumnWithName = ColumnWithName(prefix + "j_1btxsdc_string")
  val j_1atxrel_string: ColumnWithName = ColumnWithName(prefix + "j_1atxrel_string")
  val aplzl_string: ColumnWithName = ColumnWithName(prefix + "aplzl_string")
  val vbeln_string: ColumnWithName = ColumnWithName(prefix + "vbeln_string")
  val vgtyp_ext_string: ColumnWithName = ColumnWithName(prefix + "vgtyp_ext_string")
  val kurrf_dat_orig_string: ColumnWithName = ColumnWithName(prefix + "kurrf_dat_orig_string")
  val abrbg_string: ColumnWithName = ColumnWithName(prefix + "abrbg_string")
  val upmat_string: ColumnWithName = ColumnWithName(prefix + "upmat_string")
  val gewei_string: ColumnWithName = ColumnWithName(prefix + "gewei_string")
  val vgbel_ex_string: ColumnWithName = ColumnWithName(prefix + "vgbel_ex_string")
  val j_1aidatep_string: ColumnWithName = ColumnWithName(prefix + "j_1aidatep_string")
  val aland_string: ColumnWithName = ColumnWithName(prefix + "aland_string")
  val charg_string: ColumnWithName = ColumnWithName(prefix + "charg_string")
  val compreas_string: ColumnWithName = ColumnWithName(prefix + "compreas_string")
  val j_1btaxlw5_string: ColumnWithName = ColumnWithName(prefix + "j_1btaxlw5_string")
  val aupos_string: ColumnWithName = ColumnWithName(prefix + "aupos_string")
  val bzirk_auft_string: ColumnWithName = ColumnWithName(prefix + "bzirk_auft_string")
  val prodh_string: ColumnWithName = ColumnWithName(prefix + "prodh_string")
  val brgew_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "brgew_decimal_15_3")
  val pmatn_string: ColumnWithName = ColumnWithName(prefix + "pmatn_string")
  val abfor_string: ColumnWithName = ColumnWithName(prefix + "abfor_string")
  val vkorg_auft_string: ColumnWithName = ColumnWithName(prefix + "vkorg_auft_string")
  val prosa_string: ColumnWithName = ColumnWithName(prefix + "prosa_string")
  val lgort_string: ColumnWithName = ColumnWithName(prefix + "lgort_string")
  val j_1aindxp_string: ColumnWithName = ColumnWithName(prefix + "j_1aindxp_string")
  val kdgrp_auft_string: ColumnWithName = ColumnWithName(prefix + "kdgrp_auft_string")
  val kzwi3_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi3_decimal_13_2")
  val umvkn_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "umvkn_decimal_5_0")
  val fund_usage_item_binary: ColumnWithName = ColumnWithName(prefix + "fund_usage_item_binary")
  val taxm6_string: ColumnWithName = ColumnWithName(prefix + "taxm6_string")
  val mvgr5_string: ColumnWithName = ColumnWithName(prefix + "mvgr5_string")
  val posnv_string: ColumnWithName = ColumnWithName(prefix + "posnv_string")
  val posar_string: ColumnWithName = ColumnWithName(prefix + "posar_string")
  val werks_string: ColumnWithName = ColumnWithName(prefix + "werks_string")
  val j_3gpmaufe_string: ColumnWithName = ColumnWithName(prefix + "j_3gpmaufe_string")
  val logsys_string: ColumnWithName = ColumnWithName(prefix + "logsys_string")
  val fonds_string: ColumnWithName = ColumnWithName(prefix + "fonds_string")
  val sktof_string: ColumnWithName = ColumnWithName(prefix + "sktof_string")
  val taxm1_string: ColumnWithName = ColumnWithName(prefix + "taxm1_string")
  val cuobj_string: ColumnWithName = ColumnWithName(prefix + "cuobj_string")
  val kvgr3_string: ColumnWithName = ColumnWithName(prefix + "kvgr3_string")
  val kursk_dat_string: ColumnWithName = ColumnWithName(prefix + "kursk_dat_string")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val taxm8_string: ColumnWithName = ColumnWithName(prefix + "taxm8_string")
  val kondm_string: ColumnWithName = ColumnWithName(prefix + "kondm_string")
  val volum_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "volum_decimal_15_3")
  val vtweg_auft_string: ColumnWithName = ColumnWithName(prefix + "vtweg_auft_string")
  val prefe_string: ColumnWithName = ColumnWithName(prefix + "prefe_string")
  val eannr_string: ColumnWithName = ColumnWithName(prefix + "eannr_string")
  val kdkg3_string: ColumnWithName = ColumnWithName(prefix + "kdkg3_string")
  val rrrel_string: ColumnWithName = ColumnWithName(prefix + "rrrel_string")
  val fplnr_string: ColumnWithName = ColumnWithName(prefix + "fplnr_string")
  val j_1aregio_string: ColumnWithName = ColumnWithName(prefix + "j_1aregio_string")
  val plaufz_string: ColumnWithName = ColumnWithName(prefix + "plaufz_string")
  val stafo_string: ColumnWithName = ColumnWithName(prefix + "stafo_string")
  val bonus_string: ColumnWithName = ColumnWithName(prefix + "bonus_string")
  val wkcty_string: ColumnWithName = ColumnWithName(prefix + "wkcty_string")
  val konda_auft_string: ColumnWithName = ColumnWithName(prefix + "konda_auft_string")
  val dpnrb_string: ColumnWithName = ColumnWithName(prefix + "dpnrb_string")
  val voleh_string: ColumnWithName = ColumnWithName(prefix + "voleh_string")
  val txjcd_string: ColumnWithName = ColumnWithName(prefix + "txjcd_string")
  val vkaus_string: ColumnWithName = ColumnWithName(prefix + "vkaus_string")
  val smeng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "smeng_decimal_13_3")
  val fareg_string: ColumnWithName = ColumnWithName(prefix + "fareg_string")
  val rplnr_string: ColumnWithName = ColumnWithName(prefix + "rplnr_string")
  val wrf_charstc3_string: ColumnWithName = ColumnWithName(prefix + "wrf_charstc3_string")
  val aufpl_string: ColumnWithName = ColumnWithName(prefix + "aufpl_string")
  val perop_beg_string: ColumnWithName = ColumnWithName(prefix + "perop_beg_string")
  val uepvw_string: ColumnWithName = ColumnWithName(prefix + "uepvw_string")
  val msr_refund_code_string: ColumnWithName = ColumnWithName(prefix + "msr_refund_code_string")
  val kzfme_string: ColumnWithName = ColumnWithName(prefix + "kzfme_string")
  val fsh_theme_string: ColumnWithName = ColumnWithName(prefix + "fsh_theme_string")
  val meins_string: ColumnWithName = ColumnWithName(prefix + "meins_string")
  val kdkg4_string: ColumnWithName = ColumnWithName(prefix + "kdkg4_string")
  val farr_reltype_string: ColumnWithName = ColumnWithName(prefix + "farr_reltype_string")
  val j_1agicd_string: ColumnWithName = ColumnWithName(prefix + "j_1agicd_string")
  val msr_ret_reason_string: ColumnWithName = ColumnWithName(prefix + "msr_ret_reason_string")
  val mvgr4_string: ColumnWithName = ColumnWithName(prefix + "mvgr4_string")
  val fkimg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "fkimg_decimal_13_3")
  val wgru1_string: ColumnWithName = ColumnWithName(prefix + "wgru1_string")
  val fbuda_string: ColumnWithName = ColumnWithName(prefix + "fbuda_string")
  val j_1btaxlw4_string: ColumnWithName = ColumnWithName(prefix + "j_1btaxlw4_string")
  val nrab_knumh_string: ColumnWithName = ColumnWithName(prefix + "nrab_knumh_string")
  val koupd_string: ColumnWithName = ColumnWithName(prefix + "koupd_string")
  val plauez_string: ColumnWithName = ColumnWithName(prefix + "plauez_string")
  val kzwi4_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi4_decimal_13_2")
  val kzwi2_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "kzwi2_decimal_13_2")
  val spart_string: ColumnWithName = ColumnWithName(prefix + "spart_string")
  val mwskz_string: ColumnWithName = ColumnWithName(prefix + "mwskz_string")
  val vertt_string: ColumnWithName = ColumnWithName(prefix + "vertt_string")
  val cuobj_ch_string: ColumnWithName = ColumnWithName(prefix + "cuobj_ch_string")
  val cmpre_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "cmpre_decimal_11_2")
  val j_1adtyp_string: ColumnWithName = ColumnWithName(prefix + "j_1adtyp_string")
  val zzfiresgnup_string: ColumnWithName = ColumnWithName(prefix + "zzfiresgnup_string")
  val j_3gbelnri_string: ColumnWithName = ColumnWithName(prefix + "j_3gbelnri_string")
  val j_1arfz_string: ColumnWithName = ColumnWithName(prefix + "j_1arfz_string")
  val budget_pd_string: ColumnWithName = ColumnWithName(prefix + "budget_pd_string")
  val ean11_string: ColumnWithName = ColumnWithName(prefix + "ean11_string")
  val pltyp_auft_string: ColumnWithName = ColumnWithName(prefix + "pltyp_auft_string")
  val ktgrm_string: ColumnWithName = ColumnWithName(prefix + "ktgrm_string")
  val ps_psp_pnr_string: ColumnWithName = ColumnWithName(prefix + "ps_psp_pnr_string")
  val matwa_string: ColumnWithName = ColumnWithName(prefix + "matwa_string")
  val sernr_string: ColumnWithName = ColumnWithName(prefix + "sernr_string")
  val dispute_case_binary: ColumnWithName = ColumnWithName(prefix + "dispute_case_binary")
  val auref_string: ColumnWithName = ColumnWithName(prefix + "auref_string")
  val zz_eudr_val_string: ColumnWithName = ColumnWithName(prefix + "zz_eudr_val_string")
  val wminr_string: ColumnWithName = ColumnWithName(prefix + "wminr_string")
  val aktnr_string: ColumnWithName = ColumnWithName(prefix + "aktnr_string")
  val pstyv_string: ColumnWithName = ColumnWithName(prefix + "pstyv_string")
  val cmpre_flt_double: ColumnWithName = ColumnWithName(prefix + "cmpre_flt_double")
  val wgru2_string: ColumnWithName = ColumnWithName(prefix + "wgru2_string")
  val fklmg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "fklmg_decimal_13_3")
  val xchar_string: ColumnWithName = ColumnWithName(prefix + "xchar_string")
  val fsh_collection_string: ColumnWithName = ColumnWithName(prefix + "fsh_collection_string")
  val umvkz_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "umvkz_decimal_5_0")
  val taxm4_string: ColumnWithName = ColumnWithName(prefix + "taxm4_string")
  val aufpl_oaa_string: ColumnWithName = ColumnWithName(prefix + "aufpl_oaa_string")
  val mvgr1_string: ColumnWithName = ColumnWithName(prefix + "mvgr1_string")
  val nrab_value_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "nrab_value_decimal_13_2")
  val uvprs_string: ColumnWithName = ColumnWithName(prefix + "uvprs_string")
  val dp_belnr_string: ColumnWithName = ColumnWithName(prefix + "dp_belnr_string")
  val j_1btaxlw3_string: ColumnWithName = ColumnWithName(prefix + "j_1btaxlw3_string")
  val lmeng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "lmeng_decimal_13_3")
  val stadat_string: ColumnWithName = ColumnWithName(prefix + "stadat_string")
  val campaign_binary: ColumnWithName = ColumnWithName(prefix + "campaign_binary")
  val prsdt_string: ColumnWithName = ColumnWithName(prefix + "prsdt_string")
  val kdkg2_string: ColumnWithName = ColumnWithName(prefix + "kdkg2_string")
  val skfbp_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "skfbp_decimal_13_2")
  val vbelv_string: ColumnWithName = ColumnWithName(prefix + "vbelv_string")
  val wkcou_string: ColumnWithName = ColumnWithName(prefix + "wkcou_string")
  val taxm3_string: ColumnWithName = ColumnWithName(prefix + "taxm3_string")
  val vkgrp_string: ColumnWithName = ColumnWithName(prefix + "vkgrp_string")
  val zz_eudr_ref_string: ColumnWithName = ColumnWithName(prefix + "zz_eudr_ref_string")
  val dcpnr_string: ColumnWithName = ColumnWithName(prefix + "dcpnr_string")
  val knuma_pi_string: ColumnWithName = ColumnWithName(prefix + "knuma_pi_string")
  val wktps_string: ColumnWithName = ColumnWithName(prefix + "wktps_string")
  val vgbel_string: ColumnWithName = ColumnWithName(prefix + "vgbel_string")
  val j_3getype_string: ColumnWithName = ColumnWithName(prefix + "j_3getype_string")
  val shkzg_string: ColumnWithName = ColumnWithName(prefix + "shkzg_string")
  val bwtar_string: ColumnWithName = ColumnWithName(prefix + "bwtar_string")
  val cmpnt_string: ColumnWithName = ColumnWithName(prefix + "cmpnt_string")
  val j_1bcfop_string: ColumnWithName = ColumnWithName(prefix + "j_1bcfop_string")
  val kdkg5_string: ColumnWithName = ColumnWithName(prefix + "kdkg5_string")
  val uecha_string: ColumnWithName = ColumnWithName(prefix + "uecha_string")
  val prs_work_period_string: ColumnWithName = ColumnWithName(prefix + "prs_work_period_string")
  val wktnr_string: ColumnWithName = ColumnWithName(prefix + "wktnr_string")
  val gsber_string: ColumnWithName = ColumnWithName(prefix + "gsber_string")
  val ukonm_string: ColumnWithName = ColumnWithName(prefix + "ukonm_string")
  val bonba_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "bonba_decimal_13_2")
  val wkreg_string: ColumnWithName = ColumnWithName(prefix + "wkreg_string")
  val matkl_string: ColumnWithName = ColumnWithName(prefix + "matkl_string")
  val abges_double: ColumnWithName = ColumnWithName(prefix + "abges_double")
  val wrf_charstc1_string: ColumnWithName = ColumnWithName(prefix + "wrf_charstc1_string")
  val kostl_string: ColumnWithName = ColumnWithName(prefix + "kostl_string")
  val wrf_charstc2_string: ColumnWithName = ColumnWithName(prefix + "wrf_charstc2_string")
  val grant_nbr_string: ColumnWithName = ColumnWithName(prefix + "grant_nbr_string")
  val kvgr5_string: ColumnWithName = ColumnWithName(prefix + "kvgr5_string")
  val fistl_string: ColumnWithName = ColumnWithName(prefix + "fistl_string")
  val posnr_string: ColumnWithName = ColumnWithName(prefix + "posnr_string")
  val fmfgus_key_string: ColumnWithName = ColumnWithName(prefix + "fmfgus_key_string")
  val vrkme_string: ColumnWithName = ColumnWithName(prefix + "vrkme_string")
  val erdat_string: ColumnWithName = ColumnWithName(prefix + "erdat_string")
  val j_3gorgueb_string: ColumnWithName = ColumnWithName(prefix + "j_3gorgueb_string")
  val vgpos_string: ColumnWithName = ColumnWithName(prefix + "vgpos_string")
}

object C_vbrp extends C_vbrp("") {
  def as(alias: String): C_vbrp = new C_vbrp(alias + ".")
}

// AUTO GENERATED:END
