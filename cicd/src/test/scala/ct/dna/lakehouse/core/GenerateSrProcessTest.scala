package ct.dna.lakehouse.core

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

import scala.jdk.CollectionConverters._

import ct.dna.lakehouse.core.jobs.{ColumnDefinition, ColumnSource, SrRawFieldInfo}
import ct.dna.lakehouse.core.modelbuilder.ChangeKeyTableSpecAstBuilder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GenerateSrProcessTest extends AnyFlatSpec with Matchers {

  "GenerateSr.main" should "handle null descriptions and long numeric metadata and generate core imports" in {
    val tempRoot = Files.createTempDirectory("generate-sr-process-test")

    try {
      val srOutDir = tempRoot.resolve("out/sr")
      val srRawBaseDir = tempRoot.resolve("out/sr_raw")
      val schemaDir = srRawBaseDir.resolve("ct_gbl_test")
      Files.createDirectories(schemaDir)

      val srRawTableFile = schemaDir.resolve("mara.scala")
      Files.writeString(
        srRawTableFile,
        """package ct.dna.lakehouse.sr_raw.ct_gbl_test
          |
          |import ct.dna.lakehouse.core.framework.origin.Loaded
          |import ct.dna.lakehouse.core.model.Entity
          |import ct.dna.lakehouse.core.model.Entity._
          |import ct.dna.lakehouse.core.model.TableSpec
          |
          |case class E_mara(
          |    @PK _mk_system: String,
          |    @PK _mk_instance: String,
          |    @NotNull _mk_created_at: Timestamp,
          |    @PK _lh_id_in_message: Long,
          |    mandt_string: String,
          |    tsval_decimal_14_0: java.math.BigDecimal
          |) extends Entity
          |
          |object mara extends TableSpec[E_mara] with Loaded
          |""".stripMargin,
        StandardCharsets.UTF_8
      )

      val srTableDefPath = tempRoot.resolve("sr_table_def.json")
      Files.writeString(
        srTableDefPath,
        """{
          |  "schema": {
          |    "CT_GBL_TEST": {
          |      "MARA": {
          |        "columns": [
          |          {
          |            "name": "MANDT",
          |            "description": null,
          |            "type": "StringLengthMax",
          |            "length": 3,
          |            "decimalsCount": 0,
          |            "isPrimaryKey": true
          |          },
          |          {
          |            "name": "TSVAL",
          |            "description": null,
          |            "type": "Decimal",
          |            "length": 14,
          |            "decimalsCount": 0,
          |            "isPrimaryKey": false
          |          }
          |        ]
          |      }
          |    }
          |  }
          |}
          |""".stripMargin,
        StandardCharsets.UTF_8
      )

      GenerateSr.main(
        Array(
          s"baseDir=$srOutDir",
          "basePackage=ct.dna.lakehouse.sr",
          s"srTableDefPath=$srTableDefPath",
          s"srRawBaseDir=$srRawBaseDir",
          "filterSchema=CT_GBL_TEST",
          "filterTable=MARA"
        )
      )

      val generated = srOutDir.resolve("ct_gbl_test/mara.scala")
      Files.exists(generated) shouldBe true

      val generatedContent = Files.readString(generated, StandardCharsets.UTF_8)
      generatedContent should include("import ct.dna.lakehouse.core.framework.origin.ChangeKey")
      generatedContent should include("import ct.dna.lakehouse.core.model.{Entity, TableSpec}")
      generatedContent should include("import org.apache.spark.sql.functions.{col, struct}")
      generatedContent should not include "ct.dna.lakehouse.sr.core"
      generatedContent should include("@NotNull _mk_system: String")
      generatedContent should include("@NotNull _mk_instance: String")
      generatedContent should include("@NotNull _mk_created_at: Timestamp")
      generatedContent should include("_lh_id_in_message: Long")
      generatedContent should include("mandt: String")
      generatedContent should include("@Decimal(15, 0)")
      generatedContent should include("(\"_mk_created_at\", col(\"_mk_created_at\"))")
      generatedContent should include("(\"_lh_id_in_message\", col(\"_lh_id_in_message\"))")
    } finally {
      deleteRecursively(tempRoot)
    }
  }

  "ChangeKeyTableSpecAstBuilder.build" should "omit hex import when hex is not used" in {
    val ast = ChangeKeyTableSpecAstBuilder.build(
      rootPackage = "ct.dna.lakehouse.sr",
      srRawRootPackage = "ct.dna.lakehouse.sr_raw",
      schemaName = "CT_GBL_TEST",
      tableName = "MARA",
      columnSource = ColumnSource.Theobald(
        Seq(
          ColumnDefinition(
            name = "MANDT",
            description = None,
            columnType = "StringLengthMax",
            length = Some(3L),
            decimalsCount = Some(0L),
            isPrimaryKey = true
          )
        )
      ),
      srRawFields = Seq(SrRawFieldInfo("mandt_string", "String", isPrimaryKey = false, isNotNull = false)),
      rawSourceType = "E_mara"
    )

    ast.imports should contain("org.apache.spark.sql.functions.{col, struct}")
    ast.imports.exists(_.contains("hex")) shouldBe false
  }

  it should "include hex import when ByteArrayLengthExact mapping requires hex" in {
    val ast = ChangeKeyTableSpecAstBuilder.build(
      rootPackage = "ct.dna.lakehouse.sr",
      srRawRootPackage = "ct.dna.lakehouse.sr_raw",
      schemaName = "CT_GBL_TEST",
      tableName = "MARA",
      columnSource = ColumnSource.Theobald(
        Seq(
          ColumnDefinition(
            name = "PAYLOAD",
            description = None,
            columnType = "ByteArrayLengthExact",
            length = Some(16L),
            decimalsCount = Some(0L),
            isPrimaryKey = false
          )
        )
      ),
      srRawFields = Seq(SrRawFieldInfo("payload_binary", "Array[Byte]", isPrimaryKey = false, isNotNull = false)),
      rawSourceType = "E_mara"
    )

    ast.imports should contain("org.apache.spark.sql.functions.{col, hex, struct}")
  }

  it should "retain non-Theobald sr_raw fields and downgrade sr_raw PK metadata to not-null" in {
    val ast = ChangeKeyTableSpecAstBuilder.build(
      rootPackage = "ct.dna.lakehouse.sr",
      srRawRootPackage = "ct.dna.lakehouse.sr_raw",
      schemaName = "CT_GBL_TEST",
      tableName = "MARA",
      columnSource = ColumnSource.Theobald(
        Seq(
          ColumnDefinition(
            name = "MANDT",
            description = None,
            columnType = "StringLengthMax",
            length = Some(3L),
            decimalsCount = Some(0L),
            isPrimaryKey = true
          )
        )
      ),
      srRawFields = Seq(
        SrRawFieldInfo("_mk_system", "String", isPrimaryKey = true, isNotNull = false),
        SrRawFieldInfo("_lh_id_in_message", "Long", isPrimaryKey = true, isNotNull = false),
        SrRawFieldInfo("mandt_string", "String", isPrimaryKey = false, isNotNull = false)
      ),
      rawSourceType = "E_mara"
    )

    val rendered = ast.render
    rendered should include("@NotNull _mk_system: String")
    rendered should include("_lh_id_in_message: Long")
    rendered should include("mandt: String")
    rendered should include("(\"_mk_system\", col(\"_mk_system\"))")
    rendered should include("(\"_lh_id_in_message\", col(\"_lh_id_in_message\"))")
  }

  private def deleteRecursively(path: Path): Unit = {
    if (Files.exists(path)) {
      Files
        .walk(path)
        .iterator()
        .asScala
        .toSeq
        .sortBy(_.toString.length)
        .reverse
        .foreach(Files.deleteIfExists)
    }
  }
}
