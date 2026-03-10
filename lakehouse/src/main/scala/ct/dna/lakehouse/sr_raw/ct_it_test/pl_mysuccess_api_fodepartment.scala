package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class pl_mysuccess_api_fodepartment_E(
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
  startdate_string: String,
  name_pt_pt_string: String,
  mdfsystemrecordid_string: String,
  name_es_mx_string: String,
  name_defaultvalue_string: String,
  name_cs_cz_string: String,
  contains_masked_personal_data_boolean: BoxedBoolean,
  lastmodifieddatetime_string: String,
  createddatetime_string: String,
  name_nl_nl_string: String,
  status_string: String,
  createdon_string: String,
  name_it_it_string: String,
  enddate_string: String,
  parent_string: String,
  externalcode_string: String,
  name_localized_string: String,
  name_ro_ro_string: String,
  name_string: String,
  name_zh_cn_string: String,
  name_pt_br_string: String,
  cust_todivisionprop_string: String,
  createdby_string: String,
  name_sk_sk_string: String,
  name_tr_tr_string: String,
  name_hu_hu_string: String,
  cust_reportsdirecttobu_boolean: BoxedBoolean,
  lastmodifiedby_string: String,
  name_en_us_string: String,
  name_de_de_string: String,
  name_fr_fr_string: String,
  entityuuid_string: String,
  lastmodifiedon_string: String
) extends Entity

object pl_mysuccess_api_fodepartment extends TableSpec[pl_mysuccess_api_fodepartment_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
