package ct.dna.lakehouse.sr_raw.iamer_norz_enterprise
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class users_E(
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
    departmentnumber_string: String,
    givenname_string: String,
    userworkstations_string: String,
    ou_division_string: String,
    badpasswordtime_string: String,
    ou_subsidiary_division_string: String,
    employeeid_string: String,
    lastlogon_string: String,
    ou_string: String,
    ou_region_string: String,
    sn_string: String,
    useraccountcontrol_string: String,
    cn_string: String,
    ou_subsidiary_string: String,
    accountexpires_string: String,
    global_extensionattribute21_string: String,
    samaccountname_string: String,
    ou_location_name_string: String,
    canonicalname_string: String,
    title_string: String,
    userprincipalname_string: String,
    global_extensionattribute11_string: String,
    employeetype_string: String,
    global_extensionattribute22_string: String,
    company_string: String,
    division_string: String,
    department_string: String,
    global_extensionattribute16_string: String,
    global_extensionattribute27_string: String,
    id_long: BoxedLong,
    pwdlastset_string: String,
    global_extensionattribute12_string: String,
    badpwdcount_string: String,
    co_string: String,
    createtimestamp_string: String,
    global_extensionattribute24_string: String,
    l_string: String,
    employeenumber_string: String,
    objectguid_string: String,
    distinguishedname_string: String,
    load_dt_string: String,
    mail_string: String,
    rec_src_string: String,
    global_extensionattribute28_string: String,
    displayname_string: String,
    ou_location_code_string: String,
    global_extensionattribute2_string: String,
    msexchassistantname_string: String,
    global_extensionattribute30_string: String,
    modifytimestamp_string: String
) extends Entity

object users
    extends TableSpec[users_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
