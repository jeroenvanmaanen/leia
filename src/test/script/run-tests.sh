#!/bin/bash

set -e

N=0
while [ "${N}" -lt '45' ]
do
    echo ''
    N=$[${N} + 1]
done

SILENT=true
if [ ".$1" = '.-v' ]
then
    SILENT='false'
    shift
    if [ ".$1" = '.-v' ]
    then
        set -x
        shift
    fi
fi

KEEP_DB='false'
if [ ".$1" = '.-k' ]
then
    KEEP_DB='true'
fi

OFFLINE=''
if [ ".$1" = '.-o' ]
then
    OFFLINE='-o'
    shift
fi

ITERATIONS=2
if [ ".$1" = '.-n' ]
then
    ITERATIONS="$2"
    shift
    shift
fi

SINGLE_TEST='org.leialearns.utilities.TestUtilities'
if [ ".$1" = '.-t' ]
then
    SINGLE_TEST="$2"
    shift
    shift
fi

FINAL_TEST=''
if [ ".$1" = '.-f' ]
then
    FINAL_TEST="$2"
    shift
    shift
fi

SCRIPT="$(cd "$(dirname "$0")" ; pwd)"
TEST="$(dirname "${SCRIPT}")"
SRC="$(dirname "${TEST}")"
PROJECT="$(dirname "${SRC}")"
LOG_DIR="${PROJECT}/target/log"

. "${SCRIPT}/functions.sh"

cd "${PROJECT}"

DB_HOST=''
DB_SCHEMA=''
hibernate_connection_username=''
hibernate_connection_password=''
set-datasource "${SILENT}" src/test

if "${KEEP_DB}"
then
    :
else
    "${SCRIPT}/recreate-db.sh"
fi

DB_DUMP="${PROJECT}/data/leiatest-dump-empty.sql"
"${SILENT}" || echo "DB_DUMP=[${DB_DUMP}]" >&2

CAT_DUMP="cat '${DB_DUMP}'"
if [ -f "${DB_DUMP}.gz" ]
then
    CAT_DUMP="gzip -dc '${DB_DUMP}.gz'"
fi
"${SILENT}" || echo "CAT_DUMP=[${CAT_DUMP}]" >&2

eval "${CAT_DUMP}" | \
    mysql -h "${DB_HOST}" -u "${hibernate_connection_username}" -p"${hibernate_connection_password}" "${DB_SCHEMA}"

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
    DB_DUMP_TARGET="${PROJECT}/target/leiatest-dump-empty.sql"
    mysqldump -h "${DB_HOST}" -u "${hibernate_connection_username}" -p"${hibernate_connection_password}" "${DB_SCHEMA}" \
        | sed \
            -e '/INSERT/d' \
            -e 's/ AUTO_INCREMENT=[0-9]* / /' \
            -e '$s/^-- Dump completed on [-:0-9 ]*$/--/' \
        > "${DB_DUMP_TARGET}"
    wc -l "${DB_DUMP_TARGET}"
    if eval "${CAT_DUMP}" | cmp -s - "${DB_DUMP_TARGET}"
    then
        echo ">>> No database changes" >&2
    else
        eval "${CAT_DUMP}" | diff -u - "${DB_DUMP_TARGET}" | cut -c -150
    fi
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
