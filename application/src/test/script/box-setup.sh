#!/bin/bash

set -ex

BIN="$(cd "$(dirname "$0")" ; pwd)"
TEST="$(dirname "${BIN}")"
SRC="$(dirname "${TEST}")"
PROJECT="$(dirname "${SRC}")"

SETTINGS="$1"
FQDN="$2"
BASE="$3"
QUALIFIERS="$4"

echo 'Setup...' >&2
echo "SETTINGS=[${SETTINGS}]" >&2

PROJECT_USER='www-data'
SILENT='true'
USE_LDAP='false'
LOCAL_PHP='php'
OWNER=''
SSL_PREFIX='/etc/apache2/ssl/DN'
RECREATE_DB='false'
if [ -f "${SETTINGS}" ]
then
    . "${SETTINGS}"
fi

echo "PROJECT=[${PROJECT}]  FQDN=[${FQDN}]  BASE=[${BASE}]  QUALIFIERS=[${QUALIFIERS}]  SILENT=[${SILENT}]" >&2

if "${SILENT}"
then
    :
else
    set -x
fi

echo "${FQDN}" > "${PROJECT}/src/test/vagrant/.fqdn"
echo "${QUALIFIERS}" > "${PROJECT}/src/test/vagrant/.qualifiers"

if [ -f "${BIN}/functions.sh" ]
then
    . "${BIN}/functions.sh"
fi

if [ -d '/vagrant_host_etc' ]
then
    (
        cd /etc
        ln -snf '/vagrant_host_etc/hosts' .
    )
fi

if [ -d '/etc/apt/sources.list.d' ]
then
    (
        cd '/etc/apt/sources.list.d'
        rm -f 'puppet.list' 'chef.list'
    )
fi

export DEBIAN_FRONTEND=noninteractive
apt-get -q update > /dev/null

LOG_FILE="${PROJECT}/target/logs/install-packages.log"
su - "${PROJECT_USER}" -c "mkdir -p '$(dirname "${LOG_FILE}")'"
echo "Logging output of 'install-packages' to [${LOG_FILE}]" >&2
su - "${PROJECT_USER}" -c ": > '${LOG_FILE}'"

install-packages ca-certificates-java \
    >> "${LOG_FILE}" 2>&1
install-packages \
        openjdk-7-jdk maven \
        subversion \
        apache2 \
        curl \
    >> "${LOG_FILE}" 2>&1

GROUP="$(id -gn vagrant 2>/dev/null || true)"
if [ -n "${GROUP}" ]
then
    adduser www-data "${GROUP}"
fi

if [ -e /etc/apache2/sites-available/site ]
then
    echo "Apache site configuration already exists. Skipping..." >&2
else
    for Q in ${QUALIFIERS} ''
    do
        SUFFIX="$(to-suffix "${Q}")"
        V_HOST="${PROJECT}/src/test/vagrant/vhost${SUFFIX}.conf"
        if [ -e "${V_HOST}" ]
        then
            HOST="$(echo "${FQDN}" | tr '.' '\012' | sed -e 1q)"
            DOMAIN="${FQDN#*.}"
            DOC_ROOT="${PROJECT}/target/site"
            echo "Configuring site: [${V_HOST}]: [${HOST}]: [${DOMAIN}]" >&2
            sed -e "s|\\\${HOST}|${HOST}|g" \
                -e "s|\\\${DOMAIN}|${DOMAIN}|g" \
                -e "s|\\\${DOC_ROOT}|${DOC_ROOT}|g" \
                "${V_HOST}" \
                > /etc/apache2/sites-available/site
            a2ensite site
            a2dissite default
            service apache2 reload
            break
        fi
    done
fi

PROJECT_USER_HOME="$(eval "echo ~${PROJECT_USER}")"
PROJECT_USER_GROUP="$(id -gn "${PROJECT_USER}")"
mkdir -p "${PROJECT_USER_HOME}/.m2"
chown -R "${PROJECT_USER}:${PROJECT_USER_GROUP}" "${PROJECT_USER_HOME}/.m2"
HOME="${PROJECT_USER_HOME}" USER="${PROJECT_USER}" sudo -u "${PROJECT_USER}" bash -l -c "id"
HOME="${PROJECT_USER_HOME}" USER="${PROJECT_USER}" sudo -u "${PROJECT_USER}" bash -l -c "id; cd '${PROJECT}'; ls -ld ~${PROJECT_USER}/.m2 pom.xml target ; mvn javadoc:javadoc || true"
