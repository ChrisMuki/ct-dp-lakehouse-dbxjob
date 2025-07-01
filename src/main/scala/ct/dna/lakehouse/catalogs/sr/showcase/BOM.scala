package ct.dna.lakehouse.catalogs.sr.showcase

import ct.dna.lakehouse.metastore.LongType
import ct.dna.lakehouse.metastore.SRTableDef
import ct.dna.lakehouse.metastore.StringType

object BOM extends SRTableDef {

  override val keys = Seq(
    ("article", StringType),
    ("material", StringType)
  )
  override val values = Seq(
    ("amount", LongType)
  )

}
