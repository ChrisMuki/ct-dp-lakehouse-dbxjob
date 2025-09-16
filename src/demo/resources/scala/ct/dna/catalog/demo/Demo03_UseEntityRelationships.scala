package ct.dna.catalog.demo

import ct.dna.catalog.demo.utils.DemoUtils
import ct.dna.catalog.demo.entities.TestDataEntities._
import ct.dna.lakehouse.entity.Entity.Entity
import ct.dna.lakehouse.entity.Entity.EntityValidationExtensions._
import ct.dna.lakehouse.entity.Entity.EntityAutoJoinExtensions._
import org.apache.spark.sql.functions._

/** Demo 03: Use Table Relationships
  *
  * Demonstrates the power of Entity Framework annotations:
  *   - \@pk (Primary Key) validation
  *   - \@fk (Foreign Key) automatic join inference
  *   - Entity validation and schema checking
  *   - Type-safe cross-table relationships
  *
  * Usage: sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.Demo03_UseTableRelationships"
  */
object Demo03_UseEntityRelationships {

  def main(args: Array[String]): Unit = {

    DemoUtils.printDemoHeader("03", "Entity Framework Relationships")

    val testDataPath = DemoUtils.getTestDataPath()

    val spark = DemoUtils.createDemoSparkSession("Demo03-UseTableRelationships")
    import spark.implicits._

    spark.conf.set("spark.sql.catalog.unified", "ct.dna.catalog.unified.UnifiedTableCatalog")
    spark.conf.set("spark.sql.catalog.unified.path", testDataPath)

    try {
      DemoUtils.printSection("Entity Validation - Primary Keys")

      // Load entities with automatic schema validation
      val customers    = spark.table("unified.analytics.data.customers").as[Customer]
      val orders       = spark.table("unified.analytics.data.orders").as[Order]
      val products     = spark.table("unified.sales.products.catalog.products").as[Product]
      val transactions = spark.table("unified.sales.`2024`.q1.transactions.january").as[SalesTransaction]

      // Validate primary key uniqueness
      println("Validating primary keys...")
      println(s"Customer PKs unique: ${customers.validatePK()}")
      println(s"Order PKs unique: ${orders.validatePK()}")
      println(s"Product PKs unique: ${products.validatePK()}")

      DemoUtils.printSection("Foreign Key Relationship Discovery")

      // Dynamically discover foreign keys using Entity Framework
      val orderFKs       = Entity.getAllForeignKeys[Order]
      val transactionFKs = Entity.getAllForeignKeys[SalesTransaction]

      println("\nOrder entity foreign keys:")
      orderFKs.foreach { fk =>
        println(s"  ${fk.fieldName} -> ${fk.targetEntity.typeSymbol.name}")
      }

      println("\nSalesTransaction entity foreign keys:")
      transactionFKs.foreach { fk =>
        println(s"  ${fk.fieldName} -> ${fk.targetEntity.typeSymbol.name}")
      }

      DemoUtils.printSection("Automatic Join Inference")

      // Manual join (traditional way)
      val manualJoin = orders
        .join(customers, orders("customer_id") === customers("customer_id"))
        .select(
          orders("order_id"),
          customers("name").as("customer_name"),
          orders("total")
        )

      println("Manual join result:")
      manualJoin.show(5)

      // Automatic join using Entity Framework
      println("\nAutomatic join using Entity Framework:")
      val autoJoinResult = orders.autoJoin(customers)

      println("orders.autoJoin(customers) - automatically detects customer_id FK")
      autoJoinResult.map { case (order, customer) =>
        (order.order_id, customer.name, order.total)
      }.toDF("order_id", "customer_name", "total").show(5)

      DemoUtils.printSection("Multi-Level Relationships")

      // Manual approach with multiple joins
      val manualEnrichedTransactions = transactions
        .join(customers, transactions("customer_id") === customers("customer_id"))
        .join(products, transactions("product_id") === products("product_id"))
        .select(
          transactions("tx_id"),
          transactions("date"),
          customers("name").as("customer_name"),
          customers("tier").as("customer_tier"),
          products("name").as("product_name"),
          products("category").as("product_category"),
          transactions("amount")
        )

      // Automatic approach using Entity Framework
      println("Using autoJoin for multiple relationships:")
      val txWithCustomer = transactions.autoJoin(customers)
      val txWithProduct  = transactions.autoJoin(products)

      // Combine both joins
      val enrichedTransactions = transactions
        .autoJoin(customers)
        .map { case (tx, cust) => (tx, cust) }
        .join(
          transactions.autoJoin(products).toDF("tx2", "prod"),
          col("_1.tx_id") === col("tx2.tx_id")
        )
        .select(
          col("_1.tx_id").as("tx_id"),
          col("_1.date").as("date"),
          col("_2.name").as("customer_name"),
          col("_2.tier").as("customer_tier"),
          col("prod.name").as("product_name"),
          col("prod.category").as("product_category"),
          col("_1.amount").as("amount")
        )

      println("Enriched transactions with customer and product details:")
      enrichedTransactions.show(10, false)

      // Analyze spending by customer tier and product category
      val spendingAnalysis = enrichedTransactions
        .groupBy($"customer_tier", $"product_category")
        .agg(
          count("*").as("transaction_count"),
          sum($"amount").as("total_spent"),
          avg($"amount").as("avg_transaction")
        )
        .orderBy($"customer_tier", $"total_spent".desc)

      println("\nSpending analysis by customer tier and product category:")
      spendingAnalysis.show()

      DemoUtils.printSection("Foreign Key Validation")

      // Use generic foreign key validation
      println("Using Entity Framework for foreign key validation:")

      val orderCustomerValid = orders.validateForeignKeys(customers)
      println(s"Order->Customer FK validation: ${if (orderCustomerValid) "✓ All valid" else "✗ Invalid FKs found"}")

      val txCustomerValid = transactions.validateForeignKeys(customers)
      val txProductValid  = transactions.validateForeignKeys(products)

      println(s"Transaction->Customer FK validation: ${if (txCustomerValid) "✓ All valid" else "✗ Invalid FKs found"}")
      println(s"Transaction->Product FK validation: ${if (txProductValid) "✓ All valid" else "✗ Invalid FKs found"}")

      DemoUtils.printSection("Entity Framework Benefits Summary")

      println("""
        |Entity Framework with @pk and @fk annotations provides:
        |
        |1. Compile-time type safety
        |   - No more runtime SQL errors
        |   - IDE autocomplete for all fields
        |
        |2. Automatic relationship discovery
        |   - @fk annotations document relationships
        |   - Enables automatic join inference
        |
        |3. Schema validation
        |   - Ensures data integrity
        |   - Validates foreign key constraints
        |
        |4. Self-documenting code
        |   - Relationships visible in entity definitions
        |   - No need to consult ERD diagrams
        """.stripMargin)

      // Show a complex query that benefits from type safety

      val topCustomersByCategory = enrichedTransactions
        .groupBy($"customer_name", $"product_category")
        .agg(sum($"amount").as("category_total"))
        .groupBy($"customer_name")
        .agg(
          collect_list(struct($"product_category", $"category_total")).as("category_spending"),
          sum($"category_total").as("total_spent")
        )
        .orderBy($"total_spent".desc)
        .limit(3)

      println("\nTop customers with category breakdown:")
      topCustomersByCategory.show(false)

    } finally {
      spark.stop()
    }
  }
}
