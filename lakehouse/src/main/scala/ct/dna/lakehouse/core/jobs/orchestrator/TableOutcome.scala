package ct.dna.lakehouse.core.jobs.orchestrator

import ct.dna.lakehouse.core.model.TableID

/** Terminal status of a single table within an orchestrated catalog run. */
sealed trait TableOutcome

object TableOutcome {

  /** Table was successfully (re)materialised by `TableUpdaterCore`. */
  case object Updated extends TableOutcome

  /** Table was not run because at least one ancestor in the dependency DAG failed. The first such failing ancestor is reported. */
  final case class SkippedByAncestor(ancestor: TableID) extends TableOutcome

  /** `TableUpdaterCore.update` threw. The captured throwable is kept for the final status line. */
  final case class Failed(ex: Throwable) extends TableOutcome

  def isTerminalSuccess(o: TableOutcome): Boolean = o match {
    case Updated => true
    case _       => false
  }
}

/** Marker exception used by the WorkerPool watchdog to signal that a table was cancelled because it exceeded `MonitoringConfig.maxTableRuntimeSeconds`. Wraps
  * inside a regular [[TableOutcome.Failed]] so existing match sites keep compiling, but is detected in `WorkerPoolTaskRunner` to write a distinct `TIMED_OUT`
  * status row to `lakehouse_table_runs` (see [[TableRunRow.Status_TimedOut]]).
  */
final class TableTimeoutException(val tableId: TableID, val elapsedMs: Long, val limitMs: Long)
    extends RuntimeException(
      s"Table $tableId cancelled by watchdog after ${elapsedMs / 1000}s (limit ${limitMs / 1000}s)"
    )
