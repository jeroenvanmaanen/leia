#!/bin/bash

set -e

SCRIPT="$(cd "$(dirname "$0")" ; pwd)"

SILENT='true'
if [ ".$1" = '.-v' ]
then
    SILENT='false'
    shift
fi

CHUNK_SIZE='100'

FILE="$1"
FILE="$(readlink -m "${FILE}")"
"${SILENT}" || echo "FILE=[${FILE}]"

LENGTH="$(wc -l "${FILE}" | sed -e 's/[^0-9].*//')"
"${SILENT}" || echo "LENGTH=[${LENGTH}]"

N=0
while [ "${N}" -lt "${LENGTH}" ]
do
    "${SILENT}" || echo "Encounter: [${N}] (of ${LENGTH})"
    "${SCRIPT}/leia.sh" encounter -s "${N}" -l "${CHUNK_SIZE}" "file://${FILE}"

    "${SILENT}" || echo "Observer"
    "${SCRIPT}/leia.sh" observer

    "${SILENT}" || echo "Minimizer"
    "${SCRIPT}/leia.sh" minimizer

    "${SILENT}" || echo "Consolidator"
    "${SCRIPT}/leia.sh" consolidator

    N=$[${N}+${CHUNK_SIZE}]
done
