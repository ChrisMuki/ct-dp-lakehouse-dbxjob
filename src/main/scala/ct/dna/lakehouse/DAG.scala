package ct.dna.lakehouse

import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Try

import ct.dna.lakehouse.framework.Contract.validateTable
import ct.dna.lakehouse.metastore.CatalogDef
import ct.dna.lakehouse.metastore.Origin
import ct.dna.lakehouse.metastore.SchemaDef
import ct.dna.lakehouse.metastore.TableDef
import ct.dna.lakehouse.metastore.UnityObject
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

    val allCatalogs = findObjects(classOf[CatalogDef])

    val allSchemas = findObjects(classOf[SchemaDef])
    assert(allSchemas.filterNot(t => allCatalogs.contains(t.catalogDef)).isEmpty)
    val allTables = findObjects(classOf[TableDef])
    assert(allTables.filterNot(t => allSchemas.contains(t.schemaDef)).isEmpty)

    val allTransformations = findObjects(classOf[Origin])
    assert(allTransformations.map(_.table) == allTables)

    allTables.foreach(validateTable)

  }
  // implementations.forEach(cls => println(cls.getName))

}
