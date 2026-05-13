package ct.dna.lakehouse.core.jobs

import ct.dna.lakehouse.core.jobs.orchestrator.TableUpdaterCore
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.ExecuteOnce
import ct.dna.utils.runtime.Configuration
import ct.dna.utils.runtime.Task

/** Single-table Databricks-task entry point.
  *
  * Kept for backward compatibility with the per-table CLI runners (`run-hawk-sr-tables.sh`, `run-hawk-dm-tables.sh`, `run-all-tables.sh`) and any external
  * callers that still wire the old `lakehouse_job.yml` topology.
  *
  * Newer deployments use the catalog-level orchestrator in `ct.dna.lakehouse.core.jobs.orchestrator` which calls the same [[TableUpdaterCore]] helper under the
  * hood, so behaviour stays in sync between the two execution paths.
  */
final class TableUpdaterTask(
    configArg: String,
    packageName: String,
    tableName: String
) extends Task {

  override val name: String = s"TableUpdaterTask-$tableName"
  override val uid: String = s"$packageName.$tableName"

  override def start(): Unit = {
    val parsedConfig =
      Configuration
        .required("rootDir")
        .withSparkConfig
        .build(Array(configArg))

    SparkEnv.ensureInitialized(parsedConfig.getSparkConfig)

    TableUpdaterCore.update(packageName, tableName)
  }

  protected val cleanUpDuringShutdown: ExecuteOnce[Unit] = ExecuteOnce {
    logger.info(s"Graceful shutdown requested for $uid")
  }
}
