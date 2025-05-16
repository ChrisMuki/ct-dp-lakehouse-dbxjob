package ct.dna.lakehouse.framework.internal

import ct.dna.lakehouse.framework.MergeBuilder
import ct.dna.lakehouse.framework.internal.MergeBuilderImpl._
import org.apache.spark.sql
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions.expr

final case class MergeBuilderImpl(targettable: TargetTableImpl, miw: sql.MergeIntoWriter[sql.Row]) extends MergeBuilder {
  def whenMatched() = WhenMatchedImpl(targettable, miw.whenMatched())
  def whenMatched(condition: String) = WhenMatchedImpl(targettable, miw.whenMatched(expr(condition)))
  def whenMatched(condition: Column) = WhenMatchedImpl(targettable, miw.whenMatched(condition))

  def whenNotMatched() = WhenNotMatchedImpl(targettable, miw.whenNotMatched())
  def whenNotMatched(condition: String) = WhenNotMatchedImpl(targettable, miw.whenNotMatched(expr(condition)))
  def whenNotMatched(condition: Column) = WhenNotMatchedImpl(targettable, miw.whenNotMatched(condition))

  def whenNotMatchedBySource() = WhenNotMatchedBySourceImpl(targettable, miw.whenNotMatchedBySource())
  def whenNotMatchedBySource(condition: String) = WhenNotMatchedBySourceImpl(targettable, miw.whenNotMatchedBySource(expr(condition)))
  def whenNotMatchedBySource(condition: Column) = WhenNotMatchedBySourceImpl(targettable, miw.whenNotMatchedBySource(condition))

  def withSchemaEvolution() = MergeBuilderImpl(targettable, miw.withSchemaEvolution())
  def execute() = {
    targettable.merged = true
    miw.merge()
    targettable.spark.catalog.dropTempView(targettable.alias)
  }
}
object MergeBuilderImpl {
  final case class WhenMatchedImpl(targettable: TargetTableImpl, wm: sql.WhenMatched[sql.Row]) extends MergeBuilder.WhenMatched {
    def delete() = MergeBuilderImpl(targettable, wm.delete())
    def update(map: Map[String, Column]) = MergeBuilderImpl(targettable, wm.update(map))
    def updateExpr(map: Map[String, String]) = update(map.map(t => t._1 -> expr(t._2)))
    def updateAll() = MergeBuilderImpl(targettable, wm.updateAll())
  }

  final case class WhenNotMatchedImpl(targettable: TargetTableImpl, wm: sql.WhenNotMatched[sql.Row]) extends MergeBuilder.WhenNotMatched {
    def insert(map: Map[String, Column]) = MergeBuilderImpl(targettable, wm.insert(map))
    def insertExpr(map: Map[String, String]) = insert(map.map(t => t._1 -> expr(t._2)))
    def insertAll() = MergeBuilderImpl(targettable, wm.insertAll())
  }
  final case class WhenNotMatchedBySourceImpl(targettable: TargetTableImpl, wm: sql.WhenNotMatchedBySource[sql.Row])
      extends MergeBuilder.WhenNotMatchedBySource {
    def delete() = MergeBuilderImpl(targettable, wm.delete())
    def update(map: Map[String, Column]) = MergeBuilderImpl(targettable, wm.update(map))
    def updateExpr(map: Map[String, String]) = update(map.map(t => t._1 -> expr(t._2)))
    def updateAll() = MergeBuilderImpl(targettable, wm.updateAll())
  }
}
