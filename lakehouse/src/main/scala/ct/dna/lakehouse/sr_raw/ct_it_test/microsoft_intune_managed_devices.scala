package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class microsoft_intune_managed_devices_E(
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
  managementagent_string: String,
  exchangeaccessstatereason_string: String,
  isencrypted_boolean: BoxedBoolean,
  androidsecuritypatchlevel_string: String,
  exchangelastsuccessfulsyncdatetime_string: String,
  imei_string: String,
  totalstoragespaceinbytes_long: BoxedLong,
  contains_masked_personal_data_boolean: BoxedBoolean,
  lastsyncdatetime_string: String,
  easactivationdatetime_string: String,
  userdisplayname_string: String,
  osversion_string: String,
  devicename_string: String,
  managementstate_string: String,
  managementcertificateexpirationdate_string: String,
  azureaddeviceid_string: String,
  userprincipalname_string: String,
  issupervised_boolean: BoxedBoolean,
  compliancestate_string: String,
  devicecategorydisplayname_string: String,
  requireuserenrollmentapproval_boolean: BoxedBoolean,
  jailbroken_string: String,
  manufacturer_string: String,
  meid_string: String,
  wifimacaddress_string: String,
  id_string: String,
  enrolleddatetime_string: String,
  easdeviceid_string: String,
  manageddevicename_string: String,
  model_string: String,
  userid_string: String,
  freestoragespaceinbytes_long: BoxedLong,
  subscribercarrier_string: String,
  manageddeviceownertype_string: String,
  azureadregistered_boolean: BoxedBoolean,
  compliancegraceperiodexpirationdatetime_string: String,
  deviceenrollmenttype_string: String,
  easactivated_boolean: BoxedBoolean,
  serialnumber_string: String,
  physicalmemoryinbytes_long: BoxedLong,
  phonenumber_string: String,
  partnerreportedthreatstate_string: String,
  exchangeaccessstate_string: String,
  deviceregistrationstate_string: String,
  operatingsystem_string: String,
  emailaddress_string: String
) extends Entity

object microsoft_intune_managed_devices extends TableSpec[microsoft_intune_managed_devices_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
