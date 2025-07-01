package ct.dna.lakehouse.metastore

abstract class TableDef(implicit _schemaDef: SchemaDef) extends UnityObject("Table") {
  self: Origin =>
  val catalogDef = _schemaDef.catalogDef
  val schemaDef = _schemaDef
  val name = UnityObject.deriveTableName(this)

  val unityPath = s"${schemaDef.unityPath}.$name"

  // val automaticMetaColumn: Boolean = true

  val keys: Seq[(String, ColType)]
  val values: Seq[(String, ColType)]

}

abstract class SRTableDef(implicit _schema: SchemaDef) extends TableDef() with Origin.Loaded {
  override val name = UnityObject.deriveTableName(this).toLowerCase()
}

/*

CREATE TABLE my_table (
  id INT,
  name STRING,
  created_at TIMESTAMP
)
USING DELTA
LOCATION '/delta/my_table'
TBLPROPERTIES ('created.by' = 'init-script')
AS
SELECT
  CAST(NULL AS INT),
  CAST(NULL AS STRING),
  CAST(NULL AS TIMESTAMP);

 */
