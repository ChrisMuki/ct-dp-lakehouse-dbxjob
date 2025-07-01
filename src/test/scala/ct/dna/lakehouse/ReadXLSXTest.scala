package ct.dna.lakehouse
import ct.dna.lakehouse.catalogs.testOnly
import ct.dna.lakehouse.metastore.TableDef
import org.apache.spark.sql.DataFrame

class ReadXLSXTest extends TestSuiteWithEnvironment {

  def read(p: String, t: TableDef): DataFrame = ???

  "My TestDataReader" should "read a xlsx for sr.showcase.BOM" in {

    import testOnly.readxlsx.TestTable
    val df = read("path", TestTable)
    val allColumns = TestTable.keys ++ TestTable.values

    // check for names and datatype of columns
    assert(df.columns == allColumns)

    // check for data
    assert(df.count() == 47)

  }
}
