package ct.dna.lakehouse
import ct.dna.lakehouse.spark.SparkConfig
import ct.dna.utils.Configuration
import ct.dna.utils.LoggingTrait
import ct.dna.utils.json.mapper

object Environment extends LoggingTrait {

  private var initialized = false

  def initializeAndValidate(): Unit = activeConfig

  lazy val activeConfig: SparkConfig = {
    Configuration.required("SparkConfig").initializeAndValidate(Array.empty)
    mapper.readValue[SparkConfig](Configuration.getProperty("SparkConfig"))
  }
}
