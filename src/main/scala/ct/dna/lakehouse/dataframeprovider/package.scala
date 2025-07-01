package ct.dna.lakehouse

package object dataframeprovider {

  case class Commit(version: Long, timeStamp: java.sql.Timestamp)

}
