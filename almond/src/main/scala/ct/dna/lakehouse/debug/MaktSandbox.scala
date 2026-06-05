package ct.dna.lakehouse.debug

import ct.dna.lakehouse.core.SparkTestEnv
import ct.dna.lakehouse.core.testutils.TestDataManager
import ct.dna.lakehouse.dm_md.fin_hawk.makt_1
import org.apache.spark.sql.SparkSession

object MaktSandbox extends App {
  SparkTestEnv.ensureInitialized()
  val spark = SparkSession.active

  println("HELLO")

  val tdm = TestDataManager("makt")

  println("Creating target table...")
  tdm.createAsTarget(makt_1)

  spark.sql(s"SELECT * FROM ${SparkTestEnv.idResolver.asTargetFQN(makt_1).fqn}").show(10, false)

  // tdm.update(makt_1)

  // spark.sql("SELECT * FROM makt").show(10, false)

  tdm.dropTargetIfExists(makt_1)

}
