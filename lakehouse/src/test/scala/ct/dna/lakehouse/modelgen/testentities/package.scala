package ct.dna.lakehouse.modelgen

import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.LakehouseEntity
import ct.dna.lakehouse.core.model.Entity.clusterby
import ct.dna.lakehouse.core.model.Entity.decimal
import ct.dna.lakehouse.core.model.Entity.notnull
import ct.dna.lakehouse.core.model.Entity.pk
import org.apache.spark.sql.types._

/** Test entity definitions for roundtrip code generation testing.
  *
  * These are copied from lakehouseCore's caseclasses/package.scala to ensure the code generator produces compatible output.
  */
package object testentities {

  // ========== Basic Entities ==========

  @LakehouseEntity
  case class TestEntity(@pk id: Long, name: String, value: Double)

  @LakehouseEntity
  case class TestEntityNoValue(@pk id: Long)

  // ========== Primitives (non-null → Scala primitives) ==========

  case class Primitives(
      @pk col1: Int,
      col2: Long,
      col3: Short,
      col4: Byte,
      col5: Boolean,
      col6: Double,
      col7: Float
  ) extends Entity

  case class Primitives_PK(
      @pk col1: Int,
      @pk col2: Long,
      @pk col3: Short,
      @pk col4: Byte,
      @pk col5: Boolean,
      @pk col6: Double,
      @pk col7: Float
  ) extends Entity

  // Note: Primitives_Option and Primitives_Option_NotNull are INVALID entities:
  // - Option[primitive] + @pk is rejected: PK requires non-null, Option makes it nullable
  // - Option[primitive] + @notnull is rejected: conflicting nullability annotations
  // These are error cases in lakehouseCore and not valid for roundtrip testing.

  // ========== Objects (nullable → Java boxed types) ==========

  case class Objects(
      @pk col1: java.lang.Integer,
      col2: java.lang.Long,
      col3: java.lang.Short,
      col4: java.lang.Byte,
      col5: java.lang.Boolean,
      col6: java.lang.Double,
      col7: java.lang.Float,
      col8: java.lang.String,
      col9: java.sql.Date,
      col0: java.sql.Timestamp
  ) extends Entity

  case class Objects_PK(
      @pk col1: java.lang.Integer,
      @pk col2: java.lang.Long,
      @pk col3: java.lang.Short,
      @pk col4: java.lang.Byte,
      @pk col5: java.lang.Boolean,
      @pk col6: java.lang.Double,
      @pk col7: java.lang.Float,
      @pk col8: java.lang.String,
      @pk col9: java.sql.Date,
      @pk col0: java.sql.Timestamp,
      @pk col10: java.time.LocalDateTime
  ) extends Entity

  case class Objects_NotNull(
      @pk col1: java.lang.Integer,
      @notnull col2: java.lang.Long,
      @notnull col3: java.lang.Short,
      @notnull col4: java.lang.Byte,
      @notnull col5: java.lang.Boolean,
      @notnull col6: java.lang.Double,
      @notnull col7: java.lang.Float,
      @notnull col8: java.lang.String,
      @notnull col9: java.sql.Date,
      @notnull col0: java.sql.Timestamp
  ) extends Entity

  // ========== Decimals ==========

  case class Decimals(
      @pk col1: java.lang.Integer,
      col2: java.math.BigDecimal, // Default: DecimalType(38, 18)
      col3: java.math.BigDecimal,
      @decimal(15, 1) col4: java.math.BigDecimal,
      @decimal(15, 1) col5: java.math.BigDecimal
  ) extends Entity

  // ========== Collections ==========

  case class Colls(
      @pk col1: String,
      col2: Array[java.lang.Integer],
      col3: Seq[String],
      col4: Seq[java.lang.Integer],
      col5: List[String],
      col6: List[java.lang.Integer],
      col7: Map[String, String],
      col8: Map[String, java.lang.Integer],
      col9: Map[java.lang.Integer, String]
  ) extends Entity

  case class Colls_NotNull(
      @pk col1: String,
      @notnull col2: Array[java.lang.Integer],
      @notnull col3: Seq[String],
      @notnull col4: Seq[java.lang.Integer],
      @notnull col5: List[String],
      @notnull col6: List[java.lang.Integer],
      @notnull col7: Map[String, String],
      @notnull col8: Map[String, java.lang.Integer],
      @notnull col9: Map[java.lang.Integer, String]
  ) extends Entity

  // ========== Nested Structs ==========

  case class NestedChild_nullable(id: String, name: String)

  case class NestedChild_notnull(@notnull id: String, @notnull name: String)

  case class Nested(
      @pk col1: String,
      col2: NestedChild_nullable,
      col3: NestedChild_nullable,
      col4: List[NestedChild_nullable],
      col5: List[NestedChild_nullable],
      col7: Map[String, NestedChild_nullable],
      col8: Map[String, NestedChild_nullable],
      col9: Map[java.lang.Integer, NestedChild_nullable]
  ) extends Entity

  case class NestedParent_nullable(@pk id: Int, child: NestedChild_nullable) extends Entity

  case class NestedParent_notnull_nullable(@pk id: Int, @notnull child: NestedChild_nullable) extends Entity

  case class NestedParent_notnull_notnull(@pk id: Int, @notnull child: NestedChild_notnull) extends Entity

  case class Nested_NotNull(
      @pk col1: String,
      @notnull col2: NestedChild_notnull,
      col3: NestedChild_nullable,
      @notnull col4: List[NestedChild_nullable],
      col5: List[NestedChild_nullable],
      @notnull col7: Map[String, NestedChild_nullable],
      col8: Map[String, NestedChild_nullable],
      @notnull col9: Map[java.lang.Integer, NestedChild_nullable]
  ) extends Entity

  // ========== Mixed Annotations ==========

  case class Mixed(
      @pk @clusterby col1: String,
      @pk col2: String,
      @pk @clusterby col3: String,
      @pk col4: String,
      @notnull @clusterby col5: String,
      @notnull col6: String,
      @clusterby col7: String,
      col8: String
  ) extends Entity

  // ========== Binary ==========

  case class BinaryData(
      @pk id: Long,
      data: Array[Byte]
  ) extends Entity

  // ========== Large Hardcoded Entity (Roundtrip) ==========

  case class BigEntity(
      @pk id: Int,
      col1: Long,
      col2: Long,
      col3: Long,
      col4: Long,
      col5: Long,
      col6: Long,
      col7: Long,
      col8: Long,
      col9: Long,
      col10: Long,
      col11: Long,
      col12: Long,
      col13: Long,
      col14: Long,
      col15: Long,
      col16: Long,
      col17: Long,
      col18: Long,
      col19: Long,
      col20: Long
  ) extends Entity

  // ========== Large Hardcoded Entity (Joined) ==========

  @LakehouseEntity
  case class BigEntityPart1(
      @pk id: Int,
      col1: Long,
      col2: Long,
      col3: Long,
      col4: Long,
      col5: Long,
      col6: Long,
      col7: Long,
      col8: Long,
      col9: Long,
      col10: Long,
      col11: Long,
      col12: Long,
      col13: Long,
      col14: Long,
      col15: Long,
      col16: Long,
      col17: Long,
      col18: Long,
      col19: Long,
      col20: Long,
      col21: Long,
      col22: Long,
      col23: Long,
      col24: Long,
      col25: Long,
      col26: Long,
      col27: Long,
      col28: Long,
      col29: Long,
      col30: Long,
      col31: Long,
      col32: Long,
      col33: Long,
      col34: Long,
      col35: Long,
      col36: Long,
      col37: Long,
      col38: Long,
      col39: Long,
      col40: Long,
      col41: Long,
      col42: Long,
      col43: Long,
      col44: Long,
      col45: Long,
      col46: Long,
      col47: Long,
      col48: Long,
      col49: Long,
      col50: Long,
      col51: Long,
      col52: Long,
      col53: Long,
      col54: Long,
      col55: Long,
      col56: Long,
      col57: Long,
      col58: Long,
      col59: Long,
      col60: Long,
      col61: Long,
      col62: Long,
      col63: Long,
      col64: Long
  ) extends Entity

  @LakehouseEntity
  case class BigEntityPart2(
      @pk col65: Long,
      col66: Long,
      col67: Long,
      col68: Long,
      col69: Long,
      col70: Long,
      col71: Long,
      col72: Long,
      col73: Long,
      col74: Long,
      col75: Long,
      col76: Long,
      col77: Long,
      col78: Long,
      col79: Long,
      col80: Long,
      col81: Long,
      col82: Long,
      col83: Long,
      col84: Long,
      col85: Long,
      col86: Long,
      col87: Long,
      col88: Long,
      col89: Long,
      col90: Long,
      col91: Long,
      col92: Long,
      col93: Long,
      col94: Long,
      col95: Long,
      col96: Long,
      col97: Long,
      col98: Long,
      col99: Long,
      col100: Long,
      col101: Long,
      col102: Long,
      col103: Long,
      col104: Long,
      col105: Long,
      col106: Long,
      col107: Long,
      col108: Long,
      col109: Long,
      col110: Long,
      col111: Long,
      col112: Long,
      col113: Long,
      col114: Long,
      col115: Long,
      col116: Long,
      col117: Long,
      col118: Long,
      col119: Long,
      col120: Long,
      col121: Long,
      col122: Long,
      col123: Long,
      col124: Long,
      col125: Long,
      col126: Long,
      col127: Long,
      col128: Long,
      col129: Long
  ) extends Entity

  type BigEntityJoined = Entity.Joined[BigEntityPart1, BigEntityPart2]

  // Joined with matching PK names and types (merge should succeed)
  @LakehouseEntity
  case class BigEntityPart1MatchingPkOk(
      @pk id: Int,
      col1: Long
  ) extends Entity

  @LakehouseEntity
  case class BigEntityPart2MatchingPkOk(
      @pk id: Int,
      col2: Long
  ) extends Entity

  type BigEntityJoinedMatchingPkOk = Entity.Joined[BigEntityPart1MatchingPkOk, BigEntityPart2MatchingPkOk]

  // Joined with matching PK names but conflicting types (merge should fail)
  @LakehouseEntity
  case class BigEntityPart1MatchingPk(
      @pk id: Int,
      col1: Long
  ) extends Entity

  @LakehouseEntity
  case class BigEntityPart2MatchingPk(
      @pk id: Long,
      col2: Long
  ) extends Entity

  type BigEntityJoinedMatchingPk = Entity.Joined[BigEntityPart1MatchingPk, BigEntityPart2MatchingPk]

  object LargeEntityTestData {
    def largeStructType(fieldCount: Int): StructType = {
      val fields = (1 to fieldCount).map { index =>
        val dataType = if (index % 2 == 0) LongType else DoubleType
        StructField(s"col_$index", dataType, nullable = false)
      }
      StructType(fields)
    }
  }

}
