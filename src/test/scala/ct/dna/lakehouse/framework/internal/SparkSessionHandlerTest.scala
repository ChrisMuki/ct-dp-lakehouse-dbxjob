package ct.dna.lakehouse.framework.internal
import ct.dna.lakehouse.TestSuiteWithEnvironment

class SparkSessionHandlerTest extends TestSuiteWithEnvironment {

  "A Spark Session" should "be buildable" in {
    val session = SparkSessionHandler.newSession()

    assert(session.isInstanceOf[org.apache.spark.sql.SparkSession])
    assertResult(1)(session.range(1).count())
  }
}
