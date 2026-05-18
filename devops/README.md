# Deployment — `devops` module

Builds, tests, and deploys `lakehouse.jar` to Databricks as an **Asset Bundle**. The bundle is generated in-memory at deploy time by [`AssetDirectory`](src/main/scala/ct/dna/lakehouse/cicd/utils/AssetDirectory.scala) and [`CatalogWorkflowBuilder`](src/main/scala/ct/dna/lakehouse/core/CatalogWorkflowBuilder.scala) — there is no checked-in `databricks.yml`.

Each catalog (`sr`, `dm_md`, `dw_tx`, …) becomes **one Databricks Job** named `lakehouse-<catalog>`, with three tasks sharing one `job_cluster`:

```
JobSetup ──► Worker (in-process pool) ──► Summary
```

Deployment is driven by a JSON config file (one per stage), committed under [src/main/resources/deployment/configFiles/](src/main/resources/deployment/configFiles/). The configs **do not contain secrets** — auth is OIDC / Azure CLI / Managed Identity. Developers can drop a sibling `<stage>.local.json` next to the committed file to fully override it locally (gitignored, never merged).

---

## Local Deployment

### Prerequisites

- Java 17
- sbt 1.11.2+
- Databricks CLI (`curl -fsSL https://raw.githubusercontent.com/databricks/setup-cli/main/install.sh | sh`)
- `az login` (the committed dev/qual/prod configs use `deploymentAzAuth: "AzureCli"` locally)

### Run

```bash
./devops/deployTo.sh dev      # or qual / prod
```

### What happens

1. **Build & test** — [`deployTo.sh`](deployTo.sh) runs `sbt lakehouse/test lakehouse/assembly` (no clean; devops tests are skipped).
2. **Stage assets** — [`AssetDirectory`](src/main/scala/ct/dna/lakehouse/cicd/utils/AssetDirectory.scala) writes `log4j2.xml` (from [`runtime-log4j2.xml`](src/main/resources/runtime-log4j2.xml)), `init_script.sh`, `config.json`, and the generated `databricks.yml` to a staging directory.
3. **Validate bundle** — `databricks bundle validate` is run before anything is uploaded.
4. **Create UC volume** — best-effort; ignored if it already exists.
5. **Upload** — `log4j2.xml`, `init_script.sh`, `config.json`, and `lakehouse.jar` are copied to the versioned volume path `/Volumes/<catalog>/<schema>/dbxlakehousejob/<buildId>/`, and the same four files are also republished under the stable `latest/` path that the job cluster's `initScripts` reference.
6. **Deploy** — `databricks bundle deploy`.
7. **Trigger** — `databricks bundle run --no-wait <jobKey>` is invoked once **per catalog job** (`lakehouse-sr`, `lakehouse-dm_md`, `lakehouse-dw_tx`, …).

See [`Deploy.scala`](src/main/scala/ct/dna/lakehouse/cicd/Deploy.scala) for the exact orchestration.

### Local override file

If a `<stage>.local.json` exists next to the committed `<stage>.json`, the launcher uses it **instead of** the committed file (full replacement — no deep merge). Useful for tweaking `volumeSchema`, schedules, or cluster sizes during local testing without touching the committed config.

---

## Configuration Reference

All fields except `host` and `deploymentAzAuth` are optional with defaults defined in [`DeploymentConfig.scala`](src/main/scala/ct/dna/lakehouse/cicd/models/DeploymentConfig.scala). The config is wrapped in a top-level `deploymentConfig` object — the loader rejects files that omit the wrapper.

### Top-level

| Field | Default | Notes |
|---|---|---|
| `host` | — *(required)* | Databricks workspace URL |
| `deploymentAzAuth` | — *(required)* | See [Azure auth](#azure-auth) below |
| `volumeCatalog` | `lakehouse` (prod) / `<stage>_lakehouse` | UC catalog backing the job-resource volume |
| `volumeSchema` | `default` | UC schema in the volume path |
| `targetMode` | `production` | `development` makes the CLI prefix the job name with `[dev <user>]` and run the job as the deploying identity |
| `clusterConfiguration` | see below | Global job-cluster shape |
| `clusterConfigurations` | `{}` | **Per-catalog** overrides on top of `clusterConfiguration` |
| `schedules` | `{}` | Per-catalog Quartz cron schedules |
| `continuous` | `{}` | Per-catalog continuous-run config (mutually exclusive with `schedules` for the same catalog) |
| `taskParallelismDefault` | `4` | In-JVM table-parallelism inside the Worker task (not the Spark worker count) |
| `taskParallelism` | `{}` | Per-catalog override of the above |
| `monitoring` | see below | Runtime knobs for the orchestrator + Summary table |
| `permissions` | `[]` | Target-level permissions emitted into the bundle |

### `clusterConfiguration` / `clusterConfigurations.<catalog>`

| Field | Default | Notes |
|---|---|---|
| `sparkVersion` | `17.3.x-scala2.13` | Databricks Runtime |
| `clusterPolicyId` | none | Optional cluster policy |
| `maxWorkerNodes` | `4` | Autoscale max workers |
| `nodeTypeId` | `Standard_D8ds_v5` | Worker VM size |
| `driverNodeTypeId` | `Standard_D8ds_v5` | Driver VM size |
| `sparkConf` | `spark.sql.autoBroadcastJoinThreshold=-1` + adaptive variant | Merged into `new_cluster.spark_conf`. Per-catalog `sparkConf` entries shallow-merge over the global map (per-catalog wins on key collisions). |

The per-catalog overrides under `clusterConfigurations` are all optional — absent fields inherit from the global `clusterConfiguration`. Use this to give layers with different workload shapes their own cluster profile (e.g. `sr` = many small tables, `dm_md` = few large tables).

### `schedules.<catalog>` / `continuous.<catalog>`

```jsonc
"schedules": {
  "sr":    { "quartzCronExpression": "0 0 2 * * ?", "timezoneId": "UTC", "pauseStatus": "UNPAUSED" },
  "dm_md": { "quartzCronExpression": "0 0 4 * * ?", "timezoneId": "UTC", "pauseStatus": "UNPAUSED" }
}
```

Catalogs absent from both maps are deployed unscheduled and must be triggered manually or via API. `pauseStatus` defaults to `UNPAUSED`.

### `monitoring`

| Field | Default | Notes |
|---|---|---|
| `idleSleepSeconds` | `5` | Worker sleep when the shared queue returns empty |
| `statusIntervalSeconds` | `60` | Interval between consolidated status log lines |
| `summaryCatalog` / `summarySchema` | `volumeCatalog` / `volumeSchema` | UC location for the Summary Delta table |
| `summaryTable` | `lakehouse_runs` | Per-run summary table (written by the `Summary` task) |
| `summaryEnabled` | `true` | When `false`, the Summary task only logs and skips the Delta write |
| `tableRunsTable` | `lakehouse_table_runs` | Per-table results table (appended by every Worker) |
| `tableRunsEnabled` | `true` | When `false`, Workers/Summary skip per-table Delta writes |

### Azure auth

`deploymentAzAuth` is deserialised by the `AzAuth` Jackson deserializer (from `common-utils`). Supported subtypes:

| Form | Shape | Used for |
|---|---|---|
| `"AzureCli"` | string literal | Local dev (`az login` provides the token) |
| `ClientSecret` | `{ "tenantId": "...", "clientId": "...", "clientSecret": "..." }` | CI/CD |
| `ManagedIdentity` | `{ ... }` | Hosted runners with an MSI |

See [`template.json`](src/main/resources/deployment/configFiles/template.json) for an annotated example.

> **Committed `<stage>.json` files do not contain secrets.** Auth either uses Azure CLI (local) or an OIDC-issued token (CI). Never paste a `clientSecret` into a committed file.

---

## GitHub Actions CI/CD

Single workflow: [`.github/workflows/deploy.yml`](../.github/workflows/deploy.yml).

**Triggers**

| Trigger | Resulting stage |
|---|---|
| Push to `main` | `prod` |
| Push to `qual` | `qual` |
| Push to `dev` (or any other branch covered by the trigger filter) | `dev` |
| Manual (`workflow_dispatch`) | Chosen via input |

The workflow resolves the GitHub Environment to `prod`, `qual`, or `dev` — those environments must exist in repo settings with the secrets/vars below.

**Required Variables (per GitHub Environment)**

| Variable | Purpose |
|---|---|
| `ARTIFACTORY_HOST` | Artifactory host for sbt resolver |
| `ARTIFACTORY_USER` | Artifactory user |
| `AZURE_CLIENT_ID` | OIDC federated app — for `azure/login` |
| `AZURE_TENANT_ID` | Azure tenant ID |
| `SCALATEST_LOCAL_SPARKCONFIG` | Spark config JSON injected as the `sparkConfig` env var so `TestWithSpark` can spin up a local session on the runner |

**Required Secrets (per GitHub Environment)**

| Secret | Purpose |
|---|---|
| `ARTIFACTORY_TOKEN` | sbt dependency resolution |

The workflow runs (in order):

1. Checkout (full history)
2. `./.github/actions/runner-config` — PATH + git `safe.directory`
3. `./.github/actions/sbt-credentials` — write `~/.sbt/.credentials` from the variables/secret above
4. Resolve stage from branch / dispatch input; compute `buildId = <yyyymmdd-HHMM>-<sha7>`
5. `azure/login@v2` (OIDC)
6. `sbt "Test/compile" "lakehouse/test" "lakehouse/assembly" "devops/stage"`
7. Run `./devops/target/universal/stage/bin/deploy stage=… buildId=… rootPath=… assetPath=/tmp/devops jarPath=… configFile=devops/src/main/resources/deployment/configFiles/<stage>.json`

---

## Files

```
devops/
├── deployTo.sh                                  # Local deployment entry point
├── README.md                                    # This file
└── src/main/
    ├── resources/
    │   ├── log4j2.xml                           # sbt-launcher log config (not deployed)
    │   ├── runtime-log4j2.xml                   # Copied to the volume as log4j2.xml (cluster runtime config)
    │   └── deployment/configFiles/
    │       ├── template.json                    # Annotated config template
    │       ├── dev.json                         # Committed dev config (no secrets)
    │       ├── qual.json                        # Committed qual config (no secrets)
    │       └── prod.json                        # Committed prod config (no secrets)
    └── scala/ct/dna/lakehouse/
        ├── cicd/
        │   ├── Deploy.scala                     # Deployment orchestration
        │   ├── models/
        │   │   ├── DeploymentConfig.scala       # Config model + all defaults
        │   │   ├── ConfigFile.scala             # Runtime config JSON written to the volume
        │   │   ├── InitScript.scala             # init_script.sh generator
        │   │   └── AsFile.scala                 # Jackson YAML serialization helpers
        │   └── utils/AssetDirectory.scala       # Staging dir + databricks.yml generator
        └── core/
            ├── CatalogWorkflowBuilder.scala     # Emits one DAB Job per CatalogSpec
            └── GenerateColumnWithNameAccessor.scala
```

---

## Troubleshooting

| Symptom | Fix |
|---|---|
| `Bundle validate fails` | Run `databricks bundle validate` inside the staging dir to see the full diff. Check `databricks.yml` syntax — it is generated, so the bug is usually in `AssetDirectory.scala` / `CatalogWorkflowBuilder.scala`. |
| `Volume already exists` | Informational — `Deploy.scala` ignores the non-zero exit and continues. |
| `Configuring both schedule and continuous for the same catalog` | Databricks rejects this combination; remove one of the entries from the offending catalog in your `<stage>.json`. |
| `Deployment config not found` | The path passed via `configFile=` does not exist on disk or on the classpath; check that the file was committed and that `deployTo.sh` is being run from the repo root. |
| `sbt credentials failure` | Ensure `~/.sbt/.credentials` is configured locally, or check `ARTIFACTORY_HOST` / `ARTIFACTORY_USER` / `ARTIFACTORY_TOKEN` in the GitHub Environment. |
| `Schedule null warnings` from `databricks bundle validate` | Expected for catalogs that omit a `schedules` entry — suppressed by `NON_ABSENT` serialization. |
