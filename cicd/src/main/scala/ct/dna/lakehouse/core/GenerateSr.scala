package ct.dna.lakehouse.core

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

import scala.jdk.CollectionConverters._

import ct.dna.lakehouse.core.jobs.ColumnSource
import ct.dna.lakehouse.core.jobs.SrRawFieldInfo
import ct.dna.lakehouse.core.jobs.SrTableDef
import ct.dna.lakehouse.core.modelbuilder.ChangeKeyTableSpecAstBuilder
import ct.dna.utils.LocalDir
import ct.dna.utils.json.mapper
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.runtime.Configuration

/** Generates sr/<schema>/<table>.scala files from sr_table_def.json, using the ChangeKey AST builder.
  *
  * JSON (sr_table_def.json) is the source of truth for column names, order, and primary keys. sr_raw source files provide data types (via suffix inspection).
  *
  * Usage: sbt "cicd/runMain ct.dna.lakehouse.core.GenerateSr"
  */
object GenerateSr extends LoggingTrait {

  def main(args: Array[String]): Unit = {

    Thread.currentThread().setName("GenerateSr")
    logger.info("Starting GenerateSr")

    val config = Configuration
      .optional(Configuration.CONFIGFILE, "generate_sr.json")
      .required("baseDir")
      .required("basePackage")
      .required("srTableDefPath")
      .required("srRawBaseDir")
      .optional("filterSchema", "")
      .optional("filterTable", "")
      .build(args)

    val baseDir = LocalDir(config.getProperty("baseDir"))
    val basePackage = config.getProperty("basePackage")
    val srTableDefPath = config.getProperty("srTableDefPath")
    val srRawBaseDir = config.getProperty("srRawBaseDir")
    val filterSchema = Option(config.getProperty("filterSchema")).filter(_.nonEmpty)
    val filterTable = Option(config.getProperty("filterTable")).filter(_.nonEmpty)

    val inputFile = Paths.get(srTableDefPath)
    require(Files.exists(inputFile), s"sr_table_def.json not found at: $inputFile")

    val srTableDef = mapper.readValue[SrTableDef](Files.readString(inputFile))

    try {
      persistAsts(baseDir, basePackage, srRawBaseDir, srTableDef, filterSchema, filterTable)
      logger.info("GenerateSr completed successfully")
    } catch {
      case ex: Exception =>
        logger.error("GenerateSr failed", ex)
        throw ex
    }

    logger.info("DONE")
  }

  private def persistAsts(
      baseDir: LocalDir,
      basePackage: String,
      srRawBaseDir: String,
      srTableDef: SrTableDef,
      filterSchema: Option[String],
      filterTable: Option[String]
  ): Unit = {

    logger.debug(s"Generating ASTs from sr_table_def.json with basePackage '$basePackage'")

    val (tableAsts, schemaPackageAsts) = generateAsts(basePackage, srRawBaseDir, srTableDef, filterSchema, filterTable)
    logger.debug(s"Generated ASTs: ${tableAsts.size} table ASTs, ${schemaPackageAsts.size} schema ASTs")

    // Clean up stale schema directories when running a full (unfiltered) generation
    if (filterSchema.isEmpty && filterTable.isEmpty) {
      val basePath = baseDir.file(".").toPath.normalize()
      if (Files.exists(basePath)) {
        val existingDirs = Files
          .list(basePath)
          .iterator()
          .asScala
          .filter(p => Files.isDirectory(p))
          .map(_.getFileName.toString)
          .toSet
        val generatedSchemas = schemaPackageAsts.keySet
        val staleDirs = existingDirs -- generatedSchemas
        staleDirs.foreach { staleSchema =>
          val staleDir = basePath.resolve(staleSchema)
          logger.info(s"Removing stale schema directory: $staleSchema")
          Files.walk(staleDir).sorted(java.util.Comparator.reverseOrder()).forEach(Files.delete(_))
        }
      }
    }

    // Write schema package files (only if absent — may be hand-edited)
    schemaPackageAsts.foreach { case (schemaPackage, content) =>
      val schemaDir = baseDir.subDir(schemaPackage)
      schemaDir.createIfNotExists()
      val pkgFile = schemaDir.file("package.scala")
      if (!pkgFile.exists()) {
        logger.debug(s"Writing schema package: $schemaPackage/package.scala")
        writeUtf8(pkgFile, content)
      }
    }

    // Write table files
    tableAsts.foreach { case (schemaPackage, tableName, ast) =>
      val schemaDir = baseDir.subDir(schemaPackage)
      schemaDir.createIfNotExists()
      val tableFile = schemaDir.file(s"$tableName.scala")
      logger.debug(s"Writing table AST '$schemaPackage.$tableName' to '${tableFile.getAbsolutePath}'")
      writeUtf8(tableFile, ast.render)
    }

    // Clean up stale table files within each schema when running a full generation
    if (filterSchema.isEmpty && filterTable.isEmpty) {
      val generatedTablesBySchema = tableAsts.groupMap(_._1)(_._2)
      generatedTablesBySchema.foreach { case (schemaPackage, generatedTables) =>
        val schemaPath = baseDir.subDir(schemaPackage).file(".").toPath.normalize()
        if (Files.exists(schemaPath)) {
          val generatedFiles = generatedTables.map(_ + ".scala").toSet + "package.scala"
          Files
            .list(schemaPath)
            .iterator()
            .asScala
            .filter(p => Files.isRegularFile(p))
            .map(_.getFileName.toString)
            .filterNot(generatedFiles.contains)
            .foreach { staleFile =>
              logger.info(s"Removing stale table file: $schemaPackage/$staleFile")
              Files.delete(schemaPath.resolve(staleFile))
            }
        }
      }
    }

    logger.info(s"Persisted ASTs: ${tableAsts.size} tables, ${schemaPackageAsts.size} schemas")
  }

  private def generateAsts(
      basePackage: String,
      srRawBaseDir: String,
      srTableDef: SrTableDef,
      filterSchema: Option[String],
      filterTable: Option[String]
  ): (
      Seq[(String, String, ChangeKeyTableSpecAstBuilder.ChangeKeyTableSpecWithEntityAst)],
      Map[String, String]
  ) = {

    val srRawRootPackage = basePackage.replaceAll("""\bsr$""", "sr_raw")

    val tableAsts = collection.mutable.ListBuffer.empty[(String, String, ChangeKeyTableSpecAstBuilder.ChangeKeyTableSpecWithEntityAst)]
    val schemaPackageAsts = collection.mutable.Map.empty[String, String]

    srTableDef.schema
      .filter { case (s, _) => filterSchema.forall(_.equalsIgnoreCase(s)) }
      .foreach { case (schemaName, tables) =>
        val schemaPackage = schemaName.toLowerCase

        val candidateTables = tables
          .filter { case (t, _) => filterTable.forall(_.equalsIgnoreCase(t)) }
          .flatMap { case (tableName, tableDef) =>
            val cols = tableDef.resolvedColumns
            if (cols.nonEmpty) {
              Some(tableName -> cols)
            } else {
              logger.warn(s"[SKIP] $schemaName.$tableName — no columns (error or not fetched)")
              None
            }
          }
          .toSeq

        if (candidateTables.nonEmpty) {
          schemaPackageAsts(schemaPackage) = generateSchemaPackageFile(schemaPackage)

          candidateTables.foreach { case (tableName, columns) =>
            val tl = tableName.toLowerCase

            if (!srRawTableExists(srRawBaseDir, schemaPackage, tl)) {
              logger.warn(s"[SKIP-SR_RAW] $schemaName.$tableName — sr_raw/$schemaPackage/$tl.scala not found, skipping")
            } else {
              val srRawFields = loadSrRawFieldsForTable(srRawBaseDir, schemaPackage, tl)
              val rawSourceType = loadSrRawTypeForTable(srRawBaseDir, schemaPackage, tl)
                .getOrElse(s"E_$tl")

              val ast = ChangeKeyTableSpecAstBuilder.build(
                rootPackage = basePackage,
                srRawRootPackage = srRawRootPackage,
                schemaName = schemaName,
                tableName = tableName,
                columnSource = ColumnSource.Theobald(columns),
                srRawFields = srRawFields,
                rawSourceType = rawSourceType
              )

              tableAsts += ((schemaPackage, tl, ast))
            }
          }
        }
      }

    (tableAsts.toSeq, schemaPackageAsts.toMap)
  }

  // ---------- Schema package file generation ----------

  private def generateSchemaPackageFile(schemaPackage: String): String =
    s"""package ct.dna.lakehouse.sr
       |
       |import ct.dna.lakehouse.core.model.SchemaSpec
       |
       |package object $schemaPackage extends SchemaSpec {}
       |""".stripMargin

  // ---------- sr_raw per-table readers ----------

  private def srRawTableExists(srRawBasePath: String, schemaPackage: String, tableName: String): Boolean =
    Files.exists(Paths.get(srRawBasePath, schemaPackage, s"$tableName.scala"))

  private def loadSrRawFieldsForTable(srRawBasePath: String, schemaPackage: String, tableName: String): Seq[SrRawFieldInfo] = {
    val path = Paths.get(srRawBasePath, schemaPackage, s"$tableName.scala")
    if (!Files.exists(path)) return Seq.empty

    val fieldLine = """^\s*((?:@\w+(?:\([^)]*\))?\s+)*)((?:`[^`]+`|\w+))\s*:\s*([A-Za-z0-9_\.\[\]]+)\s*,?$""".r
    val content = Files.readString(path)

    var insideEntity = false
    content.linesIterator.flatMap { line =>
      if (!insideEntity && line.startsWith("case class E_")) {
        insideEntity = true
        None
      } else if (insideEntity && line.trim.startsWith(") extends Entity")) {
        insideEntity = false
        None
      } else if (insideEntity) {
        line match {
          case fieldLine(annotationBlock, rawName, scalaType) =>
            val annotations = annotationBlock.trim
            Some(
              SrRawFieldInfo(
                name = rawName.stripPrefix("`").stripSuffix("`"),
                scalaType = scalaType,
                isPrimaryKey = annotations.contains("@PK"),
                isNotNull = annotations.contains("@NotNull"),
                decimalAnnotation = """@Decimal\(\d+,\s*\d+\)""".r.findFirstIn(annotations)
              )
            )
          case _ => None
        }
      } else {
        None
      }
    }.toSeq
  }

  private def loadSrRawTypeForTable(srRawBasePath: String, schemaPackage: String, tableName: String): Option[String] = {
    val path = Paths.get(srRawBasePath, schemaPackage, s"$tableName.scala")
    if (!Files.exists(path)) return None
    val tableSpecPrefix = " extends TableSpec["
    Files
      .readString(path)
      .linesIterator
      .collectFirst {
        case line if line.startsWith("object ") && line.contains(tableSpecPrefix) && line.contains(" with Loaded") =>
          val typeStart = line.indexOf(tableSpecPrefix) + tableSpecPrefix.length
          var depth = 1
          var i = typeStart
          while (i < line.length && depth > 0) {
            line(i) match {
              case '[' => depth += 1
              case ']' => depth -= 1
              case _   =>
            }
            if (depth > 0) i += 1
          }
          line.substring(typeStart, i)
      }
  }

  private def writeUtf8(file: java.io.File, content: String): Unit =
    Files.write(file.toPath, content.getBytes(StandardCharsets.UTF_8))
}
