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

case class DmT001(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK bukrs: String,
    butxt: String,
    land1: String,
    ort01: String,
    waers: String
) extends Entity

object t001 extends TableSpec[DmT001] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.t001,
      ct_gbl_epp.t001,
      ct_gbl_ghp.t001,
      ct_gbl_p12.t001,
      ct_gbl_p24.t001,
      ct_gbl_p43.t001,
      ct_gbl_p61.t001,
      ct_gbl_p64.t001,
      ct_gbl_p69.t001,
      ct_gbl_p73.t001,
      ct_gbl_p77.t001,
      ct_gbl_p85.t001,
      ct_gbl_pbr.t001,
      ct_gbl_psp.t001
    )

  /** Value columns this transformation actually reads from each source feed.
    *
    * Passed explicitly to `lastOfKey(...)` so the projection is restricted to these columns instead of defaulting to every value column declared by the
    * per-source spec.
    */
  private val consumedValueColumnNames: Seq[String] = Seq("_mk_system", "_mk_instance", "butxt", "land1", "ort01", "waers")

  /** Project a per-feed `lastOfKey()` slice down to the columns the merge actually consumes:
    *
    *   - the three PK columns
    *   - all `DmT001` value columns passed through unchanged
    *   - the `_change_type` discriminator (used by the merge branches to distinguish UPSERT vs DELETE)
    *
    * Doing the projection up-front strips the rest of the CDF metadata so the unioned source DataFrame stays schema-aligned across all 14 sources.
    */
  private def projectChanges(lastOfKey: org.apache.spark.sql.DataFrame): org.apache.spark.sql.DataFrame =
    lastOfKey
      .filter(col("bukrs").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("bukrs"),
        col("butxt"),
        col("land1"),
        col("ort01"),
        col("waers"),
        col("_change_type")
      )
      .distinct()

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    if (feeds.forall { case (_, f) => f.isUnchanged }) return Result.NoChanges

    val snapshotSystems: Set[String] = feeds
      .collect {
        case (_, feed) if feed.isSnapshot =>
          // Project only `_mk_system` instead of every value column.
          feed.snapshot(Seq("_mk_system")).select(C_t001._mk_system).limit(1).collect().map(_.getString(0)).toSet
      }
      .flatten
      .toSet

    val grouped = feeds
      .map { case (_, feed) => projectChanges(feed.lastByKey(consumedValueColumnNames)) }
      .reduce(_.unionByName(_))

    val target = C_t001.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val bukrs = ColumnWithName("bukrs").withDFAlias("source")
      val butxt = ColumnWithName("butxt").withDFAlias("source")
      val land1 = ColumnWithName("land1").withDFAlias("source")
      val ort01 = ColumnWithName("ort01").withDFAlias("source")
      val waers = ColumnWithName("waers").withDFAlias("source")
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
          source.bukrs === target.bukrs
      )
      .whenMatched(isDelete)
      .delete()
      .whenMatched()
      .update(
        C_t001.butxt -> source.butxt,
        C_t001.land1 -> source.land1,
        C_t001.ort01 -> source.ort01,
        C_t001.waers -> source.waers
      )
      .whenNotMatched(isUpsert)
      .insert(
        C_t001._mk_system -> source._mk_system,
        C_t001._mk_instance -> source._mk_instance,
        C_t001.bukrs -> source.bukrs,
        C_t001.butxt -> source.butxt,
        C_t001.land1 -> source.land1,
        C_t001.ort01 -> source.ort01,
        C_t001.waers -> source.waers
      )
      .whenNotMatchedBySource(target._mk_system.isin(snapshotSystems.toSeq: _*))
      .delete()
      .execute()
  }

  override def validate(): Unit = {
    super.validate()

    val canonicalKeys = ct_gbl_e32.t001.keyColumnNames.toSet
    val canonicalValuesRequired = consumedValueColumnNames.toSet

    sourceTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.t001.keyColumnNames}"
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

sealed class C_t001(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val bukrs: ColumnWithName = ColumnWithName(prefix, "bukrs")
  val butxt: ColumnWithName = ColumnWithName(prefix, "butxt")
  val land1: ColumnWithName = ColumnWithName(prefix, "land1")
  val ort01: ColumnWithName = ColumnWithName(prefix, "ort01")
  val waers: ColumnWithName = ColumnWithName(prefix, "waers")
}

object C_t001 extends C_t001("") {
  def withDFAlias(alias: String): C_t001 = new C_t001(alias)
  def withoutDFAlias: C_t001 = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_t001 = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
