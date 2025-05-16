package ct.dna.lakehouse.catalogs.dw_tx.showcase

import ct.dna.lakehouse.catalogs._
import ct.dna.lakehouse.metastore.LongType
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.ChangeFeedTable
import ct.dna.lakehouse.transformations.Origin
import ct.dna.lakehouse.transformations.TargetTable
import org.apache.spark.sql.SQLImplicits
object Orders extends Table with Origin.OneTransaction {

  override val keys = Seq(
    ("OrderId", StringType),
    ("Article", StringType)
  )
  override val values = Seq(
    ("Quantity", LongType)
  )
  override val changeFeeds: Seq[Table] = Seq(sr.showcase.Orders)

  override def executeTransaction(implicits: SQLImplicits, target: TargetTable, changeFeeds: Map[Table, ChangeFeedTable]): Boolean = {
    val s = changeFeeds(sr.showcase.Orders)

    val mb = target
      .merge(
        s.getChangeFeed_last.as("source"),
        "target.Article = source.article and target.OrderId = source.orderid"
      )
      .whenMatched("source._change_type = 'delete'")
      .delete()
      .whenMatched()
      .updateExpr(Map("Quantity" -> "source.quantity"))
      .whenNotMatched()
      .insertExpr(Map("Article" -> "source.article", "OrderId" -> "source.orderid", "Quantity" -> "source.quantity"))

    if (s.isSnapshot)
      mb.whenNotMatchedBySource().delete().execute()
    else mb.execute()

    true
  }

}
