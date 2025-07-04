package ct.dna.lakehouse.framework.internal
import ct.dna.lakehouse.Environment
import ct.dna.lakehouse.metastore.TableDef
import ct.dna.utils.spark.SparkConfig.Lakehouse
import ct.dna.utils.spark.SparkConfig.Local
import ct.dna.utils.spark.SparkConfig.RemoteSandbox
import ct.dna.utils.spark.SparkConfig.Sandbox
import ct.dna.utils.spark.SparkConfig.Staging

sealed trait CatalogAccess {
  def target_fqtn(tableDef: TableDef): FQTN
  def source_fqtn(tableDef: TableDef): FQTN
}

private[internal] object CatalogAccess extends CatalogAccess {
  lazy val activeCatalogAccess: CatalogAccess = Environment.activeConfig match {
    case Local(_, _, _)                     => LocalSparkAccess
    case RemoteSandbox(stage, uid, _, _, _) => RemoteSandboxAccess(stage, uid)
    case Lakehouse(_)                       => ???
    case Staging(_)                         => ???
    case Sandbox(_, uid)                    => ???

  }
  def target_fqtn(tableDef: TableDef): FQTN = activeCatalogAccess.target_fqtn(tableDef)
  def source_fqtn(tableDef: TableDef): FQTN = activeCatalogAccess.source_fqtn(tableDef)

  private object LocalSparkAccess extends CatalogAccess {
    def target_fqtn(tableDef: TableDef): FQTN = fqtn(tableDef)
    def source_fqtn(tableDef: TableDef): FQTN = fqtn(tableDef)
    private def fqtn(tableDef: TableDef) = FQTN.PATH(s"spark_catalog.default.${tableDef.schemaDef.catalogDef.name}_${tableDef.schemaDef.name}_${tableDef.name}")
  }

  private case class RemoteSandboxAccess(stage: String, uid: String) extends CatalogAccess {
    def target_fqtn(tableDef: TableDef): FQTN = fqtn(tableDef)
    def source_fqtn(tableDef: TableDef): FQTN = fqtn(tableDef)
    private def fqtn(tableDef: TableDef): FQTN.TABLE =
      FQTN.TABLE(s"ctdp${stage}dbxsandbox.${uid}.${tableDef.schemaDef.catalogDef.name}_${tableDef.schemaDef.name}_${tableDef.name}")
  }

}
