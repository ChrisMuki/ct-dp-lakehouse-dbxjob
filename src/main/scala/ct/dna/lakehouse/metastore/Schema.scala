package ct.dna.lakehouse.metastore
abstract class Schema(implicit _catalog: Catalog) extends UnityObject("Schema") {
  implicit val _schema: Schema = this
  val catalog = _catalog

  val name = UnityObject.deriveSchemaName(this)
  val unityPath = s"${catalog.unityPath}.$name"

}
