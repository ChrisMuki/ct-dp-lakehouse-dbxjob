package ct.dna.lakehouse.framework.internal

import ct.dna.lakehouse.Environment
import ct.dna.lakehouse.spark.SparkConfig._
import ct.dna.utils.LoggingTrait
import org.apache.spark.sql.SparkSession

private[internal] object SparkSessionHandler extends LoggingTrait {

  def newSession() = activeSession.newSession()

  private lazy val activeSession: SparkSession =
    Environment.activeConfig match {
      case Lakehouse(stage)  => SparkSession.builder().appName(s"$stage Lakehouse").getOrCreate()
      case Staging(stage)    => SparkSession.builder().appName(s"$stage Staging").getOrCreate()
      case Sandbox(stage, _) => SparkSession.builder().appName(s"$stage Sandbox").getOrCreate()
      case LocalSpark(host)  => SparkSession.builder().remote(s"sc://$host").getOrCreate()
      case RemoteSandbox(_, _, workspaceUrl, clusterId, pat) =>
        SparkSession.builder().remote(s"sc://$workspaceUrl:443/;token=$pat;x-databricks-cluster-id=$clusterId").getOrCreate()
    }
}
