package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_mysuccess_api_payscaletype_E(
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
    mdfsystemstatus_string: String,
    mdfsystemcreateddate_string: String,
    externalname_zh_cn_string: String,
    externalname_de_de_string: String,
    externalname_it_it_string: String,
    createdby_string: String,
    mdfsystemtransactionsequence_string: String,
    externalname_sk_sk_string: String,
    country_string: String,
    externalname_tr_tr_string: String,
    mdfsystementityid_string: String,
    mdfsystemversionid_string: String,
    externalname_defaultvalue_string: String,
    mdfsystemlastmodifiedby_string: String,
    externalname_hu_hu_string: String,
    externalname_localized_string: String,
    mdfsystemrecordid_string: String,
    code_string: String,
    externalname_fr_fr_string: String,
    lastmodifiedby_string: String,
    externalname_nl_nl_string: String,
    mdfsystemeffectivestartdate_string: String,
    mdfsystemobjecttype_string: String,
    mdfsystemrecordstatus_string: String,
    payscaletype_string: String,
    externalname_en_us_string: String,
    externalname_pt_br_string: String,
    mdfsystemeffectiveenddate_string: String,
    mdfsystemlastmodifieddatewithtz_string: String,
    mdfsystemcreatedby_string: String,
    externalname_ro_ro_string: String,
    externalname_cs_cz_string: String,
    externalname_es_mx_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    mdfsystemlastmodifieddate_string: String,
    lastmodifieddatetime_string: String,
    createddatetime_string: String,
    externalname_pt_pt_string: String
) extends Entity

object pl_mysuccess_api_payscaletype
    extends TableSpec[pl_mysuccess_api_payscaletype_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
