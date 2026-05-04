package ct.dna.lakehouse.core

import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.core.model.internal.findAllObjectsOfType

/** Discovers DM table dependencies by reflecting on `sourceTableSpecs` in a given schema package.
  *
  * Lives in the `ct.dna.lakehouse.core` package so it can access `GenericMergedInto` (which is `private[core]`).
  */
object DmDependencyResolver {

  /** Discovers all `TableSpec` objects in `schemaPackage` and extracts DM-internal dependencies from their `sourceTableSpecs`, returning them in topological
    * order.
    *
    * @param schemaPackage
    *   fully-qualified package to scan (e.g. `"ct.dna.lakehouse.dm_md.fin_hawk"`)
    * @return
    *   tables in topological order, each paired with its DM-internal dependency names
    */
  def resolve(schemaPackage: String): Seq[(String, Seq[String])] = {
    val allSpecs = findAllObjectsOfType(classOf[TableSpec[Entity]], Seq(schemaPackage))
    require(allSpecs.nonEmpty, s"No TableSpec objects found in package '$schemaPackage'")

    val dmNames = allSpecs.map(_.id.name).toSet

    // Extract DM-internal dependencies for each table
    val deps: Map[String, Set[String]] = allSpecs.map { spec =>
      val dmDeps = spec match {
        case spec: Updated =>
          spec.sourceTableSpecs
            .map(_.id.name)
            .filter(dmNames.contains)
            .toSet - spec.id.name // exclude self-references
        case _ => Set.empty[String]
      }
      spec.id.name -> dmDeps
    }.toMap

    // Topological sort via Kahn's algorithm
    val inDegree = scala.collection.mutable.Map.from(deps.map { case (k, v) => k -> v.size })
    val children = scala.collection.mutable.Map.empty[String, scala.collection.mutable.Buffer[String]]
    deps.foreach { case (child, parents) =>
      parents.foreach { parent =>
        children.getOrElseUpdate(parent, scala.collection.mutable.Buffer.empty) += child
      }
    }

    val queue = scala.collection.mutable.Queue.from(inDegree.filter(_._2 == 0).keys.toSeq.sorted)
    val ordered = scala.collection.mutable.Buffer.empty[(String, Seq[String])]

    while (queue.nonEmpty) {
      val name = queue.dequeue()
      ordered += (name -> deps(name).toSeq.sorted)
      children.getOrElse(name, Seq.empty).foreach { child =>
        inDegree(child) -= 1
        if (inDegree(child) == 0) queue.enqueue(child)
      }
    }

    require(ordered.size == deps.size, "Cycle detected in DM dependency graph")
    ordered.toSeq
  }
}
