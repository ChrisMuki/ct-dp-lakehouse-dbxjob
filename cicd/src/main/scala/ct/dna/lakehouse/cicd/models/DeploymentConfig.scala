package ct.dna.lakehouse.cicd.models

import ct.dna.utils.az.auth.AzAuth

case class DeploymentConfig(
    host: String,
    /** Unity Catalog catalog backing the Databricks volume for job resources. Defaults to "lakehouse" for prod, "<stage>_lakehouse" for other stages.
      */
    volumeCatalog: Option[String] = None,
    /** Schema name in the Unity Catalog volume path. Defaults to "default" when not set.
      */
    volumeSchema: String = "default",
    /** Azure authentication for the Databricks CLI during deployment. Supports AzAuth subtypes: ClientSecret (CI/CD), AzureCli (local dev), ManagedIdentity.
      * Deserialized from JSON automatically by the AzAuth Jackson deserializer.
      */
    deploymentAzAuth: AzAuth,

    /** Cluster settings. Entire block is optional — all fields have sensible defaults. Only override what differs from the defaults for your environment.
      */
    clusterConfiguration: DeploymentConfig.ClusterConfiguration = DeploymentConfig.ClusterConfiguration(),
    /** Optional cron schedule for the job. If absent the job is unscheduled (triggered manually or via API).
      */
    schedule: Option[DeploymentConfig.ScheduleConfig] = None,
    /** "production" (default) or "development". In development mode the Databricks CLI ignores run_as and prefixes the job name with [dev <username>], so the
      * job runs as the deploying identity (PAT owner or OAuth-M2M SP).
      */
    targetMode: String = "production",
    /** Optional list of permissions applied at the target level (i.e. to all resources in the bundle). Each entry grants a group a specific permission level
      * (e.g. CAN_MANAGE_RUN). When empty, no permissions block is emitted.
      */
    permissions: List[DeploymentConfig.PermissionConfig] = Nil
)

object DeploymentConfig {

  case class ClusterConfiguration(
      /** Databricks Runtime version. Default: latest LTS Scala 2.13 runtime. */
      sparkVersion: String = "17.3.x-scala2.13",
      /** Cluster policy ID. When absent no policy is applied. */
      clusterPolicyId: Option[String] = None,
      /** Maximum autoscale worker count. Default: 4. */
      maxWorkerNodes: Int = 4,
      /** Worker node VM size. Default: Standard_D8ds_v5. */
      nodeTypeId: String = "Standard_D8ds_v5",
      /** Driver node VM size. Default: Standard_D8ds_v5. */
      driverNodeTypeId: String = "Standard_D8ds_v5"
  )

  /** Quartz-based cron schedule for the Databricks job. quartzCronExpression — e.g. "0 0 * ? * *" for every hour on the hour. timezoneId — IANA timezone,
    * defaults to "UTC". pauseStatus — "UNPAUSED" (run) or "PAUSED" (skip). Defaults to "UNPAUSED".
    */
  case class ScheduleConfig(
      quartzCronExpression: String,
      timezoneId: String = "UTC",
      pauseStatus: String = "UNPAUSED"
  )

  /** Permission entry for the Databricks Asset Bundle target. groupName — display name of the Databricks group. level — valid values: "CAN_MANAGE", "CAN_RUN",
    * "CAN_VIEW".
    */
  case class PermissionConfig(
      groupName: String,
      level: String
  )
}
