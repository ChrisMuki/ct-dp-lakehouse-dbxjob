# T001W Workflow — Multi-Source CDC Merge Passthrough

**File:** [`t001w.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_hawk/t001w.scala)
**Pattern:** [A — multi-source CDC merge passthrough](./README.md#pattern-a--multi-source-cdc-merge-passthrough)
**Output:** `Result.Merged`

## Purpose

Unions the SAP plant master (`t001w`) from 13 source systems into one table keyed by `(_mk_system, _mk_instance, werks)`.

## Target schema

| Column | Type | Description |
|---|---|---|
| `_mk_system` | String **PK** | SAP system ID |
| `_mk_instance` | String **PK** | SAP instance |
| `werks` | String **PK** | Plant code |
| `name1` | String | Plant name |
| `bwkey` | String | Valuation area — joins to [`t001k`](./T001K_WORKFLOW.md) |
| `land1` | String | Plant country (ISO alpha-2) |
| `kunnr` | String | Customer number |
| `lifnr` | String | Vendor number |

## Sources

`t001w` from each of: `ct_gbl_e32`, `ct_gbl_epp`, `ct_gbl_ghp`, `ct_gbl_p12`, `ct_gbl_p24`, `ct_gbl_p43`, `ct_gbl_p61`, `ct_gbl_p64`, `ct_gbl_p69`, `ct_gbl_p73`, `ct_gbl_p77`, `ct_gbl_p85`, `ct_gbl_pbr`, `ct_gbl_psp`.

## Execution flow

Same shape as [MARA](./MARA_WORKFLOW.md#execution-flow) — the canonical Pattern A flow.

1. Short-circuit `Result.NoChanges` if every feed is `isUnchanged`.
2. Collect `snapshotSystems` from `isSnapshot` feeds.
3. `projectChanges(feed.lastOfKey())` per feed → unionByName.
4. Delta MERGE on `(_mk_system, _mk_instance, werks)` with the standard four-branch shape.

## Notes

- Bridges plant → company code via `bwkey → t001k.bwkey → t001k.bukrs → t001.bukrs`. This 3-hop chain is what [MDP](./MDP_WORKFLOW.md) uses to enrich each marc row with company info.
- `land1` is the plant country — joined against `countries_ww` in MDP for `werks_country_name` / `werks_member_of_eu`.

## Validation

Asserts canonical keys against `ct_gbl_e32.t001w` and presence of required value columns.
