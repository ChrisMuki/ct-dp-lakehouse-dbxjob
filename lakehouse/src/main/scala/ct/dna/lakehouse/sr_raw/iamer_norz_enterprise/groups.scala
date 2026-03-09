package ct.dna.lakehouse.sr_raw.iamer_norz_enterprise
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class groups_E(
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
    ou_division_string: String,
    description_string: String,
    intdslocalsf_string: String,
    grouptype_string: String,
    intdscentralof_string: String,
    gidnumber_string: String,
    objectcategory_string: String,
    load_dt_string: String,
    rec_src_string: String,
    ou_location_code_string: String,
    ou_string: String,
    ou_subsidiary_division_string: String,
    intdscentralsf_string: String,
    ou_region_string: String,
    cn_string: String,
    ou_subsidiary_string: String,
    objectsid_string: String,
    samaccountname_string: String,
    ou_location_name_string: String,
    name_string: String,
    canonicalname_string: String,
    id_long: BoxedLong,
    samaccounttype_string: String,
    instancetype_string: String,
    intdslocalof_string: String,
    objectguid_string: String,
    distinguishedname_string: String
) extends Entity

object groups
    extends TableSpec[groups_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
