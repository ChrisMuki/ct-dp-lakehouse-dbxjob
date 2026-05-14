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

case class E_makt(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK matnr: String,
    spras: String,
    maktx: String,
    _maktx_d: String,
    _maktx_e: String
) extends Entity

object makt extends TableSpec[E_makt] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.makt,
      ct_gbl_epp.makt,
      ct_gbl_ghp.makt,
      ct_gbl_p12.makt,
      ct_gbl_p24.makt,
      ct_gbl_p43.makt,
      ct_gbl_p61.makt,
      ct_gbl_p64.makt,
      ct_gbl_p69.makt,
      ct_gbl_p73.makt,
      ct_gbl_p77.makt,
      ct_gbl_p85.makt,
      ct_gbl_pbr.makt,
      ct_gbl_psp.makt
    )

  /** Pivots one feed from row-per-language to one row per (key) with two language columns.
    *
    * Result: (_mk_system, _mk_instance, matnr) -> _maktx_d, _maktx_e, _changed_d, _changed_e
    *
    * Per language X ∈ {D, E}: _maktx_x : new value when the row carries a new state (`_change_type` ∈ {`insert`, `update`}); `null` on `delete` or when there
    * is no event for X. _changed_x : `true` iff there was *any* event (insert/update OR delete) for X in this batch. For snapshot feeds it is forced to `true`,
    * because a snapshot row implies the *complete* current state — any missing language must be cleared.
    *
    * Together they encode the three input states without ambiguity: insert/update → _changed_x = true, _maktx_x = <value> delete → _changed_x = true, _maktx_x
    * \= null no event → _changed_x = false, _maktx_x = null
    */
  private def pivotByLanguage(lastOfKey: DataFrame, isSnapshot: Boolean): DataFrame = {
    import ct_gbl_e32.C_makt.{spras, maktx, _mk_system, _mk_instance, matnr}
    val _change_type = col("_change_type")
    // The framework emits `_change_type` ∈ {`insert`, `update`, `delete`} (post-image semantics on
    // `lastOfKey`). Anything that is not a delete carries a new value to apply.
    val isNewValue = _change_type =!= "delete"

    def changedFor(lang: String): Column =
      if (isSnapshot) lit(true)
      else max(when(spras === lang, lit(true)).otherwise(lit(false)))

    lastOfKey
      .filter(spras === "D" || spras === "E")
      .groupBy(_mk_system, _mk_instance, matnr)
      .agg(
        // per language, at most one row contributes a value (the others stay null).
        // `ignoreNulls = true` is required: each group has up to two rows (D + E),
        // and `first` without it would happily return the null from the wrong-language row.
        first(when(spras === "D" && isNewValue, coalesce(maktx, lit(""))), ignoreNulls = true).as("_value_d"),
        first(when(spras === "E" && isNewValue, coalesce(maktx, lit(""))), ignoreNulls = true).as("_value_e"),
        changedFor("D").as("_changed_d"),
        changedFor("E").as("_changed_e")
      )
  }

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    // Snapshot semantics: any target row under such a `_mk_system` that is *not* present in the
    // (snapshotted) source must be removed — see the `whenNotMatchedBySource` branch below.
    val snapshotSystems = changeFeeds
      .collect {
        case (_, feed) if feed.isSnapshot =>
          feed.toDF().select(C_makt._mk_system).limit(1).collect().map(_.getString(0)).toSet
      }
      .flatten
      .toSet

    /** Union of all per-feed pivots. See `pivotByLanguage` for the column contract. */
    val grouped = changeFeeds
      .map { case (_, feed) => pivotByLanguage(feed.lastOfKey(), feed.isSnapshot) }
      .reduce(_.unionByName(_))

    val target = C_makt.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val matnr = ColumnWithName("matnr").withDFAlias("source")
      val _value_d = ColumnWithName("_value_d").withDFAlias("source")
      val _value_e = ColumnWithName("_value_e").withDFAlias("source")
      val _changed_d = ColumnWithName("_changed_d").withDFAlias("source")
      val _changed_e = ColumnWithName("_changed_e").withDFAlias("source")
    }

    // Per language X ∈ {D, E}: if the source carried an event for X, take its value
    // (upsert → new value, delete → null); otherwise keep whatever the target already has.
    // For inserts `target.*` is null, so this collapses to just `source._maktx_x` automatically.
    val newD = when(source._changed_d, source._value_d).otherwise(target._maktx_d)
    val newE = when(source._changed_e, source._value_e).otherwise(target._maktx_e)

    val newSprasValue = concat_ws(";", when(newD.isNotNull, lit("D")), when(newE.isNotNull, lit("E")))
    val newMaktxValue = concat_ws("~~", newD, newE)

    // Delta MERGE forbids target-column references inside `whenNotMatched().insert(...)` (the row
    // doesn't exist yet). For inserts the target side of `newD`/`newE` is null by definition, so
    // these collapse to source-only equivalents.
    val insertSprasValue = concat_ws(";", when(source._value_d.isNotNull, lit("D")), when(source._value_e.isNotNull, lit("E")))
    val insertMaktxValue = concat_ws("~~", source._value_d, source._value_e)

    table
      .merge(
        grouped,
        source._mk_system === target._mk_system &&
          source._mk_instance === target._mk_instance &&
          source.matnr === target.matnr
      )
      /* At least one language still has a value → update with whichever side(s) survive. */
      .whenMatched(newD.isNotNull || newE.isNotNull)
      .update(
        C_makt.spras -> newSprasValue,
        C_makt.maktx -> newMaktxValue,
        C_makt._maktx_d -> newD,
        C_makt._maktx_e -> newE
      )
      /* Both languages gone → row gone. */
      .whenMatched()
      .delete()
      /* New matnr: insert iff the source carries at least one upsert (i.e. some _maktx_x is non-null). */
      .whenNotMatched(source._value_d.isNotNull || source._value_e.isNotNull)
      .insert(
        C_makt._mk_system -> source._mk_system,
        C_makt._mk_instance -> source._mk_instance,
        C_makt.matnr -> source.matnr,
        C_makt.spras -> insertSprasValue,
        C_makt.maktx -> insertMaktxValue,
        C_makt._maktx_d -> source._value_d,
        C_makt._maktx_e -> source._value_e
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

    // All source makt tables must share the same key columns as the canonical sr_makt schema (ct_gbl_e32),
    // and must expose at least the same value columns we rely on.

    val canonicalKeys = ct_gbl_e32.makt.keyColumnNames.toSet
    val canonicalValuesRequired = ct_gbl_e32.makt.valueColumnNames.toSet

    sourceTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.makt.keyColumnNames}"
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

sealed class C_makt(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val matnr: ColumnWithName = ColumnWithName(prefix, "matnr")
  val spras: ColumnWithName = ColumnWithName(prefix, "spras")
  val maktx: ColumnWithName = ColumnWithName(prefix, "maktx")
  val _maktx_d: ColumnWithName = ColumnWithName(prefix, "_maktx_d")
  val _maktx_e: ColumnWithName = ColumnWithName(prefix, "_maktx_e")
}

object C_makt extends C_makt("") {
  def withDFAlias(alias: String): C_makt = new C_makt(alias)
  def withoutDFAlias: C_makt = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_makt = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
