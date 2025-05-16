package ct.dna.lakehouse.framework
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import ct.dna.lakehouse.framework.UserMetadata.INGMetadata
import ct.dna.lakehouse.framework.UserMetadata.LakehouseMetadata
import ct.dna.utils.json.serializer.{TypedProductSerializer => TSR}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "clazz")
@JsonSubTypes(
  Array(
    new JsonSubTypes.Type(value = classOf[LakehouseMetadata], name = "LakehouseMetadata"),
    new JsonSubTypes.Type(value = classOf[INGMetadata], name = "INGMetadata")
  )
)
sealed trait UserMetadata {}
object UserMetadata {

  @JsonSerialize(using = classOf[TSR]) final case class LakehouseMetadata(
      someId: String,
      operation: String,
      sourceVersions: Map[String, ChangeFeedVersion],
      version: Long
  ) extends UserMetadata

  @JsonSerialize(using = classOf[TSR]) final case class INGMetadata(
      someId: String,
      operation: String
  ) extends UserMetadata

}
