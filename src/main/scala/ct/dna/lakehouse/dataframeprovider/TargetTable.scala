package ct.dna.lakehouse.dataframeprovider
import io.delta.tables.DeltaMergeBuilder
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame

object TargetTable {
  case class Version(init: Commit, last: Commit)
}

trait TargetTable {
  def as(alias: String): TargetTable
  val fqtn: String
  def version: TargetTable.Version

  /** Does not contain the '_lh_framework' Row and Column!
    */
  def getSnapshot: DataFrame

  def merge(source: DataFrame, condition: String, sourceAlies: String = "source"): DeltaMergeBuilder
  def merge(source: DataFrame, condition: Column, sourceAlies: String): DeltaMergeBuilder

}
