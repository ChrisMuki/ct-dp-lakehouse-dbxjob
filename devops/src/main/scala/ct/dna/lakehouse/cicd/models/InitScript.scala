package ct.dna.lakehouse.cicd.models

import com.fasterxml.jackson.annotation.JsonIgnore

/** Cluster init script that (1) overwrites Databricks' driver `log4j2.xml` with the runtime config we shipped to the volume, and (2) writes the FAIR scheduler
  * allocation file to the driver's **local** filesystem. Mirrors the approach used in `dp-pipeline-dbxjob/.../models/InitScript.scala` but without the
  * hash-vs-reference guard — kept intentionally minimal.
  *
  * After the log4j swap, `logger.info`/`logger.error` calls from `ct.dna.*` packages appear in the Databricks task **Output** tab (via the `console.*`
  * appenders defined in [[runtime-log4j2.xml]]).
  *
  * The FAIR allocation file ([[InitScript.fairSchedulerXml]]) is written to [[InitScript.FairSchedulerLocalDest]] here, before the SparkContext starts. The job
  * references it via `spark.scheduler.allocation.file=file:<local path>` (injected by `AssetDirectory`). The explicit `file:` scheme and local path are both
  * required: Spark resolves the allocation file through the Hadoop `FileSystem` during SparkContext init, so a bare path routes through DBFS
  * (`DbfsDisabledException` when the public DBFS root is disabled) and a `/Volumes/...` path needs a Unity Catalog token that does not yet exist at that point
  * (`No Unity API token found in Unity Scope`). A local `file:` path bypasses both.
  *
  * @param sourceLog4j2PathInVolume
  *   Volume path of the log4j2.xml that should become the driver's active config.
  */
final case class InitScript(sourceLog4j2PathInVolume: String) extends AsFile {

  @JsonIgnore val fileName: String = "init_script.sh"

  def content: Array[Byte] = {
    val template = """#!/bin/bash
set -euo pipefail

SRC="__SRC__"
DEST="/home/ubuntu/databricks/spark/dbconf/log4j/driver/log4j2.xml"

if [[ ! -f "${SRC}" ]]; then
  echo "[ERROR] Source log4j2 not found: ${SRC}" >&2
  exit 1
fi

if [[ ! -f "${DEST}" ]]; then
  echo "[ERROR] Destination log4j2 not found: ${DEST}" >&2
  exit 1
fi

cp "${SRC}" "${DEST}"
chmod 644 "${DEST}"

echo "[INFO] Driver log4j2.xml replaced from ${SRC}"

# FAIR scheduler allocation file for the lakehouse job's layered pools (lakehouse-0 .. lakehouse-6).
# Written to a driver-LOCAL path before the SparkContext starts so `spark.scheduler.allocation.file=file:__ALLOC__`
# can load it without routing through DBFS or Unity Catalog (neither is usable for this file at SparkContext init).
ALLOC="__ALLOC__"
mkdir -p "$(dirname "${ALLOC}")"
cat > "${ALLOC}" <<'FAIRSCHEDULER_EOF'
__FAIRSCHEDULER_XML__
FAIRSCHEDULER_EOF
chmod 644 "${ALLOC}"

echo "[INFO] FAIR scheduler allocation file written to ${ALLOC}"
"""
    template
      .replace("__SRC__", sourceLog4j2PathInVolume)
      .replace("__ALLOC__", InitScript.FairSchedulerLocalDest)
      .replace("__FAIRSCHEDULER_XML__", InitScript.fairSchedulerXml)
      .getBytes("UTF-8")
  }
}

object InitScript {

  /** Driver-local path the init script writes the FAIR scheduler allocation file to. Referenced by the cluster `sparkConf` as
    * `spark.scheduler.allocation.file=file:<this path>` (see `AssetDirectory.buildJobCluster`). Must be a local path (not DBFS, not a UC volume) because Spark
    * reads it through the Hadoop `FileSystem` during SparkContext init, before Unity Catalog credentials exist.
    */
  val FairSchedulerLocalDest: String = "/tmp/fairscheduler.xml"

  /** Highest layer index that maps to a distinct pool (`lakehouse-0` .. `lakehouse-MaxLayer`). Must match `PoolStrategy.Layered.MaxLayer` in the lakehouse
    * runtime, which clamps deeper layers to this pool.
    */
  val MaxLayer: Int = 6

  /** FAIR allocation XML defining `lakehouse-0` .. `lakehouse-MaxLayer`, each in FAIR mode with `minShare = 0` and an exponentially scaling weight (10^i), so
    * later (heavier) processing stages win cores over earlier stages when pools contend.
    */
  val fairSchedulerXml: String = {
    val pools = (0 to MaxLayer)
      .map { i =>
        val weight = BigInt(10).pow(i)
        s"""  <pool name="lakehouse-$i">
           |    <schedulingMode>FAIR</schedulingMode>
           |    <weight>$weight</weight>
           |    <minShare>0</minShare>
           |  </pool>""".stripMargin
      }
      .mkString("\n")
    s"""<?xml version="1.0"?>
       |<allocations>
       |$pools
       |</allocations>""".stripMargin
  }
}
