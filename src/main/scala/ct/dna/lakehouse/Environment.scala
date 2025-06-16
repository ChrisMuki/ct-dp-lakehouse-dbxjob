package ct.dna.lakehouse
import ct.dna.lakehouse.framework.EnvironmentConfig
import ct.dna.utils.Configuration
import ct.dna.utils.LoggingTrait
import ct.dna.utils.json.mapper

object Environment extends LoggingTrait {

  private var initialized = false

  def initializeAndValidate(): Unit = activeConfig

  lazy val activeConfig: EnvironmentConfig = {
    Configuration.required("EnvironmentConfig").initializeAndValidate(Array.empty)
    mapper.readValue[EnvironmentConfig](Configuration.getProperty("EnvironmentConfig"))
  }

  // private lazy val activeCatalogAccess = CatalogAccess(activeConfig)

  // def target_fqtn(tableDef: Table): String = activeCatalogAccess.target_fqtn(tableDef)
  // def source_fqtn(tableDef: Table): String = activeCatalogAccess.source_fqtn(tableDef)

}
