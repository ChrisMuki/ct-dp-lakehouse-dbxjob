package ct.dna.lakehouse.cicd.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import ct.dna.lakehouse.core.runtime.SparkConfig
import ct.dna.utils.json.mapper

final case class ConfigFile(
    rootDir: String,
    sparkConfig: SparkConfig
) extends AsFile {
  @JsonIgnore val fileName: String = "config.json"

  def content: Array[Byte] = {
    val m = mapper
      .cloneRaw()
      .enable(SerializationFeature.INDENT_OUTPUT)
    val tree = m.valueToTree[ObjectNode](this)
    // Remove 'types' — a computed field from the DBXJobConfig trait that
    // contains abstract CatalogType references and cannot be deserialized.
    val scNode = tree.path("sparkConfig")
    if (scNode.isObject) scNode.asInstanceOf[ObjectNode].remove("types")
    m.writeValueAsBytes(tree)
  }
}
