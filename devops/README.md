# Deployment — `devops` module

Builds, tests, and deploys `lakehouse.jar` to Databricks as an **Asset Bundle**. The bundle is generated in-memory at deploy time by [`AssetDirectory`](src/main/scala/ct/dna/lakehouse/cicd/utils/AssetDirectory.scala) and [`CatalogJobBuilder`](src/main/scala/ct/dna/lakehouse/core/CatalogJobBuilder.scala) — there is no checked-in `databricks.yml`.

Each catalog (`sr`, `dm_md`, `dw_tx`, …) becomes **one Databricks Job** named `lakehouse-<catalog>`, with three tasks sharing one `job_cluster`:

```
JobSetup ──► Orchestrator (in-process worker pool) ──► Summary
```

Deployment is driven by per-stage configuration encoded directly in Scala in [`Config.scala`](src/main/scala/ct/dna/lakehouse/cicd/Config.scala). `Config.stageConfig` builds the `Config` for the active stage (held in `Stage`, a `SetOnce[Stage]` set by [`Deploy`](src/main/scala/ct/dna/lakehouse/cicd/Deploy.scala) from the `stage=` launcher argument); a small `dqp(dev, qual, prod)` helper picks the per-stage value, so shared defaults are written once and anything wrapped in `dqp(...)` is the part that varies. The config holds **no secrets** — auth is OIDC / Azure CLI / Managed Identity. The stage is selected purely by the `stage=` launcher argument; there is no config file to locate or pass.

---

## Local Deployment

### Prerequisites

- Java 17
- sbt 1.11.2+
- Databricks CLI (`curl -fsSL https://raw.githubusercontent.com/databricks/setup-cli/main/install.sh | sh`)
- `az login` (the default `azAuth=AzureCli` makes the Databricks CLI authenticate via your logged-in Azure CLI)

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

---

## Configuration Reference

All fields except `host` are required on the `Config` case class in [`Config.scala`](src/main/scala/ct/dna/lakehouse/cicd/Config.scala) and are set explicitly per stage in `Config.stageConfig`; to change a stage, edit that method (wrap stage-specific values in `dqp(...)`, leave shared values as a single literal). Azure auth is **not** part of `Config` — it is supplied at runtime via the `azAuth` property (see [Azure auth](#azure-auth) below).

### Top-level

| Field | Default | Notes |
|---|---|---|
| `host` | — *(required)* | Databricks workspace URL |
| `volumeCatalog` | `lakehouse` (prod) / `<stage>_lakehouse` | UC catalog backing the job-resource volume |
| `volumeSchema` | `default` | UC schema in the volume path |
| `targetMode` | `production` | `development` makes the CLI prefix the job name with `[dev <user>]` and run the job as the deploying identity |
| `clusterConfiguration` | see below | Global job-cluster shape |
| `clusterConfigurationOverrides` | `{}` | **Per-catalog** overrides on top of `clusterConfiguration` |
| `schedules` | `{}` | Per-catalog Quartz cron schedules |
| `continuous` | `{}` | Per-catalog continuous-run config (mutually exclusive with `schedules` for the same catalog) |
| `taskParallelism` | `{}` default `4` | In-JVM table-parallelism per catalog (not the Spark worker count); a map with a default value for unlisted catalogs |
| `orchestrator` | see below | Runtime knobs for the Orchestrator (status cadence, watchdog, per-table results table) |
| `summary` | see below | Runtime knobs for the terminal Summary step |
| `permissions` | `[]` | Target-level permissions emitted into the bundle |

### `clusterConfiguration` / `clusterConfigurationOverrides.<catalog>`

| Field | Default | Notes |
|---|---|---|
| `sparkVersion` | `17.3.x-scala2.13` | Databricks Runtime |
| `clusterPolicyId` | none | Optional cluster policy |
| `minWorkerNodes` | `1` | Autoscale min workers. Keep `>= 2` for catalogs using in-memory `localCheckpoint`s so one executor loss isn't fatal. |
| `maxWorkerNodes` | `4` | Autoscale max workers |
| `nodeTypeId` | `Standard_D8ds_v5` | Worker VM size |
| `driverNodeTypeId` | `Standard_D8ds_v5` | Driver VM size |
| `sparkConf` | `spark.sql.autoBroadcastJoinThreshold=-1` + adaptive variant | Merged into `new_cluster.spark_conf`. Per-catalog `sparkConf` entries shallow-merge over the global map (per-catalog wins on key collisions). |

The per-catalog overrides under `clusterConfigurationOverrides` are all optional — absent fields inherit from the global `clusterConfiguration`. Use this to give layers with different workload shapes their own cluster profile (e.g. `sr` = many small tables, `dm_md` = few large tables).

### `schedules.<catalog>` / `continuous.<catalog>`

```jsonc
"schedules": {
  "sr":    { "quartzCronExpression": "0 0 2 * * ?", "timezoneId": "UTC", "pauseStatus": "UNPAUSED" },
  "dm_md": { "quartzCronExpression": "0 0 4 * * ?", "timezoneId": "UTC", "pauseStatus": "UNPAUSED" }
}
```

Catalogs absent from both maps are deployed unscheduled and must be triggered manually or via API. `pauseStatus` defaults to `UNPAUSED`.

### `orchestrator`

| Field | Default | Notes |
|---|---|---|
| `statusIntervalSeconds` | `60` | Interval between consolidated status log lines |
| `maxTableRuntimeSeconds` | none | Optional per-table watchdog; cancels a Worker that exceeds the limit |
| `tableRuns` | `{ catalog: volumeCatalog, schema: volumeSchema, table: "lakehouse_table_runs" }` | UC coordinates of the per-table results Delta table (appended by every Worker). Supply all three (`catalog`, `schema`, `table`) to override |
| `tableRunsEnabled` | `true` | When `false`, Workers skip per-table Delta writes |

### `summary`

| Field | Default | Notes |
|---|---|---|
| `target` | `{ catalog: volumeCatalog, schema: volumeSchema, table: "lakehouse_runs" }` | UC coordinates of the per-run Summary Delta table. Supply all three (`catalog`, `schema`, `table`) to override |
| `enabled` | `true` | When `false`, the Summary task only logs and skips the Delta write |

### Azure auth

Azure auth for the Databricks CLI is supplied at runtime through the `azAuth` launcher property (resolved by `Configuration` from a runtime arg, a config file, a JVM option, or an **environment variable** — in that order), **not** from `Config`. The value is parsed by `AzAuth(...)` (from `common-utils`):

| `azAuth` value | Shape | Used for |
|---|---|---|
| `AzureCli` *(default)* | string literal | Local dev (`az login` provides the token) and CI (after `azure/login` OIDC) |
| `ManagedIdentity` | string literal | Hosted runners with an MSI |
| `{"tenantId":...,"clientId":...,"clientSecret":...}` | JSON | Explicit service-principal (`ClientSecret`) auth |
| `{"tenantId":...,"clientId":...}` | JSON | GitHub OIDC (`GithubOidc`) workload-identity federation |

In the GitHub workflows the value is passed as the `azAuth` environment variable from the repo/environment variable `DEPLOY_AZ_AUTH`; when that variable is unset the launcher falls back to `AzureCli`. Locally it defaults to `AzureCli`, so `az login` is all that is needed.

> **The in-code config holds no secrets.** Auth either uses Azure CLI (local) or an OIDC-issued token (CI). Never hard-code a `clientSecret`.

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
7. Run `./devops/target/universal/stage/bin/deploy stage=… buildId=… rootPath=… assetPath=/tmp/devops jarPath=…` (the stage selects the in-code config)

---

## Files

```
devops/
├── deployTo.sh                                  # Local deployment entry point
├── README.md                                    # This file
└── src/main/
    ├── resources/
    │   ├── log4j2.xml                           # sbt-launcher log config (not deployed)
    │   └── runtime-log4j2.xml                   # Copied to the volume as log4j2.xml (cluster runtime config)
    └── scala/ct/dna/lakehouse/
        ├── cicd/
        │   ├── Config.scala                     # Per-stage deployment config in code (Config + Config.stageConfig, dqp dev/qual/prod)
        │   ├── Deploy.scala                     # Deployment orchestration
        │   ├── models/
        │   │   ├── ConfigFile.scala             # Runtime config JSON written to the volume
        │   │   ├── InitScript.scala             # init_script.sh generator
        │   │   └── AsFile.scala                 # Jackson YAML serialization helpers
        │   └── utils/AssetDirectory.scala       # Staging dir + databricks.yml generator
        └── core/
            ├── CatalogJobBuilder.scala     # Emits one DAB Job per CatalogSpec
            └── GenerateColumnWithNameAccessor.scala
```

---

## Troubleshooting

| Symptom | Fix |
|---|---|
| `Bundle validate fails` | Run `databricks bundle validate` inside the staging dir to see the full diff. Check `databricks.yml` syntax — it is generated, so the bug is usually in `AssetDirectory.scala` / `CatalogJobBuilder.scala`. |
| `Volume already exists` | Informational — `Deploy.scala` ignores the non-zero exit and continues. |
| `Configuring both schedule and continuous for the same catalog` | Databricks rejects this combination; remove one of the entries for the offending catalog in [`Config.scala`](src/main/scala/ct/dna/lakehouse/cicd/Config.scala). |
| `Unknown stage '<x>'` | `Config.stageConfig` only accepts `dev`, `qual` or `prod`; check the `stage=` argument. |
| `sbt credentials failure` | Ensure `~/.sbt/.credentials` is configured locally, or check `ARTIFACTORY_HOST` / `ARTIFACTORY_USER` / `ARTIFACTORY_TOKEN` in the GitHub Environment. |
| `Schedule null warnings` from `databricks bundle validate` | Expected for catalogs that omit a `schedules` entry — suppressed by `NON_ABSENT` serialization. |
