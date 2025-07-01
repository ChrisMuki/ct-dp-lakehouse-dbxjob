package ct.dna.lakehouse.catalogs.dw_tx.showcase

import ct.dna.lakehouse.catalogs._
import ct.dna.lakehouse.dataframeprovider.ChangeFeedTable
import ct.dna.lakehouse.dataframeprovider.SnapshotTable
import ct.dna.lakehouse.dataframeprovider.TargetTable
import ct.dna.lakehouse.metastore.LongType
import ct.dna.lakehouse.metastore.Origin
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.TableDef
import org.apache.spark.sql.SQLImplicits

object MaterialUsageDetails extends TableDef with Origin.TwoTransactions {

  override val keys = Seq(
    ("Article", StringType),
    ("Material", StringType)
  )
  override val values = Seq(
    ("ArticleAmount", LongType),
    ("Amount", LongType)
  )
  override val changeFeedsOne: Seq[TableDef] = Seq(dw_md.showcase.BOM)
  override val changeFeedsTwo: Seq[TableDef] = Seq(dw_tx.showcase.Orders)

  override def executeTransactionOne(implicits: SQLImplicits, target: TargetTable, changeFeedsOne: Map[TableDef, ChangeFeedTable]): Boolean = {
    val bom = changeFeedsOne(dw_md.showcase.BOM)

    target
      .merge(bom.getChangeFeed_from_to, "target.Article = source.Article and target.Material = source.Material")
      .whenMatched()

    if (bom.isSnapshot) {} else {

      bom.getChangeFeed_from_to

    }
    true
  }

  override def executeTransactionTwo(
      implicits: SQLImplicits,
      target: TargetTable,
      changeFeedsTwo: Map[TableDef, ChangeFeedTable],
      changeFeedsOne: Map[TableDef, SnapshotTable]
  ): Boolean = { ??? }

}
