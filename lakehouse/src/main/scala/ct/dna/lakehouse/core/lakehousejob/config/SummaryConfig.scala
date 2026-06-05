package ct.dna.lakehouse.core.lakehousejob.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ct.dna.lakehouse.core.catalog.TableFQN

/** Runtime knobs consumed by the terminal Summary step: where (and whether) to write the per-run summary Delta row. Read by `JobSetup` from the catalog's
  * `configFile` and published into [[ct.dna.lakehouse.core.lakehousejob.SharedState]].
  *
  * @param target
  *   Unity Catalog coordinates of the per-run summary Delta table written by the Summary task. `Some` enables the write to those coordinates; `None` disables
  *   it (the Summary task then only emits the SUMMARY log line). `None` in the committed config until `AssetDirectory` resolves it at deploy time to the
  *   deployment's volume catalog/schema and the [[SummaryConfig.DefaultTable]] table name.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
final case class SummaryConfig(
    target: Option[TableFQN]
)

object SummaryConfig {

  /** Default table name for the per-run summary Delta table when the deployment does not override the [[SummaryConfig.target]] coordinates. */
  val DefaultTable: String = "lakehouse_runs"
}
