package ct.dna.lakehouse.catalogs.dw_tx.showcase

import ct.dna.lakehouse.catalogs._
import ct.dna.lakehouse.dataframeprovider.ChangeFeedTable
import ct.dna.lakehouse.dataframeprovider.TargetTable
import ct.dna.lakehouse.metastore.LongType
import ct.dna.lakehouse.metastore.Origin
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.TableDef
import org.apache.spark.sql.SQLImplicits
import org.apache.spark.sql.functions._

object MaterialUsage extends TableDef with Origin.OneTransaction {

  override val keys = Seq(("Material", StringType))
  override val values = Seq(("Amount", LongType))
  override val changeFeeds: Seq[TableDef] = Seq(dw_tx.showcase.MaterialUsageDetails)

  override def executeTransaction(implicits: SQLImplicits, target: TargetTable, changeFeeds: Map[TableDef, ChangeFeedTable]): Boolean = {

    import implicits._
    val source = changeFeeds(dw_tx.showcase.MaterialUsageDetails)

    if (source.isSnapshot) {
      val changes = source.getSnapshot
        .groupBy("Material")
        .agg(
          sum("Amount").as("Amount")
        )

      target
        .merge(changes, "Material")
        .whenMatched()
        .updateExpr(Map("Amount" -> "source.Amount"))
        .whenNotMatched()
        .insertExpr(Map("Amount" -> "source.Amount", "Material" -> "source.Material"))
        .whenNotMatchedBySource()
        .delete()
        .execute()
    } else {

      val changes = source.getChangeFeed_from_to
        .groupBy("Material")
        .agg(
          sum("_from_Amount").as("from"),
          sum("_to_Amount").as("to")
        )

      target
        .merge(changes, "Material")
        .whenMatched($"source.to".isNull and expr("target.Amount = source.from"))
        .delete()
        .whenMatched($"source.to".isNull)
        .updateExpr(Map("Amount" -> "target.Amount - source.from"))
        .whenMatched($"source.from".isNull)
        .updateExpr(Map("Amount" -> "target.Amount + source.to"))
        .whenMatched()
        .updateExpr(Map("Amount" -> "target.Amount - source.from + source.to"))
        .whenNotMatched($"source.from".isNull)
        .insertExpr(Map("Amount" -> "source.to", "Material" -> "source.Material"))
        .whenNotMatched()
        .insertExpr(Map("Amount" -> "THIS MUST BE AN EXCEPTION"))
        .execute()
// orders.get

    }

    // val mb = target
    //   .merge(
    //     s.getChangeFeed_last_by("orderid", "article").as("source"),
    //     "target.Article = source.article and target.OrderId = source.orderid"
    //   )
    //   .whenMatched("source._change_type = 'delete'")
    //   .delete()
    //   .whenMatched()
    //   .updateExpr(Map("Quantity" -> "source.quantity"))
    //   .whenNotMatched()
    //   .insertExpr(Map("Article" -> "source.article", "OrderId" -> "source.orderid", "Quantity" -> "source.quantity"))

    // if (s.isSnapshot)
    //   mb.whenNotMatchedBySource().delete().execute()
    // else mb.execute()

    true
  }

}
