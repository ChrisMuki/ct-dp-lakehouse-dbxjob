package ct.dna.lakehouse
import org.apache.spark.sql.SparkSession
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec._
import org.scalatest.matchers._
import ct.dna.lakehouse.framework.internal.CatalogAccess

class ConnectionTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterAll {

  "SparkConnect" should "work" in {

    val workspace = """adb-1695223150250302.2.azuredatabricks.net"""
    val clusterId = """0606-075530-9uskjfpz"""
    val pat = """dapi3ff1f7234447f8935306a2b532621ebe-2"""

    val connectionString = s"sc://$workspace:443/;token=$pat;x-databricks-cluster-id=$clusterId"
    val spark = SparkSession.builder().remote(connectionString).getOrCreate()

    // spark.sql("""
    // CREATE TABLE ctdpproddbxsandbox.uia00402.Table4 (
    //   ID STRING,
    //   Amount LONG
    // )
    // USING DELTA
    // TBLPROPERTIES (
    //   'delta.enableChangeDataFeed' = 'true'
    // )
    // """)

    import spark.implicits._
    val df1 = Seq(("id1", 47L)).toDF("ID", "Amount")
    val df2 = Seq(("Hello")).toDF("_lh_metadata")

    val df3 = df1 unionByName (df2, true)

    df3.show()

    // CatalogAccess.s
  }

}
