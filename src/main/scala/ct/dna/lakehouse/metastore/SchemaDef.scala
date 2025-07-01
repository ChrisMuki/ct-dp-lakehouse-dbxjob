package ct.dna.lakehouse.metastore

abstract class SchemaDef(implicit _catalogDef: CatalogDef) extends UnityObject("Schema") {
  implicit val _schemaDef: SchemaDef = this
  val catalogDef = _catalogDef

  val name = UnityObject.deriveSchemaName(this)
  val unityPath = s"${catalogDef.unityPath}.$name"

}
