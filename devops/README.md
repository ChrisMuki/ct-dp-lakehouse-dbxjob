# CICD Pipeline for dp-lakehouse-dbxjob

## Overview

Builds, tests, and deploys the lakehouse Spark job to Databricks using Asset Bundles. Deployment is driven by a JSON config file (local) or GitHub environment secrets/vars (CI/CD). All non-essential config fields have sensible defaults so only credentials and environment-specific overrides need to be provided.

---

## Local Deployment

### Prerequisites

- Java 17
- sbt
- Databricks CLI (`curl -fsSL https://raw.githubusercontent.com/databricks/setup-cli/main/install.sh | sh`)

### 1. Create a config file

```bash
cd devops/src/main/resources/deployment/configFiles/
cp template.json dev.json
```

Edit `dev.json`. The minimum required fields are `host` and `deploymentIdentity`. All other fields are optional — see `template.json` for the full reference with inline documentation.

**⚠️ Config files contain credentials. They are gitignored. Never commit them.**

Minimal example for local dev with a PAT:

```json
{
  "deploymentConfig": {
    "host": "https://adb-XXXXXXXXXXXXXXXX.XX.azuredatabricks.net",
    "deploymentIdentity": {
      "authType": "pat",
      "token": "dapiXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
    }
  }
}
```

### 2. Run deployment

```bash
./devops/deployTo.sh dev
```

Replace `dev` with `qual` or `prod` to target other stages.

### What happens

1. `sbt clean test lakehouse/assembly` — compile, test, build JAR
2. `databricks bundle validate` — validate the generated `databricks.yml` (written to the staging dir by `AssetDirectory`) before touching anything
3. Create Unity Catalog volume if it does not exist
4. Upload `lakehouse.jar` to the versioned volume path (`/Volumes/<catalog>/<schema>/dbxlakehousejob/<buildId>/`)
5. Copy the JAR to the stable `latest/` path for resource files that reference it
6. `databricks bundle deploy` — deploy the bundle to Databricks
7. `databricks bundle run LakehouseJob --no-wait` — trigger the job

---

## Configuration Reference

All fields except `host` and `deploymentIdentity` are optional. The table below shows what each controls and its default.

| Field | Required | Default | Notes |
|---|---|---|---|
| `host` | ✅ | — | Databricks workspace URL |
| `deploymentIdentity` | ✅ | — | Auth for the deploying identity |
| `jobRunIdentity.clientId` | ❌ | — | SP UUID used as UC schema name and `run_as` in production |
| `volumeCatalog` | ❌ | `ctdp<stage>dbxlakehouse` | Unity Catalog catalog for volumes |
| `targetMode` | ❌ | `production` | `development` omits `run_as` and prefixes job name with `[dev username]` |
| `clusterConfiguration.sparkVersion` | ❌ | `17.3.x-scala2.13` | Databricks Runtime version |
| `clusterConfiguration.clusterPolicyId` | ❌ | none | Cluster policy to apply |
| `clusterConfiguration.maxWorkerNodes` | ❌ | `4` | Autoscale max workers |
| `clusterConfiguration.nodeTypeId` | ❌ | `Standard_D8ds_v5` | Worker VM size |
| `clusterConfiguration.driverNodeTypeId` | ❌ | `Standard_D8ds_v5` | Driver VM size |
| `clusterConfiguration.instancePoolId` | ❌ | none | Pool ID (overrides node types; exposed as bundle variable) |
| `resourceFilesPath` | ❌ | none | Relative path to YAML resource files to include via `include:` |
| `schedule` | ❌ | none | Quartz cron schedule; omit for manual/API trigger only |

### Auth types for `deploymentIdentity`

| `authType` | Required fields |
|---|---|
| `azure-client-secret` | `tenantId`, `clientId`, `clientSecret` |
| `oauth-m2m` | `clientId`, `clientSecret` |
| `pat` | `token` |

---

## GitHub Actions CI/CD

Two workflows handle CI/CD:

### `deploy.yml` — Build and Deploy

Triggered on:
- **Pull request** to `main` / `qual` / `dev` → runs the `build` job only (compile, test, assemble, upload artifact). No deployment.
- **Push** to `main` / `qual` / `dev` → runs `build` then `deploy`.
- **Manual (`workflow_dispatch`)** → choose the target environment.

Branch → stage mapping:

| Branch / input | Stage | `targetMode` | GitHub Environment |
|---|---|---|---|
| `main` | `prod` | `production` | `DB-DNA-PROD` |
| `qual` | `qual` | `production` | `DB-DNA-QUAL` |
| `dev` / other | `dev` | `development` | `DB-DNA-DEV` |

### `pr.yml` — PR Bundle Validation

Runs additionally on PRs to validate `databricks.yml` against the Databricks API using `databricks bundle validate`. Catches misconfigured bundles before they reach deployment.

### Required GitHub Secrets and Variables

Set these per GitHub **Environment** (`DB-DNA-DEV`, `DB-DNA-QUAL`, `DB-DNA-PROD`) under **Settings → Environments**.

#### Secrets (encrypted)

| Secret | Required | Purpose |
|---|---|---|
| `DB_HOST` | ✅ | Databricks workspace URL |
| `ARM_TENANT_ID` | ✅ | Azure tenant ID for SP auth |
| `DB_CLIENT_ID` | ✅ | Deploying SP client ID |
| `DB_CLIENT_SECRET` | ✅ | Deploying SP client secret |
| `DB_JOB_RUN_CLIENT_ID` | ❌ | Job's SP client ID — sets UC volume schema and `run_as` in prod |
| `ARTIFACTORY_TOKEN` | ✅ | sbt dependency resolution |

#### Variables (plain text)

| Variable | Required | Default | Purpose |
|---|---|---|---|
| `ARTIFACTORY_HOST` | ✅ | — | Artifactory host for sbt credentials |
| `ARTIFACTORY_USER` | ✅ | — | Artifactory user |
| `VOLUME_CATALOG` | ❌ | `ctdp<stage>dbxlakehouse` | Unity Catalog catalog override |
| `CLUSTER_POLICY_ID` | ❌ | none | Cluster policy ID |
| `INSTANCE_POOL_ID` | ❌ | none | Azure instance pool ID |

---

## Files Structure

```
devops/
├── deployTo.sh                           # Local deployment entry point
├── README.md                             # This file
└── src/
    └── main/
        ├── resources/
        │   ├── log4j2.xml                # Logging config (copied to volume)
        │   └── deployment/
        │       └── configFiles/
        │           ├── template.json     # Annotated config template
        │           ├── dev.json          # Dev config (gitignored)
        │           └── prod.json         # Prod config (gitignored)
        └── scala/ct/dna/lakehouse/cicd/
            ├── Deploy.scala              # Deployment orchestration
            ├── models/
            │   ├── DeploymentConfig.scala # Config model (all defaults live here)
            │   └── AsFile.scala          # Jackson YAML serialization settings
            └── utils/
                └── AssetDirectory.scala  # Bundle YAML generation, volume paths, resource file copy
```

---

## Troubleshooting

| Symptom | Fix |
|---|---|
| `cannot create job: Invalid user` | Use a service principal UUID in `jobRunIdentity.clientId`, or set `targetMode: development` to omit `run_as` |
| `schedule: null` warnings | Expected for omitted optional fields — suppressed by `NON_ABSENT` serialization |
| `instance_pool_id` validation error on LakehouseJob cluster | Pool ID is only injected as a bundle variable for resource-file clusters, not the main cluster |
| Bundle validate fails | Check `databricks.yml` syntax; run `databricks bundle validate` locally |
| Volume already exists error | Informational — deployment continues normally |
| sbt credentials failure | Ensure `~/.sbt/.credentials` is configured, or check `ARTIFACTORY_*` secrets in GitHub |
