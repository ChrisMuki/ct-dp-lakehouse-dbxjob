package ct.dna.lakehouse.framework.internal

import java.sql.Timestamp

import ct.dna.lakehouse.framework.ChangeFeedVersion
import ct.dna.lakehouse.framework.DeltaHistory
import ct.dna.lakehouse.framework.DeltaHistory.INGDeltaHistory
import ct.dna.lakehouse.framework.DeltaHistory.LakehouseDeltaHistory
import ct.dna.lakehouse.framework.DeltaHistory.SimpleDeltaHistory
import ct.dna.lakehouse.framework.EnvironmentConfig
import ct.dna.lakehouse.framework.UserMetadata.LakehouseMetadata
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.ChangeFeedTable
import ct.dna.lakehouse.transformations.Origin
import ct.dna.lakehouse.transformations.Origin.Transformation
import ct.dna.lakehouse.transformations.TargetTable
import ct.dna.utils.LoggingTrait
import ct.dna.utils.json.mapper
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.LongType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.TimestampType
case class TransformationExecutor(environment: EnvironmentConfig) extends LoggingTrait {

  private val spark: SparkSession = SparkBuilder.newSession()

  spark.udf.register("update_lh_metadata", LH_Metadata.metadataUDF)

  def execute(transformation: Transformation) = synchronized {

    val target_fqtn = CatalogAccess.target_fqtn(transformation.table)

    transformation match {
      case tt: Origin.TwoTransactions => {
        logger.warn("Monitoring not integrated yet")
        val (t1, s1) = prepareTransaction(target_fqtn, tt.changeFeedsOne)
        val r1 = tt.executeTransactionOne(spark.implicits, t1, s1)
        logger.warn("Monitoring not integrated yet")
        val (t2, s2) = prepareTransaction(target_fqtn, tt.changeFeedsTwo)
        val r2 = tt.executeTransactionTwo(spark.implicits, t2, s2, s1.map { case (d, st) => d -> SnapshotTableImpl(st) }) //
        logger.warn("Monitoring not integrated yet")
      }
      case tt: Origin.OneTransaction => {
        logger.warn("Monitoring not integrated yet")
        val (t, s) = prepareTransaction(target_fqtn, tt.changeFeeds)
        val r = tt.executeTransaction(spark.implicits, t, s)
        logger.warn("Monitoring not integrated yet")
      }
    }
  }

  def prepareTransaction(target_fqtn: String, sourceDefs: Seq[Table]): (TargetTable, Map[Table, ChangeFeedTable]) = {

    logger.warn("We can not yet identify if a TargetTable must be completly recomputed. Hint, do this by only providing Snapshots instead of ChangeFeeds")
    // We can check target_DeltaHistory.metadata.version.to and target_DeltaHistory.version... When they are different, someone modified the table
    val (target_Version, sourceVersions) = spark.readTargetDeltaHistory(target_fqtn) match {
      case LakehouseDeltaHistory(version, timestamp, operation, readVersion, metadata) => (version, metadata.sourceVersions)
      case INGDeltaHistory(version, timestamp, operation, readVersion, metadata)       => ???
      case SimpleDeltaHistory(version, timestamp, operation, readVersion, metadata)    => (version, Map.empty[String, ChangeFeedVersion])
    }

    val sourceTables = sourceDefs.map(sourceDef => sourceDef -> buildCFTable(sourceDef, sourceVersions)).toMap

    logger.warn("someId not yet implemented")
    logger.warn("operation not yet implemented")

    val someId: String = "SomeID"
    val operation: String = "SomeOperation"

    val newMetadata = LakehouseMetadata(
      someId,
      operation,
      sourceVersions ++ sourceTables.values.map(cft => (cft.fqtn, cft.version)),
      target_Version + 1
    )

    val targetTable = TargetTableImpl(spark, target_fqtn, spark.readDF(target_fqtn, target_Version), newMetadata)

    spark.setUserMetadata(targetTable.metadata)
    (targetTable, sourceTables)
  }

  def buildCFTable(sourceDef: Table, sourceVersions: Map[String, ChangeFeedVersion]): ChangeFeedTable = {

    val source_fqtn = CatalogAccess.source_fqtn(sourceDef)
    val knownVersion = sourceVersions.getOrElse(source_fqtn, ChangeFeedVersion(-1, -1, -1))

    val ((earliest, earliestTS), to) = spark.readChangeFeedVersion(source_fqtn, knownVersion.to)
    if (to == knownVersion.to) {
      // nothing has changed at all
      ChangeFeedTableImpl(
        spark.implicits,
        sourceDef.keys.map(t => t._1),
        source_fqtn,
        spark.readDF(source_fqtn, to),
        spark.emptyCDF(source_fqtn, to),
        ChangeFeedVersion(knownVersion.snapshot, to, to),
        false
      )
    } else if (earliest <= knownVersion.to) {
      // here we can process CDF correct
      ChangeFeedTableImpl(
        spark.implicits,
        sourceDef.keys.map(t => t._1),
        source_fqtn,
        spark.readDF(source_fqtn, to),
        spark.readCDF(source_fqtn, knownVersion.to + 1, to),
        ChangeFeedVersion(knownVersion.snapshot, knownVersion.to, to),
        false
      )
    } else {
      // here we do snapshot
      ChangeFeedTableImpl(
        spark.implicits,
        sourceDef.keys.map(t => t._1),
        source_fqtn,
        spark.readDF(source_fqtn, to),
        spark.initCDF(source_fqtn, earliest, earliestTS, to),
        ChangeFeedVersion(earliest, earliest, to),
        true
      )

    }

  }

  implicit class SparkExtensions(spark: SparkSession) {
    def setUserMetadata(userMetadata: LakehouseMetadata): Unit =
      spark.conf.set("spark.databricks.delta.commitInfo.userMetadata", mapper.writeValueAsString(userMetadata))

    def readTargetDeltaHistory(fqtn: String): DeltaHistory = {
      logger.warn("As hint, here we could calculate if we need to recompte a table. How? The process that touches the table, can use UserMetadata")
      val row = spark.sql(s"DESCRIBE HISTORY $fqtn LIMIT 1").first()
      val umd = row.getAs[String]("userMetadata")
      if (umd == "STRUCTURE_CHANGED") {

        SimpleDeltaHistory(
          row.getAs[Long]("version"),
          row.getAs[java.sql.Timestamp]("timestamp"),
          row.getAs[String]("operation"),
          row.getAs[Long]("readVersion"),
          umd
        )

      } else
        LakehouseDeltaHistory(
          row.getAs[Long]("version"),
          row.getAs[java.sql.Timestamp]("timestamp"),
          row.getAs[String]("operation"),
          row.getAs[Long]("readVersion"),
          mapper.readValue[LakehouseMetadata](umd)
        )

    }

    def readChangeFeedVersion(fqtn: String, startingVersion: Long): ((Long, Timestamp), Long) = {
      val r = spark
        .sql(s"DESCRIBE HISTORY $fqtn ")
        .filter(s"version >= $startingVersion")
        .agg(
          min(struct("version", "timestamp")).as("from"),
          max(when(expr("version = 'STRUCTURE_CHANGED'"), struct("version", "timestamp"))).as("structureChange"),
          max("version").as("to")
        )
        .first()
      if (r.getAs[Row]("structureChange") == null)
        ((r.getAs[Long]("from.version"), r.getAs[Timestamp]("from.timestamp")), r.getAs[Long]("to"))
      else
        ((r.getAs[Long]("structureChange.version"), r.getAs[Timestamp]("structureChange.timestamp")), r.getAs[Long]("to"))

    }

    //   def readLastDeltaHistory(fqtn: String): DeltaHistory = parseDeltaHistory(
    //     spark.sql(s"DESCRIBE HISTORY $fqtn LIMIT 1").first()
    //   )

    //   def readFirstDeltaHistory(fqtn: String): DeltaHistory = parseDeltaHistory(
    //     spark.sql(s"DESCRIBE HISTORY $fqtn ").agg(min(struct("version", "timestamp", "operation", "userMetadata")).as("first")).select("first.*").first()
    //   )

    // def readDF(fqtn: String) = spark.read.format("delta").table(fqtn)
    def readDF(fqtn: String, versionAsOf: Long) = spark.read.format("delta").option("versionAsOf", versionAsOf).table(fqtn)

    /**   - startingVersion > endingVersion => org.apache.spark.SparkException: [INTERNAL_ERROR] Cannot find main error class 'DELTA_INVALID_CDC_RANGE'
      *     SQLSTATE: XX000
      *   - startingVersion = -1 => org.apache.spark.SparkException: com.databricks.sql.transaction.tahoe.DeltaFileNotFoundException: [DELTA_EMPTY_DIRECTORY]
      *   - startingVersion > actualVersion => org.apache.spark.SparkIllegalArgumentException: Provided Start version(startingVersion) for reading change data
      *     is invalid. Start version cannot be greater than the latest version of the table(actualVersion).
      */

    def emptyCDF(fqtn: String, versionAsOf: Long) = if (false)
      spark.read
        .format("delta")
        .option("readChangeFeed", "true")
        .option("startingVersion", versionAsOf)
        .option("endingVersion", versionAsOf)
        .table(fqtn)
        .limit(0)
    else
      readDF(fqtn, versionAsOf)
        .limit(0)
        .withColumn("_change_type", lit(null).cast(StringType))
        .withColumn("_commit_version", lit(null).cast(LongType))
        .withColumn("_commit_timestamp", lit(null).cast(TimestampType))

    def readCDF(fqtn: String, startingVersion: Long, endingVersion: Long) = {
      // we need to choose a proper option for the empty dataset
      if (startingVersion > endingVersion) { emptyCDF(fqtn, endingVersion) }
      else
        spark.read
          .format("delta")
          .option("readChangeFeed", "true")
          .option("startingVersion", startingVersion)
          .option("endingVersion", endingVersion)
          .table(fqtn)
    }

    def initCDF(fqtn: String, startingVersion: Long, startingTS: java.sql.Timestamp, endingVersion: Long) =
      readDF(fqtn, startingVersion)
        .withColumn("_change_type", lit("initial"))
        .withColumn("_commit_version", lit(startingVersion))
        .withColumn("_change_type", lit(startingTS))
        .union(readCDF(fqtn, startingVersion + 1, endingVersion))
  }
}
