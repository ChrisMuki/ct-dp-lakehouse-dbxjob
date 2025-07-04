package ct.dna.lakehouse.catalogs.catalog.schema

import ct.dna.lakehouse.metastore.Origin
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.TableDef

// abstract class Transformation(implicit _schemaDef: SchemaDef) {
//   val catalogDef = _schemaDef.catalogDef
//   val schemaDef = _schemaDef

//   def changeFeeds: Seq[TableDef]
//   def executeTransaction(implicits: SQLImplicits, target: TargetTable, changeFeeds: Map[TableDef, ChangeFeedTable]): Boolean
// }

// case class Bom( /*@key*/ Article: String, Material: String, Amount: Long) /* extends TableRow*/

// object Bom extends Transformation {

//   def changeFeeds: Seq[TableDef] = ???
//   def executeTransaction(implicits: SQLImplicits, target: TargetTable, changeFeeds: Map[TableDef, ChangeFeedTable]): Boolean = ???
// }

object Table1 extends TableDef() with Origin.Loaded {
  val keys = Seq(
    ("id", StringType)
  )
  val values = Seq(
    ("value1", StringType)
  )
}
