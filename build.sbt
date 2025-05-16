ThisBuild / organization := "ct.dna"
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / scalaVersion := "2.13.16"

ThisBuild / scalacOptions ++= Seq("-deprecation", "-unchecked", "-Wunused:imports")
ThisBuild / javacOptions ++= Seq("-Xlint:deprecation")
ThisBuild / resolvers += "Artifactory" at "https://eu.artifactory.conti.de/artifactory/ct_dsf_sbt_l/"
ThisBuild / outputStrategy := Some(StdoutOutput)
ThisBuild / Test / logBuffered := true
ThisBuild / Test / parallelExecution := false

javaOptions += "--add-opens=java.base/java.nio=ALL-UNNAMED"
Test / fork := true

//https://mvnrepository.com/artifact/com.azure/azure-sdk-bom/1.2.30
lazy val root = project
  .in(file("."))
  .settings(
    name := "dp-lakehouse-dbxjob",
    libraryDependencies ++= Seq(
      "ct.dna" %% "dp-pipeline" % "3.1.0",
      // must be prior to 'spark-sql'
      "org.apache.spark" %% "spark-connect-client-jvm" % "4.0.0-preview2" % Test,
      "org.apache.spark" %% "spark-core" % "4.0.0-preview2" % Test,
      "org.apache.spark" %% "spark-sql" % "4.0.0-preview2" % Test,
      // "io.delta" %% "delta-spark" % "4.0.0" % Test,
      // "org.apache.spark" %% "spark-connect-client-jvm" % "4.0.0-preview2" % Provided,
      "org.apache.spark" %% "spark-core" % "4.0.0-preview2" % Provided,
      "org.apache.spark" %% "spark-sql" % "4.0.0-preview2" % Provided,
      // "io.delta" %% "delta-spark" % "4.0.0" % Provided,
      "io.github.classgraph" % "classgraph" % "4.8.179",

      // Required Test libraries
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )
