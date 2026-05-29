# ============================================================
#  SmartLibrary - Windows EXE Builder (PowerShell)
# ============================================================

$APP_NAME     = "SmartLibrary"
$APP_VERSION  = "1.0.0"
$APP_VENDOR   = "SmartLibrary"
$APP_DESC     = "Smart Library Management System"
$MAIN_CLASS   = "library.main.Main"
$JAR_NAME     = "SmartLibrary.jar"
$SRC_DIR      = "src\main\java"
$DATA_DIR     = "data"
$RESOURCES_DIR= "src\main\resources"
$BUILD_DIR    = "build"
$CLASSES_DIR  = "$BUILD_DIR\classes"
$JAR_DIR      = "$BUILD_DIR\jar"
$INSTALLER_DIR= "$BUILD_DIR\installer"

function Write-Step($n, $msg) {
    Write-Host ""
    Write-Host "[STEP $n]  $msg" -ForegroundColor Cyan
}

function Write-OK($msg)   { Write-Host "  [OK]    $msg" -ForegroundColor Green }
function Write-Warn($msg) { Write-Host "  [WARN]  $msg" -ForegroundColor Yellow }
function Write-Err($msg)  { Write-Host "  [ERROR] $msg" -ForegroundColor Red }

Write-Host ""
Write-Host "#####################################################" -ForegroundColor Magenta
Write-Host "#        SmartLibrary  -  Windows EXE Builder      #" -ForegroundColor Magenta
Write-Host "#####################################################" -ForegroundColor Magenta
Write-Host ""

# ?? STEP 1: Verify build tools ????????????????????????????
Write-Step "1/6" "Checking required build tools..."

foreach ($tool in @("javac","jar","jpackage")) {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
        Write-Err "$tool not found in PATH."
        Write-Host "         Install JDK 17+ and add its bin\ to PATH."
        Write-Host "         Download: https://adoptium.net/temurin/releases/"
        exit 1
    }
}

$javaVer = & javac --version 2>&1
Write-OK "All build tools found. ($javaVer)"

# ?? STEP 2: Clean previous build ?????????????????????????
Write-Step "2/6" "Cleaning previous build output..."

if (Test-Path $BUILD_DIR) {
    Remove-Item $BUILD_DIR -Recurse -Force
    Write-Host "         Removed old $BUILD_DIR\"
}

New-Item -ItemType Directory -Path $CLASSES_DIR  | Out-Null
New-Item -ItemType Directory -Path $JAR_DIR       | Out-Null
New-Item -ItemType Directory -Path $INSTALLER_DIR | Out-Null
Write-OK "Build directories ready."

# ?? STEP 3: Collect sources ???????????????????????????????
Write-Step "3/6" "Collecting Java source files..."

$SOURCES_FILE = "$BUILD_DIR\sources.txt"
$sources = Get-ChildItem -Path $SRC_DIR -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
$sources | Out-File -FilePath $SOURCES_FILE -Encoding UTF8

Write-Host "         Found $($sources.Count) source file(s)."

if ($sources.Count -eq 0) {
    Write-Err "No Java source files found under $SRC_DIR\"
    exit 1
}
Write-OK "Source list written."

# ?? STEP 4: Compile ???????????????????????????????????????
Write-Step "4/6" "Compiling all Java sources..."
Write-Host ""

$compileArgs = @("-d", $CLASSES_DIR, "-encoding", "UTF-8", "--release", "17") + $sources
& javac @compileArgs

if ($LASTEXITCODE -ne 0) {
    Write-Err "Compilation failed - see errors above."
    exit 1
}
Write-OK "Compilation successful."

# ?? STEP 5: Build executable JAR ?????????????????????????
Write-Step "5/6" "Building executable JAR..."

$MF = "$BUILD_DIR\MANIFEST.MF"
@(
    "Manifest-Version: 1.0",
    "Main-Class: $MAIN_CLASS",
    "Created-By: SmartLibrary Build 1.0",
    ""
) | Out-File -FilePath $MF -Encoding ASCII

& jar cfm "$JAR_DIR\$JAR_NAME" $MF -C $CLASSES_DIR .

if ($LASTEXITCODE -ne 0) {
    Write-Err "JAR creation failed."
    exit 1
}
Write-Host "         JAR: $JAR_DIR\$JAR_NAME"

# Bundle EMPTY template CSVs so the app starts fresh for every new user.
# The app creates files on first run if they don't exist, but seeding
# with headers-only CSVs ensures the correct columns are always present.
$TEMPLATES_DIR = "$DATA_DIR\templates"
$SEED_DIR      = "$JAR_DIR\data"
New-Item -ItemType Directory -Path $SEED_DIR -Force | Out-Null

if (Test-Path $TEMPLATES_DIR) {
    Copy-Item -Path "$TEMPLATES_DIR\*" -Destination $SEED_DIR -Recurse -Force
    Write-Host "         Seeded data\ with empty template CSVs (fresh start for users)"
} else {
    # No templates found - write minimal header-only CSVs inline
    Write-Warn "data\templates\ not found - generating minimal seed files..."
    "bookId,title,author,genre,quantity,available"          | Out-File "$SEED_DIR\books.csv"         -Encoding UTF8
    "userId,name,email"                                      | Out-File "$SEED_DIR\users.csv"          -Encoding UTF8
    "transactionId,bookId,bookName,userId,userName,issueDate,returnDate,status" | Out-File "$SEED_DIR\transactions.csv" -Encoding UTF8
    "itemType,itemId,itemName,removedAt"                     | Out-File "$SEED_DIR\removed_items.csv" -Encoding UTF8
    "username,fullName,email,phone,role,passwordHash"        | Out-File "$SEED_DIR\users_auth.csv"    -Encoding UTF8
    Write-OK "Minimal seed files created."
}
Write-OK "JAR ready."

# ?? STEP 6: Package with jpackage ????????????????????????
Write-Step "6/6" "Packaging with jpackage (bundling Java runtime)..."
Write-Host "             This may take 1-3 minutes. Please wait..."
Write-Host ""

$iconOpt = @()
if (Test-Path "$RESOURCES_DIR\app.ico") {
    $iconOpt = @("--icon", "$RESOURCES_DIR\app.ico")
    Write-Host "         Custom icon: $RESOURCES_DIR\app.ico"
} else {
    Write-Host "         Icon: default (add resources\app.ico for a custom icon)"
}

$commonArgs = @(
    "--name",        $APP_NAME,
    "--app-version", $APP_VERSION,
    "--vendor",      $APP_VENDOR,
    "--description", $APP_DESC,
    "--input",       $JAR_DIR,
    "--main-jar",    $JAR_NAME,
    "--main-class",  $MAIN_CLASS,
    "--dest",        $INSTALLER_DIR,
    "--java-options", "-Dapp.dataDir=`$APPDIR/data",
    "--java-options", "-Dawt.useSystemAAFontSettings=on",
    "--java-options", "-Dswing.aatext=true",
    "--java-options", "-Dsun.java2d.uiScale.enabled=true",
    "--java-options", "-Xms64m",
    "--java-options", "-Xmx512m"
) + $iconOpt

$errLog = "$BUILD_DIR\jpackage_errors.txt"

# -- Attempt 1: Full EXE installer (needs WiX Toolset 3.x)
$exeArgs = $commonArgs + @(
    "--type",            "exe",
    "--win-menu",
    "--win-shortcut",
    "--win-per-user-install",
    "--win-dir-chooser",
    "--win-menu-group", $APP_NAME
)

& jpackage @exeArgs 2>$errLog

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=====================================================" -ForegroundColor Green
    Write-Host "  BUILD SUCCESSFUL  -  Windows Installer Created!   " -ForegroundColor Green
    Write-Host "=====================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "  Installer : $INSTALLER_DIR\$APP_NAME-$APP_VERSION.exe"
    Write-Host ""
    Write-Host "  When run, the installer will:"
    Write-Host "    - Bundle Java runtime (users need NO Java install)"
    Write-Host "    - Create Start Menu and Desktop shortcuts"
    Write-Host ""
    Write-Host "  Ready to distribute!" -ForegroundColor Green
    exit 0
}

# -- Attempt 2: Portable app-image (no WiX needed)
Write-Warn "EXE installer failed (WiX Toolset may not be installed)."
Write-Host "         Falling back to portable app-image..."
Write-Host "         To enable EXE installer: https://wixtoolset.org/releases/"
Write-Host ""

$appImageArgs = $commonArgs + @("--type", "app-image")
& jpackage @appImageArgs

if ($LASTEXITCODE -ne 0) {
    Write-Err "Packaging failed completely."
    Write-Host "         See error log: $errLog"
    Get-Content $errLog | Write-Host
    exit 1
}

Write-Host ""
Write-Host "=====================================================" -ForegroundColor Green
Write-Host "  BUILD SUCCESSFUL  -  Portable App-Image Created   " -ForegroundColor Green
Write-Host "=====================================================" -ForegroundColor Green
Write-Host ""
Write-Host "  Location : $INSTALLER_DIR\$APP_NAME\"
Write-Host "  Launcher : $INSTALLER_DIR\$APP_NAME\$APP_NAME.exe"
Write-Host ""
Write-Host "  This is a self-contained portable folder."
Write-Host "  Users run $APP_NAME.exe - no Java install needed." -ForegroundColor Green
Write-Host ""
