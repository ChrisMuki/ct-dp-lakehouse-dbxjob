package ct.dna.lakehouse.metastore

abstract class Catalog extends UnityObject("Catalog") {
  implicit val _catalog: Catalog = this

  val name = UnityObject.deriveCatalogName(this)
  val unityPath = name
}
