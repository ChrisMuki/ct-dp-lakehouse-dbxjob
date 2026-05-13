package ct.dna.lakehouse.dm_md.fin_hawk

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MarcTransformTest extends AnyFlatSpec with Matchers with SparkTestBase {

  /** Local mirror of marc.projectChanges.
    *
    * Reproduces the cleansing behaviour the production object performs in `projectChanges`: stawn / steuc have dots and whitespace stripped, and the originals
    * are kept under stawn_sap / steuc_sap. Production additionally retains `_change_type` for the merge branches.
    */
  private def transform(df: DataFrame): DataFrame =
    df.filter(col("matnr").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("matnr"),
        col("werks"),
        col("lvorm").as("lvorm_plant"),
        regexp_replace(col("stawn"), "\\.|\\s", "").as("stawn"),
        regexp_replace(col("steuc"), "\\.|\\s", "").as("steuc"),
        col("herkl"),
        col("stawn").as("stawn_sap"),
        col("steuc").as("steuc_sap"),
        col("_change_type")
      )

  private val schema = StructType(
    Seq(
      StructField("_mk_system", StringType),
      StructField("_mk_instance", StringType),
      StructField("matnr", StringType),
      StructField("werks", StringType),
      StructField("lvorm", StringType),
      StructField("stawn", StringType),
      StructField("steuc", StringType),
      StructField("herkl", StringType),
      StructField("_change_type", StringType)
    )
  )

  it should "pass through valid rows with correct column mappings" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "MAT001", "1000", "", "84719000", "84719000", "DE", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    val row = result.head
    row.getAs[String]("matnr") shouldBe "MAT001"
    row.getAs[String]("werks") shouldBe "1000"
    row.getAs[String]("herkl") shouldBe "DE"
    row.getAs[String]("stawn_sap") shouldBe "84719000"
    row.getAs[String]("steuc_sap") shouldBe "84719000"
    row.getAs[String]("_change_type") shouldBe "insert"
  }

  it should "rename lvorm to lvorm_plant" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "MAT001", "1000", "X", "12345", "67890", "US", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result.head.getAs[String]("lvorm_plant") shouldBe "X"
  }

  it should "remove dots and spaces from stawn" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "MAT001", "1000", "", "8471.90 00", "12345", "DE", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result.head.getAs[String]("stawn") shouldBe "84719000"
    result.head.getAs[String]("stawn_sap") shouldBe "8471.90 00"
  }

  it should "remove dots and spaces from steuc" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "MAT001", "1000", "", "12345", "8471.90 00", "DE", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result.head.getAs[String]("steuc") shouldBe "84719000"
    result.head.getAs[String]("steuc_sap") shouldBe "8471.90 00"
  }

  it should "filter out rows with null matnr" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(
          Row("SYS1", "100", null, "1000", "", "12345", "67890", "DE", "insert"),
          Row("SYS1", "100", "MAT002", "2000", "", "11111", "22222", "US", "update")
        )
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    result.head.getAs[String]("matnr") shouldBe "MAT002"
    result.head.getAs[String]("_change_type") shouldBe "update"
  }

  it should "propagate delete _change_type so the merge can branch on it" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "MAT001", "1000", null, null, null, null, "delete"))
      ),
      schema
    )

    transform(df).collect().head.getAs[String]("_change_type") shouldBe "delete"
  }

  it should "return correct output columns" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "MAT001", "1000", "", "12345", "67890", "DE", "insert"))
      ),
      schema
    )

    val result = transform(df)
    result.columns.toSet shouldBe Set(
      "_mk_system",
      "_mk_instance",
      "matnr",
      "lvorm_plant",
      "werks",
      "stawn",
      "steuc",
      "herkl",
      "stawn_sap",
      "steuc_sap",
      "_change_type"
    )
  }

  it should "handle clean stawn/steuc values without dots or spaces" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "MAT001", "1000", "", "84719000", "84719000", "DE", "insert"))
      ),
      schema
    )

    val result = transform(df).collect()
    result.head.getAs[String]("stawn") shouldBe "84719000"
    result.head.getAs[String]("steuc") shouldBe "84719000"
  }
}
