package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class cvdetected_E(
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
    patch_string: String,
    found_string: String,
    cvdetected_idn_long: BoxedLong,
    numinstalltries_long: BoxedLong,
    patchdetected_long: BoxedLong,
    datedetected_string: String,
    patchinstallsucceeded_long: BoxedLong,
    computer_idn_long: BoxedLong,
    expected_string: String,
    reason_string: String,
    _sourceinstance_string: String,
    _sourcehost_string: String,
    vulnerability_idn_long: BoxedLong,
    _sourcedb_string: String,
    _sourceport_long: BoxedLong,
    coreguid_string: String
) extends Entity

object cvdetected
    extends TableSpec[cvdetected_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
