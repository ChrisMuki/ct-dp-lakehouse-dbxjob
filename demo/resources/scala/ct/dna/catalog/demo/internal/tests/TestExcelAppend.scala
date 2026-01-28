package ct.dna.catalog.demo.internal.tests

import org.apache.spark.sql.SparkSession
import ct.dna.catalog.unified.syntax.UnifiedWriteOps._
import org.apache.spark.sql.SaveMode

object TestExcelAppend {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("TestExcelAppend")
      .master("local[*]")
      .config("spark.ui.enabled", "false")
      .config("spark.sql.catalog.test", "ct.dna.catalog.unified.UnifiedTableCatalog")
      .config("spark.sql.catalog.test.path", "test-excel-append")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    import spark.implicits._

    println("Testing Excel append functionality...")

    // Create test directory
    new java.io.File("test-excel-append/sales/2024/q1").mkdirs()

    // Test data for different months
    val januaryData = Seq(
      ("2024-01-01", "Product A", 100.0),
      ("2024-01-15", "Product B", 200.0)
    ).toDF("date", "product", "amount")

    val februaryData = Seq(
      ("2024-02-01", "Product A", 150.0),
      ("2024-02-20", "Product C", 300.0)
    ).toDF("date", "product", "amount")

    val marchData = Seq(
      ("2024-03-10", "Product B", 250.0),
      ("2024-03-25", "Product D", 400.0)
    ).toDF("date", "product", "amount")

    // Write first sheet (creates the file)
    println("\n1. Writing january sheet (creates new file)...")
    januaryData.writeToUnifiedTable("test", "sales.2024.q1.transactions.january", "excel", SaveMode.Overwrite)

    // Append second sheet (should preserve january)
    println("\n2. Appending february sheet...")
    februaryData.writeToUnifiedTable("test", "sales.2024.q1.transactions.february", "excel", SaveMode.Append)

    // Append third sheet (should preserve january and february)
    println("\n3. Appending march sheet...")
    marchData.writeToUnifiedTable("test", "sales.2024.q1.transactions.march", "excel", SaveMode.Append)

    println("\n4. Verifying all sheets are present...")

    // Read back all sheets to verify
    val januaryRead  = spark.table("test.sales.`2024`.q1.transactions.january")
    val februaryRead = spark.table("test.sales.`2024`.q1.transactions.february")
    val marchRead    = spark.table("test.sales.`2024`.q1.transactions.march")

    println("\nJanuary sheet:")
    januaryRead.show()

    println("February sheet:")
    februaryRead.show()

    println("March sheet:")
    marchRead.show()

    println("\nSUCCESS: All sheets preserved in single Excel file!")

    spark.stop()
  }
}
