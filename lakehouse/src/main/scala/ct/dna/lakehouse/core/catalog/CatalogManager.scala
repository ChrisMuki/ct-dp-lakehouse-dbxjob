package ct.dna.lakehouse.core.catalog

import ct.dna.lakehouse.core.catalog.internal.{CatalogManager => InternalCatalogManager}
import ct.dna.lakehouse.core.runtime.SparkEnv
import org.apache.spark.sql.SparkSession

/** Public API for catalog .
  *
  * Exposes functionality needed by external tools (e.g., code generators) that need to query catalog metadata without being in the `core` package.
  */
object CatalogManager {

  /** Check if a catalog exists
    */
  def catalogExists(catalogFQN: CatalogFQN): Boolean = {
    SparkEnv.requireInitialized()
    SparkSession.active.catalog.listCatalogs(catalogFQN.name).count() > 0
  }

  /** Find all schema FQNs in a catalog
    */
  def findSchemaFQN(catalogFQN: CatalogFQN): Seq[SchemaFQN] = {
    SparkEnv.requireInitialized()
    InternalCatalogManager.findSchemaFQN(catalogFQN)
  }

  /** Find all table FQNs in a schema
    */
  def findTableFQN(schemaFQN: SchemaFQN): Seq[TableFQN] = {
    SparkEnv.requireInitialized()
    InternalCatalogManager.findTableFQN(schemaFQN)
  }

  /** Read table description (schema, properties, etc.) for a table
    */
  def readTableDesc(tableFQN: TableFQN): TableDesc = {
    SparkEnv.requireInitialized()
    // Internal TableManager exposes readTableDesc with restricted visibility. Use reflection to access it.
    val tableManagerClass = Class.forName("ct.dna.lakehouse.core.catalog.internal.TableManager$")
    val module = tableManagerClass.getField("MODULE$").get(null)
    val method = tableManagerClass.getDeclaredMethod("readTableDesc", classOf[TableFQN])
    method.setAccessible(true)
    method.invoke(module, tableFQN).asInstanceOf[TableDesc]
  }
}
