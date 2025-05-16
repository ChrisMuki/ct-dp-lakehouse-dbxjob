package ct.dna.lakehouse.catalogs.dw_tx.showcase

import ct.dna.lakehouse.catalogs._
import ct.dna.lakehouse.metastore.LongType
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.ChangeFeedTable
import ct.dna.lakehouse.transformations.Origin
import ct.dna.lakehouse.transformations.SnapshotTable
import ct.dna.lakehouse.transformations.TargetTable
import org.apache.spark.sql.SQLImplicits

object MaterialUsageDetails extends Table with Origin.TwoTransactions {

  override val keys = Seq(
    ("Article", StringType),
    ("Material", StringType)
  )
  override val values = Seq(
    ("ArticleAmount", LongType),
    ("Amount", LongType)
  )
  override val changeFeedsOne: Seq[Table] = Seq(dw_md.showcase.BOM)
  override val changeFeedsTwo: Seq[Table] = Seq(dw_tx.showcase.Orders)

  override def executeTransactionOne(implicits: SQLImplicits, target: TargetTable, changeFeedsOne: Map[Table, ChangeFeedTable]): Boolean = {
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
      changeFeedsTwo: Map[Table, ChangeFeedTable],
      changeFeedsOne: Map[Table, SnapshotTable]
  ): Boolean = { ??? }

}
