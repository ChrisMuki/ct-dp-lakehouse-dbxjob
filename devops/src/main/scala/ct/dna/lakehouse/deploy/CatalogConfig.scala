package ct.dna.lakehouse.deploy

import ct.dna.utils.deploy.databrickscli.assetbundle.JobSchedule

/** Complete per-catalog deployment profile: the Databricks cluster shape, the in-JVM table parallelism and the cron trigger. One value is held per catalog in
  * [[Config]] (the `sr` / `dmMd` / `dwTx` fields); they are normally built from one shared base via `.copy(...)` overriding only what differs, so there is no
  * field-level merge anywhere: `AssetDirectory` reads each catalog's value directly.
  *
  * `schedule` is the upstream Databricks asset-bundle type ([[JobSchedule]]) so it flows straight into the generated job with no conversion.
  */
case class CatalogConfig(
    sparkVersion: String,
    clusterPolicyId: String,
    /** Autoscale lower bound. Keep >= 2 for catalogs whose tables use in-memory `localCheckpoint`s: with a single worker the
      * death of that one executor (e.g. a long GC pause) permanently loses every checkpoint block, failing all dependent
      * tasks. Two+ workers spread the blocks so one executor loss is recoverable.
      */
    minWorkerNodes: Int = 1,
    maxWorkerNodes: Int,
    nodeTypeId: String = "Standard_D8ds_v5",
    /** Driver node VM size. */
    driverNodeTypeId: String = "Standard_D8ds_v5",
    /** Spark configuration entries written verbatim into the job cluster's `new_cluster.spark_conf`. */
    sparkConf: Map[String, String] = Map.empty,
    /** Number of tables run concurrently inside the single Orchestrator task's driver JVM (in-JVM thread parallelism — NOT the Spark worker count, which is
      * [[maxWorkerNodes]]). Give layers with many tables more parallelism.
      */
    taskParallelism: Int = 1,
    /** Cron schedule for the catalog's Databricks job. */
    schedule: JobSchedule
)
