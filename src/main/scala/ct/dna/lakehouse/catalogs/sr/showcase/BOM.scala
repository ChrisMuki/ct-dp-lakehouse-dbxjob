package ct.dna.lakehouse.catalogs.sr.showcase

import ct.dna.lakehouse.metastore.LongType
import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.Origin

object BOM extends Table with Origin.Loaded {

  override val keys = Seq(
    ("article", StringType),
    ("material", StringType)
  )
  override val values = Seq(
    ("amount", LongType)
  )

}
