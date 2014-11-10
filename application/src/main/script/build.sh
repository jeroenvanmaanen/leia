#!/bin/bash

SCRIPT="$(cd "$(dirname "$0")" ; pwd)"
MAIN="$(dirname "$SCRIPT")"
SRC="$(dirname "$MAIN")"
PROJECT="$(dirname "$SRC")"

cd "$PROJECT"
mvn "$@" clean package assembly:single
