package ct.dna.lakehouse.core

import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.runtime.implicits.ConfigurationBuilderHasSparkConfig
import ct.dna.lakehouse.core.runtime.implicits.ConfigurationHasSparkConfig
import ct.dna.utils.runtime.Configuration

object SparkTestEnv {

  private lazy val config =
    Configuration
      .additionalConfigFiles("default-config.json")
      .withSparkConfig
      .build(
        Seq(
          Configuration.CONFIGFILE + "=local-config.json"
        )
      )

  lazy val sparkConfig = config.getSparkConfig

  def ensureInitialized() = SparkEnv.ensureInitialized(sparkConfig)
  def idResolver = SparkEnv.idResolver
  def idResolverWithTablePrefix(prefix: String) = SparkEnv.idResolverWithTablePrefix(prefix)
  def clearIdResolverTablePrefix = SparkEnv.clearIdResolverTablePrefix()

}
