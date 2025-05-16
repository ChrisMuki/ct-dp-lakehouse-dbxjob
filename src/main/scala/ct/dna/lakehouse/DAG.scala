package ct.dna.lakehouse

import io.github.classgraph.ClassGraph
import ct.dna.lakehouse.metastore.UnityObject
import ct.dna.lakehouse.metastore.Catalog
import ct.dna.lakehouse.metastore.Schema
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.transformations.Origin.Transformation
import scala.jdk.CollectionConverters.ListHasAsScala
import ct.dna.lakehouse.framework.Contract.validateTable
object DAG {

  lazy val dag = {
    val scanResult = new ClassGraph()
      .enableClassInfo()
      .enableExternalClasses()
      .scan()

    def findObjects[T](interfaceClass: Class[T]) = {
      scanResult
        .getClassesImplementing(interfaceClass)
        .filter(_.getName().endsWith("$"))
        .loadClasses()
        .asScala
        .map(cls => {
          val clazz = Class.forName(cls.getName)
          val moduleField = clazz.getField("MODULE$")
          moduleField.get(null).asInstanceOf[T]
        })
        .toSet
    }

    val allUnityObjects = findObjects(classOf[UnityObject])
    assert(allUnityObjects.groupBy(_.unityPath).filter(_._2.size > 1).flatMap(_._2).isEmpty)

    val allCatalogs = findObjects(classOf[Catalog])

    val allSchemas = findObjects(classOf[Schema])
    assert(allSchemas.filterNot(t => allCatalogs.contains(t.catalog)).isEmpty)
    val allTables = findObjects(classOf[Table])
    assert(allTables.filterNot(t => allSchemas.contains(t.schema)).isEmpty)

    val allTransformations = findObjects(classOf[Transformation])
    assert((allTransformations.map(_.table) -- allTables).isEmpty)
    assert(allTransformations.size == allTables.size)

    allTables.foreach(validateTable)

  }
  // implementations.forEach(cls => println(cls.getName))

}
