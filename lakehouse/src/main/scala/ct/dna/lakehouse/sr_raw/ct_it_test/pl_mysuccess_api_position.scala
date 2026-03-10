package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class pl_mysuccess_api_position_E(
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
  cust_shorttext_string: String,
  businessunit_string: String,
  targetfte_string: String,
  transactionsequence_string: String,
  externalname_de_de_string: String,
  vacant_boolean: BoxedBoolean,
  createdby_string: String,
  cust_localjobcatalogue_string: String,
  mdfsystementityid_string: String,
  creationsource_string: String,
  legacypositionid_long: BoxedLong,
  effectivestartdate_string: String,
  costcenter_string: String,
  mdfsystemrecordid_string: String,
  paygrade_string: String,
  code_string: String,
  cust_jobfamily_string: String,
  cust_functionalarea_string: String,
  company_string: String,
  division_string: String,
  cust_companygroup_string: String,
  department_string: String,
  cust_rbpforhr_string: String,
  createddate_string: String,
  effectivestatus_string: String,
  lastmodifiedby_string: String,
  mdfsystemobjecttype_string: String,
  jobcode_string: String,
  mdfsystemrecordstatus_string: String,
  standardhours_string: String,
  externalname_en_us_string: String,
  externalname_ro_ro_string: String,
  lastmodifieddatewithtz_string: String,
  mdfsystemoptimisticlockuuid_string: String,
  contains_masked_personal_data_boolean: BoxedBoolean,
  lastmodifieddatetime_string: String,
  lastmodifieddate_string: String,
  location_string: String,
  effectiveenddate_string: String,
  createddatetime_string: String,
  externalname_defaultvalue_string: String,
  multipleincumbentsallowed_boolean: BoxedBoolean,
  externalname_localized_string: String,
  positioncontrolled_boolean: BoxedBoolean,
  parentposition_string: String
) extends Entity

object pl_mysuccess_api_position extends TableSpec[pl_mysuccess_api_position_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
