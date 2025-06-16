package ct.dna.lakehouse.transformations
import org.apache.spark.sql.DataFrame

object SnapshotTable {
  case class Version(current: Commit)
}
trait SnapshotTable {
  val fqtn: String
  def version: SnapshotTable.Version

  /** Does not contain the '_lh_framework' Row and Column!
    */
  def getSnapshot: DataFrame

}
// trait SourceTableWithMetaRow extends ChangeFeedTable
