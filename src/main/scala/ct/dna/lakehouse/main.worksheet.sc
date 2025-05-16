import org.apache.spark.sql.SparkSession
import ct.dna.lakehouse.Environment
import ct.dna.lakehouse.framework.internal.SparkBuilder

Environment.initializeAndValidate()
ct.dna.lakehouse.framework.internal.SparkBuilder.newSession()

