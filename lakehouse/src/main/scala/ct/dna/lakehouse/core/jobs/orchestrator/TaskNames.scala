package ct.dna.lakehouse.core.jobs.orchestrator

/** Task naming convention for a catalog job's DAG.
  *
  * The job emits four roles connected by explicit `depends_on` edges:
  *
  * {{{
  *                ┌── Worker_0 ──┐
  *                ├── Worker_1 ──┤
  *   JobSetup ────┤      …       ├──── Summary (run_if: ALL_DONE)
  *                ├── Worker_N ──┤
  *                └── Monitor ───┘
  * }}}
  *
  *   - `JobSetup` — boots the catalog, builds and enqueues the plan, writes the heartbeat `run.json`.
  *   - `Worker_$i` — fleet of poll-loop workers that drain the shared `DagQueue`; each emits START/IN-PROGRESS/END to its Output tab and overwrites its
  *     `worker_<i>.json` heartbeat on every state change.
  *   - `Monitor` — long-lived observer; reads the heartbeat directory every `statusIntervalSeconds` and prints the consolidated `STATUS` line.
  *   - `Summary` — terminal observer (runs `ALL_DONE`), writes the per-run summary row to the configured summary Delta table and emits the final SUMMARY log
  *     line.
  *
  * Names are used as Databricks `taskKey`s and as JVM thread names; nothing about runtime semantics depends on them.
  */
object TaskNames {

  /** System-task `taskKey`s. Kept here so [[ct.dna.lakehouse.core.CatalogWorkflowBuilder]] and the runners stay in sync. */
  val SetupTaskKey: String = "JobSetup"
  val MonitorTaskKey: String = "Monitor"
  val SummaryTaskKey: String = "Summary"

  /** Resolve a worker `taskKey` for index `i` (e.g. `Worker_0`, `Worker_1`, …). */
  def workerName(i: Int): String = s"Worker_$i"
}
