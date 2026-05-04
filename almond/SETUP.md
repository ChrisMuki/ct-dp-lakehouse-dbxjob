# Almond / Jupyter Scala-Kernel Setup

Ziel: Aus einem sbt-Subprojekt heraus Jupyter-Notebooks mit dem
[Almond](https://almond.sh) Scala-Kernel benutzen. Der Kernel wird
vollständig über **coursier** (`cs` / `coursier`) gemanaged — Python,
`pip` oder `jupyter` werden **nicht** gebraucht. sbt liefert dem Kernel
nur den Projekt-Classpath.

---

## 1. Was im Build-Plugin landen soll (`DnaBuildPlugin`)

Aktuell steht alles inline in `build.sbt`. Damit andere Repos das
1-zeilig benutzen können, packen wir es ins Plugin.

### 1.1 Neuer Helfer in `DnaBuildPlugin.autoImport`

```scala
/** Settings für ein Almond-Notebook-Subprojekt:
  *  - definiert die Task `writeClasspath`, die den Compile-Classpath
  *    (eigene `classDirectory` + `dependencyClasspath`) in
  *    `<project>/target/classpath.txt` schreibt.
  *  - hängt sie an `Compile / compile`, damit der Kernel nach jedem
  *    Compile den aktuellen Classpath sieht.
  *
  * Wichtig: Wir bauen den Classpath bewusst aus `dependencyClasspath`
  * + `classDirectory` (statt `fullClasspath` / `exportedProducts`),
  * sonst entsteht ein Task-Graph-Cycle, sobald die Task an
  * `Compile / compile` gehängt wird.
  */
val almondWriteClasspath =
  taskKey[File]("Write Almond runtime classpath to <project>/target/classpath.txt")

def almondNotebookProject(): Seq[Setting[_]] = Seq(
  run / fork := true,
  assembly / skip := true,
  publish / skip := true,
  almondWriteClasspath := {
    val deps    = (Compile / dependencyClasspath).value.map(_.data.getAbsolutePath)
    val ownDir  = (Compile / classDirectory).value.getAbsolutePath
    val entries = ownDir +: deps
    val cp      = entries.mkString(java.io.File.pathSeparator)
    val out     = target.value / "classpath.txt"
    IO.write(out, cp)
    streams.value.log.info(
      s"Wrote Almond classpath (${entries.size} entries) to $out"
    )
    out
  },
  Compile / compile := {
    val analysis = (Compile / compile).value
    almondWriteClasspath.value
    analysis
  }
)
```

> Den Key `almondWriteClasspath` in `autoImport` exportieren, dann ist er
> in `build.sbt` ohne Import sichtbar.

### 1.2 Aufruf in `build.sbt`

```scala
lazy val almond = project
  .in(file("almond"))
  // .dependsOn(...)  // wenn Notebooks Code aus anderen Subprojekten brauchen
  .settings(
    name := "almond",
    almondNotebookProject(),
    useDnaBom(dnaBomVersion)(
      "common-utils",
      "dataplatform-core",
      "lakehouse-core",
      "local-spark-runtime"
    )
  )
```

---

## 2. Almond-Kernel im Devcontainer-Image installieren

### 2.1 Ergänzung im JVM-Setup-Skript

Direkt **nach** dem `cs setup --jvm "$JVM" --yes` und **vor** dem
`cs cache --clear` einfügen:

```bash
# -----------------------------------------------------------------------------
# Almond (Jupyter Scala-Kernel) installieren
# -----------------------------------------------------------------------------
# Wir benutzen den JVM-basierten Launcher `coursier` (nicht den nativen `cs`),
# weil der die JAVA_TOOL_OPTIONS / Proxy-Settings respektiert.
ALMOND_VERSION="${ALMOND_VERSION:-0.14.5}"
SCALA_VERSION_FOR_ALMOND="${SCALA_VERSION_FOR_ALMOND:-2.13.16}"
KERNEL_ID="${KERNEL_ID:-scala-sbt}"
KERNEL_DISPLAY_NAME="${KERNEL_DISPLAY_NAME:-Scala (sbt almond)}"

echo "===> Installing Almond $ALMOND_VERSION (Scala $SCALA_VERSION_FOR_ALMOND) ..."
coursier launch \
  --scala-version "$SCALA_VERSION_FOR_ALMOND" \
  "almond:$ALMOND_VERSION" -- \
  --install --force \
  --id "$KERNEL_ID" \
  --display-name "$KERNEL_DISPLAY_NAME"
echo "===> Almond installed -> $HOME/.local/share/jupyter/kernels/$KERNEL_ID"
```

> Anmerkung: Der Befehl legt `kernel.json` und `launcher.jar` unter
> `$HOME/.local/share/jupyter/kernels/<KERNEL_ID>/` an. Bei `--force`
> wird ein evtl. existierender Kernel mit gleicher ID überschrieben.
> Dieser Schritt **muss als Build-User** laufen, der später das Image
> benutzt — sonst landet `kernel.json` im falschen Home.

### 2.2 Was im Image **nicht** gebraucht wird

- **kein** `python`, `pip`, `jupyter`, `jupyterlab`
- **kein** weiterer Resolver/Mirror — die Kernel-JARs zieht `coursier`
  direkt von Maven Central.

### 2.3 VS Code Extension (devcontainer.json)

In `.devcontainer/devcontainer.json` unter `customizations.vscode.extensions`
ergänzen:

```jsonc
"ms-toolsai.jupyter"
```

Die Jupyter-Extension liest die Kernel-Specs unter
`~/.local/share/jupyter/kernels/` automatisch ein.

Konkretes Diff für die `extensions`-Liste in unserer
`.devcontainer/devcontainer.json`:

```diff
       "extensions": [
         "ms-azuretools.vscode-docker",
         "redhat.vscode-yaml",
         "ms-vscode.azurecli",
         "scalameta.metals@1.59.0",
         "mhutchie.git-graph",
-        "opentofu.vscode-opentofu"
+        "opentofu.vscode-opentofu",
+        "ms-toolsai.jupyter"
       ]
```

> `ms-toolsai.jupyter` zieht die zugehörigen Bundle-Extensions
> (`jupyter-keymap`, `jupyter-renderers`, `vscode-jupyter-cell-tags`,
> `vscode-jupyter-slideshow`) automatisch nach — nicht extra eintragen.
>
> `ms-python.python` wird für reine Scala-Notebooks **nicht** benötigt;
> der Almond-Kernel kommt ohne Python aus.

---

## 3. Was im Repo bleiben muss (pro Repo, **nicht** im Image)

### 3.1 Kernel-Wrapper `almond/scripts/launch-kernel.sh`

```bash
#!/usr/bin/env bash
# Forwards to the Almond kernel installed via
#   coursier launch almond -- --install ...
# but injects the sbt-managed project classpath via --extra-class-path.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CP_FILE="$REPO_ROOT/almond/target/classpath.txt"
LAUNCHER_JAR="$HOME/.local/share/jupyter/kernels/scala-sbt/launcher.jar"

if [[ ! -s "$CP_FILE" ]]; then
  echo "[almond-launcher] $CP_FILE missing. Running 'sbt almond/compile' ..." >&2
  (cd "$REPO_ROOT" && sbt -batch -no-colors "almond/compile" 1>&2)
fi

PROJECT_CP="$(cat "$CP_FILE")"

exec java \
  -cp "$LAUNCHER_JAR" \
  coursier.bootstrap.launcher.Launcher \
  --id scala-sbt \
  --display-name "Scala (sbt almond)" \
  --extra-class-path "$PROJECT_CP" \
  --connection-file "$@"
```

Ausführbar machen:
```bash
chmod +x almond/scripts/launch-kernel.sh
```

### 3.2 `kernel.json` auf den Wrapper umbiegen (einmalig pro Workspace)

Die `kernel.json`, die `coursier launch almond -- --install ...` im Image
angelegt hat, ruft `java ... coursier.bootstrap.launcher.Launcher ...`
direkt auf — **ohne** unseren sbt-Classpath. Wir patchen sie so, dass
sie unseren Wrapper ausführt:

```bash
cat > "$HOME/.local/share/jupyter/kernels/scala-sbt/kernel.json" <<JSON
{
  "argv": [
    "${PWD}/almond/scripts/launch-kernel.sh",
    "{connection_file}"
  ],
  "display_name": "Scala (sbt almond)",
  "language": "scala"
}
JSON
```

> Den absoluten Repo-Pfad bewusst hartcodiert lassen — `kernel.json`
> wird von Jupyter aus jedem Working-Dir mit denselben Argumenten
> aufgerufen. Wer mehrere Repos parallel nutzt, registriert pro Repo
> eine eigene Kernel-ID (z. B. `scala-sbt-<reponame>`).

Idealerweise als `postCreateCommand` / Repo-Setup-Skript laufen lassen.

### 3.3 Erstmaliger Compile

```bash
sbt almond/compile
```

…erzeugt `almond/target/classpath.txt`. Danach das Notebook
`almond/notebooks/hello.ipynb` öffnen und oben rechts den Kernel
**„Scala (sbt almond)"** wählen.

---

## 4. Workflow für die Nutzer:innen

1. Notebook unter `almond/notebooks/...` öffnen.
2. Kernel **„Scala (sbt almond)"** wählen.
3. Sobald Scala-Code in `almond/src/main/scala/...` geändert wird:
   `sbt almond/compile` laufen lassen → `classpath.txt` wird automatisch
   aktualisiert → **Restart Kernel** in Jupyter.
4. Soll Code aus anderen Subprojekten verwendet werden:
   `dependsOn(<projekt>)` zum `almond`-Project in `build.sbt` ergänzen
   (das Subprojekt muss kompilieren).

---

## 5. Checkliste

- [ ] `almondWriteClasspath` + `almondNotebookProject()` in `DnaBuildPlugin` eingebaut & released
- [ ] `build.sbt` benutzt `almondNotebookProject()`
- [ ] Devcontainer-Setup-Skript: `coursier launch almond -- --install ...` Block ergänzt
- [ ] `ms-toolsai.jupyter` in `devcontainer.json` ergänzt
- [ ] `almond/scripts/launch-kernel.sh` im Repo, `chmod +x`
- [ ] `~/.local/share/jupyter/kernels/scala-sbt/kernel.json` zeigt auf den Wrapper
- [ ] `sbt almond/compile` lief mind. einmal → `almond/target/classpath.txt` existiert
