@echo off
set "ROOT=%~dp0.."
cd /d "%ROOT%"

echo.
echo  Cleaning build artifacts...

if exist "build"      rmdir /s /q "build"      && echo  Removed: build\
if exist "build-dev"  rmdir /s /q "build-dev"  && echo  Removed: build-dev\
if exist "dist\SmartLibrary\runtime" rmdir /s /q "dist\SmartLibrary\runtime" 2>nul

echo.
echo  [OK] Clean complete.
pause
