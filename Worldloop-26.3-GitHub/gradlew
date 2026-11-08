#!/usr/bin/env sh
set -eu
VERSION="9.6.0"
BASE="${GRADLE_USER_HOME:-$HOME/.gradle}/worldloop-distributions"
GRADLE_HOME="$BASE/gradle-$VERSION"
if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
    echo "Downloading Gradle $VERSION from the official Gradle distribution server..."
    mkdir -p "$BASE"
    ZIP="${TMPDIR:-/tmp}/gradle-$VERSION-bin.zip"
    if command -v curl >/dev/null 2>&1; then
        curl -fL "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$ZIP"
    elif command -v wget >/dev/null 2>&1; then
        wget -O "$ZIP" "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip"
    else
        echo "Please install curl or wget to download Gradle." >&2
        exit 1
    fi
    command -v unzip >/dev/null 2>&1 || { echo "Please install unzip." >&2; exit 1; }
    unzip -q -o "$ZIP" -d "$BASE"
    rm -f "$ZIP"
fi
exec "$GRADLE_HOME/bin/gradle" "$@"
