#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
echo "Building DrivenByMoss with Maven..."
mvn -DskipTests package

echo "Looking for generated artifacts in target/..."
TARGET="$(pwd)/target"

BWEXT=$(find "$TARGET" -maxdepth 1 -type f -name '*.bwextension' | head -n 1 || true)
ZIP=$(find "$TARGET" -maxdepth 1 -type f -name '*.zip' | head -n 1 || true)

if [[ -z "$BWEXT" ]]; then
  echo "ERROR: No .bwextension found in $TARGET"
  echo "Check the Maven build output and the copy-rename plugin configuration in pom.xml"
  exit 1
fi

cp -v "$BWEXT" "$(pwd)/DrivenByMoss.bwextension"

echo "Build complete."
echo "Generated files:"
ls -lh "$TARGET"/*.jar "$TARGET"/*.zip "$TARGET"/*.bwextension 2>/dev/null || true

echo "Copied extension to: $(pwd)/DrivenByMoss.bwextension"
