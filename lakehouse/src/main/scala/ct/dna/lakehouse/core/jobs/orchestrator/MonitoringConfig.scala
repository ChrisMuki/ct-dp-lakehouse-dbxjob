package ct.dna.lakehouse.core.jobs.orchestrator

/** Runtime knobs for the catalog orchestrator. Provided once as a JSON-encoded property in the deployment config and read by every task via
  * [[ct.dna.utils.runtime.Configuration]].
  *
  * Field set must stay in sync with `ct.dna.lakehouse.cicd.models.DeploymentConfig.MonitoringConfig` in the `devops` project.
  */
final case class MonitoringConfig(
    /** How long a worker sleeps when the shared queue returns empty (and the queue is not yet drained). */
    idleSleepSeconds: Long = 5,
    /** Interval at which the WorkerPool task logs the consolidated live status block. */
    statusIntervalSeconds: Int = 60,
    /** Unity Catalog catalog hosting the per-run summary Delta table. `None` reuses the deployment's `volumeCatalog`. */
    summaryCatalog: Option[String] = None,
    /** Unity Catalog schema hosting the per-run summary Delta table. `None` reuses the deployment's `volumeSchema`. */
    summarySchema: Option[String] = None,
    /** Table name for the per-run summary written by the `Summary-Earth` task. */
    summaryTable: String = "lakehouse_runs",
    /** When `false`, the Summary task skips the Delta write and only emits the SUMMARY log line. Defaults to `true`. */
    summaryEnabled: Boolean = true,
    /** Table name for the per-table results table appended by every Worker (and by Summary for SKIPPED rows that no Worker ever picked up). Lives in the same
      * `summaryCatalog`/`summarySchema` as the run summary. Set `tableRunsEnabled = false` to suppress the writes entirely.
      */
    tableRunsTable: String = "lakehouse_table_runs",
    /** When `false`, Workers and Summary skip the per-table Delta writes. Defaults to `true`. */
    tableRunsEnabled: Boolean = true,
    /** Hard cap on how long a single table update is allowed to run before the WorkerPool status thread cancels its Spark job group
      * (`runId/<catalog>.<schema>.<table>`). A cancelled table surfaces as a `TIMED_OUT` row in `lakehouse_table_runs` and its descendants are cascade-skipped
      * just like a normal failure. `None` (default) disables the watchdog and preserves pre-existing behaviour. Recommended starting point per layer:
      * `sr = 4h`, `dm_md = 1h`, `dw_tx = 1h` — pick ≈3× the historical P95 from the `lakehouse_table_runs` Delta table to avoid false positives.
      */
    maxTableRuntimeSeconds: Option[Long] = None
)
