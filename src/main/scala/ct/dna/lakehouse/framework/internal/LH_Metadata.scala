package ct.dna.lakehouse.framework.internal

import org.apache.spark.sql.functions._

object LH_Metadata {
  val regex = ".*\"version\": *([+-]*\\d+).*".r

  val metadataUDF = udf((source: String, target: String) => {
    (source, target) match {
      case (regex(sv), regex(tv)) if sv > tv => source
      case _                                 => throw new RuntimeException("target version can only increase. Concurrency suspected")
    }
  })
}
