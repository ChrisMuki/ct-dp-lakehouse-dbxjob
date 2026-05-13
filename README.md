# dp-lakehouse-dbxjob

A Databricks Lakehouse job that ingests SAP and non-SAP data from multiple source systems, applies change-data-capture transformations, and materialises curated Delta tables in Unity Catalog. The project implements the **Data Platform Pipeline** pattern from `lakehouse-core` and deploys as a fat JAR via Databricks Asset Bundles.

## Architecture Overview

The lakehouse is split across a chain of layered sbt subprojects, mirrored by the package layout under `ct.dna.lakehouse`:

```
              16 SAP systems + 8 non-SAP sources
                         │
                ┌────────▼────────┐
                │     sr_raw      │   Raw ingestion layer (~856 tables)
                │   lakehouse-sr  │   Auto-generated `Loaded` TableSpecs
                └────────┬────────┘
                         │
                ┌────────▼────────┐
                │       sr        │   Standardised layer (~509 tables)
                │   lakehouse-sr  │   Auto-generated ChangeKey TableSpecs
                └────────┬────────┘
                         │
                ┌────────▼────────┐
                │       dw        │   Data warehouse (placeholder)
                │   lakehouse-dw  │
                └────────┬────────┘
                         │
                ┌────────▼────────┐
                │       dm        │   Data marts — hand-written transforms
                │   lakehouse-dm  │   currently `dm_md/fin_hawk`
                └────────┬────────┘
                         │
                ┌────────▼────────┐
                │       sm        │   Service marts (placeholder)
                │   lakehouse-sm  │
                └────────┬────────┘
                         │
                ┌────────▼────────┐
                │   job runtime   │   `TableUpdaterEntryPoint`
                │  lakehouse-job  │   Assembled into `lakehouse.jar`
                └─────────────────┘
```

Each layer uses Delta Lake with **Change Data Feed** enabled. Table updates are driven by the `TableUpdater` framework from `lakehouse-core` and orchestrated by `TableUpdaterEntryPoint` in [lakehouse-job](lakehouse-job/src/main/scala/ct/dna/lakehouse/core/jobs/TableUpdaterEntryPoint.scala). `lakehouse-dw` and `lakehouse-sm` are currently empty placeholders that exist to hold the dependency chain in place.

## Project Structure

```
dp-lakehouse-dbxjob/
├── build.sbt                  # 8 sbt subprojects (lakehouse layer chain + devops + almond)
├── dna-builds.sbt             # Shared settings: Scala 2.13.16, DNA BOM 2.4.0, resolvers, compiler flags
├── databricks.yml             # Databricks Asset Bundle root (bundle name + workspace targets)
│
├── lakehouse-sr/              # SR / SR_RAW layer — auto-generated TableSpecs (no business logic)
│   └── src/main/scala/ct/dna/lakehouse/
│       ├── sr/                # ~509 ChangeKey TableSpecs across 16 SAP schemas
│       └── sr_raw/            # ~856 Loaded TableSpecs across 24 SAP + non-SAP schemas
│
├── lakehouse-dw/              # Data Warehouse layer (placeholder — depends on lakehouse-sr)
├── lakehouse-dm/              # Data Mart layer — hand-written transforms (depends on lakehouse-dw)
│   └── src/main/scala/ct/dna/lakehouse/dm_md/fin_hawk/   # `fin_hawk` SAP master-data transforms
├── lakehouse-sm/              # Service Mart layer (placeholder — depends on lakehouse-dm)
│
├── lakehouse-job/             # Runtime entry point — assembled into `lakehouse.jar`
│   └── src/main/scala/ct/dna/lakehouse/core/jobs/
│       ├── TableUpdaterEntryPoint.scala   # Main class: configFile + package + table args
│       ├── TableUpdaterTask.scala         # Dynamic TableSpec resolution + create/update
│       └── TableDependancy.scala
│
├── devops-sr/                 # SR/SR_RAW source-code generator (Theobald JSON + Unity Catalog)
│   └── src/main/scala/ct/dna/lakehouse/srGenerator/
│       └── Generator.scala    # Replaces former GenerateSrRaw + GenerateSr pair
│
├── devops/                    # Deployment tooling + workflow builders + ColumnWithName accessor gen
│   ├── deployTo.sh            # Local deployment entry point
│   ├── README.md              # Deployment documentation
│   ├── SR_GENERATION.md       # SR/SR_RAW generation flow
│   ├── SR_WORKFLOW_BUILDER.md # In-memory SR workflow construction
│   └── src/main/scala/ct/dna/lakehouse/
│       ├── cicd/Deploy.scala  # Orchestrates: build → validate → upload → deploy
│       └── core/
│           ├── GenerateColumnWithNameAccessor.scala  # Typed ColumnWithName accessor patcher
│           ├── SrWorkflowBuilder.scala               # In-memory SR DAB workflow construction
│           ├── DmWorkflowBuilder.scala               # In-memory DM DAB workflow (one job per dm_md schema)
│           └── DmDependencyResolver.scala            # Topological DM task ordering
│
├── almond/                    # Jupyter / Almond Scala kernel notebook playground (not deployed)
├── demo/                      # Standalone Scala demo scripts + test data (not deployed)
│
├── config/                    # Local dev Spark config (config.json — gitignored)
│   └── config.json.template
├── schema_config.json         # Schema/table → Theobald REST endpoint mapping
├── result_columns.json        # Cached Theobald column-metadata responses
│
├── run-all-tables.sh          # Run TableUpdaterEntryPoint for every SR table locally
├── run-hawk-sr-tables.sh      # Run only the SR tables consumed by `fin_hawk`
├── run-hawk-dm-tables.sh      # Run the `fin_hawk` DM tables in dependency order
├── src_schemas.sh             # Hit Theobald endpoints from schema_config.json → result_columns.json
│
├── .github/
│   ├── workflows/deploy.yml   # CI/CD: build → test → assemble → stage devops → deploy
│   └── actions/
│       ├── runner-config/     # PATH setup + git safe.directory
│       └── sbt-credentials/   # Artifactory credential injection
│
├── .devcontainer/             # VS Code dev container (Java 17 + sbt)
└── project/                   # sbt build configuration
    ├── build.properties       # sbt 1.11.2
    ├── plugins.sbt            # Local plugins
    └── dna-plugins.sbt        # Shared: native-packager, scalafix, scalafmt, dna-build-tools
```

## Technology Stack

| Component | Version |
|---|---|
| Scala | 2.13.16 |
| sbt | 1.11.2 |
| Java | Eclipse Adoptium 17 |
| Databricks Runtime | 17.3.x (Spark 4.0.0, Delta 4.0.0) |
| DNA BOM | 2.4.0 |

All lakehouse, dataplatform, and Spark-runtime library versions (`lakehouse-core`, `dataplatform-core`, `common-utils`, `deploy-utils`, `lakehouse-modelbuilder`, `dbx-runtime`, `local-spark-runtime`) are pinned by the **DNA BOM 2.4.0**, applied via the `useDnaBom` helper in [build.sbt](build.sbt). To change a library version, bump the BOM.

## sbt Subprojects

| Project | Depends on | Purpose | Output |
|---|---|---|---|
| `devops-sr` | — | SR/SR_RAW source-code generator (Theobald JSON + Unity Catalog → Scala TableSpecs) | runnable via `sbt devops-sr/runMain …` |
| `lakehouse-sr` | — | Auto-generated SR + SR_RAW data model (no transforms) | layer JAR |
| `lakehouse-dw` | `lakehouse-sr` | Data warehouse layer (placeholder) | layer JAR |
| `lakehouse-dm` | `lakehouse-dw` | Data mart layer — hand-written transforms (`dm_md/fin_hawk`) | layer JAR |
| `lakehouse-sm` | `lakehouse-dm` | Service mart layer (placeholder) | layer JAR |
| `lakehouse-job` | `lakehouse-sm` | Runtime entry point + `TableUpdater` job framework | **`lakehouse.jar`** (fat JAR via `DbxAssemblyPlugin`) |
| `devops` | `devops-sr`, `lakehouse-job` | Deployment orchestration, workflow builders, accessor generation | staged via `sbt devops/stage` |
| `almond` | — | Jupyter/Almond notebook playground (not published, not deployed) | classpath file for Almond kernel |

The layer chain `sr → dw → dm → sm → job` keeps each lakehouse stage independently compilable and forces dependencies to flow strictly upward.

## Getting Started

### Prerequisites

- Java 17 (Eclipse Adoptium)
- sbt 1.11.2+
- Databricks CLI (for deployment)
- Artifactory credentials at `~/.sbt/.credentials`

### Build and Test

```bash
sbt clean test                    # Compile and run all tests
sbt lakehouse/assembly         # Build the fat JAR
sbt clean test                    # Compile and run all tests across every subproject
sbt lakehouse/assembly            # Build the fat JAR (lakehouse/target/scala-2.13/lakehouse.jar)
sbt devops/stage                  # Stage the `deploy` executable under devops/target/universal/stage/bin/
```

### Local Deployment

1. Create a deployment config from the template:
   ```bash
   cd devops/src/main/resources/deployment/configFiles/
   cp template.json dev.json
   ```

2. Edit `dev.json` with your workspace URL and credentials (minimum: `host` + `deploymentIdentity`):
   ```json
   {
     "host": "https://adb-XXXXXXXXX.XX.azuredatabricks.net",
     "deploymentIdentity": {
       "authType": "pat",
       "token": "dapiXXXXXXXXXXXXXXXX"
     }
   }
   ```

3. Deploy:
   ```bash
   ./devops/deployTo.sh dev
   ```

   This compiles, tests, assembles the JAR, validates the Databricks bundle, creates the Unity Catalog volume, uploads the JAR and config files, and deploys the bundle. See [devops/README.md](devops/README.md) for the full reference.

> **Config files are gitignored.** Never commit credentials.

### Dev Container

A VS Code dev container is provided with Java 17, Metals, and sbt pre-configured. sbt credentials are automatically copied from the host `~/.sbt/.credentials`.

## Code Generation

Three code-generation entry points are exposed today; all are run via sbt from the project root.

### Prerequisites (all generators)

1. **Java 17** (Eclipse Adoptium) — verify with `java -version`
2. **sbt 1.11.2+** — verify with `sbt --version`
3. **Artifactory credentials** — required for dependency resolution. Create `~/.sbt/.credentials`:
   ```properties
   realm=Artifactory Realm
   host=artifacts.ws.contitech.cloud
   user=<your-username>
   password=<your-token>
   ```
4. **First-time compile** — run `sbt compile` once to download all dependencies before running any generator.

---

### 1. `srGenerator.Generator` — SR + SR_RAW source generation

Lives in [devops-sr/Generator.scala](devops-sr/src/main/scala/ct/dna/lakehouse/srGenerator/Generator.scala). Connects to a **live Databricks workspace**, reads Theobald JSON column metadata + Unity Catalog metadata, and generates both `sr_raw/<schema>/<table>.scala` (Loaded `TableSpec` objects) and `sr/<schema>/<table>.scala` (ChangeKey `TableSpec` objects with SAP type mapping, `preApplyMapping`/`preApplyFilter`, and automatic wide-entity splitting via `Joined[Part1, Part2]` when fields exceed 254).

> This generator replaces the previous separate `GenerateSrRaw` + `GenerateSr` pair. See [devops/SR_GENERATION.md](devops/SR_GENERATION.md) for the full workflow.

**Additional prerequisites:**

- Active Databricks connectivity (run from a VM or cluster with workspace access)
- A valid Spark configuration that can reach Unity Catalog
- Theobald JSON metadata at the path configured in [devops-sr/src/main/resources/theobald.json](devops-sr/src/main/resources/theobald.json) and the catalog config in [devops-sr/src/main/resources/generateSourceCatalog.json](devops-sr/src/main/resources/generateSourceCatalog.json)

**Output:**
- `lakehouse-sr/src/main/scala/ct/dna/lakehouse/sr_raw/<schema>/<table>.scala` (~856 files across 24 schemas)
- `lakehouse-sr/src/main/scala/ct/dna/lakehouse/sr/<schema>/<table>.scala` (~509 files across 16 SAP schemas)

---

### 2. `GenerateColumnWithNameAccessor` — typed column accessors

Lives in [devops/GenerateColumnWithNameAccessor.scala](devops/src/main/scala/ct/dna/lakehouse/core/GenerateColumnWithNameAccessor.scala). Scans all `TableSpec` objects below a base package and patches typed `ColumnWithName` accessor blocks into each source file using `// AUTO GENERATED:START`/`END` markers.

```bash
sbt "devops/runMain ct.dna.lakehouse.core.GenerateColumnWithNameAccessor \
  baseDir=lakehouse/src/main/scala/ \
  basePackage=ct.dna.lakehouse"
```

---

### 3. `SrWorkflowBuilder` & `DmWorkflowBuilder` — in-memory DAB jobs

Lakeflow / DAB job definitions are no longer kept as static YAML in the repo. They are constructed in memory at deploy time:

- **[`SrWorkflowBuilder`](devops/src/main/scala/ct/dna/lakehouse/core/SrWorkflowBuilder.scala)** walks the `sr` `CatalogSpec` and produces one Databricks Job per schema, with one task per discovered SR table. See [devops/SR_WORKFLOW_BUILDER.md](devops/SR_WORKFLOW_BUILDER.md).
- **[`DmWorkflowBuilder`](devops/src/main/scala/ct/dna/lakehouse/core/DmWorkflowBuilder.scala)** walks the `dm_md` `CatalogSpec` and uses [`DmDependencyResolver`](devops/src/main/scala/ct/dna/lakehouse/core/DmDependencyResolver.scala) to discover the topological order of each DM schema's tables, emitting one `dm_<schema>_job` (e.g. `dm_fin_hawk_job`) DAB job with `SparkJarTask` entries per schema.

Both builders are invoked by `Deploy` (run via `./devops/deployTo.sh <stage>`) and merged into the bundle alongside [databricks.yml](databricks.yml). They are not normally invoked directly.

## Job Execution

Each table is updated independently via `TableUpdaterEntryPoint` in [lakehouse-job](lakehouse-job/src/main/scala/ct/dna/lakehouse/core/jobs/TableUpdaterEntryPoint.scala):

```
Main class: ct.dna.lakehouse.core.jobs.TableUpdaterEntryPoint
Arguments:  configFile=<path>  <package_name>  <table_name>

Example:    configFile=config/config.json  ct.dna.lakehouse.sr.ct_gbl_e32  ekbe
```

`TableUpdaterTask`:
1. Parses config and initialises Spark via `SparkEnv`
2. Resolves the `TableSpec` object dynamically by fully-qualified class name
3. Ensures all source tables exist (creates if missing)
4. Ensures the target table exists
5. Calls `TableUpdater.update()` which handles CDC via Change Data Feed

### Local Run Helpers

The following helper scripts wrap `sbt runMain` for common local-run scenarios:

| Script | Purpose | Key flags |
|---|---|---|
| [run-all-tables.sh](run-all-tables.sh) | Iterates every SR schema and runs `TableUpdaterEntryPoint` for each table. | `[configFile]`, `--schema <name>` |
| [run-hawk-sr-tables.sh](run-hawk-sr-tables.sh) | Discovers (by grepping `import` statements in `lakehouse-dm/`) and runs only the SR tables consumed by `fin_hawk`. | `[configFile]`, `--schema <name>`, `--list` |
| [run-hawk-dm-tables.sh](run-hawk-dm-tables.sh) | Runs the 10 `fin_hawk` DM tables in topological dependency order. | `[configFile]`, `--list` |
| [src_schemas.sh](src_schemas.sh) | Hits every Theobald/Xtract endpoint in `schema_config.json` via `curl` and writes responses to `result_columns.json`. | — |

All local-run scripts default to `config/config.json` (gitignored — copy from [config/config.json.template](config/config.json.template)).

### Job Definitions

DAB job definitions are **no longer static YAML files**. They are constructed in memory at deploy time by [`CatalogWorkflowBuilder`](devops/src/main/scala/ct/dna/lakehouse/core/CatalogWorkflowBuilder.scala) — one Databricks Job per `CatalogSpec`, each emitting one `Orchestrator` task plus N `Worker_$i` tasks that share a single `job_cluster` driver JVM. The bundle is merged with the root [databricks.yml](databricks.yml). See [devops/SR_WORKFLOW_BUILDER.md](devops/SR_WORKFLOW_BUILDER.md) for historical context.

### Catalog Orchestrator

Each catalog (`sr`, `dm_md`, …) deploys as **one Databricks Job**. Inside that job, an `Orchestrator` task and N `Worker_$i` tasks run in parallel on the same `job_cluster` and communicate through a JVM-static [`CatalogOrchestrator`](lakehouse-job/src/main/scala/ct/dna/lakehouse/core/jobs/orchestrator/CatalogOrchestrator.scala) singleton — no `depends_on` edges between Databricks tasks.

```
┌─────────────────────────────────────────────────────────────────────┐
│  Databricks Workspace                                               │
│                                                                     │
│  ┌─────────────────────────┐    ┌─────────────────────────┐         │
│  │   Job: lakehouse-sr     │    │  Job: lakehouse-dm_md   │         │
│  │   (schedule: 02:00 UTC) │    │  (schedule: 04:00 UTC)  │         │
│  │   job_cluster:          │    │   job_cluster:          │         │
│  │     sr-cluster          │    │     dm_md-cluster       │         │
│  └─────────────────────────┘    └─────────────────────────┘         │
└─────────────────────────────────────────────────────────────────────┘
```

Inside one catalog job (single driver JVM, parallel tasks, no `depends_on`):

```
                ┌──────────────────────────────────────────────────┐
                │  Job: lakehouse-sr   ·   job_cluster=sr-cluster  │
                └──────────────────────────────────────────────────┘
                                       │
        ┌────────────┬────────────┬────┴───────┬────────────┬────────────┐
        ▼            ▼            ▼            ▼            ▼            ▼
  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
  │ Orches-  │ │ Worker_0 │ │ Worker_1 │ │ Worker_2 │ │   ...    │ │ Worker_7 │
  │ trator   │ │ (Spark   │ │ (Spark   │ │ (Spark   │ │          │ │ (Spark   │
  │ (Setup   │ │  jar     │ │  jar     │ │  jar     │ │          │ │  jar     │
  │  task)   │ │  task)   │ │  task)   │ │  task)   │ │          │ │  task)   │
  └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘
        │            │            │            │            │            │
        └────────────┴────────────┴─────┬──────┴────────────┴────────────┘
                                        ▼
                          ┌──────────────────────────────────┐
                          │  shared driver JVM singleton:    │
                          │  CatalogOrchestrator             │
                          │   ├─ DagQueue[TableID, TableSpec]│
                          │   ├─ results (ConcurrentHashMap) │
                          │   ├─ skippedTables               │
                          │   └─ counters (updated / failed) │
                          └──────────────────────────────────┘
```

Task lifecycle:

```
TIME ──►

Orchestrator   │ ███ resolve catalog ─ enqueue plan ─ start StatusTimer ─ wait for drain ─ summary │
               │                                                                                  │
Worker_0       │       █████████████ poll → update → complete ─ poll → update → complete  ...     │
Worker_1       │       █████████████ poll → update → complete ─ poll → update → complete  ...     │
   ...                                                                                            │
Worker_7       │       █████████████ poll → update → complete ─ poll → update → complete  ...     │

                  (every statusIntervalSeconds) Orchestrator logs:
                    [catalog=sr] uptime=120s, pending=403, running=8, recorded=98 (updated=95, failed=1, skipped=2)
```

- **Orchestrator** ([`SetupTaskRunner`](lakehouse-job/src/main/scala/ct/dna/lakehouse/core/jobs/orchestrator/SetupTaskRunner.scala)) walks the catalog, builds the **intra-catalog DAG** from each `TableSpec`'s `sourceTableSpecs` (cross-catalog edges filtered, cycles rejected), Kahn topo-sorts it, and `DagQueue.enqueue(tableId, tableSpec, parents)` for every table.
- **Workers** ([`WorkerTaskRunner`](lakehouse-job/src/main/scala/ct/dna/lakehouse/core/jobs/orchestrator/WorkerTaskRunner.scala)) call `DagQueue.pollOne()` — they only ever receive tables whose parents have already `complete`d. They run [`TableUpdaterCore.update`](lakehouse-job/src/main/scala/ct/dna/lakehouse/core/jobs/orchestrator/TableUpdaterCore.scala) and then `DagQueue.complete(tableId)`, unblocking children.
- On failure, the worker records `Failed(ex)` and adds all transitive descendants to `skippedTables`; later polls then resolve as `SkippedByAncestor`.
- The Orchestrator polls a "queue drained" predicate (with `maxRuntimeSeconds` and `drainTimeoutSeconds` deadlines) and exits, ending the Databricks Job.

Per-catalog tuning lives in the deployment config:

```jsonc
"workerCountDefault": 4,
"workerCounts":      { "sr": 8, "dm_md": 2 },
"orchestrator": {
  "idleSleepSeconds":      5,
  "statusIntervalSeconds": 60,
  "maxRuntimeSeconds":     7200,
  "drainTimeoutSeconds":   600
}
```

Adding a new catalog is two changes:

1. Create a package object `extends CatalogSpec` (e.g. `lakehouse-sm/.../sm_yyy/package.scala`).
2. Add its `srCatalog`-style import + entry to `catalogs: List[CatalogSpec]` in [AssetDirectory.scala](devops/src/main/scala/ct/dna/lakehouse/cicd/utils/AssetDirectory.scala).

Optionally set `schedules`, `workerCounts` entries keyed by the catalog name in the deployment config. `CatalogWorkflowBuilder` then emits a full Job (Orchestrator + N Workers + own cluster) automatically.

## Top-Level Config Files

| File | Purpose |
|---|---|
| [databricks.yml](databricks.yml) | DAB root — declares the bundle name and workspace targets (`dev`, etc.). |
| [config/config.json.template](config/config.json.template) | Local-run Spark config template (`stage`, `clazz`, `workspaceUrl`, `clusterId`, `pat`). Copy to `config/config.json` (gitignored) for local runs. |
| [schema_config.json](schema_config.json) | Maps SAP schema/table names to Theobald/Xtract REST endpoint URLs (consumed by [src_schemas.sh](src_schemas.sh)). |
| [result_columns.json](result_columns.json) | Cached output of [src_schemas.sh](src_schemas.sh) — fetched column definitions from Theobald endpoints. |

## CI/CD

### GitHub Actions Workflow

[.github/workflows/deploy.yml](.github/workflows/deploy.yml) (`Deploy Lakehouse to Databricks`) handles the full build-deploy lifecycle. It runs on the `dna-db` self-hosted runner inside the `dsfacr.azurecr.io/runner/dna_temurin_17:latest` container.

| Trigger | Environment |
|---|---|
| Push to `main` | `prod` |
| Push to `qual` | `qual` |
| Push to `dev` (or any other branch) | `dev` |
| Manual dispatch | Choose `dev` / `qual` / `prod` |

The pipeline runs (in order): checkout → runner config → sbt credentials → stage detection → Azure OIDC login → `sbt "clean;test;lakehouseJob/assembly"` → `sbt "devops/stage"` → `git` identity → `./devops/target/universal/stage/bin/deploy stage=… buildId=… …`.

### Required Variables / Secrets (per GitHub Environment)

| Name | Type | Purpose |
|---|---|---|
| `DEPLOYMENT_CONFIG` | var | Full JSON deployment config (written to `/tmp/deploy-config.json` at runtime) |
| `AZURE_CLIENT_ID` | var | Azure AD app for OIDC login |
| `AZURE_TENANT_ID` | var | Azure tenant ID |
| `ARTIFACTORY_HOST` | var | Artifactory host for sbt resolver |
| `ARTIFACTORY_USER` | var | Artifactory username |
| `ARTIFACTORY_TOKEN` | secret | Artifactory token (sbt dependency resolution) |

See [devops/README.md](devops/README.md) for the full `DEPLOYMENT_CONFIG` schema (auth, cluster settings, schedule configuration, troubleshooting).

## Source Data Systems

### SAP Systems (16)

| Schema | SAP SID | Tables |
|---|---|---|
| `ct_gbl_e32` | E32 | ~50 |
| `ct_gbl_epp` | EPP | ~49 |
| `ct_gbl_ghp` | GHP | ~46 |
| `ct_gbl_p12` | P12 | ~38 |
| `ct_gbl_p24` | P24 | ~33 |
| `ct_gbl_p43` | P43 | ~35 |
| `ct_gbl_p61` | P61 | ~36 |
| `ct_gbl_p64` | P64 | ~34 |
| `ct_gbl_p69` | P69 | ~52 |
| `ct_gbl_p73` | P73 | ~37 |
| `ct_gbl_p77` | P77 | ~37 |
| `ct_gbl_p85` | P85 | ~37 |
| `ct_gbl_p9a` | P9A | ~34 |
| `ct_gbl_pbr` | PBR | ~37 |
| `ct_gbl_pp0` | PP0 | ~36 |
| `ct_gbl_psp` | PSP | ~37 |

### Non-SAP Sources (8, sr_raw only)

| Schema | Source | Tables |
|---|---|---|
| `ct_gbl_ad` | Active Directory | 10 |
| `ct_gbl_bmc` | BMC | 15 |
| `ct_gbl_clienthealth` | Client Health | 11 |
| `ct_gbl_crowdstrike` | CrowdStrike | 2 |
| `ct_gbl_intune` | Intune | 2 |
| `ct_gbl_ivanti` | Ivanti | 50 |
| `ct_gbl_locdb` | Location DB | 14 |
| `ct_gbl_sharepoint` | SharePoint | 5 |

### Test Schema

| Schema | Tables |
|---|---|
| `ct_it_test` | 94 |

## Auto-Generated Code

The `lakehouse-sr/.../sr/` and `lakehouse-sr/.../sr_raw/` directories are entirely auto-generated. **Do not edit these files manually.**

- **sr_raw** files: `case class E_<table>` with `_mk_*` header fields + data fields with type suffixes (`_string`, `_int`, `_decimal_X_Y`), plus `object <table> extends TableSpec[E_<table>] with Loaded`
- **sr** files: `case class Sr<Table>` with mapped Scala types + `@PK`/`@Decimal` annotations, plus `object <table> extends TableSpec[Sr<Table>] with ChangeKey[sr_raw_type]` with `preApplyMapping`, `preApplyFilter`, and `sequenceBy`

Wide entities (>254 fields) are automatically split into `Part1`/`Part2` and connected via `Joined[Part1, Part2]`.

## Further Documentation

| Doc | Covers |
|---|---|
| [devops/README.md](devops/README.md) | Local deployment workflow, deployment config schema, troubleshooting. |
| [devops/SR_GENERATION.md](devops/SR_GENERATION.md) | SR / SR_RAW source-code generation (Theobald + Unity Catalog → Scala). |
| [devops/SR_WORKFLOW_BUILDER.md](devops/SR_WORKFLOW_BUILDER.md) | In-memory DAB SR workflow construction (replaces static YAML). |
| [almond/SETUP.md](almond/SETUP.md) | Almond Scala kernel + Jupyter notebook setup. |
| [lakehouse-dm/src/main/scala/ct/dna/lakehouse/dm_md/fin_hawk/MAKT_WORKFLOW.md](lakehouse-dm/src/main/scala/ct/dna/lakehouse/dm_md/fin_hawk/MAKT_WORKFLOW.md) | `makt` DM table incremental-merge logic and target schema. |
