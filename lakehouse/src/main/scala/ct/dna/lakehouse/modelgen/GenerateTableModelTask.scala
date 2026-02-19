package ct.dna.lakehouse.modelgen

import ct.dna.lakehouse.core.catalog.CatalogFQN
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits.ConfigurationBuilderHasSparkConfig
import ct.dna.lakehouse.core.runtime.implicits.ConfigurationHasSparkConfig
import ct.dna.utils.LoggingTrait
import ct.dna.utils.runtime.Configuration
import ct.dna.utils.runtime.Task
import ct.dna.utils.runtime.TaskEntryPoint

/** Model generation task to generate table models (Entity + TableSpec) from existing catalog tables.
  *
  * Uses the same SparkConfig pattern as lakehouseCore tests (via config.getSparkConfig). Identifiers are sanitized/escaped to ensure valid Scala output.
  */
object GenerateTableModelTask extends TaskEntryPoint {

  override def createInstance(args: Array[String]): Task = {
    // Uses withSparkConfig from lakehouseCore (same pattern as TestConfig)
    val config = Configuration.withSparkConfig
      .required("catalogName")
      .optional("basePackage", "ct.dna.lakehouse.model")
      .optional("outputDir", "")
      .optional("printOnly", "false")
      .optional(Configuration.CONFIGFILE, "")
      .build(args)

    new GenerateTableModelTaskImpl(config)
  }

  override def shutdownHook: Unit = {
    SparkEnv.release()
  }
}

private class GenerateTableModelTaskImpl(config: Configuration) extends Task with LoggingTrait {

  // Standard pattern from lakehouseCore - just use config.getSparkConfig
  private lazy val sparkConfig = config.getSparkConfig

  override def name: String = "GenerateTableModelTask"
  override def uid: String = s"generate-tablemodel-${System.currentTimeMillis()}"
  override def shutdownHook: Unit = {}

  override def executeTask: Unit = {
    val catalogName = config.getProperty("catalogName")
    val basePackage = config.getProperty("basePackage")
    val outputDir = config.getProperty("outputDir")
    val printOnly = config.getProperty("printOnly").toBoolean

    // Initialize Spark using standard pattern (same as TestWithConfigSpark)
    SparkEnv.ensureInitialized(sparkConfig)

    val generator = new TableModelGenerator(basePackage)
    val catalogFQN = CatalogFQN(catalogName)

    if (printOnly || outputDir.isEmpty) {
      // Print to console
      logger.info(s"Generating code for catalog '$catalogName' (print only)")
      generator.printForCatalog(catalogFQN)
    } else {
      // Write to files
      logger.info(s"Generating code for catalog '$catalogName' to: $outputDir")
      generator.generateAndWriteForCatalog(catalogFQN, outputDir)
    }

    logger.info("Code generation completed successfully")
  }
}
