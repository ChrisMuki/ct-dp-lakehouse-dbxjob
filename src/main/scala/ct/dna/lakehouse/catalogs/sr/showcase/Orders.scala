package ct.dna.lakehouse.catalogs.sr.showcase

import ct.dna.lakehouse.metastore.LongType
import ct.dna.lakehouse.metastore.SRTableDef
import ct.dna.lakehouse.metastore.StringType

object Orders extends SRTableDef {

  override val keys = Seq(
    ("orderid", StringType)
  )
  override val values = Seq(
    ("article", StringType),
    ("quantity", LongType)
  )

}
