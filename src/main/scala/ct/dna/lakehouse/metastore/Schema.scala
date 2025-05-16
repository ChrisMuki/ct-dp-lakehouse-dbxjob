package ct.dna.lakehouse.metastore
abstract class Schema(implicit _catalog: Catalog) extends UnityObject("Schema") {
  implicit val _schema: Schema = this
  val catalog = _catalog

  lazy val name = UnityObject.deriveSchemaName(this)
  lazy val unityPath = s"${catalog.unityPath}.$name"

}
