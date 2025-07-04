package ct.dna.lakehouse
import ct.dna.utils.Configuration
import ct.dna.utils.LoggingTrait
import ct.dna.utils.json.mapper
import ct.dna.utils.spark.SparkConfig
import ct.dna.utils.spark._

object Environment extends LoggingTrait {

  private var initialized = false

  def initializeAndValidate(): Unit = activeConfig

  lazy val activeConfig: SparkConfig = {
    Configuration.withSparkConfig.initializeAndValidate()
    mapper.readValue[SparkConfig](Configuration.getProperty("SparkConfig"))
  }
}
