package ct.dna.lakehouse
import ct.dna.lakehouse.framework.EnvironmentConfig
import ct.dna.utils.Configuration
import ct.dna.utils.LoggingTrait
import ct.dna.utils.json.mapper

object Environment extends LoggingTrait {
  private var initArgs: Option[Array[String]] = None

  def initializeAndValidate(args: Array[String] = Array.empty) = initArgs match {
    case None        => { initArgs = Some(args); Configuration.required("EnvironmentConfig").initializeAndValidate(args) }
    case Some(value) => assert(args == value)
  }

  lazy val activeConfig: EnvironmentConfig = {
    Configuration.ensureInitialized()
    mapper.readValue[EnvironmentConfig](Configuration.getProperty("EnvironmentConfig"))
  }

  // private lazy val activeCatalogAccess = CatalogAccess(activeConfig)

  // def target_fqtn(tableDef: Table): String = activeCatalogAccess.target_fqtn(tableDef)
  // def source_fqtn(tableDef: Table): String = activeCatalogAccess.source_fqtn(tableDef)

}
