package ct.dna.lakehouse.core.testcatalogs.exclusively_used_schemas

import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.SchemaSpec

/** Spark Tables used by
  *   - ct.dna.lakehouse.core.framework.internal.tableupdater.ApplyChanges_Simple_Test_OwnJvm
  */

package object applychanges_simple extends SchemaSpec {

  case class SourceEntity(@PK id: Int, name: String, value: Double, cdc_ts: Long, cdc_op: String) extends Entity

  case class TargetEntity(@PK name: String, value: Double) extends Entity

}
