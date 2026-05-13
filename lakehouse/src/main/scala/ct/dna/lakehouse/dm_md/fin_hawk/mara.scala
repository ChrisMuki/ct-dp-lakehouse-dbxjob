package ct.dna.lakehouse.dm_md.fin_hawk

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.Decimal
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

case class DmMara(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK matnr: String,
    mtart: String,
    matkl: String,
    ersda: String,
    pstat: String,
    vpsta: String,
    lvorm: String,
    meins: String,
    ferth: String,
    formt: String,
    groes: String,
    wrkst: String,
    normt: String,
    @Decimal(13, 3) brgew: java.math.BigDecimal,
    @Decimal(13, 3) ntgew: java.math.BigDecimal,
    gewei: String,
    volum: java.lang.Double,
    voleh: String,
    laeng: java.lang.Double,
    breit: java.lang.Double,
    hoehe: java.lang.Double,
    meabm: String,
    prdha: String,
    attyp: String,
    mfrpn: String,
    mfrnr: String
) extends Entity

object mara extends TableSpec[DmMara] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.mara,
      ct_gbl_epp.mara,
      ct_gbl_ghp.mara,
      ct_gbl_p12.mara,
      ct_gbl_p24.mara,
      ct_gbl_p43.mara,
      ct_gbl_p61.mara,
      ct_gbl_p64.mara,
      ct_gbl_p73.mara,
      ct_gbl_p77.mara,
      ct_gbl_p85.mara,
      ct_gbl_pbr.mara,
      ct_gbl_psp.mara
    )

  /** Value columns this transformation actually reads from each source feed.
    *
    * Passed explicitly to `lastOfKey(...)` so the projection is restricted to these columns instead of defaulting to every value column declared by the
    * per-source spec. Some sr specs (notably the `Joined[E_mara_part1, E_mara_part2]` ones generated for ct_gbl_epp/ghp) declare extra `*_string` /
    * `*_decimal_*` fields whose sr-generator-emitted Rename ColumnMods get silently skipped (the generator only validates against the first Entity case class,
    * so part2 renames are dropped under `skipUnusedColumnMod=true`); those declared field names then do not exist on the underlying Delta table and a default
    * `lastOfKey()` would fail to resolve them.
    */
  private val consumedValueColumnNames: Seq[String] = Seq(
    "_mk_system",
    "_mk_instance",
    "mtart",
    "matkl",
    "ersda",
    "pstat",
    "vpsta",
    "lvorm",
    "meins",
    "ferth",
    "formt",
    "groes",
    "wrkst",
    "normt",
    "brgew",
    "ntgew",
    "gewei",
    "volum",
    "voleh",
    "laeng",
    "breit",
    "hoehe",
    "meabm",
    "prdha",
    "attyp",
    "mfrpn",
    "mfrnr"
  )

  /** Project a per-feed `lastOfKey()` slice down to the columns the merge actually consumes:
    *
    *   - the three PK columns
    *   - all `DmMara` value columns (passed through unchanged from sr_raw)
    *   - the `_change_type` discriminator (used by the merge branches to distinguish UPSERT vs DELETE)
    *
    * Doing the projection up-front strips the rest of the CDF metadata (`_commit_version`, `_commit_timestamp`) so the unioned source DataFrame stays
    * schema-aligned across all 13 sources and so the column maps below can address columns positionally without smuggling extra fields into the merge plan.
    */
  private def projectChanges(lastOfKey: org.apache.spark.sql.DataFrame): org.apache.spark.sql.DataFrame =
    lastOfKey
      .filter(col("matnr").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("matnr"),
        col("mtart"),
        col("matkl"),
        col("ersda"),
        col("pstat"),
        col("vpsta"),
        col("lvorm"),
        col("meins"),
        col("ferth"),
        col("formt"),
        col("groes"),
        col("wrkst"),
        col("normt"),
        col("brgew"),
        col("ntgew"),
        col("gewei"),
        col("volum"),
        col("voleh"),
        col("laeng"),
        col("breit"),
        col("hoehe"),
        col("meabm"),
        col("prdha"),
        col("attyp"),
        col("mfrpn"),
        col("mfrnr"),
        col("_change_type")
      )

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    // Nothing changed in any source → skip the run entirely.
    if (feeds.forall { case (_, f) => f.isUnchanged }) return Result.NoChanges

    // Snapshot semantics: any target row under such a `_mk_system` that is *not* present in the
    // (snapshotted) source must be removed — see the `whenNotMatchedBySource` branch below.
    val snapshotSystems: Set[String] = feeds
      .collect {
        case (_, feed) if feed.isSnapshot =>
          // Project only `_mk_system` instead of every value column — defaulting to all value columns would
          // re-project bad sr-spec columns (see `consumedValueColumnNames` doc above).
          feed.toDF(Seq("_mk_system")).select(C_mara._mk_system).limit(1).collect().map(_.getString(0)).toSet
      }
      .flatten
      .toSet

    /** Union of all per-feed projected change slices. `lastOfKey` already collapses to one row per PK with the latest `_change_type` (`upsert` or `delete`), so
      * the union is a straightforward row-stack across the 13 sources without any further dedup.
      */
    val grouped = feeds
      .map { case (_, feed) => projectChanges(feed.lastOfKey(consumedValueColumnNames)) }
      .reduce(_.unionByName(_))

    val target = C_mara.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val matnr = ColumnWithName("matnr").withDFAlias("source")
      val mtart = ColumnWithName("mtart").withDFAlias("source")
      val matkl = ColumnWithName("matkl").withDFAlias("source")
      val ersda = ColumnWithName("ersda").withDFAlias("source")
      val pstat = ColumnWithName("pstat").withDFAlias("source")
      val vpsta = ColumnWithName("vpsta").withDFAlias("source")
      val lvorm = ColumnWithName("lvorm").withDFAlias("source")
      val meins = ColumnWithName("meins").withDFAlias("source")
      val ferth = ColumnWithName("ferth").withDFAlias("source")
      val formt = ColumnWithName("formt").withDFAlias("source")
      val groes = ColumnWithName("groes").withDFAlias("source")
      val wrkst = ColumnWithName("wrkst").withDFAlias("source")
      val normt = ColumnWithName("normt").withDFAlias("source")
      val brgew = ColumnWithName("brgew").withDFAlias("source")
      val ntgew = ColumnWithName("ntgew").withDFAlias("source")
      val gewei = ColumnWithName("gewei").withDFAlias("source")
      val volum = ColumnWithName("volum").withDFAlias("source")
      val voleh = ColumnWithName("voleh").withDFAlias("source")
      val laeng = ColumnWithName("laeng").withDFAlias("source")
      val breit = ColumnWithName("breit").withDFAlias("source")
      val hoehe = ColumnWithName("hoehe").withDFAlias("source")
      val meabm = ColumnWithName("meabm").withDFAlias("source")
      val prdha = ColumnWithName("prdha").withDFAlias("source")
      val attyp = ColumnWithName("attyp").withDFAlias("source")
      val mfrpn = ColumnWithName("mfrpn").withDFAlias("source")
      val mfrnr = ColumnWithName("mfrnr").withDFAlias("source")
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
          source.matnr === target.matnr
      )
      /* CDF DELETE for an existing key → drop the row. */
      .whenMatched(isDelete)
      .delete()
      /* CDF UPSERT for an existing key → overwrite all value columns. */
      .whenMatched()
      .update(
        C_mara.mtart -> source.mtart,
        C_mara.matkl -> source.matkl,
        C_mara.ersda -> source.ersda,
        C_mara.pstat -> source.pstat,
        C_mara.vpsta -> source.vpsta,
        C_mara.lvorm -> source.lvorm,
        C_mara.meins -> source.meins,
        C_mara.ferth -> source.ferth,
        C_mara.formt -> source.formt,
        C_mara.groes -> source.groes,
        C_mara.wrkst -> source.wrkst,
        C_mara.normt -> source.normt,
        C_mara.brgew -> source.brgew,
        C_mara.ntgew -> source.ntgew,
        C_mara.gewei -> source.gewei,
        C_mara.volum -> source.volum,
        C_mara.voleh -> source.voleh,
        C_mara.laeng -> source.laeng,
        C_mara.breit -> source.breit,
        C_mara.hoehe -> source.hoehe,
        C_mara.meabm -> source.meabm,
        C_mara.prdha -> source.prdha,
        C_mara.attyp -> source.attyp,
        C_mara.mfrpn -> source.mfrpn,
        C_mara.mfrnr -> source.mfrnr
      )
      /* New PK with an UPSERT event → insert. (A CDF DELETE for a key the target never knew about is a no-op.) */
      .whenNotMatched(isUpsert)
      .insert(
        C_mara._mk_system -> source._mk_system,
        C_mara._mk_instance -> source._mk_instance,
        C_mara.matnr -> source.matnr,
        C_mara.mtart -> source.mtart,
        C_mara.matkl -> source.matkl,
        C_mara.ersda -> source.ersda,
        C_mara.pstat -> source.pstat,
        C_mara.vpsta -> source.vpsta,
        C_mara.lvorm -> source.lvorm,
        C_mara.meins -> source.meins,
        C_mara.ferth -> source.ferth,
        C_mara.formt -> source.formt,
        C_mara.groes -> source.groes,
        C_mara.wrkst -> source.wrkst,
        C_mara.normt -> source.normt,
        C_mara.brgew -> source.brgew,
        C_mara.ntgew -> source.ntgew,
        C_mara.gewei -> source.gewei,
        C_mara.volum -> source.volum,
        C_mara.voleh -> source.voleh,
        C_mara.laeng -> source.laeng,
        C_mara.breit -> source.breit,
        C_mara.hoehe -> source.hoehe,
        C_mara.meabm -> source.meabm,
        C_mara.prdha -> source.prdha,
        C_mara.attyp -> source.attyp,
        C_mara.mfrpn -> source.mfrpn,
        C_mara.mfrnr -> source.mfrnr
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

    // All source mara tables must share the same key columns as the canonical sr_mara schema (ct_gbl_e32),
    // and must expose at least the value columns this transformation actually consumes.
    //
    // We deliberately do NOT require the full canonical value-column set: some sources legitimately omit
    // optional columns (e.g. ct_gbl_epp.mara declares part2 fields under generator names like `*_string`
    // that we never read), and requiring them here would needlessly fail validation for tables we never use.

    val canonicalKeys = ct_gbl_e32.mara.keyColumnNames.toSet
    val canonicalValuesRequired = consumedValueColumnNames.toSet

    sourceTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.mara.keyColumnNames}"
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

sealed class C_mara(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val matnr: ColumnWithName = ColumnWithName(prefix, "matnr")
  val mtart: ColumnWithName = ColumnWithName(prefix, "mtart")
  val matkl: ColumnWithName = ColumnWithName(prefix, "matkl")
  val ersda: ColumnWithName = ColumnWithName(prefix, "ersda")
  val pstat: ColumnWithName = ColumnWithName(prefix, "pstat")
  val vpsta: ColumnWithName = ColumnWithName(prefix, "vpsta")
  val lvorm: ColumnWithName = ColumnWithName(prefix, "lvorm")
  val meins: ColumnWithName = ColumnWithName(prefix, "meins")
  val ferth: ColumnWithName = ColumnWithName(prefix, "ferth")
  val formt: ColumnWithName = ColumnWithName(prefix, "formt")
  val groes: ColumnWithName = ColumnWithName(prefix, "groes")
  val wrkst: ColumnWithName = ColumnWithName(prefix, "wrkst")
  val normt: ColumnWithName = ColumnWithName(prefix, "normt")
  val brgew: ColumnWithName = ColumnWithName(prefix, "brgew")
  val ntgew: ColumnWithName = ColumnWithName(prefix, "ntgew")
  val gewei: ColumnWithName = ColumnWithName(prefix, "gewei")
  val volum: ColumnWithName = ColumnWithName(prefix, "volum")
  val voleh: ColumnWithName = ColumnWithName(prefix, "voleh")
  val laeng: ColumnWithName = ColumnWithName(prefix, "laeng")
  val breit: ColumnWithName = ColumnWithName(prefix, "breit")
  val hoehe: ColumnWithName = ColumnWithName(prefix, "hoehe")
  val meabm: ColumnWithName = ColumnWithName(prefix, "meabm")
  val prdha: ColumnWithName = ColumnWithName(prefix, "prdha")
  val attyp: ColumnWithName = ColumnWithName(prefix, "attyp")
  val mfrpn: ColumnWithName = ColumnWithName(prefix, "mfrpn")
  val mfrnr: ColumnWithName = ColumnWithName(prefix, "mfrnr")
}

object C_mara extends C_mara("") {
  def withDFAlias(alias: String): C_mara = new C_mara(alias)
  def withoutDFAlias: C_mara = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_mara = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
