package ct.dna.lakehouse.framework

import org.apache.spark.sql.Column
trait MergeBuilder {
  import MergeBuilder._
  def whenMatched(): WhenMatched
  def whenMatched(condition: String): WhenMatched
  def whenMatched(condition: Column): WhenMatched

  def whenNotMatched(): WhenNotMatched
  def whenNotMatched(condition: String): WhenNotMatched
  def whenNotMatched(condition: Column): WhenNotMatched

  def whenNotMatchedBySource(): WhenNotMatchedBySource
  def whenNotMatchedBySource(condition: String): WhenNotMatchedBySource
  def whenNotMatchedBySource(condition: Column): WhenNotMatchedBySource

  def withSchemaEvolution(): MergeBuilder
  def execute(): Unit
}
object MergeBuilder {
  trait WhenMatched {
    def delete(): MergeBuilder
    def update(map: Map[String, Column]): MergeBuilder
    def updateExpr(map: Map[String, String]): MergeBuilder
    def updateAll(): MergeBuilder
  }

  trait WhenNotMatched {
    def insert(map: Map[String, Column]): MergeBuilder
    def insertExpr(map: Map[String, String]): MergeBuilder
    def insertAll(): MergeBuilder
  }
  trait WhenNotMatchedBySource {
    def delete(): MergeBuilder
    def update(map: Map[String, Column]): MergeBuilder
    def updateExpr(map: Map[String, String]): MergeBuilder
    def updateAll(): MergeBuilder
  }
}
