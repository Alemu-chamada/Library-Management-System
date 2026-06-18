# ============================================================
#  SmartLibrary - Professional Windows Release Build
# ============================================================

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

$APP_NAME       = "SmartLibrary"
$APP_VERSION    = "1.0.0"
$APP_DESC       = "Smart Library Management System"
$MAIN_CLASS     = "library.main.Main"
$JAR_NAME       = "SmartLibrary.jar"
$SRC_DIR        = "src\main\java"
$RESOURCES_DIR  = "src\main\resources"
$BUILD_DIR      = "build"
$DIST_DIR       = "dist"
$CLASSES_DIR    = "$BUILD_DIR\classes"
$JAR_DIR        = "$BUILD_DIR\jar"
$APP_IMAGE_ROOT = "$BUILD_DIR\app-image"
$INSTALLER_ROOT = "$BUILD_DIR\installer"
$APP_DIR        = "$APP_IMAGE_ROOT\$APP_NAME"
$SETUP_EXE      = "$DIST_DIR\SmartLibrary-Setup.exe"
$PORTABLE_DIR   = "$DIST_DIR\$APP_NAME"
$RELEASE_ZIP    = "$DIST_DIR\$APP_NAME-$APP_VERSION-win64.zip"

$propsFile = "$RESOURCES_DIR\application.properties"
if (Test-Path $propsFile) {
    Get-Content $propsFile | ForEach-Object {
        if ($_ -match '^\s*app\.name\s*=\s*(.+)\s*$')        { $APP_NAME    = $Matches[1].Trim() }
        if ($_ -match '^\s*app\.version\s*=\s*(.+)\s*$')     { $APP_VERSION = $Matches[1].Trim() }
        if ($_ -match '^\s*app\.description\s*=\s*(.+)\s*$') { $APP_DESC    = $Matches[1].Trim() }
    }
}

function Write-Step($n, $total, $msg) { Write-Host ""; Write-Host "[STEP $n/$total]  $msg" -ForegroundColor Cyan }
function Write-OK($msg)   { Write-Host "  [OK]    $msg" -ForegroundColor Green }
function Write-Warn($msg) { Write-Host "  [WARN]  $msg" -ForegroundColor Yellow }
function Write-Err($msg)  { Write-Host "  [ERROR] $msg" -ForegroundColor Red; exit 1 }

Write-Host ""
Write-Host "#####################################################" -ForegroundColor Magenta
Write-Host "#     SmartLibrary  -  Professional Release Build  #" -ForegroundColor Magenta
Write-Host "#####################################################" -ForegroundColor Magenta

Get-Process -Name $APP_NAME -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 1

$totalSteps = 9

Write-Step 1 $totalSteps "Checking build tools..."
foreach ($tool in @("javac", "jar", "jpackage")) {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
        Write-Err "$tool not found. Install JDK 17+ and add bin\ to PATH."
    }
}
Write-OK "Build tools ready. ($(javac --version 2>&1))"

Write-Step 2 $totalSteps "Preparing build directories..."
foreach ($dir in @($BUILD_DIR, $CLASSES_DIR, $JAR_DIR, $APP_IMAGE_ROOT, $INSTALLER_ROOT, $DIST_DIR)) {
    if (Test-Path $dir) { Remove-Item $dir -Recurse -Force -ErrorAction SilentlyContinue }
}
New-Item -ItemType Directory -Path $CLASSES_DIR, $JAR_DIR, $APP_IMAGE_ROOT, $INSTALLER_ROOT, $DIST_DIR -Force | Out-Null
Write-OK "Directories ready."

Write-Step 3 $totalSteps "Compiling sources..."
$sources = Get-ChildItem -Path $SRC_DIR -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
if ($sources.Count -eq 0) { Write-Err "No Java sources found." }
& javac -d $CLASSES_DIR -encoding UTF-8 --release 17 @sources
if ($LASTEXITCODE -ne 0) { Write-Err "Compilation failed." }
Write-OK "$($sources.Count) source file(s) compiled."

Write-Step 4 $totalSteps "Packaging JAR..."
if (Test-Path $RESOURCES_DIR) {
    Copy-Item -Path "$RESOURCES_DIR\*" -Destination $CLASSES_DIR -Recurse -Force
}
$jarPath = "$JAR_DIR\$JAR_NAME"
& jar --create --file $jarPath --main-class $MAIN_CLASS -C $CLASSES_DIR .
if ($LASTEXITCODE -ne 0) { Write-Err "JAR creation failed." }
if (-not (Test-Path $jarPath)) { Write-Err "JAR not found after build." }
Write-OK "JAR: $jarPath"

Write-Step 5 $totalSteps "Verifying JAR entry point..."
$jarListing = jar tf $jarPath
if ($jarListing -notcontains "library/main/Main.class") { Write-Err "JAR missing library/main/Main.class" }
if ($jarListing -notcontains "application.properties") { Write-Warn "JAR missing application.properties" }
if (-not ($jarListing | Where-Object { $_ -like "templates/*" })) { Write-Warn "JAR may be missing templates/ resources." }
Write-OK "JAR contents verified."

Write-Step 6 $totalSteps "Creating portable app-image (bundled JRE)..."
$iconOpt = @()
foreach ($icon in @("$RESOURCES_DIR\icons\app.ico", "$RESOURCES_DIR\icons\lib.ico")) {
    if (Test-Path $icon) { $iconOpt = @("--icon", $icon); break }
}

$commonArgs = @(
    "--name", $APP_NAME,
    "--app-version", $APP_VERSION,
    "--vendor", "SmartLibrary",
    "--description", $APP_DESC,
    "--input", $JAR_DIR,
    "--main-jar", $JAR_NAME,
    "--main-class", $MAIN_CLASS,
    "--java-options", "-Dawt.useSystemAAFontSettings=on",
    "--java-options", "-Dswing.aatext=true",
    "--java-options", "-Dsun.java2d.uiScale.enabled=true",
    "--java-options", "-Xms64m",
    "--java-options", "-Xmx512m"
) + $iconOpt

$errLog = "$BUILD_DIR\jpackage_errors.txt"
& jpackage @($commonArgs + @("--dest", $APP_IMAGE_ROOT, "--type", "app-image")) 2>$errLog
if ($LASTEXITCODE -ne 0 -or -not (Test-Path "$APP_DIR\$APP_NAME.exe")) {
    Write-Host ""; if (Test-Path $errLog) { Get-Content $errLog }; Write-Err "app-image packaging failed."
}
Write-OK "Portable app: $APP_DIR\$APP_NAME.exe"

Write-Step 7 $totalSteps "Creating Windows installer (SmartLibrary-Setup.exe)..."
$installerBuilt = $false
try {
    if (Test-Path $INSTALLER_ROOT) { Remove-Item $INSTALLER_ROOT -Recurse -Force -ErrorAction SilentlyContinue }
    New-Item -ItemType Directory -Path $INSTALLER_ROOT -Force | Out-Null
    & jpackage @($commonArgs + @(
        "--dest", $INSTALLER_ROOT,
        "--type", "exe",
        "--win-menu",
        "--win-shortcut",
        "--win-per-user-install",
        "--win-dir-chooser",
        "--win-menu-group", $APP_NAME
    )) 2>$errLog

    $builtInstaller = Get-ChildItem $INSTALLER_ROOT -Filter "*.exe" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($LASTEXITCODE -eq 0 -and $builtInstaller) {
        Copy-Item $builtInstaller.FullName $SETUP_EXE -Force
        Write-OK "Installer: $SETUP_EXE"
        $installerBuilt = $true
    } else {
        Write-Warn "Installer not created (install WiX Toolset for SmartLibrary-Setup.exe)."
        if (Test-Path $errLog) { Get-Content $errLog | Select-Object -Last 8 | Write-Host }
    }
} catch {
    Write-Warn "Installer step skipped: $($_.Exception.Message)"
    if (Test-Path $errLog) { Get-Content $errLog | Select-Object -Last 8 | Write-Host }
}

Write-Step 8 $totalSteps "Publishing release artifacts..."
if (Test-Path $PORTABLE_DIR) { Remove-Item $PORTABLE_DIR -Recurse -Force -ErrorAction SilentlyContinue }
Copy-Item $APP_DIR $PORTABLE_DIR -Recurse -Force
Write-OK "Portable: $PORTABLE_DIR\$APP_NAME.exe"

try {
    if (Test-Path $RELEASE_ZIP) { Remove-Item $RELEASE_ZIP -Force }
    Compress-Archive -Path $PORTABLE_DIR -DestinationPath $RELEASE_ZIP -Force
    Write-OK "Release zip: $RELEASE_ZIP"
} catch {
    Write-Warn "Zip skipped (close SmartLibrary if running): $RELEASE_ZIP"
}

Write-Step 9 $totalSteps "Smoke-testing portable EXE (using temp data dir)..."
Get-Process -Name $APP_NAME -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 1
$tempData = Join-Path $env:TEMP "SmartLibrary_TestData_$(Get-Date -Format 'yyyyMMddHHmmss')"
New-Item -ItemType Directory -Path $tempData -Force | Out-Null
$env:APPDATA = Split-Path $tempData -Parent # Make app use temp dir instead of real %APPDATA%
$proc = Start-Process -FilePath "$PORTABLE_DIR\$APP_NAME.exe" -PassThru
Start-Sleep -Seconds 4
if ($proc.HasExited) {
    Write-Err "Portable EXE exited immediately (code $($proc.ExitCode)). Check logs under $tempData\logs\error.log"
}
Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
Remove-Item $tempData -Recurse -Force -ErrorAction SilentlyContinue
Write-OK "Portable EXE stayed running (tested with temp data)."

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Green
Write-Host "  RELEASE BUILD SUCCESSFUL" -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green
Write-Host ""
Write-Host "  Test locally:  $PORTABLE_DIR\$APP_NAME.exe"
Write-Host "  GitHub zip:    $RELEASE_ZIP"
if ($installerBuilt) { Write-Host "  GitHub setup:  $SETUP_EXE" }
Write-Host "  JAR test:      java -jar $jarPath"
Write-Host ""
