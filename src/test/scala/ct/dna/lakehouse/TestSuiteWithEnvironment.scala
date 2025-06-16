package ct.dna.lakehouse

import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec._
import org.scalatest.matchers._

trait TestSuiteWithEnvironment extends AnyFlatSpec with should.Matchers with BeforeAndAfterAll {
  override protected def beforeAll(): Unit = {
    Environment.initializeAndValidate()
  }

}
