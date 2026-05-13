package ct.dna.lakehouse.dm_md.fin_hawk

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MaraTransformTest extends AnyFlatSpec with Matchers with SparkTestBase {

  /** Local mirror of the projection encoded in mara.projectChanges.
    *
    * Production retains `_change_type` for the merge branches; we mirror it here so the test exercises the same column-by-column contract the merge depends on.
    */
  private def transform(df: DataFrame): DataFrame =
    df.filter(col("matnr").isNotNull)
      .select(
        col("_mk_system"),
        col("_mk_instance"),
        col("matnr"),
        col("mtart"),
        col("matkl"),
        col("ersda"),
        col("pstat"),
        col("vpsta"),
        col("lvorm"),
        col("meins"),
        col("ferth"),
        col("formt"),
        col("groes"),
        col("wrkst"),
        col("normt"),
        col("brgew"),
        col("ntgew"),
        col("gewei"),
        col("volum"),
        col("voleh"),
        col("laeng"),
        col("breit"),
        col("hoehe"),
        col("meabm"),
        col("prdha"),
        col("attyp"),
        col("mfrpn"),
        col("mfrnr"),
        col("_change_type")
      )

  private val schema = StructType(
    Seq(
      StructField("_mk_system", StringType),
      StructField("_mk_instance", StringType),
      StructField("matnr", StringType),
      StructField("mtart", StringType),
      StructField("matkl", StringType),
      StructField("ersda", StringType),
      StructField("pstat", StringType),
      StructField("vpsta", StringType),
      StructField("lvorm", StringType),
      StructField("meins", StringType),
      StructField("ferth", StringType),
      StructField("formt", StringType),
      StructField("groes", StringType),
      StructField("wrkst", StringType),
      StructField("normt", StringType),
      StructField("brgew", DecimalType(13, 3)),
      StructField("ntgew", DecimalType(13, 3)),
      StructField("gewei", StringType),
      StructField("volum", DoubleType),
      StructField("voleh", StringType),
      StructField("laeng", DoubleType),
      StructField("breit", DoubleType),
      StructField("hoehe", DoubleType),
      StructField("meabm", StringType),
      StructField("prdha", StringType),
      StructField("attyp", StringType),
      StructField("mfrpn", StringType),
      StructField("mfrnr", StringType),
      StructField("_change_type", StringType)
    )
  )

  /** Build a row template with `matnr` and `_change_type` overridable. */
  private def row(matnr: String, changeType: String = "insert"): Row =
    Row(
      "SYS1",
      "100",
      matnr,
      "HAWA",
      "GRP01",
      "20200101",
      "K",
      "K",
      "",
      "ST",
      "",
      "",
      "",
      "",
      "",
      null,
      null,
      "",
      null,
      "",
      null,
      null,
      null,
      "",
      "",
      "",
      "",
      "",
      changeType
    )

  it should "pass through valid rows with all columns" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(
          Row(
            "SYS1",
            "100",
            "MAT001",
            "HAWA",
            "GRP01",
            "20200101",
            "KVBM",
            "KVBM",
            "",
            "ST",
            "FERTH1",
            "FMT1",
            "10x10",
            "STEEL",
            "NORM1",
            new java.math.BigDecimal("1.500"),
            new java.math.BigDecimal("1.200"),
            "KG",
            0.5d,
            "M3",
            10.0d,
            5.0d,
            3.0d,
            "CM",
            "PRD01",
            "00",
            "MFR001",
            "MFRNR01",
            "insert"
          )
        )
      ),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    val r = result.head
    r.getAs[String]("matnr") shouldBe "MAT001"
    r.getAs[String]("mtart") shouldBe "HAWA"
    r.getAs[String]("matkl") shouldBe "GRP01"
    r.getAs[String]("meins") shouldBe "ST"
    r.getAs[java.math.BigDecimal]("brgew") shouldBe new java.math.BigDecimal("1.500")
    r.getAs[java.math.BigDecimal]("ntgew") shouldBe new java.math.BigDecimal("1.200")
    r.getAs[String]("_change_type") shouldBe "insert"

  }

  it should "filter out rows with null matnr" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(Seq(row(null, "insert"), row("MAT002", "update"))),
      schema
    )

    val result = transform(df).collect()
    result should have length 1
    result.head.getAs[String]("matnr") shouldBe "MAT002"
    result.head.getAs[String]("_change_type") shouldBe "update"
  }

  it should "propagate delete _change_type so the merge can branch on it" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(Seq(row("MAT001", "delete"))),
      schema
    )

    transform(df).collect().head.getAs[String]("_change_type") shouldBe "delete"
  }

  it should "return correct output columns" in {
    val df = spark.createDataFrame(spark.sparkContext.parallelize(Seq(row("MAT001"))), schema)

    transform(df).columns.toSet shouldBe Set(
      "_mk_system",
      "_mk_instance",
      "matnr",
      "mtart",
      "matkl",
      "ersda",
      "pstat",
      "vpsta",
      "lvorm",
      "meins",
      "ferth",
      "formt",
      "groes",
      "wrkst",
      "normt",
      "brgew",
      "ntgew",
      "gewei",
      "volum",
      "voleh",
      "laeng",
      "breit",
      "hoehe",
      "meabm",
      "prdha",
      "attyp",
      "mfrpn",
      "mfrnr",
      "_change_type"
    )
  }

  it should "return empty DataFrame when all rows have null matnr" in {

    val df = spark.createDataFrame(spark.sparkContext.parallelize(Seq(row(null))), schema)
    transform(df).collect() shouldBe empty

  }
}
