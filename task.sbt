import sbt._
import sbt.Keys._
import java.util.jar.JarFile

lazy val checkAndLogClassPathIssues = taskKey[Unit]("Map class names to their source JARs/files")

checkAndLogClassPathIssues := {
  val log = streams.value.log
  val cp = (assembly / fullClasspath).value

//   val classToOrigin = scala.collection.mutable.Map.empty[String, Attributed[File]]

//   cp.foreach { attributed =>
//     val file = attributed.data

//     if (file.isFile && file.getName.endsWith(".jar")) {
//       val jar = new JarFile(file)
//       val entries = jar.entries()
//       while (entries.hasMoreElements) {
//         val entry = entries.nextElement()
//         if (entry.getName.endsWith(".class") && !entry.isDirectory) {
//           val className = entry.getName.stripSuffix(".class").replace('/', '.')
//           classToOrigin(className) = attributed
//         }
//       }
//       jar.close()
//     } else if (file.isDirectory) {
//       // Recursively scan .class files in directory
//       val basePath = file.getAbsolutePath
//       val classFiles = Path.allSubpaths(file).filter(_._1.getName.endsWith(".class"))
//       classFiles.foreach { case (classFile, _) =>
//         val relPath = classFile.getAbsolutePath.stripPrefix(basePath).stripPrefix(java.io.File.separator)
//         val className = relPath.stripSuffix(".class").replace(java.io.File.separatorChar, '.')
//         classToOrigin(className) = attributed
//       }
//     }
//   }

//   // Example: print conflicts or list
//   log.info("Collected classes and origins:")
//   classToOrigin.toSeq.sortBy(_._1).foreach { case (cls, attr) =>
//     log.info(s"$cls -> ${attr.data.getName}")
//   }

  // Optionally return or store the map for later use
}
