#!/usr/bin/env bash
#
# run-server.sh — build RealEstate for one target and launch a local Paper
# test server with the freshly-built jar already installed.
#
# Usage:
#   ./scripts/run-server.sh <target> [--no-launch] [-- <extra java args>]
#
# <target> is one of the build targets from .github/workflows/build.yml's
# matrix (the case statement below is kept in sync with it): 1.21.1, 1.21.4,
# 1.21.11, or 26.1.2 ("latest" is accepted as an alias for the newest/26.x
# target).
#
# For the chosen target this:
#   1. Downloads the matching Paper server jar from PaperMC's "Fill"
#      downloads API (https://fill.papermc.io) and caches it under
#      servers/<target>/ (skipped if already present there).
#   2. Builds the RealEstate plugin jar for that target, via the exact
#      `mvn package -D...` invocation build.yml uses for that target.
#   3. Copies the built jar into servers/<target>/plugins/, writes an
#      accepted eula.txt, seeds a minimal server.properties on first run
#      only (online-mode=false, so offline/cracked clients can join
#      without a Mojang login — convenient for local testing; edit it
#      freely afterwards, it's never overwritten again), and launches the
#      server in the foreground.
#
# Examples:
#   ./scripts/run-server.sh 1.21.1
#   ./scripts/run-server.sh 1.21.11
#   ./scripts/run-server.sh latest
#   ./scripts/run-server.sh 26.1.2 --no-launch      # build + stage only, don't start the server
#   ./scripts/run-server.sh 1.21.4 -- -Xms2G -Xmx4G  # pass extra JVM args through to java
#
# Requires on PATH: bash, curl, jq, mvn, and a JDK. The plugin's
# maven-compiler-plugin <release> is pinned to 25 for every target (see the
# comment on it in pom.xml), so the jar's class files require a Java 25+
# runtime to load regardless of which Minecraft target's server you're
# running — use a JDK 25+ `java` to launch, even for the older targets.
# Needs real, unrestricted internet access to fill.papermc.io and to the
# Maven repositories pom.xml declares (Spigot, PaperMC, JitPack, etc.) —
# this will not work from a network-restricted sandbox/CI environment.
#
# Server jars are cached under servers/<target>/ (gitignored) so re-runs
# skip the download. Delete that directory to force a re-download.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
USER_AGENT="RealEstate-dev-tooling/1.0 (https://github.com/EtienneDx/RealEstate)"

usage() {
  echo "Usage: $(basename "${BASH_SOURCE[0]}") <target> [--no-launch] [-- <extra java args>]"
  echo "  <target>: 1.21.1 | 1.21.4 | 1.21.11 | 26.1.2 (or 'latest')"
  echo "See the comment header at the top of this script for full details."
  exit "${1:-0}"
}

if [[ $# -lt 1 || "$1" == "-h" || "$1" == "--help" ]]; then
  usage
fi

RAW_TARGET="$1"
shift

NO_LAUNCH=0
EXTRA_JAVA_ARGS=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    --no-launch)
      NO_LAUNCH=1
      shift
      ;;
    --)
      shift
      EXTRA_JAVA_ARGS=("$@")
      break
      ;;
    *)
      echo "Unrecognized argument: $1" >&2
      usage 1
      ;;
  esac
done

# --- Target mapping -----------------------------------------------------
# Mirrors .github/workflows/build.yml's `strategy.matrix.include` exactly.
# If a target is added/changed there, update it here too.
case "$RAW_TARGET" in
  1.21.1)
    TARGET=1.21.1
    MC_VERSION=1.21.1
    SPIGOT_API_VERSION=1.21.1-R0.1-SNAPSHOT
    PAPER_API_VERSION=1.21.1-R0.1-SNAPSHOT
    SKIP_SPIGOT_API=false
    ;;
  1.21.4)
    TARGET=1.21.4
    MC_VERSION=1.21.4
    SPIGOT_API_VERSION=1.21.4-R0.1-SNAPSHOT
    PAPER_API_VERSION=1.21.4-R0.1-SNAPSHOT
    SKIP_SPIGOT_API=false
    ;;
  1.21.11)
    TARGET=1.21.11
    MC_VERSION=1.21.11
    SPIGOT_API_VERSION=1.21.11-R0.1-SNAPSHOT
    PAPER_API_VERSION=1.21.11-R0.1-SNAPSHOT
    SKIP_SPIGOT_API=false
    ;;
  26.1.2|latest)
    TARGET=26.1.2
    MC_VERSION=26.1.2
    SPIGOT_API_VERSION= # unused, SKIP_SPIGOT_API=true
    PAPER_API_VERSION='[26.1.2.build,)'
    SKIP_SPIGOT_API=true
    ;;
  *)
    echo "Unknown target: '$RAW_TARGET'" >&2
    echo "Valid targets: 1.21.1, 1.21.4, 1.21.11, 26.1.2 (or 'latest')" >&2
    exit 1
    ;;
esac

for tool in curl jq mvn java; do
  command -v "$tool" >/dev/null 2>&1 || { echo "Required tool not found on PATH: $tool" >&2; exit 1; }
done

JAVA_MAJOR="$(java -version 2>&1 | grep -m1 -oE '"[0-9]+' | tr -d '"' || echo 0)"
if [[ "$JAVA_MAJOR" -lt 25 ]]; then
  echo "Warning: 'java' on PATH reports major version $JAVA_MAJOR." >&2
  echo "  RealEstate's plugin jar is compiled for Java 25 (see pom.xml's" >&2
  echo "  maven-compiler-plugin <release>); the server will fail to load" >&2
  echo "  the plugin unless the JVM launching it is 25+." >&2
fi

SERVER_DIR="$REPO_ROOT/servers/$TARGET"
PLUGINS_DIR="$SERVER_DIR/plugins"
mkdir -p "$PLUGINS_DIR"

# --- 1. Download (and cache) the Paper server jar for this target -------
SERVER_JAR="$(find "$SERVER_DIR" -maxdepth 1 -name 'paper-*.jar' 2>/dev/null | head -n1 || true)"
if [[ -n "$SERVER_JAR" ]]; then
  echo "Using cached Paper server jar: $SERVER_JAR"
else
  echo "Looking up the latest stable Paper build for Minecraft $MC_VERSION via fill.papermc.io..."
  BUILDS_JSON="$(curl -fsSL -H "User-Agent: $USER_AGENT" \
    "https://fill.papermc.io/v3/projects/paper/versions/${MC_VERSION}/builds")"

  if echo "$BUILDS_JSON" | jq -e 'type == "object" and (.ok == false)' >/dev/null 2>&1; then
    echo "fill.papermc.io returned an error for Minecraft version $MC_VERSION:" >&2
    echo "  $(echo "$BUILDS_JSON" | jq -r '.message // "unknown error"')" >&2
    exit 1
  fi

  DL_NAME="$(echo "$BUILDS_JSON" | jq -r 'map(select(.channel == "STABLE")) | .[0].downloads."server:default".name // empty')"
  DL_URL="$(echo "$BUILDS_JSON" | jq -r 'map(select(.channel == "STABLE")) | .[0].downloads."server:default".url // empty')"

  if [[ -z "$DL_URL" || -z "$DL_NAME" ]]; then
    echo "No STABLE Paper build found for Minecraft $MC_VERSION on fill.papermc.io." >&2
    exit 1
  fi

  echo "Downloading $DL_NAME ..."
  curl -fSL -H "User-Agent: $USER_AGENT" -o "$SERVER_DIR/$DL_NAME" "$DL_URL"
  SERVER_JAR="$SERVER_DIR/$DL_NAME"
fi

# --- 2. Build the plugin jar for this target -----------------------------
echo "Building RealEstate for target $TARGET (mc=$MC_VERSION, skip.spigot.api=$SKIP_SPIGOT_API)..."
( cd "$REPO_ROOT" && mvn -B package --file pom.xml \
    "-Dminecraft.api.version=${MC_VERSION}" \
    "-Dspigot.api.version=${SPIGOT_API_VERSION}" \
    "-Dpaper.api.version=${PAPER_API_VERSION}" \
    "-Dskip.spigot.api=${SKIP_SPIGOT_API}" )

PLUGIN_VERSION="$(cd "$REPO_ROOT" && mvn -q help:evaluate -Dexpression=project.version -DforceStdout)"
PLUGIN_JAR="$REPO_ROOT/target/RealEstate-${PLUGIN_VERSION}.jar"
[[ -f "$PLUGIN_JAR" ]] || { echo "Expected build output not found: $PLUGIN_JAR" >&2; exit 1; }

# --- 3. Stage the plugin, eula, and (first-run-only) server.properties --
rm -f "$PLUGINS_DIR"/RealEstate-*.jar
STAGED_JAR="$PLUGINS_DIR/RealEstate-${TARGET}-${PLUGIN_VERSION}.jar"
cp "$PLUGIN_JAR" "$STAGED_JAR"
echo "Staged plugin: $STAGED_JAR"

cat > "$SERVER_DIR/eula.txt" <<EOF
# Accepted automatically by scripts/run-server.sh for local testing.
# By leaving this as TRUE you are indicating your agreement to the
# Minecraft EULA: https://aka.ms/MinecraftEULA
eula=true
EOF

if [[ ! -f "$SERVER_DIR/server.properties" ]]; then
  cat > "$SERVER_DIR/server.properties" <<EOF
# Minimal defaults seeded once by scripts/run-server.sh; edit freely, this
# file is never regenerated once it exists.
online-mode=false
motd=RealEstate local test server ($TARGET)
EOF
fi

if [[ "$NO_LAUNCH" == "1" ]]; then
  echo "Built and staged only (--no-launch). Server dir: $SERVER_DIR"
  exit 0
fi

# --- 4. Launch -------------------------------------------------------------
echo "Starting Paper $MC_VERSION in $SERVER_DIR ..."
cd "$SERVER_DIR"
exec java "${EXTRA_JAVA_ARGS[@]}" -jar "$(basename "$SERVER_JAR")" --nogui
