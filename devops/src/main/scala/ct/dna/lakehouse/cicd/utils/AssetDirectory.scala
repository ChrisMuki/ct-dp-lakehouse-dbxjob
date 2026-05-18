package ct.dna.lakehouse.cicd.utils

import java.nio.file.Files
import java.nio.file.Paths

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.node.ObjectNode
import ct.dna.lakehouse.cicd.models.ConfigFile
import ct.dna.lakehouse.cicd.models.DeploymentConfig
import ct.dna.lakehouse.cicd.models.InitScript
import ct.dna.lakehouse.core.CatalogJobConfig
import ct.dna.lakehouse.core.CatalogWorkflowBuilder
import ct.dna.lakehouse.core.jobs.orchestrator.TaskNames
import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.lakehouse.core.runtime.SparkConfig
import ct.dna.lakehouse.dm_md.{`package` => dmCatalog}
import ct.dna.lakehouse.dw_tx.{`package` => dwCatalog}
import ct.dna.lakehouse.sr.{`package` => srCatalog}
import ct.dna.utils.LocalDir
import ct.dna.utils.ResourceLoader
import ct.dna.utils.deploy.databrickscli.assetbundle._
import ct.dna.utils.json.{mapper => jsonMapper}
import ct.dna.utils.logging.LoggingTrait

case class AssetDirectory(
    stage: String,
    assetPath: String,
    buildId: String,
    deploymentConfig: DeploymentConfig
) extends LoggingTrait {

  private val resourceLoader = ResourceLoader.withContextClassLoader

  val assetDir: LocalDir = LocalDir(assetPath)

  /** Unity Catalog volume coordinates — used to create the volume and construct paths. volumeCatalog defaults to "lakehouse" for prod, "<stage>_lakehouse" for
    * other stages when not set in config.
    */
  val volumeCatalog: String = deploymentConfig.volumeCatalog.getOrElse {
    if (stage == "prod") "lakehouse" else s"${stage}_lakehouse"
  }

  /** Schema name in the Unity Catalog volume path.
    */
  val volumeSchema: String = deploymentConfig.volumeSchema
  val volumeName: String = "dbxlakehousejob"

  /** Volume path where all job resources (JAR, config files) will be stored. */
  val jobResourcesPath: String =
    s"/Volumes/${volumeCatalog}/${volumeSchema}/${volumeName}/${buildId}"

  /** Stable path that always points to the most recently deployed JAR. External resource files (e.g. lakehouse_job.yml) reference this path so they don't need
    * to know the buildId.
    */
  val jobResourcesLatestPath: String =
    s"/Volumes/${volumeCatalog}/${volumeSchema}/${volumeName}/latest"

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
    val is = InitScript(sourceLog4j2PathInVolume = s"$jobResourcesLatestPath/log4j2.xml")
    is.writeToFolder(assetDir)
    is
  }

  def createConfigJson(): ConfigFile = {
    logger.info("Building 'config.json'")
    val cf = ConfigFile(
      rootDir = "/tmp/lakehouse",
      sparkConfig = SparkConfig.Lakehouse(stage)
    )
    cf.writeToFolder(assetDir)
    cf
  }

  def createDatabricksYml(): AssetBundle = {
    logger.info("Building 'databricks.yml'")

    val cc = deploymentConfig.clusterConfiguration
    val ccOverrides = deploymentConfig.clusterConfigurations
    val jarPath = s"${jobResourcesLatestPath}/lakehouse.jar"
    val configFilePath = s"${jobResourcesLatestPath}/config.json"

    /** Build a `JobCluster` for a given catalog. One cluster is created per catalog job so each catalog can be tuned independently via
      * `deploymentConfig.clusterConfigurations[<catalogName>]`. Per-catalog overrides win; absent fields inherit the global `clusterConfiguration`. `sparkConf`
      * is shallow-merged (per-catalog entries override the global on key collisions).
      */
    def buildJobCluster(catalogName: String): JobCluster = {
      val ov = ccOverrides.getOrElse(catalogName, DeploymentConfig.ClusterConfigurationOverride())
      val effectiveSparkConf = cc.sparkConf ++ ov.sparkConf
      val effectiveNodeType = ov.nodeTypeId.getOrElse(cc.nodeTypeId)
      val effectiveDriverNodeType = ov.driverNodeTypeId.getOrElse(cc.driverNodeTypeId)
      val effectiveSparkVersion = ov.sparkVersion.getOrElse(cc.sparkVersion)
      // Jackson stores `Option[Int]` as `Option[java.lang.Long]` due to type erasure;
      // any direct unbox-to-Int (including `.map(identity)`) throws ClassCastException.
      // Erase to `Option[Any]` first, then coerce the boxed `Number` explicitly.
      val effectiveMaxWorkers: Int =
        ov.maxWorkerNodes.asInstanceOf[Option[Any]] match {
          case Some(n) => n.asInstanceOf[Number].intValue
          case None    => cc.maxWorkerNodes
        }
      val effectivePolicyId = ov.clusterPolicyId.orElse(cc.clusterPolicyId)
      JobCluster(
        jobClusterKey = s"${catalogName}-cluster",
        newCluster = NewCluster(
          sparkVersion = effectiveSparkVersion,
          azureAttributes = AzureAttributes(
            availability = "SPOT_WITH_FALLBACK_AZURE",
            spotBidMaxPrice = 100
          ),
          nodeTypeId = effectiveNodeType,
          driverNodeTypeId = effectiveDriverNodeType,
          clusterLogConf = ClusterLogConf(
            volumes = ClusterLogConfDestination(destination = s"${jobResourcesLatestPath}/logs")
          ),
          sparkConf = effectiveSparkConf,
          sparkEnvVars = null,
          initScripts = List(
            JobInitScript(
              volumes = JobInitScriptVolumes(destination = s"$jobResourcesLatestPath/init_script.sh")
            )
          ),
          policyId = effectivePolicyId.orNull,
          workloadType = WorkloadType(clients = WorkloadTypeClients(notebooks = false, jobs = true)),
          dataSecurityMode = "SINGLE_USER",
          runtimeEngine = "PHOTON",
          kind = "CLASSIC_PREVIEW",
          isSingleNode = false,
          autoscale = Autoscale(minWorkers = 1, maxWorkers = effectiveMaxWorkers)
        )
      )
    }

    val bundle = Bundle(name = "dp-lakehouse-dbxjob")
    val targetPermissions = deploymentConfig.permissions.map(p => Permission(groupName = Some(p.groupName), level = p.level))
    val targets = Map(
      stage -> Target(
        mode = deploymentConfig.targetMode,
        default = true,
        workspace = Workspace(rootPath = "~/.bundle/${bundle.name}/${bundle.target}"),
        permissions = targetPermissions
      )
    )

    val jobSchedules: Map[String, JobSchedule] = deploymentConfig.schedules.map { case (catalogName, s) =>
      catalogName -> JobSchedule(
        quartzCronExpression = s.quartzCronExpression,
        timezoneId = s.timezoneId,
        pauseStatus = s.pauseStatus
      )
    }

    val jobContinuous: Map[String, Continuous] = deploymentConfig.continuous.map { case (catalogName, c) =>
      catalogName -> Continuous(
        pauseStatus = c.pauseStatus,
        taskRetryMode = c.taskRetryMode
      )
    }
    // Databricks rejects both `schedule` and `continuous` on the same job. Surface the conflict at deploy time
    // rather than letting the bundle apply fail with a less informative message.
    val conflictingCatalogs = jobSchedules.keySet.intersect(jobContinuous.keySet)
    require(
      conflictingCatalogs.isEmpty,
      s"Catalogs configured with both `schedules` and `continuous`: ${conflictingCatalogs.mkString(", ")}. Pick one."
    )

    /** Catalogs that should be deployed as one Databricks Job each. Each entry gets its own cluster and an independent (optional) schedule looked up by catalog
      * name in `deploymentConfig.schedules`.
      */
    val catalogs: List[CatalogSpec] = List(srCatalog, dwCatalog, dmCatalog)

    // Monitoring runtime config is shared across every catalog job; serialised
    // once and passed verbatim as the `monitoringConfig=<json>` Spark argument.
    // Field set is a superset-compatible match for `MonitoringConfig` in the
    // `lakehouse` module. Default the summary/table-runs coordinates to the deployment's
    // volume catalog/schema.
    val monitoringWithDefaults = deploymentConfig.monitoring.copy(
      summaryCatalog = deploymentConfig.monitoring.summaryCatalog.orElse(Some(volumeCatalog)),
      summarySchema = deploymentConfig.monitoring.summarySchema.orElse(Some(volumeSchema))
    )
    val monitoringJson: String = jsonMapper.writeValueAsString(monitoringWithDefaults)

    // Jackson deserialises numeric JSON values inside `Map[String, Int]` as
    // boxed `java.lang.Long`. Reading the value through a `Map[String, Int]`
    // view triggers Scala's unbox-to-Int which then fails. Access the raw
    // boxed map via `Any` and coerce explicitly through `Number.intValue`.
    val rawTaskParallelism: Map[String, Any] =
      deploymentConfig.taskParallelism.asInstanceOf[Map[String, Any]]
    val defaultTaskParallelism: Int =
      deploymentConfig.taskParallelismDefault.asInstanceOf[Any].asInstanceOf[Number].intValue

    val jobs: Map[String, Job] = catalogs.map { catalog =>
      val catalogName = catalog.id.name
      val workerCount: Int = rawTaskParallelism.get(catalogName) match {
        case Some(n) => n.asInstanceOf[Number].intValue
        case None    => defaultTaskParallelism
      }
      val cfg = CatalogJobConfig(
        jobCluster = buildJobCluster(catalogName),
        schedule = jobSchedules.get(catalogName),
        continuous = jobContinuous.get(catalogName),
        workerCount = workerCount,
        monitoringJson = monitoringJson
      )
      CatalogWorkflowBuilder.buildJob(catalog, cfg, jarPath, configFilePath)
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
          if (task.isObject && task.path("task_key").asText() == TaskNames.SummaryTaskKey) {
            task.asInstanceOf[ObjectNode].put("run_if", "ALL_DONE")
          }
        }
      }
    }
  }

  def deleteIfExists(): Unit = assetDir.deleteIfExists()
}
