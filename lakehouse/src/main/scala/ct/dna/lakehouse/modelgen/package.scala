package ct.dna.lakehouse

/** Model generation utilities for creating TableSpec code from Unity Catalog.
  *
  * ==Generated File Structure==
  *
  * The generator creates proper Scala source code structure:
  *
  * {{{
  * <outputDir>/
  *   sr_raw/                           # Catalog folder
  *     package.scala                   # package object sr_raw extends CatalogSpec {}
  *     my_schema/                      # Schema folder
  *       package.scala                 # package object my_schema extends SchemaSpec {}
  *       tables.scala                  # Entity case classes + TableSpec objects
  *     another_schema/
  *       package.scala
  *       tables.scala
  * }}}
  */
package object modelgen {}
