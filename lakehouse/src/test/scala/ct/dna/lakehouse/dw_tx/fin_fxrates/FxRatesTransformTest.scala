package ct.dna.lakehouse.dw_tx.fin_fxrates

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.lit
import org.scalatest.funsuite.AnyFunSuite

/** Comprehensive unit tests for `fxrates.transformPreparedTcurr` and `fxrates.prepareArchiveAsTcurr`.
  *
  * Covers 16 business rules(new added):
  *   1. Archive rows included in output 2. Archive-only records preserved 3. GDATU inverse date conversion (99999999 - GDATU) 4. Positive UKURS: (TFACT *
  *      UKURS) / FFACT 5. Negative UKURS: TFACT / (FFACT * ABS(UKURS)) 6. Null FFACT defaults to 1 7. Null TFACT defaults to 1 8. Correct TCURF record selected
  *      (earliest where TCURF.GDATU >= TCURR.GDATU) 9. Duplicate rows removed
  * 10. Direct EUR rate generation 11. Inverse EUR rate generation 12. Cross rate generation 13. Identity rate generation 14. Archive + TCURR union record count
  * 15. Precision within tolerance 16. Production issue: Archive IDR→EUR precision
  */
class FxRatesTransformTest extends AnyFunSuite {

  // Reuse existing SparkSession if available (e.g. from TestWithPrefixedTables),
  // otherwise create a local one. Do NOT stop — avoids poisoning the singleton
  // for subsequent test suites (MaktTest etc.) in the same forked JVM.
  @transient lazy val spark: SparkSession = SparkSession
    .builder()
    .master("local[*]")
    .appName("FxRatesTransformTest")
    .config("spark.ui.enabled", "false")
    .config("spark.sql.shuffle.partitions", "2")
    .getOrCreate()

  import spark.implicits._

  // SAP inverted date: 99999999 - 20260101 = 79739898
  private val sapDate = "79739898"

  /** Access the private `transformPreparedTcurr` method via reflection. */
  private lazy val transformMethod = {
    val method = fxrates.getClass.getDeclaredMethods
      .find(m => m.getName == "transformPreparedTcurr" && m.getParameterCount == 2)
      .getOrElse(throw new NoSuchMethodException("fxrates.transformPreparedTcurr"))
    method.setAccessible(true)
    method
  }

  /** Access the private `prepareArchiveAsTcurr` method via reflection. */
  private lazy val archiveMethod = {
    val method = fxrates.getClass.getDeclaredMethods
      .find(m => m.getName == "prepareArchiveAsTcurr" && m.getParameterCount == 1)
      .getOrElse(throw new NoSuchMethodException("fxrates.prepareArchiveAsTcurr"))
    method.setAccessible(true)
    method
  }

  /** Invoke the transform with already-prepared TCURR and raw TCURF. */
  private def runTransform(tcurr: DataFrame, tcurf: DataFrame): DataFrame =
    transformMethod.invoke(fxrates, tcurr, tcurf).asInstanceOf[DataFrame]

  /** Invoke prepareArchiveAsTcurr to convert archive-shaped data to prepared TCURR shape. */
  private def prepareArchive(archiveRaw: DataFrame): DataFrame =
    archiveMethod.invoke(fxrates, archiveRaw).asInstanceOf[DataFrame]

  /** Create an empty TCURF DataFrame with correct schema. */
  private def emptyTcurf: DataFrame =
    spark.emptyDataFrame
      .select(
        lit("").as("mandt"),
        lit("").as("kurst"),
        lit("").as("fcurr"),
        lit("").as("tcurr"),
        lit(0).as("ffact"),
        lit(0).as("tfact"),
        lit("").as("gdatu")
      )
      .where($"mandt" =!= "")

  private def getRate(df: DataFrame, from: String, to: String, kurst: String = "MGR"): Double =
    df.where($"fcurr" === from && $"tcurr" === to && $"kurst" === kurst)
      .select("final_rate")
      .head()
      .getDecimal(0)
      .doubleValue()

  private def getBigDecimalRate(df: DataFrame, from: String, to: String, kurst: String = "MGR"): java.math.BigDecimal =
    df.where($"fcurr" === from && $"tcurr" === to && $"kurst" === kurst)
      .select("final_rate")
      .head()
      .getDecimal(0)

  // =========================================================================
  // 1. Archive rows included in output
  // =========================================================================

  test("1. archive rows are included in output when unioned with GHP data") {
    // GHP TCURR row for USD
    val ghp = Seq(
      ("100", "MGR", "USD", "EUR", -2.0, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    // Archive row for GBP (different currency, same date)
    val archiveRaw = Seq(
      ("MGR", "GBP", "EUR", -0.5, null.asInstanceOf[java.lang.Long], 79739898L)
    ).toDF("kurst_string", "fcurr_string", "tcurr_string", "ukurs_double", "ukurs_long", "gdatu_long")

    val archivePrepared = prepareArchive(archiveRaw)
    val combined = ghp.unionByName(archivePrepared)

    val tcurf = emptyTcurf
    val result = runTransform(combined, tcurf)

    // Both USD (from GHP) and GBP (from archive) should appear
    assert(result.where($"fcurr" === "USD" && $"tcurr" === "EUR").count() == 1)
    assert(result.where($"fcurr" === "GBP" && $"tcurr" === "EUR").count() == 1)
  }

  // =========================================================================
  // 2. Archive-only records preserved
  // =========================================================================

  test("2. archive-only records produce valid output when no GHP data exists") {
    val archiveRaw = Seq(
      ("MGR", "CHF", "EUR", -0.5, null.asInstanceOf[java.lang.Long], 79739898L)
    ).toDF("kurst_string", "fcurr_string", "tcurr_string", "ukurs_double", "ukurs_long", "gdatu_long")

    val archivePrepared = prepareArchive(archiveRaw)
    val tcurf = emptyTcurf
    val result = runTransform(archivePrepared, tcurf)

    // CHF→EUR should be present with rate = 1/0.5 = 2.0
    assert(math.abs(getRate(result, "CHF", "EUR") - 2.0) < 0.0001)
    // Identity and inverse should also exist
    assert(getRate(result, "CHF", "CHF") == 1.0)
    assert(result.where($"fcurr" === "EUR" && $"tcurr" === "CHF").count() == 1)
  }

  // =========================================================================
  // 3. GDATU inverse date conversion (99999999 - GDATU)
  // =========================================================================

  test("3. rate_date is correctly derived from inverted SAP GDATU") {
    val tcurr = Seq(
      ("100", "MGR", "USD", "EUR", -1.10, "79739898"), // 99999999 - 79739898 = 20260101
      ("100", "MGR", "GBP", "EUR", -0.80, "79739897") // 99999999 - 79739897 = 20260102
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = emptyTcurf
    val result = runTransform(tcurr, tcurf)

    val dates = result.select("rate_date").distinct().as[java.sql.Date].collect().map(_.toString).toSet
    assert(dates.contains("2026-01-01"))
    assert(dates.contains("2026-01-02"))
  }

  // =========================================================================
  // 4. Positive UKURS: rate = (TFACT * UKURS) / FFACT
  // =========================================================================

  test("4. positive UKURS is treated as direct quotation: (TFACT * UKURS) / FFACT") {
    // ukurs = 130 (positive), ffact = 100, tfact = 1
    // rate = (1 * 130) / 100 = 1.3
    val tcurr = Seq(
      ("100", "MGR", "JPY", "EUR", 130.0, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = Seq(
      ("100", "MGR", "JPY", "EUR", 100, 1, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ffact", "tfact", "gdatu")

    val result = runTransform(tcurr, tcurf)
    assert(math.abs(getRate(result, "JPY", "EUR") - 1.3) < 0.0001)
  }

  // =========================================================================
  // 5. Negative UKURS: rate = TFACT / (FFACT * ABS(UKURS))
  // =========================================================================

  test("5. negative UKURS is treated as indirect quotation: TFACT / (FFACT * ABS(UKURS))") {
    // ukurs = -2.0, ffact = 1, tfact = 1
    // rate = 1 / (1 * 2) = 0.5
    val tcurr = Seq(
      ("100", "MGR", "USD", "EUR", -2.0, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = Seq(
      ("100", "MGR", "USD", "EUR", 1, 1, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ffact", "tfact", "gdatu")

    val result = runTransform(tcurr, tcurf)
    assert(math.abs(getRate(result, "USD", "EUR") - 0.5) < 0.0001)
  }

  // =========================================================================
  // 6. Null FFACT defaults to 1
  // =========================================================================

  test("6. null FFACT defaults to 1 via coalesce") {
    val tcurr = Seq(
      ("100", "MGR", "GBP", "EUR", 0.80, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    // No matching TCURF row → LEFT JOIN produces null ffact → coalesced to 1
    val tcurf = emptyTcurf
    val result = runTransform(tcurr, tcurf)

    // Positive ukurs, ffact=1 (default), tfact=1 (default) → rate = (1 * 0.80) / 1 = 0.80
    assert(math.abs(getRate(result, "GBP", "EUR") - 0.8) < 0.0001)
  }

  // =========================================================================
  // 7. Null TFACT defaults to 1
  // =========================================================================

  test("7. null TFACT defaults to 1 via coalesce") {
    // Same mechanism as FFACT — both come from the LEFT JOIN with TCURF.
    // When TCURF has no match, both ffact and tfact are null → coalesced to 1.
    val tcurr = Seq(
      ("100", "MGR", "SEK", "EUR", -4.0, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = emptyTcurf
    val result = runTransform(tcurr, tcurf)

    // Negative ukurs = -4.0, tfact=1 (default), ffact=1 (default)
    // rate = 1 / (1 * 4) = 0.25
    assert(math.abs(getRate(result, "SEK", "EUR") - 0.25) < 0.0001)
  }

  // =========================================================================
  // 8. Correct TCURF record selected (earliest where TCURF.GDATU >= TCURR.GDATU)
  // =========================================================================

  test("8. selects TCURF row with smallest GDATU >= TCURR.GDATU (row_number=1)") {
    val tcurr = Seq(
      ("100", "MGR", "USD", "EUR", -1.10, "79739898")
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    // Two TCURF rows qualify (both gdatu >= tcurr.gdatu):
    //   79739898 → smallest (selected, ffact=1)
    //   79739899 → larger (not selected, ffact=2 would change rate)
    val tcurf = Seq(
      ("100", "MGR", "USD", "EUR", 2, 1, "79739899"),
      ("100", "MGR", "USD", "EUR", 1, 1, "79739898")
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ffact", "tfact", "gdatu")

    val result = runTransform(tcurr, tcurf)

    // With ffact=1: rate = 1/1.10 ≈ 0.9091 (if ffact=2 were picked: rate ≈ 0.4545)
    assert(math.abs(getRate(result, "USD", "EUR") - 0.9090909091) < 0.0001)
  }

  // =========================================================================
  // 9. Duplicate rows removed
  // =========================================================================

  test("9. duplicate TCURR rows with same PK produce exactly one output row") {
    val tcurr = Seq(
      ("100", "MGR", "USD", "EUR", -1.10, sapDate),
      ("100", "MGR", "USD", "EUR", -1.10, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = Seq(
      ("100", "MGR", "USD", "EUR", 1, 1, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ffact", "tfact", "gdatu")

    val result = runTransform(tcurr, tcurf)

    // dropDuplicates on PK ensures one row per (rate_date, fcurr, tcurr, kurst)
    val count = result.where($"fcurr" === "USD" && $"tcurr" === "EUR" && $"kurst" === "MGR").count()
    assert(count == 1)
  }

  // =========================================================================
  // 10. Direct EUR rate generation
  // =========================================================================

  test("10. direct EUR base rates are computed for all valid currencies") {
    val tcurr = Seq(
      ("100", "MGR", "USD", "EUR", -1.10, sapDate),
      ("100", "MGR", "INR", "EUR", -88.0, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = Seq(
      ("100", "MGR", "USD", "EUR", 1, 1, sapDate),
      ("100", "MGR", "INR", "EUR", 1, 1, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ffact", "tfact", "gdatu")

    val result = runTransform(tcurr, tcurf)

    // USD→EUR: negative ukurs → 1/1.10 ≈ 0.9091
    assert(math.abs(getRate(result, "USD", "EUR") - (1.0 / 1.10)) < 0.0001)
    // INR→EUR: negative ukurs → 1/88.0
    assert(math.abs(getRate(result, "INR", "EUR") - (1.0 / 88.0)) < 0.0001)
  }

  // =========================================================================
  // 11. Inverse EUR rate generation (EUR→X = 1 / X→EUR)
  // =========================================================================

  test("11. EUR inverse rates are reciprocal of direct rates") {
    val tcurr = Seq(
      ("100", "MGR", "USD", "EUR", -1.10, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = Seq(
      ("100", "MGR", "USD", "EUR", 1, 1, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ffact", "tfact", "gdatu")

    val result = runTransform(tcurr, tcurf)

    val usdToEur = getRate(result, "USD", "EUR")
    val eurToUsd = getRate(result, "EUR", "USD")
    // Product of direct and inverse must equal 1
    assert(math.abs(usdToEur * eurToUsd - 1.0) < 0.0001)
    // EUR→USD ≈ 1.10
    assert(math.abs(eurToUsd - 1.10) < 0.0001)
  }

  // =========================================================================
  // 12. Cross rate generation (source.rate / target.rate)
  // =========================================================================

  test("12. cross rates derived from EUR base rates: USD→INR = (USD→EUR) / (INR→EUR)") {
    val tcurr = Seq(
      ("100", "MGR", "USD", "EUR", -1.10, sapDate),
      ("100", "MGR", "INR", "EUR", -88.0, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = Seq(
      ("100", "MGR", "USD", "EUR", 1, 1, sapDate),
      ("100", "MGR", "INR", "EUR", 1, 1, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ffact", "tfact", "gdatu")

    val result = runTransform(tcurr, tcurf)

    // USD→INR = (1/1.10) / (1/88) = 88/1.10 = 80
    assert(math.abs(getRate(result, "USD", "INR") - 80.0) < 0.01)
    // INR→USD = (1/88) / (1/1.10) = 1.10/88 = 1/80
    assert(math.abs(getRate(result, "INR", "USD") - (1.0 / 80.0)) < 0.001)
  }

  // =========================================================================
  // 13. Identity rate generation (same currency = 1.0)
  // =========================================================================

  test("13. identity rates equal 1.0 for all currencies including EUR") {
    val tcurr = Seq(
      ("100", "MGR", "USD", "EUR", -1.10, sapDate),
      ("100", "MGR", "INR", "EUR", -88.0, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = Seq(
      ("100", "MGR", "USD", "EUR", 1, 1, sapDate),
      ("100", "MGR", "INR", "EUR", 1, 1, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ffact", "tfact", "gdatu")

    val result = runTransform(tcurr, tcurf)

    assert(getRate(result, "USD", "USD") == 1.0)
    assert(getRate(result, "INR", "INR") == 1.0)
    assert(getRate(result, "EUR", "EUR") == 1.0)
  }

  // =========================================================================
  // 14. Archive + TCURR union record count validation
  // =========================================================================

  test("14. archive + GHP union produces correct total output row count") {
    // 1 GHP currency + 1 archive currency = 2 base currencies
    // Per currency pair: direct + inverse + identity = many rows
    // With 2 currencies: 2 direct + 2 inverse + 2 cross + 3 identity = 9
    val ghp = Seq(
      ("100", "MGR", "USD", "EUR", -2.0, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val archiveRaw = Seq(
      ("MGR", "GBP", "EUR", -0.5, null.asInstanceOf[java.lang.Long], 79739898L)
    ).toDF("kurst_string", "fcurr_string", "tcurr_string", "ukurs_double", "ukurs_long", "gdatu_long")

    val archivePrepared = prepareArchive(archiveRaw)
    val combined = ghp.unionByName(archivePrepared)

    val tcurf = emptyTcurf
    val result = runTransform(combined, tcurf)

    // 2 direct (USD→EUR, GBP→EUR) + 2 inverse (EUR→USD, EUR→GBP) +
    // 2 cross (USD→GBP, GBP→USD) + 3 identity (USD→USD, GBP→GBP, EUR→EUR) = 9
    assert(result.count() == 9)
  }

  // =========================================================================
  // 15. Precision validation within tolerance
  // =========================================================================

  test("15. decimal(38,20) precision is maintained through the pipeline") {
    // Use a value that exposes floating-point issues: 1/3
    val tcurr = Seq(
      ("100", "MGR", "XYZ", "EUR", -3.0, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ukurs", "gdatu")

    val tcurf = Seq(
      ("100", "MGR", "XYZ", "EUR", 1, 1, sapDate)
    ).toDF("mandt", "kurst", "fcurr", "tcurr", "ffact", "tfact", "gdatu")

    val result = runTransform(tcurr, tcurf)

    // XYZ→EUR = 1/3 ≈ 0.33333...
    val rate = getBigDecimalRate(result, "XYZ", "EUR")
    // Verify precision: BigDecimal scale should be 20
    assert(rate.scale() == 20, s"Expected scale 20, got ${rate.scale()}")
    // Verify value within tight tolerance
    val expected = new java.math.BigDecimal("0.33333333333333333333")
    assert(rate.subtract(expected).abs().compareTo(new java.math.BigDecimal("0.00000001")) < 0)

    // EUR→XYZ (inverse) = 3.0 — should be exact
    val inverse = getBigDecimalRate(result, "EUR", "XYZ")
    val expectedInverse = new java.math.BigDecimal("3.00000000000000000000")
    assert(inverse.compareTo(expectedInverse) == 0, s"EUR→XYZ expected $expectedInverse but got $inverse")
  }

  // =========================================================================
  // 16. Production issue: Archive IDR→EUR precision
  // =========================================================================

  test("16. archive IDR→EUR rate matches production with diff < 0.00000001") {
    // Simulate the production scenario: IDR has a large negative UKURS from the archive
    // IDR→EUR ukurs = -17536.0 → rate = 1/17536 ≈ 0.00005702...
    val archiveRaw = Seq(
      ("MGR", "IDR", "EUR", -17536.0, null.asInstanceOf[java.lang.Long], 79739898L)
    ).toDF("kurst_string", "fcurr_string", "tcurr_string", "ukurs_double", "ukurs_long", "gdatu_long")

    val archivePrepared = prepareArchive(archiveRaw)
    val tcurf = emptyTcurf
    val result = runTransform(archivePrepared, tcurf)

    val rate = getBigDecimalRate(result, "IDR", "EUR")
    val expectedRate = new java.math.BigDecimal(1).divide(
      new java.math.BigDecimal(17536),
      20,
      java.math.RoundingMode.HALF_UP
    )

    val diff = rate.subtract(expectedRate).abs()
    assert(
      diff.compareTo(new java.math.BigDecimal("0.00000001")) < 0,
      s"IDR→EUR rate $rate differs from expected $expectedRate by $diff (exceeds 0.00000001)"
    )

    // Verify the inverse: EUR→IDR should round-trip correctly
    val inverseRate = getBigDecimalRate(result, "EUR", "IDR")
    val roundTrip = rate.multiply(inverseRate)
    val roundTripDiff = roundTrip.subtract(java.math.BigDecimal.ONE).abs()
    assert(
      roundTripDiff.compareTo(new java.math.BigDecimal("0.00000001")) < 0,
      s"Round-trip IDR→EUR→IDR = $roundTrip, diff from 1.0 = $roundTripDiff"
    )
  }
}

/** sbt "lakehouse/testOnly ct.dna.lakehouse.dw_tx.fin_fxrates.FxRatesTransformTest"
  */
