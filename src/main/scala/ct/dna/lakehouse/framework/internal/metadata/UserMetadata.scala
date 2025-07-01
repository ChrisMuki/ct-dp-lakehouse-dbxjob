package ct.dna.lakehouse.framework.internal.metadata

import ct.dna.lakehouse.dataframeprovider.ChangeFeedTable
import ct.dna.lakehouse.dataframeprovider.Commit
import ct.dna.lakehouse.dataframeprovider.TargetTable
import ct.dna.utils.json.mapper

private[internal] sealed trait UserMetadata {
  def format: String
}
private[internal] object UserMetadata {
  def parse(u: String): UserMetadata = u match {
    case MERGE.reg(json)            => mapper.readValue[MERGE](json)
    case INGEST.reg(json)           => mapper.readValue[INGEST](json)
    case OPTIMIZE.prefix            => OPTIMIZE
    case STRUCTURE_CHANGE.reg(json) => mapper.readValue[STRUCTURE_CHANGE](json)
  }

  object OPTIMIZE extends UserMetadata { val prefix: String = "OPTIMIZE"; def format: String = "OPTIMIZE" }

  final case class INGEST(id: String) extends UserMetadata { def format: String = INGEST.prefix + mapper.writeValueAsString(this) }
  object INGEST { val prefix = "INGEST"; val reg = s"$prefix(.*)".r }

  final case class STRUCTURE_CHANGE(
      id: String,
      changeFeedVersions: Map[String, ChangeFeedTable.Version],
      targetVersion: TargetTable.Version
  ) extends UserMetadata { def format: String = STRUCTURE_CHANGE.prefix + mapper.writeValueAsString(this) }
  object STRUCTURE_CHANGE { val prefix = "STRUCTURE_CHANGE"; val reg = s"$prefix(.*)".r }

  final case class MERGE(
      id: String,
      changeFeedVersions: Map[String, ChangeFeedTable.Version],
      targetVersion: TargetTable.Version,
      lastUpdates: Map[String, Commit]
  ) extends UserMetadata { def format: String = MERGE.prefix + mapper.writeValueAsString(this) }
  object MERGE { val prefix = "MERGE"; val reg = s"$prefix(.*)".r }

}
