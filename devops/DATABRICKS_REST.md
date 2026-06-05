# Databricks REST API — Agent-Quickstart

Kurzanleitung für KI-Agenten (und Menschen), um per REST an den Spark-/Cluster-Status
eines laufenden Jobs zu kommen. Auth läuft über ein **Azure-AD-Token** (kein PAT nötig).

## 0. Auth prüfen (IMMER zuerst)

```bash
az account show --query "{user:user.name, tenant:tenantId}" -o json
```

- Erfolg → weiter zu Schritt 1.
- Fehler `Please run 'az login'` → einloggen:

  ```bash
  az login --use-device-code
  ```

  > Der Login muss **im selben Terminal/Container** passieren wie die späteren
  > REST-Calls. Ein `az login` in einer anderen Shell zählt hier nicht.

## 1. AAD-Token für Databricks holen

Die `--resource`-GUID ist die feste Azure-Databricks-Programmatic-App-ID
(`2ff814a6-3304-4ab8-85cb-cd0e6f879c1d`) — für **alle** Workspaces gleich.

```bash
TOKEN=$(az account get-access-token \
  --resource 2ff814a6-3304-4ab8-85cb-cd0e6f879c1d \
  --query accessToken -o tsv)
[ -n "$TOKEN" ] && echo "TOKEN_OK len=${#TOKEN}" || echo "TOKEN_EMPTY -> az login fehlt"
```

## 2. Host + Cluster-ID setzen

Hosts pro Stage (siehe `devops/src/main/scala/ct/dna/lakehouse/deploy/Config.scala`):

| Stage | Host |
|---|---|
| dev  | `https://adb-7405616666691350.10.azuredatabricks.net` |
| qual | `https://adb-7405617504226685.5.azuredatabricks.net`  |
| prod | `https://adb-7405608343332957.17.azuredatabricks.net`  |

```bash
DBX_HOST="https://adb-7405608343332957.17.azuredatabricks.net"   # prod
CLUSTER_ID="0605-093132-s0josef4"                                # aus der Cluster-/Spark-UI-URL
```

> Die `CLUSTER_ID` steht in der Spark-UI-URL: `.../compute/clusters/<CLUSTER_ID>/spark-ui`.

## 3. Nützliche Calls

### Cluster-Status / Größe

```bash
curl -sS -H "Authorization: Bearer $TOKEN" \
  "$DBX_HOST/api/2.1/clusters/get?cluster_id=$CLUSTER_ID" \
| python3 -c "import sys,json; d=json.load(sys.stdin); print(json.dumps({k:d.get(k) for k in ['state','state_message','num_workers','autoscale','node_type_id','driver_node_type_id','spark_context_id']}, indent=2))"
```

### Spark-UI / REST-Proxy (Jobs, Stages, Executors, Auslastung)

Die Spark-eigene REST-API liegt hinter dem Driver-Proxy. Über den Workspace-Proxy:

```bash
# Liste der laufenden Spark-Apps am Driver
curl -sS -H "Authorization: Bearer $TOKEN" \
  "$DBX_HOST/driver-proxy-api/o/0/$CLUSTER_ID/40001/api/v1/applications"

# Executors (Cores, aktive Tasks, Speicher) -> Kern-Info für CPU-Auslastung
APP_ID="<aus applications oben>"
curl -sS -H "Authorization: Bearer $TOKEN" \
  "$DBX_HOST/driver-proxy-api/o/0/$CLUSTER_ID/40001/api/v1/applications/$APP_ID/executors" \
| python3 -c "import sys,json; e=json.load(sys.stdin); print(json.dumps([{k:x.get(k) for k in ['id','totalCores','activeTasks','completedTasks','maxTasks']} for x in e], indent=2))"

# Aktive Stages (Parallelität: numActiveTasks vs. Cores)
curl -sS -H "Authorization: Bearer $TOKEN" \
  "$DBX_HOST/driver-proxy-api/o/0/$CLUSTER_ID/40001/api/v1/applications/$APP_ID/stages?status=active"
```

> Der Port ist i. d. R. `40001` (Spark-UI hinter dem Databricks-Proxy). Falls leer,
> in der Spark-UI-URL nachsehen bzw. `4040`/`40001` durchprobieren.

## Häufige CPU-niedrig-Ursachen (Lakehouse-Job)

- **Wenige aktive Tasks vs. Cores** → zu grobe Partitionierung
  (`spark.sql.shuffle.partitions`, `maxPartitionBytes`, AQE-Coalesce).
- **DAG-Engpass**: nur ein Layer/Tabelle gleichzeitig lauffähig → Worker idlen
  (siehe `WorkerTask` / `DagQueue`).
- **FAIR-Preemption** killt Tasks (Pools `lakehouse-0..6`, Gewicht `10^i`) →
  `spark.databricks.preemption.enabled=false` (bereits in `Config.scala` gesetzt).
- **Autoscale** noch nicht hochskaliert / `num_workers` klein.
- **Treiber-seitige Arbeit** (Planung, Collect) → Executors idle.

## Sicherheit

- `TOKEN` ist kurzlebig; **nie** loggen/committen. `${#TOKEN}` (nur Länge) zum Prüfen.
- Diese Datei enthält keine Secrets — nur die öffentliche Programmatic-App-ID und Hosts.
