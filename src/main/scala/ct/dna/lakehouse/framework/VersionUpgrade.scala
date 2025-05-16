package ct.dna.lakehouse.framework

final case class ChangeFeedVersion(snapshot: Long, from: Long, to: Long) {}
final case class TargetVersion(from: Long, to: Long) {}
