package ct.dna.lakehouse.srGenerator

import java.util.concurrent.Executors

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Failure
import scala.util.Success

import ct.dna.lakehouse.core.framework.internal.CatalogInternalDelegate
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits._
import ct.dna.utils.LocalDir
import ct.dna.utils.logging.LoggingTrait
import ct.dna.utils.runtime.Configuration

object Generator extends LoggingTrait {

  private final val JobName = "GenerateSourceCatalog"

  def main(args: Array[String]): Unit = {

    Thread.currentThread().setName(JobName)
    logger.info(s"Starting $JobName")

    val config = Configuration
      .optional(Configuration.CONFIGFILE, "generateSourceCatalog.json")
      .withSparkConfig
      .required("baseDir")
      .required("basePackage")
      .required("theobaldJson")
      .required("skipUnusedColumnMod")
      .optional("parallelism", "8")
      .build(args)

    val baseDirProp = config.getProperty("baseDir")
    val basePackage = config.getProperty("basePackage")
    val theobaldJsonPath = config.getProperty("theobaldJson")
    val skipUnusedColumnMod = config.getProperty("skipUnusedColumnMod").toBoolean
    val parallelism = config.getProperty("parallelism").toInt
    require(parallelism > 0, s"parallelism must be > 0, got $parallelism")

    val sparkConfig = config.getSparkConfig
    SparkEnv.ensureInitialized(sparkConfig)

    val baseDir = LocalDir(baseDirProp)
    val srInputsByTable = theobald.loadAndParse(theobaldJsonPath)

    try {
      // 1. Sequential discovery
      val tableFQNsById = discoverTableFQNs(SourceRawCatalogId)
      val rawSchemaIds = tableFQNsById.keySet.map(_.schemaId)
      val srSchemaIds = srInputsByTable.keysIterator
        .filter(tableFQNsById.contains)
        .map(rid => theobald.srTableIdFor(rid).schemaId)
        .toSet

      // 2. Pre-create catalog/schema scaffolding (sequential, single-threaded mkdir)
      val rawCatDir = prepareCatalogDir(baseDir, basePackage, SourceRawCatalogId, rawSchemaIds)
      val srCatDir = prepareCatalogDir(baseDir, basePackage, SourceCatalogId, srSchemaIds)

      // 3. Parallel per-table: SparkEnv is initialized once per worker thread via the ThreadFactory.
      val threadCounter = new java.util.concurrent.atomic.AtomicInteger(0)
      val threadFactory = new java.util.concurrent.ThreadFactory {
        override def newThread(r: Runnable): Thread = {
          val t = new Thread(
            { () =>
              SparkEnv.ensureInitialized(sparkConfig)
              r.run()
            }: Runnable,
            s"$JobName-worker-${threadCounter.incrementAndGet()}"
          )
          t.setDaemon(true)
          t
        }
      }
      val pool = Executors.newFixedThreadPool(parallelism, threadFactory)
      implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(pool)
      val total = tableFQNsById.size
      val counter = new java.util.concurrent.atomic.AtomicInteger(0)

      val futures = tableFQNsById.toSeq.map { case (tableId, fqn) =>
        Future {
          val tableDesc = CatalogInternalDelegate.readTableDesc(fqn)
          val result = processTableDesc(
            basePackage,
            rawCatDir,
            srCatDir,
            tableId,
            tableDesc,
            srInputsByTable.get(tableId),
            skipUnusedColumnMod
          )
          val done = counter.incrementAndGet()
          if (done % 50 == 0 || done == total) logger.info(s"Processed $done/$total tables")
          tableId -> result
        }
      }

      val results =
        try Await.result(Future.sequence(futures), Duration.Inf)
        finally pool.shutdown()

      // 4. Orphan cleanup based on discovered ids
      val srTableIds = srInputsByTable.keysIterator
        .filter(tableFQNsById.contains)
        .map(theobald.srTableIdFor)
        .toSet
      deleteOrphans(rawCatDir, rawSchemaIds, tableFQNsById.keySet)
      deleteOrphans(srCatDir, srSchemaIds, srTableIds)

      // 5. Aggregated failure reporting
      val failures = results.collect { case (tid, Failure(ex)) => tid -> ex }
      val successes = results.count { case (_, r) => r.isInstanceOf[Success[_]] }
      logger.info(s"$JobName finished: $successes succeeded, ${failures.size} failed")
      failures.foreach { case (tid, ex) => logger.error(s"Table '$tid' failed: ${ex.getMessage}") }
      if (failures.nonEmpty) {
        val summary = failures.map { case (tid, ex) => s"  - $tid: ${ex.getMessage}" }.mkString("\n")
        throw new IllegalStateException(s"${failures.size} table(s) failed (others were persisted):\n$summary")
      }
    } catch {
      case ex: Exception =>
        logger.fatal(s"$JobName failed", ex)
        throw ex
    }

    logger.info("DONE")
  }
}
