# KMP Project Template

Kotlin Multiplatform template for Android + iOS with modular architecture:
- `androidApp` for Android entry point.
- `iosApp` for iOS entry point and Xcode project.
- `shared` for shared app layer and root composition.
- `base/*` for cross-cutting platform/domain/ui/network/foundation modules.
- `core/*` for reusable business/platform capabilities (database, language, design system, theme manager, network client).
- `buildLogic` for convention plugins and centralized Gradle build logic.

## Quick Start

1. Create a new repository using this template.
2. Clone your new repository.
3. Run bootstrap script:

```bash
./scripts/template/init-template.sh \
  --project-name "MyApp" \
  --app-package "com.mycompany.myapp" \
  --ios-bundle-id "com.mycompany.myapp"
```

4. Open the project in Android Studio and sync Gradle.
5. Open `iosApp` in Xcode and configure signing.

Additional setup details: see `TEMPLATE.md`.

## Build Commands

Android debug build:

```bash
./gradlew :androidApp:assembleDebug
```

Run static analysis:

```bash
./gradlew detektAll
```

Install git hook manually (optional):

```bash
./gradlew installGitHook
```

## Project Structure

- `androidApp/src/main`: Android app code.
- `shared/src/commonMain`: shared app composition and root flow.
- `shared/src/androidMain`: Android-specific shared implementations.
- `shared/src/iosMain`: iOS-specific shared implementations.
- `base/**/src/commonMain|androidMain|iosMain`: base layer source sets.
- `core/**/src/commonMain|androidMain|iosMain`: core layer source sets.

## Notes

- Project uses included build (`buildLogic`) for convention plugins.
- Dependency versions are centralized in `gradle/libs.versions.toml`.
- Kotlin and Gradle cache settings are in `gradle.properties`.
