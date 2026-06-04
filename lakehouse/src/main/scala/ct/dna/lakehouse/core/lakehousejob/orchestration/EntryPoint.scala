package ct.dna.lakehouse.core.lakehousejob.orchestration

import ct.dna.utils.ExecuteOnce
import ct.dna.utils.logging.PrintlnAppender
import ct.dna.utils.runtime.Task
import ct.dna.utils.runtime.TaskEntryPoint

/** Shared entry point for the two driver-step tasks of a per-catalog lakehouse job: `JobSetup` and `Orchestrator`. Both run in the same driver-REPL JVM
  * (`run_as_repl=true`) and communicate only through the [[ct.dna.lakehouse.core.lakehousejob.SharedState]] singleton. The task is selected by the leading
  * task-name argument; `JobSetup` parses its own config from the remaining args. The terminal `Summary` step has its own entry point
  * ([[ct.dna.lakehouse.core.lakehousejob.summary.EntryPoint]]) so it survives an aborted Orchestrator.
  */
object EntryPoint extends TaskEntryPoint {

  /** Task name */
  final val JobSetup = "JobSetup"
  final val Orchestrator = "Orchestrator"

  override def createInstance(args: Array[String]): Task = {
    PrintlnAppender.replaceConsoleAppendersWithPrintlnAppenders()
    args match {
      case Array(JobSetup, tail @ _*) => JobSetupTask().withRuntimeArguments(tail)
      case Array(Orchestrator)        => OrchestratorTask()
      case Array(other, _*)           => throw new IllegalArgumentException(s"Unknown task name '$other' for orchestration entry point")
    }
  }

  protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce { logger.info("lakehousejob orchestration shutdown hook triggered") }
}
