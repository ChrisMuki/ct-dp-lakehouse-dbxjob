package ct.dna.lakehouse.sr_raw.iamer_norz_enterprise
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class gmsa_E(
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
    description_string: String,
    useraccountcontrol_string: String,
    cn_string: String,
    name_string: String,
    dnshostname_string: String,
    lastlogontimestamp_string: String,
    id_long: BoxedLong,
    createtimestamp_string: String,
    load_dt_string: String,
    rec_src_string: String,
    modifytimestamp_string: String,
    objectguid_string: String,
    distinguishedname_string: String
) extends Entity

object gmsa
    extends TableSpec[gmsa_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
