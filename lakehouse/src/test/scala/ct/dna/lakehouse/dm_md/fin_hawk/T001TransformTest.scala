package ct.dna.lakehouse.dm_md.fin_hawk

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class T001TransformTest extends AnyFlatSpec with Matchers with SparkTestBase {

  /** Local mirror of t001.projectChanges (production keeps `_change_type` for the merge branches). */
  private def transform(df: DataFrame): DataFrame =
    df.filter(col("bukrs").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("bukrs"),
        col("butxt"),
        col("land1"),
        col("ort01"),
        col("_change_type")
      )

  private val schema = StructType(
    Seq(
      StructField("_mk_system", StringType),
      StructField("_mk_instance", StringType),
      StructField("bukrs", StringType),
      StructField("butxt", StringType),
      StructField("land1", StringType),
      StructField("ort01", StringType),
      StructField("_change_type", StringType)
    )
  )

  it should "pass through valid rows" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "1000", "Company A", "DE", "Berlin", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    val row = result.head
    row.getAs[String]("bukrs") shouldBe "1000"
    row.getAs[String]("butxt") shouldBe "Company A"
    row.getAs[String]("land1") shouldBe "DE"
    row.getAs[String]("ort01") shouldBe "Berlin"
    row.getAs[String]("_change_type") shouldBe "insert"
  }

  it should "filter out rows with null bukrs" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(
          Row("SYS1", "100", null, "No Code", "DE", "Berlin", "insert"),
          Row("SYS1", "100", "2000", "Company B", "US", "New York", "update")
        )
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    result.head.getAs[String]("bukrs") shouldBe "2000"
    result.head.getAs[String]("_change_type") shouldBe "update"
  }

  it should "propagate delete _change_type so the merge can branch on it" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "1000", null, null, null, "delete"))
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    result.head.getAs[String]("_change_type") shouldBe "delete"
  }

  it should "return correct output columns" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "1000", "Company A", "DE", "Berlin", "insert"))
      ),
      schema
    )

    val result = transform(df)
    result.columns.toSet shouldBe Set("_mk_system", "_mk_instance", "bukrs", "butxt", "land1", "ort01", "_change_type")
  }

  it should "return empty when all bukrs are null" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", null, "No Code", "DE", "Berlin", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result shouldBe empty
  }
}
