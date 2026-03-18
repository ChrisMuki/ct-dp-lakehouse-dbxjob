// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_e32

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_plpo(
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
    parkz_string: String,
    steus_string: String,
    bukrs_string: String,
    frsp_string: String,
    zgr02_string: String,
    @Decimal(5, 1) daumi_decimal_5_1: BigDecimal,
    ktsch_string: String,
    vge04_string: String,
    @Decimal(5, 0) offstb_decimal_5_0: BigDecimal,
    zeitm_string: String,
    loart_string: String,
    zgdat_string: String,
    txtsp_string: String,
    @Decimal(9, 3) zminu_decimal_9_3: BigDecimal,
    @Decimal(8, 0) evgew_decimal_8_0: BigDecimal,
    annam_string: String,
    @Decimal(3, 0) vintv_decimal_3_0: BigDecimal,
    flg_tsk_group_string: String,
    @Decimal(13, 3) qrastmeng_decimal_13_3: BigDecimal,
    ehoffe_string: String,
    pspnr_string: String,
    rasch_string: String,
    anfkokrs_string: String,
    @Decimal(5, 2) qrastzfak_decimal_5_2: BigDecimal,
    krit1_string: String,
    zeimb_string: String,
    use04_string: String,
    kalid_string: String,
    uemus_string: String,
    usr08_string: String,
    @Decimal(5, 2) qrastermng_decimal_5_2: BigDecimal,
    vertn_string: String,
    qlotype_string: String,
    qkztlsbest_string: String,
    uekan_string: String,
    sortl_string: String,
    plnnr_string: String,
    ckselkz_string: String,
    @Decimal(9, 3) vgw06_decimal_9_3: BigDecimal,
    @Decimal(13, 3) usr05_decimal_13_3: BigDecimal,
    @Decimal(9, 3) ztmin_decimal_9_3: BigDecimal,
    zeimu_string: String,
    istnr_string: String,
    vplty_string: String,
    usr03_string: String,
    kapar_string: String,
    rfgrp_string: String,
    bmeih_string: String,
    @Decimal(9, 3) vgw03_decimal_9_3: BigDecimal,
    @Decimal(13, 3) usr06_decimal_13_3: BigDecimal,
    use06_string: String,
    @Decimal(9, 3) zlpro_decimal_9_3: BigDecimal,
    vge01_string: String,
    vornr_string: String,
    nvadd_string: String,
    zgr05_string: String,
    plnty_string: String,
    qkzprzeit_string: String,
    @Decimal(13, 3) minwe_decimal_13_3: BigDecimal,
    lar04_string: String,
    packno_string: String,
    vertl_string: String,
    rfsch_string: String,
    @Decimal(5, 0) umren_decimal_5_0: BigDecimal,
    @Decimal(9, 3) zwmin_decimal_9_3: BigDecimal,
    ebeln_string: String,
    @Decimal(5, 0) peinh_decimal_5_0: BigDecimal,
    usr00_string: String,
    ablipkz_string: String,
    oprid_string: String,
    vge03_string: String,
    ddehn_string: String,
    subplnnr_string: String,
    @Decimal(13, 3) bmvrg_decimal_13_3: BigDecimal,
    datuv_string: String,
    einse_string: String,
    @Decimal(9, 3) zwnor_decimal_9_3: BigDecimal,
    capoc_string: String,
    zeilp_string: String,
    vge06_string: String,
    @Decimal(9, 3) zlmax_decimal_9_3: BigDecimal,
    kalkz_string: String,
    @Decimal(9, 3) zminb_decimal_9_3: BigDecimal,
    andat_string: String,
    @Decimal(5, 3) aufak_decimal_5_3: BigDecimal,
    qppktabs_string: String,
    plnkn_string: String,
    @Decimal(3, 0) plifz_decimal_3_0: BigDecimal,
    nprio_string: String,
    istty_string: String,
    anzzl_int: BoxedInt,
    spmus_string: String,
    qpart_string: String,
    bzoffe_string: String,
    erfsicht_string: String,
    subplnty_string: String,
    zulnr_string: String,
    vge02_string: String,
    adpsp_string: String,
    mdlid_string: String,
    @Decimal(5, 0) offste_decimal_5_0: BigDecimal,
    ruzus_string: String,
    mlstn_string: String,
    ebelp_string: String,
    pdest_string: String,
    rstra_string: String,
    @Decimal(8, 0) cn_weight_decimal_8_0: BigDecimal,
    qrastzeht_string: String,
    zeiwn_string: String,
    daume_string: String,
    werks_string: String,
    @Decimal(9, 3) ztnor_decimal_9_3: BigDecimal,
    verdart_string: String,
    @Decimal(13, 3) bmsch_decimal_13_3: BigDecimal,
    anfko_string: String,
    mandt_string: String,
    @Decimal(3, 0) splim_decimal_3_0: BigDecimal,
    zeitn_string: String,
    @Decimal(5, 0) umrez_decimal_5_0: BigDecimal,
    ekgrp_string: String,
    rsanz_string: String,
    zeiwm_string: String,
    usr11_string: String,
    vplal_string: String,
    lar03_string: String,
    indet_string: String,
    @Decimal(9, 3) vgw02_decimal_9_3: BigDecimal,
    flg_captxt_string: String,
    zeilm_string: String,
    esokz_string: String,
    @Decimal(9, 3) zmerh_decimal_9_3: BigDecimal,
    knobj_string: String,
    @Decimal(9, 3) vgw05_decimal_9_3: BigDecimal,
    meinh_string: String,
    zgr06_string: String,
    arbid_string: String,
    qkzprmeng_string: String,
    prznt_int: BoxedInt,
    qlkapar_string: String,
    frdlb_string: String,
    equnr_string: String,
    usr01_string: String,
    ekorg_string: String,
    manu_proc_string: String,
    prz01_string: String,
    @Decimal(9, 3) vgw04_decimal_9_3: BigDecimal,
    logrp_string: String,
    sakto_string: String,
    aaufg_string: String,
    zerma_string: String,
    arbeh_string: String,
    use05_string: String,
    slwid_string: String,
    larnt_string: String,
    @Decimal(7, 1) arbei_decimal_7_1: BigDecimal,
    qlobjektid_string: String,
    xexcltl_string: String,
    lar05_string: String,
    flies_string: String,
    istkn_string: String,
    phflg_string: String,
    mes_operid_string: String,
    zcode_string: String,
    @Decimal(11, 2) preis_decimal_11_2: BigDecimal,
    qrastereh_string: String,
    rfpnt_string: String,
    zaehl_string: String,
    istru_string: String,
    einsa_string: String,
    aenam_string: String,
    ehoffb_string: String,
    mes_stepid_string: String,
    loekz_string: String,
    zeier_string: String,
    ebort_string: String,
    zgr01_string: String,
    aedat_string: String,
    vplfl_string: String,
    pvzkn_string: String,
    zgr04_string: String,
    usr02_string: String,
    daune_string: String,
    zgr03_string: String,
    @Decimal(3, 0) aufkt_decimal_3_0: BigDecimal,
    @Decimal(5, 2) dafkt_decimal_5_2: BigDecimal,
    lar06_string: String,
    @Decimal(3, 0) loanz_decimal_3_0: BigDecimal,
    @Decimal(13, 3) usr07_decimal_13_3: BigDecimal,
    lifnr_string: String,
    vge05_string: String,
    @Decimal(13, 3) usr04_decimal_13_3: BigDecimal,
    kzlgf_string: String,
    usr09_string: String,
    objty_string: String,
    anlzu_string: String,
    qualf_string: String,
    @Decimal(11, 2) prkst_decimal_11_2: BigDecimal,
    @Decimal(5, 1) dauno_decimal_5_1: BigDecimal,
    iupoz_string: String,
    phseq_string: String,
    qkzprfrei_string: String,
    vplnr_string: String,
    tplnr_string: String,
    subplnal_string: String,
    aennr_string: String,
    qkzztmg1_string: String,
    techv_string: String,
    bzoffb_string: String,
    classid_string: String,
    takt_string: String,
    lar02_string: String,
    use07_string: String,
    infnr_string: String,
    ltxa1_string: String,
    matkl_string: String,
    sumnr_string: String,
    uavo_aufl_string: String,
    lar01_string: String,
    @Decimal(9, 3) vgw01_decimal_9_3: BigDecimal,
    waers_string: String,
    ltxa2_string: String,
    pprio_string: String,
    @Decimal(5, 2) anzma_decimal_5_2: BigDecimal,
    @Decimal(5, 3) rwfak_decimal_5_3: BigDecimal,
    istpo_string: String,
    usr10_string: String
) extends Entity

object plpo extends TableSpec[E_plpo](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_plpo(prefix: String) extends ColumnWithNameAccessor {
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
  val parkz_string: ColumnWithName = ColumnWithName(prefix + "parkz_string")
  val steus_string: ColumnWithName = ColumnWithName(prefix + "steus_string")
  val bukrs_string: ColumnWithName = ColumnWithName(prefix + "bukrs_string")
  val frsp_string: ColumnWithName = ColumnWithName(prefix + "frsp_string")
  val zgr02_string: ColumnWithName = ColumnWithName(prefix + "zgr02_string")
  val daumi_decimal_5_1: ColumnWithName = ColumnWithName(prefix + "daumi_decimal_5_1")
  val ktsch_string: ColumnWithName = ColumnWithName(prefix + "ktsch_string")
  val vge04_string: ColumnWithName = ColumnWithName(prefix + "vge04_string")
  val offstb_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "offstb_decimal_5_0")
  val zeitm_string: ColumnWithName = ColumnWithName(prefix + "zeitm_string")
  val loart_string: ColumnWithName = ColumnWithName(prefix + "loart_string")
  val zgdat_string: ColumnWithName = ColumnWithName(prefix + "zgdat_string")
  val txtsp_string: ColumnWithName = ColumnWithName(prefix + "txtsp_string")
  val zminu_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "zminu_decimal_9_3")
  val evgew_decimal_8_0: ColumnWithName = ColumnWithName(prefix + "evgew_decimal_8_0")
  val annam_string: ColumnWithName = ColumnWithName(prefix + "annam_string")
  val vintv_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "vintv_decimal_3_0")
  val flg_tsk_group_string: ColumnWithName = ColumnWithName(prefix + "flg_tsk_group_string")
  val qrastmeng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "qrastmeng_decimal_13_3")
  val ehoffe_string: ColumnWithName = ColumnWithName(prefix + "ehoffe_string")
  val pspnr_string: ColumnWithName = ColumnWithName(prefix + "pspnr_string")
  val rasch_string: ColumnWithName = ColumnWithName(prefix + "rasch_string")
  val anfkokrs_string: ColumnWithName = ColumnWithName(prefix + "anfkokrs_string")
  val qrastzfak_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "qrastzfak_decimal_5_2")
  val krit1_string: ColumnWithName = ColumnWithName(prefix + "krit1_string")
  val zeimb_string: ColumnWithName = ColumnWithName(prefix + "zeimb_string")
  val use04_string: ColumnWithName = ColumnWithName(prefix + "use04_string")
  val kalid_string: ColumnWithName = ColumnWithName(prefix + "kalid_string")
  val uemus_string: ColumnWithName = ColumnWithName(prefix + "uemus_string")
  val usr08_string: ColumnWithName = ColumnWithName(prefix + "usr08_string")
  val qrastermng_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "qrastermng_decimal_5_2")
  val vertn_string: ColumnWithName = ColumnWithName(prefix + "vertn_string")
  val qlotype_string: ColumnWithName = ColumnWithName(prefix + "qlotype_string")
  val qkztlsbest_string: ColumnWithName = ColumnWithName(prefix + "qkztlsbest_string")
  val uekan_string: ColumnWithName = ColumnWithName(prefix + "uekan_string")
  val sortl_string: ColumnWithName = ColumnWithName(prefix + "sortl_string")
  val plnnr_string: ColumnWithName = ColumnWithName(prefix + "plnnr_string")
  val ckselkz_string: ColumnWithName = ColumnWithName(prefix + "ckselkz_string")
  val vgw06_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "vgw06_decimal_9_3")
  val usr05_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "usr05_decimal_13_3")
  val ztmin_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "ztmin_decimal_9_3")
  val zeimu_string: ColumnWithName = ColumnWithName(prefix + "zeimu_string")
  val istnr_string: ColumnWithName = ColumnWithName(prefix + "istnr_string")
  val vplty_string: ColumnWithName = ColumnWithName(prefix + "vplty_string")
  val usr03_string: ColumnWithName = ColumnWithName(prefix + "usr03_string")
  val kapar_string: ColumnWithName = ColumnWithName(prefix + "kapar_string")
  val rfgrp_string: ColumnWithName = ColumnWithName(prefix + "rfgrp_string")
  val bmeih_string: ColumnWithName = ColumnWithName(prefix + "bmeih_string")
  val vgw03_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "vgw03_decimal_9_3")
  val usr06_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "usr06_decimal_13_3")
  val use06_string: ColumnWithName = ColumnWithName(prefix + "use06_string")
  val zlpro_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "zlpro_decimal_9_3")
  val vge01_string: ColumnWithName = ColumnWithName(prefix + "vge01_string")
  val vornr_string: ColumnWithName = ColumnWithName(prefix + "vornr_string")
  val nvadd_string: ColumnWithName = ColumnWithName(prefix + "nvadd_string")
  val zgr05_string: ColumnWithName = ColumnWithName(prefix + "zgr05_string")
  val plnty_string: ColumnWithName = ColumnWithName(prefix + "plnty_string")
  val qkzprzeit_string: ColumnWithName = ColumnWithName(prefix + "qkzprzeit_string")
  val minwe_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "minwe_decimal_13_3")
  val lar04_string: ColumnWithName = ColumnWithName(prefix + "lar04_string")
  val packno_string: ColumnWithName = ColumnWithName(prefix + "packno_string")
  val vertl_string: ColumnWithName = ColumnWithName(prefix + "vertl_string")
  val rfsch_string: ColumnWithName = ColumnWithName(prefix + "rfsch_string")
  val umren_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "umren_decimal_5_0")
  val zwmin_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "zwmin_decimal_9_3")
  val ebeln_string: ColumnWithName = ColumnWithName(prefix + "ebeln_string")
  val peinh_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "peinh_decimal_5_0")
  val usr00_string: ColumnWithName = ColumnWithName(prefix + "usr00_string")
  val ablipkz_string: ColumnWithName = ColumnWithName(prefix + "ablipkz_string")
  val oprid_string: ColumnWithName = ColumnWithName(prefix + "oprid_string")
  val vge03_string: ColumnWithName = ColumnWithName(prefix + "vge03_string")
  val ddehn_string: ColumnWithName = ColumnWithName(prefix + "ddehn_string")
  val subplnnr_string: ColumnWithName = ColumnWithName(prefix + "subplnnr_string")
  val bmvrg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bmvrg_decimal_13_3")
  val datuv_string: ColumnWithName = ColumnWithName(prefix + "datuv_string")
  val einse_string: ColumnWithName = ColumnWithName(prefix + "einse_string")
  val zwnor_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "zwnor_decimal_9_3")
  val capoc_string: ColumnWithName = ColumnWithName(prefix + "capoc_string")
  val zeilp_string: ColumnWithName = ColumnWithName(prefix + "zeilp_string")
  val vge06_string: ColumnWithName = ColumnWithName(prefix + "vge06_string")
  val zlmax_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "zlmax_decimal_9_3")
  val kalkz_string: ColumnWithName = ColumnWithName(prefix + "kalkz_string")
  val zminb_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "zminb_decimal_9_3")
  val andat_string: ColumnWithName = ColumnWithName(prefix + "andat_string")
  val aufak_decimal_5_3: ColumnWithName = ColumnWithName(prefix + "aufak_decimal_5_3")
  val qppktabs_string: ColumnWithName = ColumnWithName(prefix + "qppktabs_string")
  val plnkn_string: ColumnWithName = ColumnWithName(prefix + "plnkn_string")
  val plifz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "plifz_decimal_3_0")
  val nprio_string: ColumnWithName = ColumnWithName(prefix + "nprio_string")
  val istty_string: ColumnWithName = ColumnWithName(prefix + "istty_string")
  val anzzl_int: ColumnWithName = ColumnWithName(prefix + "anzzl_int")
  val spmus_string: ColumnWithName = ColumnWithName(prefix + "spmus_string")
  val qpart_string: ColumnWithName = ColumnWithName(prefix + "qpart_string")
  val bzoffe_string: ColumnWithName = ColumnWithName(prefix + "bzoffe_string")
  val erfsicht_string: ColumnWithName = ColumnWithName(prefix + "erfsicht_string")
  val subplnty_string: ColumnWithName = ColumnWithName(prefix + "subplnty_string")
  val zulnr_string: ColumnWithName = ColumnWithName(prefix + "zulnr_string")
  val vge02_string: ColumnWithName = ColumnWithName(prefix + "vge02_string")
  val adpsp_string: ColumnWithName = ColumnWithName(prefix + "adpsp_string")
  val mdlid_string: ColumnWithName = ColumnWithName(prefix + "mdlid_string")
  val offste_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "offste_decimal_5_0")
  val ruzus_string: ColumnWithName = ColumnWithName(prefix + "ruzus_string")
  val mlstn_string: ColumnWithName = ColumnWithName(prefix + "mlstn_string")
  val ebelp_string: ColumnWithName = ColumnWithName(prefix + "ebelp_string")
  val pdest_string: ColumnWithName = ColumnWithName(prefix + "pdest_string")
  val rstra_string: ColumnWithName = ColumnWithName(prefix + "rstra_string")
  val cn_weight_decimal_8_0: ColumnWithName = ColumnWithName(prefix + "cn_weight_decimal_8_0")
  val qrastzeht_string: ColumnWithName = ColumnWithName(prefix + "qrastzeht_string")
  val zeiwn_string: ColumnWithName = ColumnWithName(prefix + "zeiwn_string")
  val daume_string: ColumnWithName = ColumnWithName(prefix + "daume_string")
  val werks_string: ColumnWithName = ColumnWithName(prefix + "werks_string")
  val ztnor_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "ztnor_decimal_9_3")
  val verdart_string: ColumnWithName = ColumnWithName(prefix + "verdart_string")
  val bmsch_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bmsch_decimal_13_3")
  val anfko_string: ColumnWithName = ColumnWithName(prefix + "anfko_string")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val splim_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "splim_decimal_3_0")
  val zeitn_string: ColumnWithName = ColumnWithName(prefix + "zeitn_string")
  val umrez_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "umrez_decimal_5_0")
  val ekgrp_string: ColumnWithName = ColumnWithName(prefix + "ekgrp_string")
  val rsanz_string: ColumnWithName = ColumnWithName(prefix + "rsanz_string")
  val zeiwm_string: ColumnWithName = ColumnWithName(prefix + "zeiwm_string")
  val usr11_string: ColumnWithName = ColumnWithName(prefix + "usr11_string")
  val vplal_string: ColumnWithName = ColumnWithName(prefix + "vplal_string")
  val lar03_string: ColumnWithName = ColumnWithName(prefix + "lar03_string")
  val indet_string: ColumnWithName = ColumnWithName(prefix + "indet_string")
  val vgw02_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "vgw02_decimal_9_3")
  val flg_captxt_string: ColumnWithName = ColumnWithName(prefix + "flg_captxt_string")
  val zeilm_string: ColumnWithName = ColumnWithName(prefix + "zeilm_string")
  val esokz_string: ColumnWithName = ColumnWithName(prefix + "esokz_string")
  val zmerh_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "zmerh_decimal_9_3")
  val knobj_string: ColumnWithName = ColumnWithName(prefix + "knobj_string")
  val vgw05_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "vgw05_decimal_9_3")
  val meinh_string: ColumnWithName = ColumnWithName(prefix + "meinh_string")
  val zgr06_string: ColumnWithName = ColumnWithName(prefix + "zgr06_string")
  val arbid_string: ColumnWithName = ColumnWithName(prefix + "arbid_string")
  val qkzprmeng_string: ColumnWithName = ColumnWithName(prefix + "qkzprmeng_string")
  val prznt_int: ColumnWithName = ColumnWithName(prefix + "prznt_int")
  val qlkapar_string: ColumnWithName = ColumnWithName(prefix + "qlkapar_string")
  val frdlb_string: ColumnWithName = ColumnWithName(prefix + "frdlb_string")
  val equnr_string: ColumnWithName = ColumnWithName(prefix + "equnr_string")
  val usr01_string: ColumnWithName = ColumnWithName(prefix + "usr01_string")
  val ekorg_string: ColumnWithName = ColumnWithName(prefix + "ekorg_string")
  val manu_proc_string: ColumnWithName = ColumnWithName(prefix + "manu_proc_string")
  val prz01_string: ColumnWithName = ColumnWithName(prefix + "prz01_string")
  val vgw04_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "vgw04_decimal_9_3")
  val logrp_string: ColumnWithName = ColumnWithName(prefix + "logrp_string")
  val sakto_string: ColumnWithName = ColumnWithName(prefix + "sakto_string")
  val aaufg_string: ColumnWithName = ColumnWithName(prefix + "aaufg_string")
  val zerma_string: ColumnWithName = ColumnWithName(prefix + "zerma_string")
  val arbeh_string: ColumnWithName = ColumnWithName(prefix + "arbeh_string")
  val use05_string: ColumnWithName = ColumnWithName(prefix + "use05_string")
  val slwid_string: ColumnWithName = ColumnWithName(prefix + "slwid_string")
  val larnt_string: ColumnWithName = ColumnWithName(prefix + "larnt_string")
  val arbei_decimal_7_1: ColumnWithName = ColumnWithName(prefix + "arbei_decimal_7_1")
  val qlobjektid_string: ColumnWithName = ColumnWithName(prefix + "qlobjektid_string")
  val xexcltl_string: ColumnWithName = ColumnWithName(prefix + "xexcltl_string")
  val lar05_string: ColumnWithName = ColumnWithName(prefix + "lar05_string")
  val flies_string: ColumnWithName = ColumnWithName(prefix + "flies_string")
  val istkn_string: ColumnWithName = ColumnWithName(prefix + "istkn_string")
  val phflg_string: ColumnWithName = ColumnWithName(prefix + "phflg_string")
  val mes_operid_string: ColumnWithName = ColumnWithName(prefix + "mes_operid_string")
  val zcode_string: ColumnWithName = ColumnWithName(prefix + "zcode_string")
  val preis_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "preis_decimal_11_2")
  val qrastereh_string: ColumnWithName = ColumnWithName(prefix + "qrastereh_string")
  val rfpnt_string: ColumnWithName = ColumnWithName(prefix + "rfpnt_string")
  val zaehl_string: ColumnWithName = ColumnWithName(prefix + "zaehl_string")
  val istru_string: ColumnWithName = ColumnWithName(prefix + "istru_string")
  val einsa_string: ColumnWithName = ColumnWithName(prefix + "einsa_string")
  val aenam_string: ColumnWithName = ColumnWithName(prefix + "aenam_string")
  val ehoffb_string: ColumnWithName = ColumnWithName(prefix + "ehoffb_string")
  val mes_stepid_string: ColumnWithName = ColumnWithName(prefix + "mes_stepid_string")
  val loekz_string: ColumnWithName = ColumnWithName(prefix + "loekz_string")
  val zeier_string: ColumnWithName = ColumnWithName(prefix + "zeier_string")
  val ebort_string: ColumnWithName = ColumnWithName(prefix + "ebort_string")
  val zgr01_string: ColumnWithName = ColumnWithName(prefix + "zgr01_string")
  val aedat_string: ColumnWithName = ColumnWithName(prefix + "aedat_string")
  val vplfl_string: ColumnWithName = ColumnWithName(prefix + "vplfl_string")
  val pvzkn_string: ColumnWithName = ColumnWithName(prefix + "pvzkn_string")
  val zgr04_string: ColumnWithName = ColumnWithName(prefix + "zgr04_string")
  val usr02_string: ColumnWithName = ColumnWithName(prefix + "usr02_string")
  val daune_string: ColumnWithName = ColumnWithName(prefix + "daune_string")
  val zgr03_string: ColumnWithName = ColumnWithName(prefix + "zgr03_string")
  val aufkt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "aufkt_decimal_3_0")
  val dafkt_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "dafkt_decimal_5_2")
  val lar06_string: ColumnWithName = ColumnWithName(prefix + "lar06_string")
  val loanz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "loanz_decimal_3_0")
  val usr07_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "usr07_decimal_13_3")
  val lifnr_string: ColumnWithName = ColumnWithName(prefix + "lifnr_string")
  val vge05_string: ColumnWithName = ColumnWithName(prefix + "vge05_string")
  val usr04_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "usr04_decimal_13_3")
  val kzlgf_string: ColumnWithName = ColumnWithName(prefix + "kzlgf_string")
  val usr09_string: ColumnWithName = ColumnWithName(prefix + "usr09_string")
  val objty_string: ColumnWithName = ColumnWithName(prefix + "objty_string")
  val anlzu_string: ColumnWithName = ColumnWithName(prefix + "anlzu_string")
  val qualf_string: ColumnWithName = ColumnWithName(prefix + "qualf_string")
  val prkst_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "prkst_decimal_11_2")
  val dauno_decimal_5_1: ColumnWithName = ColumnWithName(prefix + "dauno_decimal_5_1")
  val iupoz_string: ColumnWithName = ColumnWithName(prefix + "iupoz_string")
  val phseq_string: ColumnWithName = ColumnWithName(prefix + "phseq_string")
  val qkzprfrei_string: ColumnWithName = ColumnWithName(prefix + "qkzprfrei_string")
  val vplnr_string: ColumnWithName = ColumnWithName(prefix + "vplnr_string")
  val tplnr_string: ColumnWithName = ColumnWithName(prefix + "tplnr_string")
  val subplnal_string: ColumnWithName = ColumnWithName(prefix + "subplnal_string")
  val aennr_string: ColumnWithName = ColumnWithName(prefix + "aennr_string")
  val qkzztmg1_string: ColumnWithName = ColumnWithName(prefix + "qkzztmg1_string")
  val techv_string: ColumnWithName = ColumnWithName(prefix + "techv_string")
  val bzoffb_string: ColumnWithName = ColumnWithName(prefix + "bzoffb_string")
  val classid_string: ColumnWithName = ColumnWithName(prefix + "classid_string")
  val takt_string: ColumnWithName = ColumnWithName(prefix + "takt_string")
  val lar02_string: ColumnWithName = ColumnWithName(prefix + "lar02_string")
  val use07_string: ColumnWithName = ColumnWithName(prefix + "use07_string")
  val infnr_string: ColumnWithName = ColumnWithName(prefix + "infnr_string")
  val ltxa1_string: ColumnWithName = ColumnWithName(prefix + "ltxa1_string")
  val matkl_string: ColumnWithName = ColumnWithName(prefix + "matkl_string")
  val sumnr_string: ColumnWithName = ColumnWithName(prefix + "sumnr_string")
  val uavo_aufl_string: ColumnWithName = ColumnWithName(prefix + "uavo_aufl_string")
  val lar01_string: ColumnWithName = ColumnWithName(prefix + "lar01_string")
  val vgw01_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "vgw01_decimal_9_3")
  val waers_string: ColumnWithName = ColumnWithName(prefix + "waers_string")
  val ltxa2_string: ColumnWithName = ColumnWithName(prefix + "ltxa2_string")
  val pprio_string: ColumnWithName = ColumnWithName(prefix + "pprio_string")
  val anzma_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "anzma_decimal_5_2")
  val rwfak_decimal_5_3: ColumnWithName = ColumnWithName(prefix + "rwfak_decimal_5_3")
  val istpo_string: ColumnWithName = ColumnWithName(prefix + "istpo_string")
  val usr10_string: ColumnWithName = ColumnWithName(prefix + "usr10_string")
}

object C_plpo extends C_plpo("") {
  def as(alias: String): C_plpo = new C_plpo(alias + ".")
}

// AUTO GENERATED:END
