# T023T Workflow — CDC Merge with Language Pivot (D/E)

**File:** [`t023t.scala`](../../src/main/scala/ct/dna/lakehouse/dm_md/fin_hawk/t023t.scala)
**Pattern:** [B — language-pivoted CDC merge](./README.md#pattern-b--multi-source-cdc-merge-with-language-pivot-de)
**Output:** `Result.Merged`

## Purpose

Aggregates SAP material-group descriptions (`t023t`) from 13 source systems into one row per `(_mk_system, _mk_instance, matkl)`, keeping only German (`D`) and English (`E`) descriptions.

Identical execution shape to [MAKT](./MAKT_WORKFLOW.md). Only the keys and value columns differ.

## Target schema

| Column | Type | Description |
|---|---|---|
| `_mk_system` | String **PK** | SAP system ID |
| `_mk_instance` | String **PK** | SAP instance |
| `matkl` | String **PK** | Material group |
| `spras` | String | `"D"`, `"E"`, or `"D;E"` |
| `wgbez` | String | `concat_ws("~~", _wgbez_d, _wgbez_e)` |
| `_wgbez_d` | String | German description (nullable) |
| `_wgbez_e` | String | English description (nullable) |

## Sources

`t023t` from each of: `ct_gbl_e32`, `ct_gbl_epp`, `ct_gbl_ghp`, `ct_gbl_p12`, `ct_gbl_p24`, `ct_gbl_p43`, `ct_gbl_p61`, `ct_gbl_p64`, `ct_gbl_p69`, `ct_gbl_p73`, `ct_gbl_p77`, `ct_gbl_p85`, `ct_gbl_pbr`, `ct_gbl_psp`.

Source PK: `(_mk_system, _mk_instance, matkl, spras)`.

## Differences vs MAKT

| Aspect | MAKT | T023T |
|---|---|---|
| Business key | `matnr` | `matkl` |
| Pivoted text column | `maktx` → `_maktx_d`/`_maktx_e` | `wgbez` → `_wgbez_d`/`_wgbez_e` |
| `pivotByLanguage` output | `_value_d`, `_value_e`, `_changed_d`, `_changed_e` | identical (same column names — kept generic for consistency) |

Everything else (snapshot detection, the `newD`/`newE` carry-forward logic, the four merge branches, `whenNotMatchedBySource` deletion) is the same as [MAKT](./MAKT_WORKFLOW.md#merge-logic).

## Worked example

### Initial target

| matkl | spras | wgbez | _wgbez_d | _wgbez_e |
|---|---|---|---|---|
| GRP-A | D;E | Schrauben~~Bolts | Schrauben | Bolts |
| GRP-B | D | Muttern | Muttern | *null* |

### CDF batch

| matkl | spras | wgbez | _change_type |
|---|---|---|---|
| GRP-A | E | Fasteners | update |
| GRP-C | D | Federn | insert |
| GRP-C | E | Springs | insert |

### After `pivotByLanguage`

| matkl | _value_d | _value_e | _changed_d | _changed_e |
|---|---|---|---|---|
| GRP-A | *null* | Fasteners | false | true |
| GRP-C | Federn | Springs | true | true |

### Merge outcomes

| matkl | newD | newE | Branch | Result |
|---|---|---|---|---|
| GRP-A | `Schrauben` (carry-forward) | `Fasteners` (source) | UPDATE | `D;E / Schrauben~~Fasteners` |
| GRP-B | not in source | not in source | not touched | unchanged |
| GRP-C | `Federn` | `Springs` | INSERT | `D;E / Federn~~Springs` |

## Validation

Asserts every source `t023t` has the canonical key columns and exposes the value columns the merge consumes.
