// import ct.dna.lakehouse.catalogs.dw_tx.iamer_norz.Orders
// import ct.dna.lakehouse.DAG
// DAG.buildDependencyGraph()

// //Create Table in Catalog once => Store code to create automatic generation "Later"
// //Try to use TransformationExecutor manaully to fill table with Life

// // do it properly:
// // split Transformation Executor into single methods, that are implemented test driven



val s = ct.dna.utils.macros.Struct.Of[ct.dna.lakehouse.catalogs.dw_tx.iamer_norz.Order]



s.AsTarget._mk_createdAt_String

s.KeyColumnNames
s.ValueColumnNames
s.AllColumnNames