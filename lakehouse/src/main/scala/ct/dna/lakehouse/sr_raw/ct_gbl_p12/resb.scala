// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.sr_raw.ct_gbl_p12

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_resb(
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
    rsnum_string: String,
    rspos_string: String,
    rsart_string: String,
    bdart_string: String,
    rssta_string: String,
    xloek_string: String,
    xwaok_string: String,
    kzear_string: String,
    xfehl_string: String,
    matnr_string: String,
    werks_string: String,
    lgort_string: String,
    prvbe_string: String,
    charg_string: String,
    plpla_string: String,
    sobkz_string: String,
    bdter_string: String,
    @Decimal(13, 3) bdmng_decimal_13_3: BigDecimal,
    meins_string: String,
    shkzg_string: String,
    fmeng_string: String,
    @Decimal(13, 3) enmng_decimal_13_3: BigDecimal,
    @Decimal(13, 2) enwrt_decimal_13_2: BigDecimal,
    waers_string: String,
    @Decimal(13, 3) erfmg_decimal_13_3: BigDecimal,
    erfme_string: String,
    plnum_string: String,
    banfn_string: String,
    bnfpo_string: String,
    aufnr_string: String,
    baugr_string: String,
    sernr_string: String,
    kdauf_string: String,
    kdpos_string: String,
    kdein_string: String,
    projn_string: String,
    bwart_string: String,
    saknr_string: String,
    gsber_string: String,
    umwrk_string: String,
    umlgo_string: String,
    nafkz_string: String,
    nomat_string: String,
    @Decimal(13, 3) nomng_decimal_13_3: BigDecimal,
    postp_string: String,
    posnr_string: String,
    @Decimal(13, 3) roms1_decimal_13_3: BigDecimal,
    @Decimal(13, 3) roms2_decimal_13_3: BigDecimal,
    @Decimal(13, 3) roms3_decimal_13_3: BigDecimal,
    romei_string: String,
    @Decimal(13, 3) romen_decimal_13_3: BigDecimal,
    sgtxt_string: String,
    @Decimal(13, 3) lmeng_decimal_13_3: BigDecimal,
    rohps_string: String,
    rform_string: String,
    @Decimal(13, 3) roanz_decimal_13_3: BigDecimal,
    @Decimal(13, 3) flmng_decimal_13_3: BigDecimal,
    stlty_string: String,
    stlnr_string: String,
    stlkn_string: String,
    stpoz_string: String,
    ltxsp_string: String,
    potx1_string: String,
    potx2_string: String,
    sanka_string: String,
    alpos_string: String,
    @Decimal(3, 0) ewahr_decimal_3_0: BigDecimal,
    @Decimal(5, 2) ausch_decimal_5_2: BigDecimal,
    @Decimal(5, 2) avoau_decimal_5_2: BigDecimal,
    netau_string: String,
    @Decimal(3, 0) nlfzt_decimal_3_0: BigDecimal,
    aennr_string: String,
    @Decimal(5, 0) umrez_decimal_5_0: BigDecimal,
    @Decimal(5, 0) umren_decimal_5_0: BigDecimal,
    sortf_string: String,
    sbter_string: String,
    verti_string: String,
    schgt_string: String,
    upskz_string: String,
    dbskz_string: String,
    txtps_string: String,
    dumps_string: String,
    beikz_string: String,
    erskz_string: String,
    aufst_string: String,
    aufwg_string: String,
    baust_string: String,
    bauwg_string: String,
    aufps_string: String,
    ebeln_string: String,
    ebelp_string: String,
    ebele_string: String,
    knttp_string: String,
    kzvbr_string: String,
    pspel_string: String,
    aufpl_string: String,
    plnfl_string: String,
    vornr_string: String,
    aplzl_string: String,
    objnr_string: String,
    flgat_string: String,
    @Decimal(15, 2) gpreis_decimal_15_2: BigDecimal,
    @Decimal(15, 2) fpreis_decimal_15_2: BigDecimal,
    @Decimal(5, 0) peinh_decimal_5_0: BigDecimal,
    rgekz_string: String,
    ekgrp_string: String,
    rokme_string: String,
    zumei_string: String,
    @Decimal(13, 3) zums1_decimal_13_3: BigDecimal,
    @Decimal(13, 3) zums2_decimal_13_3: BigDecimal,
    @Decimal(13, 3) zums3_decimal_13_3: BigDecimal,
    zudiv_string: String,
    @Decimal(15, 3) vmeng_decimal_15_3: BigDecimal,
    prreg_string: String,
    @Decimal(3, 0) lifzt_decimal_3_0: BigDecimal,
    cuobj_string: String,
    kfpos_string: String,
    revlv_string: String,
    berkz_string: String,
    lgnum_string: String,
    lgtyp_string: String,
    lgpla_string: String,
    @Decimal(13, 3) tbmng_decimal_13_3: BigDecimal,
    nptxtky_string: String,
    kbnkz_string: String,
    kzkup_string: String,
    afpos_string: String,
    no_disp_string: String,
    bdztp_string: String,
    esmng_double: BoxedDouble,
    alpgr_string: String,
    alprf_string: String,
    alpst_string: String,
    kzaus_string: String,
    nfeag_string: String,
    nfpkz_string: String,
    nfgrp_string: String,
    @Decimal(5, 4) nfuml_decimal_5_4: BigDecimal,
    adrnr_string: String,
    chobj_string: String,
    splkz_string: String,
    splrv_string: String,
    knumh_string: String,
    wempf_string: String,
    ablad_string: String,
    hkmat_string: String,
    hrkft_string: String,
    vorab_string: String,
    matkl_string: String,
    frunv_string: String,
    clakz_string: String,
    inpos_string: String,
    @Decimal(3, 0) webaz_decimal_3_0: BigDecimal,
    lifnr_string: String,
    flgex_string: String,
    funct_string: String,
    @Decimal(15, 2) gpreis_2_decimal_15_2: BigDecimal,
    @Decimal(15, 2) fpreis_2_decimal_15_2: BigDecimal,
    @Decimal(5, 0) peinh_2_decimal_5_0: BigDecimal,
    infnr_string: String,
    kzech_string: String,
    kzmpf_string: String,
    stlal_string: String,
    pbdnr_string: String,
    stvkn_string: String,
    ktoma_string: String,
    vrpla_string: String,
    kzbws_string: String,
    @Decimal(3, 0) nlfzv_decimal_3_0: BigDecimal,
    nlfmv_string: String,
    techs_string: String,
    objtype_string: String,
    ch_proc_string: String,
    fxpru_string: String,
    umsok_string: String,
    vorab_sm_string: String,
    fipos_string: String,
    fipex_string: String,
    fistl_string: String,
    geber_string: String,
    grant_nbr_string: String,
    fkber_string: String,
    prio_urg_string: String,
    prio_req_string: String,
    kblnr_string: String,
    kblpos_string: String,
    budget_pd_string: String,
    sc_object_id_string: String,
    sc_itm_no_int: BoxedInt,
    sgt_scat_string: String,
    sgt_rcat_string: String,
    fmfgus_key_string: String,
    advcode_string: String,
    struc_code_string: String,
    struc_class_string: String,
    struc_classtyp_string: String,
    @Decimal(13, 3) fsh_ralloc_qty_decimal_13_3: BigDecimal,
    fsh_critical_comp_string: String,
    @Decimal(2, 0) fsh_critical_level_decimal_2_0: BigDecimal,
    mill_ucdet_string: String,
    wty_ind_string: String,
    r_part_indicator_string: String,
    wtysc_clmitem_string: String
) extends Entity

object resb extends TableSpec[E_resb](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_resb(prefix: String) extends ColumnWithNameAccessor {
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
  val rsnum_string: ColumnWithName = ColumnWithName(prefix + "rsnum_string")
  val rspos_string: ColumnWithName = ColumnWithName(prefix + "rspos_string")
  val rsart_string: ColumnWithName = ColumnWithName(prefix + "rsart_string")
  val bdart_string: ColumnWithName = ColumnWithName(prefix + "bdart_string")
  val rssta_string: ColumnWithName = ColumnWithName(prefix + "rssta_string")
  val xloek_string: ColumnWithName = ColumnWithName(prefix + "xloek_string")
  val xwaok_string: ColumnWithName = ColumnWithName(prefix + "xwaok_string")
  val kzear_string: ColumnWithName = ColumnWithName(prefix + "kzear_string")
  val xfehl_string: ColumnWithName = ColumnWithName(prefix + "xfehl_string")
  val matnr_string: ColumnWithName = ColumnWithName(prefix + "matnr_string")
  val werks_string: ColumnWithName = ColumnWithName(prefix + "werks_string")
  val lgort_string: ColumnWithName = ColumnWithName(prefix + "lgort_string")
  val prvbe_string: ColumnWithName = ColumnWithName(prefix + "prvbe_string")
  val charg_string: ColumnWithName = ColumnWithName(prefix + "charg_string")
  val plpla_string: ColumnWithName = ColumnWithName(prefix + "plpla_string")
  val sobkz_string: ColumnWithName = ColumnWithName(prefix + "sobkz_string")
  val bdter_string: ColumnWithName = ColumnWithName(prefix + "bdter_string")
  val bdmng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "bdmng_decimal_13_3")
  val meins_string: ColumnWithName = ColumnWithName(prefix + "meins_string")
  val shkzg_string: ColumnWithName = ColumnWithName(prefix + "shkzg_string")
  val fmeng_string: ColumnWithName = ColumnWithName(prefix + "fmeng_string")
  val enmng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "enmng_decimal_13_3")
  val enwrt_decimal_13_2: ColumnWithName = ColumnWithName(prefix + "enwrt_decimal_13_2")
  val waers_string: ColumnWithName = ColumnWithName(prefix + "waers_string")
  val erfmg_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "erfmg_decimal_13_3")
  val erfme_string: ColumnWithName = ColumnWithName(prefix + "erfme_string")
  val plnum_string: ColumnWithName = ColumnWithName(prefix + "plnum_string")
  val banfn_string: ColumnWithName = ColumnWithName(prefix + "banfn_string")
  val bnfpo_string: ColumnWithName = ColumnWithName(prefix + "bnfpo_string")
  val aufnr_string: ColumnWithName = ColumnWithName(prefix + "aufnr_string")
  val baugr_string: ColumnWithName = ColumnWithName(prefix + "baugr_string")
  val sernr_string: ColumnWithName = ColumnWithName(prefix + "sernr_string")
  val kdauf_string: ColumnWithName = ColumnWithName(prefix + "kdauf_string")
  val kdpos_string: ColumnWithName = ColumnWithName(prefix + "kdpos_string")
  val kdein_string: ColumnWithName = ColumnWithName(prefix + "kdein_string")
  val projn_string: ColumnWithName = ColumnWithName(prefix + "projn_string")
  val bwart_string: ColumnWithName = ColumnWithName(prefix + "bwart_string")
  val saknr_string: ColumnWithName = ColumnWithName(prefix + "saknr_string")
  val gsber_string: ColumnWithName = ColumnWithName(prefix + "gsber_string")
  val umwrk_string: ColumnWithName = ColumnWithName(prefix + "umwrk_string")
  val umlgo_string: ColumnWithName = ColumnWithName(prefix + "umlgo_string")
  val nafkz_string: ColumnWithName = ColumnWithName(prefix + "nafkz_string")
  val nomat_string: ColumnWithName = ColumnWithName(prefix + "nomat_string")
  val nomng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "nomng_decimal_13_3")
  val postp_string: ColumnWithName = ColumnWithName(prefix + "postp_string")
  val posnr_string: ColumnWithName = ColumnWithName(prefix + "posnr_string")
  val roms1_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roms1_decimal_13_3")
  val roms2_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roms2_decimal_13_3")
  val roms3_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roms3_decimal_13_3")
  val romei_string: ColumnWithName = ColumnWithName(prefix + "romei_string")
  val romen_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "romen_decimal_13_3")
  val sgtxt_string: ColumnWithName = ColumnWithName(prefix + "sgtxt_string")
  val lmeng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "lmeng_decimal_13_3")
  val rohps_string: ColumnWithName = ColumnWithName(prefix + "rohps_string")
  val rform_string: ColumnWithName = ColumnWithName(prefix + "rform_string")
  val roanz_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roanz_decimal_13_3")
  val flmng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "flmng_decimal_13_3")
  val stlty_string: ColumnWithName = ColumnWithName(prefix + "stlty_string")
  val stlnr_string: ColumnWithName = ColumnWithName(prefix + "stlnr_string")
  val stlkn_string: ColumnWithName = ColumnWithName(prefix + "stlkn_string")
  val stpoz_string: ColumnWithName = ColumnWithName(prefix + "stpoz_string")
  val ltxsp_string: ColumnWithName = ColumnWithName(prefix + "ltxsp_string")
  val potx1_string: ColumnWithName = ColumnWithName(prefix + "potx1_string")
  val potx2_string: ColumnWithName = ColumnWithName(prefix + "potx2_string")
  val sanka_string: ColumnWithName = ColumnWithName(prefix + "sanka_string")
  val alpos_string: ColumnWithName = ColumnWithName(prefix + "alpos_string")
  val ewahr_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "ewahr_decimal_3_0")
  val ausch_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "ausch_decimal_5_2")
  val avoau_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "avoau_decimal_5_2")
  val netau_string: ColumnWithName = ColumnWithName(prefix + "netau_string")
  val nlfzt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "nlfzt_decimal_3_0")
  val aennr_string: ColumnWithName = ColumnWithName(prefix + "aennr_string")
  val umrez_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "umrez_decimal_5_0")
  val umren_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "umren_decimal_5_0")
  val sortf_string: ColumnWithName = ColumnWithName(prefix + "sortf_string")
  val sbter_string: ColumnWithName = ColumnWithName(prefix + "sbter_string")
  val verti_string: ColumnWithName = ColumnWithName(prefix + "verti_string")
  val schgt_string: ColumnWithName = ColumnWithName(prefix + "schgt_string")
  val upskz_string: ColumnWithName = ColumnWithName(prefix + "upskz_string")
  val dbskz_string: ColumnWithName = ColumnWithName(prefix + "dbskz_string")
  val txtps_string: ColumnWithName = ColumnWithName(prefix + "txtps_string")
  val dumps_string: ColumnWithName = ColumnWithName(prefix + "dumps_string")
  val beikz_string: ColumnWithName = ColumnWithName(prefix + "beikz_string")
  val erskz_string: ColumnWithName = ColumnWithName(prefix + "erskz_string")
  val aufst_string: ColumnWithName = ColumnWithName(prefix + "aufst_string")
  val aufwg_string: ColumnWithName = ColumnWithName(prefix + "aufwg_string")
  val baust_string: ColumnWithName = ColumnWithName(prefix + "baust_string")
  val bauwg_string: ColumnWithName = ColumnWithName(prefix + "bauwg_string")
  val aufps_string: ColumnWithName = ColumnWithName(prefix + "aufps_string")
  val ebeln_string: ColumnWithName = ColumnWithName(prefix + "ebeln_string")
  val ebelp_string: ColumnWithName = ColumnWithName(prefix + "ebelp_string")
  val ebele_string: ColumnWithName = ColumnWithName(prefix + "ebele_string")
  val knttp_string: ColumnWithName = ColumnWithName(prefix + "knttp_string")
  val kzvbr_string: ColumnWithName = ColumnWithName(prefix + "kzvbr_string")
  val pspel_string: ColumnWithName = ColumnWithName(prefix + "pspel_string")
  val aufpl_string: ColumnWithName = ColumnWithName(prefix + "aufpl_string")
  val plnfl_string: ColumnWithName = ColumnWithName(prefix + "plnfl_string")
  val vornr_string: ColumnWithName = ColumnWithName(prefix + "vornr_string")
  val aplzl_string: ColumnWithName = ColumnWithName(prefix + "aplzl_string")
  val objnr_string: ColumnWithName = ColumnWithName(prefix + "objnr_string")
  val flgat_string: ColumnWithName = ColumnWithName(prefix + "flgat_string")
  val gpreis_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "gpreis_decimal_15_2")
  val fpreis_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "fpreis_decimal_15_2")
  val peinh_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "peinh_decimal_5_0")
  val rgekz_string: ColumnWithName = ColumnWithName(prefix + "rgekz_string")
  val ekgrp_string: ColumnWithName = ColumnWithName(prefix + "ekgrp_string")
  val rokme_string: ColumnWithName = ColumnWithName(prefix + "rokme_string")
  val zumei_string: ColumnWithName = ColumnWithName(prefix + "zumei_string")
  val zums1_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "zums1_decimal_13_3")
  val zums2_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "zums2_decimal_13_3")
  val zums3_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "zums3_decimal_13_3")
  val zudiv_string: ColumnWithName = ColumnWithName(prefix + "zudiv_string")
  val vmeng_decimal_15_3: ColumnWithName = ColumnWithName(prefix + "vmeng_decimal_15_3")
  val prreg_string: ColumnWithName = ColumnWithName(prefix + "prreg_string")
  val lifzt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "lifzt_decimal_3_0")
  val cuobj_string: ColumnWithName = ColumnWithName(prefix + "cuobj_string")
  val kfpos_string: ColumnWithName = ColumnWithName(prefix + "kfpos_string")
  val revlv_string: ColumnWithName = ColumnWithName(prefix + "revlv_string")
  val berkz_string: ColumnWithName = ColumnWithName(prefix + "berkz_string")
  val lgnum_string: ColumnWithName = ColumnWithName(prefix + "lgnum_string")
  val lgtyp_string: ColumnWithName = ColumnWithName(prefix + "lgtyp_string")
  val lgpla_string: ColumnWithName = ColumnWithName(prefix + "lgpla_string")
  val tbmng_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "tbmng_decimal_13_3")
  val nptxtky_string: ColumnWithName = ColumnWithName(prefix + "nptxtky_string")
  val kbnkz_string: ColumnWithName = ColumnWithName(prefix + "kbnkz_string")
  val kzkup_string: ColumnWithName = ColumnWithName(prefix + "kzkup_string")
  val afpos_string: ColumnWithName = ColumnWithName(prefix + "afpos_string")
  val no_disp_string: ColumnWithName = ColumnWithName(prefix + "no_disp_string")
  val bdztp_string: ColumnWithName = ColumnWithName(prefix + "bdztp_string")
  val esmng_double: ColumnWithName = ColumnWithName(prefix + "esmng_double")
  val alpgr_string: ColumnWithName = ColumnWithName(prefix + "alpgr_string")
  val alprf_string: ColumnWithName = ColumnWithName(prefix + "alprf_string")
  val alpst_string: ColumnWithName = ColumnWithName(prefix + "alpst_string")
  val kzaus_string: ColumnWithName = ColumnWithName(prefix + "kzaus_string")
  val nfeag_string: ColumnWithName = ColumnWithName(prefix + "nfeag_string")
  val nfpkz_string: ColumnWithName = ColumnWithName(prefix + "nfpkz_string")
  val nfgrp_string: ColumnWithName = ColumnWithName(prefix + "nfgrp_string")
  val nfuml_decimal_5_4: ColumnWithName = ColumnWithName(prefix + "nfuml_decimal_5_4")
  val adrnr_string: ColumnWithName = ColumnWithName(prefix + "adrnr_string")
  val chobj_string: ColumnWithName = ColumnWithName(prefix + "chobj_string")
  val splkz_string: ColumnWithName = ColumnWithName(prefix + "splkz_string")
  val splrv_string: ColumnWithName = ColumnWithName(prefix + "splrv_string")
  val knumh_string: ColumnWithName = ColumnWithName(prefix + "knumh_string")
  val wempf_string: ColumnWithName = ColumnWithName(prefix + "wempf_string")
  val ablad_string: ColumnWithName = ColumnWithName(prefix + "ablad_string")
  val hkmat_string: ColumnWithName = ColumnWithName(prefix + "hkmat_string")
  val hrkft_string: ColumnWithName = ColumnWithName(prefix + "hrkft_string")
  val vorab_string: ColumnWithName = ColumnWithName(prefix + "vorab_string")
  val matkl_string: ColumnWithName = ColumnWithName(prefix + "matkl_string")
  val frunv_string: ColumnWithName = ColumnWithName(prefix + "frunv_string")
  val clakz_string: ColumnWithName = ColumnWithName(prefix + "clakz_string")
  val inpos_string: ColumnWithName = ColumnWithName(prefix + "inpos_string")
  val webaz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "webaz_decimal_3_0")
  val lifnr_string: ColumnWithName = ColumnWithName(prefix + "lifnr_string")
  val flgex_string: ColumnWithName = ColumnWithName(prefix + "flgex_string")
  val funct_string: ColumnWithName = ColumnWithName(prefix + "funct_string")
  val gpreis_2_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "gpreis_2_decimal_15_2")
  val fpreis_2_decimal_15_2: ColumnWithName = ColumnWithName(prefix + "fpreis_2_decimal_15_2")
  val peinh_2_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "peinh_2_decimal_5_0")
  val infnr_string: ColumnWithName = ColumnWithName(prefix + "infnr_string")
  val kzech_string: ColumnWithName = ColumnWithName(prefix + "kzech_string")
  val kzmpf_string: ColumnWithName = ColumnWithName(prefix + "kzmpf_string")
  val stlal_string: ColumnWithName = ColumnWithName(prefix + "stlal_string")
  val pbdnr_string: ColumnWithName = ColumnWithName(prefix + "pbdnr_string")
  val stvkn_string: ColumnWithName = ColumnWithName(prefix + "stvkn_string")
  val ktoma_string: ColumnWithName = ColumnWithName(prefix + "ktoma_string")
  val vrpla_string: ColumnWithName = ColumnWithName(prefix + "vrpla_string")
  val kzbws_string: ColumnWithName = ColumnWithName(prefix + "kzbws_string")
  val nlfzv_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "nlfzv_decimal_3_0")
  val nlfmv_string: ColumnWithName = ColumnWithName(prefix + "nlfmv_string")
  val techs_string: ColumnWithName = ColumnWithName(prefix + "techs_string")
  val objtype_string: ColumnWithName = ColumnWithName(prefix + "objtype_string")
  val ch_proc_string: ColumnWithName = ColumnWithName(prefix + "ch_proc_string")
  val fxpru_string: ColumnWithName = ColumnWithName(prefix + "fxpru_string")
  val umsok_string: ColumnWithName = ColumnWithName(prefix + "umsok_string")
  val vorab_sm_string: ColumnWithName = ColumnWithName(prefix + "vorab_sm_string")
  val fipos_string: ColumnWithName = ColumnWithName(prefix + "fipos_string")
  val fipex_string: ColumnWithName = ColumnWithName(prefix + "fipex_string")
  val fistl_string: ColumnWithName = ColumnWithName(prefix + "fistl_string")
  val geber_string: ColumnWithName = ColumnWithName(prefix + "geber_string")
  val grant_nbr_string: ColumnWithName = ColumnWithName(prefix + "grant_nbr_string")
  val fkber_string: ColumnWithName = ColumnWithName(prefix + "fkber_string")
  val prio_urg_string: ColumnWithName = ColumnWithName(prefix + "prio_urg_string")
  val prio_req_string: ColumnWithName = ColumnWithName(prefix + "prio_req_string")
  val kblnr_string: ColumnWithName = ColumnWithName(prefix + "kblnr_string")
  val kblpos_string: ColumnWithName = ColumnWithName(prefix + "kblpos_string")
  val budget_pd_string: ColumnWithName = ColumnWithName(prefix + "budget_pd_string")
  val sc_object_id_string: ColumnWithName = ColumnWithName(prefix + "sc_object_id_string")
  val sc_itm_no_int: ColumnWithName = ColumnWithName(prefix + "sc_itm_no_int")
  val sgt_scat_string: ColumnWithName = ColumnWithName(prefix + "sgt_scat_string")
  val sgt_rcat_string: ColumnWithName = ColumnWithName(prefix + "sgt_rcat_string")
  val fmfgus_key_string: ColumnWithName = ColumnWithName(prefix + "fmfgus_key_string")
  val advcode_string: ColumnWithName = ColumnWithName(prefix + "advcode_string")
  val struc_code_string: ColumnWithName = ColumnWithName(prefix + "struc_code_string")
  val struc_class_string: ColumnWithName = ColumnWithName(prefix + "struc_class_string")
  val struc_classtyp_string: ColumnWithName = ColumnWithName(prefix + "struc_classtyp_string")
  val fsh_ralloc_qty_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "fsh_ralloc_qty_decimal_13_3")
  val fsh_critical_comp_string: ColumnWithName = ColumnWithName(prefix + "fsh_critical_comp_string")
  val fsh_critical_level_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "fsh_critical_level_decimal_2_0")
  val mill_ucdet_string: ColumnWithName = ColumnWithName(prefix + "mill_ucdet_string")
  val wty_ind_string: ColumnWithName = ColumnWithName(prefix + "wty_ind_string")
  val r_part_indicator_string: ColumnWithName = ColumnWithName(prefix + "r_part_indicator_string")
  val wtysc_clmitem_string: ColumnWithName = ColumnWithName(prefix + "wtysc_clmitem_string")
}

object C_resb extends C_resb("") {
  def as(alias: String): C_resb = new C_resb(alias + ".")
}

// AUTO GENERATED:END
