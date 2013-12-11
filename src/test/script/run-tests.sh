#!/bin/bash

set -e

N=0
while [ "${N}" -lt '45' ]
do
    echo ''
    N=$[${N} + 1]
done

function usage() {
    echo "Usage: $(basename "$0") [ -v | -k | -o | -i <iterations> | -t <single-test> | -f <final-test> ]" >&2
}

SILENT='true'
TRACE='false'
KEEP_DB='false'
OFFLINE=''
ITERATIONS='2'
SINGLE_TEST='org.leialearns.utilities.TestUtilities'
FINAL_TEST=''

while [ "$#" -gt 0 -a ".${1#-}" != ".$1" ]
do
    OPT="${1#-}"
    shift
    case "${OPT}" in
    v)  if "${SILENT}"
        then
            SILENT='false'
        else
            TRACE='true'
            set -x
        fi
        ;;
    k)  KEEP_DB='true'
        ;;
    o)  OFFLINE='-o'
        ;;
    n)  ITERATIONS="$1"
        shift
        ;;
    t)  SINGLE_TEST="$1"
        shift
        ;;
    f)  FINAL_TEST="$1"
        shift;;
    *)  echo "Unknown option: -${OPT}"
        usage
        ;;
    esac
done

SCRIPT="$(cd "$(dirname "$0")" ; pwd)"
TEST="$(dirname "${SCRIPT}")"
SRC="$(dirname "${TEST}")"
PROJECT="$(dirname "${SRC}")"
LOG_DIR="${PROJECT}/target/log"

. "${SCRIPT}/functions.sh"

cd "${PROJECT}"

ERROR_PATTERN='^[[](INFO|ERROR)[]] BUILD FAIL(URE|ED)'

function run-maven () {
    local LOG_FILE="$1"
    local RC='0'
    shift
    echo ">>> Run maven:" "$@" | tee -a "${LOG_FILE}"
    if egrep -s "${ERROR_PATTERN}" "${LOG_FILE}"
    then
        echo ">>> Skipped." | tee -a "${LOG_FILE}"
        RC='1'
    else
        mvn ${OFFLINE} "$@" 2>&1 | tee -a "${LOG_FILE}"
    fi
    return "${RC}"
}

LOG_FILE="${LOG_DIR}/mvn.log"

[ -d "${LOG_DIR}" ] || mkdir -p "${LOG_DIR}"
echo -n '' > "${LOG_FILE}"
run-maven "${LOG_FILE}" clean javadoc:javadoc 2>&1 | grep -v '^Generating '

"${SRC}/assemble/script/inject-favicon.sh"

[ -d "${LOG_DIR}" ] || mkdir -p "${LOG_DIR}"
echo -n '' > "${LOG_FILE}"
run-maven "${LOG_FILE}" -Dtest="${SINGLE_TEST}" test
while [ 1 -le "${ITERATIONS}" ]
do
    "${SILENT}" || echo "ITERATIONS=[${ITERATIONS}]" >&2
    if run-maven "${LOG_FILE}" test
    then
        :
    else
        break
    fi
    ITERATIONS="$[${ITERATIONS} - 1]"
done

if [ -n "${FINAL_TEST}" ]
then
    "${SILENT}" || echo "FINAL_TEST=[${FINAL_TEST}]" >&2
    run-maven "${LOG_FILE}" -Dtest="${FINAL_TEST}" test
fi

RC=0
if egrep -s "${ERROR_PATTERN}" "${LOG_FILE}"
then
    RC=1
else
    : # Dump database
fi

echo "Maven log: [${LOG_FILE}]" >&2
PATTERN='^[[]INFO[]] Surefire report directory: '
SUREFIRE_LOG="$(sed -n -e "/${PATTERN}/b found" -e b -e ':found' -e "s/${PATTERN}//" -e p -e q "${LOG_FILE}")"
if [ -n "${SUREFIRE_LOG}" -a -n "$(ls "${SUREFIRE_LOG}"/* 2>/dev/null)" ]
then
    wc -l "${SUREFIRE_LOG}"/*
fi
if [ -n "$(ls "${PROJECT}/target/log"/test-leia-* 2>/dev/null)" ]
then
    wc -l "${PROJECT}/target/log"/test-leia-*
fi

exit "${RC}"
