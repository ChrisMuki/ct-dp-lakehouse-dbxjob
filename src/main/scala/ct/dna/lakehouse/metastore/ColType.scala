package ct.dna.lakehouse.metastore

sealed abstract class ColType {
  val nullable: Boolean
}

// Extensible if needed
case class StringType(nullable: Boolean = true) extends ColType
object StringType extends StringType(true)

case class LongType(nullable: Boolean = true) extends ColType
object LongType extends LongType(true)

case class DoubleType(nullable: Boolean = true) extends ColType
object DoubleType extends DoubleType(true)

case class BooleanType(nullable: Boolean = true) extends ColType
object BooleanType extends BooleanType(true)

case class TimestampType(nullable: Boolean = true) extends ColType
object TimestampType extends TimestampType(true)

case class StructType(nullable: Boolean = true) extends ColType
object StructType extends StructType(true)
