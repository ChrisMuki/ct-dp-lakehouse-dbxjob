# Authoring test `.xlsx` fixtures

Test fixtures for `TestDataManager`-backed suites (e.g. `MaktTest`) live under
`lakehouse/src/test/resources/<pkg>/<Foo>Test.xlsx` and are produced from a
sibling generator script under
`lakehouse/src/test/scripts/<pkg>/generate_<foo>_test_xlsx.py`. The reference
implementation is
[`generate_makt_test_xlsx.py`](ct/dna/lakehouse/dm_md/fin_hawk/generate_makt_test_xlsx.py).

This note captures the conventions that are easy to get wrong, and provides
**copy-paste Python helpers** at the bottom that already encode them.

## 1. Comment columns (`#`-prefix)

`TestDataManager.read` ignores every column whose header starts with `#` (see
`_5_lakehouseCore/.../testutils/TestDataManager.scala`, the "Spalten, deren
Header mit `#` beginnt, sind Kommentar-Spalten" block).

Use comment columns for any per-row human context that does not belong to the
entity schema — case ids, transition descriptions, expected outcomes. They can
appear anywhere in the column order; convention is to put them **first** so a
reader sees them before the noisy metadata columns.

```text
#D_case         | #E_case            | mandt | matnr  | spras | maktx        | ...
value -> NULL   | no row -> value    | 100   | D02E10 | D     | D_D02E10_pre | ...
```

The header column count check on read excludes `#`-prefixed columns, so
`schema.fields.length == header.count(h => !h.startsWith("#"))` must hold.

## 2. NULL vs. empty string (`""`)

Spark distinguishes `NULL` from `""`. Both must be representable in the
fixture and must survive the round trip through POI / `TestDataManager`.

| Intent      | Cell appearance                                   | XML the writer must emit                                  |
| ----------- | ------------------------------------------------- | --------------------------------------------------------- |
| `NULL`      | Truly blank cell. Nothing in the formula bar.     | `<c r="…"/>` (no `t`, no inline value).                   |
| `""` string | Looks blank, but with the **Excel quote-prefix**. | `<c r="…" s="…" t="inlineStr"><is><t></t></is></c>`       |

The quote-prefix is what you get in Excel by typing `'` followed by Enter into
an empty cell. POI reads such a cell as `STRING`/`""`; a bare `<c/>` is read
as `BLANK` and becomes Spark `NULL`.

### openpyxl gotcha

openpyxl 3.0.9 short-circuits empty-string cells in `cell/_writer.py`:

```python
if value is None or value == "":
    xf.write(el)   # bare <c/> — indistinguishable from a missing cell
    return
```

That collapses `""` into `NULL` at write time. Use `patch_openpyxl_empty_string_cells()`
from the utilities below **at module top, before any `Workbook()` call**, so
that `data_type=='s'` cells always emit `<is><t></t></is>`.

## 3. Conditional formatting for visibility

Empty strings and NULLs look identical in the grid. To keep the two visually
distinct, attach a per-sheet conditional formatting rule to the **data range
only** — never workbook-wide or whole-column, that would shade everything
outside the table as well.

Use `add_empty_string_highlight()` from the utilities below.

Notes:

- The formula's anchor cell (`A2`) is interpreted **relatively** by Excel: the
  rule is evaluated cell-by-cell across the data range.
- Do **not** add a rule for `NULL`. Cells outside the table are blank too, so
  an `ISBLANK`-rule on a wider range would flood the sheet with colour.
- `FFFACD` (light yellow) is the project default fill for empty strings.

## 4. Regenerating and validating

Generators are stand-alone scripts; run them from the repo root with the
project's `python3`:

```bash
python3 lakehouse/src/test/scripts/<pkg>/generate_<foo>_test_xlsx.py
```

They write directly to `lakehouse/src/test/resources/<pkg>/<Foo>Test.xlsx`.
Always rerun the corresponding ScalaTest afterwards:

```bash
sbt "lakehouse/testOnly ct.dna.lakehouse.<pkg>.<Foo>Test"
```

A quick XML sanity check is useful when in doubt:

```bash
unzip -q <Foo>Test.xlsx -d /tmp/x
grep -oE '<c r="F[0-9]+"[^/]*</c>|<c r="F[0-9]+"[^/]*/>' /tmp/x/xl/worksheets/sheet*.xml | head
```

If you see `<c r="F62" s="2" t="inlineStr"></c>` (no `<is>`), the monkey-patch
is missing or did not apply — empty strings are being silently dropped. If you
see `<c r="F62" s="2" t="inlineStr"><is><t></t></is></c>`, the cell is a
proper empty string.

## 5. Checklist for a new generator

1. Put the script under `lakehouse/src/test/scripts/<pkg>/`, mirroring the
   resources/scala package path.
2. Resolve the output path relative to the script — do not hard-code. Use
   `resource_sibling()`.
3. Call `patch_openpyxl_empty_string_cells()` at module top, before any
   `Workbook()` call.
4. Add rows with `write_row()`, which handles the empty-string case correctly.
5. After populating each sheet, call `add_empty_string_highlight()` so empty
   strings are visually distinct from NULLs.
6. Run the matching ScalaTest to confirm the fixture is consumable.

---

## 6. Reusable helpers

Drop the following block at the top of any new generator. Everything below is
self-contained and depends only on `openpyxl` 3.x. The reference generator
[`generate_makt_test_xlsx.py`](ct/dna/lakehouse/dm_md/fin_hawk/generate_makt_test_xlsx.py)
uses an inline equivalent of the same logic.

```python
"""Reusable helpers for authoring TestDataManager-readable .xlsx fixtures."""
from pathlib import Path

from openpyxl import LXML
from openpyxl.cell import _writer as _cell_writer
from openpyxl.formatting.rule import FormulaRule
from openpyxl.styles import PatternFill
from openpyxl.utils import get_column_letter
from openpyxl.worksheet import _writer as _ws_writer
from openpyxl.xml.functions import Element, SubElement, XML_NS

# Light yellow used to highlight empty-string cells ("") so they stand out
# from real NULL cells (which stay default-empty).
EMPTY_STRING_FILL = PatternFill(start_color="FFFACD", end_color="FFFACD", fill_type="solid")


def patch_openpyxl_empty_string_cells() -> None:
    """Monkey-patch openpyxl so empty-string cells are written as real
    `<is><t></t></is>` inline strings instead of bare `<c/>`. Call once,
    before any `Workbook()` instantiation. Idempotent.

    See the openpyxl gotcha in TEST_XLSX_AUTHORING.md section 2 for the why.
    """
    if getattr(_cell_writer, "_EMPTY_STRING_PATCH_APPLIED", False):
        return

    def _patched_etree_write_cell(xf, worksheet, cell, styled=None):
        value, attributes = _cell_writer._set_attributes(cell, styled)
        el = Element("c", attributes)
        if cell.data_type == "s":
            is_el = SubElement(el, "is")
            t_el = SubElement(is_el, "t")
            if value:
                t_el.text = value
            if value is not None and value != value.strip():
                t_el.set("{%s}space" % XML_NS, "preserve")
            xf.write(el)
            return
        _cell_writer._ORIGINAL_etree_write_cell(xf, worksheet, cell, styled)

    def _patched_lxml_write_cell(xf, worksheet, cell, styled=False):
        value, attributes = _cell_writer._set_attributes(cell, styled)
        if cell.data_type == "s":
            with xf.element("c", attributes):
                with xf.element("is"):
                    attrs = {}
                    if value is not None and value != value.strip():
                        attrs["{%s}space" % XML_NS] = "preserve"
                    el = Element("t", attrs)
                    el.text = value or ""
                    xf.write(el)
            return
        _cell_writer._ORIGINAL_lxml_write_cell(xf, worksheet, cell, styled)

    _cell_writer._ORIGINAL_etree_write_cell = _cell_writer.etree_write_cell
    _cell_writer._ORIGINAL_lxml_write_cell = _cell_writer.lxml_write_cell
    _cell_writer.etree_write_cell = _patched_etree_write_cell
    _cell_writer.lxml_write_cell = _patched_lxml_write_cell
    _cell_writer.write_cell = _patched_lxml_write_cell if LXML else _patched_etree_write_cell
    # worksheet/_writer captured the symbol at import time — patch there too.
    _ws_writer.write_cell = _cell_writer.write_cell
    _cell_writer._EMPTY_STRING_PATCH_APPLIED = True


def resource_sibling(script_file: str, filename: str) -> Path:
    """Resolve `…/test/scripts/<pkg>/<script>` to `…/test/resources/<pkg>/<filename>`.

    Call with `__file__` as the first argument:
        OUT = resource_sibling(__file__, "FooTest.xlsx")
    """
    here = Path(script_file).resolve()
    scripts_root = next(p for p in here.parents if p.name == "scripts" and p.parent.name == "test")
    pkg = here.parent.relative_to(scripts_root)
    return scripts_root.parent / "resources" / pkg / filename


def write_row(ws, values) -> None:
    """Append one row, encoding `""` as a quote-prefixed inline-string cell.

    Requires `patch_openpyxl_empty_string_cells()` to have been called first.
    `None` stays NULL (truly blank); any other value goes through openpyxl's
    default type inference.
    """
    ws.append(values)
    row_idx = ws.max_row
    for col_idx, value in enumerate(values, start=1):
        if value == "":
            cell = ws.cell(row=row_idx, column=col_idx)
            cell.value = ""
            cell.data_type = "s"
            cell.quotePrefix = True


def add_empty_string_highlight(ws, header) -> None:
    """Attach a sheet-local CF rule that highlights every non-blank, zero-length
    cell in the populated data range. No-op if the sheet has no data rows.

    The rule uses `AND(NOT(ISBLANK(A2)), LEN(A2)=0)` rather than `A2=""` because
    Excel treats `BLANK = ""` as true, which would defeat the purpose.
    """
    if ws.max_row < 2:
        return
    last_col = get_column_letter(len(header))
    data_range = f"A2:{last_col}{ws.max_row}"
    rule = FormulaRule(
        formula=["AND(NOT(ISBLANK(A2)), LEN(A2)=0)"],
        fill=EMPTY_STRING_FILL,
    )
    ws.conditional_formatting.add(data_range, rule)
```

### Minimal generator skeleton

```python
#!/usr/bin/env python3
from openpyxl import Workbook

# Either inline the helpers from section 6 above, or import them from a
# shared module that re-exports them.
from test_xlsx_authoring_helpers import (
    patch_openpyxl_empty_string_cells,
    resource_sibling,
    write_row,
    add_empty_string_highlight,
)

patch_openpyxl_empty_string_cells()

OUT = resource_sibling(__file__, "FooTest.xlsx")

HEADER = ["#case", "id", "value"]
ROWS = [
    ["plain",          1, "hello"],
    ["empty string",   2, ""],     # → highlighted, read as Spark ""
    ["null",           3, None],   # → blank cell, read as Spark NULL
]

wb = Workbook()
del wb["Sheet"]
ws = wb.create_sheet("data")
ws.append(HEADER)
for r in ROWS:
    write_row(ws, r)
add_empty_string_highlight(ws, HEADER)

wb.save(OUT)
print(f"wrote {OUT}")
```
