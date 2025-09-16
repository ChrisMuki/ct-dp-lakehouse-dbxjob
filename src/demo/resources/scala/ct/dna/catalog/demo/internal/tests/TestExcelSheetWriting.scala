package ct.dna.catalog.demo.internal.tests

import org.apache.spark.sql.SparkSession
import ct.dna.catalog.unified.syntax.UnifiedWriteOps._
import org.apache.spark.sql.SaveMode

object TestExcelSheetWriting {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("TestExcelSheetWriting")
      .master("local[*]")
      .config("spark.ui.enabled", "false")
      .config("spark.sql.catalog.test", "ct.dna.catalog.unified.UnifiedTableCatalog")
      .config("spark.sql.catalog.test.path", "test-excel-output")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    import spark.implicits._

    println("Testing Excel sheet name extraction...")

    // Test data
    val df = Seq(("A", 1), ("B", 2)).toDF("col1", "col2")

    // Test 1: Simple table name -> should create test.xlsx with sheet "data"
    println("\nTest 1: Writing to 'simple'")
    df.writeToUnifiedTable("test", "simple", "excel", SaveMode.Overwrite)

    // Test 2: Table name with sheet -> should create transactions.xlsx with sheet "january"
    println("\nTest 2: Writing to 'sales.2024.q1.transactions.january'")
    df.writeToUnifiedTable("test", "sales.2024.q1.transactions.january", "excel", SaveMode.Overwrite)

    println("\nCheck the output in test-excel-output/")

    spark.stop()
  }
}
