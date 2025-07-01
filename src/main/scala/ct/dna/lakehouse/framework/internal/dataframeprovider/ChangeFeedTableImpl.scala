package ct.dna.lakehouse.framework.internal.dataframeprovider
import scala.collection.immutable.ArraySeq

import ct.dna.lakehouse.dataframeprovider.ChangeFeedTable
import ct.dna.lakehouse.framework.internal.metadata.Row_lh_framework.{columnName => _lh_framework}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLImplicits
import org.apache.spark.sql.functions._
private[internal] case class ChangeFeedTableImpl(
    implicits: SQLImplicits,
    keys: Seq[String],
    fqtn: String,
    df: DataFrame,
    cdf: DataFrame,
    version: ChangeFeedTable.Version,
    isSnapshot: Boolean
) extends ChangeFeedTable {
  import implicits._
  private val cdfColNames = Seq("_commit_version", "_change_type", "_commit_timestamp")

  lazy val getSnapshot: DataFrame = df.filter(col(_lh_framework).isNull).drop(_lh_framework)

  lazy val getChangeFeed: DataFrame = cdf.filter(col(_lh_framework).isNull).drop(_lh_framework)
  def getChangeFeed_last: DataFrame = {

    val valueColNames = ArraySeq.unsafeWrapArray(getChangeFeed.columns.filterNot(c => keys.contains(c) || cdfColNames.contains(c)))

    val allNonKeyColumns = (cdfColNames ++ valueColNames).map(col)
    val keyCols = keys.map(col)

    getChangeFeed
      .groupBy(keyCols: _*)
      .agg(
        max(when(expr("_change_type != 'update_preimage'"), struct(allNonKeyColumns: _*))).as("__temp")
      )
      .select((keyCols :+ $"__temp.*"): _*)
  }

  def getChangeFeed_from_to: DataFrame = {
    val valueColNames = ArraySeq.unsafeWrapArray(getChangeFeed.columns.filterNot(c => keys.contains(c) || cdfColNames.contains(c)))

    val allNonKeyColumns = (cdfColNames ++ valueColNames).map(col)
    val keyCols = keys.map(col)

    getChangeFeed
      .withColumn("__temp_is_from", when(expr("_change_type == 'update_preimage' or _change_type == 'delete'"), true).otherwise(false))
      .groupBy(keyCols: _*)
      .agg(
        min(when($"__temp_is_from", struct(allNonKeyColumns: _*))).as("__from"),
        max(when(!$"__temp_is_from", struct(allNonKeyColumns: _*))).as("__to")
      )
    // .select(
    //   (keyCols
    //     :++ allNonKeyColumns.map(f => col(s"_temp_from.$f").as(s"_temp_from_$f"))
    //     :++ allNonKeyColumns.map(f => col(s"_temp_to.$f").as(s"_temp_to_$f"))): _*
    // )
  }

}
