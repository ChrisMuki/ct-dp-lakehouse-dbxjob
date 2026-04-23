package ct.dna.lakehouse.core

import ct.dna.utils.LoggingTrait
import ct.dna.utils.deploy.databrickscli.assetbundle._

object HawkWorkflowBuilder extends LoggingTrait {

  private val entryPointClass: String =
    "ct.dna.lakehouse.core.jobs.TableUpdaterEntryPoint"

  private val dmSchemaPackage: String =
    "ct.dna.lakehouse.dm_md.fin_hawk"

  private val jobName: String = "dm_hawk_job"

  def buildJobs(
      jobCluster: JobCluster,
      jarPath: String,
      configFilePath: String
  ): Map[String, Job] = {

    val dmTableDeps = DmDependencyResolver.resolve(dmSchemaPackage)
    logger.info(s"Discovered ${dmTableDeps.size} DM tables in $dmSchemaPackage")

    val tasks = dmTableDeps.map { case (table, deps) =>
      Task(
        taskKey = table,
        dependsOn = deps.map(d => DependsRef(taskKey = d)).toList,
        sparkJarTask = Some(
          SparkJarTask(
            mainClassName = entryPointClass,
            parameters = List(
              s"configFile=${configFilePath}",
              dmSchemaPackage,
              table
            )
          )
        ),
        jobClusterKey = jobCluster.jobClusterKey,
        libraries = List(Library(jar = jarPath)),
        maxRetries = None,
        minRetryIntervalMillis = None
      )
    }.toList

    Map(
      jobName -> Job(
        name = jobName,
        schedule = None,
        continuous = None,
        maxConcurrentRuns = 1,
        tasks = tasks,
        jobClusters = List(jobCluster),
        queue = Queue(enabled = true),
        runAs = null,
        tags = null
      )
    )
  }
}
