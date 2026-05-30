# 🚀 SmartLibrary Deployment Guide

This document outlines the professional deployment, distribution, backup, and security configurations for **SmartLibrary v1.0.0**.

---

## 📋 Table of Contents
- [System Requirements](#-system-requirements)
- [Distribution Formats](#-distribution-formats)
- [Build Pipeline & Release Checklist](#-build-pipeline--release-checklist)
- [GitHub Release Process](#-github-release-process)
- [Security Hardening & First-Run](#-security-hardening--first-run)
- [Backup and Disaster Recovery](#-backup-and-disaster-recovery)
- [Troubleshooting & Logs](#-troubleshooting--logs)
- [License](#-license)

---

## 📋 System Requirements

SmartLibrary is designed as a lightweight, zero-dependency desktop client:
* **Operating System:** Windows 10 or Windows 11 (64-bit).
* **Storage Space:** ~200 MB disk space.
* **Java Dependency:** None. The application is packaged with a bundled, optimized Java Runtime Environment (JRE). No system Java installation or PATH updates are required on client machines.
* **Network Connectivity:** 100% offline. The system requires no network connection, database servers, or internet access to function.

---

## 📦 Distribution Formats

The automated build system produces two formats under the `dist/` folder (gitignored from version control):

### 1. Native Windows Installer (Recommended)
* **File:** `SmartLibrary-Setup.exe`
* **Features:**
  - Installs into `C:\Users\<User>\AppData\Local\SmartLibrary`
  - Adds shortcut to Windows Start Menu and Desktop
  - Automatic registry registration
* **Data Storage:** Production data is sandboxed securely under the user profile:
  ```text
  %APPDATA%\SmartLibrary\data\
  ```
  *(Translates to: `C:\Users\<Username>\AppData\Roaming\SmartLibrary\data\`)*

### 2. Portable Package
* **File:** `SmartLibrary-1.0.0-win64.zip`
* **Features:**
  - Zero-installation needed. Ideal for running from shared network drives or USB sticks.
  - Simply extract the ZIP file to any directory (e.g., `C:\Program Files\SmartLibrary\`).
* **Execution:** Run the `SmartLibrary.exe` inside the extracted folder.

> [!WARNING]
> Do not move `SmartLibrary.exe` out of its root directory. It requires the accompanying `app/` and `runtime/` sub-folders to launch.

---

## 🛠️ Build Pipeline & Release Checklist

Production binaries should be built on a clean machine to guarantee artifact security.

### Build Prerequisites
1. **JDK 17+:** Standard compiler toolchain.
2. **WiX Toolset v3.x+:** (Required for generating `SmartLibrary-Setup.exe`). Ensure the WiX binaries are added to your Windows system PATH environment variable.

### Compilation and Packaging
Execute the master build script from the project root:
```cmd
build.bat
```
*This triggers the PowerShell builder script `scripts/build.ps1` which automatically compiles, tests, links, and zips the files.*

---

## 📤 GitHub Release Process

Follow these steps to publish a new version to GitHub:

### 1. Prepare the Repository
Ensure your git repository is clean and all changes are committed:
```cmd
git status
git add .
git commit -m "Prepare for v1.0.0 release"
```

### 2. Tag the Release
Create and push a version tag:
```cmd
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

### 3. Build the Binaries
Run the build script to generate the release artifacts:
```cmd
build.bat
```
This will create:
- `dist/SmartLibrary-Setup.exe`
- `dist/SmartLibrary-1.0.0-win64.zip`

### 4. Create GitHub Release
1. Open your GitHub repository in a browser
2. Navigate to **Releases** -> **Draft a new release**
3. Select the `v1.0.0` tag you just created
4. Add a release title: `v1.0.0`
5. Copy release notes from [CHANGELOG.md](CHANGELOG.md)
6. Upload the two binary files from the `dist/` folder
7. Click **Publish release**

### Important Notes
- **Do NOT commit `dist/` or `build/` folders** — They are already gitignored and should only be uploaded as release assets
- The `.gitignore` file excludes: `build/`, `dist/`, and development data files

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

---

## 📄 License

SmartLibrary is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for details.
