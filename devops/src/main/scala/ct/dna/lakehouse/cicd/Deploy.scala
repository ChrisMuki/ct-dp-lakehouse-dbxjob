package ct.dna.lakehouse.cicd

import java.io.File
import java.io.InputStream
import java.nio.file.Paths

import ct.dna.lakehouse.cicd.models.DeploymentConfig
import ct.dna.lakehouse.cicd.utils.AssetDirectory
import ct.dna.utils.ResourceLoader
import ct.dna.utils.deploy.Process
import ct.dna.utils.json
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.runtime.Configuration

object Deploy extends LoggingTrait {

  def main(args: Array[String]): Unit = {
    Thread.currentThread().setName("Deploy")

    val config = Configuration
      .required("stage")
      .required("rootPath")
      .required("assetPath")
      .required("jarPath")
      .required("buildId")
      .required("deploymentConfig")
      .build(args)

    val stage = config.getProperty("stage")
    val rootPath = config.getProperty("rootPath")
    val assetPath = config.getProperty("assetPath")
    val jarPath = config.getProperty("jarPath")
    val buildId = config.getProperty("buildId")
    val configFile = config.getProperty("configFile")

    val deploymentConfig = loadDeploymentConfig(configFile)

    logger.info(s"Starting deployment: stage=$stage buildId=$buildId")

    val assetDir = AssetDirectory(stage, assetPath, buildId, deploymentConfig)

    val jobResourcesPathAsBDFS = "dbfs:" + assetDir.jobResourcesPath

    val dbxProcess = Process(
      assetDir.assetDir,
      Seq(
        "DATABRICKS_HOST" -> deploymentConfig.host,
        "DATABRICKS_TOKEN" -> deploymentConfig.deploymentAzAuth.getDbxToken(30)
      )
    )

    // Prepare asset directory with all files
    assetDir.assetDir.makeEmptyDir()
    assetDir.copyLog4j2File()
    assetDir.createInitScriptSh()
    assetDir.createConfigJson()

    val assetBundle = assetDir.createDatabricksYml()

    logger.info("Validating Databricks bundle")
    dbxProcess.execute("databricks bundle validate").throwOnFailure("Databricks bundle validation failed.")

    logger.info(s"Creating job resource path: $jobResourcesPathAsBDFS")
    // Create the volume first (idempotent: ignore non-zero if it already exists)
    val volumeCreateCmd = s"databricks volumes create ${assetDir.volumeCatalog} ${assetDir.volumeSchema} ${assetDir.volumeName} MANAGED"
    dbxProcess.execute(volumeCreateCmd)
    // Ignore result — volume may already exist

    dbxProcess.execute(s"databricks fs mkdir $jobResourcesPathAsBDFS").throwOnFailure("Creating job resource path failed.")

    logger.info("Copying files to Databricks volume")
    val filesToCopy = Seq(
      assetDir.assetDir.getAbsolutePath + "/log4j2.xml" -> s"$jobResourcesPathAsBDFS/log4j2.xml",
      assetDir.assetDir.getAbsolutePath + "/init_script.sh" -> s"$jobResourcesPathAsBDFS/init_script.sh",
      assetDir.assetDir.getAbsolutePath + "/config.json" -> s"$jobResourcesPathAsBDFS/config.json",
      jarPath -> s"$jobResourcesPathAsBDFS/lakehouse.jar"
    )
    filesToCopy.foreach { case (from, to) =>
      logger.info(s"Uploading $from -> $to")
      dbxProcess.execute(s"databricks fs cp $from $to --overwrite").throwOnFailure(s"Uploading '$from' to '$to' failed.")
    }

    // Also publish to a stable 'latest' path so bundle variables reference a fixed, buildId-independent path.
    val jobResourcesLatestAsBDFS = "dbfs:" + assetDir.jobResourcesLatestPath
    dbxProcess.execute(s"databricks fs mkdir $jobResourcesLatestAsBDFS")
    logger.info(s"Updating latest JAR: $jarPath -> $jobResourcesLatestAsBDFS/lakehouse.jar")
    dbxProcess.execute(s"databricks fs cp $jarPath $jobResourcesLatestAsBDFS/lakehouse.jar --overwrite").throwOnFailure("Uploading JAR to latest/ path failed.")

    val configFilePath = assetDir.assetDir.getAbsolutePath + "/config.json"
    logger.info(s"Updating latest config: $configFilePath -> $jobResourcesLatestAsBDFS/config.json")
    dbxProcess
      .execute(s"databricks fs cp $configFilePath $jobResourcesLatestAsBDFS/config.json --overwrite")
      .throwOnFailure("Uploading config.json to latest/ path failed.")

    // log4j2.xml + init_script.sh must live at the stable `latest/` path because the cluster's
    // `initScripts` entry in databricks.yml references that fixed path (no buildId interpolation).
    val log4j2Path = assetDir.assetDir.getAbsolutePath + "/log4j2.xml"
    val initScriptPath = assetDir.assetDir.getAbsolutePath + "/init_script.sh"
    logger.info(s"Updating latest log4j2: $log4j2Path -> $jobResourcesLatestAsBDFS/log4j2.xml")
    dbxProcess
      .execute(s"databricks fs cp $log4j2Path $jobResourcesLatestAsBDFS/log4j2.xml --overwrite")
      .throwOnFailure("Uploading log4j2.xml to latest/ path failed.")
    logger.info(s"Updating latest init_script: $initScriptPath -> $jobResourcesLatestAsBDFS/init_script.sh")
    dbxProcess
      .execute(s"databricks fs cp $initScriptPath $jobResourcesLatestAsBDFS/init_script.sh --overwrite")
      .throwOnFailure("Uploading init_script.sh to latest/ path failed.")

    logger.info("Deploying Databricks bundle")
    dbxProcess.execute("databricks bundle deploy").throwOnFailure("Databricks bundle deployment failed.")

    logger.info("Triggering all deployed jobs")
    assetBundle.resources.jobs.keys.foreach { jobKey =>
      logger.info(s"Running job: $jobKey")
      dbxProcess.execute(s"databricks bundle run --no-wait $jobKey")
    }

    logger.info("Cleaning up local staging directory")
    assetDir.deleteIfExists()

    logger.info(s"Deployment complete: stage=$stage buildId=$buildId")
  }

  /** Load the deployment config from `configFile`. If a sibling `<basename>.local.json` exists on disk it is loaded **instead** of the committed file (full
    * replacement, no merge). Local files are gitignored so individual developers can keep a personal copy with overridden `volumeSchema`, schedules, etc.
    *
    * Resolution order for the base file (so both `deployTo.sh` relative paths and CI absolute paths work):
    *   1. Absolute / cwd-relative filesystem path. 2. Classpath resource (the committed config under `devops/src/main/resources/...` ships on the classpath).
    *
    * Local override is looked up on the filesystem only; never on the classpath.
    */
  private[cicd] def loadDeploymentConfig(configFile: String): DeploymentConfig = {
    val name = Paths.get(configFile).getFileName.toString
    val localName =
      if (name.endsWith(".json")) name.stripSuffix(".json") + ".local.json"
      else name + ".local"

    val baseFsFile = Some(new File(configFile)).filter(_.isFile)
    val localCandidate = baseFsFile.map(b => new File(b.getAbsoluteFile.getParentFile, localName))
    val activeLocal = localCandidate.filter(_.isFile)

    val (jsonBytes, source) = activeLocal match {
      case Some(local) =>
        logger.info(s"Using local override config: ${local.getAbsolutePath} (committed file ignored)")
        (java.nio.file.Files.readAllBytes(local.toPath), local.getAbsolutePath)
      case None =>
        baseFsFile match {
          case Some(b) =>
            logger.info(s"Using committed config: ${b.getAbsolutePath}")
            (java.nio.file.Files.readAllBytes(b.toPath), b.getAbsolutePath)
          case None =>
            // Fall back to classpath resource (devops jar ships configFiles/* under its resources dir).
            val loader = ResourceLoader.withContextClassLoader.withClassLoader(getClass).withFilesystem
            val stream: InputStream = loader
              .getResourceAsStream(configFile)
              .getOrElse(throw new java.io.FileNotFoundException(s"Deployment config not found on filesystem or classpath: $configFile"))
            try {
              logger.info(s"Using committed config from classpath: $configFile")
              (stream.readAllBytes(), s"classpath:$configFile")
            } finally stream.close()
        }
    }

    val root = json.mapper.readTree(jsonBytes)
    val deploymentNode = root.get("deploymentConfig")
    if (deploymentNode == null) {
      throw new IllegalArgumentException(s"Config $source does not contain a top-level 'deploymentConfig' object")
    }
    json.mapper.readValue[DeploymentConfig](json.mapper.writeValueAsString(deploymentNode))
  }
}
