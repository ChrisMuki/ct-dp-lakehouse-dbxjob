import sbt._
import Keys._

val maxParallelism = math.min(8, math.max(2, java.lang.Runtime.getRuntime.availableProcessors / 4))
val artifactoryResolver = "Artifactory Realm" at "https://artifacts.ws.contitech.cloud/artifactory/ctdna-sbt"

// -----------------------------------------------------------------------------
// Build settings
// -----------------------------------------------------------------------------
inThisBuild(
  Seq(
    // Project identity
    organization := "ct.dna",
    versionScheme := Some("semver-spec"),
    scalaVersion := "2.13.16",
    // Tooling and compiler
    semanticdbEnabled := true,
    scalafmtOnCompile := true,
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Wunused:imports",
      "-Ymacro-annotations",
      "-language:experimental.macros",
      // Speeds Scala code generation on multi-core machines.
      "-Ybackend-parallelism",
      maxParallelism.toString,
      // Reuses macro classloaders between compiles when sources are unchanged.
      "-Ycache-macro-class-loader:last-modified"
    ),
    javacOptions ++= Seq("-Xlint:deprecation"),
    // Publishing and resolvers
    resolvers += artifactoryResolver,
    outputStrategy := Some(StdoutOutput),
    publishTo := Some(artifactoryResolver),
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
    // Test defaults
    Test / logBuffered := true,
    Test / fork := true,
    Test / parallelExecution := true
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges

Global / concurrentRestrictions :=
  (Global / concurrentRestrictions).value.filterNot(_.toString.contains("forked-test-group")) :+
    Tags.limit(Tags.ForkedTestGroup, maxParallelism)


