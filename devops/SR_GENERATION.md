# SR Generation

## Scope

This document describes the **code-generation flow for `sr_raw` and `sr` catalogs** in this repository.

It does **not** describe Databricks Asset Bundle assembly or deployment. For deployment-related behavior, see [devops/SR_WORKFLOW_BUILDER.md](./SR_WORKFLOW_BUILDER.md).

The generation flow has two separate stages:

1. `GenerateSrRaw` builds `sr_raw` Scala sources from Unity Catalog metadata.
2. `GenerateSr` builds `sr` Scala sources from Theobald column definitions plus the already-generated `sr_raw` sources.

The dependency direction is important:

```text
Unity Catalog metadata
  -> GenerateSrRaw
  -> lakehouse/catalog/sr_raw/**

Theobald JSON + sr_raw Scala sources
  -> GenerateSr
  -> lakehouse/catalog/sr/**
```

## High-Level Goals

The repository keeps two separate views of the same source tables:

- `sr_raw`: a direct loaded/raw representation of the physical source data as discovered from Unity Catalog.
- `sr`: a business-facing `ChangeKey` view derived from `sr_raw`, using configured source-column semantics.

In practical terms:

- `sr_raw` is the structural foundation.
- `sr` is the curated model that decides which source fields become logical SR fields and how they are mapped.

## Stage 1: `GenerateSrRaw`

Implementation: [devops/src/main/scala/ct/dna/lakehouse/core/GenerateSrRaw.scala](./src/main/scala/ct/dna/lakehouse/core/GenerateSrRaw.scala)

### Purpose

`GenerateSrRaw` connects to the configured lakehouse environment, discovers the physical catalog structure, reads Unity table descriptions, and generates Scala source files under `lakehouse-sr/src/main/scala/ct/dna/lakehouse/sr_raw`.

### Inputs

Primary runtime config: [devops/src/main/resources/generate_sr_raw.json](./src/main/resources/generate_sr_raw.json)

Template: [devops/src/main/resources/generate_sr_raw.json.template](./src/main/resources/generate_sr_raw.json.template)

Key fields:

- `sparkConfig`: remote Spark / Databricks connection settings
- `baseDir`: output root for generated sources
- `basePackage`: package root, typically `ct.dna.lakehouse`
- `catalogId`: catalog to generate, typically `{"name": "sr_raw"}`

### What it does

`GenerateSrRaw` performs these steps:

1. Loads runtime configuration.
2. Initializes the Spark environment via `SparkEnv.ensureInitialized(...)`.
3. Resolves the requested catalog through `SparkEnv.idResolver`.
4. Finds schemas in that catalog, excluding `information_schema`.
5. Finds tables under each schema.
6. Reads the Unity `TableDesc` for each table via `TableManagerDelegation.readTableDesc(...)`.
7. Keeps only `TableDesc.UnityTableDesc` entries.
8. Builds Scala ASTs using external modelbuilder components:
   - `CatalogSpecAstBuilder`
   - `SchemaSpecAstBuilder`
   - `LoadedTableSpecAstBuilder`
9. Recreates the target catalog directory and writes generated files.

### Outputs

Generated structure:

```text
lakehouse-sr/src/main/scala/ct/dna/lakehouse/sr_raw/
  package.scala
  <schema>/
    package.scala
    <table>.scala
```

Typical generated `sr_raw` table shape:

```scala
case class E_a017(
    @PK _mk_org: String,
    @PK _mk_site: String,
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK _mk_partition: String,
    @PK _mk_file: String,
    @NotNull _mk_container: String,
    @NotNull _mk_account: String,
    @NotNull _mk_created_at: Timestamp,
    @PK _lh_id_in_message: Long,
    _lh_ingest_warning: String,
    matnr_string: String,
    ...
) extends Entity

object a017 extends TableSpec[E_a017](...) with Loaded
```

Representative file: [lakehouse-sr/src/main/scala/ct/dna/lakehouse/sr_raw/ct_gbl_p12/a017.scala](../lakehouse-sr/src/main/scala/ct/dna/lakehouse/sr_raw/ct_gbl_p12/a017.scala)

### Operational note

`GenerateSrRaw` rewrites the target catalog directory tree. Treat it as a generator-owned output.

## Stage 2: `GenerateSr`

Implementation: [devops/src/main/scala/ct/dna/lakehouse/core/GenerateSr.scala](./src/main/scala/ct/dna/lakehouse/core/GenerateSr.scala)

Builder: [devops/src/main/scala/ct/dna/lakehouse/core/modelbuilder/ChangeKeyTableSpecAstBuilder.scala](./src/main/scala/ct/dna/lakehouse/core/modelbuilder/ChangeKeyTableSpecAstBuilder.scala)

Model types: [devops/src/main/scala/ct/dna/lakehouse/core/jobs/SrTableModel.scala](./src/main/scala/ct/dna/lakehouse/core/jobs/SrTableModel.scala)

### Purpose

`GenerateSr` builds the curated `sr` catalog from two sources of truth:

1. `sr_table_def.json` provides configured source columns, source types, and primary-key intent.
2. Generated `sr_raw` Scala files provide the actual loaded field list, order, raw annotations, and raw source entity type.

This stage produces `ChangeKey` table definitions under `lakehouse/catalog/sr/**`.

### Inputs

Primary runtime config: [devops/src/main/resources/generate_sr.json](./src/main/resources/generate_sr.json)

Template: [devops/src/main/resources/generate_sr.json.template](./src/main/resources/generate_sr.json.template)

Key fields:

- `baseDir`: output root for generated SR sources
- `basePackage`: package root, typically `ct.dna.lakehouse.sr`
- `srTableDefPath`: path to Theobald column-definition JSON
- `srRawBaseDir`: path to generated `sr_raw` sources
- `filterSchema`: optional schema filter
- `filterTable`: optional table filter

The Theobald metadata file typically lives at:

- [resources/sr/sr_table_def.json](../resources/sr/sr_table_def.json)

### Source model in `SrTableModel.scala`

The generation pipeline currently uses these model types:

- `SrTableDef`: top-level JSON wrapper
- `TableDefinition`: per-table payload, supports both flat `columns` and wrapped `response.columns`
- `ColumnDefinition`: source-column definition from Theobald JSON
- `ColumnSource`: abstraction for configured source types
- `SrRawFieldInfo`: parsed field info extracted from generated `sr_raw` Scala source

`ColumnSource` already contains extension points:

- `ColumnSource.Theobald`
- `ColumnSource.Custom`
- `ColumnSource.Cdc`

Current wiring: `GenerateSr` uses `ColumnSource.Theobald(columns)` for all generated SR tables.

## `GenerateSr` Execution Flow

`GenerateSr` performs these steps:

1. Loads `generate_sr.json` configuration.
2. Reads and deserializes `sr_table_def.json`.
3. Filters schemas/tables if `filterSchema` or `filterTable` is set.
4. Skips tables that have no resolved Theobald columns.
5. Skips tables whose `sr_raw/<schema>/<table>.scala` source file does not exist.
6. Parses the `sr_raw` table source file to extract:
   - ordered field names
   - Scala types
   - `@PK`
   - `@NotNull`
7. Extracts the raw source entity type from the generated `object <table> extends TableSpec[...] with Loaded` line.
8. Builds the SR AST via `ChangeKeyTableSpecAstBuilder.build(...)`.
9. Writes schema package files if missing.
10. Writes table files for all generated tables.

## How `sr_raw` Is Parsed

`GenerateSr` does not use Scala reflection or compile the `sr_raw` source to understand fields. Instead, it parses the generated Scala source text directly.

Specifically, `loadSrRawFieldsForTable(...)`:

- opens `<srRawBaseDir>/<schema>/<table>.scala`
- scans only inside the `case class E_<table>(...) extends Entity` block
- extracts each field line with a regex
- records:
  - `name`
  - `scalaType`
  - whether the annotation block contains `@PK`
  - whether the annotation block contains `@NotNull`

This means SR generation depends on the generated `sr_raw` file format staying broadly stable.

## Merge Rules in `ChangeKeyTableSpecAstBuilder`

This is the core of SR generation.

### Inputs to the builder

The builder receives:

- package roots (`rootPackage`, `srRawRootPackage`)
- schema/table identity
- a `ColumnSource`
- the ordered `Seq[SrRawFieldInfo]`
- the raw entity type name (for `ChangeKey[...]`)

### Match strategy

For configured source columns, the builder attempts to resolve the corresponding `sr_raw` field name based on source type.

Examples:

- `MANDT` + `StringLengthMax` -> `mandt_string`
- `DATBI` + `Date` -> prefers `datbi_string`, falls back to `datbi_date`
- `PAYLOAD` + `ByteArrayLengthExact` -> prefers `payload_string`, falls back to `payload_binary`
- `TSVAL` + `Decimal(length, decimals)` -> `tsval_decimal_<len>_<scale>`

This allows SR generation to strip the technical suffix only for fields that are recognized as configured source columns.

### Configured source columns

If an `sr_raw` field matches a configured source field from Theobald JSON:

- the SR field name becomes the normalized logical source name
- the SR Scala type comes from the configured source type mapping
- the `preApplyMapping` entry points to the matching `sr_raw` field expression
- Theobald primary-key intent is applied
- `MANDT` is explicitly not marked as `@PK` even when Theobald marks it as primary key

Examples:

- `matnr_string` -> `matnr: String`
- `datbi_string` -> `datbi: String`
- `tsval_decimal_14_0` -> `@Decimal(15, 0) tsval: java.math.BigDecimal`

### Passthrough `sr_raw` fields

If an `sr_raw` field is **not** matched by configured source metadata, it is retained as a passthrough field.

Rules:

- field name stays exactly as it appears in `sr_raw`
- field type stays exactly as it appears in `sr_raw`
- `preApplyMapping` is a direct `col("<same_name>")`
- passthrough fields are never emitted as `@PK`
- if a passthrough field was `@PK` or `@NotNull` in `sr_raw` and its type supports `@NotNull`, it becomes `@NotNull` in SR
- primitive-like types (`Int`, `Long`, `Double`, `Float`, `Boolean`, `Byte`, `Short`) do not get `@NotNull`

This is the mechanism that preserves metadata fields such as:

- `_mk_org`
- `_mk_site`
- `_mk_system`
- `_mk_instance`
- `_mk_partition`
- `_mk_file`
- `_mk_container`
- `_mk_account`
- `_mk_created_at`
- `_lh_id_in_message`
- `_lh_ingest_warning`

### Why PK metadata is downgraded

Many technical `sr_raw` metadata fields are marked `@PK` because they participate in raw uniqueness. That does **not** mean they should become SR business keys.

Current SR behavior is therefore:

- configured source columns decide SR business PKs
- passthrough metadata fields are preserved structurally
- raw PK status on passthrough fields is downgraded to `@NotNull` when possible

### Example transformation

Input `sr_raw` fields:

```scala
@PK _mk_system: String,
@PK _mk_instance: String,
@NotNull _mk_created_at: Timestamp,
@PK _lh_id_in_message: Long,
mandt_string: String,
kappl_string: String,
kschl_string: String
```

Input Theobald definitions:

```text
MANDT  StringLengthMax  PK=true
KAPPL  StringLengthMax  PK=true
KSCHL  StringLengthMax  PK=true
```

Generated SR fields:

```scala
@NotNull _mk_system: String,
@NotNull _mk_instance: String,
@NotNull _mk_created_at: Timestamp,
_lh_id_in_message: Long,
mandt: String,
@PK kappl: String,
@PK kschl: String
```

Generated mapping entries:

```scala
("_mk_system", col("_mk_system")),
("_mk_instance", col("_mk_instance")),
("_mk_created_at", col("_mk_created_at")),
("_lh_id_in_message", col("_lh_id_in_message")),
("mandt", col("mandt_string")),
("kappl", col("kappl_string")),
("kschl", col("kschl_string"))
```

## Generated SR File Shape

Each SR table file contains:

1. package declaration under `ct.dna.lakehouse.sr.<schema>`
2. framework imports
3. generated SR entity case class
4. `object <table> extends TableSpec[...] with ChangeKey[...]`
5. `sourceTableSpec`
6. `sequenceBy`
7. `preApplyMapping`

The builder always emits:

- `sequenceBy = struct(col("_mk_created_at"), col("_lh_id_in_message"))`

That assumes those fields exist in the corresponding `sr_raw` model.

### Imports

Imports are computed dynamically:

- `Joined` is imported only when the generated entity exceeds Scala case-class parameter limits
- `PK` is imported only if at least one SR field is emitted as primary key
- `NotNull` is imported only if at least one SR field uses it
- `Decimal` is imported only if at least one decimal field is emitted
- `hex` is imported only if some mapping requires `hex(col(...))`

## Wide Table Handling

Scala case classes have practical constructor limits. The builder therefore supports wide SR entities.

Mechanism:

- each field gets a parameter weight
- `Long` and `Double` count as 2
- most other types count as 1
- if the total exceeds `MaxCaseClassFields = 254`, the builder splits value fields into multiple `Part` entities
- the final entity type becomes nested `Joined[...]`

This keeps very wide tables generatable without manual intervention.

## Schema Package Generation

For each schema that contains at least one generated table, `GenerateSr` prepares a schema package object:

```scala
package ct.dna.lakehouse.sr

import ct.dna.lakehouse.core.model.SchemaSpec

package object ct_gbl_p12 extends SchemaSpec {}
```

Important behavior:

- schema `package.scala` is only written if it does not already exist
- table files are always rewritten by the generator

## Running the Generators

### Generate `sr_raw`

Use the configured resource file:

```bash
sbt "devops/runMain ct.dna.lakehouse.core.GenerateSrRaw"
```

Or override config via arguments such as `configFile=...` depending on your local setup.

Because `generate_sr_raw.json` may contain environment-specific credentials, prefer the template file for committed defaults and keep local secrets out of source control.

### Generate `sr`

Generate all SR tables:

```bash
sbt "devops/runMain ct.dna.lakehouse.core.GenerateSr"
```

Generate a single schema/table subset:

```bash
sbt "devops/runMain ct.dna.lakehouse.core.GenerateSr baseDir=/workspaces/dp-lakehouse-dbxjob/lakehouse-sr/src/main/scala/ct/dna/lakehouse/sr basePackage=ct.dna.lakehouse.sr srTableDefPath=/workspaces/dp-lakehouse-dbxjob/resources/sr/sr_table_def.json srRawBaseDir=/workspaces/dp-lakehouse-dbxjob/lakehouse-sr/src/main/scala/ct/dna/lakehouse/sr_raw filterSchema=CT_GBL_P12 filterTable=A017"
```

Recommended order:

1. regenerate `sr_raw`
2. regenerate `sr`
3. run compile/tests

## Verification

At minimum, validate generation with:

```bash
sbt --batch -Dsbt.supershell=false devops/compile
sbt --batch -Dsbt.supershell=false "devops/testOnly ct.dna.lakehouse.core.GenerateSrProcessTest"
```

The focused SR-generation tests currently live in:

- [devops/src/test/scala/ct/dna/lakehouse/core/GenerateSrProcessTest.scala](./src/test/scala/ct/dna/lakehouse/core/GenerateSrProcessTest.scala)

These tests verify, among other things:

- core imports are generated correctly
- decimal metadata is preserved
- `hex` imports are included only when needed
- passthrough `sr_raw` fields are retained
- raw PK metadata is downgraded to `@NotNull` where appropriate

## Current Limitations and Extension Points

### What is implemented now

- `GenerateSrRaw` generates `sr_raw` from Unity metadata.
- `GenerateSr` uses `ColumnSource.Theobald`.
- configured Theobald columns become logical SR fields.
- unmatched `sr_raw` fields are retained as passthrough fields.

### What is prepared but not fully wired

- `ColumnSource.Cdc`
- `ColumnSource.Custom`

The shape is present in the model, but `GenerateSr` currently always instantiates `ColumnSource.Theobald(...)`.

### Future directions already anticipated by the code shape

- CDC tables that do not require Theobald JSON
- custom small JSON definitions for non-Theobald sources
- optional explicit drop-column rules
- source-specific primary-key policies

## Important Note About Checked-In Generated Files

The checked-in generated files under `lakehouse/catalog/sr/**` may temporarily lag behind the current generator behavior if the generator has not been rerun since a logic change.

When reviewing behavior, the source of truth is:

1. the generator implementation
2. the focused generator tests
3. freshly regenerated output

Do not assume an older checked-in SR file reflects the latest merge rules unless generation was rerun after the corresponding code change.