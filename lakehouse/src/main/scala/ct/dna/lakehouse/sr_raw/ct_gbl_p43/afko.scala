// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.sr_raw.ct_gbl_p43

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_afko(
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
    mandt_string: String,
    aufnr_string: String,
    gltrp_string: String,
    gstrp_string: String,
    ftrms_string: String,
    gltrs_string: String,
    gstrs_string: String,
    gstri_string: String,
    getri_string: String,
    gltri_string: String,
    ftrmi_string: String,
    ftrmp_string: String,
    rsnum_string: String,
    @Decimal(13, 3) gasmg_decimal_13_3: BigDecimal,
    @Decimal(13, 3) gamng_decimal_13_3: BigDecimal,
    gmein_string: String,
    plnbez_string: String,
    plnty_string: String,
    plnnr_string: String,
    plnaw_string: String,
    plnal_string: String,
    pverw_string: String,
    plauf_string: String,
    @Decimal(13, 3) plsvb_decimal_13_3: BigDecimal,
    plnme_string: String,
    @Decimal(13, 3) plsvn_decimal_13_3: BigDecimal,
    pdatv_string: String,
    paenr_string: String,
    plgrp_string: String,
    @Decimal(13, 3) lodiv_decimal_13_3: BigDecimal,
    stlty_string: String,
    stlbez_string: String,
    stlst_string: String,
    stlnr_string: String,
    sdatv_string: String,
    @Decimal(13, 3) sbmng_decimal_13_3: BigDecimal,
    sbmeh_string: String,
    saenr_string: String,
    stlal_string: String,
    stlan_string: String,
    @Decimal(13, 3) slsvn_decimal_13_3: BigDecimal,
    @Decimal(13, 3) slsbs_decimal_13_3: BigDecimal,
    aufld_string: String,
    dispo_string: String,
    aufpl_string: String,
    fevor_string: String,
    fhori_string: String,
    terkz_string: String,
    redkz_string: String,
    aprio_string: String,
    ntzue_string: String,
    vorue_string: String,
    profid_string: String,
    vorgz_string: String,
    sichz_string: String,
    freiz_string: String,
    upter_string: String,
    bedid_string: String,
    pronr_string: String,
    zaehl_string: String,
    mzaehl_string: String,
    zkriz_string: String,
    prueflos_string: String,
    klvarp_string: String,
    klvari_string: String,
    rgekz_string: String,
    plart_string: String,
    flg_aob_string: String,
    flg_arbei_string: String,
    gltpp_string: String,
    gstpp_string: String,
    gltps_string: String,
    gstps_string: String,
    ftrps_string: String,
    rdkzp_string: String,
    trkzp_string: String,
    rueck_string: String,
    rmzhl_string: String,
    @Decimal(13, 3) igmng_decimal_13_3: BigDecimal,
    ratid_string: String,
    groid_string: String,
    cuobj_string: String,
    gluzs_string: String,
    gsuzs_string: String,
    revlv_string: String,
    rshty_string: String,
    rshid_string: String,
    rsnty_string: String,
    rsnid_string: String,
    nauterm_string: String,
    naucost_string: String,
    @Decimal(2, 0) stufe_decimal_2_0: BigDecimal,
    @Decimal(4, 0) wegxx_decimal_4_0: BigDecimal,
    @Decimal(4, 0) vwegx_decimal_4_0: BigDecimal,
    arsnr_string: String,
    arsps_string: String,
    maufnr_string: String,
    lknot_string: String,
    rknot_string: String,
    prodnet_string: String,
    @Decimal(13, 3) iasmg_decimal_13_3: BigDecimal,
    abarb_string: String,
    aufnt_string: String,
    aufpt_string: String,
    aplzt_string: String,
    no_disp_string: String,
    csplit_string: String,
    aennr_string: String,
    cy_seqnr_string: String,
    breaks_string: String,
    @Decimal(6, 3) vorgz_trm_decimal_6_3: BigDecimal,
    @Decimal(6, 3) sichz_trm_decimal_6_3: BigDecimal,
    trmdt_string: String,
    gluzp_string: String,
    gsuzp_string: String,
    gsuzi_string: String,
    geuzi_string: String,
    glupp_string: String,
    gsupp_string: String,
    glups_string: String,
    gsups_string: String,
    chsch_string: String,
    kapt_vorgz_string: String,
    kapt_sichz_string: String,
    lead_aufnr_string: String,
    pnetstartd_string: String,
    pnetstartt_string: String,
    pnetendd_string: String,
    pnetendt_string: String,
    kbed_string: String,
    kkalkr_string: String,
    sfcpf_string: String,
    @Decimal(13, 3) rmnga_decimal_13_3: BigDecimal,
    gsbtr_string: String,
    @Decimal(13, 3) vfmng_decimal_13_3: BigDecimal,
    nopcost_string: String,
    netzkont_string: String,
    atrkz_string: String,
    objtype_string: String,
    ch_proc_string: String,
    kapversa_string: String,
    colordproc_string: String,
    kzerb_string: String,
    conf_key_string: String,
    st_arbid_string: String,
    vsnmr_v_string: String,
    terhw_string: String,
    splstat_string: String,
    costupd_string: String,
    @Decimal(13, 3) max_gamng_decimal_13_3: BigDecimal,
    mes_routingid_string: String,
    adpsp_string: String,
    rmanr_string: String,
    posnr_rma_string: String,
    posnv_rma_string: String,
    @Decimal(5, 0) cfb_maxlz_decimal_5_0: BigDecimal,
    cfb_lzeih_string: String,
    @Decimal(4, 0) cfb_adtdays_decimal_4_0: BigDecimal,
    cfb_datofm_string: String,
    cfb_bbdpi_string: String,
    oihantyp_string: String,
    fsh_mprod_ord_string: String,
    flg_bundle_string: String,
    mill_ratio_int: BoxedInt,
    bmeins_string: String,
    @Decimal(13, 3) bmenge_decimal_13_3: BigDecimal,
    mill_oc_zuskz_string: String
) extends Entity

object afko extends TableSpec[E_afko](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_afko(prefix: String) extends ColumnWithNameAccessor {
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
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val aufnr_string: ColumnWithName = ColumnWithName(prefix + "aufnr_string")
  val gltrp_string: ColumnWithName = ColumnWithName(prefix + "gltrp_string")
  val gstrp_string: ColumnWithName = ColumnWithName(prefix + "gstrp_string")
  val ftrms_string: ColumnWithName = ColumnWithName(prefix + "ftrms_string")
  val gltrs_string: ColumnWithName = ColumnWithName(prefix + "gltrs_string")
  val gstrs_string: ColumnWithName = ColumnWithName(prefix + "gstrs_string")
  val gstri_string: ColumnWithName = ColumnWithName(prefix + "gstri_string")
  val getri_string: ColumnWithName = ColumnWithName(prefix + "getri_string")
  val gltri_string: ColumnWithName = ColumnWithName(prefix + "gltri_string")
  val ftrmi_string: ColumnWithName = ColumnWithName(prefix + "ftrmi_string")
  val ftrmp_string: ColumnWithName = ColumnWithName(prefix + "ftrmp_string")
  val rsnum_string: ColumnWithName = ColumnWithName(prefix + "rsnum_string")
  val gasmg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "gasmg_decimal_13_3")
  val gamng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "gamng_decimal_13_3")
  val gmein_string: ColumnWithName = ColumnWithName(prefix + "gmein_string")
  val plnbez_string: ColumnWithName = ColumnWithName(prefix + "plnbez_string")
  val plnty_string: ColumnWithName = ColumnWithName(prefix + "plnty_string")
  val plnnr_string: ColumnWithName = ColumnWithName(prefix + "plnnr_string")
  val plnaw_string: ColumnWithName = ColumnWithName(prefix + "plnaw_string")
  val plnal_string: ColumnWithName = ColumnWithName(prefix + "plnal_string")
  val pverw_string: ColumnWithName = ColumnWithName(prefix + "pverw_string")
  val plauf_string: ColumnWithName = ColumnWithName(prefix + "plauf_string")
  val plsvb_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "plsvb_decimal_13_3")
  val plnme_string: ColumnWithName = ColumnWithName(prefix + "plnme_string")
  val plsvn_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "plsvn_decimal_13_3")
  val pdatv_string: ColumnWithName = ColumnWithName(prefix + "pdatv_string")
  val paenr_string: ColumnWithName = ColumnWithName(prefix + "paenr_string")
  val plgrp_string: ColumnWithName = ColumnWithName(prefix + "plgrp_string")
  val lodiv_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "lodiv_decimal_13_3")
  val stlty_string: ColumnWithName = ColumnWithName(prefix + "stlty_string")
  val stlbez_string: ColumnWithName = ColumnWithName(prefix + "stlbez_string")
  val stlst_string: ColumnWithName = ColumnWithName(prefix + "stlst_string")
  val stlnr_string: ColumnWithName = ColumnWithName(prefix + "stlnr_string")
  val sdatv_string: ColumnWithName = ColumnWithName(prefix + "sdatv_string")
  val sbmng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "sbmng_decimal_13_3")
  val sbmeh_string: ColumnWithName = ColumnWithName(prefix + "sbmeh_string")
  val saenr_string: ColumnWithName = ColumnWithName(prefix + "saenr_string")
  val stlal_string: ColumnWithName = ColumnWithName(prefix + "stlal_string")
  val stlan_string: ColumnWithName = ColumnWithName(prefix + "stlan_string")
  val slsvn_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "slsvn_decimal_13_3")
  val slsbs_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "slsbs_decimal_13_3")
  val aufld_string: ColumnWithName = ColumnWithName(prefix + "aufld_string")
  val dispo_string: ColumnWithName = ColumnWithName(prefix + "dispo_string")
  val aufpl_string: ColumnWithName = ColumnWithName(prefix + "aufpl_string")
  val fevor_string: ColumnWithName = ColumnWithName(prefix + "fevor_string")
  val fhori_string: ColumnWithName = ColumnWithName(prefix + "fhori_string")
  val terkz_string: ColumnWithName = ColumnWithName(prefix + "terkz_string")
  val redkz_string: ColumnWithName = ColumnWithName(prefix + "redkz_string")
  val aprio_string: ColumnWithName = ColumnWithName(prefix + "aprio_string")
  val ntzue_string: ColumnWithName = ColumnWithName(prefix + "ntzue_string")
  val vorue_string: ColumnWithName = ColumnWithName(prefix + "vorue_string")
  val profid_string: ColumnWithName = ColumnWithName(prefix + "profid_string")
  val vorgz_string: ColumnWithName = ColumnWithName(prefix + "vorgz_string")
  val sichz_string: ColumnWithName = ColumnWithName(prefix + "sichz_string")
  val freiz_string: ColumnWithName = ColumnWithName(prefix + "freiz_string")
  val upter_string: ColumnWithName = ColumnWithName(prefix + "upter_string")
  val bedid_string: ColumnWithName = ColumnWithName(prefix + "bedid_string")
  val pronr_string: ColumnWithName = ColumnWithName(prefix + "pronr_string")
  val zaehl_string: ColumnWithName = ColumnWithName(prefix + "zaehl_string")
  val mzaehl_string: ColumnWithName = ColumnWithName(prefix + "mzaehl_string")
  val zkriz_string: ColumnWithName = ColumnWithName(prefix + "zkriz_string")
  val prueflos_string: ColumnWithName = ColumnWithName(prefix + "prueflos_string")
  val klvarp_string: ColumnWithName = ColumnWithName(prefix + "klvarp_string")
  val klvari_string: ColumnWithName = ColumnWithName(prefix + "klvari_string")
  val rgekz_string: ColumnWithName = ColumnWithName(prefix + "rgekz_string")
  val plart_string: ColumnWithName = ColumnWithName(prefix + "plart_string")
  val flg_aob_string: ColumnWithName = ColumnWithName(prefix + "flg_aob_string")
  val flg_arbei_string: ColumnWithName = ColumnWithName(prefix + "flg_arbei_string")
  val gltpp_string: ColumnWithName = ColumnWithName(prefix + "gltpp_string")
  val gstpp_string: ColumnWithName = ColumnWithName(prefix + "gstpp_string")
  val gltps_string: ColumnWithName = ColumnWithName(prefix + "gltps_string")
  val gstps_string: ColumnWithName = ColumnWithName(prefix + "gstps_string")
  val ftrps_string: ColumnWithName = ColumnWithName(prefix + "ftrps_string")
  val rdkzp_string: ColumnWithName = ColumnWithName(prefix + "rdkzp_string")
  val trkzp_string: ColumnWithName = ColumnWithName(prefix + "trkzp_string")
  val rueck_string: ColumnWithName = ColumnWithName(prefix + "rueck_string")
  val rmzhl_string: ColumnWithName = ColumnWithName(prefix + "rmzhl_string")
  val igmng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "igmng_decimal_13_3")
  val ratid_string: ColumnWithName = ColumnWithName(prefix + "ratid_string")
  val groid_string: ColumnWithName = ColumnWithName(prefix + "groid_string")
  val cuobj_string: ColumnWithName = ColumnWithName(prefix + "cuobj_string")
  val gluzs_string: ColumnWithName = ColumnWithName(prefix + "gluzs_string")
  val gsuzs_string: ColumnWithName = ColumnWithName(prefix + "gsuzs_string")
  val revlv_string: ColumnWithName = ColumnWithName(prefix + "revlv_string")
  val rshty_string: ColumnWithName = ColumnWithName(prefix + "rshty_string")
  val rshid_string: ColumnWithName = ColumnWithName(prefix + "rshid_string")
  val rsnty_string: ColumnWithName = ColumnWithName(prefix + "rsnty_string")
  val rsnid_string: ColumnWithName = ColumnWithName(prefix + "rsnid_string")
  val nauterm_string: ColumnWithName = ColumnWithName(prefix + "nauterm_string")
  val naucost_string: ColumnWithName = ColumnWithName(prefix + "naucost_string")
  val stufe_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "stufe_decimal_2_0")
  val wegxx_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "wegxx_decimal_4_0")
  val vwegx_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "vwegx_decimal_4_0")
  val arsnr_string: ColumnWithName = ColumnWithName(prefix + "arsnr_string")
  val arsps_string: ColumnWithName = ColumnWithName(prefix + "arsps_string")
  val maufnr_string: ColumnWithName = ColumnWithName(prefix + "maufnr_string")
  val lknot_string: ColumnWithName = ColumnWithName(prefix + "lknot_string")
  val rknot_string: ColumnWithName = ColumnWithName(prefix + "rknot_string")
  val prodnet_string: ColumnWithName = ColumnWithName(prefix + "prodnet_string")
  val iasmg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "iasmg_decimal_13_3")
  val abarb_string: ColumnWithName = ColumnWithName(prefix + "abarb_string")
  val aufnt_string: ColumnWithName = ColumnWithName(prefix + "aufnt_string")
  val aufpt_string: ColumnWithName = ColumnWithName(prefix + "aufpt_string")
  val aplzt_string: ColumnWithName = ColumnWithName(prefix + "aplzt_string")
  val no_disp_string: ColumnWithName = ColumnWithName(prefix + "no_disp_string")
  val csplit_string: ColumnWithName = ColumnWithName(prefix + "csplit_string")
  val aennr_string: ColumnWithName = ColumnWithName(prefix + "aennr_string")
  val cy_seqnr_string: ColumnWithName = ColumnWithName(prefix + "cy_seqnr_string")
  val breaks_string: ColumnWithName = ColumnWithName(prefix + "breaks_string")
  val vorgz_trm_decimal_6_3: ColumnWithName = ColumnWithName(prefix + "vorgz_trm_decimal_6_3")
  val sichz_trm_decimal_6_3: ColumnWithName = ColumnWithName(prefix + "sichz_trm_decimal_6_3")
  val trmdt_string: ColumnWithName = ColumnWithName(prefix + "trmdt_string")
  val gluzp_string: ColumnWithName = ColumnWithName(prefix + "gluzp_string")
  val gsuzp_string: ColumnWithName = ColumnWithName(prefix + "gsuzp_string")
  val gsuzi_string: ColumnWithName = ColumnWithName(prefix + "gsuzi_string")
  val geuzi_string: ColumnWithName = ColumnWithName(prefix + "geuzi_string")
  val glupp_string: ColumnWithName = ColumnWithName(prefix + "glupp_string")
  val gsupp_string: ColumnWithName = ColumnWithName(prefix + "gsupp_string")
  val glups_string: ColumnWithName = ColumnWithName(prefix + "glups_string")
  val gsups_string: ColumnWithName = ColumnWithName(prefix + "gsups_string")
  val chsch_string: ColumnWithName = ColumnWithName(prefix + "chsch_string")
  val kapt_vorgz_string: ColumnWithName = ColumnWithName(prefix + "kapt_vorgz_string")
  val kapt_sichz_string: ColumnWithName = ColumnWithName(prefix + "kapt_sichz_string")
  val lead_aufnr_string: ColumnWithName = ColumnWithName(prefix + "lead_aufnr_string")
  val pnetstartd_string: ColumnWithName = ColumnWithName(prefix + "pnetstartd_string")
  val pnetstartt_string: ColumnWithName = ColumnWithName(prefix + "pnetstartt_string")
  val pnetendd_string: ColumnWithName = ColumnWithName(prefix + "pnetendd_string")
  val pnetendt_string: ColumnWithName = ColumnWithName(prefix + "pnetendt_string")
  val kbed_string: ColumnWithName = ColumnWithName(prefix + "kbed_string")
  val kkalkr_string: ColumnWithName = ColumnWithName(prefix + "kkalkr_string")
  val sfcpf_string: ColumnWithName = ColumnWithName(prefix + "sfcpf_string")
  val rmnga_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "rmnga_decimal_13_3")
  val gsbtr_string: ColumnWithName = ColumnWithName(prefix + "gsbtr_string")
  val vfmng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "vfmng_decimal_13_3")
  val nopcost_string: ColumnWithName = ColumnWithName(prefix + "nopcost_string")
  val netzkont_string: ColumnWithName = ColumnWithName(prefix + "netzkont_string")
  val atrkz_string: ColumnWithName = ColumnWithName(prefix + "atrkz_string")
  val objtype_string: ColumnWithName = ColumnWithName(prefix + "objtype_string")
  val ch_proc_string: ColumnWithName = ColumnWithName(prefix + "ch_proc_string")
  val kapversa_string: ColumnWithName = ColumnWithName(prefix + "kapversa_string")
  val colordproc_string: ColumnWithName = ColumnWithName(prefix + "colordproc_string")
  val kzerb_string: ColumnWithName = ColumnWithName(prefix + "kzerb_string")
  val conf_key_string: ColumnWithName = ColumnWithName(prefix + "conf_key_string")
  val st_arbid_string: ColumnWithName = ColumnWithName(prefix + "st_arbid_string")
  val vsnmr_v_string: ColumnWithName = ColumnWithName(prefix + "vsnmr_v_string")
  val terhw_string: ColumnWithName = ColumnWithName(prefix + "terhw_string")
  val splstat_string: ColumnWithName = ColumnWithName(prefix + "splstat_string")
  val costupd_string: ColumnWithName = ColumnWithName(prefix + "costupd_string")
  val max_gamng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "max_gamng_decimal_13_3")
  val mes_routingid_string: ColumnWithName = ColumnWithName(prefix + "mes_routingid_string")
  val adpsp_string: ColumnWithName = ColumnWithName(prefix + "adpsp_string")
  val rmanr_string: ColumnWithName = ColumnWithName(prefix + "rmanr_string")
  val posnr_rma_string: ColumnWithName = ColumnWithName(prefix + "posnr_rma_string")
  val posnv_rma_string: ColumnWithName = ColumnWithName(prefix + "posnv_rma_string")
  val cfb_maxlz_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "cfb_maxlz_decimal_5_0")
  val cfb_lzeih_string: ColumnWithName = ColumnWithName(prefix + "cfb_lzeih_string")
  val cfb_adtdays_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "cfb_adtdays_decimal_4_0")
  val cfb_datofm_string: ColumnWithName = ColumnWithName(prefix + "cfb_datofm_string")
  val cfb_bbdpi_string: ColumnWithName = ColumnWithName(prefix + "cfb_bbdpi_string")
  val oihantyp_string: ColumnWithName = ColumnWithName(prefix + "oihantyp_string")
  val fsh_mprod_ord_string: ColumnWithName = ColumnWithName(prefix + "fsh_mprod_ord_string")
  val flg_bundle_string: ColumnWithName = ColumnWithName(prefix + "flg_bundle_string")
  val mill_ratio_int: ColumnWithName = ColumnWithName(prefix + "mill_ratio_int")
  val bmeins_string: ColumnWithName = ColumnWithName(prefix + "bmeins_string")
  val bmenge_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bmenge_decimal_13_3")
  val mill_oc_zuskz_string: ColumnWithName = ColumnWithName(prefix + "mill_oc_zuskz_string")
}

object C_afko extends C_afko("") {
  def as(alias: String): C_afko = new C_afko(alias + ".")
}

// AUTO GENERATED:END
