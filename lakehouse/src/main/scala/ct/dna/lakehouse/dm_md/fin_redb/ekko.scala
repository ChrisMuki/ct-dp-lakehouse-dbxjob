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

case class DmEkko(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK ebeln: String,
    bukrs: String,
    loekz: String,
    statu: String,
    aedat: String,
    lifnr: String,
    bsart: String,
    waers: String,
    ekorg: String
) extends Entity

object ekko extends TableSpec[DmEkko] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.ekko,
      ct_gbl_epp.ekko,
      ct_gbl_ghp.ekko,
      ct_gbl_p12.ekko,
      ct_gbl_p24.ekko,
      ct_gbl_p43.ekko,
      ct_gbl_p61.ekko,
      ct_gbl_p64.ekko,
      ct_gbl_p69.ekko,
      ct_gbl_p73.ekko,
      ct_gbl_p77.ekko,
      ct_gbl_p85.ekko,
      ct_gbl_pbr.ekko,
      ct_gbl_psp.ekko
    )

  /** Value columns this transformation actually reads from each source feed.
    *
    * Passed explicitly to `lastOfKey(...)` so the projection is restricted to these columns instead of defaulting to every value column declared by the
    * per-source spec. `bstyp` is appended at call-site because it is needed for the filter but is not a value column of `DmEkko`.
    */
  private val consumedValueColumnNames: Seq[String] = Seq(
    "_mk_system",
    "_mk_instance",
    "bukrs",
    "loekz",
    "statu",
    "aedat",
    "lifnr",
    "bsart",
    "waers",
    "ekorg"
  )

  /** Project a per-feed `lastOfKey()` slice down to the columns the merge actually consumes:
    *
    *   - the three PK columns
    *   - all `DmEkko` value columns passed through unchanged
    *   - the `_change_type` discriminator (used by the merge branches to distinguish UPSERT vs DELETE)
    *
    * Only rows with `bstyp = "F"` (standard purchase orders) are forwarded; all other document categories are discarded here before reaching the merge. Doing
    * the projection up-front strips the rest of the CDF metadata (`_commit_version`, `_commit_timestamp`) so the unioned source DataFrame stays schema-aligned
    * across all 14 sources.
    */
  private def projectChanges(lastOfKey: org.apache.spark.sql.DataFrame): org.apache.spark.sql.DataFrame =
    lastOfKey
      .filter(col("ebeln").isNotNull && col("bstyp") === "F")
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("ebeln"),
        col("bukrs"),
        col("loekz"),
        col("statu"),
        col("aedat"),
        col("lifnr"),
        col("bsart"),
        col("waers"),
        col("ekorg"),
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
          feed.snapshot(Seq("_mk_system")).select(C_ekko._mk_system).limit(1).collect().map(_.getString(0)).toSet
      }
      .flatten
      .toSet

    val grouped = feeds
      .map { case (_, feed) => projectChanges(feed.lastByKey(consumedValueColumnNames :+ "bstyp")) }
      .reduce(_.unionByName(_))

    val target = C_ekko.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val ebeln = ColumnWithName("ebeln").withDFAlias("source")
      val bukrs = ColumnWithName("bukrs").withDFAlias("source")
      val loekz = ColumnWithName("loekz").withDFAlias("source")
      val statu = ColumnWithName("statu").withDFAlias("source")
      val aedat = ColumnWithName("aedat").withDFAlias("source")
      val lifnr = ColumnWithName("lifnr").withDFAlias("source")
      val bsart = ColumnWithName("bsart").withDFAlias("source")
      val waers = ColumnWithName("waers").withDFAlias("source")
      val ekorg = ColumnWithName("ekorg").withDFAlias("source")
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
          source.ebeln === target.ebeln
      )
      /* CDF DELETE for an existing key → drop the row. */
      .whenMatched(isDelete)
      .delete()
      /* CDF UPSERT for an existing key → overwrite all value columns. */
      .whenMatched()
      .update(
        C_ekko.bukrs -> source.bukrs,
        C_ekko.loekz -> source.loekz,
        C_ekko.statu -> source.statu,
        C_ekko.aedat -> source.aedat,
        C_ekko.lifnr -> source.lifnr,
        C_ekko.bsart -> source.bsart,
        C_ekko.waers -> source.waers,
        C_ekko.ekorg -> source.ekorg
      )
      /* New PK with an UPSERT event → insert. (A CDF DELETE for a key the target never knew about is a no-op.) */
      .whenNotMatched(isUpsert)
      .insert(
        C_ekko._mk_system -> source._mk_system,
        C_ekko._mk_instance -> source._mk_instance,
        C_ekko.ebeln -> source.ebeln,
        C_ekko.bukrs -> source.bukrs,
        C_ekko.loekz -> source.loekz,
        C_ekko.statu -> source.statu,
        C_ekko.aedat -> source.aedat,
        C_ekko.lifnr -> source.lifnr,
        C_ekko.bsart -> source.bsart,
        C_ekko.waers -> source.waers,
        C_ekko.ekorg -> source.ekorg
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

    // All source ekko tables must share the same key columns as the canonical sr_ekko schema (ct_gbl_e32),
    // and must expose at least the value columns this transformation actually consumes.
    val canonicalKeys = ct_gbl_e32.ekko.keyColumnNames.toSet
    val canonicalValuesRequired = consumedValueColumnNames.toSet

    sourceTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.ekko.keyColumnNames}"
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

sealed class C_ekko(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val ebeln: ColumnWithName = ColumnWithName(prefix, "ebeln")
  val bukrs: ColumnWithName = ColumnWithName(prefix, "bukrs")
  val loekz: ColumnWithName = ColumnWithName(prefix, "loekz")
  val statu: ColumnWithName = ColumnWithName(prefix, "statu")
  val aedat: ColumnWithName = ColumnWithName(prefix, "aedat")
  val lifnr: ColumnWithName = ColumnWithName(prefix, "lifnr")
  val bsart: ColumnWithName = ColumnWithName(prefix, "bsart")
  val waers: ColumnWithName = ColumnWithName(prefix, "waers")
  val ekorg: ColumnWithName = ColumnWithName(prefix, "ekorg")
}

object C_ekko extends C_ekko("") {
  def withDFAlias(alias: String): C_ekko = new C_ekko(alias)
  def withoutDFAlias: C_ekko = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_ekko = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
