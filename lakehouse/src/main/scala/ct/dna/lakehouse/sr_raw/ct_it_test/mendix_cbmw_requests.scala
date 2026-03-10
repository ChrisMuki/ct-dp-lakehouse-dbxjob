package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class mendix_cbmw_requests_E(
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
  amountofworkdays_long: BoxedLong,
  destinationcountry_string: String,
  state_string: String,
  createddate_string: String,
  _requestoruid_string: String,
  denyreason_string: String,
  requestdate_string: String,
  firstworkingday_string: String,
  lastworkingday_string: String,
  departurecountry_string: String,
  code_string: String,
  reviewdate_string: String,
  contains_masked_personal_data_boolean: BoxedBoolean
) extends Entity

object mendix_cbmw_requests extends TableSpec[mendix_cbmw_requests_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
