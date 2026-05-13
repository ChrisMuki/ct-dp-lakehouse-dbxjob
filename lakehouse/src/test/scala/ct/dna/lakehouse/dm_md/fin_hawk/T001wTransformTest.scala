package ct.dna.lakehouse.dm_md.fin_hawk

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class T001wTransformTest extends AnyFlatSpec with Matchers with SparkTestBase {

  /** Local mirror of t001w.projectChanges (production keeps `_change_type` for the merge branches). */
  private def transform(df: DataFrame): DataFrame =
    df.filter(col("werks").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("werks"),
        col("name1"),
        col("bwkey"),
        col("land1"),
        col("kunnr"),
        col("lifnr"),
        col("_change_type")
      )

  private val schema = StructType(
    Seq(
      StructField("_mk_system", StringType),
      StructField("_mk_instance", StringType),
      StructField("werks", StringType),
      StructField("name1", StringType),
      StructField("bwkey", StringType),
      StructField("land1", StringType),
      StructField("kunnr", StringType),
      StructField("lifnr", StringType),
      StructField("_change_type", StringType)
    )
  )

  it should "pass through valid rows with all columns" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "1000", "Plant Berlin", "1000", "DE", "CUST01", "VEND01", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    val row = result.head
    row.getAs[String]("werks") shouldBe "1000"
    row.getAs[String]("name1") shouldBe "Plant Berlin"
    row.getAs[String]("bwkey") shouldBe "1000"
    row.getAs[String]("land1") shouldBe "DE"
    row.getAs[String]("kunnr") shouldBe "CUST01"
    row.getAs[String]("lifnr") shouldBe "VEND01"
    row.getAs[String]("_change_type") shouldBe "insert"
  }

  it should "filter out rows with null werks" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(
          Row("SYS1", "100", null, "No Plant", "1000", "DE", "C01", "V01", "insert"),
          Row("SYS1", "100", "2000", "Plant Munich", "2000", "DE", "C02", "V02", "update")
        )
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    result.head.getAs[String]("werks") shouldBe "2000"
    result.head.getAs[String]("_change_type") shouldBe "update"
  }

  it should "propagate delete _change_type so the merge can branch on it" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "1000", null, null, null, null, null, "delete"))
      ),
      schema
    )

    transform(df).collect().head.getAs[String]("_change_type") shouldBe "delete"
  }

  it should "return correct output columns" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "1000", "Plant", "1000", "DE", "C01", "V01", "insert"))
      ),
      schema
    )

    val result = transform(df)
    result.columns.toSet shouldBe Set(
      "_mk_system",
      "_mk_instance",
      "werks",
      "name1",
      "bwkey",
      "land1",
      "kunnr",
      "lifnr",
      "_change_type"
    )
  }

  it should "return empty when all werks are null" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", null, "No Plant", "1000", "DE", "C01", "V01", "insert"))
      ),
      schema
    )

    transform(df).collect() shouldBe empty
  }

  it should "handle multiple plants from different systems" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(
          Row("SYS1", "100", "1000", "Plant A", "1000", "DE", "C01", "V01", "insert"),
          Row("SYS2", "200", "1000", "Plant B", "1000", "US", "C02", "V02", "insert")
        )
      ),
      schema
    )

    val result = transform(df).collect().sortBy(_.getAs[String]("_mk_system"))
    result should have length 2
    result(0).getAs[String]("name1") shouldBe "Plant A"
    result(1).getAs[String]("name1") shouldBe "Plant B"
  }
}
