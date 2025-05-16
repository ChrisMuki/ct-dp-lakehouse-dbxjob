package ct.dna.lakehouse.catalogs.dw_md.showcase

import ct.dna.lakehouse.catalogs._
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.ChangeFeedTable
import ct.dna.lakehouse.transformations.Origin
import ct.dna.lakehouse.transformations.TargetTable
import org.apache.spark.sql.SQLImplicits

object Articles extends Table with Origin.OneTransaction {

  override val keys = Seq(
    ("Article", StringType)
  )
  override val values = Seq(
    ("Description", StringType)
  )

  override val changeFeeds: Seq[Table] = Seq(sr.showcase.Articles)

  override def executeTransaction(implicits: SQLImplicits, target: TargetTable, changeFeeds: Map[Table, ChangeFeedTable]): Boolean = {
    val s = changeFeeds(sr.showcase.Articles)
    val mb = target
      .merge(s.getChangeFeed_last, "target.Article = source.article")
      .whenMatched("source._change_type = 'delete'")
      .delete()
      .whenMatched()
      .updateExpr(Map("Description" -> "source.description"))
      .whenNotMatched()
      .insertExpr(Map("Article" -> "source.article", "Description" -> "source.description"))

    if (s.isSnapshot)
      mb.whenNotMatchedBySource().delete().execute()
    else mb.execute()

    true
  }

}
