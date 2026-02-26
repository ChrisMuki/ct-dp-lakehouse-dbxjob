package ct.dna.lakehouse.modelgen

import ct.dna.lakehouse.core.catalog.CatalogFQN
import ct.dna.lakehouse.core.catalog.SchemaFQN
import ct.dna.lakehouse.core.catalog.TableDesc.UnityTableDesc
import ct.dna.lakehouse.core.catalog.TableFQN
import org.apache.spark.sql.types._
import org.scalatest.funsuite.AnyFunSuite

class TableModelGeneratorTest extends AnyFunSuite {

  test("sparkTypeToScala should convert basic Spark types correctly") {
    // Matches lakehouseCore convention:
    // - Non-null primitives → Scala primitives (Int, Long, etc.)
    // - Nullable primitives → Java boxed types (java.lang.Integer, java.lang.Long, etc.)
    val generator = new TableModelGenerator("ct.dna.test.model")

    // Non-null primitives → Scala primitives
    assert(generator.sparkTypeToScala(IntegerType, nullable = false) == "Int")
    assert(generator.sparkTypeToScala(LongType, nullable = false) == "Long")
    assert(generator.sparkTypeToScala(BooleanType, nullable = false) == "Boolean")

    // Nullable primitives → Java boxed types (NOT Option)
    assert(generator.sparkTypeToScala(IntegerType, nullable = true) == "java.lang.Integer")
    assert(generator.sparkTypeToScala(LongType, nullable = true) == "java.lang.Long")
    assert(generator.sparkTypeToScala(DoubleType, nullable = true) == "java.lang.Double")
    assert(generator.sparkTypeToScala(BooleanType, nullable = true) == "java.lang.Boolean")

    // Reference types (always same type regardless of nullable)
    assert(generator.sparkTypeToScala(StringType, nullable = true) == "String")
    assert(generator.sparkTypeToScala(StringType, nullable = false) == "String")
    assert(generator.sparkTypeToScala(DateType, nullable = true) == "Date")
    assert(generator.sparkTypeToScala(TimestampType, nullable = true) == "Timestamp")
    assert(generator.sparkTypeToScala(BinaryType, nullable = true) == "Array[Byte]")
    assert(generator.sparkTypeToScala(DecimalType(10, 2), nullable = true) == "java.math.BigDecimal")
  }

  test("sparkTypeToScala should handle complex types") {
    val generator = new TableModelGenerator("ct.dna.test.model")

    // Collection types are reference types and don't need Option wrapping
    // (they can hold null directly, like String)
    assert(generator.sparkTypeToScala(ArrayType(StringType), nullable = true) == "Seq[String]")
    assert(generator.sparkTypeToScala(ArrayType(StringType), nullable = false) == "Seq[String]")
    assert(generator.sparkTypeToScala(MapType(StringType, IntegerType), nullable = true) == "Map[String, java.lang.Integer]")
    assert(generator.sparkTypeToScala(MapType(StringType, IntegerType), nullable = false) == "Map[String, java.lang.Integer]")
    // Nested collections
    assert(generator.sparkTypeToScala(ArrayType(ArrayType(IntegerType)), nullable = true) == "Seq[Seq[java.lang.Integer]]")
  }

  test("generateCatalogPackage should produce valid Scala code") {
    val generator = new TableModelGenerator("ct.dna.test.model")
    val code = generator.generateCatalogPackage("my_catalog")

    assert(code.contains("package ct.dna.test.model"))
    assert(code.contains("import ct.dna.lakehouse.core.model.CatalogSpec"))
    assert(code.contains("package object my_catalog extends CatalogSpec"))
  }

  test("generateSchemaPackage should produce valid Scala code") {
    val generator = new TableModelGenerator("ct.dna.test.model")
    val code = generator.generateSchemaPackage("my_catalog", "my_schema")

    assert(code.contains("package ct.dna.test.model.my_catalog"))
    assert(code.contains("import ct.dna.lakehouse.core.model.SchemaSpec"))
    assert(code.contains("package object my_schema extends SchemaSpec"))
  }

  test("getDecimalAnnotation should generate annotation for non-default precision/scale") {
    val generator = new TableModelGenerator("ct.dna.test.model")

    // Default precision/scale (38, 18) should not generate annotation
    assert(generator.getDecimalAnnotation(DecimalType(38, 18)) == "")
    assert(generator.getDecimalAnnotation(StringType) == "")
    assert(generator.getDecimalAnnotation(IntegerType) == "")

    // Non-default precision/scale should generate annotation
    assert(generator.getDecimalAnnotation(DecimalType(10, 2)) == "@decimal(10, 2) ")
    assert(generator.getDecimalAnnotation(DecimalType(15, 5)) == "@decimal(15, 5) ")
    assert(generator.getDecimalAnnotation(DecimalType(38, 10)) == "@decimal(38, 10) ")
    assert(generator.getDecimalAnnotation(DecimalType(20, 18)) == "@decimal(20, 18) ")
  }

  test("generateTableCode should use Joined for wide entities") {
    val generator = new TableModelGenerator("ct.dna.test.model")
    val tableFQN = TableFQN(SchemaFQN(CatalogFQN("test_catalog"), "test_schema"), "wide_table")
    val fields = (1 to 130).map { index =>
      StructField(s"col_$index", LongType, nullable = false)
    }
    val tableDesc = UnityTableDesc(
      fqn = tableFQN,
      schema = StructType(fields),
      enableChangeDataFeed = true,
      timetravelDays = 35,
      pkColumns = Seq.empty,
      clusterByAuto = true,
      clusterByColumns = Seq.empty
    )

    val code = generator.generateTableCode("wide_table", tableDesc)
    assert(code.contains("object wide_table extends TableSpec[Joined["))
    assert(code.contains("@LakehouseEntity\ncase class Entity_wide_table_Part1"))
    assert(!code.contains("case class Entity_wide_table("))
  }
}
