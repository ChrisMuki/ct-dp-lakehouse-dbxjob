package ct.dna.lakehouse.framework.internal
import ct.dna.lakehouse.Environment
import ct.dna.lakehouse.metastore.TableDef
import ct.dna.lakehouse.spark.SparkConfig.Lakehouse
import ct.dna.lakehouse.spark.SparkConfig.RemoteSandbox
import ct.dna.lakehouse.spark.SparkConfig.Sandbox
import ct.dna.lakehouse.spark.SparkConfig.Staging
import ct.dna.lakehouse.spark.SparkConfig.LocalSpark

sealed trait CatalogAccess {
  def target_fqtn(tableDef: TableDef): String
  def source_fqtn(tableDef: TableDef): String
}

private[internal] object CatalogAccess extends CatalogAccess {
  lazy val activeCatalogAccess: CatalogAccess = Environment.activeConfig match {
    case LocalSpark(_, _)                   => LocalSparkAccess
    case RemoteSandbox(stage, uid, _, _, _) => RemoteSandboxAccess(stage, uid)
    case Lakehouse(_)                       => ???
    case Staging(_)                         => ???
    case Sandbox(_, uid)                    => ???

  }
  def target_fqtn(tableDef: TableDef): String = activeCatalogAccess.target_fqtn(tableDef)
  def source_fqtn(tableDef: TableDef): String = activeCatalogAccess.source_fqtn(tableDef)

  private object LocalSparkAccess extends CatalogAccess {
    def target_fqtn(tableDef: TableDef): String = fqtn(tableDef)
    def source_fqtn(tableDef: TableDef): String = fqtn(tableDef)
    private def fqtn(tableDef: TableDef): String =
      s"${tableDef.schemaDef.catalogDef.name}_${tableDef.schemaDef.name}.${tableDef.name}"
  }

  private case class RemoteSandboxAccess(stage: String, uid: String) extends CatalogAccess {
    def target_fqtn(tableDef: TableDef): String = fqtn(tableDef)
    def source_fqtn(tableDef: TableDef): String = fqtn(tableDef)
    private def fqtn(tableDef: TableDef): String =
      s"ctdp${stage}dbxsandbox.${uid}.${tableDef.schemaDef.catalogDef.name}_${tableDef.schemaDef.name}_${tableDef.name}"
  }

}
