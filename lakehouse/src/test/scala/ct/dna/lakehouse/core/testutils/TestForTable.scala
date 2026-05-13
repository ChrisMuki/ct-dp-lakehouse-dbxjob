package ct.dna.lakehouse.core.testutils

import ct.dna.lakehouse.core.model.internal.TableSpecBase
import ct.dna.lakehouse.core.runtime.SparkConfig

abstract class TestForTable(spec: TableSpecBase) extends TestWithPrefixedTables {
  def sparkConfig: SparkConfig = TestConfig.sparkConfig
  val tablePrefix: String = f"t_${spec.getClass.getName.hashCode & 0xffffffffL}%08x_"
}
