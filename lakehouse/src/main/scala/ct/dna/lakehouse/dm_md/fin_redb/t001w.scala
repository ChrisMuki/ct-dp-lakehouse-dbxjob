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

case class DmT001W(
    @PK _mk_system: String,
    @PK _mk_instance: String,
    @PK werks: String,
    name1: String,
    bwkey: String,
    land1: String,
    kunnr: String,
    lifnr: String,
    country: String,
    iso_code: String,
    eco_regions: String,
    subregion: String,
    @Decimal(19, 17) latitude_geo_center: java.math.BigDecimal,
    @Decimal(21, 18) longitude_geo_center: java.math.BigDecimal,
    member_of_eu: java.lang.Long = null
) extends Entity

object t001w extends TableSpec[DmT001W] with Updated.ByOneTransaction {

  override def sourceTableSpecs: Seq[TableSpec[Entity]] =
    Seq(
      ct_gbl_e32.t001w,
      ct_gbl_epp.t001w,
      ct_gbl_ghp.t001w,
      ct_gbl_p12.t001w,
      ct_gbl_p24.t001w,
      ct_gbl_p43.t001w,
      ct_gbl_p61.t001w,
      ct_gbl_p64.t001w,
      ct_gbl_p69.t001w,
      ct_gbl_p73.t001w,
      ct_gbl_p77.t001w,
      ct_gbl_p85.t001w,
      ct_gbl_pbr.t001w,
      ct_gbl_psp.t001w,
      customs_regions_raw
    )

  private val sapTableSpecs: Seq[TableSpec[Entity]] = sourceTableSpecs.filterNot(_ == customs_regions_raw)

  override def executeTransaction(
      table: Table,
      changeFeeds: Map[TableSpec[Entity], ChangeFeed]
  ): Result = {

    val feeds = sourceTableSpecs.map(s => s -> changeFeeds(s))

    if (feeds.forall { case (_, f) => f.isUnchanged }) return Result.NoChanges

    // SAP source columns consumed before the region join. Scoped locally because t001w columns differ from
    // the final DmT001W schema (the join appends the country/geo enrichment columns).
    val consumedValueColumnNames = Seq("_mk_system", "_mk_instance", "werks", "name1", "bwkey", "land1", "kunnr", "lifnr")

    val t001wUnion = sapTableSpecs
      .map(ts => changeFeeds(ts).snapshot().select(consumedValueColumnNames.map(col): _*))
      .reduce(_.unionByName(_))
      .filter(col("werks").isNotNull)
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

    val combined = t001wUnion
      .as("t")
      .join(regions.as("r"), col("t.land1") === col("r.alpha_2_string"), "left")
      .select(
        col("t._mk_system"),
        col("t._mk_instance"),
        col("t.werks"),
        col("t.name1"),
        col("t.bwkey"),
        col("t.land1"),
        col("t.kunnr"),
        col("t.lifnr"),
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

    val canonicalKeys = ct_gbl_e32.t001w.keyColumnNames.toSet

    sapTableSpecs.foreach { spec =>
      require(
        spec.keyColumnNames.toSet == canonicalKeys,
        s"Source table '$spec' has key columns ${spec.keyColumnNames} but expected ${ct_gbl_e32.t001w.keyColumnNames}"
      )
    }
  }
}

// COLUMN ACCESSOR AUTO GENERATED:START
// Generated by ColumnWithNameAccessorEmbeddedAstBuilder - DO NOT EDIT MANUALLY
import ct.dna.lakehouse.core.model.ColumnWithName
import ct.dna.lakehouse.core.model.ColumnWithNameAccessor

sealed class C_t001w(prefix: String) extends ColumnWithNameAccessor {
  val _mk_system: ColumnWithName = ColumnWithName(prefix, "_mk_system")
  val _mk_instance: ColumnWithName = ColumnWithName(prefix, "_mk_instance")
  val werks: ColumnWithName = ColumnWithName(prefix, "werks")
  val name1: ColumnWithName = ColumnWithName(prefix, "name1")
  val bwkey: ColumnWithName = ColumnWithName(prefix, "bwkey")
  val land1: ColumnWithName = ColumnWithName(prefix, "land1")
  val kunnr: ColumnWithName = ColumnWithName(prefix, "kunnr")
  val lifnr: ColumnWithName = ColumnWithName(prefix, "lifnr")
  val country: ColumnWithName = ColumnWithName(prefix, "country")
  val iso_code: ColumnWithName = ColumnWithName(prefix, "iso_code")
  val eco_regions: ColumnWithName = ColumnWithName(prefix, "eco_regions")
  val subregion: ColumnWithName = ColumnWithName(prefix, "subregion")
  val latitude_geo_center: ColumnWithName = ColumnWithName(prefix, "latitude_geo_center")
  val longitude_geo_center: ColumnWithName = ColumnWithName(prefix, "longitude_geo_center")
  val member_of_eu: ColumnWithName = ColumnWithName(prefix, "member_of_eu")
}

object C_t001w extends C_t001w("") {
  def withDFAlias(alias: String): C_t001w = new C_t001w(alias)
  def withoutDFAlias: C_t001w = this

  @deprecated("Use withDFAlias instead.", "")
  def as(alias: String): C_t001w = withDFAlias(alias)
}
// COLUMN ACCESSOR AUTO GENERATED:END
