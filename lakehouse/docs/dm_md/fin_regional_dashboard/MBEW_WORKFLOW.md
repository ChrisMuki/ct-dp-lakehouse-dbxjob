# MBEW Workflow — Multi-Source CDC Merge Passthrough

**File:** [`mbew.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_regional_dashboard/mbew.scala)
**Pattern:** [A — multi-source CDC merge passthrough](./README.md#pattern-a--multi-source-cdc-merge-passthrough)
**Output:** `Result.Merged`

## Purpose

Unions the SAP material valuation master (`mbew`) from 14 source systems into one table keyed by `(_mk_system, _mk_instance, matnr, bwkey, bwtar)`. Straight passthrough — no transformations.

## Target schema (PKs + value columns)

| Column | Type | Description |
|---|---|---|
| `_mk_system`, `_mk_instance` | String **PK** | SAP system / instance |
| `matnr` | String **PK** | Material number |
| `bwkey` | String **PK** | Valuation area |
| `bwtar` | String **PK** | Valuation type |
| `stprs` | Double | Standard price |
| `peinh` | Double | Price unit |

## Sources

`mbew` from each of the 14 `ct_gbl_*` systems (same list as [EKBE](./EKBE_WORKFLOW.md#sources)).

## Notes specific to mbew

- `projectChanges` filters `matnr.isNotNull && bwkey.isNotNull` and selects the 5 PKs + `stprs`, `peinh` + `_change_type`. No cleansing or renames.
- **Snapshot-detection probe differs from the other Pattern-A tables.** Instead of `feed.snapshot(Seq("_mk_system"))`, mbew reuses its own `projectChanges(feed.lastByKey(...)).select("_mk_system")` to derive `snapshotSystems`. The net effect (a `Set[String]` of snapshotted systems) is identical; the merge branches are unchanged.

## Merge branches

Standard four-branch Pattern A on the 5-column key (see [EKBE](./EKBE_WORKFLOW.md#merge-branches)).

## Downstream

`mbew` supplies standard price / price unit (`mbew_stprs`, `mbew_peinh`) to [`customs_regional_reporting`](./CUSTOMS_REGIONAL_REPORTING_WORKFLOW.md), joined from `ekpo` on `werks = bwkey`, `matnr`, `bwtar`. Those two columns drive the `stprs_per_unit` calculation in [`import_table`](./IMPORT_TABLE_WORKFLOW.md) when `ekpo_pstyp = "2"`.
</content>
