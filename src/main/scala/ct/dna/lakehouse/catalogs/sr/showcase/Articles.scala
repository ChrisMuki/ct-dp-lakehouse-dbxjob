package ct.dna.lakehouse.catalogs.sr.showcase

import ct.dna.lakehouse.metastore.SRTable
import ct.dna.lakehouse.metastore.StringType

object Articles extends SRTable {
  override val keys = Seq(
    ("article", StringType)
  )

  override val values = Seq(
    ("description", StringType)
  )

}
