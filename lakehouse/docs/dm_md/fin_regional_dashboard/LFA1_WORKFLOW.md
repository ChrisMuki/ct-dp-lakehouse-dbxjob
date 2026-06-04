# LFA1 Workflow — Vendor Dimension + Region Enrichment (`overwriteByKeys`)

**File:** [`lfa1.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_regional_dashboard/lfa1.scala)
**Pattern:** [C — derived recompute + `overwriteByKeys`](./README.md#pattern-c--derived-recompute--overwritebykeys-full-recompute)
**Output:** `Result.FullRecompute`

## Purpose

Builds the vendor dimension: unions the SAP vendor master (`lfa1`) from 14 source systems, then left-joins the global country/region reference (`countries_ww`) on the vendor country (`land1`) to append geo / economic-region enrichment. Structurally identical to [T001W](./T001W_WORKFLOW.md) — only the keys and SAP columns differ.

## Target schema

| Column | Type | Description |
|---|---|---|
| `_mk_system`, `_mk_instance` | String **PK** | SAP system / instance |
| `lifnr` | String **PK** | Vendor number |
| `land1`, `name1`, `regio`, `stras`, `pstlz`, `ort01` | String | Vendor attributes from SAP |
| `country`, `iso_code`, `eco_regions`, `subregion` | String | From `countries_ww` (`eco_regions` → `"No Entry"` fallback) |
| `latitude_geo_center` | `Decimal(19,17)` | Geo centroid |
| `longitude_geo_center` | `Decimal(21,18)` | Geo centroid |
| `member_of_eu` | Long | EU-membership flag |

## Sources

- `lfa1` from each of the 14 `ct_gbl_*` systems (the `sapTableSpecs`).
- `ct.dna.lakehouse.sr_raw.mn_gbl_spcustoms.countries_ww` (aliased `customs_regions_raw`).

## Execution flow

Same shape as [T001W](./T001W_WORKFLOW.md#execution-flow):

1. Union the 14 SAP `lfa1` snapshots (`changeFeeds(ts).snapshot()`), restricted to `consumedValueColumnNames` = `_mk_system, _mk_instance, lifnr, land1, name1, regio, stras, pstlz, ort01`; filter non-null `lifnr`; `distinct`.
2. Dedup `countries_ww` to one row per `alpha_2_string` via the `row_number` window (newest ingest wins); `broadcast`.
3. `LEFT JOIN` on `l.land1 = r.alpha_2_string`.
4. `select` vendor columns + region enrichment.
5. `table.overwriteByKeys(combined)` → `Result.FullRecompute`.

See [T001W's grain-safety note](./T001W_WORKFLOW.md#countries_ww-dedup-the-grain-safety-bit) — the `countries_ww` dedup is what keeps the output unique per `(_mk_system, _mk_instance, lifnr)`.

## Downstream

`lfa1` joins from `ekko` on `lifnr` in [`customs_regional_reporting`](./CUSTOMS_REGIONAL_REPORTING_WORKFLOW.md), supplying all the `lfa1_*` vendor/geo columns (and `lfa1_member_of_eu` / `lfa1_iso_code` used by the `import_` classification).
</content>
