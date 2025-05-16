package ct.dna.lakehouse.catalogs.dw_md.showcase

import ct.dna.lakehouse.catalogs._
import ct.dna.lakehouse.metastore.LongType
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.ChangeFeedTable
import ct.dna.lakehouse.transformations.Origin
import ct.dna.lakehouse.transformations.TargetTable
import org.apache.spark.sql.SQLImplicits

object BOM extends Table with Origin.OneTransaction {

  override val keys = Seq(
    ("Article", StringType),
    ("Material", StringType)
  )
  override val values = Seq(
    ("Amount", LongType)
  )

  override val changeFeeds: Seq[Table] = Seq(sr.showcase.BOM)

  override def executeTransaction(implicits: SQLImplicits, target: TargetTable, changeFeeds: Map[Table, ChangeFeedTable]): Boolean = {
    val s = changeFeeds(sr.showcase.BOM)

    val mb = target
      .merge(
        s.getChangeFeed_last,
        "target.Article = source.article and target.Material = source.material"
      )
      .whenMatched("source._change_type = 'delete'")
      .delete()
      .whenMatched()
      .updateExpr(Map("Amount" -> "source.amount"))
      .whenNotMatched()
      .insertExpr(Map("Article" -> "source.article", "Material" -> "source.material", "Amount" -> "source.amount"))

    if (s.isSnapshot)
      mb.whenNotMatchedBySource().delete().execute()
    else mb.execute()

    true
  }

}
