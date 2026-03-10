package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class ivanti_metaattributes_E(
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
  length_long: BoxedLong,
  def_string: String,
  cht_string: String,
  chs_string: String,
  rus_string: String,
  _sourceinstance_string: String,
  _sourcehost_string: String,
  enu_string: String,
  attrname_string: String,
  fra_string: String,
  kor_string: String,
  deu_string: String,
  displaymask_string: String,
  ptb_string: String,
  datatype_string: String,
  esp_string: String,
  _sourcedb_string: String,
  _sourceport_long: BoxedLong,
  jpn_string: String,
  metaattributes_idn_long: BoxedLong,
  coreguid_string: String
) extends Entity

object ivanti_metaattributes extends TableSpec[ivanti_metaattributes_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
