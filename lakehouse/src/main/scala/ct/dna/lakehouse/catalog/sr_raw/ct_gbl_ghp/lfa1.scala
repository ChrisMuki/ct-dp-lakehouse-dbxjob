// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_ghp

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_lfa1(
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
    ort02_string: String,
    dlgrp_string: String,
    j_1iexcive_string: String,
    au_payment_exempt_string: String,
    dtams_string: String,
    gbdat_string: String,
    telfx_string: String,
    kraus_string: String,
    j_1iexco_string: String,
    begru_string: String,
    brsch_string: String,
    sc_currency_string: String,
    exp_string: String,
    data_ctrlr9_string: String,
    xcpdk_string: String,
    dvalss_string: String,
    regss_string: String,
    ernam_string: String,
    pfach_string: String,
    data_ctrlr8_string: String,
    ven_class_string: String,
    icmstaxpay_string: String,
    sfrgr_string: String,
    term_li_string: String,
    name3_string: String,
    spras_string: String,
    fityp_string: String,
    sortl_string: String,
    data_ctrlr3_string: String,
    j_1iexrg_string: String,
    stgdl_string: String,
    j_1issist_string: String,
    borgr_datun_string: String,
    bahns_string: String,
    cvp_xblck_string: String,
    name1_string: String,
    stcd5_string: String,
    confs_string: String,
    telx1_string: String,
    addr2_country_string: String,
    stkzn_string: String,
    legalnat_string: String,
    qssysdat_string: String,
    stkza_string: String,
    duefl_string: String,
    entpub_string: String,
    telbx_string: String,
    stcd1_string: String,
    regio_string: String,
    crtn_string: String,
    alc_string: String,
    stkzu_string: String,
    stcd4_string: String,
    datlt_string: String,
    carrier_conf_string: String,
    gbort_string: String,
    j_1ilstno_string: String,
    partner_name_string: String,
    ppa_relevant_string: String,
    scheduling_type_string: String,
    transport_chain_string: String,
    stcd6_string: String,
    j_1ivencre_string: String,
    data_ctrlr6_string: String,
    psost_string: String,
    data_ctrlr4_string: String,
    emnfr_string: String,
    lnrza_string: String,
    land1_string: String,
    au_wholly_inp_taxed_string: String,
    stceg_string: String,
    vfnid_string: String,
    lfurl_string: String,
    ric_string: String,
    scacd_string: String,
    bbsnr_string: String,
    stcd2_string: String,
    crc_num_string: String,
    stenr_string: String,
    anred_string: String,
    addr2_house_num_string: String,
    fisku_string: String,
    updat_string: String,
    mcod2_string: String,
    nodel_string: String,
    au_payment_not_exceed_75_string: String,
    au_carrying_ent_string: String,
    escrit_string: String,
    werks_string: String,
    pson1_string: String,
    mcod3_string: String,
    j_sc_currency_string: String,
    j_1kftbus_string: String,
    decregpc_string: String,
    mandt_string: String,
    ktokk_string: String,
    addr2_city_string: String,
    j_1idedref_string: String,
    sperz_string: String,
    j_1kfrepre_string: String,
    frmcss_string: String,
    j_1i_customs_string: String,
    vbund_string: String,
    psohs_string: String,
    data_ctrlr7_string: String,
    crn_string: String,
    codcae_string: String,
    pson3_string: String,
    txjcd_string: String,
    plkal_string: String,
    rne_string: String,
    ipisp_string: String,
    telf1_string: String,
    name4_string: String,
    psofg_string: String,
    fiskn_string: String,
    xdcset_string: String,
    mcod1_string: String,
    categ_string: String,
    psotl_string: String,
    profs_string: String,
    rg_string: String,
    vfnum_string: String,
    konzs_string: String,
    adrnr_string: String,
    dummy_lfa1_addr_incl_eew_ps_string: String,
    partner_utr_string: String,
    comsize_string: String,
    psois_string: String,
    j_1ipanref_string: String,
    telf2_string: String,
    paytrsn_string: String,
    j_1kftind_string: String,
    j_1ipanvaldt_string: String,
    podkzb_string: String,
    esrnr_string: String,
    au_not_entitled_abn_string: String,
    addr2_post_string: String,
    au_domestic_nature_string: String,
    actss_string: String,
    data_ctrlr1_string: String,
    xzemp_string: String,
    j_1iexcd_string: String,
    lfa1_eew_supp_string: String,
    psovn_string: String,
    teltx_string: String,
    borgr_yeaun_string: String,
    j_1ivtyp_string: String,
    revdb_string: String,
    sperm_string: String,
    indtyp_string: String,
    submi_relevant_string: String,
    pstlz_string: String,
    rnedate_string: String,
    pmt_office_string: String,
    taxbs_string: String,
    pson2_string: String,
    au_partner_without_gain_string: String,
    allowance_type_string: String,
    name2_string: String,
    stcdt_string: String,
    cnae_string: String,
    sam_ue_id_string: String,
    data_ctrlr5_string: String,
    aedat_string: String,
    j_1icstno_string: String,
    ltsna_string: String,
    ausdiv_string: String,
    pstl2_string: String,
    sperq_string: String,
    @Decimal(3, 0) staging_time_decimal_3_0: BigDecimal,
    uf_string: String,
    min_comp_string: String,
    au_private_hobby_string: String,
    stcd3_string: String,
    dtaws_string: String,
    ktock_string: String,
    j_1isern_string: String,
    addr2_street_string: String,
    werkr_string: String,
    fr_occupation_string: String,
    ort01_string: String,
    lifnr_string: String,
    j_1iexdi_string: String,
    bubkz_string: String,
    weora_string: String,
    lzone_string: String,
    @Decimal(15, 2) j_sc_capital_decimal_15_2: BigDecimal,
    au_ind_under_18_string: String,
    kunnr_string: String,
    sperr_string: String,
    loevm_string: String,
    @Decimal(15, 2) sc_capital_decimal_15_2: BigDecimal,
    stras_string: String,
    pfort_string: String,
    bbbnr_string: String,
    status_string: String,
    usnam_string: String,
    j_1ipanno_string: String,
    uptim_string: String,
    data_ctrlr10_string: String,
    sam_eft_ind_string: String,
    j_1iexrn_string: String,
    tdt_string: String,
    xlfza_string: String,
    qssys_string: String,
    rgdate_string: String,
    sexkz_string: String,
    data_ctrlr2_string: String,
    erdat_string: String
) extends Entity

object lfa1 extends TableSpec[E_lfa1](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_lfa1(prefix: String) extends ColumnWithNameAccessor {
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
  val ort02_string: ColumnWithName = ColumnWithName(prefix + "ort02_string")
  val dlgrp_string: ColumnWithName = ColumnWithName(prefix + "dlgrp_string")
  val j_1iexcive_string: ColumnWithName = ColumnWithName(prefix + "j_1iexcive_string")
  val au_payment_exempt_string: ColumnWithName = ColumnWithName(prefix + "au_payment_exempt_string")
  val dtams_string: ColumnWithName = ColumnWithName(prefix + "dtams_string")
  val gbdat_string: ColumnWithName = ColumnWithName(prefix + "gbdat_string")
  val telfx_string: ColumnWithName = ColumnWithName(prefix + "telfx_string")
  val kraus_string: ColumnWithName = ColumnWithName(prefix + "kraus_string")
  val j_1iexco_string: ColumnWithName = ColumnWithName(prefix + "j_1iexco_string")
  val begru_string: ColumnWithName = ColumnWithName(prefix + "begru_string")
  val brsch_string: ColumnWithName = ColumnWithName(prefix + "brsch_string")
  val sc_currency_string: ColumnWithName = ColumnWithName(prefix + "sc_currency_string")
  val exp_string: ColumnWithName = ColumnWithName(prefix + "exp_string")
  val data_ctrlr9_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr9_string")
  val xcpdk_string: ColumnWithName = ColumnWithName(prefix + "xcpdk_string")
  val dvalss_string: ColumnWithName = ColumnWithName(prefix + "dvalss_string")
  val regss_string: ColumnWithName = ColumnWithName(prefix + "regss_string")
  val ernam_string: ColumnWithName = ColumnWithName(prefix + "ernam_string")
  val pfach_string: ColumnWithName = ColumnWithName(prefix + "pfach_string")
  val data_ctrlr8_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr8_string")
  val ven_class_string: ColumnWithName = ColumnWithName(prefix + "ven_class_string")
  val icmstaxpay_string: ColumnWithName = ColumnWithName(prefix + "icmstaxpay_string")
  val sfrgr_string: ColumnWithName = ColumnWithName(prefix + "sfrgr_string")
  val term_li_string: ColumnWithName = ColumnWithName(prefix + "term_li_string")
  val name3_string: ColumnWithName = ColumnWithName(prefix + "name3_string")
  val spras_string: ColumnWithName = ColumnWithName(prefix + "spras_string")
  val fityp_string: ColumnWithName = ColumnWithName(prefix + "fityp_string")
  val sortl_string: ColumnWithName = ColumnWithName(prefix + "sortl_string")
  val data_ctrlr3_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr3_string")
  val j_1iexrg_string: ColumnWithName = ColumnWithName(prefix + "j_1iexrg_string")
  val stgdl_string: ColumnWithName = ColumnWithName(prefix + "stgdl_string")
  val j_1issist_string: ColumnWithName = ColumnWithName(prefix + "j_1issist_string")
  val borgr_datun_string: ColumnWithName = ColumnWithName(prefix + "borgr_datun_string")
  val bahns_string: ColumnWithName = ColumnWithName(prefix + "bahns_string")
  val cvp_xblck_string: ColumnWithName = ColumnWithName(prefix + "cvp_xblck_string")
  val name1_string: ColumnWithName = ColumnWithName(prefix + "name1_string")
  val stcd5_string: ColumnWithName = ColumnWithName(prefix + "stcd5_string")
  val confs_string: ColumnWithName = ColumnWithName(prefix + "confs_string")
  val telx1_string: ColumnWithName = ColumnWithName(prefix + "telx1_string")
  val addr2_country_string: ColumnWithName = ColumnWithName(prefix + "addr2_country_string")
  val stkzn_string: ColumnWithName = ColumnWithName(prefix + "stkzn_string")
  val legalnat_string: ColumnWithName = ColumnWithName(prefix + "legalnat_string")
  val qssysdat_string: ColumnWithName = ColumnWithName(prefix + "qssysdat_string")
  val stkza_string: ColumnWithName = ColumnWithName(prefix + "stkza_string")
  val duefl_string: ColumnWithName = ColumnWithName(prefix + "duefl_string")
  val entpub_string: ColumnWithName = ColumnWithName(prefix + "entpub_string")
  val telbx_string: ColumnWithName = ColumnWithName(prefix + "telbx_string")
  val stcd1_string: ColumnWithName = ColumnWithName(prefix + "stcd1_string")
  val regio_string: ColumnWithName = ColumnWithName(prefix + "regio_string")
  val crtn_string: ColumnWithName = ColumnWithName(prefix + "crtn_string")
  val alc_string: ColumnWithName = ColumnWithName(prefix + "alc_string")
  val stkzu_string: ColumnWithName = ColumnWithName(prefix + "stkzu_string")
  val stcd4_string: ColumnWithName = ColumnWithName(prefix + "stcd4_string")
  val datlt_string: ColumnWithName = ColumnWithName(prefix + "datlt_string")
  val carrier_conf_string: ColumnWithName = ColumnWithName(prefix + "carrier_conf_string")
  val gbort_string: ColumnWithName = ColumnWithName(prefix + "gbort_string")
  val j_1ilstno_string: ColumnWithName = ColumnWithName(prefix + "j_1ilstno_string")
  val partner_name_string: ColumnWithName = ColumnWithName(prefix + "partner_name_string")
  val ppa_relevant_string: ColumnWithName = ColumnWithName(prefix + "ppa_relevant_string")
  val scheduling_type_string: ColumnWithName = ColumnWithName(prefix + "scheduling_type_string")
  val transport_chain_string: ColumnWithName = ColumnWithName(prefix + "transport_chain_string")
  val stcd6_string: ColumnWithName = ColumnWithName(prefix + "stcd6_string")
  val j_1ivencre_string: ColumnWithName = ColumnWithName(prefix + "j_1ivencre_string")
  val data_ctrlr6_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr6_string")
  val psost_string: ColumnWithName = ColumnWithName(prefix + "psost_string")
  val data_ctrlr4_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr4_string")
  val emnfr_string: ColumnWithName = ColumnWithName(prefix + "emnfr_string")
  val lnrza_string: ColumnWithName = ColumnWithName(prefix + "lnrza_string")
  val land1_string: ColumnWithName = ColumnWithName(prefix + "land1_string")
  val au_wholly_inp_taxed_string: ColumnWithName = ColumnWithName(prefix + "au_wholly_inp_taxed_string")
  val stceg_string: ColumnWithName = ColumnWithName(prefix + "stceg_string")
  val vfnid_string: ColumnWithName = ColumnWithName(prefix + "vfnid_string")
  val lfurl_string: ColumnWithName = ColumnWithName(prefix + "lfurl_string")
  val ric_string: ColumnWithName = ColumnWithName(prefix + "ric_string")
  val scacd_string: ColumnWithName = ColumnWithName(prefix + "scacd_string")
  val bbsnr_string: ColumnWithName = ColumnWithName(prefix + "bbsnr_string")
  val stcd2_string: ColumnWithName = ColumnWithName(prefix + "stcd2_string")
  val crc_num_string: ColumnWithName = ColumnWithName(prefix + "crc_num_string")
  val stenr_string: ColumnWithName = ColumnWithName(prefix + "stenr_string")
  val anred_string: ColumnWithName = ColumnWithName(prefix + "anred_string")
  val addr2_house_num_string: ColumnWithName = ColumnWithName(prefix + "addr2_house_num_string")
  val fisku_string: ColumnWithName = ColumnWithName(prefix + "fisku_string")
  val updat_string: ColumnWithName = ColumnWithName(prefix + "updat_string")
  val mcod2_string: ColumnWithName = ColumnWithName(prefix + "mcod2_string")
  val nodel_string: ColumnWithName = ColumnWithName(prefix + "nodel_string")
  val au_payment_not_exceed_75_string: ColumnWithName = ColumnWithName(prefix + "au_payment_not_exceed_75_string")
  val au_carrying_ent_string: ColumnWithName = ColumnWithName(prefix + "au_carrying_ent_string")
  val escrit_string: ColumnWithName = ColumnWithName(prefix + "escrit_string")
  val werks_string: ColumnWithName = ColumnWithName(prefix + "werks_string")
  val pson1_string: ColumnWithName = ColumnWithName(prefix + "pson1_string")
  val mcod3_string: ColumnWithName = ColumnWithName(prefix + "mcod3_string")
  val j_sc_currency_string: ColumnWithName = ColumnWithName(prefix + "j_sc_currency_string")
  val j_1kftbus_string: ColumnWithName = ColumnWithName(prefix + "j_1kftbus_string")
  val decregpc_string: ColumnWithName = ColumnWithName(prefix + "decregpc_string")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val ktokk_string: ColumnWithName = ColumnWithName(prefix + "ktokk_string")
  val addr2_city_string: ColumnWithName = ColumnWithName(prefix + "addr2_city_string")
  val j_1idedref_string: ColumnWithName = ColumnWithName(prefix + "j_1idedref_string")
  val sperz_string: ColumnWithName = ColumnWithName(prefix + "sperz_string")
  val j_1kfrepre_string: ColumnWithName = ColumnWithName(prefix + "j_1kfrepre_string")
  val frmcss_string: ColumnWithName = ColumnWithName(prefix + "frmcss_string")
  val j_1i_customs_string: ColumnWithName = ColumnWithName(prefix + "j_1i_customs_string")
  val vbund_string: ColumnWithName = ColumnWithName(prefix + "vbund_string")
  val psohs_string: ColumnWithName = ColumnWithName(prefix + "psohs_string")
  val data_ctrlr7_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr7_string")
  val crn_string: ColumnWithName = ColumnWithName(prefix + "crn_string")
  val codcae_string: ColumnWithName = ColumnWithName(prefix + "codcae_string")
  val pson3_string: ColumnWithName = ColumnWithName(prefix + "pson3_string")
  val txjcd_string: ColumnWithName = ColumnWithName(prefix + "txjcd_string")
  val plkal_string: ColumnWithName = ColumnWithName(prefix + "plkal_string")
  val rne_string: ColumnWithName = ColumnWithName(prefix + "rne_string")
  val ipisp_string: ColumnWithName = ColumnWithName(prefix + "ipisp_string")
  val telf1_string: ColumnWithName = ColumnWithName(prefix + "telf1_string")
  val name4_string: ColumnWithName = ColumnWithName(prefix + "name4_string")
  val psofg_string: ColumnWithName = ColumnWithName(prefix + "psofg_string")
  val fiskn_string: ColumnWithName = ColumnWithName(prefix + "fiskn_string")
  val xdcset_string: ColumnWithName = ColumnWithName(prefix + "xdcset_string")
  val mcod1_string: ColumnWithName = ColumnWithName(prefix + "mcod1_string")
  val categ_string: ColumnWithName = ColumnWithName(prefix + "categ_string")
  val psotl_string: ColumnWithName = ColumnWithName(prefix + "psotl_string")
  val profs_string: ColumnWithName = ColumnWithName(prefix + "profs_string")
  val rg_string: ColumnWithName = ColumnWithName(prefix + "rg_string")
  val vfnum_string: ColumnWithName = ColumnWithName(prefix + "vfnum_string")
  val konzs_string: ColumnWithName = ColumnWithName(prefix + "konzs_string")
  val adrnr_string: ColumnWithName = ColumnWithName(prefix + "adrnr_string")
  val dummy_lfa1_addr_incl_eew_ps_string: ColumnWithName = ColumnWithName(prefix + "dummy_lfa1_addr_incl_eew_ps_string")
  val partner_utr_string: ColumnWithName = ColumnWithName(prefix + "partner_utr_string")
  val comsize_string: ColumnWithName = ColumnWithName(prefix + "comsize_string")
  val psois_string: ColumnWithName = ColumnWithName(prefix + "psois_string")
  val j_1ipanref_string: ColumnWithName = ColumnWithName(prefix + "j_1ipanref_string")
  val telf2_string: ColumnWithName = ColumnWithName(prefix + "telf2_string")
  val paytrsn_string: ColumnWithName = ColumnWithName(prefix + "paytrsn_string")
  val j_1kftind_string: ColumnWithName = ColumnWithName(prefix + "j_1kftind_string")
  val j_1ipanvaldt_string: ColumnWithName = ColumnWithName(prefix + "j_1ipanvaldt_string")
  val podkzb_string: ColumnWithName = ColumnWithName(prefix + "podkzb_string")
  val esrnr_string: ColumnWithName = ColumnWithName(prefix + "esrnr_string")
  val au_not_entitled_abn_string: ColumnWithName = ColumnWithName(prefix + "au_not_entitled_abn_string")
  val addr2_post_string: ColumnWithName = ColumnWithName(prefix + "addr2_post_string")
  val au_domestic_nature_string: ColumnWithName = ColumnWithName(prefix + "au_domestic_nature_string")
  val actss_string: ColumnWithName = ColumnWithName(prefix + "actss_string")
  val data_ctrlr1_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr1_string")
  val xzemp_string: ColumnWithName = ColumnWithName(prefix + "xzemp_string")
  val j_1iexcd_string: ColumnWithName = ColumnWithName(prefix + "j_1iexcd_string")
  val lfa1_eew_supp_string: ColumnWithName = ColumnWithName(prefix + "lfa1_eew_supp_string")
  val psovn_string: ColumnWithName = ColumnWithName(prefix + "psovn_string")
  val teltx_string: ColumnWithName = ColumnWithName(prefix + "teltx_string")
  val borgr_yeaun_string: ColumnWithName = ColumnWithName(prefix + "borgr_yeaun_string")
  val j_1ivtyp_string: ColumnWithName = ColumnWithName(prefix + "j_1ivtyp_string")
  val revdb_string: ColumnWithName = ColumnWithName(prefix + "revdb_string")
  val sperm_string: ColumnWithName = ColumnWithName(prefix + "sperm_string")
  val indtyp_string: ColumnWithName = ColumnWithName(prefix + "indtyp_string")
  val submi_relevant_string: ColumnWithName = ColumnWithName(prefix + "submi_relevant_string")
  val pstlz_string: ColumnWithName = ColumnWithName(prefix + "pstlz_string")
  val rnedate_string: ColumnWithName = ColumnWithName(prefix + "rnedate_string")
  val pmt_office_string: ColumnWithName = ColumnWithName(prefix + "pmt_office_string")
  val taxbs_string: ColumnWithName = ColumnWithName(prefix + "taxbs_string")
  val pson2_string: ColumnWithName = ColumnWithName(prefix + "pson2_string")
  val au_partner_without_gain_string: ColumnWithName = ColumnWithName(prefix + "au_partner_without_gain_string")
  val allowance_type_string: ColumnWithName = ColumnWithName(prefix + "allowance_type_string")
  val name2_string: ColumnWithName = ColumnWithName(prefix + "name2_string")
  val stcdt_string: ColumnWithName = ColumnWithName(prefix + "stcdt_string")
  val cnae_string: ColumnWithName = ColumnWithName(prefix + "cnae_string")
  val sam_ue_id_string: ColumnWithName = ColumnWithName(prefix + "sam_ue_id_string")
  val data_ctrlr5_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr5_string")
  val aedat_string: ColumnWithName = ColumnWithName(prefix + "aedat_string")
  val j_1icstno_string: ColumnWithName = ColumnWithName(prefix + "j_1icstno_string")
  val ltsna_string: ColumnWithName = ColumnWithName(prefix + "ltsna_string")
  val ausdiv_string: ColumnWithName = ColumnWithName(prefix + "ausdiv_string")
  val pstl2_string: ColumnWithName = ColumnWithName(prefix + "pstl2_string")
  val sperq_string: ColumnWithName = ColumnWithName(prefix + "sperq_string")
  val staging_time_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "staging_time_decimal_3_0")
  val uf_string: ColumnWithName = ColumnWithName(prefix + "uf_string")
  val min_comp_string: ColumnWithName = ColumnWithName(prefix + "min_comp_string")
  val au_private_hobby_string: ColumnWithName = ColumnWithName(prefix + "au_private_hobby_string")
  val stcd3_string: ColumnWithName = ColumnWithName(prefix + "stcd3_string")
  val dtaws_string: ColumnWithName = ColumnWithName(prefix + "dtaws_string")
  val ktock_string: ColumnWithName = ColumnWithName(prefix + "ktock_string")
  val j_1isern_string: ColumnWithName = ColumnWithName(prefix + "j_1isern_string")
  val addr2_street_string: ColumnWithName = ColumnWithName(prefix + "addr2_street_string")
  val werkr_string: ColumnWithName = ColumnWithName(prefix + "werkr_string")
  val fr_occupation_string: ColumnWithName = ColumnWithName(prefix + "fr_occupation_string")
  val ort01_string: ColumnWithName = ColumnWithName(prefix + "ort01_string")
  val lifnr_string: ColumnWithName = ColumnWithName(prefix + "lifnr_string")
  val j_1iexdi_string: ColumnWithName = ColumnWithName(prefix + "j_1iexdi_string")
  val bubkz_string: ColumnWithName = ColumnWithName(prefix + "bubkz_string")
  val weora_string: ColumnWithName = ColumnWithName(prefix + "weora_string")
  val lzone_string: ColumnWithName = ColumnWithName(prefix + "lzone_string")
  val j_sc_capital_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "j_sc_capital_decimal_15_2")
  val au_ind_under_18_string: ColumnWithName = ColumnWithName(prefix + "au_ind_under_18_string")
  val kunnr_string: ColumnWithName = ColumnWithName(prefix + "kunnr_string")
  val sperr_string: ColumnWithName = ColumnWithName(prefix + "sperr_string")
  val loevm_string: ColumnWithName = ColumnWithName(prefix + "loevm_string")
  val sc_capital_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "sc_capital_decimal_15_2")
  val stras_string: ColumnWithName = ColumnWithName(prefix + "stras_string")
  val pfort_string: ColumnWithName = ColumnWithName(prefix + "pfort_string")
  val bbbnr_string: ColumnWithName = ColumnWithName(prefix + "bbbnr_string")
  val status_string: ColumnWithName = ColumnWithName(prefix + "status_string")
  val usnam_string: ColumnWithName = ColumnWithName(prefix + "usnam_string")
  val j_1ipanno_string: ColumnWithName = ColumnWithName(prefix + "j_1ipanno_string")
  val uptim_string: ColumnWithName = ColumnWithName(prefix + "uptim_string")
  val data_ctrlr10_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr10_string")
  val sam_eft_ind_string: ColumnWithName = ColumnWithName(prefix + "sam_eft_ind_string")
  val j_1iexrn_string: ColumnWithName = ColumnWithName(prefix + "j_1iexrn_string")
  val tdt_string: ColumnWithName = ColumnWithName(prefix + "tdt_string")
  val xlfza_string: ColumnWithName = ColumnWithName(prefix + "xlfza_string")
  val qssys_string: ColumnWithName = ColumnWithName(prefix + "qssys_string")
  val rgdate_string: ColumnWithName = ColumnWithName(prefix + "rgdate_string")
  val sexkz_string: ColumnWithName = ColumnWithName(prefix + "sexkz_string")
  val data_ctrlr2_string: ColumnWithName = ColumnWithName(prefix + "data_ctrlr2_string")
  val erdat_string: ColumnWithName = ColumnWithName(prefix + "erdat_string")
}

object C_lfa1 extends C_lfa1("") {
  def as(alias: String): C_lfa1 = new C_lfa1(alias + ".")
}

// AUTO GENERATED:END
