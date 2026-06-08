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

## Final Rate Calculation

The `final_rate` stored in `fx_conversion_rates` is computed from TCURR + TCURF as follows:

### Positive UKURS (direct quotation)

```
final_rate = (TFACT × UKURS) / FFACT
```

### Negative UKURS (inverse quotation)

A negative `UKURS` represents an **inverse quotation** — not a negative value.

```
final_rate = TFACT / (FFACT × ABS(UKURS))
```

### Zero-value Guard

If `FFACT = 0` or `TFACT = 0`, the rate is set to `null` and excluded from output.

### Example 1: JPY → EUR (positive UKURS, large FFACT)

Given:

- **TCURR**: `FCURR = JPY`, `TCURR = EUR`, `UKURS = 0.6115`
- **TCURF**: `FFACT = 100`, `TFACT = 1`

Calculation (positive UKURS):

```
final_rate = (TFACT × UKURS) / FFACT
           = (1 × 0.6115) / 100
           = 0.006115
```

Interpretation: SAP stores the rate for 100 JPY (FFACT=100) rather than 1 JPY.
The formula normalizes this to a per-unit rate: 1 JPY = 0.006115 EUR.

### Example 2: USD → EUR (negative UKURS)

Given:

- **TCURR**: `FCURR = USD`, `TCURR = EUR`, `UKURS = -1.10`
- **TCURF**: `FFACT = 1`, `TFACT = 1`

Calculation (negative UKURS):

```
final_rate = TFACT / (FFACT × ABS(UKURS))
           = 1 / (1 × 1.10)
           = 0.909091
```

Interpretation: 1 USD = 0.909091 EUR

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
4. **Join TCURF** — LEFT JOIN with TCURF on `(mandt, kurst, fcurr, tcurr)` where `tcurf.gdatu >= tcurr.gdatu`; pick the earliest TCURF row whose GDATU is greater than or equal to the TCURR GDATU using `row_number()`
5. **Compute EUR rates** — Apply `(TFACT × UKURS) / FFACT` for positive UKURS, or `TFACT / (FFACT × ABS(UKURS))` for negative UKURS; null if FFACT or TFACT is zero
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
        │  pick earliest TCURF where GDATU >= TCURR    │
        │  GDATU using row_number()                    │
        └──────────────────────┬───────────────────────┘
                               │
                               ▼
                  ┌─────────────────────────────────┐
                  │       Compute EUR Rates          │
                  │  +UKURS: (TFACT×UKURS)/FFACT     │
                  │  −UKURS: TFACT/(FFACT×ABS(UKURS))│
                  └────────────────┬────────────────┘
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

## Precision Handling

The business logic did **not** change.

Previously, Spark evaluated the calculation through multiple intermediate decimal operations.
During those intermediate steps, precision was lost before the final rate was produced.
For currencies with very small EUR rates (e.g., IDR → EUR ≈ `0.00004921196874`), the
intermediate precision loss compounded when that value was used to derive inverse rates
(EUR → IDR), producing a noticeable difference in the final result.

The calculation was restructured so that intermediate arithmetic preserves `decimal(38,20)`
precision throughout the calculation before the final rate is stored.

The updated results were validated against the SAP archive/source data and align with the
expected rates.

### Example: IDR precision issue

```
IDR → EUR = 0.00004921196874
```

Derived inverse:

```
EUR → IDR = 1 / 0.00004921196874
          ≈ 20320.26
```

In the FX rates pipeline, inverse rates are generated by taking the reciprocal of the EUR
rate. Because of this, even a very small precision loss in the IDR → EUR rate can become a
much larger difference when the EUR → IDR rate is derived.

This was the scenario that exposed the precision issue during validation. Preserving precision
throughout the intermediate calculation ensures the derived inverse rates align with the SAP
archive/source data.

---

## Key Takeaways

- TCURR defines the exchange rate and its validity over time
- TCURF defines how that rate should be scaled
- Always combine both tables to get the correct per-unit rate
- Never use `UKURS` alone without applying factors
- Negative `UKURS` means inverse quotation, not a negative value
- The `pick()` helper handles SR schema variations across systems (e.g., `mandt_string` vs `mandt`)