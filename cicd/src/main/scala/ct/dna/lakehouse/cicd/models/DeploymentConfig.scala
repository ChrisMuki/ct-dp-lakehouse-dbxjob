package ct.dna.lakehouse.cicd.models

case class DeploymentConfig(
    host: String,
    /** Unity Catalog catalog backing the Databricks volume for job resources. Defaults to "ctdp<stage>dbxlakehouse" when absent, e.g. "ctdpdevdbxlakehouse".
      */
    volumeCatalog: Option[String] = None,
    deploymentIdentity: DeploymentConfig.DeploymentIdentity,

    /** Cluster settings. Entire block is optional — all fields have sensible defaults. Only override what differs from the defaults for your environment.
      */
    clusterConfiguration: DeploymentConfig.ClusterConfiguration = DeploymentConfig.ClusterConfiguration(),
    /** Optional path (relative to project root) to a resource YAML file or a directory of resource YAML files to include in the Databricks asset bundle.
      *   - A single file: "resources/lakehouse_job.yml"
      *   - A directory: "resources" (all *.yml / *.yaml files in it are included) If absent, no extra resource files are included.
      */
    resourceFilesPath: Option[String] = None,
    /** Optional cron schedule for the job. If absent the job is unscheduled (triggered manually or via API).
      */
    schedule: Option[DeploymentConfig.ScheduleConfig] = None,
    /** "production" (default) or "development". In development mode the Databricks CLI ignores run_as and prefixes the job name with [dev <username>], so the
      * job runs as the deploying identity (PAT owner or OAuth-M2M SP).
      */
    targetMode: String = "production"
)

object DeploymentConfig {

  /** Selects how the Databricks CLI authenticates during deployment.
    *
    * "azure-client-secret" — Azure-registered service principal. Required fields: tenantId, clientId, clientSecret.
    *
    * "oauth-m2m" — Databricks-native service principal (OAuth M2M). Required fields: clientId, clientSecret.
    *
    * "pat" — Personal access token (local dev / testing only). Required fields: token.
    */
  case class DeploymentIdentity(
      authType: String, // "azure-client-secret" | "oauth-m2m" | "pat"
      tenantId: Option[String] = None, // azure-client-secret only
      clientId: Option[String] = None, // azure-client-secret + oauth-m2m
      clientSecret: Option[String] = None, // azure-client-secret + oauth-m2m
      token: Option[String] = None // pat only
  )

  case class ClusterConfiguration(
      /** Databricks Runtime version. Default: latest LTS Scala 2.13 runtime. */
      sparkVersion: String = "17.3.x-scala2.13",
      /** Cluster policy ID. When absent no policy is applied. */
      clusterPolicyId: Option[String] = None,
      /** Maximum autoscale worker count. Default: 4. */
      maxWorkerNodes: Int = 4,
      /** Worker node VM size. Ignored when instancePoolId is set. Default: Standard_D8ds_v5. */
      nodeTypeId: String = "Standard_D8ds_v5",
      /** Driver node VM size. Ignored when instancePoolId is set. Default: Standard_D8ds_v5. */
      driverNodeTypeId: String = "Standard_D8ds_v5",
      /** Azure instance pool ID. When set, node types are governed by the pool. */
      instancePoolId: Option[String] = None
  )

  /** Quartz-based cron schedule for the Databricks job. quartzCronExpression — e.g. "0 0 * ? * *" for every hour on the hour. timezoneId — IANA timezone,
    * defaults to "UTC". pauseStatus — "UNPAUSED" (run) or "PAUSED" (skip). Defaults to "UNPAUSED".
    */
  case class ScheduleConfig(
      quartzCronExpression: String,
      timezoneId: String = "UTC",
      pauseStatus: String = "UNPAUSED"
  )
}
