#!/bin/bash

set -e

BIN="$(cd "$(dirname "$0")" ; pwd)"
echo "BIN=[${BIN}]"

. "${BIN}/functions.sh"

DB_PROTOCOL='TCP'
DB_ROOT=root
DB_ROOT_HAS_PASSWORD='false'
DB_ROOT_PASSWORD=''

if [ -e "${BIN}/settings.sh" ]
then
    . "${BIN}/settings.sh"
fi

DB_HOST=''
DB_SCHEMA=''
hibernate_connection_username=''
hibernate_connection_password=''

set-datasource false "$(dirname "${BIN}")"

typeset -a MYSQL_ARGS
N=1
function add-args {
    for ARG in "$@"
    do
        MYSQL_ARGS[${N}]="${ARG}"
        N=$[${N}+1]
    done
}

add-args --protocol "${DB_PROTOCOL}"
add-args --host "${DB_HOST}"
add-args --user "${DB_ROOT}"
if "${DB_ROOT_HAS_PASSWORD}"
then
    add-args "-p${DB_ROOT_PASSWORD}"
fi
echo "N=[${N}]"

echo mysql "${MYSQL_ARGS[@]}" "${DB_SCHEMA}"
mysql "${MYSQL_ARGS[@]}" <<EOT
drop database if exists ${DB_SCHEMA};
create database ${DB_SCHEMA} default charset = 'ucs2';
grant all on ${DB_SCHEMA}.* to '${hibernate_connection_username}'@'%' identified by '${hibernate_connection_password}';
grant all on ${DB_SCHEMA}.* to '${hibernate_connection_username}'@'localhost' identified by '${hibernate_connection_password}';

connect ${DB_SCHEMA};

show tables;

select 'Databases:';
show databases;
EOT
