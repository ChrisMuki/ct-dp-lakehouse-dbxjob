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

case class DmT001W(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK werks: String,
    name1: String,
    bwkey: String,
    land1: String,
    kunnr: String,
    lifnr: String
) extends Entity

object t001w extends TableSpec[DmT001W] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.t001w,
      ct_gbl_epp.t001w,
      ct_gbl_ghp.t001w,
      ct_gbl_p12.t001w,
      ct_gbl_p24.t001w,
      ct_gbl_p43.t001w,
      ct_gbl_p61.t001w,
      ct_gbl_p64.t001w,
      ct_gbl_p73.t001w,
      ct_gbl_p77.t001w,
      ct_gbl_p85.t001w,
      ct_gbl_pbr.t001w,
      ct_gbl_psp.t001w
    )

  private def projectChanges(lastOfKey: org.apache.spark.sql.DataFrame): org.apache.spark.sql.DataFrame =
    lastOfKey
      .filter(col("werks").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("werks"),
        col("name1"),
        col("bwkey"),
        col("land1"),
        col("kunnr"),
        col("lifnr"),
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
          feed.toDF().select(C_t001w._mk_system).limit(1).collect().map(_.getString(0)).toSet
      }
      .flatten
      .toSet

    val grouped = feeds
      .map { case (_, feed) => projectChanges(feed.lastOfKey()) }
      .reduce(_.unionByName(_))

    val target = C_t001w.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val werks = ColumnWithName("werks").withDFAlias("source")
      val name1 = ColumnWithName("name1").withDFAlias("source")
      val bwkey = ColumnWithName("bwkey").withDFAlias("source")
      val land1 = ColumnWithName("land1").withDFAlias("source")
      val kunnr = ColumnWithName("kunnr").withDFAlias("source")
      val lifnr = ColumnWithName("lifnr").withDFAlias("source")
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
          source.werks === target.werks
      )
      .whenMatched(isDelete)
      .delete()
      .whenMatched()
      .update(
        C_t001w.name1 -> source.name1,
        C_t001w.bwkey -> source.bwkey,
        C_t001w.land1 -> source.land1,
        C_t001w.kunnr -> source.kunnr,
        C_t001w.lifnr -> source.lifnr
      )
      .whenNotMatched(isUpsert)
      .insert(
        C_t001w._mk_system -> source._mk_system,
        C_t001w._mk_instance -> source._mk_instance,
        C_t001w.werks -> source.werks,
        C_t001w.name1 -> source.name1,
        C_t001w.bwkey -> source.bwkey,
        C_t001w.land1 -> source.land1,
        C_t001w.kunnr -> source.kunnr,
        C_t001w.lifnr -> source.lifnr
      )
      .whenNotMatchedBySource(target._mk_system.isin(snapshotSystems.toSeq: _*))
      .delete()
      .execute()
  }

  override def validate(): Unit = {
    super.validate()

    val canonicalKeys = ct_gbl_e32.t001w.keyColumnNames.toSet
    val canonicalValuesRequired = Set("name1", "bwkey", "land1", "kunnr", "lifnr")

    sourceTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.t001w.keyColumnNames}"
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

sealed class C_t001w(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val werks: ColumnWithName = ColumnWithName(prefix, "werks")
  val name1: ColumnWithName = ColumnWithName(prefix, "name1")
  val bwkey: ColumnWithName = ColumnWithName(prefix, "bwkey")
  val land1: ColumnWithName = ColumnWithName(prefix, "land1")
  val kunnr: ColumnWithName = ColumnWithName(prefix, "kunnr")
  val lifnr: ColumnWithName = ColumnWithName(prefix, "lifnr")
}

object C_t001w extends C_t001w("") {
  def withDFAlias(alias: String): C_t001w = new C_t001w(alias)
  def withoutDFAlias: C_t001w = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_t001w = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
