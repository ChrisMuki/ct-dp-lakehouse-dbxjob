package ct.dna.lakehouse.core.testutils

import ct.dna.lakehouse.core.runtime.implicits.ConfigurationBuilderHasSparkConfig
import ct.dna.lakehouse.core.runtime.implicits.ConfigurationHasSparkConfig
import ct.dna.utils.Hash
import ct.dna.utils.runtime.Configuration

object TestConfig {
  val hash = Hash.base36(sys.props.getOrElse("test_group", "default")).take(4)
  Thread.currentThread().setName(s"lakehouseCore-$hash")

  private lazy val config =
    Configuration.withSparkConfig
      .build(Seq(Configuration.CONFIGFILE + "=test-config.json"))

  lazy val sparkConfig = config.getSparkConfig
}
