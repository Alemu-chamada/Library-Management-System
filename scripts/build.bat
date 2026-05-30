@echo off
setlocal
powershell -ExecutionPolicy Bypass -File "%~dp0build.ps1"
if errorlevel 1 (
    echo.
    echo  [ERROR] Build failed. See messages above.
    pause
    exit /b 1
)
echo.
echo  [OK] Build completed successfully.
pause
