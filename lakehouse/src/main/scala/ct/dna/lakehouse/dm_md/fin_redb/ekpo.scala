package ct.dna.lakehouse.dm_md.fin_redb

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.sr.ct_gbl_e32
import ct.dna.lakehouse.sr.ct_gbl_epp
import ct.dna.lakehouse.sr.ct_gbl_ghp
import ct.dna.lakehouse.sr.ct_gbl_p12
import ct.dna.lakehouse.sr.ct_gbl_p24
import ct.dna.lakehouse.sr.ct_gbl_p43
import ct.dna.lakehouse.sr.ct_gbl_p61
import ct.dna.lakehouse.sr.ct_gbl_p64
import ct.dna.lakehouse.sr.ct_gbl_p69
import ct.dna.lakehouse.sr.ct_gbl_p73
import ct.dna.lakehouse.sr.ct_gbl_p77
import ct.dna.lakehouse.sr.ct_gbl_p85
import ct.dna.lakehouse.sr.ct_gbl_pbr
import ct.dna.lakehouse.sr.ct_gbl_psp
import org.apache.spark.sql.functions._

case class DmEkpo(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK ebeln: String,
    @PK ebelp: String,
    loekz: String,
    txz01: String,
    matnr: String,
    ematn: String,
    bukrs: String,
    werks: String,
    matkl: String,
    menge: Double,
    meins: String,
    netpr: Double,
    peinh: Double,
    netwr: Double,
    knttp: String,
    wepos: String,
    kunnr: String,
    txjcd: String,
    inco1: String,
    inco2: String,
    ltsnr: String,
    attyp: String,
    bwtar: String,
    pstyp: String
) extends Entity

object ekpo extends TableSpec[DmEkpo] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.ekpo,
      ct_gbl_epp.ekpo,
      ct_gbl_ghp.ekpo,
      ct_gbl_p12.ekpo,
      ct_gbl_p24.ekpo,
      ct_gbl_p43.ekpo,
      ct_gbl_p61.ekpo,
      ct_gbl_p64.ekpo,
      ct_gbl_p69.ekpo,
      ct_gbl_p73.ekpo,
      ct_gbl_p77.ekpo,
      ct_gbl_p85.ekpo,
      ct_gbl_pbr.ekpo,
      ct_gbl_psp.ekpo
    )

  /** Value columns this transformation actually reads from each source feed.
    *
    * Passed explicitly to `lastOfKey(...)` so the projection is restricted to these columns instead of defaulting to every value column declared by the
    * per-source spec.
    */
  private val consumedValueColumnNames: Seq[String] = Seq(
    "_mk_system",
    "_mk_instance",
    "loekz",
    "txz01",
    "matnr",
    "ematn",
    "bukrs",
    "werks",
    "matkl",
    "menge",
    "meins",
    "netpr",
    "peinh",
    "netwr",
    "knttp",
    "wepos",
    "kunnr",
    "txjcd",
    "inco1",
    "inco2",
    "ltsnr",
    "attyp",
    "bwtar",
    "pstyp"
  )

  /** Project a per-feed `lastOfKey()` slice down to the columns the merge actually consumes:
    *
    *   - the four PK columns
    *   - all `DmEkpo` value columns passed through unchanged
    *   - the `_change_type` discriminator (used by the merge branches to distinguish UPSERT vs DELETE)
    *
    * Doing the projection up-front strips the rest of the CDF metadata (`_commit_version`, `_commit_timestamp`) so the unioned source DataFrame stays
    * schema-aligned across all 14 sources and so the column maps below can address columns positionally without smuggling extra fields into the merge plan.
    */
  private def projectChanges(lastOfKey: org.apache.spark.sql.DataFrame): org.apache.spark.sql.DataFrame =
    lastOfKey
      .filter(col("ebeln").isNotNull && col("ebelp").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("ebeln"),
        col("ebelp"),
        col("loekz"),
        col("txz01"),
        col("matnr"),
        col("ematn"),
        col("bukrs"),
        col("werks"),
        col("matkl"),
        col("menge"),
        col("meins"),
        col("netpr"),
        col("peinh"),
        col("netwr"),
        col("knttp"),
        col("wepos"),
        col("kunnr"),
        col("txjcd"),
        col("inco1"),
        col("inco2"),
        col("ltsnr"),
        col("attyp"),
        col("bwtar"),
        col("pstyp"),
        col("_change_type")
      )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    if (feeds.forall { case (_, f) => f.isUnchanged }) return Result.NoChanges

    val snapshotSystems: Set[String] = feeds
      .collect {
        case (_, feed) if feed.isSnapshot =>
          // Project only `_mk_system` instead of every value column — defaulting to all value columns would
          // re-project bad sr-spec columns (see `consumedValueColumnNames` doc above).
          feed.snapshot(Seq("_mk_system")).select(C_ekpo._mk_system).limit(1).collect().map(_.getString(0)).toSet
      }
      .flatten
      .toSet

    val grouped = feeds
      .map { case (_, feed) => projectChanges(feed.lastByKey(consumedValueColumnNames)) }
      .reduce(_.unionByName(_))

    val target = C_ekpo.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val ebeln = ColumnWithName("ebeln").withDFAlias("source")
      val ebelp = ColumnWithName("ebelp").withDFAlias("source")
      val loekz = ColumnWithName("loekz").withDFAlias("source")
      val txz01 = ColumnWithName("txz01").withDFAlias("source")
      val matnr = ColumnWithName("matnr").withDFAlias("source")
      val ematn = ColumnWithName("ematn").withDFAlias("source")
      val bukrs = ColumnWithName("bukrs").withDFAlias("source")
      val werks = ColumnWithName("werks").withDFAlias("source")
      val matkl = ColumnWithName("matkl").withDFAlias("source")
      val menge = ColumnWithName("menge").withDFAlias("source")
      val meins = ColumnWithName("meins").withDFAlias("source")
      val netpr = ColumnWithName("netpr").withDFAlias("source")
      val peinh = ColumnWithName("peinh").withDFAlias("source")
      val netwr = ColumnWithName("netwr").withDFAlias("source")
      val knttp = ColumnWithName("knttp").withDFAlias("source")
      val wepos = ColumnWithName("wepos").withDFAlias("source")
      val kunnr = ColumnWithName("kunnr").withDFAlias("source")
      val txjcd = ColumnWithName("txjcd").withDFAlias("source")
      val inco1 = ColumnWithName("inco1").withDFAlias("source")
      val inco2 = ColumnWithName("inco2").withDFAlias("source")
      val ltsnr = ColumnWithName("ltsnr").withDFAlias("source")
      val attyp = ColumnWithName("attyp").withDFAlias("source")
      val bwtar = ColumnWithName("bwtar").withDFAlias("source")
      val pstyp = ColumnWithName("pstyp").withDFAlias("source")
      val _change_type = ColumnWithName("_change_type").withDFAlias("source")
    }

    val isDelete = source._change_type === "delete"
    // Framework emits `_change_type` ∈ {`insert`, `update`, `delete`}; anything not a delete is an upsert.
    val isUpsert = source._change_type =!= "delete"

    table
      .merge(
        grouped,
        source._mk_system === target._mk_system &&
          source._mk_instance === target._mk_instance &&
          source.ebeln === target.ebeln &&
          source.ebelp === target.ebelp
      )
      /* CDF DELETE for an existing key → drop the row. */
      .whenMatched(isDelete)
      .delete()
      /* CDF UPSERT for an existing key → overwrite all value columns. */
      .whenMatched()
      .update(
        C_ekpo.loekz -> source.loekz,
        C_ekpo.txz01 -> source.txz01,
        C_ekpo.matnr -> source.matnr,
        C_ekpo.ematn -> source.ematn,
        C_ekpo.bukrs -> source.bukrs,
        C_ekpo.werks -> source.werks,
        C_ekpo.matkl -> source.matkl,
        C_ekpo.menge -> source.menge,
        C_ekpo.meins -> source.meins,
        C_ekpo.netpr -> source.netpr,
        C_ekpo.peinh -> source.peinh,
        C_ekpo.netwr -> source.netwr,
        C_ekpo.knttp -> source.knttp,
        C_ekpo.wepos -> source.wepos,
        C_ekpo.kunnr -> source.kunnr,
        C_ekpo.txjcd -> source.txjcd,
        C_ekpo.inco1 -> source.inco1,
        C_ekpo.inco2 -> source.inco2,
        C_ekpo.ltsnr -> source.ltsnr,
        C_ekpo.attyp -> source.attyp,
        C_ekpo.bwtar -> source.bwtar,
        C_ekpo.pstyp -> source.pstyp
      )
      /* New PK with an UPSERT event → insert. (A CDF DELETE for a key the target never knew about is a no-op.) */
      .whenNotMatched(isUpsert)
      .insert(
        C_ekpo._mk_system -> source._mk_system,
        C_ekpo._mk_instance -> source._mk_instance,
        C_ekpo.ebeln -> source.ebeln,
        C_ekpo.ebelp -> source.ebelp,
        C_ekpo.loekz -> source.loekz,
        C_ekpo.txz01 -> source.txz01,
        C_ekpo.matnr -> source.matnr,
        C_ekpo.ematn -> source.ematn,
        C_ekpo.bukrs -> source.bukrs,
        C_ekpo.werks -> source.werks,
        C_ekpo.matkl -> source.matkl,
        C_ekpo.menge -> source.menge,
        C_ekpo.meins -> source.meins,
        C_ekpo.netpr -> source.netpr,
        C_ekpo.peinh -> source.peinh,
        C_ekpo.netwr -> source.netwr,
        C_ekpo.knttp -> source.knttp,
        C_ekpo.wepos -> source.wepos,
        C_ekpo.kunnr -> source.kunnr,
        C_ekpo.txjcd -> source.txjcd,
        C_ekpo.inco1 -> source.inco1,
        C_ekpo.inco2 -> source.inco2,
        C_ekpo.ltsnr -> source.ltsnr,
        C_ekpo.attyp -> source.attyp,
        C_ekpo.bwtar -> source.bwtar,
        C_ekpo.pstyp -> source.pstyp
      )
      /* Snapshot cleanup: target rows from a snapshotted system that are absent from the source
         no longer exist upstream and must be deleted. `isin` over an empty Seq is always false,
         so this branch is a no-op when no snapshot feed is present. */
      .whenNotMatchedBySource(target._mk_system.isin(snapshotSystems.toSeq: _*))
      .delete()
      .execute()
  }

  override def validate(): Unit = {
    super.validate()

    // All source ekpo tables must share the same key columns as the canonical sr_ekpo schema (ct_gbl_e32),
    // and must expose at least the value columns this transformation actually consumes.
    val canonicalKeys = ct_gbl_e32.ekpo.keyColumnNames.toSet
    val canonicalValuesRequired = consumedValueColumnNames.toSet

    sourceTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.ekpo.keyColumnNames}"
      )
      val missingValues = canonicalValuesRequired -- spec.valueColumnNames.toSet
      require(
        missingValues.isEmpty,
        s"Source table '$spec' is missing value columns required by transformation: ${missingValues.mkString(", ")}"
      )
    }
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_ekpo(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val ebeln: ColumnWithName = ColumnWithName(prefix, "ebeln")
  val ebelp: ColumnWithName = ColumnWithName(prefix, "ebelp")
  val loekz: ColumnWithName = ColumnWithName(prefix, "loekz")
  val txz01: ColumnWithName = ColumnWithName(prefix, "txz01")
  val matnr: ColumnWithName = ColumnWithName(prefix, "matnr")
  val ematn: ColumnWithName = ColumnWithName(prefix, "ematn")
  val bukrs: ColumnWithName = ColumnWithName(prefix, "bukrs")
  val werks: ColumnWithName = ColumnWithName(prefix, "werks")
  val matkl: ColumnWithName = ColumnWithName(prefix, "matkl")
  val menge: ColumnWithName = ColumnWithName(prefix, "menge")
  val meins: ColumnWithName = ColumnWithName(prefix, "meins")
  val netpr: ColumnWithName = ColumnWithName(prefix, "netpr")
  val peinh: ColumnWithName = ColumnWithName(prefix, "peinh")
  val netwr: ColumnWithName = ColumnWithName(prefix, "netwr")
  val knttp: ColumnWithName = ColumnWithName(prefix, "knttp")
  val wepos: ColumnWithName = ColumnWithName(prefix, "wepos")
  val kunnr: ColumnWithName = ColumnWithName(prefix, "kunnr")
  val txjcd: ColumnWithName = ColumnWithName(prefix, "txjcd")
  val inco1: ColumnWithName = ColumnWithName(prefix, "inco1")
  val inco2: ColumnWithName = ColumnWithName(prefix, "inco2")
  val ltsnr: ColumnWithName = ColumnWithName(prefix, "ltsnr")
  val attyp: ColumnWithName = ColumnWithName(prefix, "attyp")
  val bwtar: ColumnWithName = ColumnWithName(prefix, "bwtar")
  val pstyp: ColumnWithName = ColumnWithName(prefix, "pstyp")
}

object C_ekpo extends C_ekpo("") {
  def withDFAlias(alias: String): C_ekpo = new C_ekpo(alias)
  def withoutDFAlias: C_ekpo = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_ekpo = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
