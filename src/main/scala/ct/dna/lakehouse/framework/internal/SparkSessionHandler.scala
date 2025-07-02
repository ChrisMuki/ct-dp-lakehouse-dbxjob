package ct.dna.lakehouse.framework.internal

import ct.dna.lakehouse.Environment
import ct.dna.lakehouse.spark.SparkConfig._
import ct.dna.utils.LoggingTrait
import org.apache.spark.sql.SparkSession

private[internal] object SparkSessionHandler extends LoggingTrait {

  def newSession() = activeSession.newSession()

  private lazy val activeSession: SparkSession = {
    val builder = Environment.activeConfig match {
      case LocalSpark(master, catalogPath) =>
        SparkSession
          .builder()
          .appName("LocalSpark")
          .master(master)
          .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
          .config("spark.sql.catalog.spark_catalog", "org.apache.spark.sql.delta.catalog.DeltaCatalog")
          .config("spark.sql.warehouse.dir", catalogPath)

      case RemoteSandbox(_, _, workspaceUrl, clusterId, pat) =>
        SparkSession.builder().remote(s"sc://$workspaceUrl:443/;token=$pat;x-databricks-cluster-id=$clusterId")

      case Lakehouse(stage)  => SparkSession.builder().appName(s"$stage Lakehouse")
      case Staging(stage)    => SparkSession.builder().appName(s"$stage Staging")
      case Sandbox(stage, _) => SparkSession.builder().appName(s"$stage Sandbox")

    }
    builder.getOrCreate()
  }
}
