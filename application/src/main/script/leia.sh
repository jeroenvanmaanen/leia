#!/bin/bash

SCRIPT="$(cd "$(dirname "$0")" ; pwd)"
MAIN="$(dirname "${SCRIPT}")"
SRC="$(dirname "${MAIN}")"
PROJECT="$(dirname "${SRC}")"
TARGET="${PROJECT}/target"
LOG="${TARGET}/log"

java -Xmx1024m -jar "${TARGET}/leia-exe.jar" -l "${LOG}" "$@"
