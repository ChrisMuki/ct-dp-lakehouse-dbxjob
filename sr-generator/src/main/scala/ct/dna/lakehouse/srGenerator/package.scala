package ct.dna.lakehouse

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

import scala.util.Try

import ct.dna.lakehouse.core.catalog.TableDesc
import ct.dna.lakehouse.core.catalog.TableFQN
import ct.dna.lakehouse.core.framework.IDResolver
import ct.dna.lakehouse.core.framework.internal.CatalogInternalDelegate
import ct.dna.lakehouse.core.model.CatalogID
import ct.dna.lakehouse.core.model.ClusterBy
import ct.dna.lakehouse.core.model.SchemaID
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.modelbuilder.CatalogSpecAstBuilder
import ct.dna.lakehouse.core.modelbuilder.ChangeKeyAstBuilder
import ct.dna.lakehouse.core.modelbuilder.ColumnMod
import ct.dna.lakehouse.core.modelbuilder.ColumnMod.SourceColumnMod
import ct.dna.lakehouse.core.modelbuilder.ColumnWithNameAccessorEmbeddedAstBuilder
import ct.dna.lakehouse.core.modelbuilder.GeneratedBlockPatcher
import ct.dna.lakehouse.core.modelbuilder.GeneratedBlockPatcher.MarkerConfig
import ct.dna.lakehouse.core.modelbuilder.LoadedTableSpecAstBuilder
import ct.dna.lakehouse.core.modelbuilder.LoadedTableSpecAstBuilder.LoadedTableSpecWithEntityAst
import ct.dna.lakehouse.core.modelbuilder.SchemaSpecAstBuilder
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.utils.LocalDir
import ct.dna.utils.LoggingTrait

package object srGenerator extends LoggingTrait {

  final val SourceRawCatalogId: CatalogID = CatalogID("sr_raw")
  final val SourceCatalogId: CatalogID = CatalogID("sr")
  final val PackageObjectFileName: String = "package.scala"

  final val CatalogSpecMarkerConfig = MarkerConfig("// CATALOG SPEC AUTO GENERATED:START", "// CATALOG SPEC AUTO GENERATED:END")
  final val SchemaSpecMarkerConfig = MarkerConfig("// SCHEMA SPEC AUTO GENERATED:START", "// SCHEMA SPEC AUTO GENERATED:END")
  final val LoadedTableSpecMarkerConfig = MarkerConfig("// LOADED TABLE SPEC AUTO GENERATED:START", "// LOADED TABLE SPEC AUTO GENERATED:END")
  final val ColumnAccessorMarkerConfig = MarkerConfig("// COLUMN ACCESSOR AUTO GENERATED:START", "// COLUMN ACCESSOR AUTO GENERATED:END")
  final val ChangeKeyTableSpecMarkerConfig = MarkerConfig("// CHANGE KEY TABLE SPEC AUTO GENERATED:START", "// CHANGE KEY TABLE SPEC AUTO GENERATED:END")

  // ---------- Unity discovery ----------

  /** Discover all Unity table FQNs in the given catalog (excluding `information_schema`). Sequential. */
  def discoverTableFQNs(catalogId: CatalogID): Map[TableID, TableFQN] = {
    val resolver = SparkEnv.idResolver match {
      case x: IDResolver.Bijection => x
      case _                       => throw new IllegalStateException("IDResolver must be a Bijection")
    }
    val catalogFQN = resolver.asFQN(catalogId)
    val schemaFQNs = CatalogInternalDelegate.findSchemaFQN(catalogFQN).filterNot(_.name == "information_schema")
    val tableFQNs = schemaFQNs.flatMap(CatalogInternalDelegate.findTableFQN)
    logger.info(s"Discovered ${tableFQNs.size} tables across ${schemaFQNs.size} schemas in catalog '${catalogId.name}'")
    tableFQNs.map(fqn => resolver.asID(fqn) -> fqn).toMap
  }

  // ---------- Catalog/schema scaffolding ----------

  /** Create catalog dir and write catalog/schema `package.scala` files. Sequential. */
  def prepareCatalogDir(
      baseDir: LocalDir,
      basePackage: String,
      catalogId: CatalogID,
      schemaIds: Set[SchemaID]
  ): LocalDir = {
    val catalogAst = CatalogSpecAstBuilder.build(basePackage, catalogId)
    val catDir = baseDir.subDir(catalogAst.catalogId.name)
    catDir.createIfNotExists()
    patchFile(catDir.file(PackageObjectFileName), catalogAst.render, CatalogSpecMarkerConfig)
    schemaIds.foreach { schemaId =>
      val schemaAst = SchemaSpecAstBuilder.build(basePackage, schemaId)
      val schemaDir = catDir.subDir(schemaId.name)
      schemaDir.createIfNotExists()
      patchFile(schemaDir.file(PackageObjectFileName), schemaAst.render, SchemaSpecMarkerConfig)
    }
    catDir
  }

  // ---------- ColumnMod resolution ----------

  /** Resolve the effective ColumnMods for a single sr_raw table, validating against the actual sr_raw entity fields. Source column mods that reference fields
    * not present in the sr_raw schema are either skipped (with warning) or cause an exception, controlled by `skipUnusedColumnMod`.
    */
  def parseColumnMods(
      srRawTableId: TableID,
      input: SrTableInput,
      rawAst: LoadedTableSpecWithEntityAst,
      skipUnusedColumnMod: Boolean
  ): Seq[ColumnMod] = {
    val srRawEntity = rawAst.caseClasses.find(_.extendsEntity)
    val srRawFieldNames = srRawEntity.map(_.fields.map(_.name).toSet).getOrElse(Set.empty)

    val (sourceColumnMods, otherColumnMods) = input.columnMods.partition(_.isInstanceOf[SourceColumnMod])
    val (validSourceColumnMods, invalidSourceColumnMods) =
      sourceColumnMods.asInstanceOf[Seq[SourceColumnMod]].partition(cm => srRawFieldNames.contains(cm.sourceColumnName))

    if (invalidSourceColumnMods.nonEmpty) {
      val msg =
        s"${invalidSourceColumnMods.size} ColumnMod(s) for '$srRawTableId' reference fields not in sr_raw schema: ${invalidSourceColumnMods.map(_.sourceColumnName).mkString(", ")}"
      if (skipUnusedColumnMod) logger.warn(msg)
      else throw new IllegalStateException(msg)
    }

    if (skipUnusedColumnMod) validSourceColumnMods ++ otherColumnMods else input.columnMods
  }

  // ---------- Per-table processing ----------

  /** Build & persist sr_raw artifacts for the given table desc, and (when an [[SrTableInput]] is provided) also build & persist the corresponding sr ChangeKey
    * artifacts. Returns a `Try[Unit]` so callers can aggregate failures across tables without aborting other work.
    */
  def processTableDesc(
      basePackage: String,
      rawCatDir: LocalDir,
      srCatDir: LocalDir,
      tableId: TableID,
      tableDesc: TableDesc,
      srInput: Option[SrTableInput],
      skipUnusedColumnMod: Boolean
  ): Try[Unit] = Try {
    tableDesc match {
      case unityDesc: TableDesc.UnityTableDesc =>
        // sr_raw
        val rawAst = LoadedTableSpecAstBuilder.build(basePackage, tableId, unityDesc)
        writeTableFile(rawCatDir, tableId, rawAst.render, LoadedTableSpecMarkerConfig, ColumnWithNameAccessorEmbeddedAstBuilder.build(rawAst))

        // sr (only if configured)
        srInput.foreach { input =>
          input.strategy match {
            case WriteStrategy.ChangeKey =>
              val srTableId = theobald.srTableIdFor(tableId)
              val columnMods = parseColumnMods(tableId, input, rawAst, skipUnusedColumnMod)
              logger.debug(s"Building ChangeKey AST for '$tableId' -> '$srTableId' (${columnMods.size} column mods)")
              val srAst = ChangeKeyAstBuilder.build(basePackage, srTableId, unityDesc, rawAst, columnMods, ClusterBy.PRIMARY_KEY)
              writeTableFile(srCatDir, srTableId, srAst.render, ChangeKeyTableSpecMarkerConfig, ColumnWithNameAccessorEmbeddedAstBuilder.build(srAst))
          }
        }

      case _ =>
        logger.debug(s"Skipping non-Unity table desc for '$tableId'")
    }
  }

  /** Write the spec block + accessor block for one table file. Each table writes to its own file, so this is safe to call concurrently from multiple threads as
    * long as different tableIds are used.
    */
  private def writeTableFile(
      catDir: LocalDir,
      tableId: TableID,
      specBlock: String,
      specMarker: MarkerConfig,
      accessorAst: ColumnWithNameAccessorEmbeddedAstBuilder.ColumnWithNameAccessorEmbeddedAst
  ): Unit = {
    val schemaDir = catDir.subDir(tableId.schemaId.name)
    val tableFile = schemaDir.file(s"${tableId.name}.scala")
    patchFile(tableFile, specBlock, specMarker)
    patchFile(tableFile, accessorAst.render, ColumnAccessorMarkerConfig)
  }

  // ---------- file helpers ----------

  def patchFile(file: File, generatedBlock: String, markerConfig: MarkerConfig): Unit = {
    ensureFileExists(file)
    val original = new String(Files.readAllBytes(file.toPath), StandardCharsets.UTF_8)
    val patched = GeneratedBlockPatcher.patchOrAppend(original, generatedBlock, markerConfig)
    if (patched != original) {
      writeUtf8(file, patched)
      logger.debug(s"Patched block (start='${markerConfig.startMarker}') in '${file.getAbsolutePath}'")
    } else {
      logger.debug(s"Block (start='${markerConfig.startMarker}') unchanged in '${file.getAbsolutePath}'")
    }
  }

  def ensureFileExists(file: File): Unit =
    if (!file.exists()) { Option(file.getParentFile).foreach(_.mkdirs()); writeUtf8(file, "") }

  def deleteOrphans(catDir: LocalDir, knownSchemaIds: Set[SchemaID], knownTableIds: Set[TableID]): Unit = {
    val knownSchemaNames = knownSchemaIds.map(_.name)
    val knownTablesBySchema = knownTableIds.groupBy(_.schemaId.name).map { case (s, ids) => s -> ids.map(_.name) }
    Option(catDir.dir.listFiles()).getOrElse(Array.empty).filter(_.isDirectory).foreach { schemaDir =>
      if (!knownSchemaNames.contains(schemaDir.getName)) deleteRecursively(schemaDir)
      else {
        val knownTables = knownTablesBySchema.getOrElse(schemaDir.getName, Set.empty)
        Option(schemaDir.listFiles())
          .getOrElse(Array.empty)
          .filter(f => f.isFile && f.getName.endsWith(".scala") && f.getName != PackageObjectFileName)
          .filterNot(f => knownTables.contains(f.getName.stripSuffix(".scala")))
          .foreach(_.delete())
      }
    }
  }

  def deleteRecursively(file: File): Unit = {
    if (file.isDirectory) Option(file.listFiles()).getOrElse(Array.empty).foreach(deleteRecursively)
    file.delete()
  }

  def writeUtf8(file: File, content: String): Unit =
    Files.write(file.toPath, content.getBytes(StandardCharsets.UTF_8))
}
