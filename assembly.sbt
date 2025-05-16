import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial
import sbt._
import sbt.Keys._
import java.util.jar.JarFile

assembly / assemblyJarName := "dp-lakehouse-dbxjob.jar"

assembly / assemblyShadeRules := DBX_Runtime_Assembly.shadedLibs.map(moduleID => ShadeRule.rename("*.**" -> "ct.dna.shaded.@0").inLibrary(moduleID))

assembly / assemblyExcludedJars := {
  val log = streams.value.log
  val cp = (assembly / fullClasspath).value

  // gather definition of DBR and also our shading rules
  val DBR_libs = DBX_Runtime_Assembly.includedLibs.map(module => (module.organization, module.name) -> module.revision).toMap
  val toBeShaded_libs = DBX_Runtime_Assembly.shadedLibs.map(module => (module.organization, module.name) -> module.revision).toMap

  // calculate with libs to exclude
  val project_libs = cp.map(_.get(moduleID.key).map { module => (module.organization, module.name) -> module.revision }.getOrElse(("", ""), "")).toMap
  val excluded_dbr_libs = project_libs.filter { case (lib, rev) => DBR_libs.getOrElse(lib, "") == rev }
  val excluded_shaded_libs = project_libs.filter { case (lib, rev) => toBeShaded_libs.getOrElse(lib, "") == rev }
  val (kept_missmatch_libs, kept_good_libs) =
    project_libs.filterNot { case (lib, rev) => DBR_libs.getOrElse(lib, "") == rev }.filterNot { case (lib, rev) => toBeShaded_libs.getOrElse(lib, "") == rev }.partition {
      case (lib, _) => DBR_libs.contains(lib) || toBeShaded_libs.contains(lib)
    }

  val finalExcludedJars = cp.filter(attr =>
    attr.get(moduleID.key).exists { module =>
      excluded_dbr_libs.contains((module.organization, module.name))
      // || excluded_shaded_libs.contains((module.organization, module.name))
    }
  )

  // Only Logging purposes
  {
    excluded_dbr_libs.map { case (lib, rev) => "Excluded (part of DBR): \"" + lib._1 + "\" % \"" + lib._2 + "\" % \"" + rev + "\"" }.toSeq.sorted.foreach(log.info(_))
    excluded_shaded_libs.map { case (lib, rev) => "Excluded (shaded): \"" + lib._1 + "\" % \"" + lib._2 + "\" % \"" + rev + "\"" }.toSeq.sorted.foreach(log.info(_))
    kept_good_libs.map { case (lib, rev) => "Included: \"" + lib._1 + "\" % \"" + lib._2 + "\" % \"" + rev + "\"" }.toSeq.sorted.foreach(log.info(_))

    if (kept_missmatch_libs.size > 0) {
      kept_missmatch_libs.toSeq.sorted.foreach { case (lib, rev) =>
        log.error("Consider Shading or change dependencies: \"" + lib._1 + "\" % \"" + lib._2 + "\" % \"" + rev + "\" - DBX rev  \"" + DBR_libs(lib))
      }
      sys.error("Failing as we have conflicting versions.")
    }
  }

  finalExcludedJars
}

assembly / assemblyMergeStrategy := {
  case PathList("ct", "dna", "shaded", "module-info.class") => MergeStrategy.discard
  case PathList("ct", "dna", "shaded", "LICENSE")           => MergeStrategy.discard
  case PathList("ct", "dna", "shaded", "NOTICE")            => MergeStrategy.discard
  case PathList("ct", "dna", "shaded", "META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("module-info.class")                        => MergeStrategy.discard
  case PathList("LICENSE")                                  => MergeStrategy.discard
  case PathList("NOTICE")                                   => MergeStrategy.discard
  case PathList("META-INF", xs @ _*)                        => MergeStrategy.discard
  case x                                                    => MergeStrategy.singleOrError
}
