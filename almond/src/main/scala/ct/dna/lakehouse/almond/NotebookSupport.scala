package ct.dna.lakehouse.almond

import ct.dna.lakehouse.core.runtime.SparkConfig

/** Placeholder package object so the `almond` sbt subproject compiles even when no notebook-support sources have been added yet. Add reusable helpers for
  * notebooks here and they will be available to every kernel session via the sbt-managed classpath.
  */
object NotebookSupport {
  val greeting: String = "almond subproject is on the classpath"

  val config = SparkConfig.LocalSpark("local[*]", None, Some("./spark-warehouse"))
}
