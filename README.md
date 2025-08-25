# AbyssiniaMaritime (Console Edition)

Console-first maritime logistics and port ops demo:
- **Java CLI** (main orchestrator)
- **Rust route optimizer** (distance/ETA in nautical miles)
- **Python cost engine** (fuel + fees + distance costs)
- **Node formatter** (optional pretty output)

## Quickstart

### 1) Build the Rust route optimizer
```bash
cd src/rust/route
cargo build --release
mkdir -p ../../../bin
cp target/release/route_optimizer ../../../bin/route_optimizer
```

### 2) Compile the Java CLI
```bash
cd ../../../
mkdir -p build/java
javac -d build/java src/java/com/abyssinia/App.java
jar --create --file build/AbyssiniaMaritime.jar -C build/java .
```

### 3) (Optional) Ensure Python and Node are available
- Python: `python3 --version` (no extra packages required)
- Node: `node --version`

### 4) Run demo end-to-end
```bash
ROUTE_BIN=bin/route_optimizer java -cp build/AbyssiniaMaritime.jar com.abyssinia.App
# choose option 3: Demo Full Flow
```

## Windows notes
- After `cargo build --release`, copy `target\release\route_optimizer.exe` to `bin\route_optimizer.exe`
- Run Java with:
```bat
set ROUTE_BIN=bin\route_optimizer.exe
java -cp build\AbyssiniaMaritime.jar com.abyssinia.App
```

## Project layout
```text
src/
  java/com/abyssinia/App.java
  rust/route/Cargo.toml
  rust/route/src/main.rs
  python/cost_engine.py
  node/formatter.js
bin/ (created after building Rust)
build/ (created after compiling Java)
```

## Notes
- JSON I/O connects all components. Java calls Rust/Python/Node via subprocess.
- The math model is simplified for a console demo (haversine + basic fuel estimate).