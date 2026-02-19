package ct.dna.lakehouse.modelgen

import ct.dna.lakehouse.modelgen.testentities._
import ct.dna.lakehouse.core.catalog.CatalogFQN
import ct.dna.lakehouse.core.catalog.SchemaFQN
import ct.dna.lakehouse.core.catalog.TableDesc.UnityTableDesc
import ct.dna.lakehouse.core.catalog.TableFQN
import ct.dna.lakehouse.core.model.Entity
import org.apache.spark.sql.types._
import org.scalatest.funsuite.AnyFunSuite

import scala.reflect.runtime.universe._

/** Roundtrip test for TableModelGenerator.
  *
  * Tests that: Entity → StructType → TableDesc → Generated Code preserves:
  *   - Field names
  *   - Field count
  *   - \@pk annotations (from Entity.keyNames)
  *   - \@clusterby annotations (from StructType metadata)
  *   - \@notnull annotations (non-null non-pk non-primitive fields)
  *   - \@decimal annotations (non-default precision/scale)
  *
  * Uses the ACTUAL TableModelGenerator - no duplicate logic. NOTE: This test does NOT require Spark. Entity.structType uses Scala reflection only.
  *
  * For verbose output, run with: VERBOSE=true sbt "lakehouseBuild/testOnly *Roundtrip*"
  */
class TableModelGeneratorRoundtripTest extends AnyFunSuite {

  private val testCatalog = "test_catalog"
  private val testSchema = "test_schema"
  private val basePackage = "ct.dna.test.generated"

  // Set VERBOSE=true to enable detailed comparison output
  private val verbose: Boolean = sys.env.getOrElse("VERBOSE", "false").toBoolean

  // Use the ACTUAL code generator
  private val generator = new TableModelGenerator(basePackage)

  // ========== Core Roundtrip Test ==========

  /** Roundtrip test: Entity → StructType → Generated Code
    *
    * Verifies annotations and field structure match - uses actual generator, no duplicated logic.
    */
  private def verifyRoundtrip[E <: Entity: TypeTag](entityName: String): Unit = {
    val originalStructType = Entity.structType[E]
    val pkColumns = Entity.keyNames[E].toSet
    val clusterByColumns = extractClusterByFromMetadata(originalStructType)

    // Build TableDesc from original Entity
    val tableFQN = TableFQN(SchemaFQN(CatalogFQN(testCatalog), testSchema), entityName)
    val tableDesc = UnityTableDesc(
      fqn = tableFQN,
      schema = originalStructType,
      enableChangeDataFeed = true,
      timetravelDays = 35,
      pkColumns = pkColumns.toSeq,
      clusterByAuto = clusterByColumns.isEmpty,
      clusterByColumns = clusterByColumns
    )

    // Use ACTUAL TableModelGenerator to generate code
    val generatedCode = generator.generateTableCode(entityName, tableDesc)

    // Parse generated code
    val generatedFields = parseGeneratedEntityFields(generatedCode, s"Entity_$entityName")

    // Verify field count matches
    assert(
      generatedFields.size == originalStructType.fields.length,
      s"Field count mismatch: expected ${originalStructType.fields.length}, got ${generatedFields.size}\n" +
        s"Expected: ${originalStructType.fieldNames.mkString(", ")}\n" +
        s"Generated: ${generatedFields.map(_.name).mkString(", ")}"
    )

    // Verify each field
    originalStructType.fields.zip(generatedFields).foreach { case (expectedField, generatedField) =>
      // Field name
      assert(
        generatedField.name == expectedField.name,
        s"Field name mismatch: expected '${expectedField.name}', got '${generatedField.name}'"
      )

      val isPk = pkColumns.contains(expectedField.name)
      val isClusterBy = clusterByColumns.contains(expectedField.name)
      // Reuse generator's logic - primitives don't need @notnull
      val expectNotnull = !expectedField.nullable && !isPk && !generator.isScalaPrimitive(expectedField.dataType)

      // @pk annotation
      assert(
        generatedField.hasPk == isPk,
        s"Field '${expectedField.name}': @pk mismatch - expected $isPk, got ${generatedField.hasPk}"
      )

      // @clusterby annotation
      assert(
        generatedField.hasClusterBy == isClusterBy,
        s"Field '${expectedField.name}': @clusterby mismatch - expected $isClusterBy, got ${generatedField.hasClusterBy}"
      )

      // @notnull annotation
      assert(
        generatedField.hasNotnull == expectNotnull,
        s"Field '${expectedField.name}': @notnull mismatch - expected $expectNotnull, got ${generatedField.hasNotnull}"
      )

      // @decimal annotation
      val expectedDecimal = expectedField.dataType match {
        case d: DecimalType if d.precision != 38 || d.scale != 18 => Some((d.precision, d.scale))
        case _                                                    => None
      }
      assert(
        generatedField.decimalInfo == expectedDecimal,
        s"Field '${expectedField.name}': @decimal mismatch - expected $expectedDecimal, got ${generatedField.decimalInfo}"
      )

      // Verify nullability is reflected correctly in generated type:
      // - Non-null primitives should NOT have "java.lang." prefix (use Scala primitives)
      // - Nullable primitives SHOULD have "java.lang." prefix (use Java boxed types)
      expectedField.dataType match {
        case IntegerType | LongType | ShortType | ByteType | DoubleType | FloatType | BooleanType =>
          val usesBoxedType = generatedField.scalaType.startsWith("java.lang.")
          assert(
            usesBoxedType == expectedField.nullable,
            s"Field '${expectedField.name}': nullability mismatch - " +
              s"nullable=${expectedField.nullable} but generated type='${generatedField.scalaType}' " +
              s"(boxed=$usesBoxedType)"
          )
        case _ => // Reference types, collections, structs - no simple check needed
      }
    }

  }

  /** Print full comparison of original vs generated Entity (only when VERBOSE=true) */
  private def printFullComparison[E <: Entity: TypeTag](entityName: String): Unit = {
    if (!verbose) return

    val originalStructType = Entity.structType[E]
    val pkColumns = Entity.keyNames[E].toSet
    val clusterByColumns = extractClusterByFromMetadata(originalStructType)

    val tableFQN = TableFQN(SchemaFQN(CatalogFQN(testCatalog), testSchema), entityName)
    val tableDesc = UnityTableDesc(
      fqn = tableFQN,
      schema = originalStructType,
      enableChangeDataFeed = true,
      timetravelDays = 35,
      pkColumns = pkColumns.toSeq,
      clusterByAuto = clusterByColumns.isEmpty,
      clusterByColumns = clusterByColumns
    )

    val generatedCode = generator.generateTableCode(entityName, tableDesc)
    val generatedFields = parseGeneratedEntityFields(generatedCode, s"Entity_$entityName")

    println(s"\n${"=" * 80}")
    println(s"ROUNDTRIP COMPARISON: $entityName")
    println(s"${"=" * 80}")

    // Print original Entity fields
    println(s"\n--- ORIGINAL Entity (from StructType) ---")
    originalStructType.fields.foreach { field =>
      val isPk = pkColumns.contains(field.name)
      val isClusterBy = clusterByColumns.contains(field.name)
      val isNotnull = !field.nullable && !isPk && !generator.isScalaPrimitive(field.dataType)
      val decimal = field.dataType match {
        case d: DecimalType if d.precision != 38 || d.scale != 18 => s"@decimal(${d.precision}, ${d.scale}) "
        case _                                                    => ""
      }
      val annotations = Seq(
        if (isPk) "@pk" else "",
        if (isClusterBy) "@clusterby" else "",
        if (isNotnull) "@notnull" else "",
        decimal.trim
      ).filter(_.nonEmpty).mkString(" ")
      val annotStr = if (annotations.nonEmpty) s"$annotations " else ""
      println(s"    $annotStr${field.name}: ${field.dataType.simpleString} (nullable=${field.nullable})")
    }

    // Print generated Entity fields
    println(s"\n--- GENERATED Entity (from TableModelGenerator) ---")
    generatedFields.foreach { field =>
      val annotations = Seq(
        if (field.hasPk) "@pk" else "",
        if (field.hasClusterBy) "@clusterby" else "",
        if (field.hasNotnull) "@notnull" else "",
        field.decimalInfo.map { case (p, s) => s"@decimal($p, $s)" }.getOrElse("")
      ).filter(_.nonEmpty).mkString(" ")
      val annotStr = if (annotations.nonEmpty) s"$annotations " else ""
      println(s"    $annotStr${field.name}: ${field.scalaType}")
    }

    // Print field-by-field comparison
    println(s"\n--- FIELD-BY-FIELD MATCH ---")
    originalStructType.fields.zip(generatedFields).foreach { case (orig, gen) =>
      val isPk = pkColumns.contains(orig.name)
      val isClusterBy = clusterByColumns.contains(orig.name)
      // Reuse generator's logic - primitives don't need @notnull
      val expectNotnull = !orig.nullable && !isPk && !generator.isScalaPrimitive(orig.dataType)
      val expectedDecimal = orig.dataType match {
        case d: DecimalType if d.precision != 38 || d.scale != 18 => Some((d.precision, d.scale))
        case _                                                    => None
      }

      val nameMatch = orig.name == gen.name
      val pkMatch = gen.hasPk == isPk
      val clusterByMatch = gen.hasClusterBy == isClusterBy
      val notnullMatch = gen.hasNotnull == expectNotnull
      val decimalMatch = gen.decimalInfo == expectedDecimal

      val status = if (nameMatch && pkMatch && clusterByMatch && notnullMatch && decimalMatch) "✓" else "✗"
      println(s"  $status ${orig.name}: name=$nameMatch, @pk=$pkMatch, @clusterby=$clusterByMatch, @notnull=$notnullMatch, @decimal=$decimalMatch")
    }

    println(s"\n--- GENERATED CODE ---")
    println(generatedCode)
    println(s"${"=" * 80}\n")
  }

  // ========== Tests ==========

  // Basic entities
  test("roundtrip: TestEntity") { verifyRoundtrip[TestEntity]("test_entity") }
  test("roundtrip: TestEntityNoValue") { verifyRoundtrip[TestEntityNoValue]("test_entity_no_value") }

  // Primitives
  test("roundtrip: Primitives") { verifyRoundtrip[Primitives]("primitives") }
  test("roundtrip: Primitives_PK") { verifyRoundtrip[Primitives_PK]("primitives_pk") }
  // test("roundtrip: Primitives_NotNull") { verifyRoundtrip[Primitives_NotNull]("primitives_notnull") }
  // Note: Primitives_Option and Primitives_Option_NotNull are INVALID entities (Option + @pk/@notnull conflict)

  // Objects (nullable/boxed types)
  test("roundtrip: Objects") { verifyRoundtrip[Objects]("objects") }
  test("roundtrip: Objects_PK") { verifyRoundtrip[Objects_PK]("objects_pk") }
  test("roundtrip: Objects_NotNull") { verifyRoundtrip[Objects_NotNull]("objects_notnull") }

  // Decimals
  test("roundtrip: Decimals") { verifyRoundtrip[Decimals]("decimals") }

  // Collections
  test("roundtrip: Colls") { verifyRoundtrip[Colls]("colls") }
  test("roundtrip: Colls_NotNull") { verifyRoundtrip[Colls_NotNull]("colls_notnull") }

  // Nested structs
  test("roundtrip: Nested") { verifyRoundtrip[Nested]("nested") }
  test("roundtrip: Nested_NotNull") { verifyRoundtrip[Nested_NotNull]("nested_notnull") }
  test("roundtrip: NestedParent_nullable") { verifyRoundtrip[NestedParent_nullable]("nested_parent") }
  test("roundtrip: NestedParent_notnull_nullable") { verifyRoundtrip[NestedParent_notnull_nullable]("nested_parent_notnull") }
  test("roundtrip: NestedParent_notnull_notnull") { verifyRoundtrip[NestedParent_notnull_notnull]("nested_parent_notnull_notnull") }

  // Mixed annotations
  test("roundtrip: Mixed") { verifyRoundtrip[Mixed]("mixed") }

  // Binary
  test("roundtrip: BinaryData") { verifyRoundtrip[BinaryData]("binary_data") }

  // ========== Full Comparison Tests (run with: VERBOSE=true) ==========

  test("full comparison: show all entities") {
    printFullComparison[Primitives]("primitives")
    printFullComparison[Objects]("objects")
    printFullComparison[Decimals]("decimals")
    printFullComparison[Colls]("colls")
    printFullComparison[Mixed]("mixed")
  }

  // ========== Helper Methods ==========

  private def extractClusterByFromMetadata(schema: StructType): Seq[String] = {
    schema.fields
      .filter { field =>
        field.metadata.contains("@clusterby") && field.metadata.getBoolean("@clusterby")
      }
      .map(_.name)
      .toSeq
  }

  /** Parse generated Entity code to extract field definitions */
  private def parseGeneratedEntityFields(code: String, entityName: String): Seq[GeneratedField] = {
    // Extract entity body - handle nested parentheses (e.g., @decimal(15, 1))
    val entityBody = extractEntityBody(code, entityName).getOrElse {
      fail(s"Could not find @LakehouseEntity case class $entityName in generated code:\n$code")
    }

    // Split by comma, but not commas inside parentheses or brackets
    val fields = splitFieldsBalanced(entityBody)

    val fieldPattern = """^\s*((?:@\w+(?:\([^)]*\))?\s*)*)\s*(\w+):\s*(.+?)\s*$""".r

    fields
      .map(_.trim)
      .filter(_.nonEmpty)
      .map { fieldLine =>
        fieldPattern.findFirstMatchIn(fieldLine) match {
          case Some(m) =>
            GeneratedField(
              name = m.group(2),
              scalaType = m.group(3).trim,
              hasPk = m.group(1).contains("@pk"),
              hasNotnull = m.group(1).contains("@notnull"),
              hasClusterBy = m.group(1).contains("@clusterby"),
              decimalInfo = parseDecimalAnnotation(m.group(1))
            )
          case None => fail(s"Could not parse field: '$fieldLine'")
        }
      }
  }

  /** Extract entity body handling nested parentheses */
  private def extractEntityBody(code: String, entityName: String): Option[String] = {
    val marker = s"@LakehouseEntity\ncase class $entityName("
    val startIdx = code.indexOf(marker)
    if (startIdx < 0) return None

    val bodyStart = startIdx + marker.length
    var depth = 1
    var i = bodyStart
    while (i < code.length && depth > 0) {
      code.charAt(i) match {
        case '(' => depth += 1
        case ')' => depth -= 1
        case _   =>
      }
      i += 1
    }
    if (depth == 0) Some(code.substring(bodyStart, i - 1)) else None
  }

  /** Split by comma, respecting balanced parentheses and brackets */
  private def splitFieldsBalanced(s: String): Seq[String] = {
    val result = scala.collection.mutable.ListBuffer.empty[String]
    val current = new StringBuilder
    var depth = 0 // tracks () and [] depth

    for (c <- s) {
      c match {
        case '(' | '[' =>
          depth += 1
          current.append(c)
        case ')' | ']' =>
          depth -= 1
          current.append(c)
        case ',' if depth == 0 =>
          result += current.toString()
          current.clear()
        case _ =>
          current.append(c)
      }
    }
    if (current.nonEmpty) result += current.toString()
    result.toSeq
  }

  private def parseDecimalAnnotation(annotations: String): Option[(Int, Int)] = {
    """@decimal\((\d+),\s*(\d+)\)""".r.findFirstMatchIn(annotations).map { m =>
      (m.group(1).toInt, m.group(2).toInt)
    }
  }

  case class GeneratedField(
      name: String,
      scalaType: String,
      hasPk: Boolean,
      hasNotnull: Boolean,
      hasClusterBy: Boolean,
      decimalInfo: Option[(Int, Int)]
  )
}
