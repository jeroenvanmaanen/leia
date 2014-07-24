# settings for Vagrant box

if false
then
    # Trick IDEA into believing that QUALIFIER is set
    QUALIFIER=''
fi

PROJECT='/vagrant_project'
PROJECT_USER='vagrant'
DUMP_FILE='/vagrant_data/dump.sql.gz'
Q="${QUALIFIER}"
QUALIFIERS='vagrant'
if [ -n "${Q}" ]
then
    QUALIFIERS="${QUALIFIERS}-${Q}"
fi
USE_LDAP='false'
DB_ROOT='root'
DB_ROOT_PASSWORD='rotor'
DB_ROOT_HAS_PASSWORD='true'
RECREATE_DB='update'
OPEN_TO_ANONYMOUS='true'
