package ct.dna.lakehouse.core.jobs.orchestrator

/** Task naming convention for a catalog job's DAG.
  *
  * The job emits three roles connected by explicit `depends_on` edges:
  *
  * {{{
  *   JobSetup ────▶ Worker (WorkerPool) ────▶ Summary (run_if: ALL_DONE)
  * }}}
  *
  *   - `JobSetup` — boots the catalog, builds and enqueues the plan.
  *   - `Worker` — single Databricks task that runs `poolSize` worker threads draining the shared `DagQueue`, plus an in-process status reporter that emits the
  *     consolidated `STATUS` block to the same Output tab.
  *   - `Summary` — terminal observer (runs `ALL_DONE`), writes the per-run summary row to the configured summary Delta table and emits the final SUMMARY log
  *     line.
  *
  * Names are used as Databricks `taskKey`s and as JVM thread names; nothing about runtime semantics depends on them.
  */
object TaskNames {

  /** System-task `taskKey`s. Kept here so [[ct.dna.lakehouse.core.CatalogWorkflowBuilder]] and the runners stay in sync. */
  val SetupTaskKey: String = "JobSetup"
  val SummaryTaskKey: String = "Summary"

  /** Single-task replacement for the historical `Worker_0 … Worker_N` fan-out. Runs `poolSize` worker threads + one status-reporter thread in the same
    * driver-REPL JVM, so the bundle only declares one Databricks task regardless of pool size.
    */
  val WorkerTaskKey: String = "Worker"

  /** Resolve a worker thread name for index `i` within a pool of `poolSize` (e.g. `task-00`, `task-01`, …). Zero-padded to at least 2 digits so logs stay
    * column-aligned. These names are also used as Spark `setJobGroup` description prefix so individual table updates can be located in the Spark UI.
    */
  def workerName(i: Int, poolSize: Int): String = {
    val width = math.max(2, math.max(1, poolSize - 1).toString.length)
    s"task-${"%0" + width + "d" format i}"
  }
}
