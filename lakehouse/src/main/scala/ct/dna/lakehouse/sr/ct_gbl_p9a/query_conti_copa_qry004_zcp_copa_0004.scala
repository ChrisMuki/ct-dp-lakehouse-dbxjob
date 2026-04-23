// This file is auto-generated. Do not edit manually.
package ct.dna.lakehouse.sr.ct_gbl_p9a

import ct.dna.lakehouse.core.framework.origin.ChangeKey
import ct.dna.lakehouse.core.model.Entity.{PK, NotNull, Decimal}
import ct.dna.lakehouse.core.model.{Entity, TableSpec}
import ct.dna.lakehouse.sr_raw.ct_gbl_p9a.{query_conti_copa_qry004_zcp_copa_0004 => sr_raw_query_conti_copa_qry004_zcp_copa_0004}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.{coalesce, col, lit, struct}

case class SrQuery_conti_copa_qry004_zcp_copa_0004(
    @NotNull _mk_org: String,
    @NotNull _mk_site: String,
    @NotNull _mk_system: String,
    @NotNull _mk_instance: String,
    @NotNull _mk_partition: String,
    @NotNull _mk_file: String,
    @NotNull _mk_container: String,
    @NotNull _mk_account: String,
    @NotNull _mk_created_at: Timestamp,
    _lh_id_in_message: Long,
    _lh_ingest_warning: String,
    _0gn_r3_ssy: String,
    _4zcp_copa_documenttype: String,
    _zdw_btype: String,
    @PK _zdw_bu: String,
    _zdw_bu_2zdw_bu_string: String,
    _zdw_bu_1zdw_bu_string: String,
    _zdw_ddate: String,
    _zdw_dtype: String,
    _zdw_gidat: String,
    _zdw_ivdat: String,
    @PK _zdw_matl: String,
    _zdw_matl_2zdw_matl_string: String,
    _zdw_matl_1zdw_matl_string: String,
    _zdw_ocdat: String,
    _zdw_pcomp: String,
    _zdw_pcomp_2zdw_pcomp_string: String,
    _zdw_pcomp_1zdw_pcomp_string: String,
    @PK _zdw_plant: String,
    _zdw_plant_2zdw_plant_string: String,
    _zdw_plant_1zdw_plant_string: String,
    _zdw_podat: String,
    _zdw_pscyr: String,
    _zdw_psdat: String,
    _zdw_rhdat: String,
    _zdw_rpdat: String,
    _zdw_sdto: String,
    _zdw_sdto_2zdw_sdto_string: String,
    _zdw_sdto_1zdw_sdto_string: String,
    @PK _zdw_seg: String,
    _zdw_seg_2zdw_seg_string: String,
    _zdw_seg_1zdw_seg_string: String,
    @PK _zdw_snpdt: String,
    _zdw_tgdat: String,
    _zdw_vrtup: String,
    _zdw_vrtup_2zdw_vrtup_string: String,
    _zdw_vrtup_1zdw_vrtup_string: String,
    @Decimal(15, 2) _1nbg5lj7w7gam9zbngu9lqisu_1nbg5lj7w7gam9zbngu9ltoku_decimal_15_2: BigDecimal,
    _1nbg5lj7w7gam9zbngu9lqisu_1nbg5lj7w7gam9zbngu9mwhim_double: BoxedDouble,
    _1nbg5lj7w7gam9zbngu9lqisu_1nbg5lj7w7gamnplq3t3p38s1_double: BoxedDouble,
    @Decimal(15, 3) _1nbg5lj7w7gam9zbngu9lqisu_e6l8g90fhoqyrdsl7kpfvu0l4_decimal_15_3: BigDecimal
) extends Entity

object query_conti_copa_qry004_zcp_copa_0004
    extends TableSpec[SrQuery_conti_copa_qry004_zcp_copa_0004]
    with ChangeKey[ct.dna.lakehouse.sr_raw.ct_gbl_p9a.E_query_conti_copa_qry004_zcp_copa_0004] {

  override def sourceTableSpec: TableSpec[ct.dna.lakehouse.sr_raw.ct_gbl_p9a.E_query_conti_copa_qry004_zcp_copa_0004] =
    sr_raw_query_conti_copa_qry004_zcp_copa_0004

  def sequenceBy: Column = struct(col("_mk_created_at"), col("_lh_id_in_message"))

  lazy val preApplyMapping: Seq[(String, Column)] = Seq(
    ("_mk_org", col("_mk_org")),
    ("_mk_site", col("_mk_site")),
    ("_mk_system", col("_mk_system")),
    ("_mk_instance", col("_mk_instance")),
    ("_mk_partition", col("_mk_partition")),
    ("_mk_file", col("_mk_file")),
    ("_mk_container", col("_mk_container")),
    ("_mk_account", col("_mk_account")),
    ("_mk_created_at", col("_mk_created_at")),
    ("_lh_id_in_message", col("_lh_id_in_message")),
    ("_lh_ingest_warning", col("_lh_ingest_warning")),
    ("_0gn_r3_ssy", col("_0gn_r3_ssy_string")),
    ("_4zcp_copa_documenttype", col("_4zcp_copa_documenttype_string")),
    ("_zdw_btype", col("_zdw_btype_string")),
    ("_zdw_bu", coalesce(col("_zdw_bu_string"), lit(""))),
    ("_zdw_bu_2zdw_bu_string", col("_zdw_bu_2zdw_bu_string")),
    ("_zdw_bu_1zdw_bu_string", col("_zdw_bu_1zdw_bu_string")),
    ("_zdw_ddate", col("_zdw_ddate_string")),
    ("_zdw_dtype", col("_zdw_dtype_string")),
    ("_zdw_gidat", col("_zdw_gidat_string")),
    ("_zdw_ivdat", col("_zdw_ivdat_string")),
    ("_zdw_matl", coalesce(col("_zdw_matl_string"), lit(""))),
    ("_zdw_matl_2zdw_matl_string", col("_zdw_matl_2zdw_matl_string")),
    ("_zdw_matl_1zdw_matl_string", col("_zdw_matl_1zdw_matl_string")),
    ("_zdw_ocdat", col("_zdw_ocdat_string")),
    ("_zdw_pcomp", col("_zdw_pcomp_string")),
    ("_zdw_pcomp_2zdw_pcomp_string", col("_zdw_pcomp_2zdw_pcomp_string")),
    ("_zdw_pcomp_1zdw_pcomp_string", col("_zdw_pcomp_1zdw_pcomp_string")),
    ("_zdw_plant", coalesce(col("_zdw_plant_string"), lit(""))),
    ("_zdw_plant_2zdw_plant_string", col("_zdw_plant_2zdw_plant_string")),
    ("_zdw_plant_1zdw_plant_string", col("_zdw_plant_1zdw_plant_string")),
    ("_zdw_podat", col("_zdw_podat_string")),
    ("_zdw_pscyr", col("_zdw_pscyr_string")),
    ("_zdw_psdat", col("_zdw_psdat_string")),
    ("_zdw_rhdat", col("_zdw_rhdat_string")),
    ("_zdw_rpdat", col("_zdw_rpdat_string")),
    ("_zdw_sdto", col("_zdw_sdto_string")),
    ("_zdw_sdto_2zdw_sdto_string", col("_zdw_sdto_2zdw_sdto_string")),
    ("_zdw_sdto_1zdw_sdto_string", col("_zdw_sdto_1zdw_sdto_string")),
    ("_zdw_seg", coalesce(col("_zdw_seg_string"), lit(""))),
    ("_zdw_seg_2zdw_seg_string", col("_zdw_seg_2zdw_seg_string")),
    ("_zdw_seg_1zdw_seg_string", col("_zdw_seg_1zdw_seg_string")),
    ("_zdw_snpdt", coalesce(col("_zdw_snpdt_string"), lit(""))),
    ("_zdw_tgdat", col("_zdw_tgdat_string")),
    ("_zdw_vrtup", col("_zdw_vrtup_string")),
    ("_zdw_vrtup_2zdw_vrtup_string", col("_zdw_vrtup_2zdw_vrtup_string")),
    ("_zdw_vrtup_1zdw_vrtup_string", col("_zdw_vrtup_1zdw_vrtup_string")),
    ("_1nbg5lj7w7gam9zbngu9lqisu_1nbg5lj7w7gam9zbngu9ltoku_decimal_15_2", col("_1nbg5lj7w7gam9zbngu9lqisu_1nbg5lj7w7gam9zbngu9ltoku_decimal_15_2")),
    ("_1nbg5lj7w7gam9zbngu9lqisu_1nbg5lj7w7gam9zbngu9mwhim_double", col("_1nbg5lj7w7gam9zbngu9lqisu_1nbg5lj7w7gam9zbngu9mwhim_double")),
    ("_1nbg5lj7w7gam9zbngu9lqisu_1nbg5lj7w7gamnplq3t3p38s1_double", col("_1nbg5lj7w7gam9zbngu9lqisu_1nbg5lj7w7gamnplq3t3p38s1_double")),
    ("_1nbg5lj7w7gam9zbngu9lqisu_e6l8g90fhoqyrdsl7kpfvu0l4_decimal_15_3", col("_1nbg5lj7w7gam9zbngu9lqisu_e6l8g90fhoqyrdsl7kpfvu0l4_decimal_15_3"))
  )
}
