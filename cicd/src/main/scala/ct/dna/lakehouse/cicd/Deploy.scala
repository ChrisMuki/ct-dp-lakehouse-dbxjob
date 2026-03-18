package ct.dna.lakehouse.cicd

import ct.dna.lakehouse.cicd.models.DeploymentConfig
import ct.dna.lakehouse.cicd.utils.AssetDirectory
import ct.dna.utils.LocalDir
import ct.dna.utils.LoggingTrait
import ct.dna.utils.deploy.Process
import ct.dna.utils.json
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

    val stage     = config.getProperty("stage")
    val rootPath  = config.getProperty("rootPath")
    val assetPath = config.getProperty("assetPath")
    val jarPath   = config.getProperty("jarPath")
    val buildId   = config.getProperty("buildId")

    val deploymentConfig =
      json.mapper.readValue[DeploymentConfig](config.getProperty("deploymentConfig"))

    logger.info(s"Starting deployment: stage=$stage buildId=$buildId")

    val assetDir   = AssetDirectory(stage, assetPath, buildId, deploymentConfig)
    val rootDir    = LocalDir(rootPath)
    val gitProcess = Process(rootDir, Seq())

    val jobResourcesPathAsBDFS = "dbfs:" + assetDir.jobResourcesPath

    val dbxEnv: Seq[(String, String)] = deploymentConfig.deploymentIdentity.authType match {
      case "azure-client-secret" =>
        Seq(
          "DATABRICKS_HOST"      -> deploymentConfig.host,
          "DATABRICKS_AUTH_TYPE" -> "azure-client-secret",
          "ARM_TENANT_ID"        -> deploymentConfig.deploymentIdentity.tenantId.getOrElse(sys.error("tenantId required for azure-client-secret auth")),
          "ARM_CLIENT_ID"        -> deploymentConfig.deploymentIdentity.clientId.getOrElse(sys.error("clientId required for azure-client-secret auth")),
          "ARM_CLIENT_SECRET"    -> deploymentConfig.deploymentIdentity.clientSecret.getOrElse(sys.error("clientSecret required for azure-client-secret auth"))
        )
      case "oauth-m2m" =>
        Seq(
          "DATABRICKS_HOST"          -> deploymentConfig.host,
          "DATABRICKS_AUTH_TYPE"     -> "oauth-m2m",
          "DATABRICKS_CLIENT_ID"     -> deploymentConfig.deploymentIdentity.clientId.getOrElse(sys.error("clientId required for oauth-m2m auth")),
          "DATABRICKS_CLIENT_SECRET" -> deploymentConfig.deploymentIdentity.clientSecret.getOrElse(sys.error("clientSecret required for oauth-m2m auth"))
        )
      case "pat" =>
        Seq(
          "DATABRICKS_HOST"  -> deploymentConfig.host,
          "DATABRICKS_TOKEN" -> deploymentConfig.deploymentIdentity.token.getOrElse(sys.error("token required for pat auth"))
        )
      case other =>
        sys.error(s"Unknown authType '$other'. Supported: azure-client-secret, oauth-m2m, pat")
    }

    val dbxProcess = Process(
      assetDir.assetDir,
      dbxEnv
    )

    // Prepare asset directory with all files
    assetDir.assetDir.makeEmptyDir()
    assetDir.copyLog4j2File()

    val resourceIncludes: List[String] = deploymentConfig.resourceFilesPath match {
      case Some(relPath) =>
        val absPath = s"$rootPath/$relPath".replaceAll("/+", "/")
        logger.info(s"Including resource files from: $absPath")
        assetDir.copyResourceFiles(absPath)
      case None =>
        Nil
    }

    assetDir.createDatabricksYml(resourceIncludes)

    logger.info("Validating Databricks bundle")
    if (dbxProcess.run("databricks bundle validate") != 0)
      logAndThrow(new RuntimeException("Databricks bundle validation failed."))

    //logger.info("Checking git status")
    //if (gitProcess.resultOf("git status --porcelain").nonEmpty)
    //  logAndThrow(new RuntimeException("There are uncommitted changes — commit or stash before deploying."))

    logger.info(s"Creating job resource path: $jobResourcesPathAsBDFS")
    // Create the volume first (idempotent: ignore non-zero if it already exists)
    val volumeCreateCmd = s"databricks volumes create ${assetDir.volumeCatalog} ${assetDir.volumeSchema} ${assetDir.volumeName} MANAGED"
    val volumeCreateResult = dbxProcess.run(volumeCreateCmd)
    if (volumeCreateResult != 0)
      logger.info("Volume already exists or creation skipped — continuing")

    if (dbxProcess.run(s"databricks fs mkdir $jobResourcesPathAsBDFS") != 0)
      logAndThrow(new RuntimeException("Creating job resource path failed."))

    logger.info("Copying files to Databricks volume")
    val filesToCopy = Seq(
      assetDir.assetDir.getAbsolutePath + "/log4j2.xml" -> s"$jobResourcesPathAsBDFS/log4j2.xml",
      jarPath                                            -> s"$jobResourcesPathAsBDFS/lakehouse.jar"
    )
    filesToCopy.foreach { case (from, to) =>
      logger.info(s"Uploading $from -> $to")
      if (dbxProcess.run(s"databricks fs cp $from $to --overwrite") != 0)
        logAndThrow(new RuntimeException(s"Uploading '$from' to '$to' failed."))
    }

    // Also publish the JAR to a stable 'latest' path so that external resource
    // files (e.g. lakehouse_job.yml) can reference a fixed, buildId-independent path.
    val jobResourcesLatestAsBDFS = "dbfs:" + assetDir.jobResourcesLatestPath
    dbxProcess.run(s"databricks fs mkdir $jobResourcesLatestAsBDFS")
    logger.info(s"Updating latest JAR: $jarPath -> $jobResourcesLatestAsBDFS/lakehouse.jar")
    if (dbxProcess.run(s"databricks fs cp $jarPath $jobResourcesLatestAsBDFS/lakehouse.jar --overwrite") != 0)
      logAndThrow(new RuntimeException("Uploading JAR to latest/ path failed."))

    logger.info("Deploying Databricks bundle")
    if (dbxProcess.run("databricks bundle deploy") != 0)
      logAndThrow(new RuntimeException("Databricks bundle deployment failed."))

    //logger.info("Triggering Databricks job (no-wait)")
    //if (dbxProcess.run("databricks bundle run LakehouseJob --no-wait") != 0)
    //  logger.warn("Databricks bundle run failed — trigger the job manually if needed")

    logger.info("Cleaning up local staging directory")
    assetDir.deleteIfExists()

    // logger.info("Creating and pushing git tag")
    // gitProcess.run(s"""git tag -a "${stage}_$buildId" -m "Deployed to $stage (buildId=$buildId)"""")
    // gitProcess.run(s"""git push origin "${stage}_$buildId"""")

    logger.info(s"Deployment complete: stage=$stage buildId=$buildId")
  }
}


