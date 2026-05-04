package ct.dna.lakehouse.core.jobs

import ct.dna.utils.runtime.Task
import ct.dna.utils.runtime.TaskEntryPoint

object TableUpdaterEntryPoint extends TaskEntryPoint {

  override def createInstance(args: Array[String]): Task = {
    require(
      args.length == 3,
      "usage: <configFile=...> <package_name> <target_table_name>"
    )

    new TableUpdaterTask(
      configArg = args(0),
      packageName = args(1),
      tableName = args(2)
    )
  }

  override def shutdownHook: Unit = {
    logger.info("TableUpdaterEntryPoint shutdown hook triggered")
  }
}
