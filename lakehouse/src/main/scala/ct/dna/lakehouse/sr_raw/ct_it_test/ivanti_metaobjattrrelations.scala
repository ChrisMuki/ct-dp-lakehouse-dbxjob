package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ivanti_metaobjattrrelations_E(
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
    uniqueattr_long: BoxedLong,
    rollup_long: BoxedLong,
    showinsummary_long: BoxedLong,
    disptype_long: BoxedLong,
    am_sections_idn_long: BoxedLong,
    disporder_long: BoxedLong,
    identityattribute_long: BoxedLong,
    relationshiptype_long: BoxedLong,
    scan_long: BoxedLong,
    foreignrelationship_idn_long: BoxedLong,
    metaobjattrrelations_idn_long: BoxedLong,
    parenttable_string: String,
    metaattributes_idn_long: BoxedLong,
    _sourcehost_string: String,
    requiredattr_long: BoxedLong,
    trackhist_long: BoxedLong,
    tablename_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    inputmask_string: String,
    pkey_long: BoxedLong,
    iskey_long: BoxedLong,
    metaobjrelations_idn_long: BoxedLong,
    _sourceinstance_string: String,
    coreguid_string: String
) extends Entity

object ivanti_metaobjattrrelations
    extends TableSpec[ivanti_metaobjattrrelations_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
