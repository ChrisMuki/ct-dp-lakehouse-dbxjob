package ct.dna.lakehouse.core

import ct.dna.lakehouse.core.model.CatalogSpec
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.internal.findSchemaSpecs
import ct.dna.lakehouse.core.model.internal.findTableSpecs
import ct.dna.utils.LoggingTrait
import ct.dna.utils.deploy.databrickscli.assetbundle._

object SrWorkflowBuilder extends LoggingTrait {

  private val entryPointClass: String =
    "ct.dna.lakehouse.core.jobs.TableUpdaterEntryPoint"

  def calcJobName(tableSpec: TableSpec[Entity]): String =
    s"sr_${tableSpec.id.schemaId.name}_job"

  def calcTaskName(tableSpec: TableSpec[Entity]): String =
    s"${tableSpec.id.schemaId.name}_${tableSpec.id.name}"

  def buildJobs(
      catalogSpec: CatalogSpec,
      jobCluster: JobCluster,
      jarPath: String,
      configFilePath: String,
      schedule: Option[JobSchedule] = None
  ): Map[String, Job] = {

    val allSchemas = findSchemaSpecs(catalogSpec)
    val allTables = allSchemas.flatMap(findTableSpecs)

    logger.info(s"Discovered ${allSchemas.size} schemas, ${allTables.size} tables in catalog '${catalogSpec.id.name}'")

    allTables
      .groupBy(calcJobName)
      .map { case (jobName, tablesOfJob) =>
        val tasks = tablesOfJob
          .map { tableSpec =>
            Task(
              taskKey = calcTaskName(tableSpec),
              dependsOn = Nil,
              sparkJarTask = Some(
                SparkJarTask(
                  mainClassName = entryPointClass,
                  parameters = List(
                    s"configFile=${configFilePath}",
                    tableSpec.getClass.getPackage.getName,
                    tableSpec.id.name
                  )
                )
              ),
              jobClusterKey = jobCluster.jobClusterKey,
              libraries = List(Library(jar = jarPath)),
              maxRetries = None,
              minRetryIntervalMillis = None
            )
          }
          .sortBy(_.taskKey)
          .toList

        jobName -> Job(
          name = jobName,
          schedule = schedule,
          continuous = None,
          maxConcurrentRuns = 1,
          tasks = tasks,
          jobClusters = List(jobCluster),
          queue = Queue(enabled = true),
          runAs = null,
          tags = null
        )
      }
  }
}
