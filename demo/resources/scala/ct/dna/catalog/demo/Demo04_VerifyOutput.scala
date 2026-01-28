package ct.dna.catalog.demo

import ct.dna.catalog.demo.utils.DemoUtils
import ct.dna.catalog.demo.entities.TestDataEntities.Customer
import org.apache.spark.sql.Dataset

/** Verify that Demo04 output preserves types correctly */
object Demo04_VerifyOutput {

  def main(args: Array[String]): Unit = {

    DemoUtils.printDemoHeader("04-Verify", "Verify Excel Output Types")

    val spark = DemoUtils.createDemoSparkSession("Demo04-Verify")
    import spark.implicits._

    // Setup paths
    val outputPath = DemoUtils.getTestDataPath().replace("test-data-typed", "demo-output")

    try {
      // Read the Excel file we created
      val excelPath = s"$outputPath/customer_report.xlsx"

      println(s"Reading Excel file: $excelPath")

      // Read the customers sheet
      val customersFromExcel = spark.read
        .format("excel")
        .option("header", "true")
        .option("inferSchema", "true")
        .option("sheetName", "customers")
        .load(excelPath)

      println("\nSchema from 'customers' sheet:")
      customersFromExcel.printSchema()

      // Read the filtered sheet
      val filteredFromExcel = spark.read
        .format("excel")
        .option("header", "true")
        .option("inferSchema", "true")
        .option("sheetName", "customers_filtered")
        .load(excelPath)

      println("\nSchema from 'customers_filtered' sheet:")
      filteredFromExcel.printSchema()

      // Try to convert to typed Dataset
      println("\nTrying to convert to typed Dataset[Customer]...")
      try {
        val typedCustomers: Dataset[Customer] = customersFromExcel.as[Customer]
        println(s"✓ Successfully converted to Dataset[Customer], count: ${typedCustomers.count()}")

        // Show first record with types
        val first = typedCustomers.first()
        println(s"\nFirst customer:")
        println(s"  customer_id: ${first.customer_id} (${first.customer_id.getClass.getSimpleName})")
        println(s"  name: ${first.name} (${first.name.getClass.getSimpleName})")
        println(s"  tier: ${first.tier} (${first.tier.getClass.getSimpleName})")
        println(s"  total_spent: ${first.total_spent} (${first.total_spent.getClass.getSimpleName})")
        println(s"  join_date: ${first.join_date} (${first.join_date.getClass.getSimpleName})")

      } catch {
        case e: Exception =>
          println(s"✗ Failed to convert to Dataset[Customer]: ${e.getMessage}")
      }

      // Compare original parquet schema
      val originalPath      = DemoUtils.getTestDataPath() + "/analytics/data/customers.parquet"
      val originalCustomers = spark.read.parquet(originalPath)

      println(s"\n\nOriginal parquet schema for comparison:")
      originalCustomers.printSchema()

    } finally {
      spark.stop()
    }
  }
}
