package ct.dna.lakehouse.sr_raw.ct_gbl_epp
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec


@LakehouseEntity
case class t024_E(
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
  telfx_string: String,
  tel_number_string: String,
  yyemail_string: String,
  eknam_string: String,
  mandt_string: String,
  ekgrp_string: String,
  tel_extens_string: String,
  ektel_string: String,
  smtp_addr_string: String,
  ldest_string: String
) extends Entity

object t024 extends TableSpec[t024_E](
  enableChangeDataFeed = true,
  manualClusterBy = None,
  timetravelDays = 35
) with Loaded
