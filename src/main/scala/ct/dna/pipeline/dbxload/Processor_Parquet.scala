package ct.dna.pipeline.dbxload
import com.azure.core.credential.TokenCredential
import ct.dna.pipeline.workorder.InvalidMessageContentError
import ct.dna.pipeline.workorder.TablesUpdateFailed
import ct.dna.pipeline.workorder.TablesUpdated
import ct.dna.pipeline.workorder.WorkOrder.DBXLoadOrder
import ct.dna.utils.Union

object Processor_Parquet {

  def processParquets(
      dbxloadOrder: DBXLoadOrder,
      token: TokenCredential,
      dequeueCount: Long
  ): Union.U3[TablesUpdated, TablesUpdateFailed, InvalidMessageContentError] = {

    // Identify all parquet files
    // map to table in volume
    // for each:
    // - create spark clone
    // - make proper user meta data
    // - add row count
    // - call the fancy merge logic
    // - collect success/failure do to version/otherfailure
    // -
    // -
    // -
    ???
  }
}
