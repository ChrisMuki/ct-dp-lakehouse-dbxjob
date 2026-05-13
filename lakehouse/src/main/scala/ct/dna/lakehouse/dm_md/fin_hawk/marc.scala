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
import ct.dna.lakehouse.sr.ct_gbl_p73
import ct.dna.lakehouse.sr.ct_gbl_p77
import ct.dna.lakehouse.sr.ct_gbl_p85
import ct.dna.lakehouse.sr.ct_gbl_pbr
import ct.dna.lakehouse.sr.ct_gbl_psp
import org.apache.spark.sql.functions._

case class DmMarc(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK matnr: String,
    @PK werks: String,
    lvorm_plant: String,
    stawn: String,
    steuc: String,
    herkl: String,
    stawn_sap: String,
    steuc_sap: String
) extends Entity

object marc extends TableSpec[DmMarc] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.marc,
      ct_gbl_epp.marc,
      ct_gbl_ghp.marc,
      ct_gbl_p12.marc,
      ct_gbl_p24.marc,
      ct_gbl_p43.marc,
      ct_gbl_p61.marc,
      ct_gbl_p64.marc,
      ct_gbl_p73.marc,
      ct_gbl_p77.marc,
      ct_gbl_p85.marc,
      ct_gbl_pbr.marc,
      ct_gbl_psp.marc
    )

  /** Value columns this transformation actually reads from each source feed.
    *
    * Passed explicitly to `lastOfKey(...)` so the projection is restricted to these columns instead of defaulting to every value column declared by the
    * per-source spec. Some sr specs (notably the `Joined[E_marc_part1, E_marc_part2]` ones generated for ct_gbl_epp/ghp) declare extra `*_decimal_13_3` /
    * `*_string` fields whose sr-generator-emitted Rename ColumnMods get silently skipped (the generator only validates against the first Entity case class, so
    * part2 renames are dropped under `skipUnusedColumnMod=true`); those declared field names then do not exist on the underlying Delta table and a default
    * `lastOfKey()` would fail to resolve them.
    */
  private val consumedValueColumnNames: Seq[String] = Seq("_mk_system", "_mk_instance", "lvorm", "stawn", "steuc", "herkl")

  /** Project a per-feed `lastOfKey()` slice down to the columns the merge actually consumes:
    *
    *   - the four PK columns (incl. `werks`)
    *   - the renamed value column `lvorm_plant` (sourced from `lvorm` to disambiguate from `mara.lvorm`)
    *   - cleansed `stawn` / `steuc` (dots and whitespace stripped) — derived columns
    *   - the original `stawn` / `steuc` preserved as `stawn_sap` / `steuc_sap`
    *   - `herkl` passed through
    *   - the `_change_type` discriminator (drives the merge UPSERT/DELETE branches)
    *
    * Cleansing happens here, not in the merge map, so the value flows once through Catalyst and the merge plan stays a straight column lookup.
    */
  private def projectChanges(lastOfKey: org.apache.spark.sql.DataFrame): org.apache.spark.sql.DataFrame =
    lastOfKey
      .filter(col("matnr").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("matnr"),
        col("werks"),
        col("lvorm").as("lvorm_plant"),
        regexp_replace(col("stawn"), "\\.|\\s", "").as("stawn"),
        regexp_replace(col("steuc"), "\\.|\\s", "").as("steuc"),
        col("herkl"),
        col("stawn").as("stawn_sap"),
        col("steuc").as("steuc_sap"),
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
          feed.toDF(Seq("_mk_system")).select(C_marc._mk_system).limit(1).collect().map(_.getString(0)).toSet
      }
      .flatten
      .toSet

    val grouped = feeds
      .map { case (_, feed) => projectChanges(feed.lastOfKey(consumedValueColumnNames)) }
      .reduce(_.unionByName(_))

    val target = C_marc.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val matnr = ColumnWithName("matnr").withDFAlias("source")
      val werks = ColumnWithName("werks").withDFAlias("source")
      val lvorm_plant = ColumnWithName("lvorm_plant").withDFAlias("source")
      val stawn = ColumnWithName("stawn").withDFAlias("source")
      val steuc = ColumnWithName("steuc").withDFAlias("source")
      val herkl = ColumnWithName("herkl").withDFAlias("source")
      val stawn_sap = ColumnWithName("stawn_sap").withDFAlias("source")
      val steuc_sap = ColumnWithName("steuc_sap").withDFAlias("source")
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
          source.matnr === target.matnr &&
          source.werks === target.werks
      )
      .whenMatched(isDelete)
      .delete()
      .whenMatched()
      .update(
        C_marc.lvorm_plant -> source.lvorm_plant,
        C_marc.stawn -> source.stawn,
        C_marc.steuc -> source.steuc,
        C_marc.herkl -> source.herkl,
        C_marc.stawn_sap -> source.stawn_sap,
        C_marc.steuc_sap -> source.steuc_sap
      )
      .whenNotMatched(isUpsert)
      .insert(
        C_marc._mk_system -> source._mk_system,
        C_marc._mk_instance -> source._mk_instance,
        C_marc.matnr -> source.matnr,
        C_marc.werks -> source.werks,
        C_marc.lvorm_plant -> source.lvorm_plant,
        C_marc.stawn -> source.stawn,
        C_marc.steuc -> source.steuc,
        C_marc.herkl -> source.herkl,
        C_marc.stawn_sap -> source.stawn_sap,
        C_marc.steuc_sap -> source.steuc_sap
      )
      .whenNotMatchedBySource(target._mk_system.isin(snapshotSystems.toSeq: _*))
      .delete()
      .execute()
  }

  override def validate(): Unit = {
    super.validate()

    val canonicalKeys = ct_gbl_e32.marc.keyColumnNames.toSet
    val canonicalValuesRequired = Set("lvorm", "stawn", "steuc", "herkl")

    sourceTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.marc.keyColumnNames}"
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

sealed class C_marc(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val matnr: ColumnWithName = ColumnWithName(prefix, "matnr")
  val werks: ColumnWithName = ColumnWithName(prefix, "werks")
  val lvorm_plant: ColumnWithName = ColumnWithName(prefix, "lvorm_plant")
  val stawn: ColumnWithName = ColumnWithName(prefix, "stawn")
  val steuc: ColumnWithName = ColumnWithName(prefix, "steuc")
  val herkl: ColumnWithName = ColumnWithName(prefix, "herkl")
  val stawn_sap: ColumnWithName = ColumnWithName(prefix, "stawn_sap")
  val steuc_sap: ColumnWithName = ColumnWithName(prefix, "steuc_sap")
}

object C_marc extends C_marc("") {
  def withDFAlias(alias: String): C_marc = new C_marc(alias)
  def withoutDFAlias: C_marc = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_marc = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
