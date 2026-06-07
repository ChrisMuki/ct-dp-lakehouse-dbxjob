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
      // prod: 4 smaller E48 workers instead of 2 fat E96 — same 192 aggregate cores, but each JVM heap roughly halves
      // (E48 ≈ 384GB node vs E96 ≈ 672GB), so the Full-GC pauses that were timing out the 120s executor heartbeat
      // (observed 125-176s on the E96; ~29% of total cluster runtime lost to GC, 800+ major GCs per executor) shrink
      // back under the heartbeat window. More JVMs also spread the in-memory localCheckpoints, so losing one executor is
      // survivable instead of wiping a whole 96-core node (CHECKPOINT_RDD_BLOCK_ID_NOT_FOUND). Large MERGEs are
      // distributed sort-merge joins and scale across the 4 smaller nodes' aggregate capacity just as well. dev/qual
      // stay at 2× E16 (tiny data). dm_md/dw_tx override the worker count back to 2 below (they don't run sr's MERGEs).
      minWorkerNodes = dqp(dev_qual = 2, prod = 4),
      maxWorkerNodes = dqp(dev_qual = 2, prod = 4),
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
        // Coalesce floor: AQE never shrinks a post-shuffle partition below this. Kept at half the advisory size below
        // (8MB vs 16MB) so coalescing still has head-room after the advisory was lowered from 32MB.
        "spark.sql.adaptive.coalescePartitions.minPartitionSize" -> "8MB",
        "spark.sql.objectHashAggregate.sortBased.fallbackThreshold" -> "4096",
        // Read split size. The CDF partial aggregation runs PRE-shuffle on the input partitions and does not
        // shrink the data here (the wide `first/max` buffer carries every column, ~1 row per group), so it is pure
        // per-task CPU. Splitting the read into more, smaller partitions parallelises exactly that step — without an
        // extra shuffle. 32MB takes the ~1.3GB read from ~10 to ~40 tasks; drop to 16MB to double it again. No-op on
        // the tiny dev/qual data (files already below the limit), so it stays uniform across stages.
        "spark.sql.files.maxPartitionBytes" -> (32 * 1024 * 1024).toString,
        // Target size of each POST-shuffle partition (AQE). Governs the partition count of the final aggregate and
        // the MERGE write — the other half of "make the operations smaller". Lowered 32MB → 16MB to halve per-task
        // shuffle/merge memory (the prod sr MERGE spilled ~276GB in one stage and ran 44% in GC); 16MB still sits above
        // the 8MB coalesce floor above, so AQE coalesces toward 16MB → ~2× more, smaller post-shuffle tasks. Benchmark lever.
        "spark.sql.adaptive.advisoryPartitionSizeInBytes" -> (16 * 1024 * 1024).toString,
        // Behavioural Delta write knobs — identical across stages so dev/qual produce the same output file
        // layout as prod (optimizeWrite coalesces the fan-out, autoCompact compacts small files afterwards).
        // Cheap no-ops on the tiny dev/qual data; essential on the large prod MERGEs.
        "spark.databricks.delta.optimizeWrite.enabled" -> "true",
        "spark.databricks.delta.autoCompact.enabled" -> "true",
        // Disk cache: perf-only (no correctness impact). On in every stage to avoid env drift.
        "spark.databricks.io.cache.enabled" -> "true",
        // Heartbeat head-room. Executors were declared dead after 125-176s Full-GC pauses (the default
        // spark.network.timeout=120s drives the driver's heartbeat-receiver timeout). Raise to 300s so a long GC pause
        // no longer kills an otherwise-healthy executor. Band-aid that complements the smaller E48 nodes above.
        "spark.network.timeout" -> "300s",
        // MERGE source materialization resilience. Databricks ALWAYS materializes the MERGE source (the value "none" of
        // spark.databricks.delta.merge.materializeSource only exists in OSS Delta; DBR accepts only "auto"/"all"), storing
        // it as a localCheckpoint RDD. The default storage level DISK_ONLY keeps ONE copy on one executor, so when that
        // executor dies the block is unrecoverable → CHECKPOINT_RDD_BLOCK_ID_NOT_FOUND → full recompute cascade (the main
        // reason one executor loss dragged whole tables to hours). DISK_ONLY_2 keeps a second replica on another node, so
        // a single executor loss is survived without recompute. Costs ~1× extra disk + replication traffic on the
        // materialized source; cheap vs. an hours-long recompute. Benchmark lever.
        "spark.databricks.delta.merge.materializeSource.rddStorageLevel" -> "DISK_ONLY_2"
      ) ++ dqp(
        // initialPartitionNum = numWorkers × coresPerWorker × 4 (= total cores × 4); the prod value is unchanged at 768
        // because 4×E48 = 2×E96 = 192 cores. Also pin spark.sql.shuffle.partitions to the same value so exchanges that
        // bypass AQE coalescing stop falling back to the magic default of 200 (the many "exactly 200-task" stages seen in
        // the Spark UI) — AQE coalesces the small ones back up toward the 16MB advisory size anyway. Benchmark lever.
        dev_qual = Map(
          "spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> (2 * 16 * 4).toString,
          "spark.sql.shuffle.partitions" -> (2 * 16 * 4).toString
        ),
        prod = Map(
          "spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> (4 * 48 * 4).toString,
          "spark.sql.shuffle.partitions" -> (4 * 48 * 4).toString
        )
      ),
      // In-JVM concurrent tables. Halved prod 48 → 24 so fewer heavy MERGEs share a node's heap at once (the GC-pressure
      // driver): with 4 nodes now, 24 keeps ~6 heavy tables per node instead of ~24/node before. Benchmark lever.
      taskParallelism = dqp(dev_qual = 8 * 2, prod = 24),
      runtimeEngine = "PHOTON",
      schedule = JobSchedule(quartzCronExpression = "0 0 2 * * ?", timezoneId = "UTC", pauseStatus = dqp("PAUSED", "UNPAUSED"))
    )

    val dmConfig = srConfig.copy(
      // dm_md/dw_tx keep the original 2× E16 shape (they don't run the heavy sr MERGEs); pin the worker count back to 2
      // because srConfig now defaults prod to 4 workers.
      minWorkerNodes = 2,
      maxWorkerNodes = 2,
      taskParallelism = dqp(dev_qual = 4 * 2, prod = 8 * 2),
      nodeTypeId = dqp(dev_qual = "Standard_E8ds_v5", prod = "Standard_E16ds_v5"),
      driverNodeTypeId = dqp(dev_qual = "Standard_E4ds_v5", prod = "Standard_E8ds_v5"),
      sparkConf = srConfig.sparkConf ++ dqp(
        dev_qual = Map(
          "spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> (1 * 8 * 4).toString,
          "spark.sql.shuffle.partitions" -> (1 * 8 * 4).toString
        ),
        prod = Map(
          "spark.sql.adaptive.coalescePartitions.initialPartitionNum" -> (1 * 32 * 4).toString,
          "spark.sql.shuffle.partitions" -> (1 * 32 * 4).toString
        )
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
        // Watchdog disabled for now: let a full run complete uninterrupted to establish the real per-table P95 runtimes
        // (the 1800s cap was firing on the large sr MERGEs before they finished). Re-enable with a data-driven cap
        // (doc rule of thumb: sr ≈ 4h, dm_md/dw_tx ≈ 1h, ~3× observed P95) once a clean baseline exists.
        maxTableRuntimeSeconds = None,
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
