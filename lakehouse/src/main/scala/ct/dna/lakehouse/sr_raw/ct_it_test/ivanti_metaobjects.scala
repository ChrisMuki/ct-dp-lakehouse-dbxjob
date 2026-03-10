package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_metaobjects_E(
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
  def_string: String,
  cht_string: String,
  fra_string: String,
  ptb_string: String,
  esp_string: String,
  chs_string: String,
  objectname_string: String,
  _sourceinstance_string: String,
  objtype_long: BoxedLong,
  jpn_string: String,
  _sourcehost_string: String,
  enu_string: String,
  metaobjects_idn_long: BoxedLong,
  kor_string: String,
  deu_string: String,
  invdata_long: BoxedLong,
  reltbl_long: BoxedLong,
  _sourcedb_string: String,
  _sourceport_long: BoxedLong,
  tblreset_long: BoxedLong,
  rus_string: String,
  coreguid_string: String
) extends Entity

object ivanti_metaobjects extends TableSpec[ivanti_metaobjects_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
