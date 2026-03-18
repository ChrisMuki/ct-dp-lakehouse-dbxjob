package ct.dna.lakehouse.core

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

import scala.jdk.CollectionConverters._

import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.modelbuilder.ColumnWithNameAccessorEmbeddedAstBuilder
import ct.dna.lakehouse.core.testfixtures.columnaccessor.testcat.testschema.alpha
import ct.dna.lakehouse.core.testfixtures.columnaccessor.testcat.testschema.beta
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ColumnWithNameAccessorTest extends AnyFlatSpec with Matchers {

  private val basePackage = "ct.dna.lakehouse.core.testfixtures.columnaccessor.testcat.testschema"

  private val specs: Seq[TableSpec[_ <: Entity]] = Seq(
    alpha,
    beta
  )

  "ColumnWithNameAccessor.main" should "append missing blocks and replace existing marked blocks" in {
    val baseDir = Files.createTempDirectory("column-with-name-accessor-test")

    try {
      specs.foreach { spec =>
        writeScalaFile(baseDir, spec, fileContentWithoutGeneratedBlock(spec))
      }

      val replaceSpec = beta
      writeScalaFile(baseDir, replaceSpec, fileContentWithGeneratedBlock(replaceSpec, "old generated content"))

      runMain(baseDir)

      val appendedSpec = alpha
      val appendedContent = readScalaFile(baseDir, appendedSpec)
      appendedContent should include(ColumnWithNameAccessorEmbeddedAstBuilder.StartMarker)
      appendedContent should include(ColumnWithNameAccessorEmbeddedAstBuilder.EndMarker)
      appendedContent should include(s"sealed class C_${appendedSpec.id.name}(prefix: String) extends ColumnWithNameAccessor")
      appendedContent should include(s"""object C_${appendedSpec.id.name} extends C_${appendedSpec.id.name}("")""")
      appendedContent should include("""val id: ColumnWithName = ColumnWithName(prefix + "id")""")
      appendedContent should include("""val value: ColumnWithName = ColumnWithName(prefix + "value")""")

      val replacedContent = readScalaFile(baseDir, replaceSpec)
      replacedContent should include(ColumnWithNameAccessorEmbeddedAstBuilder.StartMarker)
      replacedContent should include(ColumnWithNameAccessorEmbeddedAstBuilder.EndMarker)
      replacedContent should not include "old generated content"
      replacedContent should include(s"sealed class C_${replaceSpec.id.name}(prefix: String) extends ColumnWithNameAccessor")
      replacedContent should include("""val key: ColumnWithName = ColumnWithName(prefix + "key")""")
      replacedContent should include("""val amount: ColumnWithName = ColumnWithName(prefix + "amount")""")
    } finally {
      deleteRecursively(baseDir)
    }
  }

  it should "be idempotent when run multiple times" in {
    val baseDir = Files.createTempDirectory("column-with-name-accessor-idempotency-test")

    try {
      specs.foreach { spec =>
        writeScalaFile(baseDir, spec, fileContentWithoutGeneratedBlock(spec))
      }

      runMain(baseDir)

      val firstRunContents = specs.map(spec => spec.id.name -> readScalaFile(baseDir, spec)).toMap

      runMain(baseDir)

      val secondRunContents = specs.map(spec => spec.id.name -> readScalaFile(baseDir, spec)).toMap

      secondRunContents shouldBe firstRunContents
    } finally {
      deleteRecursively(baseDir)
    }
  }

  it should "continue processing other files and throw a summary exception at the end if one file fails" in {
    val baseDir = Files.createTempDirectory("column-with-name-accessor-error-test")

    try {
      specs.foreach { spec =>
        writeScalaFile(baseDir, spec, fileContentWithoutGeneratedBlock(spec))
      }

      val brokenSpec = beta
      writeScalaFile(baseDir, brokenSpec, fileContentWithDuplicateStartMarker(brokenSpec))

      val ex = the[RuntimeException] thrownBy {
        runMain(baseDir)
      }

      ex.getMessage shouldBe "Processing completed with 1 error(s). See logs for details."

      val successfulSpec = alpha
      val successfulContent = readScalaFile(baseDir, successfulSpec)
      successfulContent should include(ColumnWithNameAccessorEmbeddedAstBuilder.StartMarker)
      successfulContent should include(s"sealed class C_${successfulSpec.id.name}(prefix: String) extends ColumnWithNameAccessor")

      val stillBrokenContent = readScalaFile(baseDir, brokenSpec)
      stillBrokenContent should include("broken generated content A")
      stillBrokenContent should include("broken generated content B")
    } finally {
      deleteRecursively(baseDir)
    }
  }

  private def runMain(baseDir: Path): Unit = {
    ColumnWithNameAccessor.main(
      Array(
        s"baseDir=$baseDir",
        s"basePackage=$basePackage"
      )
    )
  }

  private def writeScalaFile(baseDir: Path, tableSpec: TableSpec[_ <: Entity], content: String): Path = {
    val filePath = resolveFilePath(baseDir, tableSpec)
    Files.createDirectories(filePath.getParent)
    Files.write(filePath, content.getBytes(StandardCharsets.UTF_8))
    filePath
  }

  private def readScalaFile(baseDir: Path, tableSpec: TableSpec[_ <: Entity]): String = {
    new String(Files.readAllBytes(resolveFilePath(baseDir, tableSpec)), StandardCharsets.UTF_8)
  }

  private def resolveFilePath(baseDir: Path, tableSpec: TableSpec[_ <: Entity]): Path = {
    val packagePath = tableSpec.getClass.getPackage.getName.split('.').foldLeft(baseDir) { (acc, part) =>
      acc.resolve(part)
    }
    packagePath.resolve(s"${tableSpec.id.name}.scala")
  }

  private def fileContentWithoutGeneratedBlock(tableSpec: TableSpec[_ <: Entity]): String = {
    val packageName = tableSpec.getClass.getPackage.getName
    val objectName = tableSpec.id.name

    s"""package $packageName
       |
       |object $objectName {
       |  val keepMe = 1
       |}
       |""".stripMargin
  }

  private def fileContentWithGeneratedBlock(tableSpec: TableSpec[_ <: Entity], generatedContent: String): String = {
    val packageName = tableSpec.getClass.getPackage.getName
    val objectName = tableSpec.id.name

    s"""package $packageName
       |
       |object $objectName {
       |  val keepMe = 1
       |}
       |
       |${ColumnWithNameAccessorEmbeddedAstBuilder.StartMarker}
       |$generatedContent
       |${ColumnWithNameAccessorEmbeddedAstBuilder.EndMarker}
       |""".stripMargin
  }

  private def fileContentWithDuplicateStartMarker(tableSpec: TableSpec[_ <: Entity]): String = {
    val packageName = tableSpec.getClass.getPackage.getName
    val objectName = tableSpec.id.name

    s"""package $packageName
       |
       |object $objectName {
       |  val keepMe = 1
       |}
       |
       |${ColumnWithNameAccessorEmbeddedAstBuilder.StartMarker}
       |broken generated content A
       |${ColumnWithNameAccessorEmbeddedAstBuilder.StartMarker}
       |broken generated content B
       |${ColumnWithNameAccessorEmbeddedAstBuilder.EndMarker}
       |""".stripMargin
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
