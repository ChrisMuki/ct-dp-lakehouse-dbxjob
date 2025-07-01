package ct.dna.lakehouse.metastore

abstract class CatalogDef extends UnityObject("Catalog") {
  implicit val _catalogDef: CatalogDef = this

  val name = UnityObject.deriveCatalogName(this)
  val unityPath = name
}
