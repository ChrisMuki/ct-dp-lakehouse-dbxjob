package ct.dna.lakehouse.cicd.utils

import java.nio.file.Files
import java.nio.file.Paths

import com.fasterxml.jackson.annotation.JsonInclude
import ct.dna.lakehouse.cicd.models.ConfigFile
import ct.dna.lakehouse.cicd.models.DeploymentConfig
import ct.dna.lakehouse.sr.{`package` => srCatalog}
import ct.dna.lakehouse.core.HawkWorkflowBuilder
import ct.dna.lakehouse.core.SrWorkflowBuilder
import ct.dna.lakehouse.core.runtime.SparkConfig
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
    logger.info("Copying log4j2.xml to asset directory")
    val bytes = resourceLoader
      .getResourceAsStream("log4j2.xml")
      .getOrElse(sys.error("log4j2.xml not found on classpath"))
      .readAllBytes()
    Files.write(Paths.get(assetDir.getAbsolutePath, "log4j2.xml"), bytes)
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
    val jarPath = s"${jobResourcesLatestPath}/lakehouse.jar"
    val configFilePath = s"${jobResourcesLatestPath}/config.json"

    val jobCluster = JobCluster(
      jobClusterKey = "sr-cluster",
      newCluster = NewCluster(
        sparkVersion = cc.sparkVersion,
        azureAttributes = AzureAttributes(
          availability = "SPOT_WITH_FALLBACK_AZURE",
          spotBidMaxPrice = 100
        ),
        nodeTypeId = cc.nodeTypeId,
        driverNodeTypeId = cc.driverNodeTypeId,
        clusterLogConf = ClusterLogConf(
          volumes = ClusterLogConfDestination(destination = s"${jobResourcesLatestPath}/logs")
        ),
        sparkEnvVars = null,
        initScripts = null,
        policyId = cc.clusterPolicyId.orNull,
        workloadType = WorkloadType(clients = WorkloadTypeClients(notebooks = false, jobs = true)),
        dataSecurityMode = "SINGLE_USER",
        runtimeEngine = "STANDARD",
        kind = "CLASSIC_PREVIEW",
        isSingleNode = false,
        autoscale = Autoscale(minWorkers = 1, maxWorkers = cc.maxWorkerNodes)
      )
    )

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

    val jobSchedule = deploymentConfig.schedule.map(s =>
      JobSchedule(
        quartzCronExpression = s.quartzCronExpression,
        timezoneId = s.timezoneId,
        pauseStatus = s.pauseStatus
      )
    )

    val srJobs = SrWorkflowBuilder.buildJobs(srCatalog, jobCluster, jarPath, configFilePath, jobSchedule)
    val hawkJobs = HawkWorkflowBuilder.buildJobs(jobCluster, jarPath, configFilePath)

    val assetBundle = AssetBundle(
      bundle,
      Resources(jobs = srJobs ++ hawkJobs),
      targets
    )

    val yamlMapper = ct.dna.utils.deploy.yaml.mapper
      .cloneRaw()
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

    Files.write(Paths.get(assetDir.getAbsolutePath, "databricks.yml"), yamlMapper.writeValueAsBytes(assetBundle))
    assetBundle
  }

  def deleteIfExists(): Unit = assetDir.deleteIfExists()
}
