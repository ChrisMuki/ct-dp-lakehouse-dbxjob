package ct.dna.lakehouse.core.modelbuilder

import ct.dna.lakehouse.core.jobs.{ColumnDefinition, ColumnSource, SrRawFieldInfo}
import ct.dna.dataplatform.NamingConvention

import scala.collection.mutable

object ChangeKeyTableSpecAstBuilder {

  private val MaxCaseClassFields = 254

  // ---------- Inner model types ----------

  private case class SrField(
      fieldName: String,
      scalaType: String,
      colExpr: String,
      isPrimaryKey: Boolean,
      annotation: Option[String]
  )

  // ---------- AST case classes ----------

  /** AST node for the `object <table> extends TableSpec[T] with ChangeKey[R] { ... }` block. */
  final case class ChangeKeyObjectAst(
      name: String,
      entityType: String,
      rawSourceType: String,
      srRawObjectAlias: String,
      sequenceByExpr: String,
      preApplyMapping: Seq[(String, String)]
  ) {

    def render: String = {
      val sb = new StringBuilder()
      sb.append(s"object $name extends TableSpec[$entityType] with ChangeKey[$rawSourceType] {\n\n")
      sb.append(s"  override def sourceTableSpec: TableSpec[$rawSourceType] = $srRawObjectAlias\n\n")
      sb.append(s"  def sequenceBy: Column = $sequenceByExpr\n\n")
      sb.append("  lazy val preApplyMapping: Seq[(String, Column)] = Seq(\n")
      sb.append(preApplyMapping.map { case (name, expr) => s"""    ("$name", $expr)""" }.mkString(",\n"))
      sb.append("\n  )\n}")
      sb.toString()
    }
  }

  /** AST for a complete sr/<schema>/<table>.scala file. */
  final case class ChangeKeyTableSpecWithEntityAst(
      packageName: String,
      imports: List[String],
      entityCode: String,
      tableObject: ChangeKeyObjectAst
  ) {

    def render: String = {
      val importCode = if (imports.isEmpty) "" else imports.sorted.distinct.map(i => s"import $i").mkString("\n") + "\n"
      s"""// This file is auto-generated. Do not edit manually.
package $packageName

$importCode
$entityCode
${tableObject.render}
"""
    }
  }

  // ---------- Public build ----------

  def build(
      rootPackage: String,
      srRawRootPackage: String,
      schemaName: String,
      tableName: String,
      columnSource: ColumnSource,
      srRawFields: Seq[SrRawFieldInfo],
      rawSourceType: String
  ): ChangeKeyTableSpecWithEntityAst = {

    val schemaPackage = schemaName.toLowerCase
    val tl = tableName.toLowerCase
    val packageName = s"$rootPackage.$schemaPackage"
    val entName = s"Sr${tableName.head.toUpper}${tableName.tail.toLowerCase}"

    val configuredColumns = columnSource.columns
    val srRawFieldNames = srRawFields.map(_.name).toSet
    val configuredBySourceField = configuredColumns.flatMap { column =>
      resolveSourceFieldName(column, srRawFieldNames).map(_ -> column)
    }.toMap
    val allFields = srRawFields.map { srRawField =>
      configuredBySourceField.get(srRawField.name) match {
        case Some(column) => toConfiguredSrField(column, srRawFieldNames)
        case None         => toPassthroughSrField(srRawField)
      }
    }

    val needsJoined = totalParamWeight(allFields) > MaxCaseClassFields
    val needsDecimal = allFields.exists(_.annotation.exists(_.startsWith("@Decimal")))

    val entityCode = if (needsJoined) {
      generateWideEntityCode(entName, allFields)
    } else {
      generateEntityCode(entName, allFields)
    }

    val targetEntityType = joinedEntityType(entName, allFields)
    val fqRawSourceType = fullyQualifiedRawType(rawSourceType, srRawRootPackage, schemaPackage)

    val mappingEntries = allFields.map(field => field.fieldName -> field.colExpr)

    val tableObject = ChangeKeyObjectAst(
      name = tl,
      entityType = targetEntityType,
      rawSourceType = fqRawSourceType,
      srRawObjectAlias = s"sr_raw_$tl",
      sequenceByExpr = """struct(col("_mk_created_at"), col("_lh_id_in_message"))""",
      preApplyMapping = mappingEntries.toSeq
    )

    val sparkFunctionsImport = {
      val usesHex = mappingEntries.exists { case (_, expr) => expr.contains("hex(") }
      val usesCoalesce = mappingEntries.exists { case (_, expr) => expr.contains("coalesce(") }
      val extras = Seq(
        if (usesCoalesce) Seq("coalesce", "lit") else Seq.empty,
        if (usesHex) Seq("hex") else Seq.empty
      ).flatten
      val fns = (Seq("col") ++ extras ++ Seq("struct")).sorted.mkString(", ")
      s"org.apache.spark.sql.functions.{$fns}"
    }

    val entityImportParts = Seq(
      if (needsJoined) Some("Joined") else None,
      if (allFields.exists(_.isPrimaryKey)) Some("PK") else None,
      if (allFields.exists(_.annotation.contains("@NotNull"))) Some("NotNull") else None,
      if (needsDecimal) Some("Decimal") else None
    ).flatten
    val corePackageRoot = rootPackage.stripSuffix(".sr")

    val imports = List(
      s"$corePackageRoot.core.framework.origin.ChangeKey",
      s"$corePackageRoot.core.model.{Entity, TableSpec}",
      s"$corePackageRoot.core.model.Entity.{${entityImportParts.mkString(", ")}}",
      s"$srRawRootPackage.$schemaPackage.{$tl => sr_raw_$tl}",
      "org.apache.spark.sql.Column",
      sparkFunctionsImport
    )

    ChangeKeyTableSpecWithEntityAst(
      packageName = packageName,
      imports = imports,
      entityCode = entityCode,
      tableObject = tableObject
    )
  }

  // ---------- Type mapping (configured source column → SR field + source expression) ----------

  private def resolveSourceFieldName(col: ColumnDefinition, srRawFieldNames: Set[String]): Option[String] = {
    val baseName = col.name.toLowerCase.replaceAll("_+$", "")
    val candidateNames = col.columnType match {
      case "Date" =>
        if (srRawFieldNames.isEmpty || srRawFieldNames.contains(s"${baseName}_string")) Seq(s"${baseName}_string", s"${baseName}_date")
        else Seq(s"${baseName}_date", s"${baseName}_string")
      case "StringLengthMax" | "NumericString" | "Time" | "ByteArray" =>
        Seq(s"${baseName}_string")
      case "ByteArrayLengthExact" =>
        if (srRawFieldNames.isEmpty || srRawFieldNames.contains(s"${baseName}_string")) Seq(s"${baseName}_string", s"${baseName}_binary")
        else Seq(s"${baseName}_binary", s"${baseName}_string")
      case "Short" | "Byte" | "Int" | "Integer" =>
        Seq(s"${baseName}_int")
      case "Long" | "BigInteger" =>
        Seq(s"${baseName}_long")
      case "Float" =>
        Seq(s"${baseName}_float")
      case "Double" =>
        Seq(s"${baseName}_double")
      case "Boolean" =>
        Seq(s"${baseName}_boolean")
      case "Decimal" =>
        val len = col.length.map(_.toInt).getOrElse(13)
        val dec = col.decimalsCount.map(_.toInt).getOrElse(2)
        Seq(s"${baseName}_decimal_${len}_${dec}")
      case _ =>
        Seq(s"${baseName}_string")
    }

    candidateNames.find(srRawFieldNames.contains).orElse(candidateNames.headOption)
  }

  private def typeMapping(col: ColumnDefinition, sourceFieldName: String): (String, String) = col.columnType match {
    case "Date" =>
      if (sourceFieldName.endsWith("_date"))
        ("String", s"""col("$sourceFieldName").cast("string")""")
      else
        ("String", s"""col("$sourceFieldName")""")
    case "StringLengthMax" | "NumericString" | "Time" | "ByteArray" =>
      ("String", s"""col("$sourceFieldName")""")
    case "ByteArrayLengthExact" =>
      if (sourceFieldName.endsWith("_binary"))
        ("String", s"""hex(col("$sourceFieldName"))""")
      else
        ("String", s"""col("$sourceFieldName")""")
    case "Short" | "Byte" =>
      ("Int", s"""col("$sourceFieldName")""")
    case "Decimal" =>
      ("java.math.BigDecimal", s"""col("$sourceFieldName")""")
    case "Int" | "Integer" =>
      ("Int", s"""col("$sourceFieldName")""")
    case "Long" | "BigInteger" =>
      ("Long", s"""col("$sourceFieldName")""")
    case "Float" =>
      ("Float", s"""col("$sourceFieldName")""")
    case "Double" =>
      ("Double", s"""col("$sourceFieldName")""")
    case "Boolean" =>
      ("Boolean", s"""col("$sourceFieldName")""")
    case other =>
      System.err.println(s"  [WARN] Unknown SAP type '$other' for column ${col.name} — defaulting to String")
      ("String", s"""col("$sourceFieldName")""")
  }

  private def safeFieldName(raw: String): String = {
    val name = NamingConvention.SourceDataColumns.enforce(raw)
    name.replaceAll("_+$", "")
  }

  private def toConfiguredSrField(col: ColumnDefinition, srRawFieldNames: Set[String]): SrField = {
    val sourceFieldName = resolveSourceFieldName(col, srRawFieldNames).getOrElse(s"${col.name.toLowerCase}_string")
    val (scalaType, colExpr) = typeMapping(col, sourceFieldName)
    val annotation = col.columnType match {
      case "Decimal" =>
        val rawLen = col.length.map(_.toInt).getOrElse(13)
        val len = if (rawLen == 14) 15 else rawLen
        val dec = col.decimalsCount.map(_.toInt).getOrElse(2)
        Some(s"@Decimal($len, $dec)")
      case _ => None
    }
    val isPK = col.isPrimaryKey && col.name != "MANDT"
    val safeColExpr = scalaType match {
      case "String" if isPK   => s"""coalesce($colExpr, lit(""))"""
      case "Int" | "Long"     => s"""coalesce($colExpr, lit(0))"""
      case "Float" | "Double" => s"""coalesce($colExpr, lit(0.0))"""
      case "Boolean"          => s"""coalesce($colExpr, lit(false))"""
      case _                  => colExpr
    }
    SrField(
      fieldName = safeFieldName(col.name),
      scalaType = scalaType,
      colExpr = safeColExpr,
      isPrimaryKey = isPK,
      annotation = annotation
    )
  }

  private def toPassthroughSrField(srRawField: SrRawFieldInfo): SrField = {
    val annotation = srRawField.decimalAnnotation.orElse {
      if (supportsNotNullAnnotation(srRawField.scalaType) && (srRawField.isNotNull || srRawField.isPrimaryKey)) Some("@NotNull")
      else None
    }

    SrField(
      fieldName = srRawField.name,
      scalaType = srRawField.scalaType,
      colExpr = s"""col("${srRawField.name}")""",
      isPrimaryKey = false,
      annotation = annotation
    )
  }

  private def supportsNotNullAnnotation(scalaType: String): Boolean = scalaType match {
    case "Int" | "Long" | "Double" | "Float" | "Boolean" | "Byte" | "Short" => false
    case _                                                                  => true
  }

  // ---------- Entity code generation ----------

  private def generateEntityCode(entityName: String, fields: Seq[SrField]): String = {
    val sb = new StringBuilder()
    sb.append(s"case class $entityName(\n")
    sb.append(fields.map(generateFieldLine).mkString(",\n"))
    sb.append("\n) extends Entity\n")
    sb.toString()
  }

  private def generateFieldLine(field: SrField): String = {
    val pkAnnotation = if (field.isPrimaryKey) "@PK " else ""
    val extraAnnotation = field.annotation.map(_ + " ").getOrElse("")
    s"    $pkAnnotation$extraAnnotation${field.fieldName}: ${field.scalaType}"
  }

  private def generateWideEntityCode(entityName: String, fields: Seq[SrField]): String = {
    val pkFields = fields.filter(_.isPrimaryKey)
    val valueFields = fields.filterNot(_.isPrimaryKey)
    val pkWeight = totalParamWeight(pkFields)
    val maxValueWeight = MaxCaseClassFields - pkWeight

    require(maxValueWeight > 0, s"Wide SR entity $entityName has PK weight $pkWeight which exceeds $MaxCaseClassFields")

    val valueParts = splitByParamWeight(valueFields, maxValueWeight)
    val sb = new StringBuilder()

    valueParts.zipWithIndex.foreach { case (partFields, index) =>
      sb.append(generateEntityCode(s"${entityName}_Part${index + 1}", pkFields ++ partFields))
      sb.append("\n")
    }

    sb.toString()
  }

  private def joinedEntityType(entityName: String, fields: Seq[SrField]): String = {
    if (totalParamWeight(fields) <= MaxCaseClassFields) entityName
    else {
      val pkFields = fields.filter(_.isPrimaryKey)
      val valueFields = fields.filterNot(_.isPrimaryKey)
      val maxValueWeight = MaxCaseClassFields - totalParamWeight(pkFields)
      val partCount = splitByParamWeight(valueFields, maxValueWeight).size
      (1 to partCount).map(i => s"${entityName}_Part$i").reduceLeft((l, r) => s"Joined[$l, $r]")
    }
  }

  // ---------- Helpers ----------

  private def fieldParamWeight(field: SrField): Int = field.scalaType match {
    case "Long" | "Double" => 2
    case _                 => 1
  }

  private def totalParamWeight(fields: Seq[SrField]): Int =
    fields.map(fieldParamWeight).sum

  private def splitByParamWeight(fields: Seq[SrField], maxWeight: Int): Seq[Seq[SrField]] = {
    val parts = mutable.ArrayBuffer.empty[mutable.ArrayBuffer[SrField]]
    var current = mutable.ArrayBuffer.empty[SrField]
    var currentWeight = 0

    fields.foreach { field =>
      val fieldWeight = fieldParamWeight(field)
      if (current.nonEmpty && currentWeight + fieldWeight > maxWeight) {
        parts += current
        current = mutable.ArrayBuffer.empty[SrField]
        currentWeight = 0
      }
      current += field
      currentWeight += fieldWeight
    }

    if (current.nonEmpty) parts += current
    parts.map(_.toSeq).toSeq
  }

  private def fullyQualifiedRawType(rawType: String, srRawRootPackage: String, schemaPackage: String): String =
    rawType
      .replaceAll("""\bJoined\b""", "ct.dna.lakehouse.core.model.Entity.Joined")
      .replaceAll("""\b(E_[A-Za-z0-9_]+)\b""", s"$srRawRootPackage.$schemaPackage.$$1")
}
