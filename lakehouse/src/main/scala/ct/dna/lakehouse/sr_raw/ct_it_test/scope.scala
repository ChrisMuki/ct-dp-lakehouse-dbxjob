package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class scope_E(
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
    name_string: String,
    parentscope_idn_long: BoxedLong,
    lastresolution_string: String,
    directory_idn_long: BoxedLong,
    bnf_string: String,
    scope_idn_long: BoxedLong,
    _sourcehost_string: String,
    description_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    scopetype_long: BoxedLong,
    _sourceinstance_string: String
) extends Entity

object scope
    extends TableSpec[scope_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
