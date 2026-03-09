package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class pl_mysuccess_api_trenddata_sysoverallperformance_E(
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
    max_string: String,
    source_string: String,
    enddate_string: String,
    label_string: String,
    name_string: String,
    lastmodified_string: String,
    id_string: String,
    contains_masked_personal_data_boolean: BoxedBoolean,
    min_string: String,
    rating_string: String,
    startdate_string: String,
    description_string: String,
    userid_string: String,
    module_string: String
) extends Entity

object pl_mysuccess_api_trenddata_sysoverallperformance
    extends TableSpec[pl_mysuccess_api_trenddata_sysoverallperformance_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
