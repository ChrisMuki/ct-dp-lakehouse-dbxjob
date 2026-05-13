package ct.dna.lakehouse.core.jobs.orchestrator

import ct.dna.lakehouse.core.framework.internal.TableManager
import ct.dna.lakehouse.core.framework.internal.UpdatedTableProcessor
import ct.dna.lakehouse.core.model.Updated
import ct.dna.utils.logging.LoggingTrait

/** Core "update one table" routine shared by:
  *   - the legacy single-table `ct.dna.lakehouse.core.jobs.TableUpdaterTask` (still used by `run-hawk-*.sh` and the static-DAB code paths),
  *   - the new `WorkerTask` that pulls work from a `DagQueue` (one Databricks task per worker, many tables per task).
  *
  * Spark **must** be initialised by the caller (Setup / TableUpdaterTask) before this is invoked. The function is intentionally side-effect heavy and runs on
  * the driver JVM.
  */
object TableUpdaterCore extends LoggingTrait {

  /** Compatibility / migration marker. Persisted alongside the metadata; bumped only when the table-update logic changes in a way that requires a full
    * recompute. Mirrors the previous in-class constant in `TableUpdaterTask`.
    */
  val logicVersion: String = "1.0"

  /** Update or create the target table identified by `packageName.tableName`.
    *
    * Resolves the `TableSpec` singleton object via reflection, validates the model up to the catalog root, makes sure the underlying Delta table exists, then
    * calls `UpdatedTableProcessor.update`.
    *
    * @param packageName
    *   FQCN of the schema package (e.g. `ct.dna.lakehouse.sr.ct_gbl_e32`)
    * @param tableName
    *   `TableSpec` object name (matches the Scala `object` identifier)
    * @param forceRecreate
    *   if true the table is rebuilt from scratch ("as target")
    */
  def update(packageName: String, tableName: String, forceRecreate: Boolean = false): Unit = {

    val runId = s"run_${System.currentTimeMillis()}"

    val tableSpec = {
      val foundClass = Class.forName(s"$packageName.$tableName$$")
      val module = foundClass.getField("MODULE$").get(null)
      module match {
        case ts: Updated => Some(ts)
        case _           => None
      }
    }.getOrElse(throw new ClassNotFoundException(s"Could not find Updated TableSpec for $packageName.$tableName"))

    tableSpec.validateToRoot()

    if (forceRecreate)
      TableManager(runId).createUpdated(tableSpec, asTarget = true)
    else
      TableManager(runId).ensureUpdatedTableAsNeeded(tableSpec, asTarget = true)

    UpdatedTableProcessor(runId).update(tableSpec, logicVersion)

    logger.info(s"Update completed for $packageName.$tableName (runId=$runId)")
  }
}
