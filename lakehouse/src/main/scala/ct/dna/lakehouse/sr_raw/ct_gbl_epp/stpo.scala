// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.sr_raw.ct_gbl_epp

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_stpo(
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
    stlty_string: String,
    stlnr_string: String,
    stlkn_string: String,
    stpoz_string: String,
    datuv_string: String,
    techv_string: String,
    aennr_string: String,
    lkenz_string: String,
    vgknt_string: String,
    vgpzl_string: String,
    andat_string: String,
    annam_string: String,
    aedat_string: String,
    aenam_string: String,
    idnrk_string: String,
    pswrk_string: String,
    postp_string: String,
    posnr_string: String,
    sortf_string: String,
    meins_string: String,
    @Decimal(13, 3) menge_decimal_13_3: BigDecimal,
    fmeng_string: String,
    @Decimal(5, 2) ausch_decimal_5_2: BigDecimal,
    @Decimal(5, 2) avoau_decimal_5_2: BigDecimal,
    netau_string: String,
    schgt_string: String,
    beikz_string: String,
    erskz_string: String,
    rvrel_string: String,
    sanfe_string: String,
    sanin_string: String,
    sanka_string: String,
    sanko_string: String,
    sanvs_string: String,
    stkkz_string: String,
    rekri_string: String,
    rekrs_string: String,
    cadpo_string: String,
    nfmat_string: String,
    @Decimal(3, 0) nlfzt_decimal_3_0: BigDecimal,
    verti_string: String,
    alpos_string: String,
    @Decimal(3, 0) ewahr_decimal_3_0: BigDecimal,
    ekgrp_string: String,
    @Decimal(3, 0) lifzt_decimal_3_0: BigDecimal,
    lifnr_string: String,
    @Decimal(11, 2) preis_decimal_11_2: BigDecimal,
    @Decimal(5, 0) peinh_decimal_5_0: BigDecimal,
    waers_string: String,
    sakto_string: String,
    @Decimal(13, 3) roanz_decimal_13_3: BigDecimal,
    @Decimal(13, 3) roms1_decimal_13_3: BigDecimal,
    @Decimal(13, 3) roms2_decimal_13_3: BigDecimal,
    @Decimal(13, 3) roms3_decimal_13_3: BigDecimal,
    romei_string: String,
    @Decimal(13, 3) romen_decimal_13_3: BigDecimal,
    rform_string: String,
    upskz_string: String,
    valkz_string: String,
    ltxsp_string: String,
    potx1_string: String,
    potx2_string: String,
    objty_string: String,
    matkl_string: String,
    @Decimal(3, 0) webaz_decimal_3_0: BigDecimal,
    dokar_string: String,
    doknr_string: String,
    dokvr_string: String,
    doktl_string: String,
    @Decimal(5, 2) csstr_decimal_5_2: BigDecimal,
    _class_string: String,
    klart_string: String,
    potpr_string: String,
    awakz_string: String,
    inskz_string: String,
    vcekz_string: String,
    vstkz_string: String,
    vackz_string: String,
    ekorg_string: String,
    clobk_string: String,
    clmul_string: String,
    clalt_string: String,
    cview_string: String,
    knobj_string: String,
    lgort_string: String,
    kzkup_string: String,
    intrm_string: String,
    tpekz_string: String,
    stvkn_string: String,
    dvdat_string: String,
    dvnam_string: String,
    dspst_string: String,
    alpst_string: String,
    alprf_string: String,
    alpgr_string: String,
    kznfp_string: String,
    nfgrp_string: String,
    nfeag_string: String,
    kndvb_string: String,
    kndbz_string: String,
    kstty_string: String,
    kstnr_string: String,
    kstkn_string: String,
    kstpz_string: String,
    clszu_string: String,
    kzclb_string: String,
    aehlp_string: String,
    prvbe_string: String,
    @Decimal(3, 0) nlfzv_decimal_3_0: BigDecimal,
    nlfmv_string: String,
    idpos_string: String,
    idhis_string: String,
    idvar_string: String,
    alekz_string: String,
    itmid_string: String,
    guid_string: String,
    itsob_string: String,
    rfpnt_string: String,
    guidx_binary: Array[Byte],
    sgt_cmkz_string: String,
    sgt_catv_string: String,
    valid_to_string: String,
    valid_to_rkey_string: String,
    ecn_to_string: String,
    ecn_to_rkey_string: String,
    ablad_string: String,
    wempf_string: String,
    cufactor_string: String,
    _sapmp_met_lrch_string: String,
    @Decimal(13, 3) _sapmp_max_fertl_decimal_13_3: BigDecimal,
    @Decimal(13, 3) _sapmp_fix_as_j_decimal_13_3: BigDecimal,
    @Decimal(13, 3) _sapmp_fix_as_e_decimal_13_3: BigDecimal,
    @Decimal(13, 3) _sapmp_fix_as_l_decimal_13_3: BigDecimal,
    _sapmp_abl_zahl_string: String,
    @Decimal(13, 3) _sapmp_rund_fakt_decimal_13_3: BigDecimal,
    fsh_vmkz_string: String,
    fsh_pgqr_string: String,
    fsh_pgqrrf_string: String,
    fsh_critical_comp_string: String,
    @Decimal(2, 0) fsh_critical_level_decimal_2_0: BigDecimal,
    funcid_string: String,
    ytyp_string: String,
    ysubg_string: String,
    @Decimal(7, 4) yga_decimal_7_4: BigDecimal,
    @Decimal(7, 4) ynga_decimal_7_4: BigDecimal,
    ygau_string: String,
    @Decimal(5, 0) yends_decimal_5_0: BigDecimal,
    @Decimal(7, 4) yang_decimal_7_4: BigDecimal,
    @Decimal(6, 4) ysg_decimal_6_4: BigDecimal,
    @Decimal(6, 4) yshr_decimal_6_4: BigDecimal,
    @Decimal(6, 4) ysnk_decimal_6_4: BigDecimal,
    @Decimal(7, 2) ywidth_decimal_7_2: BigDecimal,
    ywidum_string: String,
    yauto_string: String,
    ydeflt_string: String,
    @Decimal(10, 3) ydia_decimal_10_3: BigDecimal,
    ydiau_string: String,
    @Decimal(6, 3) ypitch_decimal_6_3: BigDecimal,
    ypitum_string: String,
    @Decimal(10, 4) ynumbr_decimal_10_4: BigDecimal,
    @Decimal(10, 3) yywgt_decimal_10_3: BigDecimal,
    @Decimal(6, 4) yshr1_decimal_6_4: BigDecimal,
    @Decimal(6, 4) ysnk1_decimal_6_4: BigDecimal,
    @Decimal(8, 2) ystrn_decimal_8_2: BigDecimal
) extends Entity

object stpo extends TableSpec[E_stpo](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_stpo(prefix: String) extends ColumnWithNameAccessor {
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
  val stlty_string: ColumnWithName = ColumnWithName(prefix + "stlty_string")
  val stlnr_string: ColumnWithName = ColumnWithName(prefix + "stlnr_string")
  val stlkn_string: ColumnWithName = ColumnWithName(prefix + "stlkn_string")
  val stpoz_string: ColumnWithName = ColumnWithName(prefix + "stpoz_string")
  val datuv_string: ColumnWithName = ColumnWithName(prefix + "datuv_string")
  val techv_string: ColumnWithName = ColumnWithName(prefix + "techv_string")
  val aennr_string: ColumnWithName = ColumnWithName(prefix + "aennr_string")
  val lkenz_string: ColumnWithName = ColumnWithName(prefix + "lkenz_string")
  val vgknt_string: ColumnWithName = ColumnWithName(prefix + "vgknt_string")
  val vgpzl_string: ColumnWithName = ColumnWithName(prefix + "vgpzl_string")
  val andat_string: ColumnWithName = ColumnWithName(prefix + "andat_string")
  val annam_string: ColumnWithName = ColumnWithName(prefix + "annam_string")
  val aedat_string: ColumnWithName = ColumnWithName(prefix + "aedat_string")
  val aenam_string: ColumnWithName = ColumnWithName(prefix + "aenam_string")
  val idnrk_string: ColumnWithName = ColumnWithName(prefix + "idnrk_string")
  val pswrk_string: ColumnWithName = ColumnWithName(prefix + "pswrk_string")
  val postp_string: ColumnWithName = ColumnWithName(prefix + "postp_string")
  val posnr_string: ColumnWithName = ColumnWithName(prefix + "posnr_string")
  val sortf_string: ColumnWithName = ColumnWithName(prefix + "sortf_string")
  val meins_string: ColumnWithName = ColumnWithName(prefix + "meins_string")
  val menge_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "menge_decimal_13_3")
  val fmeng_string: ColumnWithName = ColumnWithName(prefix + "fmeng_string")
  val ausch_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "ausch_decimal_5_2")
  val avoau_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "avoau_decimal_5_2")
  val netau_string: ColumnWithName = ColumnWithName(prefix + "netau_string")
  val schgt_string: ColumnWithName = ColumnWithName(prefix + "schgt_string")
  val beikz_string: ColumnWithName = ColumnWithName(prefix + "beikz_string")
  val erskz_string: ColumnWithName = ColumnWithName(prefix + "erskz_string")
  val rvrel_string: ColumnWithName = ColumnWithName(prefix + "rvrel_string")
  val sanfe_string: ColumnWithName = ColumnWithName(prefix + "sanfe_string")
  val sanin_string: ColumnWithName = ColumnWithName(prefix + "sanin_string")
  val sanka_string: ColumnWithName = ColumnWithName(prefix + "sanka_string")
  val sanko_string: ColumnWithName = ColumnWithName(prefix + "sanko_string")
  val sanvs_string: ColumnWithName = ColumnWithName(prefix + "sanvs_string")
  val stkkz_string: ColumnWithName = ColumnWithName(prefix + "stkkz_string")
  val rekri_string: ColumnWithName = ColumnWithName(prefix + "rekri_string")
  val rekrs_string: ColumnWithName = ColumnWithName(prefix + "rekrs_string")
  val cadpo_string: ColumnWithName = ColumnWithName(prefix + "cadpo_string")
  val nfmat_string: ColumnWithName = ColumnWithName(prefix + "nfmat_string")
  val nlfzt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "nlfzt_decimal_3_0")
  val verti_string: ColumnWithName = ColumnWithName(prefix + "verti_string")
  val alpos_string: ColumnWithName = ColumnWithName(prefix + "alpos_string")
  val ewahr_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "ewahr_decimal_3_0")
  val ekgrp_string: ColumnWithName = ColumnWithName(prefix + "ekgrp_string")
  val lifzt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "lifzt_decimal_3_0")
  val lifnr_string: ColumnWithName = ColumnWithName(prefix + "lifnr_string")
  val preis_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "preis_decimal_11_2")
  val peinh_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "peinh_decimal_5_0")
  val waers_string: ColumnWithName = ColumnWithName(prefix + "waers_string")
  val sakto_string: ColumnWithName = ColumnWithName(prefix + "sakto_string")
  val roanz_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roanz_decimal_13_3")
  val roms1_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roms1_decimal_13_3")
  val roms2_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roms2_decimal_13_3")
  val roms3_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roms3_decimal_13_3")
  val romei_string: ColumnWithName = ColumnWithName(prefix + "romei_string")
  val romen_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "romen_decimal_13_3")
  val rform_string: ColumnWithName = ColumnWithName(prefix + "rform_string")
  val upskz_string: ColumnWithName = ColumnWithName(prefix + "upskz_string")
  val valkz_string: ColumnWithName = ColumnWithName(prefix + "valkz_string")
  val ltxsp_string: ColumnWithName = ColumnWithName(prefix + "ltxsp_string")
  val potx1_string: ColumnWithName = ColumnWithName(prefix + "potx1_string")
  val potx2_string: ColumnWithName = ColumnWithName(prefix + "potx2_string")
  val objty_string: ColumnWithName = ColumnWithName(prefix + "objty_string")
  val matkl_string: ColumnWithName = ColumnWithName(prefix + "matkl_string")
  val webaz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "webaz_decimal_3_0")
  val dokar_string: ColumnWithName = ColumnWithName(prefix + "dokar_string")
  val doknr_string: ColumnWithName = ColumnWithName(prefix + "doknr_string")
  val dokvr_string: ColumnWithName = ColumnWithName(prefix + "dokvr_string")
  val doktl_string: ColumnWithName = ColumnWithName(prefix + "doktl_string")
  val csstr_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "csstr_decimal_5_2")
  val _class_string: ColumnWithName = ColumnWithName(prefix + "_class_string")
  val klart_string: ColumnWithName = ColumnWithName(prefix + "klart_string")
  val potpr_string: ColumnWithName = ColumnWithName(prefix + "potpr_string")
  val awakz_string: ColumnWithName = ColumnWithName(prefix + "awakz_string")
  val inskz_string: ColumnWithName = ColumnWithName(prefix + "inskz_string")
  val vcekz_string: ColumnWithName = ColumnWithName(prefix + "vcekz_string")
  val vstkz_string: ColumnWithName = ColumnWithName(prefix + "vstkz_string")
  val vackz_string: ColumnWithName = ColumnWithName(prefix + "vackz_string")
  val ekorg_string: ColumnWithName = ColumnWithName(prefix + "ekorg_string")
  val clobk_string: ColumnWithName = ColumnWithName(prefix + "clobk_string")
  val clmul_string: ColumnWithName = ColumnWithName(prefix + "clmul_string")
  val clalt_string: ColumnWithName = ColumnWithName(prefix + "clalt_string")
  val cview_string: ColumnWithName = ColumnWithName(prefix + "cview_string")
  val knobj_string: ColumnWithName = ColumnWithName(prefix + "knobj_string")
  val lgort_string: ColumnWithName = ColumnWithName(prefix + "lgort_string")
  val kzkup_string: ColumnWithName = ColumnWithName(prefix + "kzkup_string")
  val intrm_string: ColumnWithName = ColumnWithName(prefix + "intrm_string")
  val tpekz_string: ColumnWithName = ColumnWithName(prefix + "tpekz_string")
  val stvkn_string: ColumnWithName = ColumnWithName(prefix + "stvkn_string")
  val dvdat_string: ColumnWithName = ColumnWithName(prefix + "dvdat_string")
  val dvnam_string: ColumnWithName = ColumnWithName(prefix + "dvnam_string")
  val dspst_string: ColumnWithName = ColumnWithName(prefix + "dspst_string")
  val alpst_string: ColumnWithName = ColumnWithName(prefix + "alpst_string")
  val alprf_string: ColumnWithName = ColumnWithName(prefix + "alprf_string")
  val alpgr_string: ColumnWithName = ColumnWithName(prefix + "alpgr_string")
  val kznfp_string: ColumnWithName = ColumnWithName(prefix + "kznfp_string")
  val nfgrp_string: ColumnWithName = ColumnWithName(prefix + "nfgrp_string")
  val nfeag_string: ColumnWithName = ColumnWithName(prefix + "nfeag_string")
  val kndvb_string: ColumnWithName = ColumnWithName(prefix + "kndvb_string")
  val kndbz_string: ColumnWithName = ColumnWithName(prefix + "kndbz_string")
  val kstty_string: ColumnWithName = ColumnWithName(prefix + "kstty_string")
  val kstnr_string: ColumnWithName = ColumnWithName(prefix + "kstnr_string")
  val kstkn_string: ColumnWithName = ColumnWithName(prefix + "kstkn_string")
  val kstpz_string: ColumnWithName = ColumnWithName(prefix + "kstpz_string")
  val clszu_string: ColumnWithName = ColumnWithName(prefix + "clszu_string")
  val kzclb_string: ColumnWithName = ColumnWithName(prefix + "kzclb_string")
  val aehlp_string: ColumnWithName = ColumnWithName(prefix + "aehlp_string")
  val prvbe_string: ColumnWithName = ColumnWithName(prefix + "prvbe_string")
  val nlfzv_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "nlfzv_decimal_3_0")
  val nlfmv_string: ColumnWithName = ColumnWithName(prefix + "nlfmv_string")
  val idpos_string: ColumnWithName = ColumnWithName(prefix + "idpos_string")
  val idhis_string: ColumnWithName = ColumnWithName(prefix + "idhis_string")
  val idvar_string: ColumnWithName = ColumnWithName(prefix + "idvar_string")
  val alekz_string: ColumnWithName = ColumnWithName(prefix + "alekz_string")
  val itmid_string: ColumnWithName = ColumnWithName(prefix + "itmid_string")
  val guid_string: ColumnWithName = ColumnWithName(prefix + "guid_string")
  val itsob_string: ColumnWithName = ColumnWithName(prefix + "itsob_string")
  val rfpnt_string: ColumnWithName = ColumnWithName(prefix + "rfpnt_string")
  val guidx_binary: ColumnWithName = ColumnWithName(prefix + "guidx_binary")
  val sgt_cmkz_string: ColumnWithName = ColumnWithName(prefix + "sgt_cmkz_string")
  val sgt_catv_string: ColumnWithName = ColumnWithName(prefix + "sgt_catv_string")
  val valid_to_string: ColumnWithName = ColumnWithName(prefix + "valid_to_string")
  val valid_to_rkey_string: ColumnWithName = ColumnWithName(prefix + "valid_to_rkey_string")
  val ecn_to_string: ColumnWithName = ColumnWithName(prefix + "ecn_to_string")
  val ecn_to_rkey_string: ColumnWithName = ColumnWithName(prefix + "ecn_to_rkey_string")
  val ablad_string: ColumnWithName = ColumnWithName(prefix + "ablad_string")
  val wempf_string: ColumnWithName = ColumnWithName(prefix + "wempf_string")
  val cufactor_string: ColumnWithName = ColumnWithName(prefix + "cufactor_string")
  val _sapmp_met_lrch_string: ColumnWithName = ColumnWithName(prefix + "_sapmp_met_lrch_string")
  val _sapmp_max_fertl_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_max_fertl_decimal_13_3")
  val _sapmp_fix_as_j_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_fix_as_j_decimal_13_3")
  val _sapmp_fix_as_e_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_fix_as_e_decimal_13_3")
  val _sapmp_fix_as_l_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_fix_as_l_decimal_13_3")
  val _sapmp_abl_zahl_string: ColumnWithName = ColumnWithName(prefix + "_sapmp_abl_zahl_string")
  val _sapmp_rund_fakt_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_rund_fakt_decimal_13_3")
  val fsh_vmkz_string: ColumnWithName = ColumnWithName(prefix + "fsh_vmkz_string")
  val fsh_pgqr_string: ColumnWithName = ColumnWithName(prefix + "fsh_pgqr_string")
  val fsh_pgqrrf_string: ColumnWithName = ColumnWithName(prefix + "fsh_pgqrrf_string")
  val fsh_critical_comp_string: ColumnWithName = ColumnWithName(prefix + "fsh_critical_comp_string")
  val fsh_critical_level_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "fsh_critical_level_decimal_2_0")
  val funcid_string: ColumnWithName = ColumnWithName(prefix + "funcid_string")
  val ytyp_string: ColumnWithName = ColumnWithName(prefix + "ytyp_string")
  val ysubg_string: ColumnWithName = ColumnWithName(prefix + "ysubg_string")
  val yga_decimal_7_4: ColumnWithName = ColumnWithName(prefix + "yga_decimal_7_4")
  val ynga_decimal_7_4: ColumnWithName = ColumnWithName(prefix + "ynga_decimal_7_4")
  val ygau_string: ColumnWithName = ColumnWithName(prefix + "ygau_string")
  val yends_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "yends_decimal_5_0")
  val yang_decimal_7_4: ColumnWithName = ColumnWithName(prefix + "yang_decimal_7_4")
  val ysg_decimal_6_4: ColumnWithName = ColumnWithName(prefix + "ysg_decimal_6_4")
  val yshr_decimal_6_4: ColumnWithName = ColumnWithName(prefix + "yshr_decimal_6_4")
  val ysnk_decimal_6_4: ColumnWithName = ColumnWithName(prefix + "ysnk_decimal_6_4")
  val ywidth_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "ywidth_decimal_7_2")
  val ywidum_string: ColumnWithName = ColumnWithName(prefix + "ywidum_string")
  val yauto_string: ColumnWithName = ColumnWithName(prefix + "yauto_string")
  val ydeflt_string: ColumnWithName = ColumnWithName(prefix + "ydeflt_string")
  val ydia_decimal_10_3: ColumnWithName = ColumnWithName(prefix + "ydia_decimal_10_3")
  val ydiau_string: ColumnWithName = ColumnWithName(prefix + "ydiau_string")
  val ypitch_decimal_6_3: ColumnWithName = ColumnWithName(prefix + "ypitch_decimal_6_3")
  val ypitum_string: ColumnWithName = ColumnWithName(prefix + "ypitum_string")
  val ynumbr_decimal_10_4: ColumnWithName = ColumnWithName(prefix + "ynumbr_decimal_10_4")
  val yywgt_decimal_10_3: ColumnWithName = ColumnWithName(prefix + "yywgt_decimal_10_3")
  val yshr1_decimal_6_4: ColumnWithName = ColumnWithName(prefix + "yshr1_decimal_6_4")
  val ysnk1_decimal_6_4: ColumnWithName = ColumnWithName(prefix + "ysnk1_decimal_6_4")
  val ystrn_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "ystrn_decimal_8_2")
}

object C_stpo extends C_stpo("") {
  def as(alias: String): C_stpo = new C_stpo(alias + ".")
}

// AUTO GENERATED:END
