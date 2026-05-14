# T001K Workflow — Multi-Source CDC Merge Passthrough

**File:** [`t001k.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_hawk/t001k.scala)
**Pattern:** [A — multi-source CDC merge passthrough](./README.md#pattern-a--multi-source-cdc-merge-passthrough)
**Output:** `Result.Merged`

## Purpose

Unions the SAP valuation-area-to-company-code mapping (`t001k`) from 13 source systems into one table keyed by `(_mk_system, _mk_instance, bwkey)`.

## Target schema

| Column | Type | Description |
|---|---|---|
| `_mk_system` | String **PK** | SAP system ID |
| `_mk_instance` | String **PK** | SAP instance |
| `bwkey` | String **PK** | Valuation area |
| `bukrs` | String | Company code — joins to [`t001`](./T001_WORKFLOW.md) |

## Sources

`t001k` from each of: `ct_gbl_e32`, `ct_gbl_epp`, `ct_gbl_ghp`, `ct_gbl_p12`, `ct_gbl_p24`, `ct_gbl_p43`, `ct_gbl_p61`, `ct_gbl_p64`, `ct_gbl_p69`, `ct_gbl_p73`, `ct_gbl_p77`, `ct_gbl_p85`, `ct_gbl_pbr`, `ct_gbl_psp`.

## Execution flow

Same shape as [MARA](./MARA_WORKFLOW.md#execution-flow) — the canonical Pattern A flow.

1. Short-circuit `Result.NoChanges` if every feed is `isUnchanged`.
2. Collect `snapshotSystems` from `isSnapshot` feeds.
3. `projectChanges(feed.lastOfKey())` per feed → unionByName.
4. Delta MERGE on `(_mk_system, _mk_instance, bwkey)` with the standard four-branch shape.

## Notes

- Smallest of the dimension tables — only one value column (`bukrs`).
- Sole purpose: bridge `t001w.bwkey` to `t001.bukrs` in the [MDP](./MDP_WORKFLOW.md) join chain.

## Validation

Asserts canonical keys against `ct_gbl_e32.t001k` and presence of `bukrs`.
