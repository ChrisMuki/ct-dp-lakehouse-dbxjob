package ct.dna.lakehouse.transformations

import ct.dna.lakehouse.metastore.Table
import org.apache.spark.sql.SQLImplicits

trait Origin {
  self: Table =>
  def table: Table = self
}
object Origin {
  trait Loaded extends Origin {
    self: Table =>
  }

  trait Transformation extends Origin {
    self: Table =>
    def changeFeeds: Seq[Table]
  }
  trait OneTransaction extends Transformation {
    self: Table =>
    def executeTransaction(implicits: SQLImplicits, target: TargetTable, changeFeeds: Map[Table, ChangeFeedTable]): Boolean
  }
  trait TwoTransactions extends Transformation {
    self: Table =>
    def changeFeedsOne: Seq[Table]
    def executeTransactionOne(implicits: SQLImplicits, target: TargetTable, changeFeedsOne: Map[Table, ChangeFeedTable]): Boolean
    def changeFeedsTwo: Seq[Table]
    def executeTransactionTwo(
        implicits: SQLImplicits,
        target: TargetTable,
        changeFeedsTwo: Map[Table, ChangeFeedTable],
        snapshots: Map[Table, SnapshotTable]
    ): Boolean
    final def changeFeeds: Seq[Table] = changeFeedsOne ++ changeFeedsTwo
  }
}
