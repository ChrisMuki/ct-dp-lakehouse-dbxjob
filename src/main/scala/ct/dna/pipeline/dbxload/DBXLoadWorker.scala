package ct.dna.pipeline.dbxload
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import ct.dna.pipeline.meta.MetaTable
import ct.dna.pipeline.workorder.DequeueCountReached
import ct.dna.pipeline.workorder.WorkOrder
import ct.dna.pipeline.workorder.WorkOrder.DBXLoadOrder
import ct.dna.pipeline.workorder.WorkOrderInvalid
import ct.dna.pipeline.workorder.WorkOrderQueue
import ct.dna.pipeline.workorderresult
import ct.dna.pipeline.workorderresult.MonitoringQueue
import ct.dna.utils.Union
import ct.dna.utils.az.queue.MessageItem
import ct.dna.worker.Worker
import ct.dna.worker.WorkerConfig.DBXLoadConfig
import ct.dna.worker.WorkerRuntime

final class DBXLoadWorker(val runtime: WorkerRuntime, val config: DBXLoadConfig) extends Worker[DBXLoadConfig] {

  def cleanUpDuringShutdown: Unit = Try { currentMessage.giveMessageBack }
  var currentMessage: MessageItem = _

  var initialized = false
  var sparkSession: Any = _

  val counter = new java.util.concurrent.atomic.AtomicLong(0)
  val monitoringQueue = new MonitoringQueue(config.monitoringQueue)
  val metaTable = new MetaTable(config.metaTable, 1000)
  val dbxloadQueue = new WorkOrderQueue(config.dbxloadQueue)

  def execute(): Unit = {
    if (Cache.checkRefresh) {
      logger.debug("Refresh clients")
      monitoringQueue.refreshTokenCredential(Cache.getToken)
      metaTable.refreshTokenCredential(Cache.getToken)
      dbxloadQueue.refreshTokenCredential(Cache.getToken)
    }
    if (!initialized) {
      // init UDF to throw error
      initialized = true
    }
    logger.debug("Prepare Local Fields")
    counter.set(0)

    var has_workOrders = true
    logger.info("Start processing dbxloadQueue")
    while (has_workOrders && runtime.continueWorkerExecution) {
      has_workOrders = false
      for ((mi, woTry) <- dbxloadQueue.receiveWorkOrders(1, config.duration_s) if runtime.continueWorkerExecution) {
        has_workOrders = true
        currentMessage = mi
        woTry match {
          case Failure(exception) => {
            logger.error(exception)
            logger.error("Could not processed message with Text: " + mi.getMessageText)
            mi.deleteMessage
          }
          case Success(value) =>
            Try(processWorkOrder(mi, value)) match {
              case Failure(exception) => {
                logger.error(exception)
                logger.error("Could not processed WorkOrder:" + value)
              }
              case Success(true) => {
                val c = counter.incrementAndGet()
                if (c % 10 == 0) logger.info("Processed already " + c + " WorkOrders")
              }
              case Success(_) =>
            }
        }
      }
    }
    logger.info("Processed " + counter.get() + " WorkOrders")

    def processWorkOrder(messageItem: MessageItem, workOrder: WorkOrder): Boolean = {
      logger.debug("Processing: " + workOrder)

      val dequeueCount = messageItem.qmItem.getDequeueCount()
      if (dequeueCount > config.maxDequeueCount) {
        monitoringQueue.sendWorkOrderResult(workorderresult.buildResults(workOrder, DequeueCountReached(dequeueCount, config.maxDequeueCount)))
        messageItem.deleteMessage
        return false
      }

      val dbxloadOrder = workOrder match {
        case po: DBXLoadOrder => po
        case x => {
          monitoringQueue.sendWorkOrderResult(workorderresult.buildResults(workOrder, WorkOrderInvalid("DBXLoadWorker must be called only for DBXLoadOrder")))
          messageItem.deleteMessage
          return false
        }
      }

      if (dequeueCount > 1) messageItem.extendTTLbasedOnDequeueCount(config.duration_s)

      Processor_Parquet.processParquets(dbxloadOrder, Cache.getToken, dequeueCount) match {
        case Union.C1(tablesupdated) => {
          val result = workorderresult.buildResults(dbxloadOrder, tablesupdated)
          metaTable.getSubscriber(dbxloadOrder.messageKey.messagePath.messageInstance).dbx.foreach(Cache.getSubscriberQueue(_).sendWorkOrderResult(result))
          monitoringQueue.sendWorkOrderResult(result)
          messageItem.deleteMessage
          true
        }
        case Union.C2(tablesupdatefailed) => false // This will be retried
        case Union.C3(invalid) => {
          logger.error("Found Invalid MessageContent for dbxloadOrder: " + dbxloadOrder + " : " + invalid.reason)
          monitoringQueue.sendWorkOrderResult(workorderresult.buildResults(dbxloadOrder, invalid))
          messageItem.deleteMessage
          false
        }
      }
    }

  }
}
