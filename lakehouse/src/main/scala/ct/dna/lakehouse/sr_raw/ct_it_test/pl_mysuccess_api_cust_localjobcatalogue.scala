package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class pl_mysuccess_api_cust_localjobcatalogue_E(
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
  externalcode_string: String,
  mdfsystemrecordid_string: String,
  externalname_de_de_string: String,
  createdby_string: String,
  mdfsystemtransactionsequence_string: String,
  lastmodifiedby_string: String,
  externalname_en_us_string: String,
  mdfsystementityid_string: String,
  mdfsystemeffectiveenddate_string: String,
  contains_masked_personal_data_boolean: BoxedBoolean,
  createddatetime_string: String,
  effectivestartdate_string: String,
  externalname_defaultvalue_string: String,
  mdfsystemlastmodifiedby_string: String,
  externalname_localized_string: String,
  mdfsystemobjecttype_string: String,
  mdfsystemrecordstatus_string: String,
  mdfsystemlastmodifieddatewithtz_string: String,
  mdfsystemcreatedby_string: String,
  mdfsystemlastmodifieddate_string: String,
  lastmodifieddatetime_string: String
) extends Entity

object pl_mysuccess_api_cust_localjobcatalogue extends TableSpec[pl_mysuccess_api_cust_localjobcatalogue_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
