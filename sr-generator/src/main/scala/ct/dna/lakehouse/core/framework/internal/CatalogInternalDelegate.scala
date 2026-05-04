package ct.dna.lakehouse.core.framework.internal

import ct.dna.lakehouse.core.catalog.CatalogFQN
import ct.dna.lakehouse.core.catalog.SchemaFQN
import ct.dna.lakehouse.core.catalog.TableDesc
import ct.dna.lakehouse.core.catalog.TableFQN

/** Sibling-package delegate that exposes the package-private `TableManager` / `CatalogManager` APIs to code outside of
  * `ct.dna.lakehouse.core.catalog.internal`.
  */
object CatalogInternalDelegate {
  def readTableDesc(fqn: TableFQN): TableDesc = TableManager.readTableDesc(fqn)
  def findSchemaFQN(catalogFQN: CatalogFQN): Seq[SchemaFQN] = CatalogManager.findSchemaFQN(catalogFQN)
  def findTableFQN(schemaFQN: SchemaFQN): Seq[TableFQN] = CatalogManager.findTableFQN(schemaFQN)
}
