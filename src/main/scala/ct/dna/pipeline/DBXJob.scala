package ct.dna.pipeline
import ct.dna.pipeline.dbxload.DBXLoadWorker
import ct.dna.utils.Configuration
import ct.dna.utils.az.token.withCredentialBuilder
import ct.dna.utils.json.mapper
import ct.dna.worker.WorkerConfig
import ct.dna.worker.WorkerConfig.DBXLoadConfig
import ct.dna.worker.WorkerConfig.IngestConfig
import ct.dna.worker.WorkerConfig.MonitoringConfig
import ct.dna.worker.WorkerConfig.ParquetfyConfig
import ct.dna.worker.WorkerRuntime

object DBXJob extends WorkerRuntime {

  def main(args: Array[String]): Unit = {

    Configuration
      .setName("Worker")
      .required("WorkerConfig")
      .withCredentialBuilder
      .initializeAndValidate(args)

    logger.debug("parse configuration")
    val worker = mapper.readValue[WorkerConfig](Configuration.getProperty("WorkerConfig")) match {
      case config: DBXLoadConfig    => new DBXLoadWorker(this, config)
      case config: MonitoringConfig => throw new IllegalArgumentException("DSFApp can not execute MonitoringWorker")
      case config: ParquetfyConfig  => throw new IllegalArgumentException("DSFApp can not execute ParquetfyWorker")
      case config: IngestConfig     => throw new IllegalArgumentException("DSFApp can not execute IngestWorker")
    }

    logger.debug("addShutdownHook")
    scala.sys.addShutdownHook({
      logDuringShutdown("Shutdown initiated...")
      _shutdownInitiated = true
      worker.cleanUpDuringShutdown
      logDuringShutdown("Shutdown finalized!")
    })

    logger.info("Worker started")

    if (worker.config.rate_s > 0) while (!_shutdownInitiated) { startWorkerExecution(worker) }
    else startWorkerExecution(worker)

    logger.info("Final CleanUp")
    worker.cleanUpDuringShutdown
    logger.info("Worker finished")
  }

}
