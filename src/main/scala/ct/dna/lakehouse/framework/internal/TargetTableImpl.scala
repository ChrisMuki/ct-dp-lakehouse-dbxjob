package ct.dna.lakehouse.framework.internal

import ct.dna.lakehouse.framework.MergeBuilder
import ct.dna.lakehouse.framework.UserMetadata.LakehouseMetadata
import ct.dna.lakehouse.transformations.TargetTable
import ct.dna.utils.json.mapper
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions
import org.apache.spark.sql.functions._
import ct.dna.utils.LoggingTrait

//TODO we need to ensure that the meta column is always existing!
case class TargetTableImpl(spark: SparkSession, fqtn: String, df: DataFrame, metadata: LakehouseMetadata, alias: String = "target")
    extends TargetTable
    with LoggingTrait {
  import spark.implicits._

  def as(alias: String): TargetTable = TargetTableImpl(spark, fqtn, df, metadata, alias)

  lazy val getSnapshot: DataFrame = df.filter($"_lh_metadata" === null)
  def merge(source: DataFrame, condition: String, sourceAlias: String = "source"): MergeBuilder = merge(source, functions.expr(condition), sourceAlias)

  def merge(source: DataFrame, condition: Column, sourceAlias: String): MergeBuilder = {
    if (merged) logAndThrow(new IllegalStateException(s"TargetTable merge can be called at most once"))
    val source__lh_meta = col(s"$sourceAlias._lh_metadata")
    val target__lh_meta = col(s"$alias._lh_metadata")
    df.createTempView(alias)
    MergeBuilderImpl(
      this,
      source
        .unionByName(Seq((mapper.writeValueAsString(metadata))).toDF("_lh_metadata"), true)
        .as(sourceAlias)
        .mergeInto(alias, (source__lh_meta.isNull and target__lh_meta.isNull) or (source__lh_meta.isNotNull and target__lh_meta.isNotNull and condition))
        .whenMatched(source__lh_meta.isNull and target__lh_meta.isNull)
        .update(Map("_lh_metadata" -> expr("update_lh_metadata(source._lh_metadata,target._lh_metadata)")))
    )
  }

  var merged = false
}
