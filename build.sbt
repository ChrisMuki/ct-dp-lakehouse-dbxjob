import sbt._
import Keys._
import Tests._

inThisBuild(
  Seq(
    // Scope: applies to forked run/test JVMs, not the sbt launcher process.
    // Launcher JVM options are configured in .jvmopts.
    // See docs/sbt-jvm-settings.md for the full split rationale.
    javaOptions ++= Seq(
      "-Xms1g",
      "-Xmx8g",
      "-Xss4M",
      "-XX:+UseG1GC",
      "-XX:ReservedCodeCacheSize=128m",
      "--add-opens=java.base/java.nio=ALL-UNNAMED",
      "--add-opens=java.base/java.net=ALL-UNNAMED",
      "--add-opens=java.base/java.lang=ALL-UNNAMED",
      "--add-opens=java.base/java.util=ALL-UNNAMED",
      "--add-opens=java.base/java.util=ALL-UNNAMED",
      "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
      "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED"
    )
  )
)

lazy val lakehouse = project
  .in(file("lakehouse"))
  .enablePlugins(DbxAssemblyPlugin)
  .settings(
    name := "lakehouse",
    assembly / assemblyJarName := "lakehouse.jar",
    libraryDependencies ++= Seq(
      // DBR Runtime
      "ct.dna" %% "dbx-runtime" % "17.3.0" % Provided,
      // Application Libs
      "ct.dna" %% "common-utils" % "1.16.1",
      "ct.dna" %% "dataplatform-core" % "1.15.2",
      "ct.dna" %% "lakehouse-core" % "2.0.3",
      // Test only
      "ct.dna" %% "local-spark-runtime" % "17.3.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

lazy val cicd = project
  .in(file("cicd"))
  .dependsOn(lakehouse)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "cicd",
    run / fork := true,
    assembly / skip := true,
    libraryDependencies ++= Seq(
      "ct.dna" %% "deploy-utils" % "1.13.1",
      "ct.dna" %% "lakehouse-modelbuilder" % "1.2.1",
      "ct.dna" %% "local-spark-runtime" % "17.3.0",
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
