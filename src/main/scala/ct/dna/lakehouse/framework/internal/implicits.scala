package ct.dna.lakehouse.framework.internal

import java.sql.Timestamp

import ct.dna.lakehouse.dataframeprovider.Commit
import ct.dna.lakehouse.framework.internal.FQTN.PATH
import ct.dna.lakehouse.framework.internal.FQTN.TABLE
import ct.dna.lakehouse.framework.internal.metadata.Row_lh_framework
import ct.dna.lakehouse.framework.internal.metadata.UserMetadata
import ct.dna.lakehouse.framework.internal.metadata.UserMetadata.INGEST
import ct.dna.lakehouse.framework.internal.metadata.UserMetadata.MERGE
import ct.dna.lakehouse.framework.internal.metadata.UserMetadata.OPTIMIZE
import ct.dna.lakehouse.framework.internal.metadata.UserMetadata.STRUCTURE_CHANGE
import ct.dna.utils.LoggingTrait
import io.delta.tables.DeltaTable
import org.apache.spark.sql.DataFrameReader
import org.apache.spark.sql.DataFrameWriter
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.LongType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.TimestampType

object implicits extends LoggingTrait {

  private[internal] implicit class DataFrameWriterExtension[T](dfw: DataFrameWriter[T]) {
    def saveAsFQTN(fqtn: FQTN) = fqtn match {
      case PATH(value)  => dfw.save(value)
      case TABLE(value) => dfw.saveAsTable(value)
    }
  }
  private[internal] implicit class DataFrameReaderExtension(dfr: DataFrameReader) {
    def forFQTN(fqtn: FQTN) = fqtn match {
      case PATH(value)  => dfr.load(value)
      case TABLE(value) => dfr.table(value)
    }
  }
  private[internal] implicit class DeltaTableExtension(dt: DeltaTable.type) {
    def forFQTN(spark: SparkSession, fqtn: FQTN) = fqtn match {
      case PATH(value)  => dt.forPath(spark, value)
      case TABLE(value) => dt.forName(spark, value)
    }
  }

  // private[internal] implicit class DataframeReaderExtension(dfr: DataFrameReader) {
  //   def fromFQTN(fqtn: FQTN) = fqtn match {
  //     case PATH(value)  => dfr.load(value)
  //     case TABLE(value) => dfr.table(value)
  //   }
  // }

  private[internal] implicit class SparkExtensions(spark: SparkSession) {
    def setUserMetadata(userMetadata: UserMetadata): Unit =
      spark.conf.set("spark.databricks.delta.commitInfo.userMetadata", userMetadata.format)

    def readTargetDeltaHistory(targetDeltaTable: DeltaTable) = {
      logger.warn("As hint, here we could calculate if we need to recompte a table. How? The process that touches the table, can use UserMetadata")

      val row = targetDeltaTable.history(1).first()
      val umd = UserMetadata.parse(row.getAs[String]("userMetadata"))
      val lastTargetCommit = Commit(row.getAs[Long]("version"), row.getAs[Timestamp]("timestamp"))

      val (newInitCommit, lh_framework): (Commit, Row_lh_framework) = umd match {
        case INGEST(id) => throw new UnsupportedOperationException("INGEST not allowed tor TargetTable")
        case MERGE(id, changeFeedVersions, targetVersion, lastUpdates) =>
          (targetVersion.init, Row_lh_framework(changeFeedVersions, targetVersion))
        case STRUCTURE_CHANGE(id, changeFeedVersions, targetVersion) =>
          (lastTargetCommit, Row_lh_framework(changeFeedVersions, targetVersion))
        case OPTIMIZE => {
          val nonOptimizeRow = targetDeltaTable
            .history()
            // .filter(s"version <= ${lastTargetCommit.version}")
            .agg(max(when(!col("userMetadata").startsWith(UserMetadata.OPTIMIZE.prefix), struct("version", "timestamp", "userMetadata"))).as("__agg"))
            .select("__agg.*")
            .first()
          UserMetadata.parse(nonOptimizeRow.getAs[String]("userMetadata")) match {
            case INGEST(id) => throw new UnsupportedOperationException("INGEST not allowed tor TargetTable")
            case MERGE(id, changeFeedVersions, targetVersion, lastUpdates) =>
              (targetVersion.init, Row_lh_framework(changeFeedVersions, targetVersion))
            case OPTIMIZE => throw new UnsupportedOperationException("OPTIMIZE already filtered out")
            case STRUCTURE_CHANGE(id, changeFeedVersions, targetVersion) =>
              (lastTargetCommit, Row_lh_framework(changeFeedVersions, targetVersion))
          }
        }
      }
      (lastTargetCommit, newInitCommit, lh_framework)
    }

    def readChangeFeedVersionAfter(fqtn: FQTN, known: Commit): (Commit, Commit) = {
      val dt = DeltaTable.forFQTN(spark, fqtn)
      val df = dt
        .history()
        .filter(s"version >= ${known.version}")
        .agg(
          min(struct("version", "timestamp")).as("__from"),
          max(when(col("userMetadata").startsWith(UserMetadata.STRUCTURE_CHANGE.prefix), struct("version", "timestamp"))).as("__structure_change"),
          max(struct("version", "timestamp")).as("__to"),
          max(when(!col("userMetadata").startsWith(UserMetadata.OPTIMIZE.prefix), struct("version", "timestamp"))).as("__nonOptimize")
        )

      if (df.count() > 0) {
        val r = df.first()
        if (r.getAs[Row]("__structure_change") == null)
          (
            Commit(r.getAs[Long]("__from.version"), r.getAs[Timestamp]("__from.timestamp")),
            Commit(r.getAs[Long]("__to.version"), r.getAs[Timestamp]("__to.timestamp"))
          )
        else
          (
            Commit(r.getAs[Long]("__structure_change.version"), r.getAs[Timestamp]("__structure_change.timestamp")),
            Commit(r.getAs[Long]("__to.version"), r.getAs[Timestamp]("__to.timestamp"))
          )
      } else {
        val r = dt.history(1).first()
        val c = Commit(r.getAs[Long]("version"), r.getAs[Timestamp]("timestamp"))
        (c, c)
      }
    }

    //   def readLastDeltaHistory(fqtn: String): DeltaHistory = parseDeltaHistory(
    //     spark.sql(s"DESCRIBE HISTORY $fqtn LIMIT 1").first()
    //   )

    //   def readFirstDeltaHistory(fqtn: String): DeltaHistory = parseDeltaHistory(
    //     spark.sql(s"DESCRIBE HISTORY $fqtn ").agg(min(struct("version", "timestamp", "operation", "userMetadata")).as("first")).select("first.*").first()
    //   )

    // def readDF(fqtn: String) = spark.read.format("delta").table(fqtn)
    def readDF(fqtn: FQTN, versionAsOf: Long) = spark.read.format("delta").option("versionAsOf", versionAsOf).forFQTN(fqtn)

    /**   - startingVersion > endingVersion => org.apache.spark.SparkException: [INTERNAL_ERROR] Cannot find main error class 'DELTA_INVALID_CDC_RANGE'
      *     SQLSTATE: XX000
      *   - startingVersion = -1 => org.apache.spark.SparkException: com.databricks.sql.transaction.tahoe.DeltaFileNotFoundException: [DELTA_EMPTY_DIRECTORY]
      *   - startingVersion > actualVersion => org.apache.spark.SparkIllegalArgumentException: Provided Start version(startingVersion) for reading change data
      *     is invalid. Start version cannot be greater than the latest version of the table(actualVersion).
      */

    def emptyCDF(fqtn: FQTN, versionAsOf: Long) = if (false) {
      spark.read
        .format("delta")
        .option("readChangeFeed", "true")
        .option("startingVersion", versionAsOf)
        .option("endingVersion", versionAsOf)
        .forFQTN(fqtn)
    } else
      readDF(fqtn, versionAsOf)
        .limit(0)
        .withColumn("_change_type", lit(null).cast(StringType))
        .withColumn("_commit_version", lit(null).cast(LongType))
        .withColumn("_commit_timestamp", lit(null).cast(TimestampType))

    def readCDF(fqtn: FQTN, startingVersion: Long, endingVersion: Long) = {
      // we need to choose a proper option for the empty dataset
      if (startingVersion > endingVersion) { emptyCDF(fqtn, endingVersion) }
      else {
        spark.read
          .format("delta")
          .option("readChangeFeed", "true")
          .option("startingVersion", startingVersion)
          .option("endingVersion", endingVersion)
          .forFQTN(fqtn)
      }
    }

    def initCDF(fqtn: FQTN, initComit: Commit, endingVersion: Long) =
      readDF(fqtn, initComit.version)
        .withColumn("_change_type", lit("initial"))
        .withColumn("_commit_version", lit(initComit.version))
        .withColumn("_change_type", lit(initComit.timeStamp))
        .union(readCDF(fqtn, initComit.version + 1, endingVersion))
  }

}
