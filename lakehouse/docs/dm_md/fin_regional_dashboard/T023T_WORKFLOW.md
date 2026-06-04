# T023T Workflow — CDC Merge with Language Pivot (D/E)

**File:** [`t023t.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_regional_dashboard/t023t.scala)
**Pattern:** [B — language-pivoted CDC merge](./README.md#pattern-b--multi-source-cdc-merge-with-language-pivot-de)
**Output:** `Result.Merged`

## Purpose

Aggregates SAP material-group descriptions (`t023t`) from 14 source systems into one row per `(_mk_system, _mk_instance, matkl)`, keeping only German (`D`) and English (`E`) descriptions.

## Target schema (PKs + value columns)

| Column | Type | Description |
|---|---|---|
| `_mk_system` | String **PK** | SAP system ID |
| `_mk_instance` | String **PK** | SAP instance |
| `matkl` | String **PK** | Material group |
| `spras` | String | `"D"`, `"E"`, or `"D;E"` |
| `wgbez` | String | `concat_ws("~~", _wgbez_d, _wgbez_e)`, or `"No Entry"` when both blank |
| `_wgbez_d` | String | German description (nullable) |
| `_wgbez_e` | String | English description (nullable) |

## Sources

`t023t` from each of the 14 `ct_gbl_*` systems (same list as [EKBE](./EKBE_WORKFLOW.md#sources)). Source PK: `(_mk_system, _mk_instance, matkl, spras)`.

## `pivotByLanguage`

Collapses the per-language source rows into one row per business key:

- `_value_x` (X ∈ {D, E}): new value on upsert; `null` on delete or when there is no event for X.
- `_changed_x`: `true` iff there was *any* event (upsert OR delete) for X this batch. For **snapshot** feeds it is forced to `true`, because a snapshot row implies complete current state — any missing language must be cleared.
- `first(..., ignoreNulls = true)` is required: each group has up to two rows (D + E), so a plain `first` could return the null from the wrong-language row.

## Merge logic

```scala
newD = when(_changed_d, _value_d).otherwise(target._wgbez_d)   // carry-forward when unchanged
newE = when(_changed_e, _value_e).otherwise(target._wgbez_e)
```

| Branch | Condition | Action |
|---|---|---|
| `whenMatched` | `newD.isNotNull || newE.isNotNull` | UPDATE `spras`, `wgbez`, `_wgbez_d`, `_wgbez_e` |
| `whenMatched` | (else — both languages gone) | DELETE |
| `whenNotMatched` | `source._value_d.isNotNull || source._value_e.isNotNull` | INSERT |
| `whenNotMatchedBySource` | `target._mk_system isin snapshotSystems` | DELETE |

`spras` is recomputed as `concat_ws(";", D?, E?)`; `wgbez` as `concat_ws("~~", newD, newE)` with a `"No Entry"` fallback when blank. Inserts use source-only equivalents because Delta forbids target-column references inside `whenNotMatched().insert(...)`.

## Downstream

`t023t` is **broadcast** in [`customs_regional_reporting`](./CUSTOMS_REGIONAL_REPORTING_WORKFLOW.md), joined from `mara` on `matkl`, supplying `matkl_text` (the `wgbez` description).
</content>
