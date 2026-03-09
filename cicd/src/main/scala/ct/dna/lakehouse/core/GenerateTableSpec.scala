package ct.dna.lakehouse.core

import ct.dna.lakehouse.core.catalog.IDResolver
import ct.dna.lakehouse.core.catalog.internal.CatalogManager
import ct.dna.lakehouse.core.catalog.internal.TableManagerDelegation
import ct.dna.lakehouse.core.model.CatalogID
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.modelbuilder.LoadedTableSpecAstBuilder
import ct.dna.lakehouse.core.modelbuilder.CatalogSpecAstBuilder
import ct.dna.lakehouse.core.modelbuilder.SchemaSpecAstBuilder
import ct.dna.utils.LocalDir
import ct.dna.lakehouse.core.catalog.TableDesc
import ct.dna.utils.json.mapper
import ct.dna.utils.runtime.Configuration
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.LoggingTrait
object GenerateSrRaw extends LoggingTrait {
  def main(args: Array[String]): Unit = {
    Thread.currentThread().setName("GenerateSrRaw")
    logger.info("Starting GenerateSrRaw")

    val config = Configuration
      .optional(Configuration.CONFIGFILE, "generate_sr_raw.json")
      .withSparkConfig
      .required("baseDir")
      .required("basePackage")
      .required("catalogId")
      .build(args)

    val baseDirProp = config.getProperty("baseDir")
    val basePackage = config.getProperty("basePackage")
    val catalogIdProp = config.getProperty("catalogId")

    logger.debug("Initializing Spark environment")
    SparkEnv.ensureInitialized(config.getSparkConfig)
    logger.debug("Spark environment initialized successfully")
    val baseDir = LocalDir(baseDirProp)
    val catalogId = mapper.readValue[CatalogID](catalogIdProp)

    try {
      persistAsts(baseDir, basePackage, catalogId)
      logger.info(s"GenerateSrRaw completed successfully for catalog '${catalogId.name}'")
    } catch {
      case ex: Throwable =>
        logger.error(s"GenerateSrRaw failed for catalog '${catalogId.name}'", ex)
        throw ex
    }

    println("DONE")
  }

  def persistAsts(baseDir: LocalDir, basePackage: String, catalogId: CatalogID) = {
    logger.debug(s"Generating ASTs for catalog '${catalogId.name}' and basePackage '$basePackage'")

    val (tableAsts, schemaAsts, catalogAst) = generateAsts(basePackage, catalogId)
    logger.debug(s"Generated ASTs: ${tableAsts.size} table ASTs, ${schemaAsts.size} schema ASTs")

    val catDir = baseDir.subDir(catalogAst.catalogId.name)
    val schemaDirs = schemaAsts.map(schemaAst => catDir.subDir(schemaAst.schemaId.name))
    logger.debug(s"Preparing output directories under '$catDir'")
    baseDir.makeEmptyDir()
    schemaDirs.foreach(_.makeEmptyDir())

    val catalogFile = catDir.file("package.scala")

    logger.debug(s"Writing catalog AST to '${catalogFile.getAbsolutePath}'")
    writeUtf8(catalogFile, catalogAst.render)

    tableAsts.foreach {
      case (tableId, ast) => {
        val tableFile = catDir.subDir(tableId.schemaId.name).file(s"${tableId.name}.scala")
        logger.debug(s"Writing table AST '${tableId.schemaId.name}.${tableId.name}' to '${tableFile.getAbsolutePath}'")
        writeUtf8(tableFile, ast.render)
      }
    }
    schemaAsts.foreach(ast => {
      val schemaFile = catDir.subDir(ast.schemaId.name).file("package.scala")
      logger.debug(s"Writing schema AST '${ast.schemaId.name}' to '${schemaFile.getAbsolutePath}'")
      writeUtf8(schemaFile, ast.render)
    })

    logger.info(s"Persisted ASTs for catalog '${catalogId.name}': ${tableAsts.size} tables, ${schemaAsts.size} schemas")

  }

  def generateAsts(basePackage: String, catalogId: CatalogID) = {
    logger.debug(s"Identifying table descriptions for catalog '${catalogId.name}'")
    val tableDescs = identifyTableDesc(catalogId)
    logger.debug(s"Identified ${tableDescs.size} Unity table descriptions")

    val tableAsts = tableDescs.map { case (tableId, tableDesc) =>
      tableId -> LoadedTableSpecAstBuilder.build(basePackage, tableId, tableDesc)
    }
    val schemaAsts = tableDescs.keySet.map(_.schemaId).map(SchemaSpecAstBuilder.build(basePackage, _))
    val catalogAst = CatalogSpecAstBuilder.build(basePackage, catalogId)

    (tableAsts, schemaAsts, catalogAst)

  }

  def identifyTableDesc(catalogId: CatalogID) = {
    logger.debug(s"Resolving catalog '${catalogId.name}'")
    val resolver = SparkEnv.idResolver match {
      case x: IDResolver.Bijection => x
      case _: IDResolver           => throw new IllegalStateException("IDResolver must be a Bijection for TableSpecGenerator")
    }

    val catalogFQN = resolver.asFQN(catalogId)
    logger.debug(s"Catalog FQN resolved to '$catalogFQN'")

    val schemasFQNs = CatalogManager.findSchemaFQN(catalogFQN).filterNot(_.name == "information_schema")
    logger.debug(s"Found ${schemasFQNs.size} schemas (excluding information_schema)")

    val tableFQNs = schemasFQNs.flatMap(CatalogManager.findTableFQN)
    logger.debug(s"Found ${tableFQNs.size} tables before description lookup")

    val unityTables = tableFQNs.zipWithIndex
      .map { case (tableFQN, index) =>
        if (index % 10 == 9) {
          logger.debug(s"Processing table ${index + 1}/${tableFQNs.size}: '$tableFQN'")
        }
        val tableId = resolver.asID(tableFQN)
        val tableDesc = TableManagerDelegation.readTableDesc(tableFQN)
        tableId -> tableDesc
      }
      .toMap
      .collect { case (tableId, tableDesc: TableDesc.UnityTableDesc) => tableId -> tableDesc }

    logger.debug(s"Retained ${unityTables.size} Unity tables after filtering")
    unityTables
  }
  private def writeUtf8(file: java.io.File, content: String): Unit = {
    val writer = new java.io.BufferedWriter(
      new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8)
    )
    try writer.write(content)
    finally writer.close()
  }
}
