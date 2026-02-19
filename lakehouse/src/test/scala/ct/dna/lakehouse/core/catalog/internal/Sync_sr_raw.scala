package ct.dna.lakehouse.core.catalog.internal
import ct.dna.utils.runtime.Configuration
import ct.dna.lakehouse.core.runtime.implicits.ConfigurationBuilderHasSparkConfig
import ct.dna.lakehouse.core.runtime.implicits.ConfigurationHasSparkConfig
import ct.dna.utils.ResourceLoader
import ct.dna.lakehouse.core.runtime.SparkEnv
import ct.dna.lakehouse.core.model.CatalogID
import ct.dna.lakehouse.core.catalog.internal.CatalogManager
import ct.dna.lakehouse.sr_raw
import ct.dna.lakehouse.core.catalog.internal.TableManager
import ct.dna.lakehouse.core.catalog.TableFQN
import ct.dna.lakehouse.core.catalog.TableDesc
import ct.dna.lakehouse.core.catalog.SchemaFQN

object Sync_sr_raw {

  def main(args: Array[String]): Unit = {

    val config = args.toList match {
      case Nil          => "sync_sr_raw/dev.json"
      case "dev" :: Nil => "sync_sr_raw/dev.json"
    }

    val parsedConfig = Configuration
      .required("rootDir")
      .withSparkConfig
      .build(Array("configFile=" + config))

    SparkEnv.ensureInitialized(parsedConfig.getSparkConfig)

    val catalogID = sr_raw.id
    val catalogFQN = SparkEnv.idResolver.asTargetFQN(catalogID)

    val schemaFQNs = CatalogManager.findSchemaFQN(catalogFQN)

    schemaFQNs.foreach(createSchemaScala)
    // we need to create folder and package.scala for the actual schema
    // open question: what is the name field in  SchemaID(catalogId: CatalogID, name: String) that leads then to the very same SchemaFQN?
    // SparkEnv.idResolver.asTargetFQN(SchemaID(catalogID, "WHAT TO ADD")) == schemaFQN

    val tablesFQNs = schemaFQNs.flatMap(s => CatalogManager.findTableFQN(s))

    tablesFQNs.foreach(createTableScala)
    println("Sync_sr_raw test running...")
    // Add test logic here
  }

  def createTableScala(tableFQN: TableFQN): Unit = createTableScala(TableManager.readTableDesc(tableFQN))
  def createTableScala(tableDesc: TableDesc): Unit = ???
  def createSchemaScala(schemaFQN: SchemaFQN): Unit = {
    // folder and file
    ???

    // val path = SparkEnv.idResolver.fqnToPath(schemaFQN)
    // val packageScalaPath = s"$path/package.scala"
    // val packageScalaContent =
    //   s"""package ${schemaFQN.substring(0, schemaFQN.lastIndexOf('.'))}
    //      |
    //      |package object ${schemaFQN.substring(schemaFQN.lastIndexOf('.') + 1)} {}
    //      |""".stripMargin

    // ResourceLoader.writeFile(packageScalaPath, packageScalaContent)
  }

}
