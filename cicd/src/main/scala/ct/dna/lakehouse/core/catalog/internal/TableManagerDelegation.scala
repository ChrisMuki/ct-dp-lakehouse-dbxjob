package ct.dna.lakehouse.core.catalog.internal

import ct.dna.lakehouse.core.catalog.TableDesc
import ct.dna.lakehouse.core.catalog.TableFQN
import ct.dna.lakehouse.core.catalog.internal.TableManager
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableSpec

object TableManagerDelegation {

  def readTableDesc(fqn: TableFQN): TableDesc = TableManager.readTableDesc(fqn)
  def buildTableDesc(tableSpec: TableSpec[Entity]): TableDesc = TableManager.buildTableDesc(tableSpec, false)
}
