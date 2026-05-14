# T001 Workflow — Multi-Source CDC Merge Passthrough

**File:** [`t001.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_hawk/t001.scala)
**Pattern:** [A — multi-source CDC merge passthrough](./README.md#pattern-a--multi-source-cdc-merge-passthrough)
**Output:** `Result.Merged`

## Purpose

Unions the SAP company-code master (`t001`) from 13 source systems into one table keyed by `(_mk_system, _mk_instance, bukrs)`.

## Target schema

| Column | Type | Description |
|---|---|---|
| `_mk_system` | String **PK** | SAP system ID |
| `_mk_instance` | String **PK** | SAP instance |
| `bukrs` | String **PK** | Company code |
| `butxt` | String | Company name |
| `land1` | String | Country (ISO alpha-2) |
| `ort01` | String | City |

## Sources

`t001` from each of: `ct_gbl_e32`, `ct_gbl_epp`, `ct_gbl_ghp`, `ct_gbl_p12`, `ct_gbl_p24`, `ct_gbl_p43`, `ct_gbl_p61`, `ct_gbl_p64`, `ct_gbl_p69`, `ct_gbl_p73`, `ct_gbl_p77`, `ct_gbl_p85`, `ct_gbl_pbr`, `ct_gbl_psp`.

## Execution flow

Same shape as [MARA](./MARA_WORKFLOW.md#execution-flow) — the canonical Pattern A flow.

1. Short-circuit `Result.NoChanges` if every feed is `isUnchanged`.
2. Collect `snapshotSystems` from `isSnapshot` feeds.
3. `projectChanges(feed.lastOfKey())` per feed → unionByName.
4. Delta MERGE on `(_mk_system, _mk_instance, bukrs)` with the four-branch shape: `delete` / `update` / `insert` / `whenNotMatchedBySource isin snapshotSystems → delete`.

## Notes

- No language pivot, no cleansing, no derived columns — pure passthrough of `butxt`, `land1`, `ort01`.
- `land1` is later consumed by [MDP](./MDP_WORKFLOW.md) as the country-of-company-code (`company_country`) and joined against `countries_ww` for `cu_country_name` / `cu_member_of_eu`.

## Validation

Asserts canonical keys against `ct_gbl_e32.t001` and presence of required value columns.
