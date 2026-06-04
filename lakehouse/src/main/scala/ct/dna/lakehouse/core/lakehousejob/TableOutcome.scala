package ct.dna.lakehouse.core.lakehousejob

import ct.dna.lakehouse.core.model.TableID

/** Terminal status of a single table within an orchestrated catalog run. Recorded by Workers into [[SharedState.results]] and read by the Summary step. */
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
