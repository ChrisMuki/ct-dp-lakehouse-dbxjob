package ct.dna.lakehouse.catalogs.sr.showcase

import ct.dna.lakehouse.metastore.LongType
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.Origin

object Orders extends Table with Origin.Loaded {

  override val keys = Seq(
    ("orderid", StringType)
  )
  override val values = Seq(
    ("article", StringType),
    ("quantity", LongType)
  )

}
