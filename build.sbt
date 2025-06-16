ThisBuild / organization := "ct.dna"
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / scalaVersion := "2.13.16"

ThisBuild / scalacOptions ++= Seq("-deprecation", "-unchecked", "-Wunused:imports")
ThisBuild / javacOptions ++= Seq("-Xlint:deprecation")
ThisBuild / resolvers += "Artifactory" at "https://eu.artifactory.conti.de/artifactory/ct_dsf_sbt_l/"
ThisBuild / outputStrategy := Some(StdoutOutput)
ThisBuild / Test / logBuffered := true
ThisBuild / Test / parallelExecution := false

javaOptions ++= Seq("-Xms1024m", "-Xmx3584m", "-Xss4M", "-XX:ReservedCodeCacheSize=128m", "--add-opens=java.base/java.nio=ALL-UNNAMED")
Test / fork := true
fork := true

//https://mvnrepository.com/artifact/com.azure/azure-sdk-bom/1.2.30
lazy val root = project
  .in(file("."))
  .settings(
    name := "dp-lakehouse-dbxjob",
    libraryDependencies ++= Seq(
      "ct.dna" %% "dp-pipeline" % "3.1.1",
      "org.apache.spark" %% "spark-connect-client-jvm" % "4.0.0" % Test,
      "org.apache.spark" %% "spark-connect-client-jvm" % "4.0.0" % Provided,
      "io.delta" %% "delta-connect-client" % "4.0.0" % Test,
      "io.delta" %% "delta-connect-client" % "4.0.0" % Provided,

      // When using "org.apache.spark" %% "spark-sql" % "4.0.0" % Provided, Worksheets does not work properly
      // "org.apache.spark" %% "spark-sql" % "4.0.0" % Provided,

      // For DAG
      "io.github.classgraph" % "classgraph" % "4.8.179",

      

      // Required Test libraries
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

// // Because sbt does stupid stuff and for some weird reason keeps spark-sql in test scope
// Test / fullClasspath := {
//   val cp = (Test / fullClasspath).value
//   cp.filterNot(attrF => {
//     val modO = attrF.get(moduleID.key).map { module => (module.organization, module.name) }
//     modO == Some(("org.apache.spark", "spark-sql_2.13"))
//   })
// }
// Test / unmanagedClasspath := {
//   val cp = (Test / unmanagedClasspath).value
//   cp.filterNot(attrF => {
//     val modO = attrF.get(moduleID.key).map { module => (module.organization, module.name) }
//     modO == Some(("org.apache.spark", "spark-sql_2.13"))
//   })
// }
