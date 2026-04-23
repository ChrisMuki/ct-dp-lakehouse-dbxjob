// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.sr_raw.ct_gbl_p24

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_zppkopf_part1(
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
    matnr_string: String,
    werks_string: String,
    stlan_string: String,
    entw_werk_string: String,
    fertigung_string: String,
    makt_string: String,
    change_string: String,
    takeme_string: String,
    status_string: String,
    hersabteil_string: String,
    orgsystem_string: String,
    fides_change_no_string: String,
    fides_stlnr_string: String,
    fides_stl_aedat_string: String,
    fides_stl_aestat_string: String,
    fides_stl_aedate_string: String,
    fides_stl_aetime_string: String,
    fides_class_chg_string: String,
    aeersatz_string: String,
    aegrund_string: String,
    aegrund2_string: String,
    entwgrund_string: String,
    entwickler_string: String,
    bearb_pe_string: String,
    entwickler_stort_string: String,
    entwvor_string: String,
    entwdat_string: String,
    kennsdok_string: String,
    @Decimal(4, 2) faddm_decimal_4_2: BigDecimal,
    anzspul_string: String,
    vorvers_string: String,
    @Decimal(5, 2) vspprap_decimal_5_2: BigDecimal,
    vspprap_tm_string: String,
    vspprap_tn_string: String,
    vspprap_ib_string: String,
    vspprap_ic_string: String,
    vspprap_check_string: String,
    @Decimal(5, 2) vsproh_decimal_5_2: BigDecimal,
    vsproh_tm_string: String,
    vsproh_tn_string: String,
    vsproh_ib_string: String,
    vsproh_ic_string: String,
    vsproh_check_string: String,
    @Decimal(5, 2) lengewdin_decimal_5_2: BigDecimal,
    makeapl_string: String,
    @Decimal(3, 1) reisde_decimal_3_1: BigDecimal,
    @Decimal(7, 2) reiskr_decimal_7_2: BigDecimal,
    @Decimal(3, 1) spgew_decimal_3_1: BigDecimal,
    stdate_string: String,
    stdate2_string: String,
    stbemerk_string: String,
    stmenge_string: String,
    stmeins_string: String,
    @Decimal(5, 2) lenfak_decimal_5_2: BigDecimal,
    @Decimal(6, 2) lengew_decimal_6_2: BigDecimal,
    maschnum_string: String,
    fadanz_string: String,
    spver_string: String,
    ident_string: String,
    anddatum_string: String,
    veranlsr_string: String,
    fbl_int: BoxedInt,
    @Decimal(4, 0) biegrad_decimal_4_0: BigDecimal,
    biegradpm_string: String,
    @Decimal(4, 2) wanddiff_decimal_4_2: BigDecimal,
    wanddiffpm_string: String,
    @Decimal(7, 3) bdruk_decimal_7_3: BigDecimal,
    bdrukpm_string: String,
    @Decimal(7, 3) pdruk_decimal_7_3: BigDecimal,
    pdrukpm_string: String,
    @Decimal(7, 3) sbdruk_decimal_7_3: BigDecimal,
    sbdrukpm_string: String,
    @Decimal(4, 2) znudruk_decimal_4_2: BigDecimal,
    znudrukpm_string: String,
    @Decimal(9, 3) tbdruk_decimal_9_3: BigDecimal,
    @Decimal(12, 3) volume_decimal_12_3: BigDecimal,
    @Decimal(12, 3) gewicht_decimal_12_3: BigDecimal,
    gewichtpm_string: String,
    dorn_string: String,
    dornmakt_string: String,
    @Decimal(2, 0) dornumlauf_decimal_2_0: BigDecimal,
    label_bom_string: String,
    speznr_string: String,
    spez_werk_string: String,
    slbnr_string: String,
    prodort_string: String,
    zsbteil_string: String,
    @Decimal(5, 2) indm1_decimal_5_2: BigDecimal,
    @Decimal(5, 2) indm2_decimal_5_2: BigDecimal,
    anzahl_string: String,
    @Decimal(7, 3) frgew_decimal_7_3: BigDecimal,
    frgewpm_string: String,
    @Decimal(7, 3) rlgew_decimal_7_3: BigDecimal,
    @Decimal(5, 1) drukprz_decimal_5_1: BigDecimal,
    @Decimal(5, 1) drukprb_decimal_5_1: BigDecimal,
    drukprbpm_string: String,
    cadzvom_string: String,
    cadzbis_string: String,
    krkzvom_string: String,
    krkzbis_string: String,
    zsbzvom_string: String,
    zsbzbis_string: String,
    prick_string: String,
    krart_string: String,
    krdorn_string: String,
    krdim_string: String,
    krbog_string: String,
    bogdorn_string: String,
    sndart_string: String,
    sndausf_string: String,
    @Decimal(5, 2) dorndm_decimal_5_2: BigDecimal,
    @Decimal(5, 2) dmauf1_decimal_5_2: BigDecimal,
    @Decimal(5, 2) dmauf2_decimal_5_2: BigDecimal,
    @Decimal(5, 2) vfabfall_decimal_5_2: BigDecimal,
    langfix_string: String,
    admfix_string: String,
    endoskop_string: String,
    sgpruf_string: String,
    rarkont_string: String,
    mtart_string: String,
    bgart_string: String,
    stufkz_string: String,
    meins_string: String,
    weitabteil_string: String,
    pkz_string: String,
    zondfp_string: String,
    artnr_string: String,
    art_werk_string: String,
    krumnr_string: String,
    krum_werk_string: String,
    endbeg_string: String,
    @Decimal(12, 3) effvol_decimal_12_3: BigDecimal,
    @Decimal(12, 3) diffvol_decimal_12_3: BigDecimal,
    baukette_string: String,
    kurtxt_string: String,
    kurtxt24_string: String,
    fertig_string: String,
    sbdrukopt_string: String,
    artmode_string: String,
    speed_string: String,
    vkdat_string: String,
    newkz_string: String,
    newkteil_string: String,
    newkonf_string: String,
    newapl_string: String,
    pemsyprj_string: String,
    wrkver_string: String,
    verf1_string: String,
    farbe1_string: String,
    pos1_string: String,
    text1_string: String,
    verf2_string: String,
    farbe2_string: String,
    pos2_string: String,
    text2_string: String,
    verf3_string: String,
    farbe3_string: String,
    pos3_string: String,
    text3_string: String,
    verf4_string: String,
    farbe4_string: String,
    pos4_string: String,
    text4_string: String,
    verf5_string: String,
    farbe5_string: String,
    pos5_string: String,
    text5_string: String,
    herstellverfahr_string: String,
    gewicht_neu_wert_string: String,
    ststempel_string: String,
    anfnr_string: String,
    version_string: String,
    zt_apl_erstell_string: String,
    zt_art_string: String,
    @Decimal(5, 0) zt_laenge_ges_decimal_5_0: BigDecimal,
    zt_aufziehen_art_string: String,
    zt_aufziehen_aus_string: String,
    @Decimal(5, 0) zt_aufziehen_lae_decimal_5_0: BigDecimal,
    zt_biegen_art_string: String,
    @Decimal(2, 0) zt_biegen_anz_bo_decimal_2_0: BigDecimal,
    @Decimal(5, 0) zt_biegen_laenge_decimal_5_0: BigDecimal,
    zt_montage_art_string: String,
    @Decimal(2, 0) zt_montage_baute_decimal_2_0: BigDecimal,
    @Decimal(2, 0) zt_montage_vorga_decimal_2_0: BigDecimal,
    @Decimal(2, 0) zt_montage_handl_decimal_2_0: BigDecimal,
    zt_armieren_art_string: String,
    @Decimal(2, 0) zt_armieren_anz_decimal_2_0: BigDecimal,
    @Decimal(5, 0) zt_armieren_laen_decimal_5_0: BigDecimal,
    zt_loeten_art_string: String,
    @Decimal(2, 0) zt_loeten_anzahl_decimal_2_0: BigDecimal,
    @Decimal(5, 0) zt_loeten_laenge_decimal_5_0: BigDecimal,
    zt_schneiden_art_string: String,
    @Decimal(2, 0) zt_schneiden_anz_decimal_2_0: BigDecimal,
    @Decimal(5, 0) zt_schneiden_lae_decimal_5_0: BigDecimal,
    zt_druckpr_art_string: String,
    zt_druckpr_ausf_string: String,
    @Decimal(5, 0) zt_druckpr_lae_decimal_5_0: BigDecimal,
    zt_stauchen_art_string: String,
    @Decimal(2, 0) zt_stauchen_anz_decimal_2_0: BigDecimal,
    @Decimal(5, 0) zt_stauchen_lae_decimal_5_0: BigDecimal,
    benslb_string: String,
    identslb_string: String,
    @Decimal(5, 2) zt_innendm_decimal_5_2: BigDecimal,
    begru_string: String,
    @Decimal(4, 0) fp_laufzeit_spul_decimal_4_0: BigDecimal,
    lagerware_string: String,
    cross_media_string: String,
    temperkey_string: String,
    @Decimal(5, 2) audm1_decimal_5_2: BigDecimal,
    @Decimal(5, 2) audm2_decimal_5_2: BigDecimal,
    kst_roart_string: String,
    kst_wkz_string: String,
    kst_bogen_string: String,
    kst_snart_string: String,
    kst_stutz_string: String,
    tfx_verfahren_string: String,
    @Decimal(4, 1) tfx_druck_decimal_4_1: BigDecimal,
    tfx_zeit_string: String,
    tfx_temp_string: String,
    tfx_text_string: String,
    lfix_huelse_string: String,
    lfix_kappe_string: String,
    lfix_trennsch_string: String,
    lfix_kreismsr_string: String,
    @Decimal(5, 2) extr_geschw_decimal_5_2: BigDecimal,
    rohlaufbau_string: String,
    @Decimal(2, 0) anzkaliber_decimal_2_0: BigDecimal,
    @Decimal(4, 0) krdornlen_decimal_4_0: BigDecimal,
    ausmi_string: String,
    ausro_string: String,
    @Decimal(3, 0) mindestmenge_decimal_3_0: BigDecimal,
    @Decimal(3, 0) kavitaeten_decimal_3_0: BigDecimal,
    @Decimal(3, 0) einlegeteile_decimal_3_0: BigDecimal,
    @Decimal(3, 0) spindeln_decimal_3_0: BigDecimal,
    @Decimal(3, 0) bohren_decimal_3_0: BigDecimal,
    @Decimal(3, 0) saegen_decimal_3_0: BigDecimal,
    @Decimal(3, 0) huelsen_decimal_3_0: BigDecimal,
    @Decimal(3, 0) schneidenzw_decimal_3_0: BigDecimal,
    @Decimal(5, 2) abkuehlenwz_decimal_5_2: BigDecimal,
    @Decimal(5, 2) dichtpruefung_decimal_5_2: BigDecimal,
    @Decimal(5, 2) schweissens_decimal_5_2: BigDecimal
) extends Entity

case class E_zppkopf_part2(
    @PK _mk_org: String,
    @Decimal(5, 2) schweissenc_decimal_5_2: BigDecimal,
    @Decimal(5, 2) schweissenir_decimal_5_2: BigDecimal,
    @Decimal(4, 0) befuellakk_decimal_4_0: BigDecimal,
    @Decimal(4, 0) einsaugen_decimal_4_0: BigDecimal,
    @Decimal(4, 0) aufblasen_decimal_4_0: BigDecimal,
    @Decimal(4, 0) abkuehlen_decimal_4_0: BigDecimal,
    @Decimal(4, 0) schneiden_decimal_4_0: BigDecimal,
    @Decimal(4, 0) entnahme_decimal_4_0: BigDecimal,
    @Decimal(4, 0) bestueckung_decimal_4_0: BigDecimal,
    @Decimal(5, 2) kuehlfixi_decimal_5_2: BigDecimal,
    @Decimal(7, 2) zyklus_decimal_7_2: BigDecimal,
    @Decimal(3, 0) braende_decimal_3_0: BigDecimal,
    @Decimal(3, 0) teiletempern_decimal_3_0: BigDecimal,
    @Decimal(7, 2) lohnarbeit_decimal_7_2: BigDecimal,
    maschtyp_string: String,
    profil_string: String,
    blechtyp_string: String,
    @Decimal(4, 0) stckgeb_decimal_4_0: BigDecimal,
    blechbem_string: String,
    kdmatnr_string: String,
    kdwerks_string: String,
    kdfirma_string: String,
    kennnok_string: String,
    aedatkenn_string: String,
    @Decimal(4, 0) anzvulkwz_decimal_4_0: BigDecimal,
    @Decimal(10, 2) laengeoh_decimal_10_2: BigDecimal,
    hwickel_v_string: String,
    hwickel_h_string: String
) extends Entity

object zppkopf extends TableSpec[Joined[E_zppkopf_part1, E_zppkopf_part2]](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_zppkopf(prefix: String) extends ColumnWithNameAccessor {
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
  val matnr_string: ColumnWithName = ColumnWithName(prefix + "matnr_string")
  val werks_string: ColumnWithName = ColumnWithName(prefix + "werks_string")
  val stlan_string: ColumnWithName = ColumnWithName(prefix + "stlan_string")
  val entw_werk_string: ColumnWithName = ColumnWithName(prefix + "entw_werk_string")
  val fertigung_string: ColumnWithName = ColumnWithName(prefix + "fertigung_string")
  val makt_string: ColumnWithName = ColumnWithName(prefix + "makt_string")
  val change_string: ColumnWithName = ColumnWithName(prefix + "change_string")
  val takeme_string: ColumnWithName = ColumnWithName(prefix + "takeme_string")
  val status_string: ColumnWithName = ColumnWithName(prefix + "status_string")
  val hersabteil_string: ColumnWithName = ColumnWithName(prefix + "hersabteil_string")
  val orgsystem_string: ColumnWithName = ColumnWithName(prefix + "orgsystem_string")
  val fides_change_no_string: ColumnWithName = ColumnWithName(prefix + "fides_change_no_string")
  val fides_stlnr_string: ColumnWithName = ColumnWithName(prefix + "fides_stlnr_string")
  val fides_stl_aedat_string: ColumnWithName = ColumnWithName(prefix + "fides_stl_aedat_string")
  val fides_stl_aestat_string: ColumnWithName = ColumnWithName(prefix + "fides_stl_aestat_string")
  val fides_stl_aedate_string: ColumnWithName = ColumnWithName(prefix + "fides_stl_aedate_string")
  val fides_stl_aetime_string: ColumnWithName = ColumnWithName(prefix + "fides_stl_aetime_string")
  val fides_class_chg_string: ColumnWithName = ColumnWithName(prefix + "fides_class_chg_string")
  val aeersatz_string: ColumnWithName = ColumnWithName(prefix + "aeersatz_string")
  val aegrund_string: ColumnWithName = ColumnWithName(prefix + "aegrund_string")
  val aegrund2_string: ColumnWithName = ColumnWithName(prefix + "aegrund2_string")
  val entwgrund_string: ColumnWithName = ColumnWithName(prefix + "entwgrund_string")
  val entwickler_string: ColumnWithName = ColumnWithName(prefix + "entwickler_string")
  val bearb_pe_string: ColumnWithName = ColumnWithName(prefix + "bearb_pe_string")
  val entwickler_stort_string: ColumnWithName = ColumnWithName(prefix + "entwickler_stort_string")
  val entwvor_string: ColumnWithName = ColumnWithName(prefix + "entwvor_string")
  val entwdat_string: ColumnWithName = ColumnWithName(prefix + "entwdat_string")
  val kennsdok_string: ColumnWithName = ColumnWithName(prefix + "kennsdok_string")
  val faddm_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "faddm_decimal_4_2")
  val anzspul_string: ColumnWithName = ColumnWithName(prefix + "anzspul_string")
  val vorvers_string: ColumnWithName = ColumnWithName(prefix + "vorvers_string")
  val vspprap_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "vspprap_decimal_5_2")
  val vspprap_tm_string: ColumnWithName = ColumnWithName(prefix + "vspprap_tm_string")
  val vspprap_tn_string: ColumnWithName = ColumnWithName(prefix + "vspprap_tn_string")
  val vspprap_ib_string: ColumnWithName = ColumnWithName(prefix + "vspprap_ib_string")
  val vspprap_ic_string: ColumnWithName = ColumnWithName(prefix + "vspprap_ic_string")
  val vspprap_check_string: ColumnWithName = ColumnWithName(prefix + "vspprap_check_string")
  val vsproh_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "vsproh_decimal_5_2")
  val vsproh_tm_string: ColumnWithName = ColumnWithName(prefix + "vsproh_tm_string")
  val vsproh_tn_string: ColumnWithName = ColumnWithName(prefix + "vsproh_tn_string")
  val vsproh_ib_string: ColumnWithName = ColumnWithName(prefix + "vsproh_ib_string")
  val vsproh_ic_string: ColumnWithName = ColumnWithName(prefix + "vsproh_ic_string")
  val vsproh_check_string: ColumnWithName = ColumnWithName(prefix + "vsproh_check_string")
  val lengewdin_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "lengewdin_decimal_5_2")
  val makeapl_string: ColumnWithName = ColumnWithName(prefix + "makeapl_string")
  val reisde_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "reisde_decimal_3_1")
  val reiskr_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "reiskr_decimal_7_2")
  val spgew_decimal_3_1: ColumnWithName = ColumnWithName(prefix + "spgew_decimal_3_1")
  val stdate_string: ColumnWithName = ColumnWithName(prefix + "stdate_string")
  val stdate2_string: ColumnWithName = ColumnWithName(prefix + "stdate2_string")
  val stbemerk_string: ColumnWithName = ColumnWithName(prefix + "stbemerk_string")
  val stmenge_string: ColumnWithName = ColumnWithName(prefix + "stmenge_string")
  val stmeins_string: ColumnWithName = ColumnWithName(prefix + "stmeins_string")
  val lenfak_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "lenfak_decimal_5_2")
  val lengew_decimal_6_2: ColumnWithName = ColumnWithName(prefix + "lengew_decimal_6_2")
  val maschnum_string: ColumnWithName = ColumnWithName(prefix + "maschnum_string")
  val fadanz_string: ColumnWithName = ColumnWithName(prefix + "fadanz_string")
  val spver_string: ColumnWithName = ColumnWithName(prefix + "spver_string")
  val ident_string: ColumnWithName = ColumnWithName(prefix + "ident_string")
  val anddatum_string: ColumnWithName = ColumnWithName(prefix + "anddatum_string")
  val veranlsr_string: ColumnWithName = ColumnWithName(prefix + "veranlsr_string")
  val fbl_int: ColumnWithName = ColumnWithName(prefix + "fbl_int")
  val biegrad_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "biegrad_decimal_4_0")
  val biegradpm_string: ColumnWithName = ColumnWithName(prefix + "biegradpm_string")
  val wanddiff_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "wanddiff_decimal_4_2")
  val wanddiffpm_string: ColumnWithName = ColumnWithName(prefix + "wanddiffpm_string")
  val bdruk_decimal_7_3: ColumnWithName = ColumnWithName(prefix + "bdruk_decimal_7_3")
  val bdrukpm_string: ColumnWithName = ColumnWithName(prefix + "bdrukpm_string")
  val pdruk_decimal_7_3: ColumnWithName = ColumnWithName(prefix + "pdruk_decimal_7_3")
  val pdrukpm_string: ColumnWithName = ColumnWithName(prefix + "pdrukpm_string")
  val sbdruk_decimal_7_3: ColumnWithName = ColumnWithName(prefix + "sbdruk_decimal_7_3")
  val sbdrukpm_string: ColumnWithName = ColumnWithName(prefix + "sbdrukpm_string")
  val znudruk_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "znudruk_decimal_4_2")
  val znudrukpm_string: ColumnWithName = ColumnWithName(prefix + "znudrukpm_string")
  val tbdruk_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "tbdruk_decimal_9_3")
  val volume_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "volume_decimal_12_3")
  val gewicht_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "gewicht_decimal_12_3")
  val gewichtpm_string: ColumnWithName = ColumnWithName(prefix + "gewichtpm_string")
  val dorn_string: ColumnWithName = ColumnWithName(prefix + "dorn_string")
  val dornmakt_string: ColumnWithName = ColumnWithName(prefix + "dornmakt_string")
  val dornumlauf_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "dornumlauf_decimal_2_0")
  val label_bom_string: ColumnWithName = ColumnWithName(prefix + "label_bom_string")
  val speznr_string: ColumnWithName = ColumnWithName(prefix + "speznr_string")
  val spez_werk_string: ColumnWithName = ColumnWithName(prefix + "spez_werk_string")
  val slbnr_string: ColumnWithName = ColumnWithName(prefix + "slbnr_string")
  val prodort_string: ColumnWithName = ColumnWithName(prefix + "prodort_string")
  val zsbteil_string: ColumnWithName = ColumnWithName(prefix + "zsbteil_string")
  val indm1_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "indm1_decimal_5_2")
  val indm2_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "indm2_decimal_5_2")
  val anzahl_string: ColumnWithName = ColumnWithName(prefix + "anzahl_string")
  val frgew_decimal_7_3: ColumnWithName = ColumnWithName(prefix + "frgew_decimal_7_3")
  val frgewpm_string: ColumnWithName = ColumnWithName(prefix + "frgewpm_string")
  val rlgew_decimal_7_3: ColumnWithName = ColumnWithName(prefix + "rlgew_decimal_7_3")
  val drukprz_decimal_5_1: ColumnWithName = ColumnWithName(prefix + "drukprz_decimal_5_1")
  val drukprb_decimal_5_1: ColumnWithName = ColumnWithName(prefix + "drukprb_decimal_5_1")
  val drukprbpm_string: ColumnWithName = ColumnWithName(prefix + "drukprbpm_string")
  val cadzvom_string: ColumnWithName = ColumnWithName(prefix + "cadzvom_string")
  val cadzbis_string: ColumnWithName = ColumnWithName(prefix + "cadzbis_string")
  val krkzvom_string: ColumnWithName = ColumnWithName(prefix + "krkzvom_string")
  val krkzbis_string: ColumnWithName = ColumnWithName(prefix + "krkzbis_string")
  val zsbzvom_string: ColumnWithName = ColumnWithName(prefix + "zsbzvom_string")
  val zsbzbis_string: ColumnWithName = ColumnWithName(prefix + "zsbzbis_string")
  val prick_string: ColumnWithName = ColumnWithName(prefix + "prick_string")
  val krart_string: ColumnWithName = ColumnWithName(prefix + "krart_string")
  val krdorn_string: ColumnWithName = ColumnWithName(prefix + "krdorn_string")
  val krdim_string: ColumnWithName = ColumnWithName(prefix + "krdim_string")
  val krbog_string: ColumnWithName = ColumnWithName(prefix + "krbog_string")
  val bogdorn_string: ColumnWithName = ColumnWithName(prefix + "bogdorn_string")
  val sndart_string: ColumnWithName = ColumnWithName(prefix + "sndart_string")
  val sndausf_string: ColumnWithName = ColumnWithName(prefix + "sndausf_string")
  val dorndm_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "dorndm_decimal_5_2")
  val dmauf1_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "dmauf1_decimal_5_2")
  val dmauf2_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "dmauf2_decimal_5_2")
  val vfabfall_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "vfabfall_decimal_5_2")
  val langfix_string: ColumnWithName = ColumnWithName(prefix + "langfix_string")
  val admfix_string: ColumnWithName = ColumnWithName(prefix + "admfix_string")
  val endoskop_string: ColumnWithName = ColumnWithName(prefix + "endoskop_string")
  val sgpruf_string: ColumnWithName = ColumnWithName(prefix + "sgpruf_string")
  val rarkont_string: ColumnWithName = ColumnWithName(prefix + "rarkont_string")
  val mtart_string: ColumnWithName = ColumnWithName(prefix + "mtart_string")
  val bgart_string: ColumnWithName = ColumnWithName(prefix + "bgart_string")
  val stufkz_string: ColumnWithName = ColumnWithName(prefix + "stufkz_string")
  val meins_string: ColumnWithName = ColumnWithName(prefix + "meins_string")
  val weitabteil_string: ColumnWithName = ColumnWithName(prefix + "weitabteil_string")
  val pkz_string: ColumnWithName = ColumnWithName(prefix + "pkz_string")
  val zondfp_string: ColumnWithName = ColumnWithName(prefix + "zondfp_string")
  val artnr_string: ColumnWithName = ColumnWithName(prefix + "artnr_string")
  val art_werk_string: ColumnWithName = ColumnWithName(prefix + "art_werk_string")
  val krumnr_string: ColumnWithName = ColumnWithName(prefix + "krumnr_string")
  val krum_werk_string: ColumnWithName = ColumnWithName(prefix + "krum_werk_string")
  val endbeg_string: ColumnWithName = ColumnWithName(prefix + "endbeg_string")
  val effvol_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "effvol_decimal_12_3")
  val diffvol_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "diffvol_decimal_12_3")
  val baukette_string: ColumnWithName = ColumnWithName(prefix + "baukette_string")
  val kurtxt_string: ColumnWithName = ColumnWithName(prefix + "kurtxt_string")
  val kurtxt24_string: ColumnWithName = ColumnWithName(prefix + "kurtxt24_string")
  val fertig_string: ColumnWithName = ColumnWithName(prefix + "fertig_string")
  val sbdrukopt_string: ColumnWithName = ColumnWithName(prefix + "sbdrukopt_string")
  val artmode_string: ColumnWithName = ColumnWithName(prefix + "artmode_string")
  val speed_string: ColumnWithName = ColumnWithName(prefix + "speed_string")
  val vkdat_string: ColumnWithName = ColumnWithName(prefix + "vkdat_string")
  val newkz_string: ColumnWithName = ColumnWithName(prefix + "newkz_string")
  val newkteil_string: ColumnWithName = ColumnWithName(prefix + "newkteil_string")
  val newkonf_string: ColumnWithName = ColumnWithName(prefix + "newkonf_string")
  val newapl_string: ColumnWithName = ColumnWithName(prefix + "newapl_string")
  val pemsyprj_string: ColumnWithName = ColumnWithName(prefix + "pemsyprj_string")
  val wrkver_string: ColumnWithName = ColumnWithName(prefix + "wrkver_string")
  val verf1_string: ColumnWithName = ColumnWithName(prefix + "verf1_string")
  val farbe1_string: ColumnWithName = ColumnWithName(prefix + "farbe1_string")
  val pos1_string: ColumnWithName = ColumnWithName(prefix + "pos1_string")
  val text1_string: ColumnWithName = ColumnWithName(prefix + "text1_string")
  val verf2_string: ColumnWithName = ColumnWithName(prefix + "verf2_string")
  val farbe2_string: ColumnWithName = ColumnWithName(prefix + "farbe2_string")
  val pos2_string: ColumnWithName = ColumnWithName(prefix + "pos2_string")
  val text2_string: ColumnWithName = ColumnWithName(prefix + "text2_string")
  val verf3_string: ColumnWithName = ColumnWithName(prefix + "verf3_string")
  val farbe3_string: ColumnWithName = ColumnWithName(prefix + "farbe3_string")
  val pos3_string: ColumnWithName = ColumnWithName(prefix + "pos3_string")
  val text3_string: ColumnWithName = ColumnWithName(prefix + "text3_string")
  val verf4_string: ColumnWithName = ColumnWithName(prefix + "verf4_string")
  val farbe4_string: ColumnWithName = ColumnWithName(prefix + "farbe4_string")
  val pos4_string: ColumnWithName = ColumnWithName(prefix + "pos4_string")
  val text4_string: ColumnWithName = ColumnWithName(prefix + "text4_string")
  val verf5_string: ColumnWithName = ColumnWithName(prefix + "verf5_string")
  val farbe5_string: ColumnWithName = ColumnWithName(prefix + "farbe5_string")
  val pos5_string: ColumnWithName = ColumnWithName(prefix + "pos5_string")
  val text5_string: ColumnWithName = ColumnWithName(prefix + "text5_string")
  val herstellverfahr_string: ColumnWithName = ColumnWithName(prefix + "herstellverfahr_string")
  val gewicht_neu_wert_string: ColumnWithName = ColumnWithName(prefix + "gewicht_neu_wert_string")
  val ststempel_string: ColumnWithName = ColumnWithName(prefix + "ststempel_string")
  val anfnr_string: ColumnWithName = ColumnWithName(prefix + "anfnr_string")
  val version_string: ColumnWithName = ColumnWithName(prefix + "version_string")
  val zt_apl_erstell_string: ColumnWithName = ColumnWithName(prefix + "zt_apl_erstell_string")
  val zt_art_string: ColumnWithName = ColumnWithName(prefix + "zt_art_string")
  val zt_laenge_ges_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "zt_laenge_ges_decimal_5_0")
  val zt_aufziehen_art_string: ColumnWithName = ColumnWithName(prefix + "zt_aufziehen_art_string")
  val zt_aufziehen_aus_string: ColumnWithName = ColumnWithName(prefix + "zt_aufziehen_aus_string")
  val zt_aufziehen_lae_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "zt_aufziehen_lae_decimal_5_0")
  val zt_biegen_art_string: ColumnWithName = ColumnWithName(prefix + "zt_biegen_art_string")
  val zt_biegen_anz_bo_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "zt_biegen_anz_bo_decimal_2_0")
  val zt_biegen_laenge_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "zt_biegen_laenge_decimal_5_0")
  val zt_montage_art_string: ColumnWithName = ColumnWithName(prefix + "zt_montage_art_string")
  val zt_montage_baute_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "zt_montage_baute_decimal_2_0")
  val zt_montage_vorga_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "zt_montage_vorga_decimal_2_0")
  val zt_montage_handl_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "zt_montage_handl_decimal_2_0")
  val zt_armieren_art_string: ColumnWithName = ColumnWithName(prefix + "zt_armieren_art_string")
  val zt_armieren_anz_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "zt_armieren_anz_decimal_2_0")
  val zt_armieren_laen_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "zt_armieren_laen_decimal_5_0")
  val zt_loeten_art_string: ColumnWithName = ColumnWithName(prefix + "zt_loeten_art_string")
  val zt_loeten_anzahl_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "zt_loeten_anzahl_decimal_2_0")
  val zt_loeten_laenge_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "zt_loeten_laenge_decimal_5_0")
  val zt_schneiden_art_string: ColumnWithName = ColumnWithName(prefix + "zt_schneiden_art_string")
  val zt_schneiden_anz_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "zt_schneiden_anz_decimal_2_0")
  val zt_schneiden_lae_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "zt_schneiden_lae_decimal_5_0")
  val zt_druckpr_art_string: ColumnWithName = ColumnWithName(prefix + "zt_druckpr_art_string")
  val zt_druckpr_ausf_string: ColumnWithName = ColumnWithName(prefix + "zt_druckpr_ausf_string")
  val zt_druckpr_lae_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "zt_druckpr_lae_decimal_5_0")
  val zt_stauchen_art_string: ColumnWithName = ColumnWithName(prefix + "zt_stauchen_art_string")
  val zt_stauchen_anz_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "zt_stauchen_anz_decimal_2_0")
  val zt_stauchen_lae_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "zt_stauchen_lae_decimal_5_0")
  val benslb_string: ColumnWithName = ColumnWithName(prefix + "benslb_string")
  val identslb_string: ColumnWithName = ColumnWithName(prefix + "identslb_string")
  val zt_innendm_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "zt_innendm_decimal_5_2")
  val begru_string: ColumnWithName = ColumnWithName(prefix + "begru_string")
  val fp_laufzeit_spul_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "fp_laufzeit_spul_decimal_4_0")
  val lagerware_string: ColumnWithName = ColumnWithName(prefix + "lagerware_string")
  val cross_media_string: ColumnWithName = ColumnWithName(prefix + "cross_media_string")
  val temperkey_string: ColumnWithName = ColumnWithName(prefix + "temperkey_string")
  val audm1_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "audm1_decimal_5_2")
  val audm2_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "audm2_decimal_5_2")
  val kst_roart_string: ColumnWithName = ColumnWithName(prefix + "kst_roart_string")
  val kst_wkz_string: ColumnWithName = ColumnWithName(prefix + "kst_wkz_string")
  val kst_bogen_string: ColumnWithName = ColumnWithName(prefix + "kst_bogen_string")
  val kst_snart_string: ColumnWithName = ColumnWithName(prefix + "kst_snart_string")
  val kst_stutz_string: ColumnWithName = ColumnWithName(prefix + "kst_stutz_string")
  val tfx_verfahren_string: ColumnWithName = ColumnWithName(prefix + "tfx_verfahren_string")
  val tfx_druck_decimal_4_1: ColumnWithName = ColumnWithName(prefix + "tfx_druck_decimal_4_1")
  val tfx_zeit_string: ColumnWithName = ColumnWithName(prefix + "tfx_zeit_string")
  val tfx_temp_string: ColumnWithName = ColumnWithName(prefix + "tfx_temp_string")
  val tfx_text_string: ColumnWithName = ColumnWithName(prefix + "tfx_text_string")
  val lfix_huelse_string: ColumnWithName = ColumnWithName(prefix + "lfix_huelse_string")
  val lfix_kappe_string: ColumnWithName = ColumnWithName(prefix + "lfix_kappe_string")
  val lfix_trennsch_string: ColumnWithName = ColumnWithName(prefix + "lfix_trennsch_string")
  val lfix_kreismsr_string: ColumnWithName = ColumnWithName(prefix + "lfix_kreismsr_string")
  val extr_geschw_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "extr_geschw_decimal_5_2")
  val rohlaufbau_string: ColumnWithName = ColumnWithName(prefix + "rohlaufbau_string")
  val anzkaliber_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "anzkaliber_decimal_2_0")
  val krdornlen_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "krdornlen_decimal_4_0")
  val ausmi_string: ColumnWithName = ColumnWithName(prefix + "ausmi_string")
  val ausro_string: ColumnWithName = ColumnWithName(prefix + "ausro_string")
  val mindestmenge_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "mindestmenge_decimal_3_0")
  val kavitaeten_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "kavitaeten_decimal_3_0")
  val einlegeteile_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "einlegeteile_decimal_3_0")
  val spindeln_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "spindeln_decimal_3_0")
  val bohren_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "bohren_decimal_3_0")
  val saegen_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "saegen_decimal_3_0")
  val huelsen_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "huelsen_decimal_3_0")
  val schneidenzw_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "schneidenzw_decimal_3_0")
  val abkuehlenwz_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "abkuehlenwz_decimal_5_2")
  val dichtpruefung_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "dichtpruefung_decimal_5_2")
  val schweissens_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "schweissens_decimal_5_2")
  val schweissenc_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "schweissenc_decimal_5_2")
  val schweissenir_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "schweissenir_decimal_5_2")
  val befuellakk_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "befuellakk_decimal_4_0")
  val einsaugen_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "einsaugen_decimal_4_0")
  val aufblasen_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "aufblasen_decimal_4_0")
  val abkuehlen_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "abkuehlen_decimal_4_0")
  val schneiden_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "schneiden_decimal_4_0")
  val entnahme_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "entnahme_decimal_4_0")
  val bestueckung_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "bestueckung_decimal_4_0")
  val kuehlfixi_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "kuehlfixi_decimal_5_2")
  val zyklus_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "zyklus_decimal_7_2")
  val braende_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "braende_decimal_3_0")
  val teiletempern_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "teiletempern_decimal_3_0")
  val lohnarbeit_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "lohnarbeit_decimal_7_2")
  val maschtyp_string: ColumnWithName = ColumnWithName(prefix + "maschtyp_string")
  val profil_string: ColumnWithName = ColumnWithName(prefix + "profil_string")
  val blechtyp_string: ColumnWithName = ColumnWithName(prefix + "blechtyp_string")
  val stckgeb_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "stckgeb_decimal_4_0")
  val blechbem_string: ColumnWithName = ColumnWithName(prefix + "blechbem_string")
  val kdmatnr_string: ColumnWithName = ColumnWithName(prefix + "kdmatnr_string")
  val kdwerks_string: ColumnWithName = ColumnWithName(prefix + "kdwerks_string")
  val kdfirma_string: ColumnWithName = ColumnWithName(prefix + "kdfirma_string")
  val kennnok_string: ColumnWithName = ColumnWithName(prefix + "kennnok_string")
  val aedatkenn_string: ColumnWithName = ColumnWithName(prefix + "aedatkenn_string")
  val anzvulkwz_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "anzvulkwz_decimal_4_0")
  val laengeoh_decimal_10_2: ColumnWithName = ColumnWithName(prefix + "laengeoh_decimal_10_2")
  val hwickel_v_string: ColumnWithName = ColumnWithName(prefix + "hwickel_v_string")
  val hwickel_h_string: ColumnWithName = ColumnWithName(prefix + "hwickel_h_string")
}

object C_zppkopf extends C_zppkopf("") {
  def as(alias: String): C_zppkopf = new C_zppkopf(alias + ".")
}

// AUTO GENERATED:END
