package ct.dna.catalog.demo

import ct.dna.catalog.unified.CatalogSchemaInspectorExtensions._
import ct.dna.catalog.demo.utils.DemoUtils

/** Demo 01: Catalog Explorer
  *
  * High-level overview of all tables in the unified catalog showing:
  *   - Namespace
  *   - Table name
  *   - Format
  *   - Column names with types
  *
  * Usage: sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.Demo01_CatalogExplorer"
  */
object Demo01_CatalogExplorer {

  def main(args: Array[String]): Unit = {

    DemoUtils.printDemoHeader("01", "Catalog Explorer")

    val testDataPath = DemoUtils.getTestDataPath()

    val spark = DemoUtils.createDemoSparkSession("Demo01-CatalogExplorer")
    spark.conf.set("spark.sql.catalog.unified", "ct.dna.catalog.unified.UnifiedTableCatalog")
    spark.conf.set("spark.sql.catalog.unified.path", testDataPath)

    try {
      val catalogSchema = spark.getCatalogSchema("unified")

      println(s"Found ${catalogSchema.tables.length} tables in catalog\n")

      DemoUtils.printSection("Catalog Contents")

      // Sort tables by namespace and name
      val sortedTables = catalogSchema.tables.sortBy(t => (t.namespace, t.tableName))

      val namespaceWidth = math.max(9, sortedTables.map(_.namespace.length).max)
      val tableWidth     = math.max(5, sortedTables.map(_.tableName.length).max)
      val formatWidth    = 7
      val fieldsWidth    = 120

      println(
        s"| ${"Namespace".padTo(namespaceWidth, ' ')} | ${"Table".padTo(tableWidth, ' ')} | ${"Format".padTo(formatWidth, ' ')} | ${"Fields".padTo(fieldsWidth, ' ')} |")
      println(s"|${"-" * (namespaceWidth + 2)}|${"-" * (tableWidth + 2)}|${"-" * (formatWidth + 2)}|${"-" * (fieldsWidth + 2)}|")

      sortedTables.foreach { table =>
        val fieldsSummary   = table.schema.fields.map(f => s"${f.name}:${f.dataType.simpleString}").mkString(", ")
        val truncatedFields = if (fieldsSummary.length > fieldsWidth) {
          fieldsSummary.take(fieldsWidth - 3) + "..."
        } else {
          fieldsSummary
        }

        println(s"| ${table.namespace.padTo(namespaceWidth, ' ')} | ${table.tableName.padTo(tableWidth, ' ')} | ${table.format.padTo(formatWidth, ' ')} | ${truncatedFields.padTo(fieldsWidth, ' ')} |")
      }

      println(s"\nTotal tables: ${catalogSchema.tables.length}")

      val formatCounts = sortedTables.groupBy(_.format).view.mapValues(_.size).toMap
      println(s"By format: ${formatCounts.toSeq.sortBy(_._1).map { case (format, count) => s"$format($count)" }.mkString(", ")}")

      val namespaceCounts = sortedTables.groupBy(_.namespace).view.mapValues(_.size).toMap
      println(s"By namespace: ${namespaceCounts.size} namespaces")

    } finally {
      spark.stop()
    }
  }
}
