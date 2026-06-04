package ct.dna.lakehouse.core.lakehousejob

import ct.dna.lakehouse.core.lakehousejob.orchestration.JobSetupTask
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.dm_md.{`package` => dmCatalog}
import org.scalatest.funsuite.AnyFunSuite

/** Exercises `PlanBuilder.buildPlan` against the real `dm_md` catalog.
  *
  * `dm_md` is small but has intra-catalog edges (`mdm`, `mdp` depend on `mara`/`makt`/`t023t`/...) and cross-catalog edges (every base table pulls from `sr`).
  * The cross-catalog edges must be filtered out of the dependency graph so each catalog can be deployed as a self-contained Databricks job.
  */
class LakehouseJobBuildPlanTest extends AnyFunSuite {

  test("buildPlan returns a topologically sorted plan with cross-catalog parents filtered") {
    val (plan, descendants) = JobSetupTask.buildPlan(dmCatalog)

    assert(plan.nonEmpty, "dm_md is expected to contain at least one TableSpec")

    val intraIds: Set[TableID] = plan.map(_._1.id).toSet

    // Topological invariant: every declared (intra-catalog) parent appears before its child.
    val positionById: Map[TableID, Int] = plan.map(_._1.id).zipWithIndex.toMap
    plan.foreach { case (tableSpec, parents) =>
      parents.foreach { p =>
        assert(intraIds.contains(p), s"Plan contains cross-catalog parent $p for ${tableSpec.id}")
        assert(
          positionById(p) < positionById(tableSpec.id),
          s"Parent $p must appear before child ${tableSpec.id} in the topological order"
        )
      }
    }

    // Self-reference filter: no table is its own declared parent in the plan.
    plan.foreach { case (tableSpec, parents) =>
      assert(!parents.contains(tableSpec.id), s"${tableSpec.id} must not be listed as its own parent")
    }

    // Descendant map covers every table and is consistent with the plan ordering
    // (a descendant always comes after its ancestor).
    intraIds.foreach { id =>
      val ds = descendants.getOrElse(id, Set.empty)
      ds.foreach { d =>
        assert(intraIds.contains(d), s"Descendant $d of $id must be an intra-catalog table")
        assert(
          positionById(id) < positionById(d),
          s"Descendant $d must come after ancestor $id in the topological order"
        )
      }
    }
  }
}
