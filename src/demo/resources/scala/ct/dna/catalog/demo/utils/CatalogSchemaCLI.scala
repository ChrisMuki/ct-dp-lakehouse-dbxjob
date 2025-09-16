package ct.dna.catalog.demo.utils

import org.apache.spark.sql.SparkSession
import ct.dna.catalog.unified.CatalogSchemaInspectorExtensions._

/** Command Line Interface for Catalog Schema Inspection
  *
  * Usage: sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.CatalogSchemaCLI [command] [args]"
  *
  * Commands: list - List all tables describe <table> - Describe a specific table schema - Show full catalog schema find <column> - Find tables containing a
  * column export <path> - Export catalog schema to CSV
  */
object CatalogSchemaCLI {

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      printUsage()
      System.exit(1)
    }

    val spark = SparkSession.builder()
      .appName("CatalogSchemaCLI")
      .master("local[*]")
      .config("spark.ui.enabled", "false")
      .config("spark.sql.adaptive.enabled", "false")
      .config("spark.sql.catalog.unified", "ct.dna.catalog.unified.UnifiedTableCatalog")
      .config("spark.sql.catalog.unified.path", new java.io.File("../multiformat-catalog/src/test/resources/test-data").getAbsolutePath)
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    try {
      args.head.toLowerCase match {
        case "list" =>
          listTables(spark)

        case "describe" if args.length > 1 =>
          describeTable(spark, args(1))

        case "schema" =>
          showFullSchema(spark)

        case "find" if args.length > 1 =>
          findColumn(spark, args(1))

        case "export" if args.length > 1 =>
          exportSchema(spark, args(1))

        case _ =>
          printUsage()
      }
    } finally {
      spark.stop()
    }
  }

  private def listTables(spark: SparkSession): Unit = {
    spark.showCatalogTables("unified")
  }

  private def describeTable(spark: SparkSession, tableName: String): Unit = {
    spark.describeCatalogTable(tableName, "unified")
  }

  private def showFullSchema(spark: SparkSession): Unit = {
    val schema = spark.getCatalogSchema("unified")
    println(schema.formatReport())
  }

  private def findColumn(spark: SparkSession, columnName: String): Unit = {
    val schema = spark.getCatalogSchema("unified")
    val tables = schema.tables.filter { table =>
      table.schema.fieldNames.exists(_.toLowerCase.contains(columnName.toLowerCase))
    }

    println(s"\nTables containing column '$columnName':")
    if (tables.isEmpty) {
      println("  No tables found")
    } else {
      tables.foreach { table =>
        val matchingFields = table.schema.fields
          .filter(_.name.toLowerCase.contains(columnName.toLowerCase))
          .map(f => s"${f.name}: ${f.dataType.simpleString}")

        println(s"\n  ${table.fullName} (${table.format})")
        matchingFields.foreach(f => println(s"    - $f"))
      }
      println(s"\nTotal: ${tables.size} tables")
    }
  }

  private def exportSchema(spark: SparkSession, path: String): Unit = {
    import spark.implicits._

    val schema = spark.getCatalogSchema("unified")

    // Create detailed export
    val exportData = schema.tables.flatMap { table =>
      table.schema.fields.map { field =>
        (
          table.namespace,
          table.tableName,
          table.format,
          field.name,
          field.dataType.simpleString,
          field.nullable,
          field.metadata.toString
        )
      }
    }

    val df = exportData.toDF(
      "namespace",
      "table",
      "format",
      "column",
      "type",
      "nullable",
      "metadata"
    )

    df.coalesce(1)
      .write
      .mode("overwrite")
      .option("header", "true")
      .csv(path)

    println(s"Schema exported to: $path")
    println(s"Total columns exported: ${df.count()}")
  }

  private def printUsage(): Unit = {
    println("""
      |Catalog Schema CLI - Inspect unified catalog schemas
      |
      |Usage: CatalogSchemaCLI <command> [args]
      |
      |Commands:
      |  list                    List all tables in the catalog
      |  describe <table>        Show detailed schema for a table
      |  schema                  Show complete catalog schema report  
      |  find <column>          Find tables containing a column
      |  export <path>          Export catalog schema to CSV
      |
      |Examples:
      |  CatalogSchemaCLI list
      |  CatalogSchemaCLI describe analytics.data.customers
      |  CatalogSchemaCLI find customer_id
      |  CatalogSchemaCLI export /tmp/catalog_schema
    """.stripMargin)
  }
}
