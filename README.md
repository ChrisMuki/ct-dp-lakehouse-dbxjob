# dp-lakehouse-dbxjob

A Databricks Lakehouse job that ingests SAP and non-SAP data from multiple source systems, applies change-data-capture transformations, and materialises curated Delta tables in Unity Catalog. The project implements the **Data Platform Pipeline** pattern from the `libraries-main` monorepo and deploys as a fat JAR via Databricks Asset Bundles.

## Architecture Overview

The pipeline follows a two-layer generated lakehouse architecture in this branch:

```
              16 SAP systems + 8 non-SAP sources
                         │
                ┌────────▼────────┐
                │     sr_raw      │   Raw ingestion layer
                │  (856 tables)   │   Auto-generated from Unity Catalog
                │  Loaded tables  │   metadata via GenerateSrRaw
                └────────┬────────┘
                         │
                ┌────────▼────────┐
                │       sr        │   Standardised layer
                │  (509 tables)   │   Auto-generated from sr_table_def.json
                │  ChangeKey CDC  │   via GenerateSr. SAP type mapping,
                │                 │   PK filtering, column renaming
                └─────────────────┘
```

Each layer uses Delta Lake with **Change Data Feed** enabled, and table updates are driven by the `TableUpdater` framework from `lakehouse-core`.

            The current scope keeps only the generated `sr_raw` and `sr` layers in the repository.

## Project Structure

```
dp-lakehouse-dbxjob/
├── build.sbt                          # Multi-project build: lakehouse + cicd
├── dna-builds.sbt                     # Shared settings: Scala 2.13.16, resolvers, compiler flags
├── databricks.yml                     # Databricks Asset Bundle definition
│
├── lakehouse/                         # ── Main Spark job (assembled into lakehouse.jar) ──
│   └── src/main/scala/ct/dna/lakehouse/
│       ├── core/
│       │   ├── package.scala
│       │   └── jobs/
│       │       ├── TableUpdaterEntryPoint.scala   # Main class: configFile + package + table args
│       │       ├── TableUpdaterTask.scala         # Dynamic TableSpec resolution, table creation, update
│       │       └── TableDependancy.scala          # Placeholder (moved to library)
│       └── catalog/
│           ├── sr/                                # ── Standardised layer (auto-generated) ──
│           │   └── <16 SAP schemas>/              # ~509 tables total
│           └── sr_raw/                            # ── Raw layer (auto-generated) ──
│               └── <24 schemas>/                  # ~856 tables total
│
├── cicd/                              # ── Deployment tooling ──
│   ├── deployTo.sh                    # Local deployment entry point
│   ├── README.md                      # Deployment documentation
│   └── src/main/scala/ct/dna/lakehouse/
│       ├── cicd/
│       │   ├── Deploy.scala                       # Orchestrates: build → validate → upload → deploy
│       │   ├── models/
│       │   │   ├── DeploymentConfig.scala         # Config: auth, cluster, schedule, volume
│       │   │   ├── AsFile.scala                   # YAML serialisation for databricks.yml
│       │   │   ├── LakeflowJobYamlModel.scala     # Case classes for Databricks YAML structure
│       │   │   └── ClusterInfo.scala              # Bundle variable placeholders + cluster defaults
│       │   └── utils/
│       │       ├── AssetDirectory.scala           # Volume paths, resource file copy, bundle generation
│       │       └── SrJobYamlGenerator.scala       # SR job YAML generator
│       └── core/
│           ├── GenerateColumnWithNameAccessor.scala  # Patches ColumnWithName blocks into .scala files
│           ├── GenerateSrRaw.scala                   # Unity Catalog → sr_raw entity/TableSpec code gen
│           ├── GenerateSr.scala                      # sr_table_def.json + sr_raw → sr ChangeKey code gen
│           └── catalog/internal/
│               └── TableManagerDelegation.scala       # Delegation to core TableManager
│   └── src/main/resources/
│       └── generate_sr.json.template                 # Template config for GenerateSr
│
├── config/                            # Spark config templates for local development
├── resources/
│   ├── lakehouse_job.yaml             # Job definition (existing_cluster_id variant)
│   ├── lakehouse_job.yml              # Job definition (job_clusters variant)
│   └── sr/
│       └── sr_table_def.json          # SAP schema definitions (352K lines)
│
├── .github/
│   ├── workflows/deploy.yml           # CI/CD: build → test → deploy
│   └── actions/
│       ├── runner-config/             # PATH setup + git safe.directory
│       └── sbt-credentials/           # Artifactory credential injection
│
├── .devcontainer/                     # VS Code dev container (Java 17 + sbt)
└── project/                           # sbt build configuration
    ├── build.properties               # sbt 1.11.2
    ├── plugins.sbt                    # Local plugins
    └── dna-plugins.sbt                # Shared: native-packager, scalafix, scalafmt, dna-build-tools
```

## Technology Stack

| Component | Version |
|---|---|
| Scala | 2.13.16 |
| sbt | 1.11.2 |
| Java | Eclipse Adoptium 17 |
| Databricks Runtime | 17.3.x (Spark 4.0.0, Delta 4.0.0) |
| lakehouse-core | 2.0.3 |
| dataplatform-core | 1.15.2 |
| common-utils | 1.16.1 |
| deploy-utils | 1.13.1 |
| lakehouse-modelbuilder | 1.2.1 |
| dbx-runtime | 17.3.0 (Provided) |
| local-spark-runtime | 17.3.0 (Test) |

## sbt Subprojects

| Project | Purpose | Assembly |
|---|---|---|
| `lakehouse` | Main Spark job — entities, table definitions, update logic | `lakehouse.jar` (fat JAR) |
| `cicd` | Deployment — config parsing, asset bundling, Databricks CLI orchestration | staged via `sbt-native-packager` |

The `cicd` subproject `dependsOn(lakehouse)` to access entity classes for code generation tasks.

## Getting Started

### Prerequisites

- Java 17 (Eclipse Adoptium)
- sbt 1.11.2+
- Databricks CLI (for deployment)
- Artifactory credentials at `~/.sbt/.credentials`

### Build and Test

```bash
sbt clean test                    # Compile and run all tests
sbt lakehouse/assembly            # Build the fat JAR
```

### Local Deployment

1. Create a deployment config:
   ```bash
   cd cicd/src/main/resources/deployment/configFiles/
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
   ./cicd/deployTo.sh dev
   ```

   This will: compile, test, assemble the JAR, validate the Databricks bundle, create the Unity Catalog volume, upload the JAR and config files, deploy the bundle, and optionally trigger the job.

> **Config files are gitignored.** Never commit credentials.

### Dev Container

A VS Code dev container is provided with Java 17, Metals, and sbt pre-configured. sbt credentials are automatically copied from the host `~/.sbt/.credentials`.

## Code Generation

The project has four code-generation workflows. All generators are run via sbt from the project root directory.

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

### 1. GenerateSrRaw — Raw Layer from Unity Catalog

Connects to a **live Databricks workspace**, reads table metadata from Unity Catalog, and generates `sr_raw/<schema>/<table>.scala` files with entity case classes and `TableSpec with Loaded` objects.

**Additional prerequisites:**
- Active Databricks connectivity (run from a VM or cluster with access to the workspace)
- A valid Spark configuration that can reach Unity Catalog (set via `generate_sr_raw.json` or Spark config properties)

**Run:**

```bash
sbt "cicd/runMain ct.dna.lakehouse.core.GenerateSrRaw \
  baseDir=lakehouse/src/main/scala/ct/dna/lakehouse \
  basePackage=ct.dna.lakehouse \
  catalogId={\"name\":\"sr_raw\"}"
```

**Parameters:**

| Parameter | Description |
|---|---|
| `baseDir` | Root directory for generated output (relative to project root) |
| `basePackage` | Scala base package for the generated files |
| `catalogId` | JSON object identifying the Unity Catalog catalog to read — `{"name":"sr_raw"}` |

**Output:** `lakehouse/src/main/scala/ct/dna/lakehouse/sr_raw/<schema>/<table>.scala` (~856 files across 24 schemas)

> **Note:** This generator connects to live infrastructure. It requires network access to Databricks and will read real catalog metadata. Running locally without Databricks connectivity will fail.

---

### 2. GenerateSr — Standardised Layer from JSON Schema

Reads `resources/sr/sr_table_def.json` (SAP column metadata fetched from Databricks) and cross-references with existing `sr_raw` files to generate `sr/<schema>/<table>.scala` files with:
- Entity case classes with SAP type mapping (`Decimal`, `Date`, etc.)
- `ChangeKey` TableSpec objects with `preApplyMapping` and `preApplyFilter`
- Automatic wide-entity splitting via `Joined[Part1, Part2]` when fields exceed 254
- Import correction against `ct.dna.lakehouse.core.*` and conditional Spark function imports

**Additional prerequisites:**
- `resources/sr/sr_table_def.json` must exist (352K-line JSON with SAP column metadata)
- The `sr_raw/` generated files should already be in place (from step 1)
- Either pass all generator arguments explicitly or create a config from `cicd/src/main/resources/generate_sr.json.template`

**Run (all tables):**

```bash
sbt "cicd/runMain ct.dna.lakehouse.core.GenerateSr \
  baseDir=lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr \
  basePackage=ct.dna.lakehouse.sr \
  srTableDefPath=resources/sr/sr_table_def.json \
  srRawBaseDir=lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr_raw"
```

This reads from `resources/sr/sr_table_def.json` and writes to `lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr/`.

**Run via config file:**

```bash
cp cicd/src/main/resources/generate_sr.json.template cicd/src/main/resources/generate_sr.json
sbt "cicd/runMain ct.dna.lakehouse.core.GenerateSr configFile=cicd/src/main/resources/generate_sr.json"
```

**Run (single schema or table):**

```bash
# Generate only the CT_GBL_P12 schema
sbt "cicd/runMain ct.dna.lakehouse.core.GenerateSr \
  baseDir=lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr \
  basePackage=ct.dna.lakehouse.sr \
  srTableDefPath=resources/sr/sr_table_def.json \
  srRawBaseDir=lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr_raw \
  filterSchema=CT_GBL_P12"

# Generate only the MARA table across all schemas
sbt "cicd/runMain ct.dna.lakehouse.core.GenerateSr \
  baseDir=lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr \
  basePackage=ct.dna.lakehouse.sr \
  srTableDefPath=resources/sr/sr_table_def.json \
  srRawBaseDir=lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr_raw \
  filterTable=MARA"

# Generate one specific table in one schema
sbt "cicd/runMain ct.dna.lakehouse.core.GenerateSr \
  baseDir=lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr \
  basePackage=ct.dna.lakehouse.sr \
  srTableDefPath=resources/sr/sr_table_def.json \
  srRawBaseDir=lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr_raw \
  filterSchema=CT_GBL_P12 \
  filterTable=MARA"
```

**Parameters:**

| Parameter | Form | Default | Description |
|---|---|---|---|
| `baseDir` | Required key | none | Output directory for generated `.scala` files |
| `basePackage` | Required key | none | Scala root package for generated SR tables, typically `ct.dna.lakehouse.sr` |
| `srTableDefPath` | Required key | none | Path to `sr_table_def.json` |
| `srRawBaseDir` | Required key | none | Directory containing generated `sr_raw/<schema>/<table>.scala` files |
| `filterSchema` | Optional key | empty | Filter to a single schema (e.g. `CT_GBL_P12`) |
| `filterTable` | Optional key | empty | Filter to a single table (e.g. `MARA`) |
| `configFile` | Optional key | `generate_sr.json` | Generator config file consumed by `Configuration` |

**Output:** `lakehouse/src/main/scala/ct/dna/lakehouse/catalog/sr/<schema>/<table>.scala` (~509 files across 16 schemas)

---

### 3. GenerateColumnWithNameAccessor — Column Accessors

Scans all `TableSpec` objects and patches typed `ColumnWithName` accessor classes into their source files using `// AUTO GENERATED:START`/`END` markers.

```bash
sbt "cicd/runMain ct.dna.lakehouse.core.ColumnWithNameAccessor \
  baseDir=lakehouse/src/main/scala/ \
  basePackage=ct.dna.lakehouse"
```

---

### 4. SrJobYamlGenerator — Databricks Job YAML Generation

Generates the Databricks Asset Bundle YAML for the shared SR workflow across all discovered SR tables, or a filtered SR-only subset when `--tables` is provided.

**Run (all discovered SR tables):**

```bash
sbt "cicd/runMain ct.dna.lakehouse.cicd.utils.SrJobYamlGenerator"
```

That command writes [cicd/resources/sr_job.yml](cicd/resources/sr_job.yml) by default because `cicd/runMain` runs inside the `cicd` subproject and the generator default is `./resources`.

**Run (filtered SR tables):**

```bash
sbt "cicd/runMain ct.dna.lakehouse.cicd.utils.SrJobYamlGenerator --tables mara,makt"
```

**Run (custom output directory):**

```bash
sbt "cicd/runMain ct.dna.lakehouse.cicd.utils.SrJobYamlGenerator /path/to/output"
```

**Parameters:**

| Parameter | Form | Default | Description |
|---|---|---|---|
| Output dir | 1st positional or `--output-dir` | `./resources` | Directory where YAML files are written relative to the `cicd` subproject |
| Output file | `--output-file` | `sr_job.yml` | Output YAML file name |
| Prefix | `--prefix` | `sr` | Prefix for generated per-schema job names |
| Cluster key | `--cluster-key` | `sr-cluster` | Job cluster key written to tasks and clusters |
| Tables | `--tables` | _(all)_ | Comma-separated SR table filter |

**Output:**

| File | Content |
|---|---|
| `cicd/resources/sr_job.yml` | Shared SR workflow, grouped by schema |

## Job Execution

Each table is updated independently via `TableUpdaterEntryPoint`:

```
Main class: ct.dna.lakehouse.core.jobs.TableUpdaterEntryPoint
Arguments:  <configFile> <package_name> <table_name>

Example:    configFile=deployment/configFiles/dev.json  ct.dna.lakehouse.sr.p12  mara
```

The `TableUpdaterTask`:
1. Parses config and initialises Spark via `SparkEnv`
2. Resolves the `TableSpec` object dynamically by fully-qualified class name
3. Ensures all source tables exist (creates if missing)
4. Ensures the target table exists
5. Calls `TableUpdater.update()` which handles CDC via Change Data Feed

### Job Definitions (YAML)

Three resource files define the Databricks job structures:

| File | Cluster Strategy | Use Case |
|---|---|---|
| `resources/lakehouse_job.yaml` | `existing_cluster_id` | Attach to a running cluster |
| `resources/lakehouse_job.yml` | `job_clusters` with autoscale | Dedicated ephemeral cluster |
| `cicd/resources/sr_job.yml` | `job_clusters` with autoscale | Shared SR workflow across all discovered SR tables |

The shared SR YAML is generated by `SrJobYamlGenerator`.

## CI/CD

### GitHub Actions Workflow

The `deploy.yml` workflow handles the full build-deploy lifecycle:

| Trigger | Action |
|---|---|
| PR to `main` / `qual` / `dev` | Build + test only |
| Push to `dev` | Deploy to `DB-DNA-DEV` (development mode) |
| Push to `qual` | Deploy to `DB-DNA-QUAL` (production mode) |
| Push to `main` | Deploy to `DB-DNA-PROD` (production mode) |
| Manual dispatch | Choose environment |

### Required Secrets (per GitHub Environment)

| Secret | Purpose |
|---|---|
| `DB_HOST` | Databricks workspace URL |
| `ARM_TENANT_ID` | Azure tenant ID |
| `DB_CLIENT_ID` | Deploying service principal |
| `DB_CLIENT_SECRET` | SP secret |
| `ARTIFACTORY_TOKEN` | sbt dependency resolution |

See [cicd/README.md](cicd/README.md) for the full configuration reference, including optional cluster settings, schedule configuration, and troubleshooting.

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

The `sr/` and `sr_raw/` directories are entirely auto-generated. **Do not edit these files manually.**

- **sr_raw** files: `case class E_<table>` with `_mk_*` header fields + data fields with type suffixes (`_string`, `_int`, `_decimal_X_Y`), plus `object <table> extends TableSpec[E_<table>] with Loaded`
- **sr** files: `case class Sr<Table>` with mapped Scala types + `@PK`/`@Decimal` annotations, plus `object <table> extends TableSpec[Sr<Table>] with ChangeKey[sr_raw_type]` with `preApplyMapping`, `preApplyFilter`, and `sequenceBy`

Wide entities (>254 fields) are automatically split into `Part1`/`Part2` and connected via `Joined[Part1, Part2]`.
