# T001 Workflow — Multi-Source CDC Merge Passthrough

**File:** [`t001.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_redb/t001.scala)
**Pattern:** [A — multi-source CDC merge passthrough](./README.md#pattern-a--multi-source-cdc-merge-passthrough)
**Output:** `Result.Merged`

## Purpose

Unions the SAP company-code master (`t001`) from 14 source systems into one table keyed by `(_mk_system, _mk_instance, bukrs)`. Pure CDC passthrough.

## Target schema (PKs + value columns)

| Column | Type | Description |
|---|---|---|
| `_mk_system`, `_mk_instance` | String **PK** | SAP system / instance |
| `bukrs` | String **PK** | Company code |
| `butxt` | String | Company-code name |
| `land1` | String | Country key |
| `ort01` | String | City |
| `waers` | String | Company-code currency |

## Sources

`t001` from each of the 14 `ct_gbl_*` systems (same list as [EKBE](./EKBE_WORKFLOW.md#sources)).

## Execution flow

Standard Pattern A: `feed.lastByKey(consumedValueColumnNames)` → `projectChanges` → `unionByName` → `table.merge` on `(_mk_system, _mk_instance, bukrs)` with the four standard branches (see [EKBE](./EKBE_WORKFLOW.md#merge-branches)). Snapshot detection probes `feed.snapshot(Seq("_mk_system"))`.

## Downstream

`t001` is **broadcast** inside [`customs_regional_reporting`](./CUSTOMS_REGIONAL_REPORTING_WORKFLOW.md) (small dimension) and joined from `ekko` on `bukrs`, supplying `t001_waers`, `t001_butxt`, `t001_ort01`.
</content>
