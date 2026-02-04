package ct.dna.lakehouse
import ct.dna.utils.runtime.Configuration
import ct.dna.utils.ResourceLoader

object Sync_sr_raw {

  def main(args: Array[String]): Unit = {

    val config = args.toList match {
      case Nil          => "sync_sr_raw/dev.json"
      case "dev" :: Nil => "sync_sr_raw/dev.json"
    }

    Configuration.build(Array("configFile=" + config))

    println("Sync_sr_raw test running...")
    // Add test logic here
  }

}
