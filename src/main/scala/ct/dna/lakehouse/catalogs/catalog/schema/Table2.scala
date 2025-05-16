package ct.dna.lakehouse.catalogs.catalog.schema

import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.Origin

object Table2 extends Table() with Origin.Loaded {

  val keys = Seq(
    ("id", StringType)
  )
  val values = Seq(
    ("value2", StringType)
  )

}
//
