package ct.dna.lakehouse.dm_md.fin_hawk

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class T001kTransformTest extends AnyFlatSpec with Matchers with SparkTestBase {

  /** Local mirror of t001k.projectChanges (production keeps `_change_type` for the merge branches). */
  private def transform(df: DataFrame): DataFrame =
    df.filter(col("bwkey").isNotNull)
      .select(col("_mk_system"), col("_mk_instance"), col("bwkey"), col("bukrs"), col("_change_type"))

  private val schema = StructType(
    Seq(
      StructField("_mk_system", StringType),
      StructField("_mk_instance", StringType),
      StructField("bwkey", StringType),
      StructField("bukrs", StringType),
      StructField("_change_type", StringType)
    )
  )

  it should "pass through valid rows" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "1000", "CC01", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    val row = result.head
    row.getAs[String]("bwkey") shouldBe "1000"
    row.getAs[String]("bukrs") shouldBe "CC01"
    row.getAs[String]("_change_type") shouldBe "insert"
  }

  it should "filter out rows with null bwkey" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(
          Row("SYS1", "100", null, "CC01", "insert"),
          Row("SYS1", "100", "2000", "CC02", "update")
        )
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    result.head.getAs[String]("bwkey") shouldBe "2000"
    result.head.getAs[String]("_change_type") shouldBe "update"
  }

  it should "propagate delete _change_type so the merge can branch on it" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "1000", null, "delete"))
      ),
      schema
    )

    transform(df).collect().head.getAs[String]("_change_type") shouldBe "delete"
  }

  it should "return correct output columns" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "1000", "CC01", "insert"))
      ),
      schema
    )

    val result = transform(df)
    result.columns.toSet shouldBe Set("_mk_system", "_mk_instance", "bwkey", "bukrs", "_change_type")
  }

  it should "return empty when all bwkey are null" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", null, "CC01", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result shouldBe empty
  }
}
