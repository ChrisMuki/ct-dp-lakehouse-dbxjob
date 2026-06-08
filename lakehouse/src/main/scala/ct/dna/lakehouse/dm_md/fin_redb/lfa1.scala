package ct.dna.lakehouse.dm_md.fin_redb

import ct.dna.lakehouse.core.framework.ChangeFeed
import ct.dna.lakehouse.core.framework.Result
import ct.dna.lakehouse.core.framework.Table
import ct.dna.lakehouse.core.model.Entity
import ct.dna.lakehouse.core.model.Entity.Decimal
import ct.dna.lakehouse.core.model.Entity.PK
import ct.dna.lakehouse.core.model.TableSpec
import ct.dna.lakehouse.core.model.Updated
import ct.dna.lakehouse.sr.ct_gbl_e32
import ct.dna.lakehouse.sr.ct_gbl_epp
import ct.dna.lakehouse.sr.ct_gbl_ghp
import ct.dna.lakehouse.sr.ct_gbl_p12
import ct.dna.lakehouse.sr.ct_gbl_p24
import ct.dna.lakehouse.sr.ct_gbl_p43
import ct.dna.lakehouse.sr.ct_gbl_p61
import ct.dna.lakehouse.sr.ct_gbl_p64
import ct.dna.lakehouse.sr.ct_gbl_p69
import ct.dna.lakehouse.sr.ct_gbl_p73
import ct.dna.lakehouse.sr.ct_gbl_p77
import ct.dna.lakehouse.sr.ct_gbl_p85
import ct.dna.lakehouse.sr.ct_gbl_pbr
import ct.dna.lakehouse.sr.ct_gbl_psp
import ct.dna.lakehouse.sr_raw.mn_gbl_spcustoms.{countries_ww => customs_regions_raw}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

case class DmLfa1(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK lifnr: String,
    land1: String,
    name1: String,
    regio: String,
    stras: String,
    pstlz: String,
    ort01: String,
    country: String,
    iso_code: String,
    eco_regions: String,
    subregion: String,
    @Decimal(19, 17) latitude_geo_center: java.math.BigDecimal,
    @Decimal(21, 18) longitude_geo_center: java.math.BigDecimal,
    member_of_eu: java.lang.Long = null
) extends Entity

object lfa1 extends TableSpec[DmLfa1] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.lfa1,
      ct_gbl_epp.lfa1,
      ct_gbl_ghp.lfa1,
      ct_gbl_p12.lfa1,
      ct_gbl_p24.lfa1,
      ct_gbl_p43.lfa1,
      ct_gbl_p61.lfa1,
      ct_gbl_p64.lfa1,
      ct_gbl_p69.lfa1,
      ct_gbl_p73.lfa1,
      ct_gbl_p77.lfa1,
      ct_gbl_p85.lfa1,
      ct_gbl_pbr.lfa1,
      ct_gbl_psp.lfa1,
      customs_regions_raw
    )

  private val sapTableSpecs: Seq[TableSpec[Entity]] =
    sourceTableSpecs.filterNot(_ == customs_regions_raw)

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    if (feeds.forall { case (_, f) => f.isUnchanged }) return Result.NoChanges

    // SAP source columns consumed before the region join. Scoped locally because lfa1 columns differ from
    // the final DmLfa1 schema (the join appends the country/geo enrichment columns).
    // snapshot() always returns the sr-entity key cols (e.g. lifnr) automatically;
    // _mk_system and _mk_instance are value cols in the source sr-entity and must be listed explicitly.
    val consumedValueColumnNames = Seq("_mk_system", "_mk_instance", "land1", "name1", "regio", "stras", "pstlz", "ort01")

    val lfa1Union = sapTableSpecs
      .map(ts => changeFeeds(ts).snapshot(consumedValueColumnNames))
      .reduce(_.unionByName(_))
      .filter(col("lifnr").isNotNull)
      .distinct()

    // `customs_regions_raw` is a Loaded table — the same `alpha_2_string` can appear in multiple rows
    // across different ingests. Dedupe to one row per country (newest ingest wins) to prevent
    // DELTA_MULTIPLE_SOURCE_ROW_MATCHING_TARGET_ROW_IN_MERGE when the left join fans out.
    val regions = broadcast(
      changeFeeds(customs_regions_raw)
        .snapshot()
        .withColumn(
          "_rn",
          row_number().over(
            Window
              .partitionBy(col("alpha_2_string"))
              .orderBy(col("_mk_created_at").desc_nulls_last, col("_lh_id_in_message").desc_nulls_last)
          )
        )
        .filter(col("_rn") === 1)
        .drop("_rn")
    )

    val combined = lfa1Union
      .as("l")
      .join(regions.as("r"), col("l.land1") === col("r.alpha_2_string"), "left")
      .select(
        col("l._mk_system"),
        col("l._mk_instance"),
        col("l.lifnr"),
        col("l.land1"),
        col("l.name1"),
        col("l.regio"),
        col("l.stras"),
        col("l.pstlz"),
        col("l.ort01"),
        col("r.name_string").as("country"),
        col("r.alpha_2_string").as("iso_code"),
        when(col("r.eco_regions_string").isNull || col("r.eco_regions_string") === "", lit("No Entry"))
          .otherwise(col("r.eco_regions_string"))
          .as("eco_regions"),
        col("r.sub_region_string").as("subregion"),
        col("r.latitude_geo_center_double").cast("decimal(19,17)").as("latitude_geo_center"),
        col("r.longitude_geo_center_double").cast("decimal(21,18)").as("longitude_geo_center"),
        col("r.member_of_eu_long").as("member_of_eu")
      )

    table.overwriteByKeys(combined)
  }

  override def validate(): Unit = {
    super.validate()

    val canonicalValuesRequired = Set("_mk_system", "_mk_instance", "land1", "name1", "regio", "stras", "pstlz", "ort01")
    val canonicalKeys = ct_gbl_e32.lfa1.keyColumnNames.toSet

    sapTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.lfa1.keyColumnNames}"
      )
      val missingValues = canonicalValuesRequired -- spec.valueColumnNames.toSet
      require(
        missingValues.isEmpty,
        s"Source table '$spec' is missing value columns required by transformation: ${missingValues.mkString(", ")}"
      )
    }
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_lfa1(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val lifnr: ColumnWithName = ColumnWithName(prefix, "lifnr")
  val land1: ColumnWithName = ColumnWithName(prefix, "land1")
  val name1: ColumnWithName = ColumnWithName(prefix, "name1")
  val regio: ColumnWithName = ColumnWithName(prefix, "regio")
  val stras: ColumnWithName = ColumnWithName(prefix, "stras")
  val pstlz: ColumnWithName = ColumnWithName(prefix, "pstlz")
  val ort01: ColumnWithName = ColumnWithName(prefix, "ort01")
  val country: ColumnWithName = ColumnWithName(prefix, "country")
  val iso_code: ColumnWithName = ColumnWithName(prefix, "iso_code")
  val eco_regions: ColumnWithName = ColumnWithName(prefix, "eco_regions")
  val subregion: ColumnWithName = ColumnWithName(prefix, "subregion")
  val latitude_geo_center: ColumnWithName = ColumnWithName(prefix, "latitude_geo_center")
  val longitude_geo_center: ColumnWithName = ColumnWithName(prefix, "longitude_geo_center")
  val member_of_eu: ColumnWithName = ColumnWithName(prefix, "member_of_eu")
}

object C_lfa1 extends C_lfa1("") {
  def withDFAlias(alias: String): C_lfa1 = new C_lfa1(alias)
  def withoutDFAlias: C_lfa1 = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_lfa1 = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
