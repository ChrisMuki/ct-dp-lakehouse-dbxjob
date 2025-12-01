package ct.dna.lakehouse.catalogs.dw_tx.iamer_norz
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import ct.dna.lakehouse.catalogs.sr
import ct.dna.lakehouse.dataframeprovider.ChangeFeedTable
import ct.dna.lakehouse.dataframeprovider.TargetTable
import ct.dna.utils.spark.entity.Entity
import ct.dna.utils.spark.entity.pk
import ct.dna.lakehouse.metastore.Origin
import ct.dna.lakehouse.metastore.TableDef
import ct.dna.utils.spark.entity.Struct
import org.apache.spark.sql.SQLImplicits
import org.apache.spark.sql.functions.col

case class Order(
    @pk _mk_org_String: String,
    @pk _mk_site_String: String,
    @pk _mk_system_String: String,
    @pk _mk_instance_String: String,
    @pk _mk_partition_String: String,
    @pk _mk_file_String: String,
    _mk_container_String: String,
    _mk_account_String: String,
    _mk_createdAt_String: String,
    @pk _lh_id_in_message_Long: Long,
    //
    plant_String: String,
    //
    compound_String: String,
    x_date_utc_String: String,
    date_ordered_utc_String: String,
    order_id_String: String,
    order_name_String: String,
    date_produced_utc_String: String,
    number_of_batches_Long: Long,
    specification_id_String: String,
//

    unix_run_num_String: String,
    spec_name_String: String,
    mixer_num_Long: Long,
    mes_spec_num_Long: Long,
    run_start_time_utc_String: String,
    initial_unix_run_num_String: String,
    run_start_time_String: String
) extends Entity

object Orders extends TableDef[Order] with Origin.OneTransaction[Order] {

  override def changeFeeds: Seq[TableDef[Entity]] = Seq(sr.iamer_norz_enterprise.enterprise_orders, sr.iamer_norz_masda.masda_orders)
  override def executeTransaction(implicits: SQLImplicits, target: TargetTable, changeFeeds: Map[TableDef[Entity], ChangeFeedTable]): Boolean = {
    import implicits._
    val source = changeFeeds(sr.iamer_norz_enterprise.enterprise_orders)
      .getChangeFeed_last()
      .unionByName(
        changeFeeds(sr.iamer_norz_masda.masda_orders).getChangeFeed_last(),
        true
      )
    val tc = Struct.Of[Order].TargetColNames
    val sc = Struct.Of[Order].SourceColNames

    Try(target.merge(source, col(tc._mk_org_String) === col(sc._mk_org_String), "XXXXX").execute()) match {
      case Success(_) => true
      case Failure(_) => false
    }

    //  @pk _mk_org_String: String,
    // @pk _mk_site_String: String,
    // @pk _mk_system_String: String,
    // @pk _mk_instance_String: String,
    // @pk _mk_partition_String: String,
    // @pk _mk_file_String: String,
    // _mk_container_String: String,
    // _mk_account_String: String,
    // _mk_createdAt_String: String,
    // @pk _lh_id_in_message_Long: Long,)
    // ???
  }

}
