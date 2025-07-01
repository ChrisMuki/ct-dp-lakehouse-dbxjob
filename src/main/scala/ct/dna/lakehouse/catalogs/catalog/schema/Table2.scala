package ct.dna.lakehouse.catalogs.catalog.schema

import ct.dna.lakehouse.metastore.Origin
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.TableDef

object Table2 extends TableDef() with Origin.Loaded {

  val keys = Seq(
    ("id", StringType)
  )
  val values = Seq(
    ("value2", StringType)
  )

}
//
