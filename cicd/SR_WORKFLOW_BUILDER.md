# SR Workflow Builder — Dynamic DAB Generation

## Overview

`SrWorkflowBuilder` replaces the previous two-step static YAML workflow (`SrJobYamlGenerator` -> `sr_job.yml` -> `include:`) with **in-memory bundle construction at deploy time**.

Instead of generating a static YAML file, committing it to the repo, and referencing it via `include:`, the SR Databricks jobs are now built programmatically during `AssetDirectory.createDatabricksYml()` by walking the lakehouse catalog hierarchy.

## How It Works

### Discovery

`SrWorkflowBuilder.buildJobs(catalogSpec)` takes the `sr` catalog package object (`ct.dna.lakehouse.sr extends CatalogSpec`) and uses the lakehouse-core model internals to discover all tables:

```
ct.dna.lakehouse.sr (CatalogSpec)
  ├── ct_gbl_e32 (SchemaSpec)
  │   ├── mara (TableSpec[Entity])
  │   ├── makt (TableSpec[Entity])
  │   └── ...
  ├── ct_gbl_p12 (SchemaSpec)
  │   ├── mara (TableSpec[Entity])
  │   └── ...
  └── ...
```

Two `private[core]` functions from `ct.dna.lakehouse.core.model.internal` perform the traversal:

- `findSchemaSpecs(catalogSpec)` — returns all `SchemaSpec` objects under the catalog  
- `findTableSpecs(schemaSpec)` — returns all `TableSpec[Entity]` objects within a schema

These use ClassGraph to scan for Scala singleton objects on the classpath.

### Grouping

Tables are grouped into Databricks Jobs by schema:

| Naming        | Pattern                    | Example                |
|---------------|----------------------------|------------------------|
| Job name      | `sr_{schemaName}_job`      | `sr_ct_gbl_e32_job`    |
| Task name     | `{schemaName}_{tableName}` | `ct_gbl_e32_mara`      |

Each `Job` contains one `Task` per table, all sharing a single `JobCluster` (`sr-cluster`). Tasks are sorted by `taskKey`, and there are no explicit task dependencies within a schema job.

### Task Definition

Each task calls `TableUpdaterEntryPoint` with:

```
parameters:
  - configFile=${var.config_file}
  - ct.dna.lakehouse.sr.ct_gbl_e32    # package name (resolved from tableSpec.getClass.getPackage)
  - mara                               # table name
```

The JAR reference uses the `${var.jar_latest_path}` bundle variable, resolved at deploy time.

Tasks are emitted with:

- `maxRetries = None`
- `minRetryIntervalMillis = None`
- `jobClusterKey = "sr-cluster"`

At the job level, `maxConcurrentRuns` and `queue` come from `ClusterInfo.defaultMaxConcurrentRuns` and `ClusterInfo.defaultQueue`.

### Bundle Assembly

`AssetDirectory.createDatabricksYml()` imports the package object as `ct.dna.lakehouse.sr.{package => srCatalog}` and calls `SrWorkflowBuilder.buildJobs(srCatalog)`. The alias avoids Scala package-object resolution issues from within the `cicd` module.

The resulting `Map[String, Job]` is passed directly into the `AssetBundle`. After serialisation to a YAML tree, the following post-processing steps run:

1. **instance_pool_id injection** — iterates all jobs' `job_clusters` and inserts `${var.instance_pool_id}` (the `NewCluster` case class doesn't have this field, so it's added via tree manipulation)
2. **`is_single_node` removal** — removes `is_single_node` from each generated cluster definition
3. **Bundle variables** — `jar_latest_path` and `spark_version` are always emitted as top-level `variables:`; `instance_pool_id` is emitted only when configured in `DeploymentConfig`
4. **Optional include passthrough** — any extra resource YAML files copied into the asset directory are written to a top-level `include:` array

The resulting `databricks.yml` is written to the staging directory by `createDatabricksYml()`. Deployment happens later in the deploy flow; this method itself only assembles and writes the bundle file. The generated file is not committed to the repo.

## Files

| File | Role |
|------|------|
| `cicd/.../core/SrWorkflowBuilder.scala` | Discovers tables, builds `Map[String, Job]` |
| `cicd/.../cicd/utils/AssetDirectory.scala` | Calls `SrWorkflowBuilder`, serialises + post-processes the bundle, emits bundle variables, and preserves optional extra `include:` resources |
| `cicd/.../cicd/models/ClusterInfo.scala` | Bundle variable placeholders, cluster defaults, entry point class |

## Comparison with Previous Approach

| Aspect | Before (SrJobYamlGenerator) | After (SrWorkflowBuilder) |
|--------|----------------------------|---------------------------|
| Discovery | `SrSchemaDiscoverer` (flat scan, filters by `ApplyChanges` trait) | `findSchemaSpecs` / `findTableSpecs` (top-down catalog traversal) |
| Output | Static `sr_job.yml` committed to repo | In-memory `Map[String, Job]` at deploy time |
| Integration | Static generated SR workflow referenced through `include:` | Direct `Resources(jobs = srJobs)` plus optional passthrough `include:` for other resource YAMLs |
| Table types | Only `ChangeKey` / `ApplyChanges` tables | All `TableSpec[Entity]` under the catalog |
| When it runs | Manually via `sbt cicd/runMain SrJobYamlGenerator` | Automatically during the deploy asset assembly flow |

## Notes

- The workflow builder currently groups by schema and creates a flat task set per schema job.
- The task package argument is derived from `tableSpec.getClass.getPackage.getName`, so generated tasks automatically follow the compiled SR package layout.
- `SrWorkflowBuilder` operates on all `TableSpec[Entity]` values under the supplied catalog and does not currently filter by traits such as `ChangeKey` or `ApplyChanges`.

## Comparison with dp-pipeline-dxbjob

Both repos now use the in-memory approach (no static YAML), but differ in DAG shape:

| | dp-lakehouse-dbxjob | dp-pipeline-dxbjob |
|---|---|---|
| DAG shape | One job per schema, one task per table | Fixed fan-out (setup → producer → N executors) |
| Task count | Dynamic (discovered from classpath) | Fixed (`maxWorkerNodes` from config) |
| Work distribution | Databricks scheduler (parallel tasks) | Runtime queue polling (`GroupedDagQueue`) |
| Task identity | Named per table (`ct_gbl_e32_mara`) | Generic workers (`LoadItemExecutor_0..N`) |
