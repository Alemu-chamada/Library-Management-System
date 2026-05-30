# 🚀 SmartLibrary Deployment Guide

This document outlines the professional deployment, distribution, backup, and security configurations for **SmartLibrary v1.0.0**.

---

## 📋 System Requirements

SmartLibrary is designed as a lightweight, zero-dependency desktop client:
* **Operating System:** Windows 10 or Windows 11 (64-bit).
* **Storage Space:** ~200 MB disk space.
* **Java Dependency:** None. The application is packaged with a bundled, optimized Java Runtime Environment (JRE). No system Java installation or PATH updates are required on client machines.
* **Network Connectivity:** 100% offline. The system requires no network connection, database servers, or internet access to function.

---

## 📦 Distribution Formats

The automated build system produces all outputs under the `build/` folder:

### Build Output Structure
```
build/
├── jar/
│   └── SmartLibrary.jar          # Executable JAR file
└── package/
    └── SmartLibrary/
        ├── SmartLibrary.zip      # Portable ZIP package
        ├── SmartLibrary.exe      # Runnable Windows executable
        ├── app/                  # Application files
        └── runtime/              # Bundled Java Runtime Environment
```

### Portable Package
* **Location:** `build/package/SmartLibrary/`
* **Features:**
  - Zero-installation needed. Ideal for running from shared network drives or USB sticks.
  - Simply run `SmartLibrary.exe` directly from `build/package/SmartLibrary/`
* **Execution:** Run the `SmartLibrary.exe`
  
> [!WARNING]
> Do not move `SmartLibrary.exe` out of its directory. It requires the accompanying `app/` and `runtime/` sub-folders to launch.

---

## 🛠️ Build Pipeline & Release Checklist

Production binaries should be built on a clean machine to guarantee artifact security.

### Build Prerequisites
1. **JDK 17+:** Standard compiler toolchain.

### Compilation and Packaging
Execute the master build script from the project root:
```cmd
build.bat
```
*This triggers the PowerShell builder script `scripts/build.ps1` which automatically compiles, tests, links, and zips the files.*

### Release Checklist
Follow these steps to publish a new version:
1. **Tag the Release:**
   ```cmd
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```
2. Run `build.bat` on the build machine.
3. The final artifacts are located under `build/package/SmartLibrary/`:
   - `SmartLibrary.exe` (runnable)
   - `SmartLibrary.zip` (portable package)
4. Copy release notes from [CHANGELOG.md](CHANGELOG.md).

---

## 🔒 Security Hardening & First-Run

SmartLibrary provides baseline credentials for the initial initialization:
* **Default Admin Username:** `admin`
* **Default Admin Password:** `admin`

### Mandatory Actions Upon First Run:
1. **Change Default Credentials:** Log in immediately and update the password for the `admin` account from the user profile panel.
2. **Assign Librarian Accounts:** Do not share the master `admin` account. Create unique login IDs for library staff and librarians, assigning them the **Librarian** role.
3. **Password Policies:** Enforce strong passwords. Passwords are cryptographically hashed locally using **SHA-256** and are never saved in plaintext format.

---

## 💾 Backup and Disaster Recovery

Since SmartLibrary relies on flat-file CSV storage, backing up user data is extremely straightforward.

### Database File Schema
The database files under `data/` include:
- `books.csv` — Contains details on books, categories, and quantities.
- `users.csv` — Basic profile information (names, emails, active loans).
- `users_auth.csv` — Hashed passwords, roles, and authorization credentials.
- `transactions.csv` — Historical lending and return logs.
- `removed_items.csv` — Deleted records for auditing purposes.

### Backup Procedure
1. Ensure the SmartLibrary application is closed on the target computer.
2. Navigate to the database folder:
   - For installed applications: `%APPDATA%\SmartLibrary\data\`
   - For dev mode: `[project-root]/data/`
3. Copy all `.csv` files inside that directory to a secure backup storage (e.g., encrypted cloud storage or offline external drive).

### Restore Procedure
1. Close the SmartLibrary application.
2. Replace all files in `%APPDATA%\SmartLibrary\data\` with the backed-up `.csv` files.
3. Restart the application.

---

## 🔍 Troubleshooting & Logs

If the application fails to launch or displays unexpected errors, logs are saved under:
```text
%APPDATA%\SmartLibrary\logs\error.log
```
Check this file for system stacks or file access failures.
