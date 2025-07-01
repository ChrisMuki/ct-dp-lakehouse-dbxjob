package ct.dna.lakehouse.framework.internal.metadata

import ct.dna.lakehouse.TestSuiteWithEnvironment
import ct.dna.lakehouse.dataframeprovider.ChangeFeedTable
import ct.dna.lakehouse.dataframeprovider.Commit
import ct.dna.lakehouse.dataframeprovider.TargetTable
import ct.dna.utils.json.mapper

class Row_lh_frameworkTest extends TestSuiteWithEnvironment {

  lazy val meta1 = mapper.writeValueAsString(Row_lh_framework(Map.empty, TargetTable.Version(Commit(47, null), Commit(59, null))))
  lazy val meta2 =
    mapper.writeValueAsString(
      Row_lh_framework(Map("" -> ChangeFeedTable.Version(Commit(9, null), Commit(15, null))), TargetTable.Version(Commit(59, null), Commit(60, null)))
    )

  "LH_Metadata" should "parse LakehouseMetadata" in {

    assertResult(meta2)(Row_lh_framework.upgrade(meta1, meta2, meta1))
  }

  it should "upgrade iff target.targetVersion.to == source.targetVersion.from" in {
    assertResult("XXXXX")(Row_lh_framework.upgrade(meta1, "XXXXX", meta1))
    assertThrows[RuntimeException](Row_lh_framework.upgrade(meta1, "XXXXX", meta2))
  }

}
