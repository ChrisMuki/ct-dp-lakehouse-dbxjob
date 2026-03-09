package ct.dna.lakehouse.sr_raw.ct_it_test
import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity._
import ct.dna.lakehouse.core.model.TableSpec

@LakehouseEntity
case class bios_E(
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
    installeddate_string: String,
    firmwarerevision_string: String,
    assettag_string: String,
    hwattrs_string: String,
    univrom_string: String,
    monitorserial_string: String,
    smbiosver_string: String,
    romfamily_string: String,
    _sourceinstance_string: String,
    ideserial_string: String,
    _sourcehost_string: String,
    biosdate_string: String,
    sysmodnum_string: String,
    uefienabled_string: String,
    boardrevlevel_string: String,
    monitorvendorid_string: String,
    machid_string: String,
    systemname_string: String,
    monmanufacturer_string: String,
    bootromver_string: String,
    romsize_long: BoxedLong,
    manufacturer_string: String,
    systemserial_string: String,
    monitormfgweek_long: BoxedLong,
    securebootenabled_string: String,
    monitormfgyear_long: BoxedLong,
    swbundle_string: String,
    systemmodel_string: String,
    monitorsize_string: String,
    model_string: String,
    servicetag_string: String,
    idbytes_string: String,
    computer_idn_long: BoxedLong,
    sysmanufacturer_string: String,
    romfilever_string: String,
    _sourcedb_string: String,
    romversion_string: String,
    _sourceport_long: BoxedLong,
    copyright_string: String,
    serialnum_string: String,
    ordnum_string: String,
    monitormodel_string: String,
    coreguid_string: String
) extends Entity

object bios
    extends TableSpec[bios_E](
      enableChangeDataFeed = true,
      manualClusterBy = None,
      timetravelDays = 35
    )
    with Loaded
