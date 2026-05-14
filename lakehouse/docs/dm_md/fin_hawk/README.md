# fin_hawk Workflow Docs

Per-table execution-flow documentation for the `ct.dna.lakehouse.dm_md.fin_hawk` package. Each doc describes how the table is populated from its sources at runtime, the merge/overwrite strategy used, and the per-table edge cases.

## Pattern catalogue

The fin_hawk tables fall into three execution patterns. Each table doc identifies which pattern it uses and what's specific to it.

### Pattern A — Multi-source CDC merge passthrough

Used by: [MARA](./MARA_WORKFLOW.md), [MARC](./MARC_WORKFLOW.md), [T001](./T001_WORKFLOW.md), [T001W](./T001W_WORKFLOW.md), [T001K](./T001K_WORKFLOW.md).

Reads N homogeneous SAP source tables (one per system: `ct_gbl_e32`, `ct_gbl_epp`, …), each with `(_mk_system, _mk_instance, …)` PKs. For every feed:

1. `feed.lastOfKey(consumedValueColumnNames)` → one row per PK with `_change_type ∈ {insert, update, delete}`.
2. `projectChanges(...)` slims the projection (and on `marc` cleanses `stawn`/`steuc`).
3. All per-feed slices are unioned via `unionByName`.
4. `table.merge(...)` runs an explicit Delta MERGE:
   - `whenMatched(_change_type === "delete").delete()`
   - `whenMatched().update(...)`
   - `whenNotMatched(_change_type =!= "delete").insert(...)`
   - `whenNotMatchedBySource(_mk_system isin snapshotSystems).delete()` — only triggers for systems whose feed is `isSnapshot`.

Returns `Result.Merged`.

### Pattern B — Multi-source CDC merge with language pivot (D/E)

Used by: [MAKT](./MAKT_WORKFLOW.md), [T023T](./T023T_WORKFLOW.md).

Same as Pattern A, but each source row is keyed by language (`spras`). The pivot collapses up to two language rows per business key into a single row with `_value_d`, `_value_e`, `_changed_d`, `_changed_e` columns. The merge:

- Filters to `spras IN ('D', 'E')`.
- Per language X: `newX = when(_changed_x, _value_x).otherwise(target._maktx_x)` — keeps target value when unchanged, applies upsert/delete when changed.
- `whenMatched(newD.isNotNull || newE.isNotNull).update(...)` — at least one language survives.
- `whenMatched().delete()` — both languages gone.
- `whenNotMatched(_value_d.isNotNull || _value_e.isNotNull).insert(...)`.
- `whenNotMatchedBySource(target._mk_system isin snapshotSystems).delete()`.

For snapshot feeds, `_changed_x` is forced to `true` so any missing language clears the target.

### Pattern C — Derived join + `overwriteByKeys` (full recompute)

Used by: [MDM](./MDM_WORKFLOW.md), [MO](./MO_WORKFLOW.md), [MDP](./MDP_WORKFLOW.md).

Reads dm-layer snapshots via `feed.toDF()` (post-merge consistent state), joins them, and writes via `table.overwriteByKeys(result)` which returns `Result.FullRecompute`. No per-row `_change_type` handling — the joined output is the new source of truth for every key it produces.

Important: `overwriteByKeys` runs a Delta MERGE on the PK. **The joined output must be unique per `key_column`** or Delta raises `DELTA_MULTIPLE_SOURCE_ROW_MATCHING_TARGET_ROW_IN_MERGE`. Each pattern-C doc identifies how its joins preserve grain.

## Common framework references

- `ChangeFeed.toDF(...)` — current consistent snapshot of business rows (no CDF metadata).
- `ChangeFeed.lastOfKey(...)` — one row per PK from the change interval, with `_change_type` and `_commit_version`.
- `ChangeFeed.isSnapshot` — `true` when no continuous CDF is available; `lastOfKey` simulates a full load by emitting all current rows as inserts.
- `ChangeFeed.isUnchanged` — `true` when the known commit equals the current commit; used to short-circuit with `Result.NoChanges`.
- `Table.merge(source, condition)` — explicit Delta MERGE builder; returns `Result.Merged` after `.execute()`.
- `Table.overwriteByKeys(source)` — full recompute MERGE on the table PK; returns `Result.FullRecompute`.
