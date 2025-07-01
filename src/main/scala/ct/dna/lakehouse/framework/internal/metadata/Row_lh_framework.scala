package ct.dna.lakehouse.framework.internal.metadata

import ct.dna.lakehouse.dataframeprovider.ChangeFeedTable
import ct.dna.lakehouse.dataframeprovider.TargetTable
import ct.dna.utils.json.mapper
import org.apache.spark.sql.functions._

private[internal] object Row_lh_framework {
  val columnName = "_lh_framework"
  val udfName = "update_lh_framework"

  def upgrade(target: String, source: String, expected: String): String =
    if (target == expected) source else throw new RuntimeException(s"can not upgrade to ${source} - found ${target} expected ${expected}")

  val metadataUDF = udf((target: String, source: String, expected: String) => upgrade(target, source, expected))

}

private[internal] case class Row_lh_framework(changeFeedVersions: Map[String, ChangeFeedTable.Version], targetVersion: TargetTable.Version) {
  def asValue: String = mapper.writeValueAsString(this)
}
