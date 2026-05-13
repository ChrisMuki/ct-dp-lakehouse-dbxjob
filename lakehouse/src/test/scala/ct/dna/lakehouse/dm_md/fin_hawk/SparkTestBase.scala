package ct.dna.lakehouse.dm_md.fin_hawk

import org.apache.spark.sql.SparkSession
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Suite

trait SparkTestBase extends BeforeAndAfterAll { self: Suite =>

  @transient lazy val spark: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .appName(getClass.getSimpleName)
    .config("spark.ui.enabled", "false")
    .config("spark.sql.shuffle.partitions", "2")
    .config("spark.default.parallelism", "2")
    .getOrCreate()

  override def afterAll(): Unit = {
    try spark.stop()
    finally super.afterAll()
  }
}
