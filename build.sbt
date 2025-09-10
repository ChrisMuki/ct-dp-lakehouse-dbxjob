ThisBuild / organization := "ct.dna"
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / scalaVersion := "2.13.16"

ThisBuild / scalacOptions ++= Seq("-deprecation", "-unchecked", "-Wunused:imports")
ThisBuild / javacOptions ++= Seq("-Xlint:deprecation")
ThisBuild / resolvers += "Artifactory" at "https://eu.artifactory.conti.de/artifactory/ct_dsf_sbt_l/"
ThisBuild / outputStrategy := Some(StdoutOutput)
ThisBuild / Test / logBuffered := true
ThisBuild / Test / parallelExecution := false

javaOptions ++= Seq(
  "-Xms1024m",
  "-Xmx3584m",
  "-Xss4M",
  "-XX:ReservedCodeCacheSize=128m",
  "--add-opens=java.base/java.nio=ALL-UNNAMED",
  "--add-opens=java.base/java.net=ALL-UNNAMED",
  "--add-opens=java.base/java.lang=ALL-UNNAMED",
  "--add-opens=java.base/java.util=ALL-UNNAMED",
  "--add-opens=java.base/java.util=ALL-UNNAMED",
  "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
  "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED"
)
Test / fork := true
fork := true

//https://mvnrepository.com/artifact/com.azure/azure-sdk-bom/1.2.30
lazy val root = project
  .in(file("."))
  .settings(
    name := "dp-lakehouse-dbxjob",
    libraryDependencies ++= Seq(
      "ct.dna" %% "lakehouse-framework" % "25.1.0",
      "ct.dna" %% "macro-utils" % "25.0.0",
      "ct.dna" %% "dbr-provided" % "25.0.0" % Provided,
      "ct.dna" %% "spark-test" % "25.2.0" % Test
    )
  )
