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

val dnaBomVersion = "2.3.0"

lazy val srGenerator = project
  .in(file("sr-generator"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "sr-generator",
    run / fork := true,
    assembly / skip := true,
    useDnaBom(dnaBomVersion)(
      "lakehouse-modelbuilder",
      "local-spark-runtime"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

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

// will be renamed to devops later: this will contain the code to all ColumnWithNameAccessors and also the related scala files (deployment, ...)
lazy val cicd = project
  .in(file("cicd"))
  .dependsOn(srGenerator)
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

// almond: notebook playground subproject. Hosts custom Scala helpers and Jupyter
// notebooks. The Almond kernel itself is installed/managed via coursier (`cs`),
// not via sbt — so this project has no Almond dependencies. The `writeClasspath`
// task exports the project's runtime classpath to a file that the Almond kernel
// wrapper script prepends to the kernel JVM. Add `dependsOn(...)` here when you
// want notebooks to import code from another subproject.
lazy val almond = project
  .in(file("almond"))
  // .dependsOn(srGenerator)
  .settings(
    name := "almond",
    run / fork := true,
    assembly / skip := true,
    publish / skip := true,
    useDnaBom(dnaBomVersion)(
      "common-utils",
      "dataplatform-core",
      "lakehouse-core",
      "local-spark-runtime"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    ),
    // Write the Compile classpath (this project + any dependsOn) to a file. The
    // Almond kernel wrapper script reads it and prepends it to the kernel JVM.
    // Built from dependencyClasspath + own classDirectory (instead of
    // fullClasspath / exportedProducts) to avoid a task-graph cycle when this
    // task is hooked into Compile / compile below.
    TaskKey[File]("writeClasspath", "Write Almond runtime classpath to almond/target/classpath.txt") := {
      val deps = (Compile / dependencyClasspath).value.map(_.data.getAbsolutePath)
      val ownDir = (Compile / classDirectory).value.getAbsolutePath
      val entries = ownDir +: deps
      val cp = entries.mkString(java.io.File.pathSeparator)
      val out = target.value / "classpath.txt"
      IO.write(out, cp)
      streams.value.log.info(s"Wrote Almond classpath (${entries.size} entries) to $out")
      out
    },
    // Refresh the classpath file after every successful Compile / compile so the
    // kernel always sees the latest classpath. Sequenced (compile first, then
    // writeClasspath) by reading both `.value`s in the body of the override.
    Compile / compile := {
      val analysis = (Compile / compile).value
      TaskKey[File]("writeClasspath").value
      analysis
    }
  )

lazy val root = project
  .in(file("."))
  .aggregate(
    srGenerator,
    lakehouse,
    cicd,
    almond
  )
  .settings(
    assembly / skip := true,
    name := "multi-project-root"
  )
