package ct.dna.lakehouse.deploy.model

import scala.annotation.meta.getter

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.SerializationFeature
import ct.dna.lakehouse.core.lakehousejob.config.OrchestratorConfig
import ct.dna.lakehouse.core.lakehousejob.config.SummaryConfig
import ct.dna.lakehouse.core.runtime.SparkConfig
import ct.dna.utils.json.mapper

/** The per-catalog runtime config written to the volume as `config-<catalog>.json`. `JobSetup` loads it through `configFile=<path>` and resolves everything it
  * needs from it — the catalog identity, the in-JVM worker count, the per-task orchestrator/summary configs and the Spark config — so the only runtime task
  * argument left is the Databricks-resolved `runId`.
  *
  * `fileName` is local staging metadata (the volume path of this file), not part of the serialized payload — the `@getter`-targeted `@JsonIgnore` keeps it off
  * the JSON Jackson reads through the case-class getter.
  */
final case class ConfigFile(
    @(JsonIgnore @getter) fileName: String,
    catalogClass: String,
    workerCount: Int,
    orchestratorConfig: OrchestratorConfig,
    summaryConfig: SummaryConfig,
    sparkConfig: SparkConfig
) extends AsFile {

  def content: Array[Byte] =
    mapper
      .cloneRaw()
      .enable(SerializationFeature.INDENT_OUTPUT)
      .writeValueAsBytes(this)
}
