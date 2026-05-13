package ct.dna.lakehouse.dm_md.fin_hawk

import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** Mirrors `t023t.pivotByLanguage` from the production object. See [[MaktTransformTest]] for a full description of the contract — t023t differs only in its
  * value column (`wgbez` instead of `maktx`) and its key column (`matkl` instead of `matnr`).
  */
class T023tTransformTest extends AnyFlatSpec with Matchers with SparkTestBase {

  private val cdfSchema = StructType(
    Seq(
      StructField("_mk_system", StringType),
      StructField("_mk_instance", StringType),
      StructField("matkl", StringType),
      StructField("spras", StringType),
      StructField("wgbez", StringType),
      StructField("_change_type", StringType)
    )
  )

  private def pivotByLanguage(lastOfKey: DataFrame, isSnapshot: Boolean): DataFrame = {
    val spras = col("spras")
    val wgbez = col("wgbez")
    val _change_type = col("_change_type")
    val isNewValue = _change_type =!= "delete"

    def changedFor(lang: String): Column =
      if (isSnapshot) lit(true)
      else max(when(spras === lang, lit(true)).otherwise(lit(false)))

    lastOfKey
      .filter(spras === "D" || spras === "E")
      .groupBy(col("_mk_system"), col("_mk_instance"), col("matkl"))
      .agg(
        first(when(spras === "D" && isNewValue, coalesce(wgbez, lit(""))), ignoreNulls = true).as("_value_d"),
        first(when(spras === "E" && isNewValue, coalesce(wgbez, lit(""))), ignoreNulls = true).as("_value_e"),
        changedFor("D").as("_changed_d"),
        changedFor("E").as("_changed_e")
      )
  }

  it should "pivot insert-D + insert-E into per-language values" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(
          Row("SYS1", "100", "GRP01", "D", "Warengruppe", "insert"),
          Row("SYS1", "100", "GRP01", "E", "Material Group", "insert")
        )
      ),
      cdfSchema
    )

    val row = pivotByLanguage(df, isSnapshot = false).collect().head
    row.getAs[String]("matkl") shouldBe "GRP01"
    row.getAs[String]("_value_d") shouldBe "Warengruppe"
    row.getAs[String]("_value_e") shouldBe "Material Group"
    row.getAs[Boolean]("_changed_d") shouldBe true
    row.getAs[Boolean]("_changed_e") shouldBe true
  }

  it should "treat update like insert" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "GRP01", "D", "neu", "update"))
      ),
      cdfSchema
    )

    val row = pivotByLanguage(df, isSnapshot = false).collect().head
    row.getAs[String]("_value_d") shouldBe "neu"
    row.getAs[Boolean]("_changed_d") shouldBe true
  }

  it should "set _value_x to null on delete but keep _changed_x = true" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "GRP01", "D", "old", "delete"))
      ),
      cdfSchema
    )

    val row = pivotByLanguage(df, isSnapshot = false).collect().head
    Option(row.getAs[String]("_value_d")) shouldBe None
    row.getAs[Boolean]("_changed_d") shouldBe true
    row.getAs[Boolean]("_changed_e") shouldBe false

  }

  it should "filter out non-D/E language rows" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(
          Row("SYS1", "100", "GRP04", "F", "Français", "insert"),
          Row("SYS1", "100", "GRP04", "D", "Deutsch", "insert")
        )
      ),
      cdfSchema
    )

    val result = pivotByLanguage(df, isSnapshot = false).collect()
    result should have length 1
    result.head.getAs[String]("_value_d") shouldBe "Deutsch"

  }

  it should "return empty when no D or E rows exist" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "GRP05", "F", "Français", "insert"))
      ),
      cdfSchema
    )

    pivotByLanguage(df, isSnapshot = false).collect() shouldBe empty
  }

  it should "group multiple matkl independently" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(
          Row("SYS1", "100", "GRP01", "D", "Gruppe1", "insert"),
          Row("SYS1", "100", "GRP01", "E", "Group1", "insert"),
          Row("SYS1", "100", "GRP02", "D", "Gruppe2", "insert"),
          Row("SYS1", "100", "GRP02", "E", "Group2", "insert")
        )
      ),
      cdfSchema
    )

    val result = pivotByLanguage(df, isSnapshot = false).collect().sortBy(_.getAs[String]("matkl"))
    result should have length 2
    result(0).getAs[String]("_value_d") shouldBe "Gruppe1"
    result(0).getAs[String]("_value_e") shouldBe "Group1"
    result(1).getAs[String]("_value_d") shouldBe "Gruppe2"
    result(1).getAs[String]("_value_e") shouldBe "Group2"
  }

  it should "force _changed_x = true for every language under a snapshot feed" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "GRP01", "D", "Warengruppe", "insert"))
      ),
      cdfSchema
    )

    val row = pivotByLanguage(df, isSnapshot = true).collect().head
    row.getAs[String]("_value_d") shouldBe "Warengruppe"
    row.getAs[Boolean]("_changed_d") shouldBe true
    row.getAs[Boolean]("_changed_e") shouldBe true
    Option(row.getAs[String]("_value_e")) shouldBe None
  }

  it should "expose the production output column contract" in {
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(
        Seq(Row("SYS1", "100", "GRP01", "D", "Test", "insert"))
      ),
      cdfSchema
    )

    pivotByLanguage(df, isSnapshot = false).columns.toSet shouldBe Set(
      "_mk_system",
      "_mk_instance",
      "matkl",
      "_value_d",
      "_value_e",
      "_changed_d",
      "_changed_e"
    )

  }
}
