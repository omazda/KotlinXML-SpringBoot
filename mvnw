#!/bin/sh
set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-.}"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

DISTRIBUTION_URL=$(grep "distributionUrl" "$MAVEN_WRAPPER_PROPERTIES" | sed 's/.*=//')
DISTRIBUTION_URL=$(echo "$DISTRIBUTION_URL" | tr -d '[:space:]')

if [ -n "$MAVEN_USER_HOME" ]; then
    MAVEN_USER_HOME_LOCAL="$MAVEN_USER_HOME"
else
    MAVEN_USER_HOME_LOCAL="$HOME"
fi

MAVEN_HOME_LOCAL="$MAVEN_USER_HOME_LOCAL/.m2/wrapper/dists"
DIST_NAME=$(basename "$DISTRIBUTION_URL" .zip)
MAVEN_DIST_DIR="$MAVEN_HOME_LOCAL/$DIST_NAME"

if [ ! -d "$MAVEN_DIST_DIR" ] || [ -z "$(find "$MAVEN_DIST_DIR" -mindepth 1 -maxdepth 1 -type d 2>/dev/null)" ]; then
    echo "Downloading Maven $DIST_NAME..."
    mkdir -p "$MAVEN_DIST_DIR"
    if command -v curl >/dev/null 2>&1; then
        curl -fsSL "$DISTRIBUTION_URL" -o "$MAVEN_DIST_DIR.zip"
    elif command -v wget >/dev/null 2>&1; then
        wget -q "$DISTRIBUTION_URL" -O "$MAVEN_DIST_DIR.zip"
    else
        echo "curl or wget is required to download Maven." >&2
        exit 1
    fi
    unzip -q "$MAVEN_DIST_DIR.zip" -d "$MAVEN_DIST_DIR"
    rm "$MAVEN_DIST_DIR.zip"
fi

MAVEN_HOME="$(find "$MAVEN_DIST_DIR" -mindepth 1 -maxdepth 1 -type d | head -n 1)"
exec "$MAVEN_HOME/bin/mvn" "$@"
