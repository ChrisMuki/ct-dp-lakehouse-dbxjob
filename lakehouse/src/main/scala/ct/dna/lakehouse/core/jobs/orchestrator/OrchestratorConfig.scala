package ct.dna.lakehouse.core.jobs.orchestrator

/** Runtime knobs for the catalog orchestrator. Provided once as a JSON-encoded property in the deployment config and read by every task via
  * [[ct.dna.utils.runtime.Configuration]].
  *
  * Field set must stay in sync with `ct.dna.lakehouse.cicd.models.DeploymentConfig.OrchestratorRuntimeConfig` in the `devops` project.
  */
final case class OrchestratorConfig(
    /** Number of `Worker` tasks Setup expects to see (driver-side parallelism for the shared `DagQueue`). Used only for logging — the actual Databricks task
      * count comes from `CatalogWorkflowBuilder`.
      */
    workerCount: Int = 4,
    /** How long a worker sleeps when `DagQueue.pollOne()` returns empty (and the queue is not yet drained). */
    idleSleepSeconds: Long = 5,
    /** Interval at which the Monitor task logs the consolidated live status line. */
    statusIntervalSeconds: Int = 60,
    /** Optional hard cap on wall-clock runtime. `0` means no cap. After the cap the Monitor flips `keepRunning=false` so workers stop polling. */
    maxRuntimeSeconds: Long = 0,
    /** Maximum time the Monitor waits for in-flight tables to drain after the queue empties. `0` means wait forever. */
    drainTimeoutSeconds: Long = 0,
    /** Cancel the run when no table outcome has been recorded for this many seconds. Acts as a heartbeat-based stall detector that is independent of the
      * single-table drain timeout. `0` disables.
      */
    noProgressTimeoutSeconds: Long = 0,
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
    /** Unity Catalog volume directory used for live status heartbeats (one tiny JSON file per Worker, plus a `run.json` written by JobSetup). The Monitor task
      * lists these files every `statusIntervalSeconds`. `None` disables the live monitor (Monitor task will exit cleanly).
      */
    heartbeatDir: Option[String] = None
)
