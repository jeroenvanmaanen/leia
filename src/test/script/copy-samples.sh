#!/bin/bash

set -e

SED_EXT='-r'
case $(uname) in
Darwin*)
        SED_EXT='-E'
esac
export SED_EXT

SILENT='true'
if [ ".$1" = '.-v' ]
then
    shift
    SILENT='false'
fi

"${SILENT}" || echo "Copy samples..." >&2

LINK='false'
if [ ".$1" = '.-l' ]
then
    shift
    LINK='true'
fi

while read F
do
    G="$(echo "${F}" | sed "${SED_EXT}" -e 's/-sample([+][^.]*)?//')"
    if [ -e "${G}" ]
    then
        "${SILENT}" || echo ">>> Skip: F=[${F}]" >&2
    else
        if "${LINK}"
        then
            echo ">>> Link: [${F}] => [${G}]" >&2
            (
                cd "$(dirname "${F}")"
                ln -nsf "$(basename "${F}")" "$(basename "${G}")"
            )
        else
            echo ">>> Copy: [${F}] => [${G}]" >&2
            cp -n "${F}" "${G}"
        fi
    fi
done
