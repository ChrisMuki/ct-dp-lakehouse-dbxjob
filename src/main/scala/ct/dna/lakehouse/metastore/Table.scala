package ct.dna.lakehouse.metastore

import ct.dna.lakehouse.transformations.Origin

abstract class Table(implicit _schema: Schema) extends UnityObject("Table") {
  self: Origin =>
  val catalog = _schema.catalog
  val schema = _schema
  lazy val name = UnityObject.deriveTableName(this)

  lazy val unityPath = s"${schema.unityPath}.$name"

  // val automaticMetaColumn: Boolean = true

  val keys: Seq[(String, ColType)]
  val values: Seq[(String, ColType)]

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
