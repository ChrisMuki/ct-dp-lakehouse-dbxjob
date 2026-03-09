package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_mysuccess_api_empemployment_E(
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
    customdate43_string: String,
    customdate28_string: String,
    isecrecord_boolean: BoxedBoolean,
    iscontingentworker_boolean: BoxedBoolean,
    customdate10_string: String,
    customdate23_string: String,
    senioritydate_string: String,
    assignmentclass_string: String,
    createddatetime_string: String,
    customdate21_string: String,
    firstdateworked_string: String,
    startdate_string: String,
    customdate25_string: String,
    customdate26_string: String,
    customdate16_string: String,
    enddate_string: String,
    employmentid_string: String,
    createdby_string: String,
    assignmentidexternal_string: String,
    lastdateworked_string: String,
    customdate42_string: String,
    lastmodifiedby_string: String,
    bonuspayexpirationdate_string: String,
    originalstartdate_string: String,
    personidexternal_string: String,
    customstring17_string: String,
    customstring18_string: String,
    customstring16_string: String,
    customdate27_string: String,
    hiringnotcompleted_boolean: BoxedBoolean,
    userid_string: String,
    customdate41_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    lastmodifieddatetime_string: String,
    customdate1_string: String,
    customdate9_string: String,
    customdate24_string: String,
    lastmodifiedon_string: String,
    customstring1_string: String,
    createdon_string: String,
    customdate22_string: String
) extends Entity

object pl_mysuccess_api_empemployment
    extends TableSpec[pl_mysuccess_api_empemployment_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
