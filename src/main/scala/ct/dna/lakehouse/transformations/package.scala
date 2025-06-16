package ct.dna.lakehouse

package object transformations {

  case class Commit(version: Long, timeStamp: java.sql.Timestamp)

}
