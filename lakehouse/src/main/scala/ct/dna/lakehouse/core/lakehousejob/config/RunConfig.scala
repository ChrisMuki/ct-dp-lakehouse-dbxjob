package ct.dna.lakehouse.core.lakehousejob.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/** Identity + sizing of one catalog run. Assembled by `JobSetup` from the catalog's `configFile` (`catalogClass`, `workerCount`) plus the runtime `runId`
  * argument, then published into [[ct.dna.lakehouse.core.lakehousejob.SharedState]].
  *
  * @param catalogClass
  *   fully-qualified package name of the `CatalogSpec` to run.
  * @param runId
  *   the Databricks job run id (`{{job.run_id}}`).
  * @param workerCount
  *   number of in-process worker threads the Orchestrator launches for this run.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
final case class RunConfig(
    catalogClass: String,
    runId: String,
    workerCount: Int
) {
  require(workerCount >= 1, "workerCount must be >= 1")
}
