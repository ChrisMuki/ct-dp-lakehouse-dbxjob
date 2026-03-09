package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_mysuccess_api_perpersonal_E(
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
    startdate_string: String,
    secondlastname_string: String,
    enddate_string: String,
    secondnationality_string: String,
    attachmentid_string: String,
    lastname_string: String,
    nationality_string: String,
    firstnamealt1_string: String,
    lastmodifiedby_string: String,
    personidexternal_string: String,
    preferredname_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    salutation_string: String,
    createdon_string: String,
    lastnamealt1_string: String,
    firstname_string: String,
    nameprefix_string: String,
    title_string: String,
    middlename_string: String,
    nativepreferredlang_string: String,
    createdby_string: String,
    isoverridden_boolean: BoxedBoolean,
    gender_string: String,
    customstring3_string: String,
    lastmodifieddatetime_string: String,
    suffix_string: String,
    since_string: String,
    createddatetime_string: String,
    initials_string: String,
    displayname_string: String,
    birthname_string: String,
    maritalstatus_string: String,
    secondtitle_string: String,
    formalname_string: String,
    lastmodifiedon_string: String,
    customstring1_string: String
) extends Entity

object pl_mysuccess_api_perpersonal
    extends TableSpec[pl_mysuccess_api_perpersonal_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
