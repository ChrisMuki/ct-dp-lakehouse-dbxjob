# FX Rates — Business Logic Documentation

## Source Tables

### TCURR (Exchange Rate Table)

| Field   | Description                                                                 |
|---------|-----------------------------------------------------------------------------|
| `MANDT` | Client                                                                      |
| `KURST` | Exchange Rate Type                                                          |
| `FCURR` | From Currency                                                               |
| `TCURR` | To Currency                                                                 |
| `UKURS` | Exchange Rate                                                               |
| `GDATU` | Valid-from Date (inverted), e.g. `79739675` → `99999999 - 79739675` = `20260324` |

### TCURF (Conversion Factor Table)

| Field   | Description                                                                 |
|---------|-----------------------------------------------------------------------------|
| `MANDT` | Client                                                                      |
| `KURST` | Exchange Rate Type                                                          |
| `FCURR` | From Currency                                                               |
| `TCURR` | To Currency                                                                 |
| `FFACT` | From Factor — unit basis for the source currency                            |
| `TFACT` | To Factor — unit basis for the target currency                              |
| `GDATU` | Valid-from Date (inverted)                                                  |

---

## Relationship between TCURR and TCURF

### TCURR (Exchange Rate Table)

TCURR stores the exchange rate (`UKURS`) between two currencies, along with a valid-from date (`GDATU`).

However, `UKURS` alone does **NOT** always represent a per-unit conversion.
The value is often scaled to avoid precision issues.

### TCURF (Conversion Factor Table)

TCURF provides scaling factors:

- `FFACT` → "From factor"
- `TFACT` → "To factor"

These define the unit basis for the stored rate.

### Why Scaling Exists

For some currencies (e.g., JPY, KRW), per-unit values are very small:

> Example: 1 JPY ≈ 0.009 USD

Instead of storing very small decimals, SAP stores:

> 100 JPY ≈ 0.90 USD

This improves precision and avoids rounding issues.

---

## Core Formula

The correct conversion formula is:

```
amount_in_target = amount_in_source × (TFACT / FFACT) × UKURS
```

### Example

Given:

- **TCURR**: `UKURS = 0.90` (USD → JPY)
- **TCURF**: `FFACT = 1`, `TFACT = 100`

Interpretation: 0.90 USD corresponds to 100 JPY → 1 JPY = 0.90 / 100 = 0.009 USD

### Step-by-step Calculation

To convert 500 JPY → USD:

1. Normalize rate to per-unit:

   ```
   per_unit_rate = UKURS × (TFACT / FFACT)
                 = 0.90 × (1 / 100)
                 = 0.009
   ```

2. Apply conversion:

   ```
   500 × 0.009 = 4.5 USD
   ```

### Negative UKURS

If `UKURS` is negative, it represents an **inverse quotation** (not a negative value).

```
effective_rate = 1 / ABS(UKURS)
final_rate     = (TFACT / FFACT) × effective_rate
```

---

## Transformation Pipeline (`fxrates.scala`)

### Data Sources

| Source               | Description                                                              |
|----------------------|--------------------------------------------------------------------------|
| `sr.ct_gbl_ghp.tcurr` | Live TCURR feed from GHP system                                        |
| `sr.ct_gbl_ghp.tcurf` | Live TCURF feed from GHP system                                        |
| `sr_raw.mn_gbl_spcentral.fxarchive` | Historical archive (Unity Catalog table, no change feed) |

### Filters

- **Rate types**: Only `MGR`, `ZAYD`, `ZMEN`, `P` are processed (`validKurst`)
- **Target currency**: Only rates to `EUR` are computed from source; all other pairs are derived
- **Date cutoff**: GHP rates with `rate_date ≤ 2000-12-31` are excluded (archive covers those)

### Pipeline Steps

1. **Prepare TCURR** — Normalize column names from SR schema variants (`mandt_string` → `mandt`, etc.)
2. **Prepare Archive** — Convert the archive table into the same TCURR shape (hardcoded `mandt = "100"`)
3. **Union** — Combine GHP TCURR + Archive TCURR
4. **Join TCURF** — LEFT JOIN with TCURF on `(mandt, kurst, fcurr, tcurr)` where `tcurf.gdatu >= tcurr.gdatu`; pick the closest factor row via `row_number()`
5. **Compute EUR rates** — Apply the formula: `(TFACT / FFACT) × UKURS` (with negative UKURS handling)
6. **Derive additional rate sets**:
   - **EUR inverse**: `1 / final_rate` (EUR → X)
   - **Cross rates**: `rate(A→EUR) / rate(B→EUR)` = `rate(A→B)`
   - **Identity rates**: `X → X = 1.0`
   - **Target identity**: `EUR → EUR = 1.0`
7. **Union all** and deduplicate on `(rate_date, fcurr, tcurr, kurst)`

### Output Entity: `fx_conversion_rates`

| Column       | Type            | Key | Description                    |
|--------------|-----------------|-----|--------------------------------|
| `rate_date`  | `Date`          | PK  | Effective date of the rate     |
| `fcurr`      | `String`        | PK  | From currency                  |
| `tcurr`      | `String`        | PK  | To currency                    |
| `kurst`      | `String`        | PK  | Rate type                      |
| `final_rate` | `Decimal(38,10)`|     | Computed conversion rate        |

### Merge Strategy

This is a **full-recompute** table — not a change-feed passthrough:

- `whenMatched()` → update `final_rate`
- `whenNotMatched()` → insert new rate
- `whenNotMatchedBySource()` → delete (rate no longer exists in recomputed set)

No `_change_type` / `isDelete` / `isUpsert` pattern — inapplicable because the output is derived (many-to-many from source rows).

---

## Derived Rate Sets — Worked Examples

### Cross Rates

Given EUR base rates for May 2:

| From | To  | Rate  |
|------|-----|-------|
| USD  | EUR | 1.02  |
| GBP  | EUR | 0.92  |
| INR  | EUR | 0.011 |

Cross rates are computed as: `rate(A→B) = rate(A→EUR) / rate(B→EUR)`

| Pair      | Calculation           | Result  |
|-----------|-----------------------|---------|
| USD → GBP | 1.02 / 0.92           | 1.109   |
| USD → INR | 1.02 / 0.011          | 92.727  |
| GBP → USD | 0.92 / 1.02           | 0.902   |
| GBP → INR | 0.92 / 0.011          | 83.636  |
| INR → USD | 0.011 / 1.02          | 0.0108  |
| INR → GBP | 0.011 / 0.92          | 0.0120  |

> **Note:** Same-currency pairs (USD→USD, GBP→GBP, etc.) are excluded from cross rates — those are covered by identity rates below.

### EUR Inverse Rates

Given: `USD → EUR = 1.02` (1 USD = 1.02 EUR)

The inverse is the reciprocal:

```
EUR → USD = 1 / 1.02 = 0.980392
```

To reverse a currency rate, divide 1 by the original rate because you are solving how much of the original currency equals exactly 1 unit of the target currency.

> Formula: `EUR → X = 1 / (X → EUR)`

### Identity Rates

Every currency maps to itself with a rate of 1.0:

| From | To  | Rate |
|------|-----|------|
| EUR  | EUR | 1.0  |
| USD  | USD | 1.0  |
| GBP  | GBP | 1.0  |
| INR  | INR | 1.0  |

These are generated so that downstream consumers can always find a rate for any `(fcurr, tcurr)` pair, even when both currencies are the same.

---

## Pipeline Flow

```
┌─────────────┐     ┌─────────────┐     ┌──────────────────┐
│  GHP TCURR  │     │  FX Archive │     │    GHP TCURF     │
│  (live SR)  │     │  (UC table) │     │    (live SR)     │
└──────┬──────┘     └──────┬──────┘     └────────┬─────────┘
       │ prepareTcurr()    │ prepareArchive()     │
       ▼                   ▼                      │
┌──────────────────────────────┐                  │
│     Combined TCURR           │                  │
│  (GHP + Archive, unionByName)│                  │
└──────────────┬───────────────┘                  │
               │                                  │
               ▼                                  ▼
        ┌──────────────────────────────────────────────┐
        │           LEFT JOIN TCURR × TCURF            │
        │  on (mandt, kurst, fcurr, tcurr, gdatu)      │
        │  pick closest factor via row_number()        │
        └──────────────────────┬───────────────────────┘
                               │
                               ▼
                  ┌────────────────────────┐
                  │   Compute EUR Rates    │
                  │  (TFACT/FFACT) × UKURS │
                  │  handle negative UKURS │
                  └────────────┬───────────┘
                               │
              ┌────────┬───────┼────────┬──────────┐
              ▼        ▼       ▼        ▼          ▼
         ┌────────┐ ┌──────┐ ┌──────┐ ┌────────┐ ┌────────┐
         │  EUR   │ │ EUR  │ │Cross │ │Identity│ │ Target │
         │ Rates  │ │Invrse│ │Rates │ │ Rates  │ │Identity│
         │ X→EUR  │ │EUR→X │ │ A→B  │ │ X→X=1  │ │EUR→EUR │
         └───┬────┘ └──┬───┘ └──┬───┘ └───┬────┘ └───┬────┘
             │         │        │          │          │
             └─────────┴────────┴──────────┴──────────┘
                               │
                               ▼
                  ┌────────────────────────┐
                  │  unionByName + dedup   │
                  │  on (rate_date, fcurr, │
                  │      tcurr, kurst)     │
                  └────────────┬───────────┘
                               │
                               ▼
                  ┌────────────────────────┐
                  │   MERGE into target    │
                  │  fx_conversion_rates   │
                  │  (full-recompute)      │
                  └────────────────────────┘
```

---

## Key Takeaways

- TCURR defines the exchange rate and its validity over time
- TCURF defines how that rate should be scaled
- Always combine both tables to get the correct per-unit rate
- Never use `UKURS` alone without applying factors
- Negative `UKURS` means inverse quotation, not a negative value
- The `pick()` helper handles SR schema variations across systems (e.g., `mandt_string` vs `mandt`)