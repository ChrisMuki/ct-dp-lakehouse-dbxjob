package ct.dna.lakehouse.catalogs.catalog.schema

// import ct.dna.lakehouse.framwork_old_old.BooleanColumn
import ct.dna.lakehouse.metastore.BooleanType
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.ChangeFeedTable
import ct.dna.lakehouse.transformations.Origin
import ct.dna.lakehouse.transformations.TargetTable
import org.apache.spark.sql.SQLImplicits

// // IDea Table4 with full history aka SCD2
object Table3 extends Table with Origin.OneTransaction {
  val keys = Seq(
    ("id", StringType)
  )
  val values = Seq(
    ("value1", StringType),
    ("value2", StringType),
    ("_from1", BooleanType),
    ("_from2", BooleanType)
  )

  override val changeFeeds: Seq[Table] = Seq(Table1, Table2)

  override def executeTransaction(implicits: SQLImplicits, target: TargetTable, sources: Map[Table, ChangeFeedTable]): Boolean = ???

  // enforce meta column exists

  // val meta = Seq(null,"","",)
}
