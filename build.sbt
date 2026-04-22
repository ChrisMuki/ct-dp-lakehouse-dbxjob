import sbt._
import Keys._

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
      "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
      "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED"
    )
  )
)

val dnaBomVersion = "1.3.4"

// lazy val srGenerator: this will contain the code to generate sr_raw and sr tablespec together with their ColumnWithNameAccessors

lazy val lakehouse = project
  .in(file("lakehouse"))
  .enablePlugins(DbxAssemblyPlugin)
  .settings(
    name := "lakehouse",
    assembly / assemblyJarName := "lakehouse.jar",
    useDnaBom(dnaBomVersion)(
      // DBR Runtime
      "dbx-runtime" % Provided,
      // Application Libs
      "common-utils",
      "dataplatform-core",
      "lakehouse-core",
      // Test only
      "local-spark-runtime" % Test
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

// will be reanmeded to devops later: this will contain the code to all ColumnWithNameAccessors and also ghe related scala files (deployment, ...)
lazy val cicd = project
  .in(file("cicd"))
  .dependsOn(lakehouse)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "cicd",
    run / fork := true,
    assembly / skip := true,
    useDnaBom(dnaBomVersion)(
      "deploy-utils",
      "lakehouse-modelbuilder",
      "local-spark-runtime"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

lazy val root = project
  .in(file("."))
  .aggregate(
    // srGenerator,
    lakehouse,
    cicd
  )
  .settings(
    assembly / skip := true,
    name := "multi-project-root"
  )
