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
  --old-app-package Optional template app package to replace (default: com.lebedaliv2601.example).
  --old-root-package Optional template root package to replace (default: com.lebedaliv2601).
  -h, --help       Show help.
EOF
}

PROJECT_NAME=""
APP_PACKAGE=""
IOS_BUNDLE_ID=""
TEAM_ID=""
OLD_APP_PACKAGE="com.lebedaliv2601.example"
OLD_ROOT_PACKAGE="com.lebedaliv2601"
TMP_APP_TOKEN="__TEMPLATE_APP_PACKAGE_TOKEN__"
TMP_STASH_DIR="__tmp_app_package_stash__"

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
    --old-app-package)
      OLD_APP_PACKAGE="${2:-}"
      shift 2
      ;;
    --old-root-package)
      OLD_ROOT_PACKAGE="${2:-}"
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

if [[ ! "$OLD_APP_PACKAGE" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
  echo "Error: --old-app-package must be a valid lowercase package." >&2
  exit 1
fi

if [[ ! "$OLD_ROOT_PACKAGE" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
  echo "Error: --old-root-package must be a valid lowercase package." >&2
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

merge_dir_contents() {
  local source_dir="$1"
  local target_dir="$2"

  mkdir -p "$target_dir"
  while IFS= read -r -d '' child; do
    if [[ "$child" == "$target_dir" ]]; then
      continue
    fi
    mv "$child" "$target_dir/"
  done < <(find "$source_dir" -mindepth 1 -maxdepth 1 -print0)
}

normalize_legacy_escaped_dirs() {
  while IFS= read -r -d '' dir; do
    local base parent clean_base target
    base="$(basename "$dir")"

    if [[ "$base" != *\\* ]]; then
      continue
    fi

    parent="$(dirname "$dir")"
    clean_base="${base//\\/}"
    target="$parent/$clean_base"

    if [[ -d "$target" ]]; then
      merge_dir_contents "$dir" "$target"
      rmdir "$dir" 2>/dev/null || true
    else
      mv "$dir" "$target"
    fi
  done < <(find . -depth -type d -print0)
}

stash_old_app_dirs() {
  local old_app_path="$1"

  while IFS= read -r -d '' dir; do
    local parent="${dir%$old_app_path}"
    local stash="${parent}${TMP_STASH_DIR}"

    if [[ -d "$stash" ]]; then
      merge_dir_contents "$dir" "$stash"
      rmdir "$dir" 2>/dev/null || true
    else
      mv "$dir" "$stash"
    fi
  done < <(find . -type d -path "*/$old_app_path" -print0)
}

move_root_dirs() {
  local old_root_path="$1"
  local new_root_path="$2"

  while IFS= read -r -d '' dir; do
    local target="${dir%$old_root_path}$new_root_path"

    if [[ "$dir" == "$target" ]]; then
      continue
    fi

    merge_dir_contents "$dir" "$target"
    rmdir "$dir" 2>/dev/null || true
  done < <(find . -type d -path "*/$old_root_path" -print0)
}

restore_stashed_app_dirs() {
  local new_root_path="$1"

  while IFS= read -r -d '' stash; do
    local target="${stash%$TMP_STASH_DIR}$new_root_path"
    merge_dir_contents "$stash" "$target"
    rmdir "$stash" 2>/dev/null || true
  done < <(find . -type d -path "*/$TMP_STASH_DIR" -print0)
}

package_to_path() {
  printf '%s' "$1" | tr '.' '/'
}

echo "Applying text replacements..."
replace_in_text_files "${OLD_APP_PACKAGE}.KmpProjectTemplate" "$IOS_BUNDLE_ID"
replace_in_text_files "$OLD_APP_PACKAGE" "$TMP_APP_TOKEN"
replace_in_text_files "$OLD_ROOT_PACKAGE" "$APP_PACKAGE"
replace_in_text_files "$TMP_APP_TOKEN" "$APP_PACKAGE"
replace_in_text_files "KmpProjectTemplate" "$PROJECT_NAME"

echo "Updating iOS config..."
BUNDLE_WITH_SUFFIX="${IOS_BUNDLE_ID}\$(TEAM_ID)"
TEAM_VALUE="$TEAM_ID" perl -0pi -e 's/^TEAM_ID=.*$/TEAM_ID=$ENV{TEAM_VALUE}/m' iosApp/Configuration/Config.xcconfig
PRODUCT_VALUE="$PROJECT_NAME" perl -0pi -e 's/^PRODUCT_NAME=.*$/PRODUCT_NAME=$ENV{PRODUCT_VALUE}/m' iosApp/Configuration/Config.xcconfig
BUNDLE_VALUE="$BUNDLE_WITH_SUFFIX" perl -0pi -e 's/^PRODUCT_BUNDLE_IDENTIFIER=.*$/PRODUCT_BUNDLE_IDENTIFIER=$ENV{BUNDLE_VALUE}/m' iosApp/Configuration/Config.xcconfig

echo "Moving package directories..."
OLD_APP_PATH="$(package_to_path "$OLD_APP_PACKAGE")"
OLD_ROOT_PATH="$(package_to_path "$OLD_ROOT_PACKAGE")"
NEW_PATH="$(package_to_path "$APP_PACKAGE")"
normalize_legacy_escaped_dirs
stash_old_app_dirs "$OLD_APP_PATH"
move_root_dirs "$OLD_ROOT_PATH" "$NEW_PATH"
restore_stashed_app_dirs "$NEW_PATH"

echo
echo "Template initialization completed."
echo "Project name:      $PROJECT_NAME"
echo "App package:       $APP_PACKAGE"
echo "iOS bundle id:     $IOS_BUNDLE_ID"
echo "TEAM_ID suffix:    ${TEAM_ID:-<empty>}"
echo "Old app package:   $OLD_APP_PACKAGE"
echo "Old root package:  $OLD_ROOT_PACKAGE"
echo
echo "Next steps:"
echo "1) ./gradlew :androidApp:assembleDebug"
echo "2) Open iosApp in Xcode and configure signing"
