package ct.dna.lakehouse.framework

import ct.dna.dp.datastore.ColumnContract
import ct.dna.dp.datastore.TableContract
import ct.dna.lakehouse.metastore.ColType
import ct.dna.lakehouse.metastore.TableDef

object Contract {
  def validateTable(table: TableDef): Unit = {
    assert(
      TableContract.sanitize(table.name.toLowerCase()) == table.name.toLowerCase(),
      s"table name not valid: found '${table.name}'"
    )
    assert(
      TableContract.sanitize(table.schemaDef.name.toLowerCase()) == table.schemaDef.name.toLowerCase(),
      s"schema name not valid: found '${table.schemaDef.name}'"
    )
    assert(
      TableContract.sanitize(table.schemaDef.catalogDef.name.toLowerCase()) == table.schemaDef.catalogDef.name.toLowerCase(),
      s"catalog name not valid: found '${table.schemaDef.catalogDef.name}'"
    )
    validateColumns(table.keys, false)
    validateColumns(table.values, false)
  }
  def validateColumns(columns: Seq[(String, ColType)], areMetaColumns: Boolean): Unit = {

    val extendedColumns = columns.map { case (name, ct) =>
      (Actual(name), Lower(name.toLowerCase()), LowerSanitized(ColumnContract.sanitize(name.toLowerCase, areMetaColumns)), ct)
    }

    val ambigousNames = extendedColumns.groupBy(_._2).filter(_._2.size > 1)
    assert(ambigousNames.isEmpty, s"Found ambigousNames ${ambigousNames}")

    val uncompliantNames = extendedColumns.filter(t => t._2.lower != t._3.lowerSanitized)
    assert(uncompliantNames.isEmpty, s"Found uncompliantNames ${uncompliantNames}")

    val reservedNames = extendedColumns.filter(t => reservedColumnNames.contains(t._2.lower))
    assert(reservedNames.isEmpty, s"Found reservedNames ${reservedNames}")
  }
  private case class Actual(actual: String) extends AnyVal
  private case class Lower(lower: String) extends AnyVal
  private case class LowerSanitized(lowerSanitized: String) extends AnyVal
  private val reservedColumnNames = Set("_change_type", "_commit_version", "_commit_timestamp")

}
