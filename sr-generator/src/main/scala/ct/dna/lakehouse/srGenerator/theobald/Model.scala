package ct.dna.lakehouse.srGenerator.theobald

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/** Case classes mirroring the structure of `theobald.json`.
  *
  * Top-level shape:
  * {{{
  * {
  *   "schema": {
  *     "<SCHEMA_NAME>": {
  *       "<TABLE_NAME>": {
  *         "url": "...",
  *         "error": "..." | absent,
  *         "response": { "columns": [ { name, description, type, length?, decimalsCount?, isPrimaryKey } ] } | absent
  *       }
  *     }
  *   }
  * }
  * }}}
  *
  * Tables that have an `error` (or are missing the `response`) carry no usable column metadata and are skipped by the generator.
  */

final case class Model(schema: Map[String, Map[String, Model.Table]])

object Model {

  @JsonIgnoreProperties(Array("url", "error"))
  final case class Table(response: Option[Response] = None)

  final case class Response(columns: Seq[Column])

  /** Source-system column definition as published by Theobald. */
  @JsonIgnoreProperties(Array("description", "referenceTable", "referenceField"))
  final case class Column(
      name: String,
      `type`: String,
      length: Option[Long] = None,
      decimalsCount: Option[Long] = None,
      isPrimaryKey: Boolean
  )
}
