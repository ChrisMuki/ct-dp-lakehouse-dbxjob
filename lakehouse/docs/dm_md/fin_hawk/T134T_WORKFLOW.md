# T134T Workflow — CDC Merge with Language Pivot (D/E)

**File:** [`t134t.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_hawk/t134t.scala)
**Pattern:** [B — language-pivoted CDC merge](./README.md#pattern-b--multi-source-cdc-merge-with-language-pivot-de)
**Output:** `Result.Merged`

## Purpose

Aggregates SAP material-type descriptions (`t134t`) from 14 source systems into one row per `(_mk_system, _mk_instance, mtart)`, keeping only German (`D`) and English (`E`) descriptions.

Identical execution shape to [T023T](./T023T_WORKFLOW.md). Only the business key and value column differ (`mtart`/`mtbez` instead of `matkl`/`wgbez`).

## Target schema

| Column | Type | Description |
|---|---|---|
| `_mk_system` | String **PK** | SAP system ID |
| `_mk_instance` | String **PK** | SAP instance |
| `mtart` | String **PK** | Material type |
| `spras` | String | `"D"`, `"E"`, or `"D;E"` |
| `mtbez` | String | `concat_ws("~~", _mtbez_d, _mtbez_e)` |
| `_mtbez_d` | String | German description (nullable) |
| `_mtbez_e` | String | English description (nullable) |

## Sources

`t134t` from each of: `ct_gbl_e32`, `ct_gbl_epp`, `ct_gbl_ghp`, `ct_gbl_p12`, `ct_gbl_p24`, `ct_gbl_p43`, `ct_gbl_p61`, `ct_gbl_p64`, `ct_gbl_p69`, `ct_gbl_p73`, `ct_gbl_p77`, `ct_gbl_p85`, `ct_gbl_pbr`, `ct_gbl_psp`.

Source PK: `(_mk_system, _mk_instance, mtart, spras)`.

## Differences vs T023T

| Aspect | T023T | T134T |
|---|---|---|
| Business key | `matkl` | `mtart` |
| Pivoted text column | `wgbez` → `_wgbez_d`/`_wgbez_e` | `mtbez` → `_mtbez_d`/`_mtbez_e` |
| Number of source systems | 14 | 14 |
| `pivotByLanguage` internals | `_value_d`, `_value_e`, `_changed_d`, `_changed_e` | identical (same column names — kept generic for consistency) |

Everything else (snapshot detection, the `newD`/`newE` carry-forward logic, the four merge branches, `whenNotMatchedBySource` deletion) is the same as [T023T](./T023T_WORKFLOW.md) and [MAKT](./MAKT_WORKFLOW.md#merge-logic).

## Worked example

### Initial target

| mtart | spras | mtbez | _mtbez_d | _mtbez_e |
|---|---|---|---|---|
| FERT | D;E | Fertigerzeugnisse~~Finished goods | Fertigerzeugnisse | Finished goods |
| HALB | D | Halbfabrikate | Halbfabrikate | *null* |

### CDF batch

| mtart | spras | mtbez | _change_type |
|---|---|---|---|
| FERT | E | Finished products | update |
| NLAG | D | Nichtlagerteile | insert |
| NLAG | E | Non-stock parts | insert |

### After `pivotByLanguage`

| mtart | _value_d | _value_e | _changed_d | _changed_e |
|---|---|---|---|---|
| FERT | *null* | Finished products | false | true |
| NLAG | Nichtlagerteile | Non-stock parts | true | true |

### Merge outcomes

| mtart | newD | newE | Branch | Result |
|---|---|---|---|---|
| FERT | `Fertigerzeugnisse` (carry-forward) | `Finished products` (source) | UPDATE | `D;E / Fertigerzeugnisse~~Finished products` |
| HALB | not in source | not in source | not touched | unchanged |
| NLAG | `Nichtlagerteile` | `Non-stock parts` | INSERT | `D;E / Nichtlagerteile~~Non-stock parts` |

## Consumers

`dm_t134t` is joined (broadcast) by three downstream tables, all on `_mk_system + _mk_instance + mtart`:

| Consumer | Join column on left side | Column(s) pulled from t134t |
|---|---|---|
| [`dm_mo`](./MO_WORKFLOW.md) | `mdm.mtart` | `mtbez` |
| [`dm_mdm`](./MDM_WORKFLOW.md) | `mara.mtart` | `spras`, `mtbez` |
| [`dm_mdp`](./MDP_WORKFLOW.md) | `mdm.mtart` | `mtbez` |

## Validation

Asserts every source `t134t` has the canonical key columns (`_mk_system`, `_mk_instance`, `mtart`, `spras`) and exposes the value column (`mtbez`) the merge consumes.
