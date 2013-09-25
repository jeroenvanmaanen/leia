#!/bin/bash

set -e

BIN="$(cd "$(dirname "$0")" ; pwd)"

SCRIPTS='/vagrant_project/src/test/script'
SILENT='true'
COMPLETE='/etc/setup_complete'

. "${SCRIPTS}/functions.sh"

IP="$(ifconfig eth1 | sed -n -e 's/^.*inet /inet:/' -e 's/ .*//' -e 's/addr://' -e 's/^inet://p')"
echo "IP=[${IP}]" >&2

"${SILENT}" || host -r "${IP}" \
    | sed -n \
        -e 's/[.]$//' \
        -e 's/[.]/ /g' \
        -e 's/^[ 0-9]* in-addr arpa domain name pointer //p' \
    | LANG=C sort

FQDN="$(
host -r "${IP}" \
    | sed -n \
        -e 's/[.]$//' \
        -e 's/[.]/ /g' \
        -e 's/^[ 0-9]* in-addr arpa domain name pointer //p' \
    | LANG=C sort \
    | sed -e 's/ /./g' -e 1q
)"
if [ -z "${FQDN}" ]
then
    IP_PATTERN="$(echo -e "^${IP}[ \\t][ \\t]*" | sed -e 's/[.]/[.]/g')"
    FQDN="$(
sed -n "s/${IP_PATTERN}//p" /vagrant_host_etc/hosts \
    | tr ' \011' '\012\012' \
    | sort \
    | sed -e 1q
)"
fi
NAME="$(echo "${FQDN}" | tr '.' '\012' | head -1)"
BASE="$(expr "${NAME}-" : '\([^-]*\)-.*')"
QUALIFIER="$(expr "${NAME}" : '[^-]*-\(.*\)' || true)"
QUALIFIERS='vagrant'
if [ -n "${QUALIFIER}" ]
then
    QUALIFIERS="vagrant_${QUALIFIER} ${QUALIFIER} ${QUALIFIERS}"
fi
echo "FQDN=[${FQDN}]  NAME=[${NAME}]  BASE=[${BASE}]  QUALIFIERS=[${QUALIFIERS}]" >&2

SETTINGS=''
find-settings '/vagrant' "${QUALIFIERS}"
"${SILENT}" || echo "SETTINGS=[${SETTINGS}]" >&2

OWNER='vagrant'
if [ -f "${SETTINGS}" ]
then
    . "${SETTINGS}"
fi

if [ -n "${OWNER}" ]
then
    OWNER_KEY='/vagrant_owner_ssh/id_rsa'
    OWNER_HOME="$(grep "^${OWNER}:" /etc/passwd | cut -d: -f 6)"
    SSH_KEY="${OWNER_HOME}/.ssh/id_rsa"
    if [ -e "${OWNER_KEY}" ] || [ \! -e "${SSH_KEY}" ]
    then
        SSH_DIR="$(dirname "${SSH_KEY}")"
        mkdir -p "${SSH_DIR}"
        ln -sfn "${OWNER_KEY}" "${SSH_KEY}"
        chmod -R go= "${SSH_DIR}"
    fi
fi

RUN_PROVISION='true'
if [ \! -f "${SCRIPTS}/always_run_provisioning" ]
then
    if [ -f "${COMPLETE}" ]
    then
        RUN_PROVISION='false'
        echo "This setup for this machine is already complete" >&2
    fi
fi

if "${RUN_PROVISION}"
then
    call-script "${SCRIPTS}/box-setup.sh" "${SETTINGS}" "${FQDN}" "${BASE}" "${QUALIFIERS}" \
        && touch "${COMPLETE}"
fi
