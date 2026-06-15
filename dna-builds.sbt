import sbt._
import Keys._

val maxParallelism = math.min(8, java.lang.Runtime.getRuntime.availableProcessors)
val workspaceLocalIvy = Resolver.file(
  "workspace-local-ivy",
  file("/home/alfons/fue-scoptic/contitech/.workspace-cache/ivy2/local")
)(Resolver.ivyStylePatterns)
val workspaceLocalMaven = "workspace-local-maven" at "file:///home/alfons/fue-scoptic/contitech/.workspace-cache/m2"

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
    resolvers ++= Seq(workspaceLocalMaven, Resolver.mavenCentral),
    outputStrategy := Some(StdoutOutput),
    publishTo := Some(workspaceLocalIvy),
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
