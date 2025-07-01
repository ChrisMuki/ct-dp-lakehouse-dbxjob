package ct.dna.lakehouse.dataframeprovider
import org.apache.spark.sql.DataFrame

object ChangeFeedTable {
  case class Version(init: Commit, current: Commit)
}
trait ChangeFeedTable {
  val fqtn: String
  def version: ChangeFeedTable.Version
  def isSnapshot: Boolean

  /** Does not contain the '_lh_framework' Row and Column!
    */
  def getSnapshot: DataFrame

  /** Return Change Data Feed of table, starting from version already included in TargetTable. Whenever the Framework identifies the need to fully recompute
    * from this ChangeFeedTable, i.e. 'isSnapshot = true', this ChangeFeed will include a _change_type 'initial'. It has following additional Columns
    *   1. _change_type: ['initial', 'insert', 'update_preimage', 'update_postimage', 'delete']
    *   1. _commit_version: Long
    *   1. _commit_timestamp: TimeStamp
    */
  def getChangeFeed: DataFrame

  /** Aggregates the ChangeFeed based on the keys of the table. It provides the following additional columns additional Columns
    *   1. _change_type: ['delete', 'initial', 'insert', 'update_postimage']
    *   1. _commit_version: Long
    *   1. _commit_timestamp: TimeStamp
    */
  def getChangeFeed_last: DataFrame

  /** Return the first and the the last entry in Change Data Feed based on the keys of the table, starting from version already included in TargetTable. It has
    * all values of the dataframe in two versions. a '__from.*' and '__to.*' version, representins the from and two values. At most one set can be null. It has
    * additional columns
    *   1. __from._change_type: ['delete', 'update_preimage', null]
    *   1. __to._change_type: ['initial', 'insert', 'update_postimage', null]
    *   1. __from._commit_version: Long
    *   1. __to._commit_version: Long
    *   1. __from._commit_timestamp: TimeStamp
    *   1. __to._commit_timestamp: TimeStamp
    *
    * ## Example 1
    *   - Original columns: id,name,age
    *   - keys: id
    *   - Returned columns:
    *     - id,
    *     - __from.name, __from.age, __from._change_type, __from._commit_version, __from._commit_timestamp,
    *     - __to.name, __to.age, __to._change_type, __to._commit_version, __to._commit_timestamp,
    */
  def getChangeFeed_from_to: DataFrame

}
// trait SourceTableWithMetaRow extends ChangeFeedTable
