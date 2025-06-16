package ct.dna.lakehouse

import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Try

import ct.dna.lakehouse.framework.Contract.validateTable
import ct.dna.lakehouse.metastore.Catalog
import ct.dna.lakehouse.metastore.Schema
import ct.dna.lakehouse.metastore.Table
import ct.dna.lakehouse.metastore.UnityObject
import ct.dna.lakehouse.transformations.Origin
import io.github.classgraph.ClassGraph
object DAG {
  // WIP

  lazy val dag = {
    val scanResult = new ClassGraph()
      .enableClassInfo()
      .enableExternalClasses()
      .scan()

    def findObjects[T](interfaceClass: Class[T]) = {
      val clazzes = Try(
        scanResult
          .getSubclasses(interfaceClass)
          .loadClasses()
          .asScala
      ).getOrElse(ArrayBuffer.empty) ++ Try(
        scanResult
          .getClassesImplementing(interfaceClass)
          .loadClasses()
          .asScala
      ).getOrElse(ArrayBuffer.empty)

      clazzes
        .filter(_.getName().endsWith("$"))
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

    val allTransformations = findObjects(classOf[Origin])
    assert(allTransformations.map(_.table) == allTables)

    allTables.foreach(validateTable)

  }
  // implementations.forEach(cls => println(cls.getName))

}
