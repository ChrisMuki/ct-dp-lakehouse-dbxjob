package ct.dna.lakehouse.deploy

import ct.dna.lakehouse.deploy.Config
import ct.dna.lakehouse.deploy.Stage
import ct.dna.lakehouse.deploy.utils.AssetDirectory
import ct.dna.utils.az.auth._
import ct.dna.utils.deploy.Process
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
      .withAzAuth
      .optional("validateOnly", "false")
      .build(args)

    val stage = config.getProperty("stage")
    val assetPath = config.getProperty("assetPath")
    val jarPath = config.getProperty("jarPath")
    val buildId = config.getProperty("buildId")
    val validateOnly = config.getProperty("validateOnly").trim.toBoolean
    val deploymentAzAuth = config.getAzAuth

    Stage.set(stage)
    val deploymentConfig = Config.stageConfig

    val mode = if (validateOnly) "VALIDATE-ONLY" else "DEPLOY"
    logger.info(s"Starting $mode: stage=$stage buildId=$buildId")

    val assetDir = AssetDirectory(stage, assetPath, buildId, deploymentConfig)

    val jobResourcesPathAsBDFS = "dbfs:" + assetDir.jobResourcesPath

    val dbxProcess = Process(
      assetDir.assetDir,
      Seq(
        "DATABRICKS_HOST" -> deploymentConfig.host,
        "DATABRICKS_TOKEN" -> deploymentAzAuth.getDbxToken(30)
      )
    )

    // Prepare asset directory with all files
    assetDir.assetDir.makeEmptyDir()
    assetDir.copyLog4j2File()
    assetDir.createInitScriptSh()
    val configFiles = assetDir.createConfigFiles()

    val assetBundle = assetDir.createDatabricksYml()

    logger.info("Validating Databricks bundle")
    dbxProcess.execute("databricks bundle validate").throwOnFailure("Databricks bundle validation failed.")

    if (validateOnly) {
      logger.info("validateOnly=true → skipping volume upload, bundle deploy, job run and cleanup. Validation complete.")
    } else {
      logger.info(s"Creating job resource path: $jobResourcesPathAsBDFS")
      // Create the volume first (idempotent: ignore non-zero if it already exists)
      val volumeCreateCmd = s"databricks volumes create ${assetDir.volumeCatalog} ${assetDir.volumeSchema} ${assetDir.volumeName} MANAGED"
      dbxProcess.execute(volumeCreateCmd)
      // Ignore result — volume may already exist

      dbxProcess.execute(s"databricks fs mkdir $jobResourcesPathAsBDFS").throwOnFailure("Creating job resource path failed.")

      logger.info("Copying files to Databricks volume")
      val configFileCopies = configFiles.map { cf =>
        (assetDir.assetDir.getAbsolutePath + "/" + cf.fileName) -> s"$jobResourcesPathAsBDFS/${cf.fileName}"
      }
      val filesToCopy = Seq(
        assetDir.assetDir.getAbsolutePath + "/log4j2.xml" -> s"$jobResourcesPathAsBDFS/log4j2.xml",
        assetDir.assetDir.getAbsolutePath + "/init_script.sh" -> s"$jobResourcesPathAsBDFS/init_script.sh",
        jarPath -> s"$jobResourcesPathAsBDFS/lakehouse.jar"
      ) ++ configFileCopies
      filesToCopy.foreach { case (from, to) =>
        logger.info(s"Uploading $from -> $to")
        dbxProcess.execute(s"databricks fs cp $from $to --overwrite").throwOnFailure(s"Uploading '$from' to '$to' failed.")
      }

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
  }

}
