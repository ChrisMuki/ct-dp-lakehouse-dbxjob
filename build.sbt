ThisBuild / organization := "ct.dna"
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / scalaVersion := "2.13.16"

ThisBuild / scalacOptions ++= Seq("-deprecation", "-unchecked", "-Wunused:imports", "-Ymacro-annotations", "-language:experimental.macros")

ThisBuild / javacOptions ++= Seq("-Xlint:deprecation")
ThisBuild / resolvers += "Artifactory Realm" at "https://artifacts.ws.contitech.cloud/artifactory/ctdna-sbt"
ThisBuild / outputStrategy := Some(StdoutOutput)
ThisBuild / publishTo := Some("Artifactory Realm" at "https://artifacts.ws.contitech.cloud/artifactory/ctdna-sbt")

ThisBuild / credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

ThisBuild / javaOptions ++= Seq(
  "-Xms1g",
  "-Xmx8g",
  "-Xms1g",
  "-Xmx8g",
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

import sbt._
import Keys._
import Tests._

ThisBuild / Test / logBuffered := true
ThisBuild / Test / fork := true
ThisBuild / Test / parallelExecution := true

lazy val lakehouse = project
  .in(file("lakehouse"))
.enablePlugins(DbxAssemblyPlugin)
  .settings(
    name := "lakehouse",
    assembly / assemblyJarName := "lakehouse.jar",
    libraryDependencies ++= Seq(
      // DBR Runtime
      "ct.dna" %% "dbx-runtime" % "1.1.0" % Provided,
      // Application Libs
      "ct.dna" %% "common-utils" % "1.10.1",
      "ct.dna" %% "lakehouse-core" % "1.10.2",
      // Test only
      "ct.dna" %% "local-spark-runtime" % "1.1.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

lazy val cicd = project
  .in(file("cicd"))
  .dependsOn(lakehouse)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "cicd",
    assembly / skip := true,
    libraryDependencies ++= Seq(
      "ct.dna" %% "deploy-utils" % "1.10.0",
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

lazy val root = project
  .in(file("."))
  .aggregate(lakehouse, cicd)
  .settings(
    assembly / skip := true,
    name := "multi-project-root"
  )
