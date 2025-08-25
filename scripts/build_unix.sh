#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
echo "[1/3] Building Rust route optimizer..."
(cd src/rust/route && cargo build --release)
mkdir -p bin
cp src/rust/route/target/release/route_optimizer bin/route_optimizer
echo "[2/3] Compiling Java CLI..."
mkdir -p build/java
javac -d build/java src/java/com/abyssinia/App.java
jar --create --file build/AbyssiniaMaritime.jar -C build/java .
echo "[3/3] Done. Run with:"
echo "ROUTE_BIN=bin/route_optimizer java -cp build/AbyssiniaMaritime.jar com.abyssinia.App"