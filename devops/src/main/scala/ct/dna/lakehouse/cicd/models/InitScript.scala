package ct.dna.lakehouse.cicd.models

import com.fasterxml.jackson.annotation.JsonIgnore

/** Cluster init script that overwrites Databricks' driver `log4j2.xml` with the runtime config we shipped to the volume. Mirrors the approach used in
  * `dp-pipeline-dbxjob/.../models/InitScript.scala` but without the hash-vs-reference guard — kept intentionally minimal.
  *
  * After the swap, `logger.info`/`logger.error` calls from `ct.dna.*` packages appear in the Databricks task **Output** tab (via the `console.*` appenders
  * defined in [[runtime-log4j2.xml]]).
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
"""
    template.replace("__SRC__", sourceLog4j2PathInVolume).getBytes("UTF-8")
  }
}
