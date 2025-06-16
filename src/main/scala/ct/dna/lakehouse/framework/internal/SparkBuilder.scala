package ct.dna.lakehouse.framework.internal

import ct.dna.lakehouse.Environment
import ct.dna.lakehouse.framework.EnvironmentConfig._
import ct.dna.utils.LoggingTrait
import org.apache.spark.sql.SparkSession

private[internal] object SparkBuilder extends LoggingTrait {

  def newSession() = activeSession.newSession()
  private lazy val activeSession: SparkSession = {

    val builder = SparkSession.builder()

    val setEnvConfig: SparkSession.Builder => SparkSession.Builder = { builder =>
      Environment.activeConfig match {
        case Lakehouse(stage)  => builder.appName(s"$stage Lakehouse")
        case Staging(stage)    => builder.appName(s"$stage Staging")
        case Sandbox(stage, _) => builder.appName(s"$stage Sandbox")
        case RemoteSandbox(_, uid, workspaceUrl, clusterId, pat) =>
          try {
            val method = builder.getClass.getDeclaredMethod("remote", classOf[String])
            method.invoke(builder, s"sc://$workspaceUrl:443/;token=$pat;x-databricks-cluster-id=$clusterId").asInstanceOf[SparkSession.Builder]
          } catch {
            case e: Exception => logAndThrow(new IllegalStateException("RemoteSandbox is not a valid EnvironmentConfig when not using Spark-Connect", e))
          }
      }
    }
    setEnvConfig(builder).getOrCreate()
  }
}
