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
  enddate_string: String,
  mdfsystemrecordid_string: String,
  name_string: String,
  createdby_string: String,
  name_defaultvalue_string: String,
  contains_masked_personal_data_boolean: BoxedBoolean,
  description_en_us_string: String,
  lastmodifieddatetime_string: String,
  createddatetime_string: String,
  createdon_string: String,
  startdate_string: String,
  description_string: String,
  description_defaultvalue_string: String,
  externalcode_string: String,
  name_localized_string: String,
  lastmodifiedby_string: String,
  name_en_us_string: String,
  entityuuid_string: String,
  lastmodifiedon_string: String,
  status_string: String,
  description_localized_string: String
) extends Entity

object pl_mysuccess_api_fodivision extends TableSpec[pl_mysuccess_api_fodivision_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
