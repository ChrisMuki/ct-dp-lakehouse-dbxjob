package ct.dna.lakehouse.catalogs.testOnly.readxlsx

import ct.dna.lakehouse.metastore.ColType
import ct.dna.lakehouse.metastore.SRTableDef

object TestTable extends SRTableDef {
  val keys: Seq[(String, ColType)] = ???
  val values: Seq[(String, ColType)] = ???
}
