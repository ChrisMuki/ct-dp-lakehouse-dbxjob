package ct.dna.lakehouse.sr_raw.ct_gbl_p64
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class stas_E(
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
  annam_string: String,
  idpos_string: String,
  dvnam_string: String,
  stvkn_string: String,
  lkenz_string: String,
  mandt_string: String,
  aehlp_string: String,
  stlkn_string: String,
  aenam_string: String,
  aedat_string: String,
  stasz_string: String,
  aennr_string: String,
  techv_string: String,
  lpsrt_string: String,
  dvdat_string: String,
  stlty_string: String,
  datuv_string: String,
  stlnr_string: String,
  stlal_string: String,
  andat_string: String,
  idvar_string: String
) extends Entity

object stas extends TableSpec[stas_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
