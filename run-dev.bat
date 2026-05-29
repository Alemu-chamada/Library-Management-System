@echo off
setlocal

echo.
echo  ─────────────────────────────────────────
echo   SmartLibrary  —  Development Quick-Run
echo  ─────────────────────────────────────────
echo.

set MAIN_CLASS=library.main.Main
set SRC_DIR=src\main\java
set DEV_BUILD=build-dev
set CLASSES_DIR=%DEV_BUILD%\classes

if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"

:: Collect sources
dir /s /b "%SRC_DIR%\*.java" > "%DEV_BUILD%\sources.txt"

:: Compile
echo  Compiling...
javac -d "%CLASSES_DIR%" -encoding UTF-8 --release 17 @"%DEV_BUILD%\sources.txt"
if %errorlevel% neq 0 (
    echo.
    echo  [ERROR]  Compilation failed.
    pause
    exit /b 1
)

echo  [OK]  Compiled. Launching SmartLibrary...
echo.

:: Run — data\ is resolved relative to CWD (project root) in dev mode
java ^
    -cp "%CLASSES_DIR%" ^
    -Dawt.useSystemAAFontSettings=on ^
    -Dswing.aatext=true ^
    %MAIN_CLASS%

endlocal
