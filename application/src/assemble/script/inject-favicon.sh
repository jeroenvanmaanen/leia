#!/bin/bash

set -e

SED_IN_PLACE='-i'
SED_EXT='-r'
case $(uname) in
Darwin*)
        SED_IN_PLACE=('-i' '')
        SED_EXT='-E'
esac

function usage() {
    echo "Usage: $(basename "$0") [ -v [ -v ] ] [ -- ]" >&2
}

SILENT='true'
TRACE='false'
if [ ".$1" = '.-v' ]
then
    shift
    SILENT='false'
    if [ ".$1" = '.-v' ]
    then
        shift
        TRACE='true'
        set -x
    fi
fi
if [ ".$1" = '.--' ]
then
    shift
fi

if [ "$#" -ne 0 ]
then
    usage
    exit 0
fi

SCRIPT="$(cd "$(dirname "$0")" ; pwd)"
ASSEMBLE="$(dirname "${SCRIPT}")"
SRC="$(dirname "${ASSEMBLE}")"
PROJECT="$(dirname "${SRC}")"
SITE="${PROJECT}/target/site"

echo "Injecting favicon icon in all HTML files under ${SITE}" >&2

find "${SITE}" -type f -name '*.html' -print0 \
    | xargs -0 \
        sed "${SED_EXT}" "${SED_IN_PLACE[@]}" \
            -e '/^[[:space:]]*<link([[:space:]][^>]*)?[[:space:]]rel="shortcut icon"([[:space:]][^>]*)?>[[:space:]]*$/d' \
            -e 's/<link([[:space:]][^>]*)?[[:space:]]rel="shortcut icon"([[:space:]][^>]*)?>//' \
            -e '/<\/head>/i\
                <link rel="shortcut icon" href="/assets/favicon.ico"/>'
