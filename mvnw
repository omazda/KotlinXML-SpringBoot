#!/bin/sh
# Maven Wrapper script
# Downloads Maven automatically on first run

set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-.}"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

# Read distribution URL from properties
DISTRIBUTION_URL=$(grep "distributionUrl" "$MAVEN_WRAPPER_PROPERTIES" | sed 's/.*=//')
DISTRIBUTION_URL=$(echo "$DISTRIBUTION_URL" | tr -d '[:space:]')

# Determine cache directory
if [ -n "$MAVEN_USER_HOME" ]; then
    MAVEN_USER_HOME_LOCAL="$MAVEN_USER_HOME"
elif [ -n "$user.home" ]; then
    MAVEN_USER_HOME_LOCAL="$user.home"
else
    MAVEN_USER_HOME_LOCAL="$HOME"
fi
MAVEN_HOME_LOCAL="$MAVEN_USER_HOME_LOCAL/.m2/wrapper/dists"

# Derive Maven home from distribution URL
DIST_NAME=$(basename "$DISTRIBUTION_URL" .zip)
MAVEN_HOME="$MAVEN_HOME_LOCAL/$DIST_NAME/$DIST_NAME"

if [ ! -d "$MAVEN_HOME" ]; then
    echo "Downloading Maven $DIST_NAME..."
    mkdir -p "$MAVEN_HOME_LOCAL/$DIST_NAME"
    if command -v curl > /dev/null; then
        curl -fsSL "$DISTRIBUTION_URL" -o "$MAVEN_HOME_LOCAL/$DIST_NAME.zip"
    elif command -v wget > /dev/null; then
        wget -q "$DISTRIBUTION_URL" -O "$MAVEN_HOME_LOCAL/$DIST_NAME.zip"
    else
        echo "Error: curl or wget is required to download Maven." >&2
        exit 1
    fi
    unzip -q "$MAVEN_HOME_LOCAL/$DIST_NAME.zip" -d "$MAVEN_HOME_LOCAL/$DIST_NAME"
    rm "$MAVEN_HOME_LOCAL/$DIST_NAME.zip"
    echo "Maven downloaded to $MAVEN_HOME"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
