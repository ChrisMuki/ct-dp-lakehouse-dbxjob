package ct.dna.lakehouse.cicd.models

import java.nio.file.Files
import java.nio.file.Paths

import com.fasterxml.jackson.annotation.JsonInclude
import ct.dna.utils.LocalDir
import ct.dna.utils.deploy.databrickscli.assetbundle.AssetBundle

trait AsFile {
  val fileName: String
  def content: Array[Byte]

  def writeToFolder(folderDir: LocalDir): Unit =
    Files.write(Paths.get(folderDir.getAbsolutePath, fileName), content)
}

object AsFile {

  // Clone the shared YAML mapper and configure NON_ABSENT so that Option[T]=None
  // and null fields (e.g. Job.schedule) are omitted from the output.
  private val yamlMapper =
    ct.dna.utils.deploy.yaml.mapper
      .cloneRaw()
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

  implicit class AssetBundleAsFile(ab: AssetBundle) extends AsFile {
    val fileName: String = "databricks.yml"
    def content: Array[Byte] = yamlMapper.writeValueAsBytes(ab)
  }
}
