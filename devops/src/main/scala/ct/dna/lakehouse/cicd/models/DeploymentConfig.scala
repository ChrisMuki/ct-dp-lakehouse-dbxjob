package ct.dna.lakehouse.cicd.models

import ct.dna.utils.az.auth.AzAuth

case class DeploymentConfig(
    host: String,
    /** Unity Catalog catalog backing the Databricks volume for job resources. Defaults to "lakehouse" for prod, "<stage>_lakehouse" for other stages.
      */
    volumeCatalog: Option[String] = None,
    /** Schema name in the Unity Catalog volume path. Defaults to "default" when not set.
      */
    volumeSchema: String = "default",
    /** Azure authentication for the Databricks CLI during deployment. Supports AzAuth subtypes: ClientSecret (CI/CD), AzureCli (local dev), ManagedIdentity.
      * Deserialized from JSON automatically by the AzAuth Jackson deserializer.
      */
    deploymentAzAuth: AzAuth,

    /** Cluster settings. Entire block is optional — all fields have sensible defaults. Only override what differs from the defaults for your environment.
      */
    clusterConfiguration: DeploymentConfig.ClusterConfiguration = DeploymentConfig.ClusterConfiguration(),
    /** Per-catalog cron schedules, keyed by catalog name (e.g. "sr", "dm_md"). Catalogs without an entry are deployed unscheduled (triggered manually or via
      * API). Use this to give each layer its own cadence.
      */
    schedules: Map[String, DeploymentConfig.ScheduleConfig] = Map.empty,
    /** Per-catalog continuous-run config, keyed by catalog name. When present, Databricks restarts the job immediately when a run finishes — useful for
      * tight-cadence catalogs where a cron interval is too coarse. Mutually exclusive with [[schedules]] for the same catalog (Databricks rejects both being
      * set on the same job). Catalogs without an entry behave according to their `schedules` entry (or are manual-trigger only).
      */
    continuous: Map[String, DeploymentConfig.ContinuousConfig] = Map.empty,
    /** Default number of `Worker_$i` tasks emitted per catalog by `CatalogWorkflowBuilder`. Override per catalog via [[workerCounts]] for layers that need more
      * parallelism (e.g. `sr` with hundreds of tables) or less (small data marts).
      */
    workerCountDefault: Int = 4,
    /** Per-catalog overrides for the number of worker tasks. Missing entries fall back to [[workerCountDefault]]. */
    workerCounts: Map[String, Int] = Map.empty,
    /** Orchestrator-side runtime knobs (idle sleep, status interval, drain timeout, …). Serialised as JSON and passed to every catalog task as
      * `orchestratorConfig=<json>`.
      */
    orchestrator: DeploymentConfig.OrchestratorRuntimeConfig = DeploymentConfig.OrchestratorRuntimeConfig(),
    /** "production" (default) or "development". In development mode the Databricks CLI ignores run_as and prefixes the job name with [dev <username>], so the
      * job runs as the deploying identity (PAT owner or OAuth-M2M SP).
      */
    targetMode: String = "production",
    /** Optional list of permissions applied at the target level (i.e. to all resources in the bundle). Each entry grants a group a specific permission level
      * (e.g. CAN_MANAGE_RUN). When empty, no permissions block is emitted.
      */
    permissions: List[DeploymentConfig.PermissionConfig] = Nil
)

object DeploymentConfig {

  case class ClusterConfiguration(
      /** Databricks Runtime version. Default: latest LTS Scala 2.13 runtime. */
      sparkVersion: String = "17.3.x-scala2.13",
      /** Cluster policy ID. When absent no policy is applied. */
      clusterPolicyId: Option[String] = None,
      /** Maximum autoscale worker count. Default: 4. */
      maxWorkerNodes: Int = 4,
      /** Worker node VM size. Default: Standard_D8ds_v5. */
      nodeTypeId: String = "Standard_D8ds_v5",
      /** Driver node VM size. Default: Standard_D8ds_v5. */
      driverNodeTypeId: String = "Standard_D8ds_v5",
      /** Spark configuration entries written into each job cluster's `new_cluster.spark_conf`. Passed directly to the typed `NewCluster.sparkConf` field in
        * `AssetDirectory.createDatabricksYml()`.
        *
        * Default disables auto-broadcast joins (was previously set at runtime in `TableUpdaterTask`). Override or extend as needed per stage.
        */
      sparkConf: Map[String, String] = Map(
        "spark.sql.autoBroadcastJoinThreshold" -> "-1",
        "spark.sql.adaptive.autoBroadcastJoinThreshold" -> "-1"
      )
  )

  /** Quartz-based cron schedule for the Databricks job. quartzCronExpression — e.g. "0 0 * ? * *" for every hour on the hour. timezoneId — IANA timezone,
    * defaults to "UTC". pauseStatus — "UNPAUSED" (run) or "PAUSED" (skip). Defaults to "UNPAUSED".
    */
  case class ScheduleConfig(
      quartzCronExpression: String,
      timezoneId: String = "UTC",
      pauseStatus: String = "UNPAUSED"
  )

  /** Per-catalog continuous-run config. pauseStatus — "UNPAUSED" (run continuously) or "PAUSED" (deployed but inactive). taskRetryMode — "NEVER" (default) or
    * "ON_FAILURE".
    */
  case class ContinuousConfig(
      pauseStatus: String = "UNPAUSED",
      taskRetryMode: String = "NEVER"
  )

  /** Permission entry for the Databricks Asset Bundle target. groupName — display name of the Databricks group. level — valid values: "CAN_MANAGE", "CAN_RUN",
    * "CAN_VIEW".
    */
  case class PermissionConfig(
      groupName: String,
      level: String
  )

  /** Runtime knobs for the per-catalog orchestrator. Serialised verbatim into the `orchestratorConfig` Spark argument so the runtime `OrchestratorConfig` case
    * class can `mapper.readValue[OrchestratorConfig]` it.
    *
    * Field semantics must stay in sync with `ct.dna.lakehouse.core.jobs.orchestrator.OrchestratorConfig` in the `lakehouse` project.
    */
  case class OrchestratorRuntimeConfig(
      /** How long a worker sleeps when `pollOne()` returns empty (and the queue is not yet drained). */
      idleSleepSeconds: Long = 5,
      /** Interval at which the Monitor logs the consolidated live status line. */
      statusIntervalSeconds: Int = 60,
      /** Hard cap on wall-clock runtime. `0` disables. */
      maxRuntimeSeconds: Long = 0,
      /** Max time the Monitor waits for in-flight workers to drain after the queue empties. `0` waits forever. */
      drainTimeoutSeconds: Long = 0,
      /** Stall detector: signal shutdown when no table outcome has been recorded for this many seconds. `0` disables. */
      noProgressTimeoutSeconds: Long = 0,
      /** Override the Unity Catalog catalog for the summary Delta table. Defaults to `deploymentConfig.volumeCatalog`. */
      summaryCatalog: Option[String] = None,
      /** Override the Unity Catalog schema for the summary Delta table. Defaults to `deploymentConfig.volumeSchema`. */
      summarySchema: Option[String] = None,
      /** Table name for the per-run summary written by the `Summary-Earth` task. */
      summaryTable: String = "lakehouse_runs",
      /** When `false`, the Summary task skips the Delta write and only emits the SUMMARY log line. */
      summaryEnabled: Boolean = true,
      /** Table name for the per-table results table appended by every Worker. Lives in the same `summaryCatalog`/`summarySchema`. */
      tableRunsTable: String = "lakehouse_table_runs",
      /** When `false`, Workers/Summary skip the per-table Delta writes. */
      tableRunsEnabled: Boolean = true,
      /** Override the UC volume directory that hosts live worker heartbeat JSON files. Defaults to `<jobResourcesPath>/heartbeat` (resolved per-stage in
        * `AssetDirectory`). The trailing `/<runId>` is appended at runtime by each task.
        */
      heartbeatDir: Option[String] = None
  )
}
