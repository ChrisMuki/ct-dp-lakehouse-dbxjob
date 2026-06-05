package ct.dna.lakehouse.deploy

import ct.dna.utils.SetOnce

/** A deployment stage ("dev" | "qual" | "prod"). */
sealed abstract class Stage(val name: String)

object Stage {
  case object Dev extends Stage("dev")
  case object Qual extends Stage("qual")
  case object Prod extends Stage("prod")

  val all: Seq[Stage] = Seq(Dev, Qual, Prod)

  /** The active deployment stage, set exactly once by [[Deploy]] from the `stage=` launcher argument before [[Config.stageConfig]] is read. */
  private val active: SetOnce[Stage] = SetOnce.empty

  /** Set the active stage from a stage name (case-insensitive); throws on anything but dev/qual/prod, or if already set. */
  def set(stage: String): Unit = active.set(parse(stage))

  /** Pick the dev / qual / prod variant of a value for the active stage. Anything wrapped in this is stage-specific by construction; a value written once is
    * the standard config, identical for dev, qual and prod.
    */
  def dqp[T](dev: T, qual: T, prod: T): T = active.get match {
    case Dev  => dev
    case Qual => qual
    case Prod => prod
  }

  def dqp[T](dev_qual: T, prod: T): T = active.get match {
    case Dev  => dev_qual
    case Qual => dev_qual
    case Prod => prod
  }

  /** Parse a stage name (case-insensitive); throws on anything but dev/qual/prod. */
  def parse(stage: String): Stage =
    all
      .find(_.name.equalsIgnoreCase(stage.trim))
      .getOrElse(throw new IllegalArgumentException(s"Unknown stage '$stage'. Valid stages: ${all.map(_.name).mkString(", ")}"))
}
