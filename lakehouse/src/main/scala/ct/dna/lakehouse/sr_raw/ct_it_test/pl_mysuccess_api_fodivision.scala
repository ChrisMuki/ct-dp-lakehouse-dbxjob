package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_mysuccess_api_fodivision_E(
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
    cust_tobusinessunitprop_string: String,
    description_pt_br_string: String,
    mdfsystemrecordid_string: String,
    name_defaultvalue_string: String,
    description_fr_fr_string: String,
    description_es_mx_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    description_en_us_string: String,
    lastmodifieddatetime_string: String,
    createddatetime_string: String,
    description_it_it_string: String,
    createdon_string: String,
    startdate_string: String,
    description_string: String,
    entityoid_string: String,
    description_cs_cz_string: String,
    description_pt_pt_string: String,
    name_pt_pt_string: String,
    name_it_it_string: String,
    enddate_string: String,
    description_defaultvalue_string: String,
    externalcode_string: String,
    name_localized_string: String,
    name_ro_ro_string: String,
    name_string: String,
    name_zh_cn_string: String,
    name_pt_br_string: String,
    description_de_de_string: String,
    createdby_string: String,
    name_sk_sk_string: String,
    description_sk_sk_string: String,
    name_es_mx_string: String,
    name_tr_tr_string: String,
    name_hu_hu_string: String,
    lastmodifiedby_string: String,
    name_en_us_string: String,
    name_cs_cz_string: String,
    description_hu_hu_string: String,
    name_nl_nl_string: String,
    description_zh_cn_string: String,
    name_de_de_string: String,
    name_fr_fr_string: String,
    entityuuid_string: String,
    lastmodifiedon_string: String,
    description_tr_tr_string: String,
    status_string: String,
    description_localized_string: String,
    description_nl_nl_string: String,
    description_ro_ro_string: String
) extends Entity

object pl_mysuccess_api_fodivision
    extends TableSpec[pl_mysuccess_api_fodivision_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
