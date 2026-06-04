# MARC Workflow — Multi-Source CDC Merge with HS-Code Cleansing

**File:** [`marc.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_redb/marc.scala)
**Pattern:** [A — multi-source CDC merge passthrough](./README.md#pattern-a--multi-source-cdc-merge-passthrough)
**Output:** `Result.Merged`

## Purpose

Unions the SAP material-per-plant master (`marc`) from 14 source systems into one table keyed by `(_mk_system, _mk_instance, matnr, werks)`. Carries the HS / commodity codes, cleansed during projection.

## Target schema (PKs + value columns)

| Column | Type | Description |
|---|---|---|
| `_mk_system`, `_mk_instance` | String **PK** | SAP system / instance |
| `matnr`, `werks` | String **PK** | Material number + plant |
| `lvorm_plant` | String | Plant-level deletion flag (sourced from `lvorm`, renamed to disambiguate from `mara.lvorm`) |
| `stawn` | String | Cleansed commodity code (`"No Entry"` fallback) |
| `steuc` | String | Cleansed control code |
| `herkl` | String | Country of origin |
| `stawn_sap`, `steuc_sap` | String | Original (uncleansed) values |

## Sources

`marc` from each of the 14 `ct_gbl_*` systems (same list as [EKBE](./EKBE_WORKFLOW.md#sources)).

## `projectChanges` transforms

- **Rename**: `lvorm → lvorm_plant`.
- **Cleanse `stawn`**: strip dots and whitespace (`regexp_replace(stawn, "\\.|\\s", "")`); when the result is null or blank, fall back to `"No Entry"`.
- **Cleanse `steuc`**: same dot/whitespace strip (no `"No Entry"` fallback).
- **Preserve originals**: `stawn → stawn_sap`, `steuc → steuc_sap`.
- Cleansing happens here (not in the merge map) so the value flows once through Catalyst and the merge plan stays a straight column lookup.

## `consumedValueColumnNames`

`Seq("_mk_system", "_mk_instance", "lvorm", "stawn", "steuc", "herkl")` — the explicit allowlist that avoids the broken `Joined[E_marc_part1, E_marc_part2]` sr-spec columns (same sr-generator quirk documented in [MARA](./MARA_WORKFLOW.md#consumedvaluecolumnnames)).

## Merge branches

Standard four-branch Pattern A on the 4-column key (see [EKBE](./EKBE_WORKFLOW.md#merge-branches)).

## Downstream

`marc` is the material×plant bridge in [`customs_regional_reporting`](./CUSTOMS_REGIONAL_REPORTING_WORKFLOW.md): it joins `ekpo` (on `werks`+`matnr`) to `mara`, `t023t`, the hawk `mdp` table, and surfaces `marc_stawn` / `marc_herkl`.
</content>
