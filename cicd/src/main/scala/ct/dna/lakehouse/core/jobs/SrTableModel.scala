package ct.dna.lakehouse.core.jobs

import com.fasterxml.jackson.annotation.JsonProperty

final case class SrTableDef(
    schema: Map[String, Map[String, TableDefinition]]
)

final case class TableDefinition(
    url: Option[String] = None,
    error: Option[String] = None,
    response: Option[TableResponse] = None,
    columns: Option[List[ColumnDefinition]] = None
) {

  /** Resolves columns from either the flat format (direct `columns`) or the wrapped format (`response.columns`). */
  def resolvedColumns: List[ColumnDefinition] =
    columns.orElse(response.map(_.columns)).getOrElse(Nil)
}

final case class TableResponse(
    columns: List[ColumnDefinition] = Nil
)

final case class ColumnDefinition(
    name: String,
    description: Option[String] = None,
    @JsonProperty("type") columnType: String,
    length: Option[Long] = None,
    decimalsCount: Option[Long] = None,
    isPrimaryKey: Boolean,
    referenceField: Option[String] = None,
    referenceTable: Option[String] = None
)

sealed trait ColumnSource {
  def columns: Seq[ColumnDefinition]
}

object ColumnSource {
  final case class Theobald(columns: Seq[ColumnDefinition]) extends ColumnSource
  final case class Custom(columns: Seq[ColumnDefinition]) extends ColumnSource

  case object Cdc extends ColumnSource {
    override val columns: Seq[ColumnDefinition] = Seq.empty
  }
}

final case class SrRawFieldInfo(
    name: String,
    scalaType: String,
    isPrimaryKey: Boolean,
    isNotNull: Boolean,
    decimalAnnotation: Option[String] = None
)
