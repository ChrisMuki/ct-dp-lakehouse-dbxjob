package ct.dna.lakehouse.dm_md.fin_hawk

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
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

case class DmT023T(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK matkl: String,
    spras: String,
    wgbez: String,
    _wgbez_d: String,
    _wgbez_e: String
) extends Entity

object t023t extends TableSpec[DmT023T] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.t023t,
      ct_gbl_epp.t023t,
      ct_gbl_ghp.t023t,
      ct_gbl_p12.t023t,
      ct_gbl_p24.t023t,
      ct_gbl_p43.t023t,
      ct_gbl_p61.t023t,
      ct_gbl_p64.t023t,
      ct_gbl_p69.t023t,
      ct_gbl_p73.t023t,
      ct_gbl_p77.t023t,
      ct_gbl_p85.t023t,
      ct_gbl_pbr.t023t,
      ct_gbl_psp.t023t
    )

  /** Pivots one feed from row-per-language to one row per (key) with two language columns.
    *
    * Result: (_mk_system, _mk_instance, matkl) -> _wgbez_d, _wgbez_e, _changed_d, _changed_e
    *
    * Per language X ∈ {D, E}: _wgbez_x : new value on `upsert`; `null` on `delete` or when there is no event for X. _changed_x : `true` iff there was *any*
    * event (upsert OR delete) for X in this batch. For snapshot feeds it is forced to `true`, because a snapshot row implies the *complete* current state — any
    * missing language must be cleared.
    *
    * Together they encode the three input states without ambiguity: upsert → _changed_x = true, _wgbez_x = <value> delete → _changed_x = true, _wgbez_x = null
    * no event → _changed_x = false, _wgbez_x = null
    */
  private def pivotByLanguage(lastOfKey: DataFrame, isSnapshot: Boolean): DataFrame = {
    import ct_gbl_e32.C_t023t.{spras, wgbez, _mk_system, _mk_instance, matkl}
    val _change_type = col("_change_type")

    def changedFor(lang: String): Column =
      if (isSnapshot) lit(true)
      else max(when(spras === lang, lit(true)).otherwise(lit(false)))

    // Framework emits `_change_type` ∈ {`insert`, `update`, `delete`} on `lastOfKey`. Anything
    // that is not a delete carries a new value to apply.
    val isNewValue = _change_type =!= "delete"

    lastOfKey
      .filter(spras === "D" || spras === "E")
      .groupBy(_mk_system, _mk_instance, matkl)
      .agg(
        // `ignoreNulls = true` is required: each group has up to two rows (D + E), and `first`
        // without it would happily return the null from the wrong-language row.
        first(when(spras === "D" && isNewValue, coalesce(wgbez, lit(""))), ignoreNulls = true).as("_value_d"),
        first(when(spras === "E" && isNewValue, coalesce(wgbez, lit(""))), ignoreNulls = true).as("_value_e"),
        changedFor("D").as("_changed_d"),
        changedFor("E").as("_changed_e")
      )
  }

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val snapshotSystems = changeFeeds
      .collect {
        case (_, feed) if feed.isSnapshot =>
          feed.snapshot().select(C_t023t._mk_system).limit(1).collect().map(_.getString(0)).toSet
      }
      .flatten
      .toSet

    val grouped = changeFeeds
      .map { case (_, feed) => pivotByLanguage(feed.lastByKey(), feed.isSnapshot) }
      .reduce(_.unionByName(_))

    val target = C_t023t.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val matkl = ColumnWithName("matkl").withDFAlias("source")
      val _value_d = ColumnWithName("_value_d").withDFAlias("source")
      val _value_e = ColumnWithName("_value_e").withDFAlias("source")
      val _changed_d = ColumnWithName("_changed_d").withDFAlias("source")
      val _changed_e = ColumnWithName("_changed_e").withDFAlias("source")
    }

    val newD = when(source._changed_d, source._value_d).otherwise(target._wgbez_d)
    val newE = when(source._changed_e, source._value_e).otherwise(target._wgbez_e)

    val newSprasValue = concat_ws(";", when(newD.isNotNull, lit("D")), when(newE.isNotNull, lit("E")))
    val newWgbezValue = concat_ws("~~", newD, newE)

    // Delta MERGE forbids target-column references inside `whenNotMatched().insert(...)` (the row
    // doesn't exist yet). For inserts the target side of `newD`/`newE` is null by definition, so
    // these collapse to source-only equivalents.
    val insertSprasValue = concat_ws(";", when(source._value_d.isNotNull, lit("D")), when(source._value_e.isNotNull, lit("E")))
    val insertWgbezValue = concat_ws("~~", source._value_d, source._value_e)

    table
      .merge(
        grouped,
        source._mk_system === target._mk_system &&
          source._mk_instance === target._mk_instance &&
          source.matkl === target.matkl
      )
      .whenMatched(newD.isNotNull || newE.isNotNull)
      .update(
        C_t023t.spras -> newSprasValue,
        C_t023t.wgbez -> newWgbezValue,
        C_t023t._wgbez_d -> newD,
        C_t023t._wgbez_e -> newE
      )
      .whenMatched()
      .delete()
      .whenNotMatched(source._value_d.isNotNull || source._value_e.isNotNull)
      .insert(
        C_t023t._mk_system -> source._mk_system,
        C_t023t._mk_instance -> source._mk_instance,
        C_t023t.matkl -> source.matkl,
        C_t023t.spras -> insertSprasValue,
        C_t023t.wgbez -> insertWgbezValue,
        C_t023t._wgbez_d -> source._value_d,
        C_t023t._wgbez_e -> source._value_e
      )
      .whenNotMatchedBySource(target._mk_system.isin(snapshotSystems.toSeq: _*))
      .delete()
      .execute()
  }

  override def validate(): Unit = {
    super.validate()

    val canonicalKeys = ct_gbl_e32.t023t.keyColumnNames.toSet
    val canonicalValuesRequired = ct_gbl_e32.t023t.valueColumnNames.toSet

    sourceTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.t023t.keyColumnNames}"
      )
      val missingValues = canonicalValuesRequired -- spec.valueColumnNames.toSet
      require(
        missingValues.isEmpty,
        s"Source table '$spec' is missing value columns required by canonical schema: ${missingValues.mkString(", ")}"
      )
    }
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_t023t(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val matkl: ColumnWithName = ColumnWithName(prefix, "matkl")
  val spras: ColumnWithName = ColumnWithName(prefix, "spras")
  val wgbez: ColumnWithName = ColumnWithName(prefix, "wgbez")
  val _wgbez_d: ColumnWithName = ColumnWithName(prefix, "_wgbez_d")
  val _wgbez_e: ColumnWithName = ColumnWithName(prefix, "_wgbez_e")
}

object C_t023t extends C_t023t("") {
  def withDFAlias(alias: String): C_t023t = new C_t023t(alias)
  def withoutDFAlias: C_t023t = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_t023t = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
