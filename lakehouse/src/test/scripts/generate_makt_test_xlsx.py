#!/usr/bin/env python3
"""Regenerate MaktTest.xlsx for ct.dna.lakehouse.dm_md.fin_hawk.MaktTest.

Run via:  python3 dp-lakehouse-dbxjob/lakehouse/src/test/scripts/generate_makt_test_xlsx.py

------------------------------------------------------------------------------
Test design — full cross-product of per-language transition cases.
------------------------------------------------------------------------------

For each language X ∈ {D, E} we enumerate all 12 transitions a single source
row can undergo between two snapshots ("pre" → "post"):

   1  Wert v1     →  anderer Wert v2
   2  Wert v1     →  NULL                       (maktx becomes NULL)
   3  Wert v1     →  gleicher Wert v1, andere Spalte (_mk_created_at) anders
   4  Wert v1     →  Zeile bytegleich           (no CDF event)
   5  Wert v1     →  Zeile gelöscht             (CDF delete)
   6  NULL        →  Wert v
   7  NULL        →  NULL, andere Spalte anders
   8  NULL        →  Zeile bytegleich           (no CDF event)
   9  NULL        →  Zeile gelöscht             (CDF delete)
  10  keine Zeile →  Wert v                     (CDF insert)
  11  keine Zeile →  keine Zeile                (no event, no row)
  12  keine Zeile →  NULL                       (CDF insert with NULL maktx)

The fixture instantiates the full 12 × 12 matrix as `matnr = D{i:02}E{j:02}`,
where i is the D-language case and j is the E-language case. The combination
(11, 11) is omitted because no row exists at all.  →  143 matnrs.

The fixture is consumed three ways:

  • Phase 1: "pre" sheets are loaded into both the E32 and EPP source tables
    (which start empty). Every row therefore arrives as a CDF insert.
  • Phase 2: the E32 source is MERGE-aligned to "post" via syncToSource. The
    diff between "pre" and "post" generates a per-row CDF stream
    (insert / update / delete) covering every transition. The EPP source is
    NOT touched, so the EPP half of the target must remain bit-identical to
    its phase-1 state.
  • Phase 3: the EPP source is dropped, recreated, and freshly loaded with
    "post". Because the previously-recorded known commit no longer exists,
    the framework treats the feed as a snapshot (isSnapshot = true). The E32
    half of the target must remain bit-identical to phase 2.

E32 and EPP source rows are byte-identical except for the `_mk_system` column.
`_mk_org = "CT"` and `_mk_site = "GBL"` everywhere.

`_lh_ingest_warning` carries a per-row label of the form "D=cN;E=cM" so that
diffs immediately point at the failing transition combination.

NULL vs empty-string contract (matters for the Excel cells):

  - Empty cell (None in openpyxl)  →  Spark NULL
  - Cell with the empty string ""  →  Spark "" (different from NULL!)

The production code applies `coalesce(maktx, lit(""))` inside pivotByLanguage,
i.e. an UPDATE event that carries `maktx = NULL` lands in the target as the
empty string, while a DELETE event (or no source row at all) leaves the
target column as NULL. The expected target sheets reflect that exactly.
"""
from datetime import datetime
from pathlib import Path

from openpyxl import Workbook

OUT = (
    Path(__file__).resolve().parents[1]
    / "resources" / "ct" / "dna" / "lakehouse" / "dm_md" / "fin_hawk" / "MaktTest.xlsx"
)

# ---------------------------------------------------------------------------
# Source schema: ct.dna.lakehouse.sr.ct_gbl_*.E_makt (entity column order).
# ---------------------------------------------------------------------------
SRC_HEADER = [
    "mandt", "matnr", "spras", "maktx", "maktg",
    "_mk_org", "_mk_site", "_mk_system", "_mk_instance", "_mk_partition",
    "_mk_file", "_mk_container", "_mk_account", "_mk_created_at",
    "_lh_id_in_message", "_lh_ingest_warning",
]

TGT_HEADER = ["_mk_system", "_mk_instance", "matnr", "spras", "maktx", "_maktx_d", "_maktx_e"]

# Two distinct timestamps so cases 3 and 7 can flip `_mk_created_at` between
# pre and post — that bumps the row hash and therefore generates a CDF
# update event even though `maktx` is unchanged.
TS_PRE  = datetime(2024, 1, 1, 12, 0, 0)
TS_POST = datetime(2024, 6, 1, 12, 0, 0)

INSTANCE = "100"
ORG  = "CT"
SITE = "GBL"

# ---------------------------------------------------------------------------
# Per-language case definitions.
#
#   pre_row  : a source row for this language exists in the "pre" snapshot.
#   pre_mx   : that row's `maktx` value in "pre". None ⇒ empty cell ⇒ Spark NULL.
#   post_row : a source row exists in "post".
#   post_mx  : that row's `maktx` value in "post".
#   alt_col  : pre vs post differ on a non-pivot value column (here:
#              `_mk_created_at`), so syncToSource emits an UPDATE CDF event
#              even though `maktx` is identical. Distinguishes "value changed
#              but not in maktx" (event) from "row bit-identical" (no event).
#
# Case 11 has no row in either snapshot — represents matnrs that never appear
# in this language. Combined with case 11 on the other language the matnr does
# not exist at all and is excluded from the fixture.
# ---------------------------------------------------------------------------
CASES = {
    1:  dict(pre_row=True,  pre_mx="pre",  post_row=True,  post_mx="post", alt_col=False),
    2:  dict(pre_row=True,  pre_mx="pre",  post_row=True,  post_mx=None,   alt_col=False),
    3:  dict(pre_row=True,  pre_mx="same", post_row=True,  post_mx="same", alt_col=True),
    4:  dict(pre_row=True,  pre_mx="same", post_row=True,  post_mx="same", alt_col=False),
    5:  dict(pre_row=True,  pre_mx="pre",  post_row=False, post_mx=None,   alt_col=False),
    6:  dict(pre_row=True,  pre_mx=None,   post_row=True,  post_mx="post", alt_col=False),
    7:  dict(pre_row=True,  pre_mx=None,   post_row=True,  post_mx=None,   alt_col=True),
    8:  dict(pre_row=True,  pre_mx=None,   post_row=True,  post_mx=None,   alt_col=False),
    9:  dict(pre_row=True,  pre_mx=None,   post_row=False, post_mx=None,   alt_col=False),
    10: dict(pre_row=False, pre_mx=None,   post_row=True,  post_mx="post", alt_col=False),
    11: dict(pre_row=False, pre_mx=None,   post_row=False, post_mx=None,   alt_col=False),
    12: dict(pre_row=False, pre_mx=None,   post_row=True,  post_mx=None,   alt_col=False),
}
ALL_CASES = sorted(CASES.keys())

ABSENT = object()  # sentinel for "no source row"


def matnr_for(i, j):
    return f"D{i:02d}E{j:02d}"


def _maktx_value(case_id, snapshot, lang, matnr):
    """Concrete maktx value for the source row of `lang` in the given snapshot.

    Returns:
      ABSENT — the row itself does not exist in this snapshot
      None   — the row exists but maktx is NULL (empty cell)
      str    — the row exists with this maktx value
    """
    c = CASES[case_id]
    present = c["pre_row"] if snapshot == "pre" else c["post_row"]
    if not present:
        return ABSENT
    raw = c["pre_mx"] if snapshot == "pre" else c["post_mx"]
    if raw is None:
        return None
    # Per-language, per-matnr-unique values: a misplaced value is obvious in a diff.
    return f"{lang}_{matnr}_{raw}"


def _ts_value(case_id, snapshot):
    """`_mk_created_at` per snapshot. Differs between pre/post only when
    `alt_col` is set (cases 3 and 7). Otherwise constant — those rows must
    NOT generate a CDF event when their maktx is unchanged."""
    c = CASES[case_id]
    if not c["alt_col"]:
        return TS_PRE
    return TS_PRE if snapshot == "pre" else TS_POST


def src_row(matnr, spras, system, maktx, ts, warning):
    """Build one source-entity row with constant metadata defaults.
    `maktx` may be None to emit an empty cell (Spark NULL)."""
    return [
        "100",       # mandt
        matnr,       # matnr
        spras,       # spras
        maktx,       # maktx (None ⇒ NULL cell)
        None,        # maktg (unused by makt; always NULL)
        ORG,         # _mk_org    = "CT"
        SITE,        # _mk_site   = "GBL"
        system,      # _mk_system
        INSTANCE,    # _mk_instance
        "p",         # _mk_partition
        "f",         # _mk_file
        "c",         # _mk_container
        "a",         # _mk_account
        ts,          # _mk_created_at
        1,           # _lh_id_in_message
        warning,     # _lh_ingest_warning
    ]


def build_source_rows(system, snapshot):
    """All source rows for one (system, snapshot) combination, ordered by
    (matnr ASC, spras ASC). E32 and EPP differ only by `_mk_system`."""
    rows = []
    for i in ALL_CASES:
        for j in ALL_CASES:
            if i == 11 and j == 11:
                continue
            matnr = matnr_for(i, j)
            warning = f"D=c{i:02d};E=c{j:02d}"
            for lang, case_id in (("D", i), ("E", j)):
                mx = _maktx_value(case_id, snapshot, lang, matnr)
                if mx is ABSENT:
                    continue
                rows.append(
                    src_row(matnr, lang, system, mx, _ts_value(case_id, snapshot), warning)
                )
    return rows


# ---------------------------------------------------------------------------
# Pivot simulator. Mirrors makt.pivotByLanguage one-to-one.
# Returns (value, changed) for one language under one feed mode.
#   value ∈ {None, str}       (Spark NULL or string; "" is a valid string)
#   changed ∈ {True, False}
# ---------------------------------------------------------------------------
def pivot_cdf(case_id, snapshot_kind, lang, matnr):
    """CDF mode. snapshot_kind ∈ {'initial', 'delta'}.
       'initial' = source went empty → "pre" (every existing pre row is an insert).
       'delta'   = source went "pre" → "post" (insert / update / delete per diff)."""
    c = CASES[case_id]

    if snapshot_kind == "initial":
        if not c["pre_row"]:
            return (None, False)
        # insert event with the pre value (None ⇒ NULL maktx)
        raw = c["pre_mx"]
        value_after_coalesce = "" if raw is None else f"{lang}_{matnr}_{raw}"
        return (value_after_coalesce, True)

    # snapshot_kind == "delta"
    if not c["pre_row"] and not c["post_row"]:
        return (None, False)  # case 11
    if c["pre_row"] and not c["post_row"]:
        return (None, True)   # delete → value=NULL, changed=true
    if not c["pre_row"] and c["post_row"]:
        # insert
        raw = c["post_mx"]
        return ("" if raw is None else f"{lang}_{matnr}_{raw}", True)
    # both rows present: update only if anything actually differs
    same_maktx = c["pre_mx"] == c["post_mx"]
    if same_maktx and not c["alt_col"]:
        return (None, False)  # bit-identical → no event
    raw = c["post_mx"]
    return ("" if raw is None else f"{lang}_{matnr}_{raw}", True)


def pivot_snapshot(case_id, lang, matnr):
    """Snapshot mode. The framework forces _changed_x = true for every
    language within a present matnr-group. value is the post `maktx` if the
    row exists for this language, else NULL (which clears the language)."""
    c = CASES[case_id]
    if not c["post_row"]:
        return (None, True)  # missing language ⇒ clear it
    raw = c["post_mx"]
    return ("" if raw is None else f"{lang}_{matnr}_{raw}", True)


# ---------------------------------------------------------------------------
# Merge simulator. Mirrors makt.executeTransaction one-to-one.
# ---------------------------------------------------------------------------
def merge_one(target_pre, source):
    """Apply the makt merge for a single matnr.

    target_pre : (d, e) tuple of current target values, or None if absent.
                 d/e are str (incl. "") or None (NULL).
    source     : pivot dict {value_d, changed_d, value_e, changed_e}, or
                 None if no pivot row exists for this matnr.

    Returns the new (d, e), or None if the row should be absent.
    Snapshot's whenNotMatchedBySource path is handled by the caller.
    """
    if source is None:
        return target_pre  # not in pivot → leave target alone (CDF) / handled separately (snapshot)

    if target_pre is None:
        # whenNotMatched — insert iff at least one _value_x is non-null
        if source["value_d"] is None and source["value_e"] is None:
            return None
        return (source["value_d"], source["value_e"])

    # whenMatched: apply changed-vs-keep per language
    new_d = source["value_d"] if source["changed_d"] else target_pre[0]
    new_e = source["value_e"] if source["changed_e"] else target_pre[1]
    if new_d is None and new_e is None:
        return None  # whenMatched().delete()
    return (new_d, new_e)


def make_pivot(i, j, mode):
    """Build the pivot dict for matnr (i,j) under one feed mode, or None
    if no pivot row exists. mode ∈ {'initial', 'delta', 'snapshot'}."""
    matnr = matnr_for(i, j)
    if mode == "snapshot":
        ci, cj = CASES[i], CASES[j]
        if not ci["post_row"] and not cj["post_row"]:
            return None  # matnr absent from snapshot
        vd, cd = pivot_snapshot(i, "D", matnr)
        ve, ce = pivot_snapshot(j, "E", matnr)
        return dict(value_d=vd, changed_d=cd, value_e=ve, changed_e=ce)

    vd, cd = pivot_cdf(i, mode, "D", matnr)
    ve, ce = pivot_cdf(j, mode, "E", matnr)
    if not cd and not ce:
        return None  # no events at all → matnr not in pivot
    return dict(value_d=vd, changed_d=cd, value_e=ve, changed_e=ce)


def apply_phase(prev_state_per_system, system, mode, snapshot_systems):
    """Apply one merge phase for one system and return the new state map."""
    prev = prev_state_per_system.get(system, {})
    new_state = dict(prev)
    for i in ALL_CASES:
        for j in ALL_CASES:
            if i == 11 and j == 11:
                continue
            matnr = matnr_for(i, j)
            target_pre = prev.get(matnr)
            pivot = make_pivot(i, j, mode)

            if pivot is None and target_pre is not None and system in snapshot_systems:
                # whenNotMatchedBySource on a snapshotted system → delete.
                del new_state[matnr]
                continue

            result = merge_one(target_pre, pivot)
            if result is None:
                if matnr in new_state:
                    del new_state[matnr]
            else:
                new_state[matnr] = result
    return new_state


def state_to_rows(state_per_system):
    """Flatten {system: {matnr: (d,e)}} into target-entity rows in
    (system ASC, matnr ASC) order."""
    rows = []
    for system in sorted(state_per_system.keys()):
        for matnr in sorted(state_per_system[system].keys()):
            d, e = state_per_system[system][matnr]
            spras = ";".join([lang for lang, v in (("D", d), ("E", e)) if v is not None])
            maktx = "~~".join([v for v in (d, e) if v is not None])
            rows.append([system, INSTANCE, matnr, spras, maktx, d, e])
    return rows


# ---------------------------------------------------------------------------
# Workbook assembly.
# ---------------------------------------------------------------------------
wb = Workbook()
del wb["Sheet"]


def add_sheet(name, header, rows):
    ws = wb.create_sheet(name)
    ws.append(header)
    for r in rows:
        ws.append(r)


# ---- Source sheets — identical between E32 and EPP except _mk_system. ----
e32_pre  = build_source_rows("E32", "pre")
e32_post = build_source_rows("E32", "post")
epp_pre  = build_source_rows("EPP", "pre")
epp_post = build_source_rows("EPP", "post")

add_sheet("e32_phase1", SRC_HEADER, e32_pre)
add_sheet("epp_phase1", SRC_HEADER, epp_pre)
add_sheet("e32_phase2", SRC_HEADER, e32_post)
add_sheet("epp_phase3", SRC_HEADER, epp_post)


# ---- Expected target sheets, derived from the merge simulator. ------------
add_sheet("target_phase0", TGT_HEADER, [])  # phase 0: target empty.

# Phase 1: both E32 and EPP get their first feed; both arrive as CDF inserts.
state = {}
state["E32"] = apply_phase(state, "E32", "initial", snapshot_systems=set())
state["EPP"] = apply_phase(state, "EPP", "initial", snapshot_systems=set())
add_sheet("target_phase1", TGT_HEADER, state_to_rows(state))

# Phase 2: only E32 sees a CDF delta pre→post; EPP rows must stay unchanged.
state["E32"] = apply_phase(state, "E32", "delta", snapshot_systems=set())
add_sheet("target_phase2", TGT_HEADER, state_to_rows(state))

# Phase 3: EPP rebuilt from scratch and re-loaded with post as a snapshot;
# E32 rows must stay unchanged.
state["EPP"] = apply_phase(state, "EPP", "snapshot", snapshot_systems={"EPP"})
add_sheet("target_phase3", TGT_HEADER, state_to_rows(state))


wb.save(OUT)
print(f"wrote {OUT}")
print(f"  e32_phase1 source rows: {len(e32_pre)}")
print(f"  epp_phase1 source rows: {len(epp_pre)}")
print(f"  e32_phase2 source rows: {len(e32_post)}")
print(f"  epp_phase3 source rows: {len(epp_post)}")
