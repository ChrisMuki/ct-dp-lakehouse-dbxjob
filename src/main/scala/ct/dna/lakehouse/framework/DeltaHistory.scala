package ct.dna.lakehouse.framework

import ct.dna.lakehouse.framework.UserMetadata.INGMetadata
import ct.dna.lakehouse.framework.UserMetadata.LakehouseMetadata

sealed trait DeltaHistory {
  val version: Long
  val timestamp: java.sql.Timestamp
  val operation: String
  val readVersion: Long
}
object DeltaHistory {
  case class LakehouseDeltaHistory(
      val version: Long,
      val timestamp: java.sql.Timestamp,
      val operation: String,
      val readVersion: Long,
      val metadata: LakehouseMetadata
  ) extends DeltaHistory

  case class INGDeltaHistory(
      val version: Long,
      val timestamp: java.sql.Timestamp,
      val operation: String,
      val readVersion: Long,
      val metadata: INGMetadata
  ) extends DeltaHistory
  case class SimpleDeltaHistory(val version: Long, val timestamp: java.sql.Timestamp, val operation: String, val readVersion: Long, val metadata: String)
      extends DeltaHistory
}
