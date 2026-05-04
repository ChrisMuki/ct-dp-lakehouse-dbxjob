package ct.dna.lakehouse.srGenerator

import ct.dna.lakehouse.core.modelbuilder.ColumnMod

sealed trait WriteStrategy

object WriteStrategy {
  case object ChangeKey extends WriteStrategy
}

final case class SrTableInput(strategy: WriteStrategy, columnMods: Seq[ColumnMod])
