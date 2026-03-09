package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class ivanti_consoleusergroup_E(
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
    user_idn_long: BoxedLong,
    _sourcehost_string: String,
    consoleusergroup_idn_long: BoxedLong,
    _sourceinstance_string: String,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    group_idn_long: BoxedLong
) extends Entity

object ivanti_consoleusergroup
    extends TableSpec[ivanti_consoleusergroup_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
