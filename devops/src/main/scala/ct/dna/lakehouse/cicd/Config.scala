package ct.dna.lakehouse.cicd

import ct.dna.lakehouse.cicd.Config.ClusterConfiguration
import ct.dna.lakehouse.cicd.Config.ClusterConfigurationOverride
import ct.dna.lakehouse.cicd.Config.PermissionConfig
import ct.dna.lakehouse.cicd.Config.ScheduleConfig
import ct.dna.lakehouse.core.catalog.TableFQN
import ct.dna.lakehouse.core.lakehousejob.config.OrchestratorConfig
import ct.dna.lakehouse.core.lakehousejob.config.SummaryConfig

/** Committed, in-code deployment configuration — the former `configFiles/{dev,qual,prod}.json` encoded as plain Scala.
  *
  * The model fields and their defaults live on this case class; the concrete per-stage values are assembled by [[Config.stageConfig]] in the companion. The
  * active stage is held in [[Stage]] (a process-wide `SetOnce`) that [[Deploy]] sets from the `stage=` launcher argument, so building the config takes no
  * arguments — `Config.stageConfig` reads the stage that was set.
  */
case class Config(
    host: String,
    /** Unity Catalog catalog backing the Databricks volume for job resources. Defaults to "lakehouse" for prod, "<stage>_lakehouse" for other stages. */
    volumeCatalog: Option[String],
    /** Schema name in the Unity Catalog volume path. Defaults to "default" when not set. */
    volumeSchema: String,
    /** Cluster settings. Entire block is optional — all fields have sensible defaults. Only override what differs from the defaults for your environment. */
    clusterConfiguration: ClusterConfiguration,
    /** Per-catalog overrides on top of [[clusterConfiguration]], keyed by catalog name (e.g. "sr", "dm_md"). Each override field is optional and falls back to
      * the corresponding value from the global `clusterConfiguration` when absent. `sparkConf` is shallow-merged (per-catalog entries win on key collisions).
      * Use to tune executor/driver size and shuffle config differently for catalogs whose workloads differ a lot in table-size distribution (e.g. SR = many
      * small tables, DM = few large tables).
      */
    clusterConfigurationOverrides: Map[String, ClusterConfigurationOverride],
    /** Per-catalog cron schedules, keyed by catalog name (e.g. "sr", "dm_md"). Catalogs without an entry are deployed unscheduled (triggered manually or via
      * API). Use this to give each layer its own cadence.
      */
    schedules: Map[String, ScheduleConfig],
    /** Per-catalog continuous-run config, keyed by catalog name. When present, Databricks restarts the job immediately when a run finishes — useful for
      * tight-cadence catalogs where a cron interval is too coarse. Mutually exclusive with [[schedules]] for the same catalog (Databricks rejects both being
      * set on the same job). Catalogs without an entry behave according to their `schedules` entry (or are manual-trigger only).
      */
    continuous: Map[String, Config.ContinuousConfig],
    /** Number of tables run concurrently per catalog (in-JVM thread parallelism inside the single Databricks WorkerPool task), keyed by catalog name with a
      * default value for catalogs not listed (read via `taskParallelism(catalogName)`). Give layers with many tables more parallelism (e.g. `sr` with hundreds
      * of tables) and small data marts less. NOTE: this is **not** the Spark executor / cluster-worker count — that lives under
      * `clusterConfiguration.maxWorkerNodes`.
      */
    taskParallelism: Map[String, Int],
    /** Runtime knobs for the Orchestrator: status cadence, per-table watchdog and the per-table results Delta table. Serialised as JSON and passed to
      * `JobSetup` as `orchestratorConfig=<json>`. The `tableRuns` coordinates default to the deployment's volume catalog/schema.
      */
    orchestrator: OrchestratorConfig,
    /** Runtime knobs for the terminal Summary step: where (and whether) to write the per-run summary Delta row. Serialised as JSON and passed to `JobSetup` as
      * `summaryConfig=<json>`. The `catalog`/`schema` coordinates default to the deployment's volume catalog/schema.
      */
    summary: SummaryConfig,
    /** "production" (default) or "development". In development mode the Databricks CLI ignores run_as and prefixes the job name with [dev <username>], so the
      * job runs as the deploying identity (PAT owner or OAuth-M2M SP).
      */
    targetMode: String,
    /** Optional list of permissions applied at the target level (i.e. to all resources in the bundle). Each entry grants a group a specific permission level
      * (e.g. CAN_MANAGE_RUN). When empty, no permissions block is emitted.
      */
    permissions: List[PermissionConfig]
)

object Config {

  /** Build the [[Config]] for the active stage (held in [[Stage]], set by [[Deploy]] from the `stage=` launcher argument). The rule of thumb when reading this:
    * a value written once is the **standard** config (identical for dev, qual and prod), and a value wrapped in `dqp(...)` is, by definition, the part that
    * **varies per stage** — the three arguments are the dev / qual / prod variants (see [[Stage.dqp]]).
    */
  def stageConfig: Config = {
    import Stage.dqp

    // DM/MD (and the dw_tx mirror): prod gives them a smaller worker + driver than the big SR profile, with a matching
    // smaller AQE partition count (E32ds_v5 -> 128); dev/qual inherit the global worker + count.
    val dmAndDwOverride = ClusterConfigurationOverride(
      maxWorkerNodes = Some(1),
      nodeTypeId = dqp(None, None, Some("Standard_E32ds_v5")),
      driverNodeTypeId = dqp(None, None, Some("Standard_E8ds_v5")),
      sparkConf = dqp(
        Map.empty,
        Map.empty,
        Map("spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> "128")
      )
    )

    // Same cron per catalog across stages; only the pause status differs (dev/qual deploy paused, prod runs).
    def schedule(cron: String): ScheduleConfig =
      ScheduleConfig(quartzCronExpression = cron, timezoneId = "UTC", pauseStatus = dqp("PAUSED", "PAUSED", "UNPAUSED"))

    Config(
      host = dqp(
        dev = "https://adb-7405616666691350.10.azuredatabricks.net",
        qual = "https://adb-7405617504226685.5.azuredatabricks.net",
        prod = "https://adb-7405608343332957.17.azuredatabricks.net"
      ),
      volumeCatalog = Some(dqp("dev_lakehouse", "qual_lakehouse", "lakehouse")),
      volumeSchema = dqp(
        dev = "sp_1043_edap_dev_dbxjob_lakehouse",
        qual = "sp_1043_edap_qual_dbxjob_lakehouse",
        prod = "sp_1043_edap_prod_dbxjob_lakehouse"
      ),
      clusterConfiguration = ClusterConfiguration(
        sparkVersion = "17.3.x-scala2.13",
        clusterPolicyId = Some(dqp("000AC9F2923A51E1", "001F2C351BFE187D", "0009D8E7AF32CDAF")),
        // Fixed-size cluster: maxWorkerNodes=1 emits autoscale(1,1) -> exactly one worker, still satisfying the policy's
        // SHOULD_USE_AUTOSCALING_INFO requirement.
        maxWorkerNodes = 1,
        // dev/qual: one small E8ds_v5 (8 vCPU). prod: one big E96ds_v5 (96 vCPU).
        nodeTypeId = dqp("Standard_E8ds_v5", "Standard_E8ds_v5", "Standard_E96ds_v5"),
        driverNodeTypeId = dqp("Standard_E8ds_v5", "Standard_E8ds_v5", "Standard_E16ds_v5"),
        // Shared across all stages; only the AQE initial-partition count differs (sized to the worker: 8c -> 32, 96c -> 384).
        // Behavioural knobs (Delta write layout, disk cache) are kept identical across stages so dev/qual exercise the same
        // write/merge behaviour prod relies on; only genuine sizing (partition count, node type) varies per stage.
        sparkConf = Map(
          "spark.scheduler.mode" -> "FAIR",
          "spark.sql.autoBroadcastJoinThreshold" -> "33554432",
          "spark.sql.adaptive.autoBroadcastJoinThreshold" -> "33554432",
          "spark.sql.adaptive.coalescePartitions.minPartitionSize" -> "16MB",
          "spark.sql.objectHashAggregate.sortBased.fallbackThreshold" -> "4096",
          // Behavioural Delta write knobs — identical across stages so dev/qual produce the same output file
          // layout as prod (optimizeWrite coalesces the fan-out, autoCompact compacts small files afterwards).
          // Cheap no-ops on the tiny dev/qual data; essential on the large prod MERGEs.
          "spark.databricks.delta.optimizeWrite.enabled" -> "true",
          "spark.databricks.delta.autoCompact.enabled" -> "true",
          // Disk cache: perf-only (no correctness impact). On in every stage to avoid env drift.
          "spark.databricks.io.cache.enabled" -> "true"
        ) ++ dqp(
          dev = Map("spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> "32"),
          qual = Map("spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> "32"),
          // initialPartitionNum sized to the big prod worker; aggressiveWindowDownS only matters on prod's autoscale-release.
          prod = Map(
            "spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> "384",
            "spark.databricks.aggressiveWindowDownS" -> "600"
          )
        )
      ),
      clusterConfigurationOverrides = Map(
        // sr keeps the MERGE materialize-source knob off in dev/qual (cheap small MERGEs); prod drops it.
        "sr" -> ClusterConfigurationOverride(
          maxWorkerNodes = Some(1),
          sparkConf = dqp(
            dev = Map("spark.databricks.delta.merge.materializeSource" -> "none"),
            qual = Map("spark.databricks.delta.merge.materializeSource" -> "none"),
            prod = Map.empty
          )
        ),
        "dm_md" -> dmAndDwOverride
      ) ++ dqp(
        dev = Map("dw_tx" -> dmAndDwOverride),
        qual = Map.empty, // qual has no dw_tx layer yet
        prod = Map("dw_tx" -> dmAndDwOverride)
      ),
      schedules = Map(
        "sr" -> schedule("0 0 2 * * ?"),
        "dm_md" -> schedule("0 0 4 * * ?")
      ) ++ dqp(
        dev = Map("dw_tx" -> schedule("0 0 3 * * ?")),
        qual = Map.empty,
        prod = Map("dw_tx" -> schedule("0 0 3 * * ?"))
      ),
      continuous = Map.empty,
      permissions = List(
        PermissionConfig(groupName = dqp("dev_lakehouse_workspace_devops", "qual_lakehouse_workspace_devops", "lakehouse_workspace_devops"), level = "CAN_RUN")
      ),
      // statusInterval (60s) is the OrchestratorConfig default; only prod arms the per-table watchdog (20 min).
      orchestrator = OrchestratorConfig(
        statusIntervalSeconds = 60,
        maxTableRuntimeSeconds = dqp(None, None, Some(1200L)),
        tableRuns = dqp(None, None, Some(TableFQN("default_catalog", "default_schema", "lakehouse_table_runs"))),
        tableRunsEnabled = dqp(true, true, true)
      ),
      summary = SummaryConfig(
        target = dqp(None, None, Some(TableFQN("default_catalog", "default_schema", "lakehouse_run_summaries"))),
        enabled = dqp(true, true, true)
      ),
      taskParallelism = dqp(
        dev = Map("sr" -> 20, "dm_md" -> 10, "dw_tx" -> 10),
        qual = Map("sr" -> 20, "dm_md" -> 10),
        prod = Map("sr" -> 40, "dm_md" -> 10, "dw_tx" -> 10)
      ).withDefaultValue(dqp(2, 3, 4)),
      targetMode = "production"
    )
  }

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

}
