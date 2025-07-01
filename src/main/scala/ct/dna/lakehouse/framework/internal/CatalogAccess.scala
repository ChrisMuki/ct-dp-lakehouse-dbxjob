package ct.dna.lakehouse.framework.internal
import ct.dna.lakehouse.Environment
import ct.dna.lakehouse.metastore.TableDef
import ct.dna.lakehouse.spark.SparkConfig.Lakehouse
import ct.dna.lakehouse.spark.SparkConfig.RemoteSandbox
import ct.dna.lakehouse.spark.SparkConfig.Sandbox
import ct.dna.lakehouse.spark.SparkConfig.Staging

sealed trait CatalogAccess {
  def target_fqtn(tableDef: TableDef): String
  def source_fqtn(tableDef: TableDef): String
}

private[internal] object CatalogAccess extends CatalogAccess {
  lazy val activeCatalogAccess: CatalogAccess = Environment.activeConfig match {
    case Lakehouse(_)                       => ???
    case Staging(_)                         => ???
    case Sandbox(_, uid)                    => ???
    case RemoteSandbox(stage, uid, _, _, _) => RemoteSandboxAccess(stage, uid)
  }
  def target_fqtn(tableDef: TableDef): String = activeCatalogAccess.target_fqtn(tableDef)
  def source_fqtn(tableDef: TableDef): String = activeCatalogAccess.source_fqtn(tableDef)

  private case class RemoteSandboxAccess(stage: String, uid: String) extends CatalogAccess {
    def target_fqtn(tableDef: TableDef): String = fqtn(tableDef)
    def source_fqtn(tableDef: TableDef): String = fqtn(tableDef)
    private def fqtn(tableDef: TableDef): String =
      s"ctdp${stage}dbxsandbox.${uid}.${tableDef.schemaDef.catalogDef.name}_${tableDef.schemaDef.name}_${tableDef.name}"
  }

}
