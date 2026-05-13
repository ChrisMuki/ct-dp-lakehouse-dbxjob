package ct.dna.lakehouse.cicd.models

import java.nio.file.Files
import java.nio.file.Paths

import ct.dna.utils.LocalDir

trait AsFile {
  val fileName: String
  def content: Array[Byte]

  def writeToFolder(folderDir: LocalDir): Unit =
    Files.write(Paths.get(folderDir.getAbsolutePath, fileName), content)
}
