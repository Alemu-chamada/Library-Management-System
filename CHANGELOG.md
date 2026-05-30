# Changelog

## [1.0.0] - 2026-05-29

### Added

- Professional package structure (`auth`, `config`, `gui/*`, `util`)
- Resource organization (`icons/`, `themes/`, `templates/`)
- GitHub-ready release build (`SmartLibrary-Setup.exe`, `SmartLibrary-1.0.0-win64.zip`)
- Deployment and build documentation
- CSV template seeding on first run
- Default admin security warning on login

### Architecture

- Separated GUI controller, auth portal, dashboard, and UI utilities
- Centralized app configuration (`AppInfo`, `AppPaths`, `ThemeConfig`)
- Extracted password hashing (`PasswordHasher`, `AuthConstants`)

### Packaging

- `scripts/` build automation with `jpackage`
- Bundled Java runtime — no user Java install required
- Portable and installer outputs for GitHub Releases
