#!/bin/sh
# ----------------------------------------------------------------------------
# Apache Maven Wrapper Startup Shell Script for POSIX / Linux / macOS
# ----------------------------------------------------------------------------

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASE_DIR=`cd "$PRGDIR" >/dev/null; pwd`

# Locate Maven installation
if [ -d "$BASE_DIR/tools/apache-maven-3.9.6" ]; then
    MAVEN_HOME="$BASE_DIR/tools/apache-maven-3.9.6"
    exec "$MAVEN_HOME/bin/mvn" "$@"
elif command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
else
    echo "Error: Maven command not found." >&2
    exit 1
fi
