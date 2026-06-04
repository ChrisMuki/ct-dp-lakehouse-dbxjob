# dp-lakehouse-dbxjob

A Databricks Lakehouse job that ingests SAP and non-SAP data, applies change-data-capture transformations, and materialises curated Delta tables in Unity Catalog. The repo builds a single fat JAR (`lakehouse.jar`) and deploys it as a Databricks Asset Bundle.

## Architecture

```
                  External dependency
              ┌────────────────────────┐
              │   ct.dna lakehouse-sr  │   sr_raw + sr layers
              │   (auto-generated)     │   (24 schemas, ~1300 tables)
              └────────────┬───────────┘
                           │
                           ▼
        ┌──────────────────────────────────────┐
        │            lakehouse module          │
        │                                      │
        │   ct.dna.lakehouse.dw_tx     ◄── DW layer (fin_fxrates)
        │   ct.dna.lakehouse.dm_md     ◄── DM layer (fin_hawk)
        │   ct.dna.lakehouse.tools     ◄── shared helpers
        │   ct.dna.lakehouse.core      ◄── job runtime + orchestrator
        │                                      │
        │              ▼ sbt assembly          │
        │           lakehouse.jar              │
        └──────────────────────────────────────┘
                           │
                           ▼
        ┌──────────────────────────────────────┐
        │   Databricks Asset Bundle (devops)   │
        │   one job per catalog (sr, dm_md,    │
        │   dw_tx) with in-process worker pool │
        └──────────────────────────────────────┘
```

The repo-local code consumes the auto-generated SR / SR_RAW model from `ct.dna %% lakehouse-sr` and adds business-logic layers (`dw_tx`, `dm_md`) plus shared `tools` and the `core/jobs` runtime. Every layer uses Delta with Change Data Feed; table updates are driven by the `TableUpdater` framework from `lakehouse-core`.

## Project Structure

```
dp-lakehouse-dbxjob/
├── build.sbt                  # 3 sbt modules: lakehouse, devops, almond
├── dna-builds.sbt             # Shared settings (Scala 2.13.16, DNA BOM, resolvers)
│
├── lakehouse/                 # Business logic + job runtime → lakehouse.jar
│   ├── src/main/scala/ct/dna/lakehouse/
│   │   ├── core/jobs/         # TableUpdater entry point + catalog orchestrator
│   │   ├── dm_md/fin_hawk/    # Data-mart transforms (SAP master data)
│   │   ├── dw_tx/fin_fxrates/ # Data-warehouse transforms (FX rates)
│   │   └── tools/             # Shared helpers (FxRateLookup, CurrencyUtils, …)
│   └── docs/                  # Per-table workflow docs (dm_md/fin_hawk, dw_tx/fin_fxrates)
│
├── devops/                    # Deployment + workflow construction
│   ├── deployTo.sh            # Local deployment entry point
│   ├── README.md              # Deployment reference (config schema, troubleshooting)
│   └── src/main/scala/ct/dna/lakehouse/
│       ├── cicd/Deploy.scala            # build → validate → upload → deploy
│       └── core/
│           ├── CatalogWorkflowBuilder.scala  # in-memory DAB job per catalog
│           └── GenerateColumnWithNameAccessor.scala
│
├── almond/                    # Jupyter / Almond Scala kernel playground (not deployed)
├── demo/                      # Standalone Scala demo scripts + test data (not deployed)
├── config/                    # Local Spark config (config.json — gitignored)
│
├── run-hawk-sr-tables.sh      # Run only the SR tables consumed by fin_hawk
├── run-hawk-dm-tables.sh      # Run the fin_hawk DM tables in dependency order
│
├── .github/workflows/         # CI/CD (deploy.yml)
└── project/                   # sbt build configuration
```

## sbt Modules

| Module | Depends on | Purpose | Output |
|---|---|---|---|
| `lakehouse` | external `lakehouse-sr` | Business logic (`dw_tx`, `dm_md`, `tools`) + job runtime (`core/jobs`) | **`lakehouse.jar`** (fat JAR via `DbxAssemblyPlugin`) |
| `devops` | `lakehouse` | Deployment orchestration, in-memory DAB workflow builder, accessor generator | runnable via `sbt devops/stage` |
| `almond` | `lakehouse` | Jupyter / Almond notebook playground | classpath file for the Almond kernel |

The SR / SR_RAW model is **not** in this repo — it is consumed as `ct.dna %% lakehouse-sr % 0.7.0`.

## Technology Stack

| Component | Version |
|---|---|
| Scala | 2.13.16 |
| sbt | 1.11.2 |
| Java | Eclipse Adoptium 17 |
| Databricks Runtime | 17.3.x (Spark 4.0.0, Delta 4.0.0) |
| DNA BOM | 4.0.0 |
| `lakehouse-sr` | 0.7.0 |

All Spark-runtime library versions (`lakehouse-core`, `dataplatform-core`, `common-utils`, `deploy-utils`, `lakehouse-modelbuilder`, `dbx-runtime`, `local-spark-runtime`) are pinned by the **DNA BOM**, applied via the `useDnaBom` helper in [build.sbt](build.sbt). To change a library version, bump the BOM.

## Getting Started

### Prerequisites

- Java 17 (Eclipse Adoptium)
- sbt 1.11.2+
- Databricks CLI (for deployment)
- Artifactory credentials at `~/.sbt/.credentials`

### Build and Test

```bash
sbt clean test                    # Compile and run all tests
sbt lakehouse/assembly            # Build the fat JAR (lakehouse/target/scala-2.13/lakehouse.jar)
sbt devops/stage                  # Stage the `deploy` executable under devops/target/universal/stage/bin/
```

### Local Deployment

```bash
./devops/deployTo.sh dev
```

This compiles, tests, assembles the JAR, validates the Databricks bundle, uploads JAR + config files, and deploys. See [devops/README.md](devops/README.md) for the deployment config schema, auth options, cluster tuning, and troubleshooting.

> **Config files are gitignored.** Never commit credentials.

### Dev Container

A VS Code dev container is provided with Java 17, Metals, and sbt pre-configured. sbt credentials are automatically copied from the host `~/.sbt/.credentials`.

## Job Execution

Each table is updated independently via `TableUpdaterEntryPoint` in [lakehouse/src/main/scala/ct/dna/lakehouse/core/jobs/TableUpdaterEntryPoint.scala](lakehouse/src/main/scala/ct/dna/lakehouse/core/jobs/TableUpdaterEntryPoint.scala):

```
Main class: ct.dna.lakehouse.core.jobs.TableUpdaterEntryPoint
Arguments:  configFile=<path>  <package_name>  <table_name>

Example:    configFile=config/config.json  ct.dna.lakehouse.sr.ct_gbl_e32  ekbe
```

At deploy time, [CatalogWorkflowBuilder](devops/src/main/scala/ct/dna/lakehouse/core/CatalogWorkflowBuilder.scala) emits **one Databricks Job per catalog** (`sr`, `dm_md`, `dw_tx`, …) with three tasks — `JobSetup → Worker → Summary` — sharing one `job_cluster`. The `Worker` task runs an in-process worker pool driven by a shared [LakehouseJob](lakehouse/src/main/scala/ct/dna/lakehouse/core/lakehousejob/LakehouseJob.scala) singleton that walks each `TableSpec`'s `sourceTableSpecs`, builds the intra-catalog DAG, Kahn topo-sorts it, and dispatches tables once their parents complete.

Per-catalog tuning (worker count, schedule, cluster shape) lives in the deployment config — see [devops/README.md](devops/README.md).

### Local Run Helpers

| Script | Purpose | Key flags |
|---|---|---|
| [run-hawk-sr-tables.sh](run-hawk-sr-tables.sh) | Discovers and runs only the SR tables consumed by `fin_hawk`. | `[configFile]`, `--schema <name>`, `--list` |
| [run-hawk-dm-tables.sh](run-hawk-dm-tables.sh) | Runs the `fin_hawk` DM tables in topological dependency order. | `[configFile]`, `--list` |

Both default to `config/config.json` (gitignored — copy from `config/config.json.template`).

## Source Data Systems

Tables come from the external `lakehouse-sr` dependency. Counts are approximate.

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

## Further Documentation

| Doc | Covers |
|---|---|
| [devops/README.md](devops/README.md) | Deployment workflow, deployment config schema, cluster + schedule tuning, troubleshooting. |
| [lakehouse/docs/dm_md/fin_hawk/README.md](lakehouse/docs/dm_md/fin_hawk/README.md) | `fin_hawk` data-mart overview (MAKT, MARA, MARC, MDM, MDP, MO, T001, T001K, T001W, T023T workflows). |
| [lakehouse/docs/dw_tx/fin_fxrates/FXRATES_WORKFLOW.md](lakehouse/docs/dw_tx/fin_fxrates/FXRATES_WORKFLOW.md) | `fxrates` DW transformation pipeline. |
| [almond/SETUP.md](almond/SETUP.md) | Almond Scala kernel + Jupyter notebook setup. |
