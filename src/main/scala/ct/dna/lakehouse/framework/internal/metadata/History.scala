package ct.dna.lakehouse.framework.internal.metadata

private[internal] case class History(version: Long, timestamp: java.sql.Timestamp, operation: String, readVersion: Long, userMetadata: UserMetadata)
