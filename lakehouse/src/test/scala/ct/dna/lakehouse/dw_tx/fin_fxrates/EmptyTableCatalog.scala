package ct.dna.lakehouse.dw_tx.fin_fxrates

import java.util
import java.util.Collections

import org.apache.spark.sql.connector.catalog._
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.connector.read._
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

/** Minimal V2 catalog for testing: serves empty tables with a given schema.
  *
  * Registered at runtime via:
  * {{{
  * spark.conf.set("spark.sql.catalog.sr_raw", classOf[EmptyTableCatalog].getName)
  * }}}
  *
  * Only supports `createTable` and `loadTable`; all tables return zero rows.
  */
class EmptyTableCatalog extends TableCatalog with SupportsNamespaces {

  private var _name: String = _
  private val tables = new util.concurrent.ConcurrentHashMap[String, StructType]()
  private val namespaces: util.Set[String] = Collections.newSetFromMap(new util.concurrent.ConcurrentHashMap[String, java.lang.Boolean]())

  override def initialize(name: String, options: CaseInsensitiveStringMap): Unit =
    _name = name

  override def name(): String = _name

  // --- TableCatalog ---

  override def listTables(namespace: Array[String]): Array[Identifier] = Array.empty

  override def loadTable(ident: Identifier): Table = {
    val key = ident.namespace().mkString(".") + "." + ident.name()
    val schema = tables.get(key)
    if (schema == null)
      throw new org.apache.spark.sql.catalyst.analysis.NoSuchTableException(ident)
    new EmptyReadableTable(ident.name(), schema)
  }

  override def createTable(
      ident: Identifier,
      columns: Array[Column],
      partitions: Array[Transform],
      properties: util.Map[String, String]
  ): Table = {
    import org.apache.spark.sql.types._
    val schema = StructType(columns.map { col =>
      StructField(col.name(), col.dataType(), col.nullable())
    })
    val key = ident.namespace().mkString(".") + "." + ident.name()
    tables.put(key, schema)
    new EmptyReadableTable(ident.name(), schema)
  }

  override def alterTable(ident: Identifier, changes: TableChange*): Table =
    throw new UnsupportedOperationException

  override def dropTable(ident: Identifier): Boolean = {
    val key = ident.namespace().mkString(".") + "." + ident.name()
    tables.remove(key) != null
  }

  override def renameTable(from: Identifier, to: Identifier): Unit =
    throw new UnsupportedOperationException

  // --- SupportsNamespaces ---

  override def listNamespaces(): Array[Array[String]] =
    namespaces.toArray(Array.empty[String]).map(_.split("\\.").asInstanceOf[Array[String]])

  override def listNamespaces(namespace: Array[String]): Array[Array[String]] = Array.empty

  override def loadNamespaceMetadata(namespace: Array[String]): util.Map[String, String] = {
    val key = namespace.mkString(".")
    if (!namespaces.contains(key))
      throw new org.apache.spark.sql.catalyst.analysis.NoSuchNamespaceException(namespace)
    util.Collections.emptyMap()
  }

  override def createNamespace(namespace: Array[String], metadata: util.Map[String, String]): Unit =
    namespaces.add(namespace.mkString("."))

  override def alterNamespace(namespace: Array[String], changes: NamespaceChange*): Unit = ()

  override def dropNamespace(namespace: Array[String], cascade: Boolean): Boolean =
    namespaces.remove(namespace.mkString("."))
}

/** A Table that supports reading but always returns zero rows. */
private class EmptyReadableTable(tableName: String, tableSchema: StructType) extends Table with SupportsRead {

  override def name(): String = tableName
  override def schema(): StructType = tableSchema

  override def capabilities(): util.Set[TableCapability] =
    util.EnumSet.of(TableCapability.BATCH_READ)

  override def newScanBuilder(options: CaseInsensitiveStringMap): ScanBuilder =
    () => new EmptyScan(tableSchema)
}

/** A Scan that produces zero rows. */
private class EmptyScan(readSchema: StructType) extends Scan with Batch {
  override def readSchema(): StructType = readSchema
  override def toBatch(): Batch = this
  override def planInputPartitions(): Array[InputPartition] = Array.empty

  override def createReaderFactory(): PartitionReaderFactory =
    (_: InputPartition) =>
      new PartitionReader[org.apache.spark.sql.catalyst.InternalRow] {
        override def next(): Boolean = false
        override def get(): org.apache.spark.sql.catalyst.InternalRow = null
        override def close(): Unit = ()
      }
}
