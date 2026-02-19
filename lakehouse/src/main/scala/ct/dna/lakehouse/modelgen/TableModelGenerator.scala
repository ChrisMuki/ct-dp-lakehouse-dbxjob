package ct.dna.lakehouse.modelgen

import java.io.File
import java.io.PrintWriter

import scala.collection.mutable
import scala.util.Using

import ct.dna.lakehouse.core.catalog.CatalogFQN
import ct.dna.lakehouse.core.catalog.CatalogManager
import ct.dna.lakehouse.core.catalog.SchemaFQN
import ct.dna.lakehouse.core.catalog.TableDesc
import ct.dna.lakehouse.core.catalog.TableDesc.UnityTableDesc
import ct.dna.lakehouse.core.catalog.TableFQN
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.utils.LoggingTrait
import org.apache.spark.sql.types._

/** Generates table model code (Entity case classes + TableSpec objects) from existing Unity Catalog tables.
  *
  * Generates proper Scala source code structure:
  * {{{
  * <outputDir>/
  *   <catalog_name>/
  *     package.scala                    # CatalogSpec
  *     <schema_name>/
  *       package.scala                  # SchemaSpec
  *       tables.scala                   # Entity case classes + TableSpec objects
  * }}}
  *
  * Notes:
  *   - Identifiers are sanitized/escaped to produce valid Scala names.
  *   - Spark must be initialized before calling generation methods.
  */
class TableModelGenerator(basePackage: String) extends LoggingTrait {

  /** Result of code generation containing all generated files
    */
  case class GeneratedFiles(
      catalogPackage: (String, String), // (path, content)
      schemaPackages: Map[SchemaFQN, (String, String)], // schema -> (path, content)
      tableDefs: Map[SchemaFQN, (String, String)] // schema -> (path, content)
  )

  /** Generate and write all files for a catalog to the specified output directory
    */
  def generateAndWriteForCatalog(catalogFQN: CatalogFQN, outputDir: String): Unit = {
    val generated = generateForCatalog(catalogFQN)
    writeFiles(generated, outputDir)
  }

  /** Generate code for all schemas and tables in a catalog. Requires Spark to be initialized.
    */
  // System schemas to skip
  private val SkipSchemas = Set("information_schema")

  def generateForCatalog(catalogFQN: CatalogFQN): GeneratedFiles = {
    SparkEnv.requireInitialized()
    require(CatalogManager.catalogExists(catalogFQN), s"Catalog '${catalogFQN.fqn}' does not exist")

    val catalogName = catalogFQN.name
    val allSchemas = CatalogManager.findSchemaFQN(catalogFQN)
    val schemas = allSchemas.filterNot(s => SkipSchemas.contains(s.name))
    logger.info(s"Found ${allSchemas.size} schemas in catalog ${catalogFQN.fqn} (skipping ${allSchemas.size - schemas.size} system schemas)")

    // Generate catalog package.scala
    val catalogPackagePath = s"$catalogName/package.scala"
    val catalogPackageContent = generateCatalogPackage(catalogName)

    // Generate schema packages and tables
    val schemaPackages = mutable.Map.empty[SchemaFQN, (String, String)]
    val tableDefs = mutable.Map.empty[SchemaFQN, (String, String)]

    schemas.foreach { schemaFQN =>
      val schemaName = schemaFQN.name

      // Schema package.scala
      val schemaPackagePath = s"$catalogName/$schemaName/package.scala"
      val schemaPackageContent = generateSchemaPackage(catalogName, schemaName)
      schemaPackages(schemaFQN) = (schemaPackagePath, schemaPackageContent)

      // Tables file
      try {
        val tables = CatalogManager.findTableFQN(schemaFQN)
        logger.info(s"Found ${tables.size} tables in schema ${schemaFQN.fqn}")

        if (tables.nonEmpty) {
          val tablesPath = s"$catalogName/$schemaName/tables.scala"
          val tablesContent = generateTablesFile(catalogName, schemaName, tables)
          tableDefs(schemaFQN) = (tablesPath, tablesContent)
        }
      } catch {
        case e: Exception =>
          logger.warn(s"Failed to process schema ${schemaFQN.fqn}: ${e.getMessage}")
      }
    }

    GeneratedFiles(
      catalogPackage = (catalogPackagePath, catalogPackageContent),
      schemaPackages = schemaPackages.toMap,
      tableDefs = tableDefs.toMap
    )
  }

  /** Write generated files to disk
    */
  def writeFiles(generated: GeneratedFiles, outputDir: String): Unit = {
    val baseDir = new File(outputDir)
    if (!baseDir.exists()) baseDir.mkdirs()

    // Write catalog package
    writeFile(baseDir, generated.catalogPackage._1, generated.catalogPackage._2)

    // Write schema packages
    generated.schemaPackages.foreach { case (_, (path, content)) =>
      writeFile(baseDir, path, content)
    }

    // Write table definitions
    generated.tableDefs.foreach { case (_, (path, content)) =>
      writeFile(baseDir, path, content)
    }

    logger.info(s"Generated files written to: ${baseDir.getAbsolutePath}")
  }

  private def writeFile(baseDir: File, relativePath: String, content: String): Unit = {
    val file = new File(baseDir, relativePath)
    file.getParentFile.mkdirs()
    Using.resource(new PrintWriter(file)) { writer =>
      writer.write(content)
      logger.info(s"  Written: $relativePath")
    }
  }

  // ========== Code Generation Methods ==========

  private[modelgen] def generateCatalogPackage(catalogName: String): String = {
    s"""package $basePackage
       |
       |import ct.dna.lakehouse.core.model.CatalogSpec
       |
       |package object $catalogName extends CatalogSpec {}
       |""".stripMargin
  }

  private[modelgen] def generateSchemaPackage(catalogName: String, schemaName: String): String = {
    s"""package $basePackage.$catalogName
       |
       |import ct.dna.lakehouse.core.model.SchemaSpec
       |
       |package object $schemaName extends SchemaSpec {}
       |""".stripMargin
  }

  private[modelgen] def generateTablesFile(catalogName: String, schemaName: String, tables: Seq[TableFQN]): String = {
    SparkEnv.requireInitialized()
    val sb = new StringBuilder()
    val packageName = s"$basePackage.$catalogName.$schemaName"

    // Package declaration
    sb.append(s"package $packageName\n\n")

    // Collect all needed imports (pk is added conditionally based on actual usage)
    val baseImports = Set(
      "ct.dna.lakehouse.core.framework.origin.Loaded",
      "ct.dna.lakehouse.core.model.Entity.LakehouseEntity",
      "ct.dna.lakehouse.core.model.TableSpec"
    )

    val tableDescriptions = tables.flatMap(readTableDescSafe)
    val imports = collectImports(baseImports, tableDescriptions.map(_._2))

    // Write imports
    imports.toSeq.sorted.foreach { imp =>
      sb.append(s"import $imp\n")
    }
    sb.append("\n")

    // Comment
    sb.append("// This file is auto-generated. Do not edit manually.\n\n")

    // Generate entity and tablespec for each table
    tableDescriptions.foreach { case (tableFQN, tableDesc) =>
      sb.append(generateTableCode(tableFQN.name, tableDesc))
      sb.append("\n")
    }

    sb.toString()
  }

  private def isPkColumn(tableDesc: TableDesc, columnName: String): Boolean = tableDesc match {
    case u: UnityTableDesc => u.pkColumns.contains(columnName)
    case _                 => false
  }

  private def addTypeImports(dataType: DataType, imports: mutable.Set[String]): Unit = {
    dataType match {
      case DateType         => imports += "java.sql.Date"
      case TimestampType    => imports += "java.sql.Timestamp"
      case TimestampNTZType => imports += "java.time.LocalDateTime"
      case d: DecimalType if d.precision != 38 || d.scale != 18 =>
        imports += "ct.dna.lakehouse.core.model.Entity.decimal"
      case ArrayType(elementType, _) => addTypeImports(elementType, imports)
      case MapType(keyType, valueType, _) =>
        addTypeImports(keyType, imports)
        addTypeImports(valueType, imports)
      case st: StructType =>
        // Also check nested struct fields for decimal types
        st.fields.foreach(f => addTypeImports(f.dataType, imports))
      case _ => // No additional imports needed
    }
  }

  /** Generate code for a single table from its TableDesc. Identifiers are sanitized to ensure valid Scala output. Exposed for testing.
    */
  private[modelgen] def generateTableCode(tableName: String, tableDesc: TableDesc): String = {
    val entityName = safeIdentifier(s"Entity_$tableName")
    val tableIdentifier = safeIdentifier(tableName)

    val pkColumns = tableDesc match {
      case u: UnityTableDesc => u.pkColumns.toSet
      case _                 => Set.empty[String]
    }

    val clusterByColumns = tableDesc.clusterByColumns.toSet

    // Collect all nested structs from the schema
    val nestedStructs = mutable.LinkedHashMap.empty[String, StructType]
    collectNestedStructs(tableDesc.schema, tableName, nestedStructs)

    val sb = new StringBuilder()

    // Generate nested case classes first (without @LakehouseEntity - only root entity has it)
    nestedStructs.foreach { case (structName, structType) =>
      sb.append(s"case class $structName(\n")
      val structFields = structType.fields.map { field =>
        val decimalAnnotation = getDecimalAnnotation(field.dataType)
        val scalaType = sparkTypeToScala(field.dataType, field.nullable, nestedStructs)
        s"    $decimalAnnotation${safeIdentifier(field.name)}: $scalaType"
      }
      sb.append(structFields.mkString(",\n"))
      sb.append("\n)\n\n")
    }

    // Entity case class with @LakehouseEntity annotation
    sb.append(s"@LakehouseEntity\ncase class $entityName(\n")
    val fields = tableDesc.schema.fields.map { field =>
      val annotations = mutable.ListBuffer.empty[String]
      if (pkColumns.contains(field.name)) annotations += "@pk"
      if (clusterByColumns.contains(field.name)) annotations += "@clusterby"
      // Only add @notnull for non-nullable, non-PK, non-primitive fields
      // Scala primitives (Int, Long, etc.) are already non-nullable by nature
      if (!field.nullable && !pkColumns.contains(field.name) && !isScalaPrimitive(field.dataType)) annotations += "@notnull"
      // Add @decimal annotation for non-default precision/scale
      getDecimalAnnotation(field.dataType) match {
        case s if s.nonEmpty => annotations += s.trim
        case _               => // no annotation needed
      }

      val annotationStr = if (annotations.nonEmpty) annotations.mkString(" ") + " " else ""
      val scalaType = sparkTypeToScala(field.dataType, field.nullable, nestedStructs)
      s"    $annotationStr${safeIdentifier(field.name)}: $scalaType"
    }
    sb.append(fields.mkString(",\n"))
    sb.append("\n)\n\n")

    // TableSpec object
    val cdfEnabled = tableDesc.enableChangeDataFeed
    val timetravelDays = tableDesc.timetravelDays

    val clusterByAuto = tableDesc match {
      case u: UnityTableDesc => u.clusterByAuto
      case _                 => false
    }

    val tableSpecParams = mutable.ListBuffer.empty[String]
    // Only add non-default parameters
    if (!cdfEnabled) tableSpecParams += "enableChangeDataFeed = false"
    if (!clusterByAuto) tableSpecParams += "clusterByAuto = false"
    if (timetravelDays != 35) tableSpecParams += s"timetravelDays = $timetravelDays"

    val paramsStr = if (tableSpecParams.nonEmpty) tableSpecParams.mkString("(", ", ", ")") else ""

    sb.append(s"object $tableIdentifier extends TableSpec[$entityName]$paramsStr with Loaded\n")

    sb.toString()
  }

  /** Recursively collect all nested StructTypes and assign them names */
  private def collectNestedStructs(
      structType: StructType,
      parentName: String,
      collected: mutable.LinkedHashMap[String, StructType]
  ): Unit = {
    structType.fields.foreach { field =>
      collectNestedStructsFromType(field.dataType, s"${parentName}_${field.name}", collected)
    }
  }

  private def collectNestedStructsFromType(
      dataType: DataType,
      baseName: String,
      collected: mutable.LinkedHashMap[String, StructType]
  ): Unit = {
    dataType match {
      case st: StructType =>
        val structName = safeIdentifier(toCamelCase(baseName))
        if (!collected.contains(structName)) {
          collected(structName) = st
          // Recursively collect nested structs within this struct
          collectNestedStructs(st, structName, collected)
        }
      case ArrayType(elementType, _) =>
        collectNestedStructsFromType(elementType, baseName, collected)
      case MapType(keyType, valueType, _) =>
        collectNestedStructsFromType(keyType, s"${baseName}_key", collected)
        collectNestedStructsFromType(valueType, s"${baseName}_value", collected)
      case _ => // Primitive or simple type, nothing to collect
    }
  }

  /** Convert snake_case to CamelCase for nested struct names */
  private def toCamelCase(name: String): String = {
    name.split("_").map(_.capitalize).mkString
  }

  /** Get @decimal annotation string if precision/scale differs from default (38, 18). Returns empty string if default or not a DecimalType.
    *
    * lakehouseCore defaults: DecimalType(38, 18) when no @decimal annotation is present.
    */
  private[modelgen] def getDecimalAnnotation(dataType: DataType): String = {
    dataType match {
      case d: DecimalType if d.precision != 38 || d.scale != 18 =>
        s"@decimal(${d.precision}, ${d.scale}) "
      case _ => ""
    }
  }

  /** Check if a Spark DataType maps to a Scala primitive type.
    *
    * Scala primitives (Int, Long, Short, Byte, Double, Float, Boolean) are inherently non-nullable, so @notnull annotation is redundant for them.
    */
  private[modelgen] def isScalaPrimitive(dataType: DataType): Boolean = dataType match {
    case IntegerType | LongType | ShortType | ByteType | DoubleType | FloatType | BooleanType => true
    case _                                                                                    => false
  }

  /** Maps Spark DataType to Scala type string, following lakehouseCore conventions:
    *   - Non-null primitives: Use Scala primitives (Int, Long, Boolean, etc.)
    *   - Nullable primitives: Use Java boxed types (java.lang.Integer, java.lang.Long, etc.)
    *   - Reference types (String, Date, etc.): Always reference type (inherently nullable)
    *   - Collections: Always use boxed element types (required by Spark)
    *   - Nested structs: Reference to generated case class name
    *
    * This matches StructTypeBuilder which accepts both primitive and boxed types, and determines nullability from whether it's a primitive or boxed type.
    */
  private[modelgen] def sparkTypeToScala(
      dataType: DataType,
      nullable: Boolean,
      nestedStructs: mutable.LinkedHashMap[String, StructType] = mutable.LinkedHashMap.empty
  ): String = {
    // Helper to find the generated name for a struct type
    def findStructName(st: StructType): String = {
      nestedStructs.find(_._2 == st).map(_._1).getOrElse("/* unknown struct */")
    }

    // Helper for collection element types - always boxed (Spark requires nullable elements)
    def elementType(dt: DataType, fieldName: String): String = dt match {
      case StringType         => "String"
      case IntegerType        => "java.lang.Integer"
      case LongType           => "java.lang.Long"
      case ShortType          => "java.lang.Short"
      case ByteType           => "java.lang.Byte"
      case DoubleType         => "java.lang.Double"
      case FloatType          => "java.lang.Float"
      case BooleanType        => "java.lang.Boolean"
      case DateType           => "Date"
      case TimestampType      => "Timestamp"
      case TimestampNTZType   => "LocalDateTime"
      case BinaryType         => "Array[Byte]"
      case _: DecimalType     => "java.math.BigDecimal"
      case ArrayType(et, _)   => s"Seq[${elementType(et, fieldName)}]"
      case MapType(kt, vt, _) => s"Map[${elementType(kt, fieldName + "_key")}, ${elementType(vt, fieldName + "_value")}]"
      case st: StructType     => findStructName(st)
      case _                  => s"/* unsupported type: ${dt.typeName} */"
    }

    // For top-level fields:
    // - Nullable primitives → boxed type (java.lang.Long)
    // - Non-null primitives → Scala primitive (Long)
    // - Reference types → always reference type (String, Date, etc.)
    // - Structs → reference to generated case class
    dataType match {
      case StringType         => "String"
      case IntegerType        => if (nullable) "java.lang.Integer" else "Int"
      case LongType           => if (nullable) "java.lang.Long" else "Long"
      case ShortType          => if (nullable) "java.lang.Short" else "Short"
      case ByteType           => if (nullable) "java.lang.Byte" else "Byte"
      case DoubleType         => if (nullable) "java.lang.Double" else "Double"
      case FloatType          => if (nullable) "java.lang.Float" else "Float"
      case BooleanType        => if (nullable) "java.lang.Boolean" else "Boolean"
      case DateType           => "Date"
      case TimestampType      => "Timestamp"
      case TimestampNTZType   => "LocalDateTime"
      case BinaryType         => "Array[Byte]"
      case _: DecimalType     => "java.math.BigDecimal"
      case ArrayType(et, _)   => s"Seq[${elementType(et, "")}]"
      case MapType(kt, vt, _) => s"Map[${elementType(kt, "_key")}, ${elementType(vt, "_value")}]"
      case st: StructType     => findStructName(st)
      case _                  => s"/* unsupported type: ${dataType.typeName} */"
    }
  }

  private def readTableDescSafe(tableFQN: TableFQN): Option[(TableFQN, TableDesc)] = {
    try {
      Some(tableFQN -> CatalogManager.readTableDesc(tableFQN))
    } catch {
      case e: Exception =>
        logger.warn(s"Failed to read table ${tableFQN.fqn}: ${e.getMessage}")
        None
    }
  }

  private def collectImports(baseImports: Set[String], tableDescs: Seq[TableDesc]): Set[String] = {
    val imports = mutable.LinkedHashSet[String]() ++ baseImports
    tableDescs.foreach { desc =>
      // Add @pk import only if there are actual PK columns
      val pkColumns = desc match {
        case u: UnityTableDesc => u.pkColumns.toSet
        case _                 => Set.empty[String]
      }
      if (pkColumns.nonEmpty) imports += "ct.dna.lakehouse.core.model.Entity.pk"

      // Add @clusterby import only if there are cluster columns
      if (desc.clusterByColumns.nonEmpty) imports += "ct.dna.lakehouse.core.model.Entity.clusterby"

      desc.schema.fields.foreach { field =>
        // Add @notnull import only for non-null reference types (primitives don't need it)
        if (!field.nullable && !isPkColumn(desc, field.name) && !isScalaPrimitive(field.dataType)) {
          imports += "ct.dna.lakehouse.core.model.Entity.notnull"
        }
        addTypeImports(field.dataType, imports)
      }
    }
    imports.toSet
  }

  private val ScalaKeywords: Set[String] = Set(
    "abstract",
    "case",
    "catch",
    "class",
    "def",
    "do",
    "else",
    "extends",
    "false",
    "final",
    "finally",
    "for",
    "forSome",
    "if",
    "implicit",
    "import",
    "lazy",
    "match",
    "new",
    "null",
    "object",
    "override",
    "package",
    "private",
    "protected",
    "return",
    "sealed",
    "super",
    "this",
    "throw",
    "trait",
    "try",
    "true",
    "type",
    "val",
    "var",
    "while",
    "with",
    "yield"
  )

  private val IdentifierPattern = "^[A-Za-z_][A-Za-z0-9_]*$".r

  private def safeIdentifier(raw: String): String = {
    val sanitized = raw.replace('`', '_')
    val isValid = IdentifierPattern.pattern.matcher(sanitized).matches() && !ScalaKeywords.contains(sanitized)
    if (isValid) sanitized else s"`$sanitized`"
  }

  // ========== Print Methods (Console Output) ==========

  /** Print all generated code to console (for notebook usage). Requires Spark to be initialized.
    */
  def printForCatalog(catalogFQN: CatalogFQN): Unit = {
    val generated = generateForCatalog(catalogFQN)

    println(s"\n// ========== ${generated.catalogPackage._1} ==========\n")
    println(generated.catalogPackage._2)

    generated.schemaPackages.toSeq.sortBy(_._1.fqn).foreach { case (_, (path, content)) =>
      println(s"\n// ========== $path ==========\n")
      println(content)
    }

    generated.tableDefs.toSeq.sortBy(_._1.fqn).foreach { case (_, (path, content)) =>
      println(s"\n// ========== $path ==========\n")
      println(content)
    }
  }
}

object TableModelGenerator {

  /** Generate and write files to disk
    */
  def generateToFiles(catalogName: String, outputDir: String, basePackage: String = "ct.dna.lakehouse.model"): Unit = {
    val generator = new TableModelGenerator(basePackage)
    val catalogFQN = CatalogFQN(catalogName)
    generator.generateAndWriteForCatalog(catalogFQN, outputDir)
  }

  /** Print generated code to console (for Databricks notebook)
    */
  def printForCatalog(catalogName: String, basePackage: String = "ct.dna.lakehouse.model"): Unit = {
    val generator = new TableModelGenerator(basePackage)
    val catalogFQN = CatalogFQN(catalogName)
    generator.printForCatalog(catalogFQN)
  }

  /** Generate code for a single table and print to console
    */
  def printForTable(catalogName: String, schemaName: String, tableName: String, basePackage: String = "ct.dna.lakehouse.model"): Unit = {
    val generator = new TableModelGenerator(basePackage)
    val tableFQN = TableFQN(SchemaFQN(CatalogFQN(catalogName), schemaName), tableName)

    println(s"// Single table: $catalogName.$schemaName.$tableName\n")
    println(s"package $basePackage.$catalogName.$schemaName\n")
    println(generator.generateTablesFile(catalogName, schemaName, Seq(tableFQN)))
  }
}
