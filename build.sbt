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
  "--add-opens=java.base/java.net=ALL-UNNAMED"
)
Test / fork := true
fork := true

//https://mvnrepository.com/artifact/com.azure/azure-sdk-bom/1.2.30
lazy val root = project
  .in(file("."))
  .settings(
    name := "dp-lakehouse-dbxjob",
    libraryDependencies ++= Seq(
      "ct.dna" %% "common-utils" % "0.0.1",
      "ct.dna" %% "spark-utils" % "0.0.3",
      "org.apache.spark" %% "spark-sql" % "4.0.0" % Provided,
      "io.delta" %% "delta-spark" % "4.0.0" % Provided,

      // For DAG
      "io.github.classgraph" % "classgraph" % "4.8.179",

      // Required Test libraries
      // Spark
      "org.apache.spark" %% "spark-core" % "4.0.0" % Test,
      "org.apache.spark" %% "spark-sql" % "4.0.0" % Test,
      "org.apache.spark" %% "spark-connect-client-jvm" % "4.0.0" % Test,
      "org.apache.spark" %% "spark-hive" % "4.0.0" % Test,
      // Delta
      "io.delta" %% "delta-spark" % "4.0.0" % Test,
      "io.delta" %% "delta-connect-client" % "4.0.0" % Test,
      // Local Catalog
      "org.apache.derby" % "derby" % "10.16.1.1" % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )
