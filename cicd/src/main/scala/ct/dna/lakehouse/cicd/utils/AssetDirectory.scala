package ct.dna.lakehouse.cicd.utils

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.node.ObjectNode
import ct.dna.lakehouse.cicd.models.DeploymentConfig
import ct.dna.utils.LocalDir
import ct.dna.utils.LoggingTrait
import ct.dna.utils.ResourceLoader
import ct.dna.utils.deploy.databrickscli.assetbundle._

case class AssetDirectory(
    stage: String,
    assetPath: String,
    buildId: String,
    deploymentConfig: DeploymentConfig
) extends LoggingTrait {

  private val resourceLoader = ResourceLoader.withContextClassLoader

  val assetDir: LocalDir = LocalDir(assetPath)

  /** Unity Catalog volume coordinates — used to create the volume and construct paths. volumeCatalog defaults to "ctdp<stage>dbxlakehouse" when not set in
    * config.
    */
  val volumeCatalog: String = deploymentConfig.volumeCatalog.getOrElse(s"ctdp${stage}dbxlakehouse")

  /** Schema name in the Unity Catalog volume path. Uses the deploying identity's clientId (SP UUID for azure-client-secret/oauth-m2m). Defaults to "default"
    * when clientId is absent (e.g. PAT auth).
    */
  val volumeSchema: String = deploymentConfig.deploymentIdentity.clientId.getOrElse("default")
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
    logger.info("Copying log4j2.xml to asset directory")
    val bytes = resourceLoader
      .getResourceAsStream("log4j2.xml")
      .getOrElse(sys.error("log4j2.xml not found on classpath"))
      .readAllBytes()
    Files.write(Paths.get(assetDir.getAbsolutePath, "log4j2.xml"), bytes)
  }

  /** Copies resource YAML files into <assetDir>/resources/ and returns their bundle-relative paths (e.g. "resources/lakehouse_job.yml") for use in the
    * `include:` directive of databricks.yml.
    *
    * @param sourcePath
    *   absolute path to a single YAML file or a directory containing *.yml / *.yaml files
    */
  def copyResourceFiles(sourcePath: String): List[String] = {
    val source = new java.io.File(sourcePath)
    if (!source.exists()) {
      logger.warn(s"resourceFilesPath '$sourcePath' does not exist — skipping")
      return Nil
    }
    val yamlFiles: List[java.io.File] =
      if (source.isDirectory)
        source.listFiles().toList.filter(f => f.getName.endsWith(".yml") || f.getName.endsWith(".yaml"))
      else
        List(source)
    if (yamlFiles.isEmpty) {
      logger.warn(s"No YAML files found at '$sourcePath' — skipping")
      return Nil
    }
    val resourcesDir = Paths.get(assetDir.getAbsolutePath, "resources")
    Files.createDirectories(resourcesDir)
    yamlFiles.map { file =>
      val target = resourcesDir.resolve(file.getName)
      logger.info(s"Copying resource file ${file.getName} to asset directory")
      Files.copy(file.toPath, target, StandardCopyOption.REPLACE_EXISTING)
      s"resources/${file.getName}"
    }
  }

  def createDatabricksYml(includeResourceFiles: List[String] = Nil): AssetBundle = {
    logger.info("Building 'databricks.yml'")

    val bundle = Bundle(name = "dp-lakehouse-dbxjob")
    val targets = Map(
      stage -> Target(
        mode = deploymentConfig.targetMode,
        default = true,
        workspace = Workspace(root_path = "~/.bundle/${bundle.name}/${bundle.target}")
      )
    )

    // val jobCluster = JobCluster(
    //   job_cluster_key = "lakehouse-cluster",
    //   new_cluster = NewCluster(
    //     spark_version = deploymentConfig.clusterConfiguration.sparkVersion,
    //     azure_attributes = AzureAttributes(
    //       availability = "SPOT_WITH_FALLBACK_AZURE",
    //       spot_bid_max_price = 100
    //     ),
    //     node_type_id = deploymentConfig.clusterConfiguration.nodeTypeId,
    //     driver_node_type_id = deploymentConfig.clusterConfiguration.driverNodeTypeId,
    //     cluster_log_conf = ClusterLogConf(
    //       volumes = ClusterLogConfDestination(destination = s"${jobResourcesPath}/logs")
    //     ),
    //     spark_env_vars = Map(
    //       "Stage" -> stage
    //     ),
    //     init_scripts = List(),
    //     policy_id = deploymentConfig.clusterConfiguration.clusterPolicyId.getOrElse(""),
    //     workload_type = WorkloadType(clients = WorkloadTypeClients(notebooks = false, jobs = true)),
    //     data_security_mode = "SINGLE_USER",
    //     runtime_engine = "STANDARD",
    //     kind = "CLASSIC_PREVIEW",
    //     is_single_node = false,
    //     autoscale = Autoscale(min_workers = 1, max_workers = deploymentConfig.clusterConfiguration.maxWorkerNodes)
    //   )
    // )

    // val lakehouseSyncTask = Task(
    //   task_key = "LakehouseSync",
    //   spark_jar_task = Some(
    //     SparkJarTask(
    //       main_class_name = "ct.dna.lakehouse.Main",
    //       parameters = List(s"stage=$stage")
    //     )
    //   ),
    //   job_cluster_key = jobCluster.job_cluster_key,
    //   libraries = List(Library(jar = s"${jobResourcesPath}/lakehouse.jar")),
    //   max_retries = Some(0),
    //   min_retry_interval_millis = Some(0)
    // )

    val assetBundle = AssetBundle(
      bundle,
      Resources(
        jobs = Map(
          // "LakehouseJob" -> Job(
          //   name = "LakehouseJob",
          //   schedule = deploymentConfig.schedule.map(s =>
          //     JobSchedule(
          //       quartz_cron_expression = s.quartzCronExpression,
          //       timezone_id = s.timezoneId,
          //       pause_status = s.pauseStatus
          //     )
          //   ),
          //   max_concurrent_runs = 1,
          //   tasks = List(lakehouseSyncTask),
          //   job_clusters = List(jobCluster),
          //   queue = Queue(enabled = true),
          //   run_as = RunAs(service_principal_name = deploymentConfig.jobRunIdentity.map(_.clientId).getOrElse("")),
          //   tags = Map("Stage" -> stage, "Project" -> "dp-lakehouse-dbxjob")
          // )
        )
      ),
      targets
    )

    // Serialise to a YAML tree so we can post-process before writing.
    // NON_ABSENT omits both null and Option.None fields (e.g. schedule).
    val yamlMapper = ct.dna.utils.deploy.yaml.mapper
      .cloneRaw()
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
    val tree = yamlMapper.valueToTree[ObjectNode](assetBundle)

    // Remove policy_id from the cluster node when no policy is configured
    // (the library requires a String, so we passed "" as placeholder).
    if (deploymentConfig.clusterConfiguration.clusterPolicyId.isEmpty) {
      Option(
        tree
          .path("resources")
          .path("jobs")
          .path("LakehouseJob")
          .path("job_clusters")
          .get(0)
          .path("new_cluster")
      )
        .collect { case obj: ObjectNode => obj }
        .foreach(_.remove("policy_id"))
    }

    // Emit top-level bundle variables.
    // - jar_latest_path : stable JAR location for external resource files to reference
    // - spark_version   : Spark/DBR version for all job clusters
    // - instance_pool_id: shared pool for lakeflow_job (and optionally other jobs)
    val varsNode = tree.putObject("variables")

    val jarVar = varsNode.putObject("jar_latest_path")
    jarVar.put("description", "Stable path to the most recently deployed lakehouse JAR")
    jarVar.put("default", s"${jobResourcesLatestPath}/lakehouse.jar")

    val sparkVar = varsNode.putObject("spark_version")
    sparkVar.put("description", "Databricks Runtime version used by all job clusters")
    sparkVar.put("default", deploymentConfig.clusterConfiguration.sparkVersion)

    deploymentConfig.clusterConfiguration.instancePoolId.foreach { poolId =>
      val poolVar = varsNode.putObject("instance_pool_id")
      poolVar.put("description", "Azure instance pool shared by all lakehouse jobs")
      poolVar.put("default", poolId)
    }

    // Inject top-level include: for any extra resource YAML files.
    if (includeResourceFiles.nonEmpty) {
      val includeArray = tree.putArray("include")
      includeResourceFiles.foreach(includeArray.add)
    }

    Files.write(Paths.get(assetDir.getAbsolutePath, "databricks.yml"), yamlMapper.writeValueAsBytes(tree))
    assetBundle
  }

  def deleteIfExists(): Unit = assetDir.deleteIfExists()
}
