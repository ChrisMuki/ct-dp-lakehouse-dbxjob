package ct.dna.lakehouse.core.testcatalogs.exclusively_used_schemas

import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.NotNull
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.SchemaSpec

/** Spark Tables used by
  *   - ct.dna.lakehouse.core.catalog.internal.TableDescTest_OwnJvm
  */
package object tabledesc_schema2 extends SchemaSpec {

  case class Table1Entity(@PK id: Int, name: String, value: Double) extends Entity
  case class Table2Entity(@PK id: Int, name: String, created: Date) extends Entity
  case class Table31Entity(@PK id: Int, @NotNull name: String, created: Date, usages: Long) extends Entity
  case class Table32Entity(@PK id: Int, @PK name: String, created: Date, usages: Long) extends Entity

}
