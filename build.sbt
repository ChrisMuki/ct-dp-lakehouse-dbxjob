import sbt._
import Keys._

// Shortcut task: runs the GenerateColumnWithNameAccessor main in the `devops`
// project. Invoke as `generateColumns` (or `devops/generateColumns`).
lazy val generateColumns = taskKey[Unit](
  "Run ct.dna.lakehouse.core.GenerateColumnWithNameAccessor in the devops project"
)

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

val dnaBomVersion = "4.2.3"
val lakehouseSrVersion = "0.9.0"

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
    Test / unmanagedResourceDirectories += (ThisBuild / baseDirectory).value / "config",
    libraryDependencies ++= Seq(
      "ct.dna" %% "lakehouse-sr" % lakehouseSrVersion,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )

lazy val devops = project
  .in(file("devops"))
  .dependsOn(lakehouse)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "devops",
    run / fork := true,
    assembly / skip := true,
    useDnaBom(dnaBomVersion)(
      "deploy-utils",
      "lakehouse-modelbuilder",
      "local-spark-runtime"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    ),
    generateColumns := (Compile / runMain)
      .toTask(" ct.dna.lakehouse.core.GenerateColumnWithNameAccessor")
      .value
  )

// almond: notebook playground subproject. Hosts custom Scala helpers and Jupyter
// notebooks. The Almond kernel itself is installed/managed via coursier (`cs`),
// not via sbt — so this project has no Almond dependencies. The `writeClasspath`
// task exports the project's runtime classpath to a file that the Almond kernel
// wrapper script prepends to the kernel JVM. Add `dependsOn(...)` here when you
// want notebooks to import code from another subproject.
lazy val almond = project
  .in(file("almond"))
  .dependsOn(lakehouse)
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
    lakehouse,
    devops,
    almond
  )
  .settings(
    assembly / skip := true,
    name := "multi-project-root",
    generateColumns := (devops / generateColumns).value
  )
