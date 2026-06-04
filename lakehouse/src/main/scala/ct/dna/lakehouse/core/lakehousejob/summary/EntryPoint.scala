package ct.dna.lakehouse.core.lakehousejob.summary

import ct.dna.utils.ExecuteOnce
import ct.dna.utils.logging.PrintlnAppender
import ct.dna.utils.runtime.Task
import ct.dna.utils.runtime.TaskEntryPoint

/** Entry point for the terminal `Summary` step. Has its own entry point on purpose: as a `run_if: ALL_DONE` step it must keep running (and stay GREEN) even
  * after the Orchestrator was aborted. It reads the same in-JVM [[ct.dna.lakehouse.core.lakehousejob.SharedState]] singleton.
  */
object EntryPoint extends TaskEntryPoint {

  /** Task name of the terminal observer step. */
  final val Summary = "Summary"

  override def createInstance(args: Array[String]): Task = {
    PrintlnAppender.replaceConsoleAppendersWithPrintlnAppenders()
    args match {
      case Array(Summary) => SummaryTask()
      case _              => throw new IllegalArgumentException(s"First arg must be the task name '$Summary'")
    }
  }

  protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce { logger.info("Summary shutdown hook triggered") }
}
