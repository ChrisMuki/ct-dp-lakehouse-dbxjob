package ct.dna.lakehouse.cicd.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ct.dna.utils.az.auth.AzAuth

@JsonIgnoreProperties(ignoreUnknown = true)
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
    /** Per-catalog overrides on top of [[clusterConfiguration]], keyed by catalog name (e.g. "sr", "dm_md"). Each override field is optional and falls back to
      * the corresponding value from the global `clusterConfiguration` when absent. `sparkConf` is shallow-merged (per-catalog entries win on key collisions).
      * Use to tune executor/driver size and shuffle config differently for catalogs whose workloads differ a lot in table-size distribution (e.g. SR = many
      * small tables, DM = few large tables).
      */
    clusterConfigurations: Map[String, DeploymentConfig.ClusterConfigurationOverride] = Map.empty,
    /** Per-catalog cron schedules, keyed by catalog name (e.g. "sr", "dm_md"). Catalogs without an entry are deployed unscheduled (triggered manually or via
      * API). Use this to give each layer its own cadence.
      */
    schedules: Map[String, DeploymentConfig.ScheduleConfig] = Map.empty,
    /** Per-catalog continuous-run config, keyed by catalog name. When present, Databricks restarts the job immediately when a run finishes — useful for
      * tight-cadence catalogs where a cron interval is too coarse. Mutually exclusive with [[schedules]] for the same catalog (Databricks rejects both being
      * set on the same job). Catalogs without an entry behave according to their `schedules` entry (or are manual-trigger only).
      */
    continuous: Map[String, DeploymentConfig.ContinuousConfig] = Map.empty,
    /** Default number of tables run concurrently per catalog (in-JVM thread parallelism inside the single Databricks WorkerPool task). Override per catalog via
      * [[taskParallelism]] for layers that need more parallelism (e.g. `sr` with hundreds of tables) or less (small data marts). NOTE: this is **not** the
      * Spark executor / cluster-worker count — that lives under `clusterConfiguration.maxWorkerNodes`.
      */
    taskParallelismDefault: Int = 4,
    /** Per-catalog overrides for the in-JVM table parallelism. Missing entries fall back to [[taskParallelismDefault]]. */
    taskParallelism: Map[String, Int] = Map.empty,
    /** Logging / monitoring runtime knobs (idle sleep, status interval, summary table coordinates, …). Serialised as JSON and passed to every catalog task as
      * `monitoringConfig=<json>`.
      */
    monitoring: DeploymentConfig.MonitoringConfig = DeploymentConfig.MonitoringConfig(),
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

  @JsonIgnoreProperties(ignoreUnknown = true)
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

  /** Optional per-catalog override applied on top of the global [[ClusterConfiguration]]. Every field is optional; absent fields inherit the global value. The
    * `sparkConf` map is shallow-merged with per-catalog entries winning on key collisions, so you can add a few extra knobs without restating the whole global
    * map.
    */
  @JsonIgnoreProperties(ignoreUnknown = true)
  case class ClusterConfigurationOverride(
      sparkVersion: Option[String] = None,
      clusterPolicyId: Option[String] = None,
      maxWorkerNodes: Option[Int] = None,
      nodeTypeId: Option[String] = None,
      driverNodeTypeId: Option[String] = None,
      sparkConf: Map[String, String] = Map.empty
  )

  /** Quartz-based cron schedule for the Databricks job. quartzCronExpression — e.g. "0 0 * ? * *" for every hour on the hour. timezoneId — IANA timezone,
    * defaults to "UTC". pauseStatus — "UNPAUSED" (run) or "PAUSED" (skip). Defaults to "UNPAUSED".
    */
  @JsonIgnoreProperties(ignoreUnknown = true)
  case class ScheduleConfig(
      quartzCronExpression: String,
      timezoneId: String = "UTC",
      pauseStatus: String = "UNPAUSED"
  )

  /** Per-catalog continuous-run config. pauseStatus — "UNPAUSED" (run continuously) or "PAUSED" (deployed but inactive). taskRetryMode — "NEVER" (default) or
    * "ON_FAILURE".
    */
  @JsonIgnoreProperties(ignoreUnknown = true)
  case class ContinuousConfig(
      pauseStatus: String = "UNPAUSED",
      taskRetryMode: String = "NEVER"
  )

  /** Permission entry for the Databricks Asset Bundle target. groupName — display name of the Databricks group. level — valid values: "CAN_MANAGE", "CAN_RUN",
    * "CAN_VIEW".
    */
  @JsonIgnoreProperties(ignoreUnknown = true)
  case class PermissionConfig(
      groupName: String,
      level: String
  )

  /** Logging / monitoring runtime knobs for a catalog job. Serialised verbatim into the `monitoringConfig` Spark argument so the runtime `MonitoringConfig`
    * case class can `mapper.readValue[MonitoringConfig]` it.
    *
    * Field semantics must stay in sync with `ct.dna.lakehouse.core.jobs.orchestrator.MonitoringConfig` in the `lakehouse` project.
    */
  @JsonIgnoreProperties(ignoreUnknown = true)
  case class MonitoringConfig(
      /** How long an in-JVM worker sleeps when the shared queue returns empty (and the queue is not yet drained). */
      idleSleepSeconds: Long = 5,
      /** Interval at which the WorkerPool task logs the consolidated live status block. */
      statusIntervalSeconds: Int = 60,
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
      /** Hard cap (seconds) on a single table update before the WorkerPool watchdog cancels its Spark job group. `None` (omit from JSON) disables the watchdog.
        * Field semantics mirror `MonitoringConfig.maxTableRuntimeSeconds` in the `lakehouse` project.
        */
      maxTableRuntimeSeconds: Option[Long] = None
  )
}
