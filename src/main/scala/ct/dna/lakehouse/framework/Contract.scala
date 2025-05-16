package ct.dna.lakehouse.framework

import ct.dna.dp.datastore.ColumnContract
import ct.dna.dp.datastore.TableContract
import ct.dna.lakehouse.metastore.{Table, ColType}

object Contract {
  def validateTable(table: Table): Unit = {
    assert(TableContract.sanitize(table.name.toLowerCase()) == table.name.toLowerCase())
    assert(TableContract.sanitize(table.schema.name.toLowerCase()) == table.schema.name.toLowerCase())
    assert(TableContract.sanitize(table.schema.catalog.name.toLowerCase()) == table.schema.catalog.name.toLowerCase())
    validateColumns(table.keys, false)
    validateColumns(table.values, false)
  }
  def validateColumns(columns: Seq[(String, ColType)], areMetaColumns: Boolean): Unit = {

    val extendedColumns = columns.map { case (name, ct) =>
      (Actual(name), Lower(name.toLowerCase()), LowerSanitized(ColumnContract.sanitize(name.toLowerCase, areMetaColumns)), ct)
    }

    val ambigousNames = extendedColumns.groupBy(_._2).filter(_._2.size > 0)
    assert(ambigousNames.isEmpty)

    val uncompliantNames = extendedColumns.filter(t => t._2.lower != t._3.lowerSanitized)
    assert(uncompliantNames.isEmpty)

    val reservedNames = extendedColumns.filter(t => reservedColumnNames.contains(t._2.lower))
    assert(reservedNames.isEmpty)
  }
  private case class Actual(actual: String) extends AnyVal
  private case class Lower(lower: String) extends AnyVal
  private case class LowerSanitized(lowerSanitized: String) extends AnyVal
  private val reservedColumnNames = Set("_change_type", "_commit_version", "_commit_timestamp")

}
