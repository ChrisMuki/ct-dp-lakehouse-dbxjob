package ct.dna.lakehouse.catalogs.dw_md.showcase

import ct.dna.lakehouse.TestSuiteWithEnvironment

class BomTest extends TestSuiteWithEnvironment /* some other suitable as well?*/ {

  "dw_md.showcase.Bom" should "be empty when 'sr.showcase.Bom' table is empty" in {

//implicit class that extends Table
// =>  def mkEmpty: Dataframe
// => def update(excelspreadsheet): Dataframe //update/override?
// => def snapshot : Dataframe
// => def executeTransformation // (implicit testSuite:TestSuiteWithEnvironment)?
//

    // sr.showcase.Bom.mkEmpty
    // dw_md.showcase.Bom.update(excel)
    // assert(dw_md.showcase.Bom.snapshot.isEmpty)

  }
}
