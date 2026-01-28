package ct.dna.catalog.demo

import ct.dna.catalog.demo.utils.DemoUtils
import ct.dna.catalog.demo.entities.TestDataEntities.Customer
import ct.dna.catalog.unified.syntax.UnifiedWriteOps._
import org.apache.spark.sql.{SaveMode, Dataset}
import java.nio.file.{Files, Paths}

/** Demo 04: Create Multi-Sheet Excel Workbooks
  *
  * Shows how to use the Unified Catalog to create Excel files with multiple sheets
  *
  * Usage: sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.Demo04_CreateMultiSheetExcel"
  */
object Demo04_CreateMultiSheetExcel {

  def main(args: Array[String]): Unit = {

    DemoUtils.printDemoHeader("04", "Create Multi-Sheet Excel Workbooks")

    val spark = DemoUtils.createDemoSparkSession("Demo04-CreateMultiSheetExcel")

    // Setup paths
    val testDataPath = DemoUtils.getTestDataPath()
    val outputPath   = Paths.get(testDataPath).getParent.resolve("demo-output")

    // Create output directory
    Files.createDirectories(outputPath)
    println(s"Output directory: $outputPath")

    // Configure unified catalog for output
    spark.conf.set("spark.sql.catalog.output", "ct.dna.catalog.unified.UnifiedTableCatalog")
    spark.conf.set("spark.sql.catalog.output.path", outputPath.toString)

    try {
      // Read parquet from test data
      val customers = spark.read.parquet(s"$testDataPath/analytics/data/customers.parquet")
      println(s"Read ${customers.count()} customers from parquet")

      // Write first sheet - complete copy
      customers.writeToUnifiedTable(
        "output",
        "customer_report.customers",
        "excel",
        SaveMode.Overwrite
      )
      println("Created Excel with 'customers' sheet")

      // Filter data and write second sheet
      val goldCustomers = customers.filter("tier = 'Gold'")
      println(s"Filtered to ${goldCustomers.count()} Gold tier customers")

      goldCustomers.writeToUnifiedTable(
        "output",
        "customer_report.customers_filtered",
        "excel",
        SaveMode.Append
      )
      println("Added 'customers_filtered' sheet to Excel")

      println(s"\nExcel file created at: ${outputPath}/customer_report.xlsx")

      // Test with nested namespace
      println("\n--- Testing nested namespace ---")

      customers.writeToUnifiedTable(
        "output",
        "reports.2024.november.monthly_customers.all_customers",
        "excel",
        SaveMode.Overwrite
      )
      println("Created nested Excel with 'all_customers' sheet")

      goldCustomers.writeToUnifiedTable(
        "output",
        "reports.2024.november.monthly_customers.gold_customers",
        "excel",
        SaveMode.Append
      )
      println("Added 'gold_customers' sheet to nested Excel")

      println(s"\nNested Excel file created at: ${outputPath}/reports/2024/november/monthly_customers.xlsx")

    } finally {
      spark.stop()
    }
  }
}
