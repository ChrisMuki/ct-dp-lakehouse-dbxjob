// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.catalog.sr_raw.ct_gbl_psp

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
    itmid_string: String,
    sortf_string: String,
    @Decimal(13, 3) romen_decimal_13_3: BigDecimal,
    kstpz_string: String,
    kzclb_string: String,
    annam_string: String,
    alpos_string: String,
    clszu_string: String,
    clobk_string: String,
    idnrk_string: String,
    romei_string: String,
    potx2_string: String,
    fmeng_string: String,
    schgt_string: String,
    clmul_string: String,
    guidx_binary: Array[Byte],
    dvdat_string: String,
    rekrs_string: String,
    @Decimal(3, 0) ewahr_decimal_3_0: BigDecimal,
    @Decimal(13, 3) _sapmp_rund_fakt_decimal_13_3: BigDecimal,
    stlty_string: String,
    sanvs_string: String,
    sanin_string: String,
    nfeag_string: String,
    idpos_string: String,
    @Decimal(13, 3) roanz_decimal_13_3: BigDecimal,
    vcekz_string: String,
    @Decimal(3, 0) nlfzt_decimal_3_0: BigDecimal,
    _sapmp_abl_zahl_string: String,
    potx1_string: String,
    dvnam_string: String,
    prvbe_string: String,
    alpgr_string: String,
    stvkn_versn_string: String,
    guid_string: String,
    netau_string: String,
    kndbz_string: String,
    doknr_string: String,
    @Decimal(5, 0) peinh_decimal_5_0: BigDecimal,
    postp_string: String,
    kzkup_string: String,
    alekz_string: String,
    sanko_string: String,
    clalt_string: String,
    datuv_string: String,
    sanka_string: String,
    stlnr_string: String,
    vgpzl_string: String,
    pswrk_string: String,
    andat_string: String,
    fsh_pgqr_string: String,
    idvar_string: String,
    alprf_string: String,
    rekri_string: String,
    vstkz_string: String,
    @Decimal(13, 3) _sapmp_fix_as_l_decimal_13_3: BigDecimal,
    @Decimal(13, 3) roms3_decimal_13_3: BigDecimal,
    class_string: String,
    nfmat_string: String,
    @Decimal(13, 3) _sapmp_fix_as_e_decimal_13_3: BigDecimal,
    nfgrp_string: String,
    lgort_string: String,
    verti_string: String,
    kstnr_string: String,
    stvkn_string: String,
    stkkz_string: String,
    itsob_string: String,
    fsh_vmkz_string: String,
    kstty_string: String,
    lkenz_string: String,
    vackz_string: String,
    mandt_string: String,
    @Decimal(13, 3) _sapmp_fix_as_j_decimal_13_3: BigDecimal,
    dokar_string: String,
    tpekz_string: String,
    ablad_string: String,
    vgknt_string: String,
    dummy_stpo_incl_eew_ps_string: String,
    ekgrp_string: String,
    @Decimal(5, 2) csstr_decimal_5_2: BigDecimal,
    erskz_string: String,
    klart_string: String,
    ecn_to_rkey_string: String,
    inskz_string: String,
    nlfmv_string: String,
    awakz_string: String,
    kznfp_string: String,
    knobj_string: String,
    @Decimal(13, 3) roms2_decimal_13_3: BigDecimal,
    valkz_string: String,
    wempf_string: String,
    potpr_string: String,
    kstkn_string: String,
    aehlp_string: String,
    @Decimal(13, 3) menge_decimal_13_3: BigDecimal,
    meins_string: String,
    @Decimal(3, 0) nlfzv_decimal_3_0: BigDecimal,
    @Decimal(5, 2) ausch_decimal_5_2: BigDecimal,
    ekorg_string: String,
    fsh_critical_comp_string: String,
    sakto_string: String,
    alpst_string: String,
    stlkn_string: String,
    fsh_pgqrrf_string: String,
    rform_string: String,
    idhis_string: String,
    intrm_string: String,
    doktl_string: String,
    @Decimal(11, 2) preis_decimal_11_2: BigDecimal,
    valid_to_string: String,
    rfpnt_string: String,
    @Decimal(3, 0) lifzt_decimal_3_0: BigDecimal,
    aenam_string: String,
    ecn_to_string: String,
    @Decimal(2, 0) fsh_critical_level_decimal_2_0: BigDecimal,
    aedat_string: String,
    @Decimal(21, 7) lastchangedatetime_decimal_21_7: BigDecimal,
    sanfe_string: String,
    @Decimal(13, 3) _sapmp_max_fertl_decimal_13_3: BigDecimal,
    ltxsp_string: String,
    rvrel_string: String,
    stpoz_string: String,
    cview_string: String,
    valid_to_rkey_string: String,
    lifnr_string: String,
    sfwind_string: String,
    upskz_string: String,
    beikz_string: String,
    dspst_string: String,
    kndvb_string: String,
    objty_string: String,
    sgt_catv_string: String,
    cufactor_string: String,
    aennr_string: String,
    funcid_string: String,
    @Decimal(13, 3) roms1_decimal_13_3: BigDecimal,
    techv_string: String,
    @Decimal(3, 0) webaz_decimal_3_0: BigDecimal,
    sgt_cmkz_string: String,
    dokvr_string: String,
    _sapmp_met_lrch_string: String,
    matkl_string: String,
    @Decimal(5, 2) avoau_decimal_5_2: BigDecimal,
    posnr_string: String,
    waers_string: String,
    cadpo_string: String
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
  val itmid_string: ColumnWithName = ColumnWithName(prefix + "itmid_string")
  val sortf_string: ColumnWithName = ColumnWithName(prefix + "sortf_string")
  val romen_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "romen_decimal_13_3")
  val kstpz_string: ColumnWithName = ColumnWithName(prefix + "kstpz_string")
  val kzclb_string: ColumnWithName = ColumnWithName(prefix + "kzclb_string")
  val annam_string: ColumnWithName = ColumnWithName(prefix + "annam_string")
  val alpos_string: ColumnWithName = ColumnWithName(prefix + "alpos_string")
  val clszu_string: ColumnWithName = ColumnWithName(prefix + "clszu_string")
  val clobk_string: ColumnWithName = ColumnWithName(prefix + "clobk_string")
  val idnrk_string: ColumnWithName = ColumnWithName(prefix + "idnrk_string")
  val romei_string: ColumnWithName = ColumnWithName(prefix + "romei_string")
  val potx2_string: ColumnWithName = ColumnWithName(prefix + "potx2_string")
  val fmeng_string: ColumnWithName = ColumnWithName(prefix + "fmeng_string")
  val schgt_string: ColumnWithName = ColumnWithName(prefix + "schgt_string")
  val clmul_string: ColumnWithName = ColumnWithName(prefix + "clmul_string")
  val guidx_binary: ColumnWithName = ColumnWithName(prefix + "guidx_binary")
  val dvdat_string: ColumnWithName = ColumnWithName(prefix + "dvdat_string")
  val rekrs_string: ColumnWithName = ColumnWithName(prefix + "rekrs_string")
  val ewahr_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "ewahr_decimal_3_0")
  val _sapmp_rund_fakt_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_rund_fakt_decimal_13_3")
  val stlty_string: ColumnWithName = ColumnWithName(prefix + "stlty_string")
  val sanvs_string: ColumnWithName = ColumnWithName(prefix + "sanvs_string")
  val sanin_string: ColumnWithName = ColumnWithName(prefix + "sanin_string")
  val nfeag_string: ColumnWithName = ColumnWithName(prefix + "nfeag_string")
  val idpos_string: ColumnWithName = ColumnWithName(prefix + "idpos_string")
  val roanz_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roanz_decimal_13_3")
  val vcekz_string: ColumnWithName = ColumnWithName(prefix + "vcekz_string")
  val nlfzt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "nlfzt_decimal_3_0")
  val _sapmp_abl_zahl_string: ColumnWithName = ColumnWithName(prefix + "_sapmp_abl_zahl_string")
  val potx1_string: ColumnWithName = ColumnWithName(prefix + "potx1_string")
  val dvnam_string: ColumnWithName = ColumnWithName(prefix + "dvnam_string")
  val prvbe_string: ColumnWithName = ColumnWithName(prefix + "prvbe_string")
  val alpgr_string: ColumnWithName = ColumnWithName(prefix + "alpgr_string")
  val stvkn_versn_string: ColumnWithName = ColumnWithName(prefix + "stvkn_versn_string")
  val guid_string: ColumnWithName = ColumnWithName(prefix + "guid_string")
  val netau_string: ColumnWithName = ColumnWithName(prefix + "netau_string")
  val kndbz_string: ColumnWithName = ColumnWithName(prefix + "kndbz_string")
  val doknr_string: ColumnWithName = ColumnWithName(prefix + "doknr_string")
  val peinh_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "peinh_decimal_5_0")
  val postp_string: ColumnWithName = ColumnWithName(prefix + "postp_string")
  val kzkup_string: ColumnWithName = ColumnWithName(prefix + "kzkup_string")
  val alekz_string: ColumnWithName = ColumnWithName(prefix + "alekz_string")
  val sanko_string: ColumnWithName = ColumnWithName(prefix + "sanko_string")
  val clalt_string: ColumnWithName = ColumnWithName(prefix + "clalt_string")
  val datuv_string: ColumnWithName = ColumnWithName(prefix + "datuv_string")
  val sanka_string: ColumnWithName = ColumnWithName(prefix + "sanka_string")
  val stlnr_string: ColumnWithName = ColumnWithName(prefix + "stlnr_string")
  val vgpzl_string: ColumnWithName = ColumnWithName(prefix + "vgpzl_string")
  val pswrk_string: ColumnWithName = ColumnWithName(prefix + "pswrk_string")
  val andat_string: ColumnWithName = ColumnWithName(prefix + "andat_string")
  val fsh_pgqr_string: ColumnWithName = ColumnWithName(prefix + "fsh_pgqr_string")
  val idvar_string: ColumnWithName = ColumnWithName(prefix + "idvar_string")
  val alprf_string: ColumnWithName = ColumnWithName(prefix + "alprf_string")
  val rekri_string: ColumnWithName = ColumnWithName(prefix + "rekri_string")
  val vstkz_string: ColumnWithName = ColumnWithName(prefix + "vstkz_string")
  val _sapmp_fix_as_l_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_fix_as_l_decimal_13_3")
  val roms3_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roms3_decimal_13_3")
  val class_string: ColumnWithName = ColumnWithName(prefix + "class_string")
  val nfmat_string: ColumnWithName = ColumnWithName(prefix + "nfmat_string")
  val _sapmp_fix_as_e_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_fix_as_e_decimal_13_3")
  val nfgrp_string: ColumnWithName = ColumnWithName(prefix + "nfgrp_string")
  val lgort_string: ColumnWithName = ColumnWithName(prefix + "lgort_string")
  val verti_string: ColumnWithName = ColumnWithName(prefix + "verti_string")
  val kstnr_string: ColumnWithName = ColumnWithName(prefix + "kstnr_string")
  val stvkn_string: ColumnWithName = ColumnWithName(prefix + "stvkn_string")
  val stkkz_string: ColumnWithName = ColumnWithName(prefix + "stkkz_string")
  val itsob_string: ColumnWithName = ColumnWithName(prefix + "itsob_string")
  val fsh_vmkz_string: ColumnWithName = ColumnWithName(prefix + "fsh_vmkz_string")
  val kstty_string: ColumnWithName = ColumnWithName(prefix + "kstty_string")
  val lkenz_string: ColumnWithName = ColumnWithName(prefix + "lkenz_string")
  val vackz_string: ColumnWithName = ColumnWithName(prefix + "vackz_string")
  val mandt_string: ColumnWithName = ColumnWithName(prefix + "mandt_string")
  val _sapmp_fix_as_j_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_fix_as_j_decimal_13_3")
  val dokar_string: ColumnWithName = ColumnWithName(prefix + "dokar_string")
  val tpekz_string: ColumnWithName = ColumnWithName(prefix + "tpekz_string")
  val ablad_string: ColumnWithName = ColumnWithName(prefix + "ablad_string")
  val vgknt_string: ColumnWithName = ColumnWithName(prefix + "vgknt_string")
  val dummy_stpo_incl_eew_ps_string: ColumnWithName = ColumnWithName(prefix + "dummy_stpo_incl_eew_ps_string")
  val ekgrp_string: ColumnWithName = ColumnWithName(prefix + "ekgrp_string")
  val csstr_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "csstr_decimal_5_2")
  val erskz_string: ColumnWithName = ColumnWithName(prefix + "erskz_string")
  val klart_string: ColumnWithName = ColumnWithName(prefix + "klart_string")
  val ecn_to_rkey_string: ColumnWithName = ColumnWithName(prefix + "ecn_to_rkey_string")
  val inskz_string: ColumnWithName = ColumnWithName(prefix + "inskz_string")
  val nlfmv_string: ColumnWithName = ColumnWithName(prefix + "nlfmv_string")
  val awakz_string: ColumnWithName = ColumnWithName(prefix + "awakz_string")
  val kznfp_string: ColumnWithName = ColumnWithName(prefix + "kznfp_string")
  val knobj_string: ColumnWithName = ColumnWithName(prefix + "knobj_string")
  val roms2_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roms2_decimal_13_3")
  val valkz_string: ColumnWithName = ColumnWithName(prefix + "valkz_string")
  val wempf_string: ColumnWithName = ColumnWithName(prefix + "wempf_string")
  val potpr_string: ColumnWithName = ColumnWithName(prefix + "potpr_string")
  val kstkn_string: ColumnWithName = ColumnWithName(prefix + "kstkn_string")
  val aehlp_string: ColumnWithName = ColumnWithName(prefix + "aehlp_string")
  val menge_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "menge_decimal_13_3")
  val meins_string: ColumnWithName = ColumnWithName(prefix + "meins_string")
  val nlfzv_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "nlfzv_decimal_3_0")
  val ausch_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "ausch_decimal_5_2")
  val ekorg_string: ColumnWithName = ColumnWithName(prefix + "ekorg_string")
  val fsh_critical_comp_string: ColumnWithName = ColumnWithName(prefix + "fsh_critical_comp_string")
  val sakto_string: ColumnWithName = ColumnWithName(prefix + "sakto_string")
  val alpst_string: ColumnWithName = ColumnWithName(prefix + "alpst_string")
  val stlkn_string: ColumnWithName = ColumnWithName(prefix + "stlkn_string")
  val fsh_pgqrrf_string: ColumnWithName = ColumnWithName(prefix + "fsh_pgqrrf_string")
  val rform_string: ColumnWithName = ColumnWithName(prefix + "rform_string")
  val idhis_string: ColumnWithName = ColumnWithName(prefix + "idhis_string")
  val intrm_string: ColumnWithName = ColumnWithName(prefix + "intrm_string")
  val doktl_string: ColumnWithName = ColumnWithName(prefix + "doktl_string")
  val preis_decimal_11_2: ColumnWithName = ColumnWithName(prefix + "preis_decimal_11_2")
  val valid_to_string: ColumnWithName = ColumnWithName(prefix + "valid_to_string")
  val rfpnt_string: ColumnWithName = ColumnWithName(prefix + "rfpnt_string")
  val lifzt_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "lifzt_decimal_3_0")
  val aenam_string: ColumnWithName = ColumnWithName(prefix + "aenam_string")
  val ecn_to_string: ColumnWithName = ColumnWithName(prefix + "ecn_to_string")
  val fsh_critical_level_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "fsh_critical_level_decimal_2_0")
  val aedat_string: ColumnWithName = ColumnWithName(prefix + "aedat_string")
  val lastchangedatetime_decimal_21_7: ColumnWithName = ColumnWithName(prefix + "lastchangedatetime_decimal_21_7")
  val sanfe_string: ColumnWithName = ColumnWithName(prefix + "sanfe_string")
  val _sapmp_max_fertl_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "_sapmp_max_fertl_decimal_13_3")
  val ltxsp_string: ColumnWithName = ColumnWithName(prefix + "ltxsp_string")
  val rvrel_string: ColumnWithName = ColumnWithName(prefix + "rvrel_string")
  val stpoz_string: ColumnWithName = ColumnWithName(prefix + "stpoz_string")
  val cview_string: ColumnWithName = ColumnWithName(prefix + "cview_string")
  val valid_to_rkey_string: ColumnWithName = ColumnWithName(prefix + "valid_to_rkey_string")
  val lifnr_string: ColumnWithName = ColumnWithName(prefix + "lifnr_string")
  val sfwind_string: ColumnWithName = ColumnWithName(prefix + "sfwind_string")
  val upskz_string: ColumnWithName = ColumnWithName(prefix + "upskz_string")
  val beikz_string: ColumnWithName = ColumnWithName(prefix + "beikz_string")
  val dspst_string: ColumnWithName = ColumnWithName(prefix + "dspst_string")
  val kndvb_string: ColumnWithName = ColumnWithName(prefix + "kndvb_string")
  val objty_string: ColumnWithName = ColumnWithName(prefix + "objty_string")
  val sgt_catv_string: ColumnWithName = ColumnWithName(prefix + "sgt_catv_string")
  val cufactor_string: ColumnWithName = ColumnWithName(prefix + "cufactor_string")
  val aennr_string: ColumnWithName = ColumnWithName(prefix + "aennr_string")
  val funcid_string: ColumnWithName = ColumnWithName(prefix + "funcid_string")
  val roms1_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "roms1_decimal_13_3")
  val techv_string: ColumnWithName = ColumnWithName(prefix + "techv_string")
  val webaz_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "webaz_decimal_3_0")
  val sgt_cmkz_string: ColumnWithName = ColumnWithName(prefix + "sgt_cmkz_string")
  val dokvr_string: ColumnWithName = ColumnWithName(prefix + "dokvr_string")
  val _sapmp_met_lrch_string: ColumnWithName = ColumnWithName(prefix + "_sapmp_met_lrch_string")
  val matkl_string: ColumnWithName = ColumnWithName(prefix + "matkl_string")
  val avoau_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "avoau_decimal_5_2")
  val posnr_string: ColumnWithName = ColumnWithName(prefix + "posnr_string")
  val waers_string: ColumnWithName = ColumnWithName(prefix + "waers_string")
  val cadpo_string: ColumnWithName = ColumnWithName(prefix + "cadpo_string")
}

object C_stpo extends C_stpo("") {
  def as(alias: String): C_stpo = new C_stpo(alias + ".")
}

// AUTO GENERATED:END
