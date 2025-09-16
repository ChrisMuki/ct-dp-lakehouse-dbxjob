package ct.dna.catalog.demo

import ct.dna.catalog.demo.utils.DemoUtils
import ct.dna.catalog.demo.entities.TestDataEntities._
import org.apache.spark.sql.functions._

object Demo02_TablesAsDatasets {

  def main(args: Array[String]): Unit = {

    DemoUtils.printDemoHeader("02", "Type-Safe Catalog Explorer")

    val testDataPath = DemoUtils.getTestDataPath()

    val spark = DemoUtils.createDemoSparkSession("Demo02-TypeSafeCatalogExplorer")
    import spark.implicits._

    spark.conf.set("spark.sql.catalog.unified", "ct.dna.catalog.unified.UnifiedTableCatalog")
    spark.conf.set("spark.sql.catalog.unified.path", testDataPath)

    try {
      DemoUtils.printSection("Type-Safe Customer Data Access")

      val customers = spark.table("unified.analytics.data.customers").as[Customer]

      println("Customer schema with Date type:")
      customers.printSchema()

      val recentCustomers = customers
        .filter(_.join_date.after(java.sql.Date.valueOf("2023-06-01")))
        .map(c => (c.name, c.join_date, c.tier))

      println("\nAll Customers")
      customers.show(false)

      println("\nCustomers who joined after June 2023:")
      recentCustomers.show(false)

      DemoUtils.printSection("Type-Safe Order Analysis")

      // Load orders - note: JSON stores dates as strings
      val orders = spark.table("unified.analytics.data.orders").as[Order]

      // Calculate monthly order totals - convert string dates for analysis
      val monthlyTotals = orders
        .withColumn("order_date_typed", to_date($"order_date"))
        .groupBy(month($"order_date_typed").as("month"))
        .agg(
          count("*").as("order_count"),
          sum($"total").as("total_revenue")
        )
        .orderBy("month")

      println("Monthly order analysis:")
      monthlyTotals.show()

      DemoUtils.printSection("Type-Safe Sales Transactions")

      val januarySales = spark
        .table("unified.sales.`2024`.q1.transactions.january")
        .as[SalesTransaction]

      val midJanuarySales = januarySales
        .filter(tx =>
          tx.date.after(java.sql.Date.valueOf("2024-01-14")) &&
            tx.date.before(java.sql.Date.valueOf("2024-01-21")))

      println(s"Mid-January sales (15-20): ${midJanuarySales.count()} transactions")
      midJanuarySales.select($"tx_id", $"date", $"amount").show()

      DemoUtils.printSection("Cross-Format Type-Safe Joins")

      val ordersWithDate = orders.withColumn("order_date_typed", to_date($"order_date"))

      // Hier manuelles join
      val customerOrders = customers
        .join(ordersWithDate, customers("customer_id") === ordersWithDate("customer_id"))
        .select(
          customers("name"),
          customers("tier"),
          customers("join_date"),
          ordersWithDate("order_date_typed").as("order_date"),
          ordersWithDate("total")
        )
        .filter($"order_date" > $"join_date") // Ensure order is after join date

      println("Customer orders (validated dates):")
      customerOrders.show(5, false)

      // Calculate days between join and first order
      val daysToFirstOrder = customerOrders
        .groupBy($"name")
        .agg(
          min($"join_date").as("join_date"),
          min($"order_date").as("first_order_date")
        )
        .withColumn("days_to_first_order", datediff($"first_order_date", $"join_date"))

      println("\nDays from customer join to first order:")
      daysToFirstOrder.show(false)

    } finally {
      spark.stop()
    }
  }
}
