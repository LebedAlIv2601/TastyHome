# Template Usage

This repository is intended to be used as a reusable Kotlin Multiplatform starter.

## 1. Create Project From Template

1. Click `Use this template` on GitHub.
2. Clone the generated repository.

## 2. Initialize Project Identity

Run:

```bash
./scripts/template/init-template.sh \
  --project-name "MyApp" \
  --app-package "com.mycompany.myapp" \
  --ios-bundle-id "com.mycompany.myapp"
```

Optional:

```bash
--team-id "DEV"
```

The script updates:
- Gradle project name.
- Android application/package constants.
- iOS product name and bundle identifier.
- Kotlin package/import strings from template namespace to your namespace.
- Source directories (`com/lebedaliv2601/...`) to your package path.

## 3. Verify Build

```bash
./gradlew :androidApp:assembleDebug
./gradlew detektAll
```

## 4. iOS Setup

1. Open `/iosApp` in Xcode.
2. Configure signing for your Apple account/team.
3. Run iOS app target.

## 5. First Feature Checklist

1. Add your first feature module (inside `shared` and/or `core`).
2. Register dependencies in `shared/src/*/di`.
3. Add navigation entry (if needed) via `base/navigation`.
4. Add tests in `commonTest` or platform-specific test source sets.
