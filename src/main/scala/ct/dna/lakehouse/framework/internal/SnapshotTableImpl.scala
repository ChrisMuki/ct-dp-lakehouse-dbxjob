package ct.dna.lakehouse.framework.internal

import ct.dna.lakehouse.transformations.ChangeFeedTable
import ct.dna.lakehouse.transformations.SnapshotTable
import org.apache.spark.sql.DataFrame
private[internal] case class SnapshotTableImpl(changeFeedTable: ChangeFeedTable) extends SnapshotTable {
  val fqtn: String = changeFeedTable.fqtn
  def version: SnapshotTable.Version = SnapshotTable.Version(changeFeedTable.version.current)
  lazy val getSnapshot: DataFrame = changeFeedTable.getSnapshot
}
