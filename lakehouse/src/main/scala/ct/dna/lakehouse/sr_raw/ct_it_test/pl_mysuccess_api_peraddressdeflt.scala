package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_mysuccess_api_peraddressdeflt_E(
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
    customstring2_string: String,
    address10_string: String,
    enddate_string: String,
    address11_string: String,
    address8_string: String,
    zipcode_string: String,
    createdby_string: String,
    state_string: String,
    address4_string: String,
    county_string: String,
    addresstype_string: String,
    personidexternal_string: String,
    address20_string: String,
    createddatetime_string: String,
    address7_string: String,
    createdon_string: String,
    startdate_string: String,
    address5_string: String,
    empuserssysid_string: String,
    address1_string: String,
    province_string: String,
    attachmentid_string: String,
    customstring13_string: String,
    address2_string: String,
    country_string: String,
    city_string: String,
    lastmodifiedby_string: String,
    address9_string: String,
    address6_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    customstring3_string: String,
    lastmodifieddatetime_string: String,
    address12_string: String,
    address3_string: String,
    lastmodifiedon_string: String,
    customstring1_string: String
) extends Entity

object pl_mysuccess_api_peraddressdeflt
    extends TableSpec[pl_mysuccess_api_peraddressdeflt_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
