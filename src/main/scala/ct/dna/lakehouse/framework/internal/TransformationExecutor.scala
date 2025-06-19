package ct.dna.lakehouse.framework.internal
import ct.dna.lakehouse.framework.EnvironmentConfig
import ct.dna.lakehouse.framework.internal.implicits.SparkExtensions
import ct.dna.lakehouse.framework.internal.metadata.Row_lh_framework
import ct.dna.lakehouse.framework.internal.metadata.UserMetadata
import ct.dna.lakehouse.framework.internal.transformations.ChangeFeedTableImpl
import ct.dna.lakehouse.framework.internal.transformations.SnapshotTableImpl
import ct.dna.lakehouse.framework.internal.transformations.TargetTableImpl
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.ChangeFeedTable
import ct.dna.lakehouse.transformations.Commit
import ct.dna.lakehouse.transformations.Origin
import ct.dna.lakehouse.transformations.Origin.Transformation
import ct.dna.lakehouse.transformations.TargetTable
import ct.dna.utils.LoggingTrait
import io.delta.tables.DeltaTable
import org.apache.spark.sql.SparkSession
private[internal] case class TransformationExecutor(environment: EnvironmentConfig) extends LoggingTrait {

  private val spark: SparkSession = SparkSessionHandler.newSession()

  spark.udf.register(Row_lh_framework.udfName, Row_lh_framework.metadataUDF)

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

  def prepareTransaction(target_fqtn: String, cFDefs: Seq[Table]): (TargetTable, Map[Table, ChangeFeedTable]) = {

    val targetDeltaTable = DeltaTable.forName(spark, target_fqtn)
    val (lastCommit, newInitCommit, last_lh_framework) = spark.readTargetDeltaHistory(targetDeltaTable)

    val cfTables = cFDefs.map(cFDef => cFDef -> buildCFTable(cFDef, last_lh_framework.changeFeedVersions)).toMap

    val someId: String = "SomeID"

    val newChangeFeedVersions = cfTables.values.map(cft => (cft.fqtn, cft.version)).toMap
    val new_lh_framework = Row_lh_framework(
      last_lh_framework.changeFeedVersions ++ newChangeFeedVersions,
      TargetTable.Version(init = newInitCommit, last = lastCommit)
    )

    val targetTable = TargetTableImpl(spark.implicits, target_fqtn, targetDeltaTable, last_lh_framework, new_lh_framework)

    val updatedChangeFeedCommits =
      newChangeFeedVersions.filter { case (t, v) => Some(v.current) != last_lh_framework.changeFeedVersions.get(t) }.map { case (t, v) => (t, v.current) }

    spark.setUserMetadata(
      UserMetadata.MERGE(
        id = { logger.warn("someId not yet implemented"); "SOMEID" },
        changeFeedVersions = new_lh_framework.changeFeedVersions,
        targetVersion = new_lh_framework.targetVersion,
        lastUpdates = updatedChangeFeedCommits
      )
    )
    (targetTable, cfTables)
  }

  def buildCFTable(cFDef: Table, sourceVersions: Map[String, ChangeFeedTable.Version]): ChangeFeedTable = {

    val source_fqtn = CatalogAccess.source_fqtn(cFDef)
    val knownVersion = sourceVersions.get(source_fqtn).getOrElse(ChangeFeedTable.Version(Commit(-1, null), Commit(-1, null)))

    val (earliestUsableCommit, currentCommit) = spark.readChangeFeedVersionAfter(source_fqtn, knownVersion.current)
    if (knownVersion.current.version == currentCommit.version) {
      // nothing has changed at all
      ChangeFeedTableImpl(
        spark.implicits,
        cFDef.keys.map(t => t._1),
        source_fqtn,
        spark.readDF(source_fqtn, currentCommit.version),
        spark.emptyCDF(source_fqtn, currentCommit.version),
        ChangeFeedTable.Version(knownVersion.init, currentCommit),
        false
      )
    } else if (knownVersion.current.version >= earliestUsableCommit.version && !knownVersion.current.timeStamp.before(earliestUsableCommit.timeStamp)) {
      // here we can process CDF correct
      ChangeFeedTableImpl(
        spark.implicits,
        cFDef.keys.map(t => t._1),
        source_fqtn,
        spark.readDF(source_fqtn, currentCommit.version),
        spark.readCDF(source_fqtn, knownVersion.current.version + 1, currentCommit.version),
        ChangeFeedTable.Version(knownVersion.init, currentCommit),
        false
      )
    } else {
      // here we do snapshot
      ChangeFeedTableImpl(
        spark.implicits,
        cFDef.keys.map(t => t._1),
        source_fqtn,
        spark.readDF(source_fqtn, currentCommit.version),
        spark.initCDF(source_fqtn, earliestUsableCommit, currentCommit.version),
        ChangeFeedTable.Version(earliestUsableCommit, currentCommit),
        true
      )

    }
  }

}
