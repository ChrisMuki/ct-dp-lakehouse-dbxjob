package ct.dna.lakehouse.framework.internal

import ct.dna.lakehouse.transformations.ChangeFeedTable
import ct.dna.lakehouse.transformations.SnapshotTable
import org.apache.spark.sql.DataFrame
case class SnapshotTableImpl(changeFeedTable: ChangeFeedTable) extends SnapshotTable {
  val fqtn: String = changeFeedTable.fqtn
  def version: Long = changeFeedTable.version.to
  lazy val getSnapshot: DataFrame = changeFeedTable.getSnapshot
}
