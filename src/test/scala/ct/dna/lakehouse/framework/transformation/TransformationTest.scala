// package ct.dna.lakehouse.framwork_old.transformation

// import ct.dna.lakehouse.unity.Table
// import org.apache.spark.sql.DataFrame
// import org.scalatest.BeforeAndAfterAll
// import org.scalatest.flatspec._
// import org.scalatest.matchers._

// trait TransformationTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterAll {
//   // beforAll and afterAll to initialize temporary tables
//   // framwork_old must enable to init Table, check for content, run transformation, check for content again
//   // protected lazy val testEnv: EnvironmentConfig = ???
//   // assert(testEnv.isTest)

//   override protected def beforeAll(): Unit = TransformationTest.beforeAll()
//   override protected def afterAll(): Unit = TransformationTest.afterAll()

//   def mkEmptyTables(tableDef1: Table, tableDefs: Table*) = { mkEmptyTable(tableDef1); tableDefs.foreach(mkEmptyTable(_)) }
//   def mkEmptyTable(tableDef: Table) = {

//     /** Somehow we need to enable individual Table only for this single test => Somehow we also need to redirect reading of the table based on the current
//       * running Test => add possibility to add a test class to an actual Transformation? Or create a copy with a TestObject?
//       */
//     ???
//   }
//   def insertData(tableDef: Table, df: DataFrame) = ???

//   def deleteData(tableDef: Table, df: DataFrame) = ???

// }
// object TransformationTest {
//   private def beforeAll(): Unit = {
//     // println("Init schema for the test suite run")
//     // ???
//   }
//   private def afterAll(): Unit = {
//     // println("clean up")
//     // ???
//   }

// }
