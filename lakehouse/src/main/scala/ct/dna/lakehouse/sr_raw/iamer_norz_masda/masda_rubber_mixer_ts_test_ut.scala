package ct.dna.lakehouse.sr_raw.iamer_norz_masda
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class masda_rubber_mixer_ts_test_ut_E(
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
    seconds_from_1980_long: BoxedLong,
    batch_num_long: BoxedLong,
    time_utc_string: String,
    mix_time_s_string: String,
    ml_long: BoxedLong,
    run_num_long: BoxedLong,
    mix_temp_f_long: BoxedLong,
    ram_pos_string: String,
    rpm_spd_string: String,
    mix_energy_kwh_string: String,
    plant_string: String,
    ram_prs_psi_string: String,
    time_local_string: String
) extends Entity

object masda_rubber_mixer_ts_test_ut
    extends TableSpec[masda_rubber_mixer_ts_test_ut_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
