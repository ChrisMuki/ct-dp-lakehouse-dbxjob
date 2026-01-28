package ct.dna.catalog.demo.utils

import java.io.File
import org.apache.spark.sql.SparkSession
import com.typesafe.config.{Config, ConfigFactory}

/** Common utilities for demo applications */
object DemoUtils {

  // Load configuration
  private val config: Config = ConfigFactory.load("demo.conf")

  /** Get the test data path from configuration
    *
    * @return
    *   Absolute path to test data directory
    */
  def getTestDataPath(): String = {
    val testDataPath = config.getString("demo.test-data.path")

    // Try different possible locations based on working directory
    val possiblePaths = Seq(
      // When running from module directory (sbt)
      s"src/main/resources/$testDataPath",
      // When running from project root (IDE)
      s"libs/excel-catalog-demo/src/main/resources/$testDataPath",
      // When running from libraries directory
      s"excel-catalog-demo/src/main/resources/$testDataPath"
    ).map(p => new File(p))

    // Find the first existing path
    possiblePaths.find(_.exists()) match {
      case Some(path) =>
        val absolutePath = path.getAbsolutePath
        println(s"Using test data from: $absolutePath")
        absolutePath
      case None       =>
        val currentDir = new File(".").getAbsolutePath
        throw new RuntimeException(
          s"Test data not found!\n" +
            s"Current working directory: $currentDir\n" +
            s"Tried paths:\n${possiblePaths.map(p => s"  - ${p.getAbsolutePath}").mkString("\n")}\n" +
            s"Please ensure test data exists at: src/main/resources/$testDataPath"
        )
    }
  }

  /** Print a demo header with title */
  def printDemoHeader(demoNumber: String, title: String): Unit = {
    println("=" * 80)
    println(s"Demo $demoNumber: $title")
    println("=" * 80)
    println()
  }

  /** Print a section header */
  def printSection(title: String, count: Option[Int] = None, itemLabel: String = "items"): Unit = {
    println("-" * 80)
    count match {
      case Some(n) => println(s"$title ($n $itemLabel)")
      case None    => println(title)
    }
    println("-" * 80)
  }

  /** Print a subsection header */
  def printSubSection(title: String): Unit = {
    println(s"\n$title:")
    println("~" * title.length)
  }

  /** Format a list of items with max display */
  def formatList(items: Seq[String], maxItems: Int = 5, separator: String = ", "): String = {
    if (items.length <= maxItems) {
      items.mkString(separator)
    } else {
      items.take(maxItems).mkString(separator) + "..."
    }
  }

  /** Print key-value pairs with indentation */
  def printKeyValue(key: String, value: String, indent: Int = 2): Unit = {
    val spaces = " " * indent
    println(s"$spaces$key: $value")
  }

  /** Print a demo footer */
  def printDemoFooter(): Unit = {
    println()
    println("=" * 80)
  }

  /** Create a Spark session configured for demos */
  def createDemoSparkSession(appName: String): SparkSession = {
    // Use the demo.conf for Spark settings
    val sparkConfig = config.getConfig("demo.spark")

    val builder = SparkSession.builder()
      .appName(appName)
      .master(sparkConfig.getString("master"))
      .config("spark.ui.enabled", sparkConfig.getBoolean("ui.enabled"))
      .config("spark.sql.adaptive.enabled", sparkConfig.getBoolean("sql.adaptive.enabled"))

    val spark = builder.getOrCreate()
    spark.sparkContext.setLogLevel(sparkConfig.getString("log.level"))
    spark
  }
}
