#!/usr/bin/env bash

set -euo pipefail

usage() {
  cat <<'EOF'
Bootstrap a new project from this template.

Usage:
  ./scripts/template/init-template.sh \
    --project-name "MyApp" \
    --app-package "com.mycompany.myapp" \
    --ios-bundle-id "com.mycompany.myapp" \
    [--team-id "DEV"]

Arguments:
  --project-name   Human-readable app/project name.
  --app-package    Base app package for Android/KMP (e.g. com.mycompany.myapp).
  --ios-bundle-id  iOS bundle identifier (e.g. com.mycompany.myapp).
  --team-id        Optional suffix used in iosApp/Configuration/Config.xcconfig TEAM_ID.
  -h, --help       Show help.
EOF
}

PROJECT_NAME=""
APP_PACKAGE=""
IOS_BUNDLE_ID=""
TEAM_ID=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --project-name)
      PROJECT_NAME="${2:-}"
      shift 2
      ;;
    --app-package)
      APP_PACKAGE="${2:-}"
      shift 2
      ;;
    --ios-bundle-id)
      IOS_BUNDLE_ID="${2:-}"
      shift 2
      ;;
    --team-id)
      TEAM_ID="${2:-}"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage
      exit 1
      ;;
  esac
done

if [[ -z "$PROJECT_NAME" || -z "$APP_PACKAGE" || -z "$IOS_BUNDLE_ID" ]]; then
  echo "Error: --project-name, --app-package and --ios-bundle-id are required." >&2
  usage
  exit 1
fi

if [[ ! -f "settings.gradle.kts" || ! -f "buildLogic/src/main/kotlin/Constants.kt" ]]; then
  echo "Error: run this script from repository root." >&2
  exit 1
fi

if [[ ! "$APP_PACKAGE" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
  echo "Error: --app-package must be a valid lowercase package (e.g. com.mycompany.myapp)." >&2
  exit 1
fi

if [[ ! "$IOS_BUNDLE_ID" =~ ^[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)+$ ]]; then
  echo "Error: --ios-bundle-id must be a valid bundle id (e.g. com.mycompany.myapp)." >&2
  exit 1
fi

replace_in_text_files() {
  local old="$1"
  local new="$2"

  while IFS= read -r -d '' file; do
    if [[ "$file" == "./scripts/template/init-template.sh" ]]; then
      continue
    fi
    if [[ "$file" == "./local.properties" ]]; then
      continue
    fi
    if grep -Iq . "$file"; then
      OLD="$old" NEW="$new" perl -0pi -e 's/\Q$ENV{OLD}\E/$ENV{NEW}/g' "$file"
    fi
  done < <(
    find . \
      -type d \( -name .git -o -name .gradle -o -name .idea -o -name .kotlin -o -name build \) -prune \
      -o -type f -print0
  )
}

move_package_dirs() {
  local old_path="$1"
  local new_path="$2"

  while IFS= read -r -d '' dir; do
    local target="${dir%$old_path}$new_path"

    if [[ "$dir" == "$target" ]]; then
      continue
    fi

    mkdir -p "$(dirname "$target")"
    if [[ -d "$target" ]]; then
      while IFS= read -r -d '' child; do
        mv "$child" "$target/"
      done < <(find "$dir" -mindepth 1 -maxdepth 1 -print0)
      rmdir "$dir" 2>/dev/null || true
    else
      mv "$dir" "$target"
    fi
  done < <(find . -type d -path "*/$old_path" -print0)
}

echo "Applying text replacements..."
replace_in_text_files "com.lebedaliv2601.example.KmpProjectTemplate" "$IOS_BUNDLE_ID"
replace_in_text_files "com.lebedaliv2601.example" "$APP_PACKAGE"
replace_in_text_files "com.lebedaliv2601" "$APP_PACKAGE"
replace_in_text_files "KmpProjectTemplate" "$PROJECT_NAME"

echo "Updating iOS config..."
BUNDLE_WITH_SUFFIX="${IOS_BUNDLE_ID}\$(TEAM_ID)"
TEAM_VALUE="$TEAM_ID" perl -0pi -e 's/^TEAM_ID=.*$/TEAM_ID=$ENV{TEAM_VALUE}/m' iosApp/Configuration/Config.xcconfig
PRODUCT_VALUE="$PROJECT_NAME" perl -0pi -e 's/^PRODUCT_NAME=.*$/PRODUCT_NAME=$ENV{PRODUCT_VALUE}/m' iosApp/Configuration/Config.xcconfig
BUNDLE_VALUE="$BUNDLE_WITH_SUFFIX" perl -0pi -e 's/^PRODUCT_BUNDLE_IDENTIFIER=.*$/PRODUCT_BUNDLE_IDENTIFIER=$ENV{BUNDLE_VALUE}/m' iosApp/Configuration/Config.xcconfig

echo "Moving package directories..."
NEW_PATH="${APP_PACKAGE//./\/}"
move_package_dirs "com/lebedaliv2601/example" "$NEW_PATH"
move_package_dirs "com/lebedaliv2601" "$NEW_PATH"

echo
echo "Template initialization completed."
echo "Project name:      $PROJECT_NAME"
echo "App package:       $APP_PACKAGE"
echo "iOS bundle id:     $IOS_BUNDLE_ID"
echo "TEAM_ID suffix:    ${TEAM_ID:-<empty>}"
echo
echo "Next steps:"
echo "1) ./gradlew :androidApp:assembleDebug"
echo "2) Open iosApp in Xcode and configure signing"
