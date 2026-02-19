package ct.dna.lakehouse.modelgen

import ct.dna.lakehouse.core.catalog.CatalogFQN
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits.ConfigurationBuilderHasSparkConfig
import ct.dna.lakehouse.core.runtime.implicits.ConfigurationHasSparkConfig
import ct.dna.utils.LoggingTrait
import ct.dna.utils.runtime.Configuration
import ct.dna.utils.runtime.Task
import ct.dna.utils.runtime.TaskEntryPoint

/** Sync task to generate TableSpec models for sr_raw catalog.
  *
  * Generates Scala source files with package `ct.dna.lakehouse.model` ready to be copied into your project's source directory. Identifiers are
  * sanitized/escaped to ensure valid Scala output.
  *
  * ==Config File Format==
  *
  * Create `sync-sr-raw.json`:
  * {{{
  * {
  *     "outputDir": "/path/to/your/project/src/main/scala",
  *     "sparkConfig": {
  *         "clazz": "RemoteDevLakehouse",
  *         "workspaceUrl": "adb-xxx.xx.azuredatabricks.net",
  *         "clusterId": "xxxx-xxxxxx-xxxxxxxx",
  *         "pat": "dapi_xxx"
  *     }
  * }
  * }}}
  *
  * ==Running==
  *
  * {{{
  * Default dev: sbt "lakehouse/Test/runMain ct.dna.lakehouse.modelgen.SyncSrRawModels"
  * Specify catalog: sbt "lakehouse/Test/runMain ct.dna.lakehouse.modelgen.SyncSrRawModels catalog dev_sr_raw"
  * Specify config + catalog: sbt "lakehouse/Test/runMain ct.dna.lakehouse.modelgen.SyncSrRawModels config [dev.json] catalog dev_sr_raw"
  * }}}
  *
  * ==Output Structure==
  *
  * Generated files will be placed at:
  * {{{
  * <outputDir>/
  *   sr_raw/
  *     package.scala                    # package object sr_raw extends CatalogSpec
  *     <schema_name>/
  *       package.scala                  # package object <schema_name> extends SchemaSpec
  *       tables.scala                   # Entity case classes + TableSpec objects
  * }}}
  *
  * All generated code uses package `ct.dna.lakehouse.model.sr_raw.*`
  */
object SyncSrRawModels extends TaskEntryPoint {

  /** The catalog to sync */
  val CatalogName = "sr_raw"
  val DefaultCatalogName = "dev_sr_raw"

  /** The production base package for generated models */
  val BasePackage = "ct.dna.lakehouse.sr_raw"

  /** Default output directory - inside dp-lakehouse-dbxjob as proper Scala source. Note: When running via sbt, working directory is the subproject root
    * (dp-lakehouse-dbxjob).
    */
  val DefaultRootDir: String = "src/main/scala/ct/dna/lakehouse/sr_raw"

  /** Resolve to absolute path to avoid issues with working directory */
  def resolveOutputDir(dir: String): String = {
    val file = new java.io.File(dir)
    if (file.isAbsolute) dir else new java.io.File(System.getProperty("user.dir"), dir).getAbsolutePath
  }

  override def createInstance(args: Array[String]): Task = {
    val configFile = args.toList match {
      case Nil                                       => "sync_sr_raw/dev.json"
      case "dev" :: Nil                              => "sync_sr_raw/dev.json"
      case "config" :: path :: Nil                   => path
      case "catalog" :: _ :: Nil                     => "sync_sr_raw/dev.json"
      case "catalog" :: _ :: "config" :: path :: Nil => path
      case "config" :: path :: "catalog" :: _ :: Nil => path
      case single :: Nil                             => single
      case _                                         => "sync_sr_raw/dev.json"
    }

    val catalogName = args.toList match {
      case Nil                                       => DefaultCatalogName
      case "dev" :: Nil                              => DefaultCatalogName
      case "catalog" :: name :: Nil                  => name
      case "catalog" :: name :: "config" :: _ :: Nil => name
      case "config" :: _ :: "catalog" :: name :: Nil => name
      case _                                         => DefaultCatalogName
    }

    val config = Configuration.withSparkConfig
      .optional("rootDir", DefaultRootDir)
      .optional(Configuration.CONFIGFILE, configFile)
      .build(args)

    new SyncSrRawModelsImpl(config, catalogName)
  }

  override def shutdownHook: Unit = {
    SparkEnv.release()
  }
}

private class SyncSrRawModelsImpl(config: Configuration, catalogName: String) extends Task with LoggingTrait {

  private lazy val sparkConfig = config.getSparkConfig

  override def name: String = "SyncSrRawModels"
  override def uid: String = s"sync-sr-raw-${System.currentTimeMillis()}"
  override def shutdownHook: Unit = {}

  override def executeTask: Unit = {
    val outputDir = SyncSrRawModels.resolveOutputDir(config.getProperty("rootDir"))

    logger.info(s"=== Syncing ${SyncSrRawModels.CatalogName} models ===")
    logger.info(s"Base package: ${SyncSrRawModels.BasePackage}")
    logger.info(s"Output directory: $outputDir")

    // Initialize Spark
    logger.info("Initializing Spark connection...")
    SparkEnv.ensureInitialized(sparkConfig)
    logger.info("Spark connection established.")

    // Generate models
    logger.info("Starting code generation...")
    val generator = new TableModelGenerator(SyncSrRawModels.BasePackage)
    val catalogFQN = CatalogFQN(catalogName)

    generator.generateAndWriteForCatalog(catalogFQN, outputDir)

    logger.info(s"=== Sync complete ===")
    logger.info(s"Generated files at: $outputDir")
    logger.info(s"Package: ${SyncSrRawModels.BasePackage}.${SyncSrRawModels.CatalogName}.*")
  }
}
