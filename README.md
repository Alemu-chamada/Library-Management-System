<div align="center">
  <img src="src/main/resources/app.ico" alt="Logo" width="120" height="120">
  <h1>SmartLibrary Management System</h1>
  <p>
    <b>A modern, standalone Java application for comprehensive library and inventory management.</b>
  </p>
  
  [![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)
  [![Windows](https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white)](#)
  [![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](#)
</div>

---

## 📖 Overview

**SmartLibrary** is a high-performance, standalone desktop application built entirely in pure Java. Designed to replace legacy, complex enterprise library tools, this application requires **no external database server** and relies on a highly robust flat-file storage architecture. 

It provides seamless workflows for Librarians to manage inventory, track book issues/returns, handle authentication, and oversee member operations via an intuitive, hardware-accelerated Swing graphical interface.

## ✨ Key Features

* **🔐 Secure Authentication**: Multi-role access (Admin vs Standard users) with strong password hashing (SHA-256).
* **📚 Inventory Management**: Add, update, and search books in real-time. Track total stock vs. available stock instantly.
* **🔁 Transaction Engine**: Issue books, log return dates, calculate status (Active/Returned), and prevent overselling.
* **🗃️ Zero-Config Database**: Custom flat-file (CSV) storage engine. No SQL databases, no complex setup. The app seamlessly parses, maps, and writes models directly to disk.
* **🖥️ Modern Interface**: Hardware-accelerated UI rendering, anti-aliased fonts (`awt.useSystemAAFontSettings=on`), and responsive layouts.
* **🚀 Standalone Windows EXE**: Ships as a fully contained Windows Installer. Users **do not need Java installed** on their computer to run the app!

## 🏗️ Architecture & Stack

* **Language**: Java 17+
* **GUI Framework**: Java Swing / AWT (with custom high-DPI scaling)
* **Data Storage**: Proprietary flat-file Object-CSV Mapper
* **Build System**: Custom PowerShell/Batch automated pipeline utilizing `javac`, `jar`, and `jpackage` + WiX Toolset.

```text
SmartLibrary/
├── src/main/java/          # Source Code (Models, Views, Controllers, Storage)
├── src/main/resources/     # Application Assets and Icons
├── data/templates/         # Zero-state database schemas (Headers only)
├── build.bat               # Windows Build Trigger
└── build.ps1               # Core PowerShell Automation Script
```

## 🚀 Installation & Usage

### Method 1: The Easy Way (End Users)
1. Go to the [Releases](../../releases) page.
2. Download the `SmartLibrary-1.0.0.exe` installer.
3. Double click the installer to add the application to your Start Menu.
4. **No Java installation required!** The application ships with an embedded Java runtime environment.

### Method 2: For Developers (Compile from Source)

**Prerequisites:**
- JDK 17 or higher
- [WiX Toolset v3 or v4+](https://wixtoolset.org/) (Only required if you want to build the `.exe` installer)

**Quick Run (Development):**
To instantly compile and run the application without building an installer, use:
```cmd
run-dev.bat
```

**Build Windows Installer (Production):**
To compile the source, bundle the JAR, embed the Java runtime, and generate a standalone `.exe`:
```cmd
build.bat
```
The final installer will be available at `build\installer\SmartLibrary-1.0.0.exe`.

## 🔒 Security & Privacy

SmartLibrary operates completely offline. All data is written locally to the `%APPDIR%/data` folder. The executable shipped in the repository strictly utilizes empty database templates (`data/templates/`) to guarantee a fresh, secure environment for all new installations. No real user data or passwords are included.

## 📄 License

This project is open-source and available under the standard MIT License.
