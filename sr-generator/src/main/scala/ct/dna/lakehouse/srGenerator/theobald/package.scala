package ct.dna.lakehouse.srGenerator

import ct.dna.dataplatform.NamingConvention
import ct.dna.lakehouse.core.model.SchemaID
import ct.dna.lakehouse.core.model.TableID
import ct.dna.lakehouse.core.modelbuilder.ColumnMod
import ct.dna.utils.ResourceLoader
import ct.dna.utils.json.mapper
import ct.dna.utils.logging.LoggingTrait

package object theobald extends LoggingTrait {

  // ---------- IO ----------

  def load(path: String): Model =
    ResourceLoader.withSystemClassLoader.getResourceAsStream(path) match {
      case Some(is) => mapper.readValue[Model](is)
      case None     => logAndThrow(new RuntimeException(s"Failed to load Theobald JSON from '$path': resource not found on classpath"))
    }

  // ---------- Parse ----------

  /** Derive [[ColumnMod]]s per sr_raw [[TableID]] purely from the Theobald JSON.
    *
    * For each column the sr_raw field name is `lowercase(name) + typeSuffix(type)`.
    *   - `isPrimaryKey` (excluding MANDT) → [[ColumnMod.MakeKey]]
    *   - field name differs from base name (has type suffix) → [[ColumnMod.Rename]]
    *
    * Returns only tables that have at least one ColumnMod.
    */
  def parse(model: Model): Map[TableID, SrTableInput] =
    model.schema.flatMap { case (_schemaName, tables) =>
      val schemaName = NamingConvention.Metastore.enforce(_schemaName)
      tables.flatMap { case (_tableName, table) =>
        val tableName = NamingConvention.Metastore.enforce(_tableName)
        val cols = table.response.map(_.columns).getOrElse(Seq.empty)
        if (cols.nonEmpty) {
          val tableId = TableID(SchemaID(SourceRawCatalogId, schemaName), tableName)
          val columnMods = cols.flatMap(toColumnMod)
          if (columnMods.nonEmpty) Some(tableId -> SrTableInput(WriteStrategy.ChangeKey, columnMods))
          else None
        } else None
      }
    }

  // ---------- Convenience ----------

  def loadAndParse(path: String): Map[TableID, SrTableInput] = parse(load(path))

  // ---------- ID helpers ----------

  def srTableIdFor(srRawTableId: TableID): TableID =
    TableID(
      SchemaID(SourceCatalogId, srRawTableId.schemaId.name),
      srRawTableId.name
    )

  // ---------- internal ----------

  private def toColumnMod(col: Model.Column): Option[ColumnMod] = {
    val baseName = NamingConvention.Metastore.enforce(col.name)
    val fieldName = resolveFieldName(baseName, col)
    val isPK = col.isPrimaryKey && col.name != "mandt" // MANDT is a special case: it's always a key in SAP but we don't want it to be a key in our tables

    if (isPK)
      Some(ColumnMod.MakeKey(fieldName, if (fieldName != baseName) Some(baseName) else None))
    else if (fieldName != baseName)
      Some(ColumnMod.Rename(fieldName, baseName))
    else
      None
  }

  private def resolveFieldName(baseName: String, col: Model.Column): String =
    NamingConvention.Metastore.enforce {
      col.`type` match {
        case "Date"                                                     => s"${baseName}_string"
        case "StringLengthMax" | "NumericString" | "Time" | "ByteArray" => s"${baseName}_string"
        case "ByteArrayLengthExact"                                     => s"${baseName}_string"
        case "Short" | "Byte" | "Int" | "Integer"                       => s"${baseName}_int"
        case "Long" | "BigInteger"                                      => s"${baseName}_long"
        case "Float"                                                    => s"${baseName}_float"
        case "Double"                                                   => s"${baseName}_double"
        case "Boolean"                                                  => s"${baseName}_boolean"
        case "Decimal" =>
          val len = col.length.map(_.toInt).getOrElse(13)
          val dec = col.decimalsCount.map(_.toInt).getOrElse(2)
          s"${baseName}_decimal_${len}_${dec}"
        case _ => throw new RuntimeException(s"Unrecognized Theobald column type '${col.`type`}' for column '${col.name}'")
      }
    }
}
