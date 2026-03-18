// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_p43

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_ekko(
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
    shipcond_string: String,
    abgru_string: String,
    bukrs_string: String,
    zterm_string: String,
    procstat_string: String,
    fixpo_string: String,
    @Decimal(3, 0) zbd1t_decimal_3_0: BigDecimal,
    logsy_string: String,
    description_string: String,
    bsart_string: String,
    inco3_l_string: String,
    fsh_os_stg_change_string: String,
    bnddt_string: String,
    ernam_string: String,
    msr_id_string: String,
    verkf_string: String,
    statu_string: String,
    spras_string: String,
    eq_eindt_string: String,
    lphis_string: String,
    ltsnr_allow_string: String,
    kdatb_string: String,
    tru_string: String,
    @Decimal(3, 0) zbd2t_decimal_3_0: BigDecimal,
    kornr_string: String,
    fsh_vas_last_item_string: String,
    key_id_string: String,
    bwbdt_string: String,
    ebeln_string: String,
    threshold_exists_string: String,
    reswk_string: String,
    contract_allow_string: String,
    @Decimal(17, 2) otb_spec_value_decimal_17_2: BigDecimal,
    frgzu_string: String,
    gwldt_string: String,
    frgsx_string: String,
    @Decimal(11, 2) dpamt_decimal_11_2: BigDecimal,
    addnr_string: String,
    zzalkunnr_string: String,
    pincr_string: String,
    llief_string: String,
    pco_string: String,
    angdt_string: String,
    con_distr_lev_string: String,
    exnum_string: String,
    eindt_allow_string: String,
    con_otb_req_string: String,
    source_logsys_string: String,
    fsh_transaction_string: String,
    dno_string: String,
    plc_string: String,
    pcy_string: String,
    check_type_string: String,
    unsez_string: String,
    @Decimal(5, 3) zbd1p_decimal_5_3: BigDecimal,
    postat_string: String,
    stceg_string: String,
    zzdummy_string: String,
    stceg_l_string: String,
    reason_code_string: String,
    otb_curr_string: String,
    bstyp_string: String,
    weakt_string: String,
    zzwekunnr_string: String,
    otb_level_string: String,
    ecc_string: String,
    pohf_type_string: String,
    pcd_string: String,
    kalsm_string: String,
    autlf_string: String,
    force_id_string: String,
    bsakz_string: String,
    rettp_string: String,
    externalsystem_string: String,
    cur_string: String,
    fixpo_allow_string: String,
    mandt_string: String,
    eq_werks_string: String,
    konnr_string: String,
    pcn_string: String,
    memorytype_string: String,
    stafo_string: String,
    ipp_string: String,
    fsh_snst_status_string: String,
    @Decimal(9, 5) wkurs_decimal_9_5: BigDecimal,
    ekgrp_string: String,
    vzskz_string: String,
    inco1_string: String,
    bedat_string: String,
    telf1_string: String,
    @Decimal(3, 0) zbd3t_decimal_3_0: BigDecimal,
    handoverloc_string: String,
    hierarchy_exists_string: String,
    dpdat_string: String,
    adrnr_string: String,
    @Decimal(21, 7) ext_rev_tmstmp_decimal_21_7: BigDecimal,
    submi_string: String,
    inco2_l_string: String,
    ihran_string: String,
    angnr_string: String,
    vsart_string: String,
    ekorg_string: String,
    frgke_string: String,
    lblif_string: String,
    aurel_allow_string: String,
    upinc_string: String,
    spr_rsn_profile_string: String,
    ausnr_string: String,
    lifre_string: String,
    scmproc_string: String,
    ekgrp_allow_string: String,
    dptyp_string: String,
    @Decimal(5, 3) zbd2p_decimal_5_3: BigDecimal,
    top_string: String,
    pstyp_allow_string: String,
    amn_string: String,
    frgrl_string: String,
    con_prebook_lev_string: String,
    @Decimal(15, 2) ktwrt_decimal_15_2: BigDecimal,
    delper_allow_string: String,
    pbn_string: String,
    fsh_item_group_string: String,
    inco2_string: String,
    loekz_string: String,
    lponr_string: String,
    stako_string: String,
    otb_status_string: String,
    absgr_string: String,
    aedat_string: String,
    externalreferenceid_string: String,
    @Decimal(17, 2) otb_value_decimal_17_2: BigDecimal,
    memory_string: String,
    legal_contract_string: String,
    otb_cond_type_string: String,
    @Decimal(5, 2) retpc_decimal_5_2: BigDecimal,
    frggr_string: String,
    lifnr_string: String,
    dpt_string: String,
    revno_string: String,
    otb_reason_string: String,
    knumv_string: String,
    force_cnt_string: String,
    reloc_id_string: String,
    release_date_string: String,
    lands_string: String,
    zzunloadpt_string: String,
    kunnr_string: String,
    ihrez_string: String,
    kufix_string: String,
    eco_string: String,
    key_id_allow_string: String,
    @Decimal(5, 2) dppct_decimal_5_2: BigDecimal,
    kdate_string: String,
    @Decimal(17, 2) otb_res_value_decimal_17_2: BigDecimal,
    budg_type_string: String,
    werks_allow_string: String,
    reloc_seq_id_string: String,
    @Decimal(15, 2) rlwrt_decimal_15_2: BigDecimal,
    sus_string: String,
    incov_string: String,
    waers_string: String,
    smb_string: String
) extends Entity

object ekko extends TableSpec[E_ekko](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_ekko(prefix: String) extends ColumnWithNameAccessor {
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
  val shipcond_string: ColumnWithName = ColumnWithName(prefix + "shipcond_string")
  val abgru_string: ColumnWithName = ColumnWithName(prefix + "abgru_string")
  val bukrs_string: ColumnWithName = ColumnWithName(prefix + "bukrs_string")
  val zterm_string: ColumnWithName = ColumnWithName(prefix + "zterm_string")
  val procstat_string: ColumnWithName = ColumnWithName(prefix + "procstat_string")
  val fixpo_string: ColumnWithName = ColumnWithName(prefix + "fixpo_string")
  val zbd1t_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "zbd1t_decimal_3_0")
  val logsy_string: ColumnWithName = ColumnWithName(prefix + "logsy_string")
  val description_string: ColumnWithName = ColumnWithName(prefix + "description_string")
  val bsart_string: ColumnWithName = ColumnWithName(prefix + "bsart_string")
  val inco3_l_string: ColumnWithName = ColumnWithName(prefix + "inco3_l_string")
  val fsh_os_stg_change_string: ColumnWithName = ColumnWithName(prefix + "fsh_os_stg_change_string")
  val bnddt_string: ColumnWithName = ColumnWithName(prefix + "bnddt_string")
  val ernam_string: ColumnWithName = ColumnWithName(prefix + "ernam_string")
  val msr_id_string: ColumnWithName = ColumnWithName(prefix + "msr_id_string")
  val verkf_string: ColumnWithName = ColumnWithName(prefix + "verkf_string")
  val statu_string: ColumnWithName = ColumnWithName(prefix + "statu_string")
  val spras_string: ColumnWithName = ColumnWithName(prefix + "spras_string")
  val eq_eindt_string: ColumnWithName = ColumnWithName(prefix + "eq_eindt_string")
  val lphis_string: ColumnWithName = ColumnWithName(prefix + "lphis_string")
  val ltsnr_allow_string: ColumnWithName = ColumnWithName(prefix + "ltsnr_allow_string")
  val kdatb_string: ColumnWithName = ColumnWithName(prefix + "kdatb_string")
  val tru_string: ColumnWithName = ColumnWithName(prefix + "tru_string")
  val zbd2t_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "zbd2t_decimal_3_0")
  val kornr_string: ColumnWithName = ColumnWithName(prefix + "kornr_string")
  val fsh_vas_last_item_string: ColumnWithName = ColumnWithName(prefix + "fsh_vas_last_item_string")
  val key_id_string: ColumnWithName = ColumnWithName(prefix + "key_id_string")
  val bwbdt_string: ColumnWithName = ColumnWithName(prefix + "bwbdt_string")
  val ebeln_string: ColumnWithName = ColumnWithName(prefix + "ebeln_string")
  val threshold_exists_string: ColumnWithName = ColumnWithName(prefix + "threshold_exists_string")
  val reswk_string: ColumnWithName = ColumnWithName(prefix + "reswk_string")
  val contract_allow_string: ColumnWithName = ColumnWithName(prefix + "contract_allow_string")
  val otb_spec_value_decimal_17_2: ColumnWithName = ColumnWithName(prefix + "otb_spec_value_decimal_17_2")
  val frgzu_string: ColumnWithName = ColumnWithName(prefix + "frgzu_string")
  val gwldt_string: ColumnWithName = ColumnWithName(prefix + "gwldt_string")
  val frgsx_string: ColumnWithName = ColumnWithName(prefix + "frgsx_string")
  val dpamt_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "dpamt_decimal_11_2")
  val addnr_string: ColumnWithName = ColumnWithName(prefix + "addnr_string")
  val zzalkunnr_string: ColumnWithName = ColumnWithName(prefix + "zzalkunnr_string")
  val pincr_string: ColumnWithName = ColumnWithName(prefix + "pincr_string")
  val llief_string: ColumnWithName = ColumnWithName(prefix + "llief_string")
  val pco_string: ColumnWithName = ColumnWithName(prefix + "pco_string")
  val angdt_string: ColumnWithName = ColumnWithName(prefix + "angdt_string")
  val con_distr_lev_string: ColumnWithName = ColumnWithName(prefix + "con_distr_lev_string")
  val exnum_string: ColumnWithName = ColumnWithName(prefix + "exnum_string")
  val eindt_allow_string: ColumnWithName = ColumnWithName(prefix + "eindt_allow_string")
  val con_otb_req_string: ColumnWithName = ColumnWithName(prefix + "con_otb_req_string")
  val source_logsys_string: ColumnWithName = ColumnWithName(prefix + "source_logsys_string")
  val fsh_transaction_string: ColumnWithName = ColumnWithName(prefix + "fsh_transaction_string")
  val dno_string: ColumnWithName = ColumnWithName(prefix + "dno_string")
  val plc_string: ColumnWithName = ColumnWithName(prefix + "plc_string")
  val pcy_string: ColumnWithName = ColumnWithName(prefix + "pcy_string")
  val check_type_string: ColumnWithName = ColumnWithName(prefix + "check_type_string")
  val unsez_string: ColumnWithName = ColumnWithName(prefix + "unsez_string")
  val zbd1p_decimal_5_3: ColumnWithName = ColumnWithName(prefix + "zbd1p_decimal_5_3")
  val postat_string: ColumnWithName = ColumnWithName(prefix + "postat_string")
  val stceg_string: ColumnWithName = ColumnWithName(prefix + "stceg_string")
  val zzdummy_string: ColumnWithName = ColumnWithName(prefix + "zzdummy_string")
  val stceg_l_string: ColumnWithName = ColumnWithName(prefix + "stceg_l_string")
  val reason_code_string: ColumnWithName = ColumnWithName(prefix + "reason_code_string")
  val otb_curr_string: ColumnWithName = ColumnWithName(prefix + "otb_curr_string")
  val bstyp_string: ColumnWithName = ColumnWithName(prefix + "bstyp_string")
  val weakt_string: ColumnWithName = ColumnWithName(prefix + "weakt_string")
  val zzwekunnr_string: ColumnWithName = ColumnWithName(prefix + "zzwekunnr_string")
  val otb_level_string: ColumnWithName = ColumnWithName(prefix + "otb_level_string")
  val ecc_string: ColumnWithName = ColumnWithName(prefix + "ecc_string")
  val pohf_type_string: ColumnWithName = ColumnWithName(prefix + "pohf_type_string")
  val pcd_string: ColumnWithName = ColumnWithName(prefix + "pcd_string")
  val kalsm_string: ColumnWithName = ColumnWithName(prefix + "kalsm_string")
  val autlf_string: ColumnWithName = ColumnWithName(prefix + "autlf_string")
  val force_id_string: ColumnWithName = ColumnWithName(prefix + "force_id_string")
  val bsakz_string: ColumnWithName = ColumnWithName(prefix + "bsakz_string")
  val rettp_string: ColumnWithName = ColumnWithName(prefix + "rettp_string")
  val externalsystem_string: ColumnWithName = ColumnWithName(prefix + "externalsystem_string")
  val cur_string: ColumnWithName = ColumnWithName(prefix + "cur_string")
  val fixpo_allow_string: ColumnWithName = ColumnWithName(prefix + "fixpo_allow_string")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val eq_werks_string: ColumnWithName = ColumnWithName(prefix + "eq_werks_string")
  val konnr_string: ColumnWithName = ColumnWithName(prefix + "konnr_string")
  val pcn_string: ColumnWithName = ColumnWithName(prefix + "pcn_string")
  val memorytype_string: ColumnWithName = ColumnWithName(prefix + "memorytype_string")
  val stafo_string: ColumnWithName = ColumnWithName(prefix + "stafo_string")
  val ipp_string: ColumnWithName = ColumnWithName(prefix + "ipp_string")
  val fsh_snst_status_string: ColumnWithName = ColumnWithName(prefix + "fsh_snst_status_string")
  val wkurs_decimal_9_5: ColumnWithName = ColumnWithName(prefix + "wkurs_decimal_9_5")
  val ekgrp_string: ColumnWithName = ColumnWithName(prefix + "ekgrp_string")
  val vzskz_string: ColumnWithName = ColumnWithName(prefix + "vzskz_string")
  val inco1_string: ColumnWithName = ColumnWithName(prefix + "inco1_string")
  val bedat_string: ColumnWithName = ColumnWithName(prefix + "bedat_string")
  val telf1_string: ColumnWithName = ColumnWithName(prefix + "telf1_string")
  val zbd3t_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "zbd3t_decimal_3_0")
  val handoverloc_string: ColumnWithName = ColumnWithName(prefix + "handoverloc_string")
  val hierarchy_exists_string: ColumnWithName = ColumnWithName(prefix + "hierarchy_exists_string")
  val dpdat_string: ColumnWithName = ColumnWithName(prefix + "dpdat_string")
  val adrnr_string: ColumnWithName = ColumnWithName(prefix + "adrnr_string")
  val ext_rev_tmstmp_decimal_21_7: ColumnWithName = ColumnWithName(prefix + "ext_rev_tmstmp_decimal_21_7")
  val submi_string: ColumnWithName = ColumnWithName(prefix + "submi_string")
  val inco2_l_string: ColumnWithName = ColumnWithName(prefix + "inco2_l_string")
  val ihran_string: ColumnWithName = ColumnWithName(prefix + "ihran_string")
  val angnr_string: ColumnWithName = ColumnWithName(prefix + "angnr_string")
  val vsart_string: ColumnWithName = ColumnWithName(prefix + "vsart_string")
  val ekorg_string: ColumnWithName = ColumnWithName(prefix + "ekorg_string")
  val frgke_string: ColumnWithName = ColumnWithName(prefix + "frgke_string")
  val lblif_string: ColumnWithName = ColumnWithName(prefix + "lblif_string")
  val aurel_allow_string: ColumnWithName = ColumnWithName(prefix + "aurel_allow_string")
  val upinc_string: ColumnWithName = ColumnWithName(prefix + "upinc_string")
  val spr_rsn_profile_string: ColumnWithName = ColumnWithName(prefix + "spr_rsn_profile_string")
  val ausnr_string: ColumnWithName = ColumnWithName(prefix + "ausnr_string")
  val lifre_string: ColumnWithName = ColumnWithName(prefix + "lifre_string")
  val scmproc_string: ColumnWithName = ColumnWithName(prefix + "scmproc_string")
  val ekgrp_allow_string: ColumnWithName = ColumnWithName(prefix + "ekgrp_allow_string")
  val dptyp_string: ColumnWithName = ColumnWithName(prefix + "dptyp_string")
  val zbd2p_decimal_5_3: ColumnWithName = ColumnWithName(prefix + "zbd2p_decimal_5_3")
  val top_string: ColumnWithName = ColumnWithName(prefix + "top_string")
  val pstyp_allow_string: ColumnWithName = ColumnWithName(prefix + "pstyp_allow_string")
  val amn_string: ColumnWithName = ColumnWithName(prefix + "amn_string")
  val frgrl_string: ColumnWithName = ColumnWithName(prefix + "frgrl_string")
  val con_prebook_lev_string: ColumnWithName = ColumnWithName(prefix + "con_prebook_lev_string")
  val ktwrt_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "ktwrt_decimal_15_2")
  val delper_allow_string: ColumnWithName = ColumnWithName(prefix + "delper_allow_string")
  val pbn_string: ColumnWithName = ColumnWithName(prefix + "pbn_string")
  val fsh_item_group_string: ColumnWithName = ColumnWithName(prefix + "fsh_item_group_string")
  val inco2_string: ColumnWithName = ColumnWithName(prefix + "inco2_string")
  val loekz_string: ColumnWithName = ColumnWithName(prefix + "loekz_string")
  val lponr_string: ColumnWithName = ColumnWithName(prefix + "lponr_string")
  val stako_string: ColumnWithName = ColumnWithName(prefix + "stako_string")
  val otb_status_string: ColumnWithName = ColumnWithName(prefix + "otb_status_string")
  val absgr_string: ColumnWithName = ColumnWithName(prefix + "absgr_string")
  val aedat_string: ColumnWithName = ColumnWithName(prefix + "aedat_string")
  val externalreferenceid_string: ColumnWithName = ColumnWithName(prefix + "externalreferenceid_string")
  val otb_value_decimal_17_2: ColumnWithName = ColumnWithName(prefix + "otb_value_decimal_17_2")
  val memory_string: ColumnWithName = ColumnWithName(prefix + "memory_string")
  val legal_contract_string: ColumnWithName = ColumnWithName(prefix + "legal_contract_string")
  val otb_cond_type_string: ColumnWithName = ColumnWithName(prefix + "otb_cond_type_string")
  val retpc_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "retpc_decimal_5_2")
  val frggr_string: ColumnWithName = ColumnWithName(prefix + "frggr_string")
  val lifnr_string: ColumnWithName = ColumnWithName(prefix + "lifnr_string")
  val dpt_string: ColumnWithName = ColumnWithName(prefix + "dpt_string")
  val revno_string: ColumnWithName = ColumnWithName(prefix + "revno_string")
  val otb_reason_string: ColumnWithName = ColumnWithName(prefix + "otb_reason_string")
  val knumv_string: ColumnWithName = ColumnWithName(prefix + "knumv_string")
  val force_cnt_string: ColumnWithName = ColumnWithName(prefix + "force_cnt_string")
  val reloc_id_string: ColumnWithName = ColumnWithName(prefix + "reloc_id_string")
  val release_date_string: ColumnWithName = ColumnWithName(prefix + "release_date_string")
  val lands_string: ColumnWithName = ColumnWithName(prefix + "lands_string")
  val zzunloadpt_string: ColumnWithName = ColumnWithName(prefix + "zzunloadpt_string")
  val kunnr_string: ColumnWithName = ColumnWithName(prefix + "kunnr_string")
  val ihrez_string: ColumnWithName = ColumnWithName(prefix + "ihrez_string")
  val kufix_string: ColumnWithName = ColumnWithName(prefix + "kufix_string")
  val eco_string: ColumnWithName = ColumnWithName(prefix + "eco_string")
  val key_id_allow_string: ColumnWithName = ColumnWithName(prefix + "key_id_allow_string")
  val dppct_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "dppct_decimal_5_2")
  val kdate_string: ColumnWithName = ColumnWithName(prefix + "kdate_string")
  val otb_res_value_decimal_17_2: ColumnWithName = ColumnWithName(prefix + "otb_res_value_decimal_17_2")
  val budg_type_string: ColumnWithName = ColumnWithName(prefix + "budg_type_string")
  val werks_allow_string: ColumnWithName = ColumnWithName(prefix + "werks_allow_string")
  val reloc_seq_id_string: ColumnWithName = ColumnWithName(prefix + "reloc_seq_id_string")
  val rlwrt_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "rlwrt_decimal_15_2")
  val sus_string: ColumnWithName = ColumnWithName(prefix + "sus_string")
  val incov_string: ColumnWithName = ColumnWithName(prefix + "incov_string")
  val waers_string: ColumnWithName = ColumnWithName(prefix + "waers_string")
  val smb_string: ColumnWithName = ColumnWithName(prefix + "smb_string")
}

object C_ekko extends C_ekko("") {
  def as(alias: String): C_ekko = new C_ekko(alias + ".")
}

// AUTO GENERATED:END
