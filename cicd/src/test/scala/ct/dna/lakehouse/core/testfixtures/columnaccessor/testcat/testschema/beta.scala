package ct.dna.lakehouse.core.testfixtures.columnaccessor.testcat.testschema

import ct.dna.lakehouse.core.framework.origin.Loaded
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec

final case class beta_E(
    @PK key: String,
    amount: Long
) extends Entity

object beta extends TableSpec[beta_E] with Loaded
