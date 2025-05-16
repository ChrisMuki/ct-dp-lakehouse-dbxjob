package ct.dna.lakehouse.catalogs.sr.showcase

import ct.dna.lakehouse.metastore.StringType
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.Origin

object Articles extends Table with Origin.Loaded {
  override val keys = Seq(
    ("article", StringType)
  )

  override val values = Seq(
    ("description", StringType)
  )

}
