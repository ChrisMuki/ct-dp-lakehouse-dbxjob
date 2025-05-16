package ct.dna.lakehouse.transformations
import ct.dna.lakehouse.framework.MergeBuilder
import ct.dna.lakehouse.framework.UserMetadata.LakehouseMetadata
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import com.fasterxml.jackson.module.scala.deser.overrides

trait TargetTable {
  def as(alias: String): TargetTable
  val fqtn: String
  def metadata: LakehouseMetadata

  /** Does not contain the '_lh_metadata' Row and Column!
    */
  def getSnapshot: DataFrame

  def merge(source: DataFrame, condition: String, sourceAlies: String = "source"): MergeBuilder
  def merge(source: DataFrame, condition: Column, sourceAlies: String): MergeBuilder

}
