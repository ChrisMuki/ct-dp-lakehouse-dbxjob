package ct.dna.lakehouse.framework.internal.transformations
import ct.dna.lakehouse.framework.internal.metadata.Row_lh_framework
import ct.dna.lakehouse.framework.internal.metadata.Row_lh_framework.{columnName => _lh_framework}
import ct.dna.lakehouse.framework.internal.metadata.Row_lh_framework.{udfName => update_lh_framework}
import ct.dna.lakehouse.transformations.TargetTable
import ct.dna.utils.LoggingTrait
import io.delta.tables.DeltaMergeBuilder
import io.delta.tables.DeltaTable
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLImplicits
import org.apache.spark.sql.functions
import org.apache.spark.sql.functions._

//TODO we need to ensure that the meta column is always existing!
private[internal] case class TargetTableImpl(
    implicits: SQLImplicits,
    fqtn: String,
    dt: DeltaTable,
    last_lh_framework: Row_lh_framework,
    new_lh_framework: Row_lh_framework,
    alias: String = "target"
) extends TargetTable
    with LoggingTrait {
  import implicits._

  def version: TargetTable.Version = new_lh_framework.targetVersion
  def as(alias: String): TargetTable = TargetTableImpl(
    implicits,
    fqtn,
    dt,
    last_lh_framework,
    new_lh_framework,
    alias
  )

  lazy val getSnapshot: DataFrame = dt.toDF.filter(col(_lh_framework) === null)
  def merge(source: DataFrame, condition: String, sourceAlias: String = "source"): DeltaMergeBuilder = merge(source, functions.expr(condition), sourceAlias)

  def merge(source: DataFrame, condition: Column, sourceAlias: String): DeltaMergeBuilder = {
    if (merged) logAndThrow(new IllegalStateException("TargetTable merge can be called at most once"))
    val source__lh_meta = col(s"$sourceAlias.${_lh_framework}")
    val target__lh_meta = col(s"$alias.${_lh_framework}")

    dt.as(alias)
      .merge(
        source
          .unionByName(Seq((new_lh_framework.asValue)).toDF(_lh_framework), true)
          .as(sourceAlias),
        (source__lh_meta.isNull and target__lh_meta.isNull) or (source__lh_meta.isNotNull and target__lh_meta.isNotNull and condition)
      )
      .whenMatched(source__lh_meta.isNull and target__lh_meta.isNull)
      .update(
        Map(_lh_framework -> expr(s"$update_lh_framework(${alias}.${_lh_framework}),${sourceAlias}.${_lh_framework},'${last_lh_framework.asValue}'"))
      )
  }

  var merged = false
}
