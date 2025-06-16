package ct.dna.lakehouse.framework.internal
import ct.dna.lakehouse.TestSuiteWithEnvironment
import org.apache.spark.sql.SparkSession

class SparkBuilderTest extends TestSuiteWithEnvironment {

  "A Remote Spark Session" should "be buildable" in {
    val session = SparkBuilder.newSession()

    assert(session.isInstanceOf[SparkSession])
    assertResult(1)(session.range(1).count())
  }
}
