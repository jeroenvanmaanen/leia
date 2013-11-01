#!/bin/bash

set -e

if [ ".$1" = '.-v' ]
then
    shift
    set -x
fi

QUALIFIERS=''
if [ ".$1" = '.-q' ]
then
    shift
    QUALIFIERS="$1"
    shift
fi

if [ \! -d "$1" ]
then
    echo "Directory not found: [$1]" >&2
    exit 1
fi
DIR="$1"
shift

if [ -n "${QUALIFIERS}" ]
then
    for Q in ${QUALIFIERS}
    do
        find "${DIR}" \( -type d -name .svn -prune -type f \) -o -type f \( -name "*-sample+${Q}" -o -name "*-sample+${Q}.*" \) -print
    done
fi

find "${DIR}" \( -type d -name .svn -prune -type f \) -o -type f \( -name "*-sample" -o -name "*-sample.*" \) -print
