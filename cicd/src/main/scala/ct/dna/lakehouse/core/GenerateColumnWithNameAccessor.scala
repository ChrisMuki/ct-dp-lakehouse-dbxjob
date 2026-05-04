package ct.dna.lakehouse.core

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import scala.util.Failure
import scala.util.Try

import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.internal.findAllObjectsOfType
import ct.dna.lakehouse.core.modelbuilder.ColumnWithNameAccessorEmbeddedAstBuilder
import ct.dna.lakehouse.core.modelbuilder.GeneratedBlockPatcher
import ct.dna.lakehouse.srGenerator
import ct.dna.utils.LoggingTrait
import ct.dna.utils.runtime.Configuration

/** Scans all TableSpec objects below the configured base package, resolves their source files from package + object name, builds the generated runtime-column
  * block, and then patches each Scala file.
  *
  * If a file already contains exactly one generated START/END marker pair, only that block is replaced. If no markers exist, the block is appended at the end
  * of the file. Any inconsistent marker state fails for the affected file, while processing continues for the remaining files. All errors are collected and
  * reported at the end.
  */
object GenerateColumnWithNameAccessor extends LoggingTrait {

  def main(args: Array[String]): Unit = {

    Thread.currentThread().setName("GenerateColumnWithNameAccessor")
    logger.info("Starting GenerateColumnWithNameAccessor")

    val config = Configuration
      .optional(Configuration.CONFIGFILE, "generate_columns.json")
      .required("baseDir")
      .required("basePackage")
      .build(args)

    val baseDir = Paths.get(config.getProperty("baseDir"))
    val basePackage = config.getProperty("basePackage")

    logger.info(s"Processing base package '$basePackage' with base directory '$baseDir'")

    val allTableSpecs = findAllObjectsOfType(classOf[TableSpec[Entity]], Seq(basePackage))

    logger.info(s"Found ${allTableSpecs.size} TableSpec object(s)")

    val markerConfig = srGenerator.ColumnAccessorMarkerConfig

    var changedCount = 0
    var unchangedCount = 0

    val results = allTableSpecs.map { tableSpec =>
      val trip = s"${tableSpec.id.schemaId.catalogId.name}.${tableSpec.id.schemaId.name}.${tableSpec.id.name}"

      val result = Try {
        val filePath = resolveScalaFile(baseDir, tableSpec)

        if (!Files.exists(filePath)) {
          throw new IllegalStateException(s"Expected Scala file does not exist for $trip")
        }

        val originalContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8)
        val generatedBlock = ColumnWithNameAccessorEmbeddedAstBuilder.build(tableSpec).render
        val patchedContent = GeneratedBlockPatcher.patchOrAppend(originalContent, generatedBlock, markerConfig)

        if (patchedContent != originalContent) {
          Files.write(filePath, patchedContent.getBytes(StandardCharsets.UTF_8))
          changedCount += 1
          logger.info(s"Updated generated columns block for $trip")
        } else {
          unchangedCount += 1
          logger.info(s"No changes required for $trip")
        }
      }

      trip -> result
    }

    val errors = results.collect { case (trip, Failure(exception)) => trip -> exception }

    errors.foreach { case (trip, exception) =>
      logger.error(s"Failed to process $trip: ${exception.getMessage}")
    }

    if (errors.nonEmpty) {
      throw new RuntimeException(s"Processing completed with ${errors.size} error(s). See logs for details.")
    } else {
      logger.info(s"DONE [changed=$changedCount unchanged=$unchangedCount]")
    }
  }

  private def resolveScalaFile(baseDir: Path, tableSpec: TableSpec[_ <: Entity]): Path = {

    val packagePath = tableSpec.getClass.getPackage.getName.split('.').foldLeft(baseDir) { (acc, part) => acc.resolve(part) }
    packagePath.resolve(s"${tableSpec.id.name}.scala")
  }
}
