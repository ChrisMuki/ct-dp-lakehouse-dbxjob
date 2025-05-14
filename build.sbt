ThisBuild / organization := "ct.dna"
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / scalaVersion := "2.13.16"
ThisBuild / crossScalaVersions := Seq("2.13.16", "3.5.2")

ThisBuild / scalacOptions ++= Seq("-deprecation", "-unchecked", "-Wunused:imports")
ThisBuild / javacOptions ++= Seq("-Xlint:deprecation")
ThisBuild / resolvers += "Artifactory" at "https://eu.artifactory.conti.de/artifactory/ct_dsf_sbt_l/"
ThisBuild / outputStrategy := Some(StdoutOutput)
ThisBuild / Test / logBuffered := true

//https://mvnrepository.com/artifact/com.azure/azure-sdk-bom/1.2.30
lazy val root = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "dp-lakehouse-dbxjob",
    libraryDependencies ++= Seq(
      "ct.dna" %% "dp-pipeline" % "2.1.6",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.22.1" % Provided, // provided by DBX 16.4
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.22.1" % Runtime, // but needed for local developing
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2" % Provided, // provided by DBX 16.4
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2" % Runtime, // but needed for local developing
      

      // Required Test libraries
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )
