package ct.dna.lakehouse.deploy.utils

import java.nio.file.Files
import java.nio.file.Paths

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.node.ObjectNode
import ct.dna.lakehouse.core.lakehousejob.summary.{EntryPoint => SummaryEntryPoint}
import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.lakehouse.core.runtime.SparkConfig
import ct.dna.lakehouse.deploy.CatalogConfig
import ct.dna.lakehouse.deploy.CatalogJobBuilder
import ct.dna.lakehouse.deploy.Config
import ct.dna.lakehouse.deploy.Stage
import ct.dna.lakehouse.deploy.model.ConfigFile
import ct.dna.lakehouse.deploy.model.InitScript
import ct.dna.utils.LocalDir
import ct.dna.utils.ResourceLoader
import ct.dna.utils.deploy.databrickscli.assetbundle._
import ct.dna.utils.logging.LoggingTrait

case class AssetDirectory(
    stage: String,
    assetPath: String,
    buildId: String,
    deploymentConfig: Config
) extends LoggingTrait {

  private val resourceLoader = ResourceLoader.withContextClassLoader

  val assetDir: LocalDir = LocalDir(assetPath)

  val volumeCatalog = deploymentConfig.volumeCatalog
  val volumeSchema = deploymentConfig.volumeSchema
  val volumeName: String = "dbxlakehousejob"

  /** Volume path where all job resources (JAR, config files) will be stored. */
  val jobResourcesPath: String =
    s"/Volumes/${volumeCatalog}/${volumeSchema}/${volumeName}/${buildId}"

  /** Staging/volume file name of the per-catalog runtime config. */
  private def configFileName(catalog: CatalogSpec): String = s"config-${catalog.id.name}.json"

  /** Volume path of the per-catalog runtime config, passed to its `JobSetup` as `configFile=<path>`. */
  def configFileVolumePath(catalog: CatalogSpec): String = s"$jobResourcesPath/${configFileName(catalog)}"

  def copyLog4j2File(): Unit = {
    logger.info("Copying runtime-log4j2.xml to asset directory as log4j2.xml")
    val bytes = resourceLoader
      .getResourceAsStream("runtime-log4j2.xml")
      .getOrElse(sys.error("runtime-log4j2.xml not found on classpath"))
      .readAllBytes()
    Files.write(Paths.get(assetDir.getAbsolutePath, "log4j2.xml"), bytes)
  }

  /** Writes `init_script.sh` to the asset directory. The script runs on every cluster startup and swaps in the runtime log4j2.xml that lives on the Unity
    * Catalog volume, so `logger.info` from `ct.dna.*` appears in each Databricks task's Output tab.
    */
  def createInitScriptSh(): InitScript = {
    logger.info("Building 'init_script.sh'")
    val is = InitScript(sourceLog4j2PathInVolume = s"$jobResourcesPath/log4j2.xml")
    is.writeToFolder(assetDir)
    is
  }

  /** Writes one `config-<catalog>.json` per catalog to the asset directory and returns them. Each file carries everything `JobSetup` needs at runtime except
    * the Databricks-resolved `runId`: the catalog identity, the in-JVM worker count, the per-task orchestrator/summary configs and the Spark config.
    */
  def createConfigFiles(): Seq[ConfigFile] = {
    val sparkConfig = SparkConfig.Lakehouse(stage)
    deploymentConfig.catalogConfig.map { case (catalog, cc) =>
      val cf = ConfigFile(
        fileName = configFileName(catalog),
        catalogClass = catalog.getClass.getPackage.getName,
        workerCount = cc.taskParallelism,
        orchestratorConfig = deploymentConfig.orchestrator,
        summaryConfig = deploymentConfig.summary,
        sparkConfig = sparkConfig
      )
      logger.info(s"Building '${cf.fileName}' for catalog '${catalog.id.name}'")
      cf.writeToFolder(assetDir)
      cf
    }
  }

  def createDatabricksYml(): AssetBundle = {
    logger.info("Building 'databricks.yml'")

    val jarPath = s"${jobResourcesPath}/lakehouse.jar"

    /** Build a `JobCluster` from a fully-resolved [[CatalogConfig]]. One cluster is created per catalog job; the config has already been resolved for the
      * catalog in `Config.stageConfig` (per-catalog `base.copy(...)`), so there is no merge here — every field is read straight off `cfg`.
      */
    def buildJobCluster(cfg: CatalogConfig, catalog: CatalogSpec): JobCluster = {
      // Point the FAIR scheduler at the driver-local allocation file written by the init script, via an explicit `file:`
      // scheme. Injected here — rather than hardcoded per stage in the config JSON — so it can never be a DBFS path
      // (`DbfsDisabledException`) or a UC volume path (`No Unity API token found in Unity Scope`): Spark resolves this file
      // through the Hadoop FileSystem at SparkContext init, before Unity Catalog credentials exist.
      val sparkConf = cfg.sparkConf +
        ("spark.scheduler.allocation.file" -> s"file:${InitScript.FairSchedulerLocalDest}")
      JobCluster(
        jobClusterKey = s"${catalog.id.name}-cluster",
        newCluster = NewCluster(
          sparkVersion = cfg.sparkVersion,
          // prod runs on a single big on-demand worker — no spot eviction risk for the production load; dev/qual use
          // cheaper spot instances (falling back to on-demand only if no spot capacity is available). spotBidMaxPrice
          // stays 100 even for ON_DEMAND_AZURE: the cluster policy validates the field regardless of availability (it is
          // simply ignored when no spot instances are requested).
          azureAttributes = Stage
            .dqp(
              AzureAttributes(availability = "SPOT_WITH_FALLBACK_AZURE", spotBidMaxPrice = 100),
              AzureAttributes(availability = "SPOT_WITH_FALLBACK_AZURE", spotBidMaxPrice = 100),
              AzureAttributes(availability = "ON_DEMAND_AZURE", spotBidMaxPrice = 100)
            ),
          nodeTypeId = cfg.nodeTypeId,
          driverNodeTypeId = cfg.driverNodeTypeId,
          clusterLogConf = ClusterLogConf(
            volumes = ClusterLogConfDestination(destination = s"${jobResourcesPath}/logs")
          ),
          sparkConf = sparkConf,
          sparkEnvVars = null,
          initScripts = List(
            JobInitScript(
              volumes = JobInitScriptVolumes(destination = s"$jobResourcesPath/init_script.sh")
            )
          ),
          policyId = cfg.clusterPolicyId,
          workloadType = WorkloadType(clients = WorkloadTypeClients(notebooks = false, jobs = true)),
          dataSecurityMode = "SINGLE_USER",
          // Photon enabled: although the lakehouse jobs do heavy shuffle/IO (Delta merges, large SAP rebuilds),
          // the per-task plans are also CPU-bound on vectorisable operators — SortMergeJoin, Sort, SortAggregate
          // and the post-join Generate/explode chain on the MERGE write side — which Photon accelerates. Worth
          // the ~2x DBU rate when wall-clock drops more than that. Switch back to "STANDARD" if benchmarks regress.
          runtimeEngine = "PHOTON",
          kind = "CLASSIC_PREVIEW",
          isSingleNode = false,
          autoscale = Autoscale(minWorkers = 1, maxWorkers = cfg.maxWorkerNodes)
        )
      )
    }

    val bundle = Bundle(name = "dp-lakehouse-dbxjob")
    val targetPermissions = deploymentConfig.permissions
    val targets = Map(
      stage -> Target(
        mode = deploymentConfig.targetMode,
        default = true,
        workspace = Workspace(rootPath = "~/.bundle/${bundle.name}/${bundle.target}"),
        permissions = targetPermissions
      )
    )

    // Every value `JobSetup` needs apart from the runtime `runId` now lives in the catalog's `config-<catalog>.json`
    // on the volume, so the job only points each task at its own config file via `configFile=<path>`.
    val jobs: Map[String, Job] = deploymentConfig.catalogConfig.map { case (catalog, cc) =>
      CatalogJobBuilder.buildJob(
        catalogSpec = catalog,
        catalogConfig = cc,
        jobCluster = buildJobCluster(cc, catalog),
        jarPath = jarPath,
        configFilePath = configFileVolumePath(catalog)
      )
    }.toMap

    val assetBundle = AssetBundle(
      bundle,
      Resources(jobs = jobs),
      targets
    )

    val yamlMapper = ct.dna.utils.deploy.yaml.mapper
      .cloneRaw()
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

    val tree = yamlMapper.valueToTree[ObjectNode](assetBundle)
    // The upstream `Task` case class has no `runIf` field. The terminal Summary task is `ALL_DONE` so it fires even
    // when a worker or the monitor fails — inject that field directly into the serialized tree.
    injectSummaryRunIf(tree)

    Files.write(Paths.get(assetDir.getAbsolutePath, "databricks.yml"), yamlMapper.writeValueAsBytes(tree))
    assetBundle
  }

  /** Sets `run_if: ALL_DONE` on the terminal Summary (Earth) task of every job in the bundle. Without this Databricks would skip Summary when any upstream task
    * fails — exactly the case where we most want it to run (failure reporting / future alerting / summary-table append). The shared `Task` case class doesn't
    * expose `run_if`, so it has to be injected post-serialisation just like `spark_conf`.
    */
  private def injectSummaryRunIf(tree: ObjectNode): Unit = {
    val jobs = tree.path("resources").path("jobs")
    if (!jobs.isObject) return
    val jobsIt = jobs.fields()
    while (jobsIt.hasNext) {
      val tasks = jobsIt.next().getValue.path("tasks")
      if (tasks.isArray) {
        val tIt = tasks.elements()
        while (tIt.hasNext) {
          val task = tIt.next()
          if (task.isObject && task.path("task_key").asText() == SummaryEntryPoint.Summary) {
            task.asInstanceOf[ObjectNode].put("run_if", "ALL_DONE")
          }
        }
      }
    }
  }

  def deleteIfExists(): Unit = assetDir.deleteIfExists()
}
