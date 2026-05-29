@echo off
echo.
echo  Cleaning all build artifacts...

if exist "build"      rmdir /s /q "build"      && echo  Removed: build\
if exist "build-dev"  rmdir /s /q "build-dev"  && echo  Removed: build-dev\

echo.
echo  [OK]  Clean complete.
echo.
pause
