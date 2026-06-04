package ct.dna.lakehouse.dm_md.fin_regional_dashboard

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

case class DmMbew(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK matnr: String,
    @PK bwkey: String,
    @PK bwtar: String,
    stprs: Double,
    peinh: Double
) extends Entity

object mbew extends TableSpec[DmMbew] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.mbew,
      ct_gbl_epp.mbew,
      ct_gbl_ghp.mbew,
      ct_gbl_p12.mbew,
      ct_gbl_p24.mbew,
      ct_gbl_p43.mbew,
      ct_gbl_p61.mbew,
      ct_gbl_p64.mbew,
      ct_gbl_p69.mbew,
      ct_gbl_p73.mbew,
      ct_gbl_p77.mbew,
      ct_gbl_p85.mbew,
      ct_gbl_pbr.mbew,
      ct_gbl_psp.mbew
    )

  /** Value columns consumed from source (PKs are always available in lastOfKey). */
  private val consumedValueColumnNames: Seq[String] = Seq(
    "_mk_system",
    "_mk_instance",
    "stprs",
    "peinh"
  )

  /** Project raw source columns into the target shape. Straight passthrough — no transformations.
    */
  private def projectChanges(lastOfKey: org.apache.spark.sql.DataFrame): org.apache.spark.sql.DataFrame =
    lastOfKey
      .filter(col("matnr").isNotNull && col("bwkey").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("matnr"),
        col("bwkey"),
        col("bwtar"),
        col("stprs"),
        col("peinh"),
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
          projectChanges(feed.lastByKey(consumedValueColumnNames))
            .select(col("_mk_system"))
            .limit(1)
            .collect()
            .map(_.getString(0))
            .toSet
      }
      .flatten
      .toSet

    val grouped = feeds
      .map { case (_, feed) => projectChanges(feed.lastByKey(consumedValueColumnNames)) }
      .reduce(_.unionByName(_))

    val target = C_mbew.withDFAlias("target")

    val source = new {
      val _mk_system = ColumnWithName("_mk_system").withDFAlias("source")
      val _mk_instance = ColumnWithName("_mk_instance").withDFAlias("source")
      val matnr = ColumnWithName("matnr").withDFAlias("source")
      val bwkey = ColumnWithName("bwkey").withDFAlias("source")
      val bwtar = ColumnWithName("bwtar").withDFAlias("source")
      val stprs = ColumnWithName("stprs").withDFAlias("source")
      val peinh = ColumnWithName("peinh").withDFAlias("source")
      val _change_type = ColumnWithName("_change_type").withDFAlias("source")
    }

    val isDelete = source._change_type === "delete"
    // Framework emits _change_type = "delete" for CDC deletes; anything else is an upsert.
    val isUpsert = source._change_type =!= "delete"

    table
      .merge(
        grouped,
        source._mk_system === target._mk_system &&
          source._mk_instance === target._mk_instance &&
          source.matnr === target.matnr &&
          source.bwkey === target.bwkey &&
          source.bwtar === target.bwtar
      )
      /* CDF DELETE: hard-delete the row from the target table. */
      .whenMatched(isDelete)
      .delete()
      /* CDF UPSERT: update changed value columns in place. */
      .whenMatched()
      .update(
        C_mbew.stprs -> source.stprs,
        C_mbew.peinh -> source.peinh
      )
      /* New PK: insert the full row. */
      .whenNotMatched(isUpsert)
      .insert(
        C_mbew._mk_system -> source._mk_system,
        C_mbew._mk_instance -> source._mk_instance,
        C_mbew.matnr -> source.matnr,
        C_mbew.bwkey -> source.bwkey,
        C_mbew.bwtar -> source.bwtar,
        C_mbew.stprs -> source.stprs,
        C_mbew.peinh -> source.peinh
      )
      /* Snapshot cleanup: remove rows whose system was fully re-snapshotted but the PK no longer exists. */
      .whenNotMatchedBySource(target._mk_system.isin(snapshotSystems.toSeq: _*))
      .delete()
      .execute()
  }

  override def validate(): Unit = {
    super.validate()
    val canonicalValuesRequired = consumedValueColumnNames.toSet
    val canonicalKeys = ct_gbl_e32.mbew.keyColumnNames.toSet
    sourceTableSpecs.foreach { spec =>
      val missingValues = canonicalValuesRequired -- spec.valueColumnNames.toSet
      require(
        missingValues.isEmpty,
        s"Source table '$spec' is missing value columns: $missingValues"
      )
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.mbew.keyColumnNames}"
      )
    }
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_mbew(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val matnr: ColumnWithName = ColumnWithName(prefix, "matnr")
  val bwkey: ColumnWithName = ColumnWithName(prefix, "bwkey")
  val bwtar: ColumnWithName = ColumnWithName(prefix, "bwtar")
  val stprs: ColumnWithName = ColumnWithName(prefix, "stprs")
  val peinh: ColumnWithName = ColumnWithName(prefix, "peinh")
}

object C_mbew extends C_mbew("") {
  def withDFAlias(alias: String): C_mbew = new C_mbew(alias)
  def withoutDFAlias: C_mbew = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_mbew = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
