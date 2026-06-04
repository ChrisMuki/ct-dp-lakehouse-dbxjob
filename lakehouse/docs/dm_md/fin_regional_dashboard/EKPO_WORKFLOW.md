# EKPO Workflow — Multi-Source CDC Merge Passthrough

**File:** [`ekpo.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_regional_dashboard/ekpo.scala)
**Pattern:** [A — multi-source CDC merge passthrough](./README.md#pattern-a--multi-source-cdc-merge-passthrough)
**Output:** `Result.Merged`

## Purpose

Unions the SAP purchasing-document **item/position** (`ekpo`) from 14 source systems into one table keyed by `(_mk_system, _mk_instance, ebeln, ebelp)`. Pure CDC passthrough.

## Target schema (PKs + value columns)

| Column | Type | Description |
|---|---|---|
| `_mk_system`, `_mk_instance` | String **PK** | SAP system / instance |
| `ebeln`, `ebelp` | String **PK** | Purchasing document + item |
| `loekz`, `txz01`, `matnr`, `ematn`, `bukrs`, `werks`, `matkl`, `meins` | String | Deletion flag, short text, material, plant, material group, unit |
| `menge`, `netpr`, `peinh`, `netwr` | Double | Quantity, price, price unit, net value |
| `knttp`, `wepos`, `kunnr`, `txjcd`, `inco1`, `inco2`, `ltsnr`, `attyp`, `bwtar`, `pstyp` | String | Account-assignment, incoterms, valuation type, item category |

## Sources

`ekpo` from each of the 14 `ct_gbl_*` systems (same list as [EKBE](./EKBE_WORKFLOW.md#sources)).

## Execution flow

Standard Pattern A: `feed.lastByKey(consumedValueColumnNames)` → `projectChanges` (filter on `ebeln`/`ebelp` not null) → `unionByName` → `table.merge` on the 4-column item key with the four standard branches (see [EKBE](./EKBE_WORKFLOW.md#merge-branches)).

## Downstream

`ekpo` joins to `ekko` on `ebeln` inside [`customs_regional_reporting`](./CUSTOMS_REGIONAL_REPORTING_WORKFLOW.md), and supplies `werks` / `matnr` / `bwtar` used for the `marc`, `mbew`, `t001w` joins. `ekpo_ebelp` is also part of the join key from `import_table` back to the reporting table.
</content>
