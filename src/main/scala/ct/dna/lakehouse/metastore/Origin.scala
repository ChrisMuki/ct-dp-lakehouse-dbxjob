package ct.dna.lakehouse.metastore

import ct.dna.lakehouse.dataframeprovider.ChangeFeedTable
import ct.dna.lakehouse.dataframeprovider.SnapshotTable
import ct.dna.lakehouse.dataframeprovider.TargetTable
import ct.dna.lakehouse.metastore.TableDef
import org.apache.spark.sql.SQLImplicits

trait Origin {
  self: TableDef =>
  def table: TableDef = self
}
object Origin {
  trait Loaded extends Origin {
    self: TableDef =>
  }

  trait Transformation extends Origin {
    self: TableDef =>
    def changeFeeds: Seq[TableDef]
  }
  trait OneTransaction extends Transformation {
    self: TableDef =>
    def executeTransaction(implicits: SQLImplicits, target: TargetTable, changeFeeds: Map[TableDef, ChangeFeedTable]): Boolean
  }
  trait TwoTransactions extends Transformation {
    self: TableDef =>
    def changeFeedsOne: Seq[TableDef]
    def executeTransactionOne(implicits: SQLImplicits, target: TargetTable, changeFeedsOne: Map[TableDef, ChangeFeedTable]): Boolean
    def changeFeedsTwo: Seq[TableDef]
    def executeTransactionTwo(
        implicits: SQLImplicits,
        target: TargetTable,
        changeFeedsTwo: Map[TableDef, ChangeFeedTable],
        snapshots: Map[TableDef, SnapshotTable]
    ): Boolean
    final def changeFeeds: Seq[TableDef] = changeFeedsOne ++ changeFeedsTwo
  }
}
