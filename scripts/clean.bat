@echo off
set "ROOT=%~dp0.."
cd /d "%ROOT%"

echo.
echo  Cleaning build artifacts...

if exist "build"      rmdir /s /q "build"      && echo  Removed: build\
if exist "build-dev"  rmdir /s /q "build-dev"  && echo  Removed: build-dev\

echo.
echo  [OK] Clean complete.
pause
