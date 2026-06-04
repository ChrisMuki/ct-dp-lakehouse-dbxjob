package ct.dna.lakehouse.core.lakehousejob.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ct.dna.lakehouse.core.catalog.TableFQN

/** Runtime knobs consumed by the Orchestrator: the live-status cadence, the per-table watchdog and the per-table results Delta table it flushes at the end of
  * the run. Parsed by `JobSetup` from the `orchestratorConfig=<json>` argument and published into [[ct.dna.lakehouse.core.lakehousejob.SharedState]].
  *
  * @param statusIntervalSeconds
  *   interval at which the Orchestrator logs the consolidated live status block.
  * @param maxTableRuntimeSeconds
  *   hard cap on how long a single table update may run before the watchdog cancels its Spark tag. A cancelled table surfaces as a `TIMED_OUT` row and its
  *   descendants are cascade-skipped. `None` (default) disables the watchdog. Recommended starting point per layer: `sr = 4h`, `dm_md = 1h`, `dw_tx = 1h` —
  *   pick ≈3× the historical P95 to avoid false positives.
  * @param tableRuns
  *   Unity Catalog coordinates of the per-table results Delta table appended by every Worker. `None` until resolved at deploy time, where `AssetDirectory`
  *   defaults it to the deployment's volume catalog/schema and the [[OrchestratorConfig.DefaultTableRunsTable]] table name.
  * @param tableRunsEnabled
  *   when `false`, the per-table Delta writes are skipped. Defaults to `true`.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
final case class OrchestratorConfig(
    statusIntervalSeconds: Int,
    maxTableRuntimeSeconds: Option[Long],
    tableRuns: Option[TableFQN],
    tableRunsEnabled: Boolean
) {
  require(statusIntervalSeconds > 0, "statusIntervalSeconds must be positive")
  require(maxTableRuntimeSeconds.forall(_ > 0), "maxTableRuntimeSeconds must be positive when defined")
  def statusIntervalMs: Long = statusIntervalSeconds * 1000L
  def maxTableRuntimeMs: Option[Long] = maxTableRuntimeSeconds.map(_ * 1000L).filter(_ > 0L)
}

object OrchestratorConfig {

  /** Default table name for the per-table results Delta table when the deployment does not override the [[OrchestratorConfig.tableRuns]] coordinates. */
  val DefaultTableRunsTable: String = "lakehouse_table_runs"
}
