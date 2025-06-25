package ct.dna.lakehouse.spark
import scala.collection.immutable.ArraySeq

import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

object implicits {

  implicit class DataFrameExtensions(val df: DataFrame) {
    def columnsRenamed(f: String => String): Map[String, String] = df.columns.map(c => c -> f(c)).toMap
    def columnsAsSeq: ArraySeq[Column] = ArraySeq.unsafeWrapArray(df.columns.map(col))
    def columnsAsStruct = struct(columnsAsSeq: _*)
    def groupBy_lastBy(groupby: Seq[String], lastby: Seq[String]) = {
      val groupbyCols = groupby.map(col)
      val lastbyCols = lastby.map(col)
      val maxCols = lastbyCols appended (columnsAsStruct.as("_temp_all"))
      df.groupBy(groupbyCols: _*).agg(max(struct(maxCols: _*)).as("_temp_lastRow")).select("_temp_lastRow._temp_all.*")
    }
  }

}
