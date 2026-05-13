package ct.dna.lakehouse.core.jobs.orchestrator

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import scala.jdk.CollectionConverters._
import scala.util.Try

import ct.dna.utils.json.{mapper => jsonMapper}
import ct.dna.utils.logging.LoggingTrait

/** Live status payloads written to a Unity Catalog volume directory by JobSetup and Workers, and read by the Monitor. UC volumes are POSIX-mounted on
  * Databricks cluster nodes so plain `java.nio.file` is enough \u2014 no Spark required.
  *
  * Files (all under `<heartbeatDir>/<runId>/`):
  *
  *   - `run.json` \u2014 written once by JobSetup after enqueue. Total table count + start time.
  *   - `worker_<id>.json` \u2014 overwritten by Worker_<id> on every START/END and on poll-loop exit. State + currentTable + per-worker counters.
  *
  * Writes are atomic (write-temp + `ATOMIC_MOVE`) so the Monitor never reads a half-serialised file. Read failures are logged at WARN and treated as "worker
  * not yet started" / "stale file" rather than fatal.
  */
private[orchestrator] object HeartbeatStore extends LoggingTrait {

  /** Per-run summary written once by JobSetup. */
  final case class RunInfo(
      runId: String,
      catalog: String,
      totalTables: Int,
      startedAtMs: Long
  )

  /** Per-worker live status overwritten on every table boundary. `state` is one of "idle" | "running" | "done". `currentTable` is `Some` only while `state ==
    * "running"`.
    *
    * `Option` is used deliberately: the shared Jackson mapper has `FAIL_ON_NULL_CREATOR_PROPERTIES` enabled and `NON_ABSENT` inclusion, which combine into the
    * trap that `null` fields are *omitted* on write and then fail deserialisation when read back. With `Option` + the Scala module, `None` round-trips cleanly
    * (omitted on write, defaulted to `None` on read).
    */
  final case class WorkerStatus(
      workerName: String,
      state: String,
      currentTable: Option[String],
      currentStartedAtMs: Option[Long],
      completed: Int,
      failed: Int,
      skipped: Int,
      lastUpdateAtMs: Long
  )

  val State_Idle: String = "idle"
  val State_Running: String = "running"
  val State_Done: String = "done"

  /** Resolve `<heartbeatDir>/<runId>` and ensure the directory exists. Returns `None` when `heartbeatDir` is unset \u2014 caller treats as "live monitor
    * disabled".
    */
  def runDir(cfg: OrchestratorConfig, runId: String): Option[Path] =
    cfg.heartbeatDir.map { base =>
      val p = Paths.get(base, runId)
      Try(Files.createDirectories(p)).recover { case t: Throwable =>
        logger.warn(s"Could not create heartbeat dir $p: ${t.getClass.getSimpleName}: ${t.getMessage}")
        p
      }.get
    }

  /** Write `run.json`. Best-effort \u2014 logs and swallows any I/O error. */
  def writeRunInfo(cfg: OrchestratorConfig, info: RunInfo): Unit =
    runDir(cfg, info.runId).foreach { dir => writeAtomic(dir.resolve("run.json"), info) }

  /** Write `worker_<id>.json`. Best-effort \u2014 logs and swallows any I/O error. */
  def writeWorkerStatus(cfg: OrchestratorConfig, runId: String, status: WorkerStatus): Unit =
    runDir(cfg, runId).foreach { dir => writeAtomic(dir.resolve(s"worker_${status.workerName}.json"), status) }

  /** Read `run.json` if present. Returns `None` when missing or unparseable. */
  def readRunInfo(cfg: OrchestratorConfig, runId: String): Option[RunInfo] =
    runDir(cfg, runId).flatMap { dir => readJson[RunInfo](dir.resolve("run.json")) }

  /** Read every `worker_*.json` in the run directory. Empty when no workers have started yet or `heartbeatDir` is unset. */
  def readAllWorkerStatuses(cfg: OrchestratorConfig, runId: String): Seq[WorkerStatus] =
    runDir(cfg, runId).toSeq.flatMap { dir =>
      Try {
        val stream = Files.list(dir)
        try {
          stream
            .iterator()
            .asScala
            .filter(p => p.getFileName.toString.startsWith("worker_") && p.getFileName.toString.endsWith(".json"))
            .flatMap(readJson[WorkerStatus])
            .toList
        } finally stream.close()
      }.recover { case t: Throwable =>
        logger.warn(s"Could not list heartbeat dir $dir: ${t.getClass.getSimpleName}: ${t.getMessage}")
        List.empty[WorkerStatus]
      }.get
    }

  private def writeAtomic[A <: AnyRef](target: Path, value: A): Unit = {
    val bytes = jsonMapper.writeValueAsBytes(value)
    val tmp = target.resolveSibling(s"${target.getFileName}.tmp")
    Try {
      Files.write(tmp, bytes)
      // ATOMIC_MOVE may not be supported on all UC volume mounts; fall back to a plain replace move on failure.
      Try(Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)).recover { case _: Throwable =>
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING)
      }.get
      ()
    }.recover { case t: Throwable =>
      logger.warn(s"Heartbeat write to $target failed: ${t.getClass.getSimpleName}: ${t.getMessage}")
      Try(Files.deleteIfExists(tmp))
      ()
    }
    ()
  }

  private def readJson[A](path: Path)(implicit m: scala.reflect.Manifest[A]): Option[A] =
    if (!Files.exists(path)) None
    else {
      Try(jsonMapper.readValue[A](new String(Files.readAllBytes(path), StandardCharsets.UTF_8))) match {
        case scala.util.Success(v) => Some(v)
        case scala.util.Failure(t) =>
          // A parse failure here is almost always one of:
          //   - half-written file caught between write-temp and atomic-move (self-heals on next poll),
          //   - schema drift between writer and reader JVMs (older Monitor against newer Worker payload).
          // Either way, surface it so a silent "no workers reporting yet" loop is debuggable.
          logger.warn(s"Heartbeat read of $path failed: ${t.getClass.getSimpleName}: ${t.getMessage}")
          None
      }
    }
}
