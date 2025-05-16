package ct.dna.lakehouse.transformations
import org.apache.spark.sql.DataFrame

trait SnapshotTable {
  val fqtn: String
  def version: Long

  /** Does not contain the '_lh_metadata' Row and Column!
    */
  def getSnapshot: DataFrame

}
// trait SourceTableWithMetaRow extends ChangeFeedTable
