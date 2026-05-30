@echo off
setlocal
set "ROOT=%~dp0.."
cd /d "%ROOT%"

echo.
echo  SmartLibrary - Development Quick-Run
echo.

set MAIN_CLASS=library.main.Main
set SRC_DIR=src\main\java
set RES_DIR=src\main\resources
set DEV_BUILD=build-dev
set CLASSES_DIR=%DEV_BUILD%\classes

if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"
if exist "%DEV_BUILD%\sources.txt" del "%DEV_BUILD%\sources.txt"
setlocal enabledelayedexpansion
for /r "%SRC_DIR%" %%f in (*.java) do (
    set "FILEPATH=%%f"
    set "FILEPATH=!FILEPATH:\=/!"
    echo "!FILEPATH!" >> "%DEV_BUILD%\sources.txt"
)
endlocal

echo  Compiling...
javac -d "%CLASSES_DIR%" -encoding UTF-8 --release 17 @"%DEV_BUILD%\sources.txt"
if %errorlevel% neq 0 (
    echo  [ERROR] Compilation failed.
    pause
    exit /b 1
)

if exist "%RES_DIR%" xcopy /E /Y /Q "%RES_DIR%\*" "%CLASSES_DIR%\" >nul 2>&1

echo  [OK] Launching SmartLibrary...
echo.

java -cp "%CLASSES_DIR%" -Dapp.dataDir=data -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true %MAIN_CLASS%
endlocal
