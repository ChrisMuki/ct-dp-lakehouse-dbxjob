import ct.dna.lakehouse.DAG
import ct.dna.lakehouse.catalogs.dw_tx.showcase.MaterialUsageDetails
import ct.dna.lakehouse.metastore.UnityObject
import io.github.classgraph.ClassGraph
import ct.dna.lakehouse.metastore.Catalog
import ct.dna.lakehouse.metastore.Schema
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.Origin.Transformation
import scala.jdk.CollectionConverters.ListHasAsScala
import ct.dna.lakehouse.framework.Contract.validateTable

val scanResult = new ClassGraph()
  .enableClassInfo()
  .enableExternalClasses()
  .scan()

def findUnityObjects = {
  scanResult
    .getSubclasses(classOf[UnityObject])
    .filter(_.getName().endsWith("$"))
    .loadClasses()
    .asScala
    .map(cls => {
      val clazz = Class.forName(cls.getName)
      val moduleField = clazz.getField("MODULE$")
      moduleField.get(null).asInstanceOf[UnityObject]
    })
    .toSet
}

val allUnityObjects = findUnityObjects


assert(allUnityObjects.groupBy(_.unityPath).filter(_._2.size > 1).flatMap(_._2).isEmpty)

val allCatalogs = allUnityObjects.collect { case c: Catalog => c }

val allSchemas = allUnityObjects.collect { case c: Schema => c }
assert(allSchemas.filterNot(t => allCatalogs.contains(t.catalog)).isEmpty)
val allTables = allUnityObjects.collect { case c: Table => c }
assert(allTables.filterNot(t => allSchemas.contains(t.schema)).isEmpty)

// val allTransformations = findObjects(classOf[Transformation])
// assert((allTransformations.map(_.table) -- allTables).isEmpty)
// assert(allTransformations.size == allTables.size)

// allTables.foreach(validateTable)
// val scanResult = new ClassGraph()
//   .enableClassInfo()
//   .enableExternalClasses()
//   .scan()

// def findObjects[T](interfaceClass: Class[T]) = {
//   scanResult
//     .getClassesImplementing(interfaceClass)
//     .filter(_.getName().endsWith("$"))
//     .loadClasses()
//     .asScala
//     .map(cls => {
//       val clazz = Class.forName(cls.getName)
//       val moduleField = clazz.getField("MODULE$")
//       moduleField.get(null).asInstanceOf[T]
//     })
//     .toSet
// }

// val classes = scanResult
//   .getClassesImplementing(classOf[UnityObject])
//   .filter(_.getName().endsWith("$"))
//   .loadClasses()
//   .asScala
//   .toSet

// classes.foreach(println(_))

// findObjects(classOf[UnityObject])
