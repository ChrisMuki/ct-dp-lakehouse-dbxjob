package ct.dna.utils.spark

import scala.collection.mutable.ArrayBuffer

import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

trait TestWithSparkHandler extends AnyFlatSpec with should.Matchers with BeforeAndAfterAll {

  private val allHandlers = ArrayBuffer.empty[SparkSessionHandler]

  def buildHandler(config: SparkConfig) = { val handler = SparkSessionHandler(config); allHandlers.addOne(handler); handler }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    allHandlers.foreach(_.close())
  }
}
