package ct.dna.lakehouse.core.lakehousejob.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/** Runtime knobs consumed by the terminal Summary step: where (and whether) to write the per-run summary Delta row. Parsed by `JobSetup` from the
  * `summaryConfig=<json>` argument and published into [[ct.dna.lakehouse.core.lakehousejob.SharedState]].
  *
  * @param target
  *   Unity Catalog coordinates of the per-run summary Delta table written by the Summary task. `None` until resolved at deploy time, where `AssetDirectory`
  *   defaults it to the deployment's volume catalog/schema and the [[SummaryConfig.DefaultTable]] table name.
  * @param enabled
  *   when `false`, the Summary task skips the Delta write and only emits the SUMMARY log line. Defaults to `true`.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
final case class SummaryConfig(
    target: Option[TableRef] = None,
    enabled: Boolean = true
)

object SummaryConfig {

  /** Default table name for the per-run summary Delta table when the deployment does not override the [[SummaryConfig.target]] coordinates. */
  val DefaultTable: String = "lakehouse_runs"
}
