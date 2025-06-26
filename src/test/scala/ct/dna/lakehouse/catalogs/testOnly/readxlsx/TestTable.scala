package ct.dna.lakehouse.catalogs.testOnly.readxlsx

import ct.dna.lakehouse.metastore.ColType
import ct.dna.lakehouse.metastore.SRTable

object TestTable extends SRTable {
  val keys: Seq[(String, ColType)] = ???
  val values: Seq[(String, ColType)] = ???
}
