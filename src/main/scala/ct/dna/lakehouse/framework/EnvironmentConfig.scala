package ct.dna.lakehouse.framework

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.framework.EnvironmentConfig.Lakehouse
import ct.dna.lakehouse.framework.EnvironmentConfig.RemoteSandbox
import ct.dna.lakehouse.framework.EnvironmentConfig.Sandbox
import ct.dna.lakehouse.framework.EnvironmentConfig.Staging
import ct.dna.utils.json.serializer.{TypedProductSerializer => TPS}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "clazz")
@JsonSubTypes(
  Array(
    // new JsonSubTypes.Type(value = classOf[Sandbox], name = "Sandbox"),
    new JsonSubTypes.Type(value = classOf[Lakehouse], name = "Lakehouse"),
    new JsonSubTypes.Type(value = classOf[Staging], name = "Staging"),
    new JsonSubTypes.Type(value = classOf[Sandbox], name = "Sandbox"),
    new JsonSubTypes.Type(value = classOf[RemoteSandbox], name = "RemoteSandbox")
  )
)
sealed trait EnvironmentConfig
object EnvironmentConfig {
  @JsonSerialize(using = classOf[TPS]) final case class RemoteSandbox(stage: String, uid: String, workspaceUrl: String, clusterId: String, pat: String)
      extends EnvironmentConfig
  @JsonSerialize(using = classOf[TPS]) final case class Sandbox(stage: String, uid: String) extends EnvironmentConfig
  @JsonSerialize(using = classOf[TPS]) final case class Staging(stage: String) extends EnvironmentConfig
  @JsonSerialize(using = classOf[TPS]) final case class Lakehouse(stage: String) extends EnvironmentConfig
}
