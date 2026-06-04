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

case class DmEkbe(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK gjahr: String,
    @PK vgabe: String,
    @PK zekkn: String,
    @PK belnr: String,
    @PK ebeln: String,
    @PK ebelp: String,
    @PK buzei: String,
    bwart: String,
    budat: String,
    menge: Double,
    dmbtr: Double,
    wrbtr: Double,
    waers: String,
    shkzg: String,
    elikz: String,
    xblnr: String,
    reewr: Double,
    lsmng: Double,
    lsmeh: String,
    areww: Double,
    hswae: String,
    bldat: String
) extends Entity

object ekbe extends TableSpec[DmEkbe] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.ekbe,
      ct_gbl_epp.ekbe,
      ct_gbl_ghp.ekbe,
      ct_gbl_p12.ekbe,
      ct_gbl_p24.ekbe,
      ct_gbl_p43.ekbe,
      ct_gbl_p61.ekbe,
      ct_gbl_p64.ekbe,
      ct_gbl_p69.ekbe,
      ct_gbl_p73.ekbe,
      ct_gbl_p77.ekbe,
      ct_gbl_p85.ekbe,
      ct_gbl_pbr.ekbe,
      ct_gbl_psp.ekbe
    )

  /** Value columns this transformation actually reads from each source feed.
    *
    * Passed explicitly to `lastOfKey(...)` so the projection is restricted to these columns instead of defaulting to every value column declared by the
    * per-source spec.
    */
  private val consumedValueColumnNames: Seq[String] = Seq(
    "_mk_system",
    "_mk_instance",
    "bwart",
    "budat",
    "menge",
    "dmbtr",
    "wrbtr",
    "waers",
    "shkzg",
    "elikz",
    "xblnr",
    "reewr",
    "lsmng",
    "lsmeh",
    "areww",
    "hswae",
    "bldat"
  )

  /** Project a per-feed `lastOfKey()` slice down to the columns the merge actually consumes:
    *
    *   - the nine PK columns
    *   - all `DmEkbe` value columns, with date formatting applied to `budat` and `bldat` (yyyyMMdd)
    *   - debit/credit sign inversion on `dmbtr`: when `shkzg = "H"` then `dmbtr * -1`, otherwise `dmbtr`
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
        col("gjahr"),
        col("vgabe"),
        col("zekkn"),
        col("belnr"),
        col("ebeln"),
        col("ebelp"),
        col("buzei"),
        col("bwart"),
        date_format(to_date(when(col("budat") === "00000000", lit(null)).otherwise(col("budat")), "yyyyMMdd"), "yyyyMMdd").as("budat"),
        col("menge"),
        when(col("shkzg") === "H", -col("dmbtr")).otherwise(col("dmbtr")).as("dmbtr"),
        col("wrbtr"),
        col("waers"),
        col("shkzg"),
        col("elikz"),
        col("xblnr"),
        col("reewr"),
        col("lsmng"),
        col("lsmeh"),
        col("areww"),
        col("hswae"),
        date_format(to_date(when(col("bldat") === "00000000", lit(null)).otherwise(col("bldat")), "yyyyMMdd"), "yyyyMMdd").as("bldat"),
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
          feed.snapshot(Seq("_mk_system")).select(C_ekbe._mk_system).limit(1).collect().map(_.getString(0)).toSet
      }
      .flatten
      .toSet

    val grouped = feeds
      .map { case (_, feed) => projectChanges(feed.lastByKey(consumedValueColumnNames)) }
      .reduce(_.unionByName(_))

    val target = C_ekbe.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val gjahr = ColumnWithName("gjahr").withDFAlias("source")
      val vgabe = ColumnWithName("vgabe").withDFAlias("source")
      val zekkn = ColumnWithName("zekkn").withDFAlias("source")
      val belnr = ColumnWithName("belnr").withDFAlias("source")
      val ebeln = ColumnWithName("ebeln").withDFAlias("source")
      val ebelp = ColumnWithName("ebelp").withDFAlias("source")
      val buzei = ColumnWithName("buzei").withDFAlias("source")
      val bwart = ColumnWithName("bwart").withDFAlias("source")
      val budat = ColumnWithName("budat").withDFAlias("source")
      val menge = ColumnWithName("menge").withDFAlias("source")
      val dmbtr = ColumnWithName("dmbtr").withDFAlias("source")
      val wrbtr = ColumnWithName("wrbtr").withDFAlias("source")
      val waers = ColumnWithName("waers").withDFAlias("source")
      val shkzg = ColumnWithName("shkzg").withDFAlias("source")
      val elikz = ColumnWithName("elikz").withDFAlias("source")
      val xblnr = ColumnWithName("xblnr").withDFAlias("source")
      val reewr = ColumnWithName("reewr").withDFAlias("source")
      val lsmng = ColumnWithName("lsmng").withDFAlias("source")
      val lsmeh = ColumnWithName("lsmeh").withDFAlias("source")
      val areww = ColumnWithName("areww").withDFAlias("source")
      val hswae = ColumnWithName("hswae").withDFAlias("source")
      val bldat = ColumnWithName("bldat").withDFAlias("source")
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
          source.gjahr === target.gjahr &&
          source.vgabe === target.vgabe &&
          source.zekkn === target.zekkn &&
          source.belnr === target.belnr &&
          source.ebeln === target.ebeln &&
          source.ebelp === target.ebelp &&
          source.buzei === target.buzei
      )
      /* CDF DELETE for an existing key → drop the row. */
      .whenMatched(isDelete)
      .delete()
      /* CDF UPSERT for an existing key → overwrite all value columns. */
      .whenMatched()
      .update(
        C_ekbe.bwart -> source.bwart,
        C_ekbe.budat -> source.budat,
        C_ekbe.menge -> source.menge,
        C_ekbe.dmbtr -> source.dmbtr,
        C_ekbe.wrbtr -> source.wrbtr,
        C_ekbe.waers -> source.waers,
        C_ekbe.shkzg -> source.shkzg,
        C_ekbe.elikz -> source.elikz,
        C_ekbe.xblnr -> source.xblnr,
        C_ekbe.reewr -> source.reewr,
        C_ekbe.lsmng -> source.lsmng,
        C_ekbe.lsmeh -> source.lsmeh,
        C_ekbe.areww -> source.areww,
        C_ekbe.hswae -> source.hswae,
        C_ekbe.bldat -> source.bldat
      )
      /* New PK with an UPSERT event → insert. (A CDF DELETE for a key the target never knew about is a no-op.) */
      .whenNotMatched(isUpsert)
      .insert(
        C_ekbe._mk_system -> source._mk_system,
        C_ekbe._mk_instance -> source._mk_instance,
        C_ekbe.gjahr -> source.gjahr,
        C_ekbe.vgabe -> source.vgabe,
        C_ekbe.zekkn -> source.zekkn,
        C_ekbe.belnr -> source.belnr,
        C_ekbe.ebeln -> source.ebeln,
        C_ekbe.ebelp -> source.ebelp,
        C_ekbe.buzei -> source.buzei,
        C_ekbe.bwart -> source.bwart,
        C_ekbe.budat -> source.budat,
        C_ekbe.menge -> source.menge,
        C_ekbe.dmbtr -> source.dmbtr,
        C_ekbe.wrbtr -> source.wrbtr,
        C_ekbe.waers -> source.waers,
        C_ekbe.shkzg -> source.shkzg,
        C_ekbe.elikz -> source.elikz,
        C_ekbe.xblnr -> source.xblnr,
        C_ekbe.reewr -> source.reewr,
        C_ekbe.lsmng -> source.lsmng,
        C_ekbe.lsmeh -> source.lsmeh,
        C_ekbe.areww -> source.areww,
        C_ekbe.hswae -> source.hswae,
        C_ekbe.bldat -> source.bldat
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

    // All source ekbe tables must share the same key columns as the canonical sr_ekbe schema (ct_gbl_e32),
    // and must expose at least the value columns this transformation actually consumes.
    val canonicalKeys = ct_gbl_e32.ekbe.keyColumnNames.toSet
    val canonicalValuesRequired = consumedValueColumnNames.toSet

    sourceTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.ekbe.keyColumnNames}"
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

sealed class C_ekbe(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val gjahr: ColumnWithName = ColumnWithName(prefix, "gjahr")
  val vgabe: ColumnWithName = ColumnWithName(prefix, "vgabe")
  val zekkn: ColumnWithName = ColumnWithName(prefix, "zekkn")
  val belnr: ColumnWithName = ColumnWithName(prefix, "belnr")
  val ebeln: ColumnWithName = ColumnWithName(prefix, "ebeln")
  val ebelp: ColumnWithName = ColumnWithName(prefix, "ebelp")
  val buzei: ColumnWithName = ColumnWithName(prefix, "buzei")
  val bwart: ColumnWithName = ColumnWithName(prefix, "bwart")
  val budat: ColumnWithName = ColumnWithName(prefix, "budat")
  val menge: ColumnWithName = ColumnWithName(prefix, "menge")
  val dmbtr: ColumnWithName = ColumnWithName(prefix, "dmbtr")
  val wrbtr: ColumnWithName = ColumnWithName(prefix, "wrbtr")
  val waers: ColumnWithName = ColumnWithName(prefix, "waers")
  val shkzg: ColumnWithName = ColumnWithName(prefix, "shkzg")
  val elikz: ColumnWithName = ColumnWithName(prefix, "elikz")
  val xblnr: ColumnWithName = ColumnWithName(prefix, "xblnr")
  val reewr: ColumnWithName = ColumnWithName(prefix, "reewr")
  val lsmng: ColumnWithName = ColumnWithName(prefix, "lsmng")
  val lsmeh: ColumnWithName = ColumnWithName(prefix, "lsmeh")
  val areww: ColumnWithName = ColumnWithName(prefix, "areww")
  val hswae: ColumnWithName = ColumnWithName(prefix, "hswae")
  val bldat: ColumnWithName = ColumnWithName(prefix, "bldat")
}

object C_ekbe extends C_ekbe("") {
  def withDFAlias(alias: String): C_ekbe = new C_ekbe(alias)
  def withoutDFAlias: C_ekbe = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_ekbe = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
