package ct.dna.lakehouse.catalogs.sr.showcase

import ct.dna.lakehouse.metastore.SRTableDef
import ct.dna.lakehouse.metastore.StringType

object Articles extends SRTableDef {
  override val keys = Seq(
    ("article", StringType)
  )

  override val values = Seq(
    ("description", StringType)
  )

}
