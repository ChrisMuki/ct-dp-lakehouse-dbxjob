package ct.dna.lakehouse.framework.internal

sealed trait FQTN {
  val value: String
}
object FQTN {
  case class PATH(value: String) extends FQTN
  case class TABLE(value: String) extends FQTN
}
