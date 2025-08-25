@echo off
setlocal enabledelayedexpansion
cd /d %~dp0\..
echo [1/3] Building Rust route optimizer...
pushd src\rust\route
cargo build --release
popd
if not exist bin mkdir bin
copy src\rust\route\target\release\route_optimizer.exe bin\route_optimizer.exe >nul
echo [2/3] Compiling Java CLI...
if not exist build\java mkdir build\java
javac -d build\java src\java\com\abyssinia\App.java
jar --create --file build\AbyssiniaMaritime.jar -C build\java .
echo [3/3] Done. Run with:
echo set ROUTE_BIN=bin\route_optimizer.exe && java -cp build\AbyssiniaMaritime.jar com.abyssinia.App