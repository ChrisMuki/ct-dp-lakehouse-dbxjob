package ct.dna.lakehouse.metastore

abstract class UnityObject private[metastore] (level: String) {
  val name: String

  val unityPath: String

  override def toString(): String = s"$level($unityPath)"
  override def hashCode(): Int = unityPath.hashCode()
  override def equals(obj: Any): Boolean = obj match {
    case that: UnityObject => this.unityPath == that.unityPath
    case _                 => false
  }
}

// private[metastore]
object UnityObject {
  def deriveTableName(o: TableDef) = {
    if (o.getClass().getSimpleName().endsWith("$"))
      o.getClass.getSimpleName.replaceAll("\\$", "")
    else throw new IllegalStateException("Only objects can extend Table")
  }

  def deriveSchemaName(o: SchemaDef) = {
    if (o.getClass().getSimpleName() == "package$")
      o.getClass.getPackage.getName.split("\\.").last
    else throw new IllegalStateException("Only package objects can extend Schema")
  }
  def deriveCatalogName(o: CatalogDef) = {
    if (o.getClass().getSimpleName() == "package$")
      o.getClass.getPackage.getName.split("\\.").last
    else throw new IllegalStateException("Only package objects can extend  Catalog")
  }
}
