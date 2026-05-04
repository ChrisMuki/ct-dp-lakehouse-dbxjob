package ct.dna.lakehouse.core.jobs

import ct.dna.lakehouse.core.framework.internal.TableManager
import ct.dna.lakehouse.core.framework.internal.UpdatedTableProcessor
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.runtime.Configuration
import ct.dna.utils.runtime.Task
import org.apache.spark.sql.SparkSession

final class TableUpdaterTask(
    configArg: String,
    packageName: String,
    tableName: String
) extends Task {

  val logicVersion: String =
    "1.0" // to be used for compatibility checks between code and persisted metadata, e.g. to enforce full recreate if logic has changed in a non-compatible way

  val forceRecreate: Boolean = false // maybe something proper like version checks between target umd and current code?
  // not yet properly supported by the framework, will be used to enforce complete recalculation of the target
  // for now we do a recreateAsTarget as workaround

  override val name: String = s"TableUpdaterTask-$tableName"
  override val uid: String = s"$packageName.$tableName"

  override def executeTask: Unit = {

    val runId = s"run_${System.currentTimeMillis()}"

    // Parse config + Spark config
    val parsedConfig =
      Configuration
        .required("rootDir")
        .withSparkConfig
        .build(Array(configArg))

    // Initialize Spark
    SparkEnv.ensureInitialized(parsedConfig.getSparkConfig)
    val spark = SparkSession.active

    // Resolve TableSpec dynamically // to be moved to library later
    val tableSpec = {
      val foundClass = Class.forName(s"$packageName.$tableName$$")
      if (foundClass.getName.endsWith("$")) {
        val module = foundClass.getField("MODULE$").get(null)
        module match {
          case ts: Updated => Some(ts)
          case _           => None
        }
      } else None
    }.getOrElse(throw new ClassNotFoundException(s"Could not find TableSpec for $packageName.$tableName"))

    tableSpec.validateToRoot()

    if (forceRecreate)
      TableManager(runId).createUpdated(tableSpec, asTarget = true)
    else
      TableManager(runId).ensureUpdatedTableAsNeeded(tableSpec, asTarget = true)

    // Execute update
    UpdatedTableProcessor(runId).update(tableSpec, logicVersion)
    // later on we will pass forceRecreate to the update method

    logger.info(s"Update completed for $uid")
  }

  override def shutdownHook: Unit =
    logger.info(s"Graceful shutdown requested for $uid")
  // println(s"[TASK] Graceful shutdown requested for $uid")
}
