package ct.dna.lakehouse.framework.internal
import ct.dna.lakehouse.Environment
import ct.dna.lakehouse.spark.SparkConfig.Lakehouse
import ct.dna.lakehouse.spark.SparkConfig.RemoteSandbox
import ct.dna.lakehouse.spark.SparkConfig.Sandbox
import ct.dna.lakehouse.spark.SparkConfig.Staging
import ct.dna.lakehouse.metastore.Table

sealed trait CatalogAccess {
  def target_fqtn(tableDef: Table): String
  def source_fqtn(tableDef: Table): String
}

private[internal] object CatalogAccess extends CatalogAccess {
  lazy val activeCatalogAccess: CatalogAccess = Environment.activeConfig match {
    case Lakehouse(_)                       => ???
    case Staging(_)                         => ???
    case Sandbox(_, uid)                    => ???
    case RemoteSandbox(stage, uid, _, _, _) => RemoteSandboxAccess(stage, uid)
  }
  def target_fqtn(tableDef: Table): String = activeCatalogAccess.target_fqtn(tableDef)
  def source_fqtn(tableDef: Table): String = activeCatalogAccess.source_fqtn(tableDef)

  private case class RemoteSandboxAccess(stage: String, uid: String) extends CatalogAccess {
    def target_fqtn(tableDef: Table): String = fqtn(tableDef)
    def source_fqtn(tableDef: Table): String = fqtn(tableDef)
    private def fqtn(tableDef: Table): String =
      s"ctdp${stage}dbxsandbox.${uid}.${tableDef.schema.catalog.name}_${tableDef.schema.name}_${tableDef.name}"
  }
}
