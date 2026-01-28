# Excel Catalog Demo Structure

This directory contains demonstrations and utilities for the multiformat-catalog, specifically focusing on Excel integration.

## Directory Organization

### `/` (Root - Main Demos)
- `Demo01_CatalogExplorer.scala` - Basic catalog exploration showing all tables and their schemas
- `Demo02_TypeSafeCatalogExplorer.scala` - Type-safe data access with proper Date types

### `entities/`
- `TestDataEntities.scala` - Case classes matching the test data schema with proper types

### `utils/`
- `DemoUtils.scala` - Common utilities for demos (Spark session creation, formatting)
- `CatalogSchemaCLI.scala` - Command-line tool for catalog schema inspection
- `ConvertTestDataDates.scala` - One-time utility to convert date fields from String to Date

### `tools/`
- `CheckExcelSheets.scala` - CLI tool to verify Excel sheet names and content

### `internal/tests/`
- `TestExcelAppend.scala` - Test for Excel SaveMode.Append functionality
- `TestExcelSheetWriting.scala` - Test for Excel sheet name extraction

## Running Demos

```bash
# Main demos
sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.Demo01_CatalogExplorer"
sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.Demo02_TypeSafeCatalogExplorer"

# CLI tools
sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.utils.CatalogSchemaCLI list"
sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.tools.CheckExcelSheets test-data-typed/sales/2024/q1/transactions.xlsx"

# Internal tests (for debugging)
sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.internal.tests.TestExcelAppend"
sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.internal.tests.TestExcelSheetWriting"

# Data conversion (one-time)
sbt "excelCatalogDemo/runMain ct.dna.catalog.demo.utils.ConvertTestDataDates"
```

## Test Data

The demos use test data from two possible locations:
1. `test-data-typed/` - Converted data with proper Date types (preferred)
2. `../multiformat-catalog/src/test/resources/test-data/` - Original test data

Run `ConvertTestDataDates` to create the typed version with proper Date columns.