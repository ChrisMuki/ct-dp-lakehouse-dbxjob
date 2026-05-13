package ct.dna.lakehouse.core.testfixtures.columnaccessor.testcat.testschema

import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.Loaded
import ct.dna.lakehouse.core.model.TableSpec

final case class alpha_E(
    @PK id: String,
    value: String
) extends Entity

object alpha extends TableSpec[alpha_E] with Loaded
