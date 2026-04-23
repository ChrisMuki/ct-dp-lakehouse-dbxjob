// AUTO GENERATED CODE - DO NOT EDIT
package ct.dna.lakehouse.sr_raw.ct_gbl_p24

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

case class E_zspppos_part1(
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
    werks_string: String,
    matnr_string: String,
    posnr_string: String,
    stlan_string: String,
    idnrk_string: String,
    orgsystem_string: String,
    anzspul_int: BoxedInt,
    faedikeit_int: BoxedInt,
    @Decimal(8, 2) steig_decimal_8_2: BigDecimal,
    @Decimal(5, 2) steigtlp_decimal_5_2: BigDecimal,
    @Decimal(5, 2) steigtln_decimal_5_2: BigDecimal,
    radsch_string: String,
    radabzg_string: String,
    @Decimal(7, 2) durchmess_decimal_7_2: BigDecimal,
    durchmesspm_string: String,
    @Decimal(8, 2) dmtlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) dmtlpn_decimal_8_2: BigDecimal,
    medurchm_string: String,
    @Decimal(8, 2) plattebr_decimal_8_2: BigDecimal,
    mebreite_string: String,
    @Decimal(8, 2) plattedi_decimal_8_2: BigDecimal,
    @Decimal(8, 2) dicke_decimal_8_2: BigDecimal,
    @Decimal(8, 2) dickefert_decimal_8_2: BigDecimal,
    dickefertpm_string: String,
    medicke_string: String,
    medruck_string: String,
    ueberlpl_string: String,
    meueberlap_string: String,
    @Decimal(8, 2) lauflaenge_decimal_8_2: BigDecimal,
    melaenge_string: String,
    @Decimal(5, 2) winkelpl_decimal_5_2: BigDecimal,
    @Decimal(8, 2) schnittbr_decimal_8_2: BigDecimal,
    uebrlapgw_string: String,
    wendelart_string: String,
    anzsphmt_string: String,
    richtung_string: String,
    @Decimal(7, 3) vabzugagi_decimal_7_3: BigDecimal,
    mevabzug_string: String,
    andruck_string: String,
    @Decimal(8, 2) drextagi_decimal_8_2: BigDecimal,
    @Decimal(8, 2) drehextlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) drehextln_decimal_8_2: BigDecimal,
    medrehzahl_string: String,
    @Decimal(4, 0) fasdenspan_decimal_4_0: BigDecimal,
    fadensptol_string: String,
    kenfadcod_string: String,
    mefadensp_string: String,
    @Decimal(8, 2) admagi_decimal_8_2: BigDecimal,
    @Decimal(8, 2) admagitlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) admagitln_decimal_8_2: BigDecimal,
    @Decimal(8, 2) idurchm_decimal_8_2: BigDecimal,
    @Decimal(8, 2) idmtlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) idmtln_decimal_8_2: BigDecimal,
    kalibduese_string: String,
    kuehlagi_string: String,
    anlage_string: String,
    passstnr_string: String,
    @Decimal(8, 2) rohmagi_decimal_8_2: BigDecimal,
    @Decimal(8, 2) rohmagitp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) rohmagitn_decimal_8_2: BigDecimal,
    melaeng_string: String,
    @Decimal(8, 2) wanddicke_decimal_8_2: BigDecimal,
    @Decimal(8, 2) wanddtlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) wanddtln_decimal_8_2: BigDecimal,
    mewanddick_string: String,
    @Decimal(8, 2) wddagi_decimal_8_2: BigDecimal,
    siebwech_string: String,
    strasagi_string: String,
    bauspezi_string: String,
    dimdornl_string: String,
    @Decimal(8, 2) dornltlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) dornltln_decimal_8_2: BigDecimal,
    trennmagi_string: String,
    hrzlsg_string: String,
    sprdagi_string: String,
    @Decimal(8, 2) sprdagitp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) sprdagitn_decimal_8_2: BigDecimal,
    @Decimal(8, 2) sprmsagi_decimal_8_2: BigDecimal,
    @Decimal(8, 2) sprsagitp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) sprsagitn_decimal_8_2: BigDecimal,
    @Decimal(8, 2) schaeltemp_decimal_8_2: BigDecimal,
    metemp_string: String,
    @Decimal(8, 2) vakagi_decimal_8_2: BigDecimal,
    @Decimal(8, 2) vakagitlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) vakagitln_decimal_8_2: BigDecimal,
    mevakuum_string: String,
    mischgew_string: String,
    matbezagi_string: String,
    kunstmant_string: String,
    messertyp_string: String,
    mgew_string: String,
    @Decimal(9, 2) steigprod_decimal_9_2: BigDecimal,
    @Decimal(8, 2) gefldmprod_decimal_8_2: BigDecimal,
    altgarncod_string: String,
    losverhlt_string: String,
    @Decimal(8, 2) drzser_decimal_8_2: BigDecimal,
    @Decimal(8, 2) drzsertlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) drzsertln_decimal_8_2: BigDecimal,
    @Decimal(8, 2) drzrol_decimal_8_2: BigDecimal,
    @Decimal(8, 2) drzroltlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) drzroltln_decimal_8_2: BigDecimal,
    @Decimal(8, 2) vabzugtlp_decimal_8_2: BigDecimal,
    @Decimal(8, 2) vabzugtln_decimal_8_2: BigDecimal,
    @Decimal(8, 2) efbrplatt_decimal_8_2: BigDecimal,
    @Decimal(8, 2) stplatte_decimal_8_2: BigDecimal,
    @Decimal(5, 2) wigewebe_decimal_5_2: BigDecimal,
    kalgeart_string: String,
    gummgew_string: String,
    kalgeart1_string: String,
    gummgew1_string: String,
    kalgeart2_string: String,
    gummgew2_string: String,
    @Decimal(8, 2) ststrick_decimal_8_2: BigDecimal,
    bemtim_string: String,
    bemerk_string: String,
    vornr_string: String,
    @Decimal(7, 2) mittdm_decimal_7_2: BigDecimal,
    aufbrart_string: String,
    @Decimal(4, 2) dickevorg_decimal_4_2: BigDecimal,
    dickeprod_string: String,
    @Decimal(12, 3) hohlvol_decimal_12_3: BigDecimal,
    @Decimal(12, 3) effvol_decimal_12_3: BigDecimal,
    @Decimal(12, 3) teilgw_decimal_12_3: BigDecimal,
    @Decimal(9, 3) berstdruck_decimal_9_3: BigDecimal,
    @Decimal(5, 2) winkelvorgb_decimal_5_2: BigDecimal,
    @Decimal(5, 2) winkelrueck_decimal_5_2: BigDecimal,
    @Decimal(6, 2) sterrch_decimal_6_2: BigDecimal,
    berechnforme_int: BoxedInt,
    @Decimal(3, 0) spulen_decimal_3_0: BigDecimal,
    @Decimal(3, 0) spulevon_decimal_3_0: BigDecimal,
    @Decimal(3, 0) spulebis_decimal_3_0: BigDecimal,
    @Decimal(4, 2) spantlp_decimal_4_2: BigDecimal,
    @Decimal(4, 2) spantln_decimal_4_2: BigDecimal,
    @Decimal(4, 2) kfaktor_decimal_4_2: BigDecimal,
    einlaufrch_string: String,
    berstdruckun_string: String,
    @Decimal(6, 2) faedmax_decimal_6_2: BigDecimal,
    @Decimal(5, 2) fadabsttheor_decimal_5_2: BigDecimal,
    @Decimal(12, 3) effvolbau_decimal_12_3: BigDecimal,
    @Decimal(5, 2) abdeckung_decimal_5_2: BigDecimal,
    @Decimal(9, 3) teilbd_decimal_9_3: BigDecimal,
    @Decimal(5, 2) effbreite_decimal_5_2: BigDecimal,
    ueberlappung_string: String,
    @Decimal(5, 2) winkel_decimal_5_2: BigDecimal,
    @Decimal(4, 0) stgewaehlt_decimal_4_0: BigDecimal,
    @Decimal(4, 2) wendeldurch_decimal_4_2: BigDecimal,
    @Decimal(5, 2) fadenabst_decimal_5_2: BigDecimal,
    @Decimal(5, 3) auftrag_decimal_5_3: BigDecimal,
    @Decimal(4, 2) stgewtlp_decimal_4_2: BigDecimal,
    @Decimal(4, 2) stgewtln_decimal_4_2: BigDecimal,
    @Decimal(3, 0) arbdurch_decimal_3_0: BigDecimal,
    @Decimal(3, 0) maschenzahl_decimal_3_0: BigDecimal,
    @Decimal(4, 2) maschenbreite_decimal_4_2: BigDecimal,
    @Decimal(4, 2) maschenhoehe_decimal_4_2: BigDecimal,
    @Decimal(4, 2) verhaeltnis_decimal_4_2: BigDecimal,
    herstabt_string: String,
    maschnum_string: String,
    maschtyp_string: String,
    checkmasch_string: String,
    @Decimal(5, 4) einstellwert_decimal_5_4: BigDecimal,
    @Decimal(4, 0) maschendrzahl_decimal_4_0: BigDecimal,
    @Decimal(2, 0) strickkopf_decimal_2_0: BigDecimal,
    @Decimal(3, 0) wechscheibevon_decimal_3_0: BigDecimal,
    @Decimal(3, 0) wechscheibebis_decimal_3_0: BigDecimal,
    @Decimal(3, 0) wechabzugvon_decimal_3_0: BigDecimal,
    @Decimal(3, 0) wechabzugbis_decimal_3_0: BigDecimal,
    abzug_string: String,
    zsbvor_string: String,
    @Decimal(13, 3) menge_decimal_13_3: BigDecimal,
    meins_string: String,
    @Decimal(4, 0) drill_decimal_4_0: BigDecimal,
    @Decimal(6, 3) berbreite_decimal_6_3: BigDecimal,
    @Decimal(8, 2) sbreite_decimal_8_2: BigDecimal,
    @Decimal(4, 2) dipauf_decimal_4_2: BigDecimal,
    tauchl_string: String,
    speed_string: String,
    counter_int: BoxedInt,
    @Decimal(12, 3) gewicht_decimal_12_3: BigDecimal,
    @Decimal(5, 2) spinfakt_decimal_5_2: BigDecimal,
    @Decimal(7, 2) breiteerr_decimal_7_2: BigDecimal,
    @Decimal(7, 2) breitegew_decimal_7_2: BigDecimal,
    @Decimal(7, 2) theorbreite_decimal_7_2: BigDecimal,
    @Decimal(12, 3) teileffvol_decimal_12_3: BigDecimal,
    @Decimal(12, 3) sumgew_decimal_12_3: BigDecimal,
    bauname_string: String,
    formnum_string: String,
    subcounter_int: BoxedInt,
    @Decimal(5, 2) schrumpfung_decimal_5_2: BigDecimal,
    @Decimal(4, 2) maschenhoehegw_decimal_4_2: BigDecimal,
    wpos_string: String,
    @Decimal(12, 3) gewicht_herst_decimal_12_3: BigDecimal,
    @Decimal(12, 3) gewicht_gewogen_decimal_12_3: BigDecimal,
    @Decimal(5, 2) idurchm_gew_decimal_5_2: BigDecimal,
    @Decimal(4, 2) idmtolo_gew_decimal_4_2: BigDecimal,
    @Decimal(4, 2) idmtolu_gew_decimal_4_2: BigDecimal,
    @Decimal(5, 2) adurchm_gew_decimal_5_2: BigDecimal,
    @Decimal(4, 2) admtolo_gew_decimal_4_2: BigDecimal,
    @Decimal(4, 2) admtolu_gew_decimal_4_2: BigDecimal,
    @Decimal(4, 2) wdtolo_gew_decimal_4_2: BigDecimal,
    @Decimal(4, 2) wdtolu_gew_decimal_4_2: BigDecimal,
    @Decimal(5, 2) ausch_decimal_5_2: BigDecimal,
    @Decimal(7, 2) mittdm_gew_decimal_7_2: BigDecimal,
    @Decimal(5, 2) winkelrueck_gew_decimal_5_2: BigDecimal,
    @Decimal(8, 2) lauflaenge_gew_decimal_8_2: BigDecimal,
    @Decimal(12, 3) teilgw_gew_decimal_12_3: BigDecimal,
    @Decimal(12, 3) gewicht_gew_decimal_12_3: BigDecimal,
    usepe_string: String,
    farbscheibe_string: String,
    zfadenspann_string: String,
    @Decimal(4, 0) gw_breite_decimal_4_0: BigDecimal,
    @Decimal(4, 0) gw_laenge_decimal_4_0: BigDecimal,
    @Decimal(7, 1) gw_gewicht_decimal_7_1: BigDecimal,
    @Decimal(3, 2) gw_schdicke_decimal_3_2: BigDecimal,
    @Decimal(1, 0) gw_lagen_decimal_1_0: BigDecimal,
    gw_winkel_string: String,
    @Decimal(4, 0) auf_breite_decimal_4_0: BigDecimal,
    @Decimal(4, 0) auf_laenge_decimal_4_0: BigDecimal,
    @Decimal(2, 0) auf_anz_rohling_decimal_2_0: BigDecimal,
    @Decimal(2, 0) anzahl_muffen_decimal_2_0: BigDecimal,
    rechnedm_string: String,
    strkopf_string: String,
    @Decimal(5, 2) mundstueck_decimal_5_2: BigDecimal,
    @Decimal(5, 2) spritzdorn_decimal_5_2: BigDecimal,
    @Decimal(3, 0) tempz01_decimal_3_0: BigDecimal,
    @Decimal(3, 0) tempz02_decimal_3_0: BigDecimal,
    @Decimal(3, 0) tempz03_decimal_3_0: BigDecimal,
    @Decimal(3, 0) tempz04_decimal_3_0: BigDecimal,
    @Decimal(3, 0) tempz05_decimal_3_0: BigDecimal,
    @Decimal(3, 0) tempz06_decimal_3_0: BigDecimal,
    extrudernr_string: String,
    extrpgm_string: String,
    @Decimal(3, 0) extrtemp_decimal_3_0: BigDecimal,
    @Decimal(5, 2) extrdruck_decimal_5_2: BigDecimal,
    @Decimal(4, 2) extrdrehzahl_decimal_4_2: BigDecimal,
    sieb1_string: String,
    sieb2_string: String,
    sieb3_string: String,
    sieb4_string: String,
    sieb5_string: String,
    sieb6_string: String,
    @Decimal(5, 2) lineabzuggeschw_decimal_5_2: BigDecimal
) extends Entity

case class E_zspppos_part2(
    @PK _mk_org: String,
    @Decimal(5, 0) stuetzluft_decimal_5_0: BigDecimal,
    @Decimal(4, 0) durchhang_decimal_4_0: BigDecimal,
    @Decimal(4, 2) vakuumduese_decimal_4_2: BigDecimal,
    @Decimal(3, 2) vakuum_decimal_3_2: BigDecimal,
    xbomconsolidate_string: String,
    section1_string: String,
    section2_string: String,
    section3_string: String,
    section4_string: String,
    section5_string: String,
    section6_string: String,
    section7_string: String,
    section8_string: String,
    section9_string: String,
    section10_string: String,
    section11_string: String,
    section12_string: String,
    section13_string: String,
    section14_string: String,
    section15_string: String,
    section16_string: String,
    section17_string: String,
    section18_string: String,
    section19_string: String,
    section20_string: String,
    section21_string: String,
    section22_string: String,
    section23_string: String,
    section24_string: String,
    section25_string: String,
    section26_string: String,
    section27_string: String,
    section28_string: String,
    section29_string: String,
    section30_string: String,
    section31_string: String,
    section32_string: String,
    section33_string: String,
    section34_string: String,
    section35_string: String,
    section36_string: String,
    section37_string: String,
    section38_string: String,
    section39_string: String,
    section40_string: String,
    section41_string: String,
    section42_string: String,
    section43_string: String,
    section44_string: String,
    section45_string: String,
    section46_string: String,
    section47_string: String,
    section48_string: String,
    section49_string: String,
    section50_string: String,
    maschflm_string: String
) extends Entity

object zspppos extends TableSpec[Joined[E_zspppos_part1, E_zspppos_part2]](enableChangeDataFeed = true, manualClusterBy = None, timetravelDays = 35) with Loaded

// AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY

import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_zspppos(prefix: String) extends ColumnWithNameAccessor {
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
  val werks_string: ColumnWithName = ColumnWithName(prefix + "werks_string")
  val matnr_string: ColumnWithName = ColumnWithName(prefix + "matnr_string")
  val posnr_string: ColumnWithName = ColumnWithName(prefix + "posnr_string")
  val stlan_string: ColumnWithName = ColumnWithName(prefix + "stlan_string")
  val idnrk_string: ColumnWithName = ColumnWithName(prefix + "idnrk_string")
  val orgsystem_string: ColumnWithName = ColumnWithName(prefix + "orgsystem_string")
  val anzspul_int: ColumnWithName = ColumnWithName(prefix + "anzspul_int")
  val faedikeit_int: ColumnWithName = ColumnWithName(prefix + "faedikeit_int")
  val steig_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "steig_decimal_8_2")
  val steigtlp_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "steigtlp_decimal_5_2")
  val steigtln_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "steigtln_decimal_5_2")
  val radsch_string: ColumnWithName = ColumnWithName(prefix + "radsch_string")
  val radabzg_string: ColumnWithName = ColumnWithName(prefix + "radabzg_string")
  val durchmess_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "durchmess_decimal_7_2")
  val durchmesspm_string: ColumnWithName = ColumnWithName(prefix + "durchmesspm_string")
  val dmtlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "dmtlp_decimal_8_2")
  val dmtlpn_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "dmtlpn_decimal_8_2")
  val medurchm_string: ColumnWithName = ColumnWithName(prefix + "medurchm_string")
  val plattebr_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "plattebr_decimal_8_2")
  val mebreite_string: ColumnWithName = ColumnWithName(prefix + "mebreite_string")
  val plattedi_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "plattedi_decimal_8_2")
  val dicke_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "dicke_decimal_8_2")
  val dickefert_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "dickefert_decimal_8_2")
  val dickefertpm_string: ColumnWithName = ColumnWithName(prefix + "dickefertpm_string")
  val medicke_string: ColumnWithName = ColumnWithName(prefix + "medicke_string")
  val medruck_string: ColumnWithName = ColumnWithName(prefix + "medruck_string")
  val ueberlpl_string: ColumnWithName = ColumnWithName(prefix + "ueberlpl_string")
  val meueberlap_string: ColumnWithName = ColumnWithName(prefix + "meueberlap_string")
  val lauflaenge_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "lauflaenge_decimal_8_2")
  val melaenge_string: ColumnWithName = ColumnWithName(prefix + "melaenge_string")
  val winkelpl_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "winkelpl_decimal_5_2")
  val schnittbr_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "schnittbr_decimal_8_2")
  val uebrlapgw_string: ColumnWithName = ColumnWithName(prefix + "uebrlapgw_string")
  val wendelart_string: ColumnWithName = ColumnWithName(prefix + "wendelart_string")
  val anzsphmt_string: ColumnWithName = ColumnWithName(prefix + "anzsphmt_string")
  val richtung_string: ColumnWithName = ColumnWithName(prefix + "richtung_string")
  val vabzugagi_decimal_7_3: ColumnWithName = ColumnWithName(prefix + "vabzugagi_decimal_7_3")
  val mevabzug_string: ColumnWithName = ColumnWithName(prefix + "mevabzug_string")
  val andruck_string: ColumnWithName = ColumnWithName(prefix + "andruck_string")
  val drextagi_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "drextagi_decimal_8_2")
  val drehextlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "drehextlp_decimal_8_2")
  val drehextln_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "drehextln_decimal_8_2")
  val medrehzahl_string: ColumnWithName = ColumnWithName(prefix + "medrehzahl_string")
  val fasdenspan_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "fasdenspan_decimal_4_0")
  val fadensptol_string: ColumnWithName = ColumnWithName(prefix + "fadensptol_string")
  val kenfadcod_string: ColumnWithName = ColumnWithName(prefix + "kenfadcod_string")
  val mefadensp_string: ColumnWithName = ColumnWithName(prefix + "mefadensp_string")
  val admagi_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "admagi_decimal_8_2")
  val admagitlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "admagitlp_decimal_8_2")
  val admagitln_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "admagitln_decimal_8_2")
  val idurchm_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "idurchm_decimal_8_2")
  val idmtlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "idmtlp_decimal_8_2")
  val idmtln_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "idmtln_decimal_8_2")
  val kalibduese_string: ColumnWithName = ColumnWithName(prefix + "kalibduese_string")
  val kuehlagi_string: ColumnWithName = ColumnWithName(prefix + "kuehlagi_string")
  val anlage_string: ColumnWithName = ColumnWithName(prefix + "anlage_string")
  val passstnr_string: ColumnWithName = ColumnWithName(prefix + "passstnr_string")
  val rohmagi_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "rohmagi_decimal_8_2")
  val rohmagitp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "rohmagitp_decimal_8_2")
  val rohmagitn_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "rohmagitn_decimal_8_2")
  val melaeng_string: ColumnWithName = ColumnWithName(prefix + "melaeng_string")
  val wanddicke_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "wanddicke_decimal_8_2")
  val wanddtlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "wanddtlp_decimal_8_2")
  val wanddtln_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "wanddtln_decimal_8_2")
  val mewanddick_string: ColumnWithName = ColumnWithName(prefix + "mewanddick_string")
  val wddagi_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "wddagi_decimal_8_2")
  val siebwech_string: ColumnWithName = ColumnWithName(prefix + "siebwech_string")
  val strasagi_string: ColumnWithName = ColumnWithName(prefix + "strasagi_string")
  val bauspezi_string: ColumnWithName = ColumnWithName(prefix + "bauspezi_string")
  val dimdornl_string: ColumnWithName = ColumnWithName(prefix + "dimdornl_string")
  val dornltlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "dornltlp_decimal_8_2")
  val dornltln_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "dornltln_decimal_8_2")
  val trennmagi_string: ColumnWithName = ColumnWithName(prefix + "trennmagi_string")
  val hrzlsg_string: ColumnWithName = ColumnWithName(prefix + "hrzlsg_string")
  val sprdagi_string: ColumnWithName = ColumnWithName(prefix + "sprdagi_string")
  val sprdagitp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "sprdagitp_decimal_8_2")
  val sprdagitn_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "sprdagitn_decimal_8_2")
  val sprmsagi_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "sprmsagi_decimal_8_2")
  val sprsagitp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "sprsagitp_decimal_8_2")
  val sprsagitn_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "sprsagitn_decimal_8_2")
  val schaeltemp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "schaeltemp_decimal_8_2")
  val metemp_string: ColumnWithName = ColumnWithName(prefix + "metemp_string")
  val vakagi_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "vakagi_decimal_8_2")
  val vakagitlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "vakagitlp_decimal_8_2")
  val vakagitln_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "vakagitln_decimal_8_2")
  val mevakuum_string: ColumnWithName = ColumnWithName(prefix + "mevakuum_string")
  val mischgew_string: ColumnWithName = ColumnWithName(prefix + "mischgew_string")
  val matbezagi_string: ColumnWithName = ColumnWithName(prefix + "matbezagi_string")
  val kunstmant_string: ColumnWithName = ColumnWithName(prefix + "kunstmant_string")
  val messertyp_string: ColumnWithName = ColumnWithName(prefix + "messertyp_string")
  val mgew_string: ColumnWithName = ColumnWithName(prefix + "mgew_string")
  val steigprod_decimal_9_2: ColumnWithName = ColumnWithName(prefix + "steigprod_decimal_9_2")
  val gefldmprod_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "gefldmprod_decimal_8_2")
  val altgarncod_string: ColumnWithName = ColumnWithName(prefix + "altgarncod_string")
  val losverhlt_string: ColumnWithName = ColumnWithName(prefix + "losverhlt_string")
  val drzser_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "drzser_decimal_8_2")
  val drzsertlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "drzsertlp_decimal_8_2")
  val drzsertln_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "drzsertln_decimal_8_2")
  val drzrol_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "drzrol_decimal_8_2")
  val drzroltlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "drzroltlp_decimal_8_2")
  val drzroltln_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "drzroltln_decimal_8_2")
  val vabzugtlp_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "vabzugtlp_decimal_8_2")
  val vabzugtln_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "vabzugtln_decimal_8_2")
  val efbrplatt_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "efbrplatt_decimal_8_2")
  val stplatte_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "stplatte_decimal_8_2")
  val wigewebe_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "wigewebe_decimal_5_2")
  val kalgeart_string: ColumnWithName = ColumnWithName(prefix + "kalgeart_string")
  val gummgew_string: ColumnWithName = ColumnWithName(prefix + "gummgew_string")
  val kalgeart1_string: ColumnWithName = ColumnWithName(prefix + "kalgeart1_string")
  val gummgew1_string: ColumnWithName = ColumnWithName(prefix + "gummgew1_string")
  val kalgeart2_string: ColumnWithName = ColumnWithName(prefix + "kalgeart2_string")
  val gummgew2_string: ColumnWithName = ColumnWithName(prefix + "gummgew2_string")
  val ststrick_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "ststrick_decimal_8_2")
  val bemtim_string: ColumnWithName = ColumnWithName(prefix + "bemtim_string")
  val bemerk_string: ColumnWithName = ColumnWithName(prefix + "bemerk_string")
  val vornr_string: ColumnWithName = ColumnWithName(prefix + "vornr_string")
  val mittdm_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "mittdm_decimal_7_2")
  val aufbrart_string: ColumnWithName = ColumnWithName(prefix + "aufbrart_string")
  val dickevorg_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "dickevorg_decimal_4_2")
  val dickeprod_string: ColumnWithName = ColumnWithName(prefix + "dickeprod_string")
  val hohlvol_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "hohlvol_decimal_12_3")
  val effvol_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "effvol_decimal_12_3")
  val teilgw_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "teilgw_decimal_12_3")
  val berstdruck_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "berstdruck_decimal_9_3")
  val winkelvorgb_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "winkelvorgb_decimal_5_2")
  val winkelrueck_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "winkelrueck_decimal_5_2")
  val sterrch_decimal_6_2: ColumnWithName = ColumnWithName(prefix + "sterrch_decimal_6_2")
  val berechnforme_int: ColumnWithName = ColumnWithName(prefix + "berechnforme_int")
  val spulen_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "spulen_decimal_3_0")
  val spulevon_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "spulevon_decimal_3_0")
  val spulebis_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "spulebis_decimal_3_0")
  val spantlp_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "spantlp_decimal_4_2")
  val spantln_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "spantln_decimal_4_2")
  val kfaktor_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "kfaktor_decimal_4_2")
  val einlaufrch_string: ColumnWithName = ColumnWithName(prefix + "einlaufrch_string")
  val berstdruckun_string: ColumnWithName = ColumnWithName(prefix + "berstdruckun_string")
  val faedmax_decimal_6_2: ColumnWithName = ColumnWithName(prefix + "faedmax_decimal_6_2")
  val fadabsttheor_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "fadabsttheor_decimal_5_2")
  val effvolbau_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "effvolbau_decimal_12_3")
  val abdeckung_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "abdeckung_decimal_5_2")
  val teilbd_decimal_9_3: ColumnWithName = ColumnWithName(prefix + "teilbd_decimal_9_3")
  val effbreite_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "effbreite_decimal_5_2")
  val ueberlappung_string: ColumnWithName = ColumnWithName(prefix + "ueberlappung_string")
  val winkel_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "winkel_decimal_5_2")
  val stgewaehlt_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "stgewaehlt_decimal_4_0")
  val wendeldurch_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "wendeldurch_decimal_4_2")
  val fadenabst_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "fadenabst_decimal_5_2")
  val auftrag_decimal_5_3: ColumnWithName = ColumnWithName(prefix + "auftrag_decimal_5_3")
  val stgewtlp_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "stgewtlp_decimal_4_2")
  val stgewtln_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "stgewtln_decimal_4_2")
  val arbdurch_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "arbdurch_decimal_3_0")
  val maschenzahl_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "maschenzahl_decimal_3_0")
  val maschenbreite_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "maschenbreite_decimal_4_2")
  val maschenhoehe_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "maschenhoehe_decimal_4_2")
  val verhaeltnis_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "verhaeltnis_decimal_4_2")
  val herstabt_string: ColumnWithName = ColumnWithName(prefix + "herstabt_string")
  val maschnum_string: ColumnWithName = ColumnWithName(prefix + "maschnum_string")
  val maschtyp_string: ColumnWithName = ColumnWithName(prefix + "maschtyp_string")
  val checkmasch_string: ColumnWithName = ColumnWithName(prefix + "checkmasch_string")
  val einstellwert_decimal_5_4: ColumnWithName = ColumnWithName(prefix + "einstellwert_decimal_5_4")
  val maschendrzahl_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "maschendrzahl_decimal_4_0")
  val strickkopf_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "strickkopf_decimal_2_0")
  val wechscheibevon_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "wechscheibevon_decimal_3_0")
  val wechscheibebis_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "wechscheibebis_decimal_3_0")
  val wechabzugvon_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "wechabzugvon_decimal_3_0")
  val wechabzugbis_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "wechabzugbis_decimal_3_0")
  val abzug_string: ColumnWithName = ColumnWithName(prefix + "abzug_string")
  val zsbvor_string: ColumnWithName = ColumnWithName(prefix + "zsbvor_string")
  val menge_decimal_13_3: ColumnWithName = ColumnWithName(prefix + "menge_decimal_13_3")
  val meins_string: ColumnWithName = ColumnWithName(prefix + "meins_string")
  val drill_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "drill_decimal_4_0")
  val berbreite_decimal_6_3: ColumnWithName = ColumnWithName(prefix + "berbreite_decimal_6_3")
  val sbreite_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "sbreite_decimal_8_2")
  val dipauf_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "dipauf_decimal_4_2")
  val tauchl_string: ColumnWithName = ColumnWithName(prefix + "tauchl_string")
  val speed_string: ColumnWithName = ColumnWithName(prefix + "speed_string")
  val counter_int: ColumnWithName = ColumnWithName(prefix + "counter_int")
  val gewicht_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "gewicht_decimal_12_3")
  val spinfakt_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "spinfakt_decimal_5_2")
  val breiteerr_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "breiteerr_decimal_7_2")
  val breitegew_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "breitegew_decimal_7_2")
  val theorbreite_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "theorbreite_decimal_7_2")
  val teileffvol_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "teileffvol_decimal_12_3")
  val sumgew_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "sumgew_decimal_12_3")
  val bauname_string: ColumnWithName = ColumnWithName(prefix + "bauname_string")
  val formnum_string: ColumnWithName = ColumnWithName(prefix + "formnum_string")
  val subcounter_int: ColumnWithName = ColumnWithName(prefix + "subcounter_int")
  val schrumpfung_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "schrumpfung_decimal_5_2")
  val maschenhoehegw_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "maschenhoehegw_decimal_4_2")
  val wpos_string: ColumnWithName = ColumnWithName(prefix + "wpos_string")
  val gewicht_herst_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "gewicht_herst_decimal_12_3")
  val gewicht_gewogen_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "gewicht_gewogen_decimal_12_3")
  val idurchm_gew_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "idurchm_gew_decimal_5_2")
  val idmtolo_gew_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "idmtolo_gew_decimal_4_2")
  val idmtolu_gew_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "idmtolu_gew_decimal_4_2")
  val adurchm_gew_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "adurchm_gew_decimal_5_2")
  val admtolo_gew_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "admtolo_gew_decimal_4_2")
  val admtolu_gew_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "admtolu_gew_decimal_4_2")
  val wdtolo_gew_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "wdtolo_gew_decimal_4_2")
  val wdtolu_gew_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "wdtolu_gew_decimal_4_2")
  val ausch_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "ausch_decimal_5_2")
  val mittdm_gew_decimal_7_2: ColumnWithName = ColumnWithName(prefix + "mittdm_gew_decimal_7_2")
  val winkelrueck_gew_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "winkelrueck_gew_decimal_5_2")
  val lauflaenge_gew_decimal_8_2: ColumnWithName = ColumnWithName(prefix + "lauflaenge_gew_decimal_8_2")
  val teilgw_gew_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "teilgw_gew_decimal_12_3")
  val gewicht_gew_decimal_12_3: ColumnWithName = ColumnWithName(prefix + "gewicht_gew_decimal_12_3")
  val usepe_string: ColumnWithName = ColumnWithName(prefix + "usepe_string")
  val farbscheibe_string: ColumnWithName = ColumnWithName(prefix + "farbscheibe_string")
  val zfadenspann_string: ColumnWithName = ColumnWithName(prefix + "zfadenspann_string")
  val gw_breite_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "gw_breite_decimal_4_0")
  val gw_laenge_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "gw_laenge_decimal_4_0")
  val gw_gewicht_decimal_7_1: ColumnWithName = ColumnWithName(prefix + "gw_gewicht_decimal_7_1")
  val gw_schdicke_decimal_3_2: ColumnWithName = ColumnWithName(prefix + "gw_schdicke_decimal_3_2")
  val gw_lagen_decimal_1_0: ColumnWithName = ColumnWithName(prefix + "gw_lagen_decimal_1_0")
  val gw_winkel_string: ColumnWithName = ColumnWithName(prefix + "gw_winkel_string")
  val auf_breite_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "auf_breite_decimal_4_0")
  val auf_laenge_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "auf_laenge_decimal_4_0")
  val auf_anz_rohling_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "auf_anz_rohling_decimal_2_0")
  val anzahl_muffen_decimal_2_0: ColumnWithName = ColumnWithName(prefix + "anzahl_muffen_decimal_2_0")
  val rechnedm_string: ColumnWithName = ColumnWithName(prefix + "rechnedm_string")
  val strkopf_string: ColumnWithName = ColumnWithName(prefix + "strkopf_string")
  val mundstueck_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "mundstueck_decimal_5_2")
  val spritzdorn_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "spritzdorn_decimal_5_2")
  val tempz01_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "tempz01_decimal_3_0")
  val tempz02_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "tempz02_decimal_3_0")
  val tempz03_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "tempz03_decimal_3_0")
  val tempz04_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "tempz04_decimal_3_0")
  val tempz05_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "tempz05_decimal_3_0")
  val tempz06_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "tempz06_decimal_3_0")
  val extrudernr_string: ColumnWithName = ColumnWithName(prefix + "extrudernr_string")
  val extrpgm_string: ColumnWithName = ColumnWithName(prefix + "extrpgm_string")
  val extrtemp_decimal_3_0: ColumnWithName = ColumnWithName(prefix + "extrtemp_decimal_3_0")
  val extrdruck_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "extrdruck_decimal_5_2")
  val extrdrehzahl_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "extrdrehzahl_decimal_4_2")
  val sieb1_string: ColumnWithName = ColumnWithName(prefix + "sieb1_string")
  val sieb2_string: ColumnWithName = ColumnWithName(prefix + "sieb2_string")
  val sieb3_string: ColumnWithName = ColumnWithName(prefix + "sieb3_string")
  val sieb4_string: ColumnWithName = ColumnWithName(prefix + "sieb4_string")
  val sieb5_string: ColumnWithName = ColumnWithName(prefix + "sieb5_string")
  val sieb6_string: ColumnWithName = ColumnWithName(prefix + "sieb6_string")
  val lineabzuggeschw_decimal_5_2: ColumnWithName = ColumnWithName(prefix + "lineabzuggeschw_decimal_5_2")
  val stuetzluft_decimal_5_0: ColumnWithName = ColumnWithName(prefix + "stuetzluft_decimal_5_0")
  val durchhang_decimal_4_0: ColumnWithName = ColumnWithName(prefix + "durchhang_decimal_4_0")
  val vakuumduese_decimal_4_2: ColumnWithName = ColumnWithName(prefix + "vakuumduese_decimal_4_2")
  val vakuum_decimal_3_2: ColumnWithName = ColumnWithName(prefix + "vakuum_decimal_3_2")
  val xbomconsolidate_string: ColumnWithName = ColumnWithName(prefix + "xbomconsolidate_string")
  val section1_string: ColumnWithName = ColumnWithName(prefix + "section1_string")
  val section2_string: ColumnWithName = ColumnWithName(prefix + "section2_string")
  val section3_string: ColumnWithName = ColumnWithName(prefix + "section3_string")
  val section4_string: ColumnWithName = ColumnWithName(prefix + "section4_string")
  val section5_string: ColumnWithName = ColumnWithName(prefix + "section5_string")
  val section6_string: ColumnWithName = ColumnWithName(prefix + "section6_string")
  val section7_string: ColumnWithName = ColumnWithName(prefix + "section7_string")
  val section8_string: ColumnWithName = ColumnWithName(prefix + "section8_string")
  val section9_string: ColumnWithName = ColumnWithName(prefix + "section9_string")
  val section10_string: ColumnWithName = ColumnWithName(prefix + "section10_string")
  val section11_string: ColumnWithName = ColumnWithName(prefix + "section11_string")
  val section12_string: ColumnWithName = ColumnWithName(prefix + "section12_string")
  val section13_string: ColumnWithName = ColumnWithName(prefix + "section13_string")
  val section14_string: ColumnWithName = ColumnWithName(prefix + "section14_string")
  val section15_string: ColumnWithName = ColumnWithName(prefix + "section15_string")
  val section16_string: ColumnWithName = ColumnWithName(prefix + "section16_string")
  val section17_string: ColumnWithName = ColumnWithName(prefix + "section17_string")
  val section18_string: ColumnWithName = ColumnWithName(prefix + "section18_string")
  val section19_string: ColumnWithName = ColumnWithName(prefix + "section19_string")
  val section20_string: ColumnWithName = ColumnWithName(prefix + "section20_string")
  val section21_string: ColumnWithName = ColumnWithName(prefix + "section21_string")
  val section22_string: ColumnWithName = ColumnWithName(prefix + "section22_string")
  val section23_string: ColumnWithName = ColumnWithName(prefix + "section23_string")
  val section24_string: ColumnWithName = ColumnWithName(prefix + "section24_string")
  val section25_string: ColumnWithName = ColumnWithName(prefix + "section25_string")
  val section26_string: ColumnWithName = ColumnWithName(prefix + "section26_string")
  val section27_string: ColumnWithName = ColumnWithName(prefix + "section27_string")
  val section28_string: ColumnWithName = ColumnWithName(prefix + "section28_string")
  val section29_string: ColumnWithName = ColumnWithName(prefix + "section29_string")
  val section30_string: ColumnWithName = ColumnWithName(prefix + "section30_string")
  val section31_string: ColumnWithName = ColumnWithName(prefix + "section31_string")
  val section32_string: ColumnWithName = ColumnWithName(prefix + "section32_string")
  val section33_string: ColumnWithName = ColumnWithName(prefix + "section33_string")
  val section34_string: ColumnWithName = ColumnWithName(prefix + "section34_string")
  val section35_string: ColumnWithName = ColumnWithName(prefix + "section35_string")
  val section36_string: ColumnWithName = ColumnWithName(prefix + "section36_string")
  val section37_string: ColumnWithName = ColumnWithName(prefix + "section37_string")
  val section38_string: ColumnWithName = ColumnWithName(prefix + "section38_string")
  val section39_string: ColumnWithName = ColumnWithName(prefix + "section39_string")
  val section40_string: ColumnWithName = ColumnWithName(prefix + "section40_string")
  val section41_string: ColumnWithName = ColumnWithName(prefix + "section41_string")
  val section42_string: ColumnWithName = ColumnWithName(prefix + "section42_string")
  val section43_string: ColumnWithName = ColumnWithName(prefix + "section43_string")
  val section44_string: ColumnWithName = ColumnWithName(prefix + "section44_string")
  val section45_string: ColumnWithName = ColumnWithName(prefix + "section45_string")
  val section46_string: ColumnWithName = ColumnWithName(prefix + "section46_string")
  val section47_string: ColumnWithName = ColumnWithName(prefix + "section47_string")
  val section48_string: ColumnWithName = ColumnWithName(prefix + "section48_string")
  val section49_string: ColumnWithName = ColumnWithName(prefix + "section49_string")
  val section50_string: ColumnWithName = ColumnWithName(prefix + "section50_string")
  val maschflm_string: ColumnWithName = ColumnWithName(prefix + "maschflm_string")
}

object C_zspppos extends C_zspppos("") {
  def as(alias: String): C_zspppos = new C_zspppos(alias + ".")
}

// AUTO GENERATED:END
