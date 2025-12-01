package ct.dna.lakehouse.deployment

import ct.dna.utils.spark.SparkHandler
import ct.dna.utils.spark.test.inspectSparkCatalogs
import ct.dna.utils.spark.SparkConfig
import ct.dna.utils.spark.SparkConfig.DevLocal
import ct.dna.utils.spark.SparkConfig.DevLocal.SparkCatalog

object Debug extends App {

  val handler = SparkHandler(
    DevLocal(
      "local[*]",
      // SparkCatalog("/tmp/spark_catalog", false, false),
      DevLocal.FileCatalog("unified", "/tmp/unified", false, false)
    )
  )
  handler.spark.sql("SHOW TABLES IN unified").show(false)
  inspectSparkCatalogs(handler.spark)
  // handler.spark.sql("show catalogs").show(true)

  handler.close()
}
