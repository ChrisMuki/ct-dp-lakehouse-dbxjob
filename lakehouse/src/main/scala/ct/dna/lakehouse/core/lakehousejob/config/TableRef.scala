package ct.dna.lakehouse.core.lakehousejob.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/** Fully-qualified Unity Catalog coordinates of a single Delta table. The three parts always travel together, so they are modelled as one value rather than
  * three loosely-coupled fields. `catalog`/`schema` are resolved at deploy time (defaulting to the deployment's volume catalog/schema); `table` carries the
  * task-specific table name.
  *
  * TODO: replace with `ct.dna.lakehouse.core.model.TableID` once the next library version ships a JSON-config-friendly (flat catalog/schema/table) form — its
  * current nested `schemaId.catalogId` shape does not serialise cleanly into the deployment config.
  *
  * @param catalog
  *   Unity Catalog catalog.
  * @param schema
  *   Unity Catalog schema.
  * @param table
  *   table name.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
final case class TableRef(catalog: String, schema: String, table: String) {
  def fqn: String = s"$catalog.$schema.$table"
}
