package ct.dna.lakehouse.deploy

import ct.dna.lakehouse.core.catalog.TableFQN
import ct.dna.lakehouse.core.lakehousejob.config.OrchestratorConfig
import ct.dna.lakehouse.core.lakehousejob.config.SummaryConfig
import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.lakehouse.dm_md.{_catalogSpec => dmMdSpec}
import ct.dna.lakehouse.dw_tx.{_catalogSpec => dwTxSpec}
import ct.dna.lakehouse.sr.{_catalogSpec => srSpec}
import ct.dna.utils.deploy.databrickscli.assetbundle.JobSchedule
import ct.dna.utils.deploy.databrickscli.assetbundle.Permission

/** Committed, in-code deployment configuration — the former `configFiles/{dev,qual,prod}.json` encoded as plain Scala.
  *
  * The model fields and their defaults live on this case class; the concrete per-stage values are assembled by [[Config.stageConfig]] in the companion. The
  * active stage is held in [[Stage]] (a process-wide `SetOnce`) that [[Deploy]] sets from the `stage=` launcher argument, so building the config takes no
  * arguments — `Config.stageConfig` reads the stage that was set.
  */
case class Config(
    host: String,
    /** Unity Catalog catalog backing the Databricks volume for job resources. */
    volumeCatalog: String,
    /** Schema name in the Unity Catalog volume path. Defaults to "default" when not set. */
    volumeSchema: String,
    /** Deployment profile for the `sr` catalog job: cluster shape, in-JVM table parallelism and trigger. */
    sr: CatalogConfig,
    /** Deployment profile for the `dm_md` catalog job. */
    dmMd: CatalogConfig,
    /** Deployment profile for the `dw_tx` catalog job. */
    dwTx: CatalogConfig,
    /** Runtime knobs for the Orchestrator: status cadence, per-table watchdog and the per-table results Delta table. Written into each catalog's
      * `config-<catalog>.json` and read by `JobSetup`. The `tableRuns` coordinates default to the deployment's volume catalog/schema.
      */
    orchestrator: OrchestratorConfig,
    /** Runtime knobs for the terminal Summary step: where (and whether) to write the per-run summary Delta row. Written into each catalog's
      * `config-<catalog>.json` and read by `JobSetup`. The `catalog`/`schema` coordinates default to the deployment's volume catalog/schema.
      */
    summary: SummaryConfig,
    /** "production" (default) or "development". In development mode the Databricks CLI ignores run_as and prefixes the job name with [dev <username>], so the
      * job runs as the deploying identity (PAT owner or OAuth-M2M SP).
      */
    targetMode: String,
    /** Optional list of permissions applied at the target level (i.e. to all resources in the bundle). Each entry grants a group a specific permission level
      * (e.g. CAN_MANAGE_RUN). When empty, no permissions block is emitted.
      */
    permissions: List[Permission]
) {

  /** The catalog jobs to deploy: each catalog paired with its config. Single source of truth for *which* catalogs are deployed and with what profile. */
  def catalogConfig: List[(CatalogSpec, CatalogConfig)] =
    List(srSpec -> sr, dmMdSpec -> dmMd, dwTxSpec -> dwTx)
}

object Config {

  /** Build the [[Config]] for the active stage (held in [[Stage]], set by [[Deploy]] from the `stage=` launcher argument). The rule of thumb when reading this:
    * a value written once is the **standard** config (identical for dev, qual and prod), and a value wrapped in `dqp(...)` is, by definition, the part that
    * **varies per stage** — the three arguments are the dev / qual / prod variants (see [[Stage.dqp]]).
    */
  def stageConfig: Config = {
    import Stage.dqp

    val host = dqp(
      dev = "https://adb-7405616666691350.10.azuredatabricks.net",
      qual = "https://adb-7405617504226685.5.azuredatabricks.net",
      prod = "https://adb-7405608343332957.17.azuredatabricks.net"
    )

    val volumeCatalog = s"${dqp("dev_", "qual_", "")}lakehouse"
    val volumeSchema = s"sp_1043_edap_${dqp("dev", "qual", "prod")}_dbxjob_lakehouse"

    val srConfig = CatalogConfig(
      sparkVersion = "17.3.x-scala2.13",
      clusterPolicyId = dqp("000AC9F2923A51E1", "001F2C351BFE187D", "0009D8E7AF32CDAF"),
      // prod: 2-3 smaller E32 workers instead of one fat E96. The single-E96 ran ~32 tables in one 96-core JVM whose
      // ~111GB heap hit GC pauses long enough (155s) to time out the executor heartbeat; its death wiped every in-memory
      // localCheckpoint and failed all dependent tasks (CHECKPOINT_RDD_BLOCK_ID_NOT_FOUND). Splitting the same ~64-96
      // aggregate cores across 2-3 JVMs keeps heaps small (shorter GC) and spreads checkpoints so one executor loss is
      // survivable. min=2 guarantees that redundancy from the start; large MERGEs are distributed sort-merge joins and
      // scale across the smaller nodes' aggregate capacity just as well.
      minWorkerNodes = dqp(dev_qual = 1, prod = 2),
      maxWorkerNodes = dqp(dev_qual = 2, prod = 3),
      nodeTypeId = dqp(dev_qual = "Standard_E16ds_v5", prod = "Standard_E48ds_v5"),
      driverNodeTypeId = dqp(dev_qual = "Standard_E8ds_v5", prod = "Standard_E16ds_v5"),
      sparkConf = Map(
        "spark.scheduler.mode" -> "FAIR",
        // Layered FAIR pools (lakehouse-0 .. lakehouse-6, weights 10^i) only set each pool's *fair share* — which pool
        // wins newly freed cores. Databricks task preemption is a separate mechanism that actively KILLS running tasks of
        // an under-share pool to hand its cores to a heavier one; with the 10^i spread that fires on almost any overlap,
        // producing a flood of "preempted by scheduler" task restarts (wasted work, no correctness gain). Disable it: the
        // weights still prioritise later layers for free cores, but in-flight tasks are never torn down.
        "spark.databricks.preemption.enabled" -> "false",
        "spark.databricks.aggressiveWindowDownS" -> "600",
        "spark.sql.autoBroadcastJoinThreshold" -> "33554432",
        "spark.sql.adaptive.autoBroadcastJoinThreshold" -> "33554432",
        "spark.sql.adaptive.coalescePartitions.minPartitionSize" -> "16MB",
        "spark.sql.objectHashAggregate.sortBased.fallbackThreshold" -> "4096",
        // Read split size. The CDF partial aggregation runs PRE-shuffle on the input partitions and does not
        // shrink the data here (the wide `first/max` buffer carries every column, ~1 row per group), so it is pure
        // per-task CPU. Splitting the read into more, smaller partitions parallelises exactly that step — without an
        // extra shuffle. 32MB takes the ~1.3GB read from ~10 to ~40 tasks; drop to 16MB to double it again. No-op on
        // the tiny dev/qual data (files already below the limit), so it stays uniform across stages.
        "spark.sql.files.maxPartitionBytes" -> (32 * 1024 * 1024).toString,
        // Target size of each POST-shuffle partition (AQE). Governs the partition count of the final aggregate and
        // the MERGE write — the other half of "make the operations smaller". 32MB sits above the 16MB coalesce floor
        // above, so AQE coalesces toward 32MB instead of the 64MB default → more, smaller post-shuffle tasks.
        "spark.sql.adaptive.advisoryPartitionSizeInBytes" -> (32 * 1024 * 1024).toString,
        // Behavioural Delta write knobs — identical across stages so dev/qual produce the same output file
        // layout as prod (optimizeWrite coalesces the fan-out, autoCompact compacts small files afterwards).
        // Cheap no-ops on the tiny dev/qual data; essential on the large prod MERGEs.
        "spark.databricks.delta.optimizeWrite.enabled" -> "true",
        "spark.databricks.delta.autoCompact.enabled" -> "true",
        // Disk cache: perf-only (no correctness impact). On in every stage to avoid env drift.
        "spark.databricks.io.cache.enabled" -> "true"
      ) ++ dqp(
        dev_qual = Map(
          "spark.databricks.delta.merge.materializeSource" -> "none",
          "spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> (1 * 16 * 4).toString
        ),
        prod = Map(
          "spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> (1 * 96 * 4).toString
        )
      ),
      taskParallelism = dqp(dev_qual = 8 * 2, prod = 48),
      runtimeEngine = "PHOTON",
      schedule = JobSchedule(quartzCronExpression = "0 0 2 * * ?", timezoneId = "UTC", pauseStatus = dqp("PAUSED", "UNPAUSED"))
    )

    val dmConfig = srConfig.copy(
      taskParallelism = dqp(dev_qual = 4 * 2, prod = 8 * 2),
      nodeTypeId = dqp(dev_qual = "Standard_E8ds_v5", prod = "Standard_E16ds_v5"),
      driverNodeTypeId = dqp(dev_qual = "Standard_E4ds_v5", prod = "Standard_E8ds_v5"),
      sparkConf = srConfig.sparkConf ++ dqp(
        dev_qual = Map("spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> (1 * 8 * 4).toString),
        prod = Map("spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> (1 * 32 * 4).toString)
      ),
      schedule = srConfig.schedule.copy(quartzCronExpression = "0 0 4 * * ?")
    )

    val dwConfig = dmConfig.copy(schedule = srConfig.schedule.copy(quartzCronExpression = "0 0 3 * * ?"))

    Config(
      host = host,
      volumeCatalog = volumeCatalog,
      volumeSchema = volumeSchema,
      sr = srConfig,
      dmMd = dmConfig,
      dwTx = dwConfig,
      orchestrator = OrchestratorConfig(
        statusIntervalSeconds = 120,
        maxTableRuntimeSeconds = Some(1800L),
        tableRuns = Some(TableFQN(volumeCatalog, volumeSchema, OrchestratorConfig.DefaultTableRunsTable))
      ),
      summary = SummaryConfig(
        target = Some(TableFQN(volumeCatalog, volumeSchema, SummaryConfig.DefaultTable))
      ),
      permissions = List(
        Permission(groupName = Some(dqp("dev_lakehouse_workspace_devops", "qual_lakehouse_workspace_devops", "lakehouse_workspace_devops")), level = "CAN_RUN")
      ),
      targetMode = "production"
    )
  }
}
