# Functions for shell scripts

if false; then SILENT='-'; PROJECT=''; fi

SED_EXT=-r
case $(uname) in
Darwin*)
        SED_EXT=-E
esac

function to-suffix() {
    if [ -n "$1" ]
    then
        echo "-$1"
    fi
}

function to-re-literal() {
    local RESULT="$(echo "$*" \
        | sed \
            -e 's/[*+?.^$\\]/\\\1/g')"
    "${SILENT}" || echo "To RE literal: [$1] => [${RESULT}]" >&2
    echo "${RESULT}"
}

function call-script() {
    local SCRIPT="$1"
    local NAME="$(basename "${SCRIPT}")"
    shift
    if [ \! -x "${SCRIPT}" ]
    then
        echo "Script not found: [${SCRIPT}]" >&2
        exit 1
    fi

    "${SILENT}" || echo "Running script: [${NAME}]" >&2
    if "${SCRIPT}" "$@"
    then
        :
    else
        RC="$?"
        echo "FAILED: [${NAME}]" >&2
        exit "${RC}"
    fi
}

function try-settings() {
    local RC='1'
    if [ -e "$1" ]
    then
        SETTINGS="$1"
    fi
}

function find-settings() {
    local VAGRANT_DIR="$1"
    local QUALIFIERS="$2"
    SETTINGS="${VAGRANT_DIR}/settings.sh"
    if [ \! -e "${SETTINGS}" ]
    then
        if [ -n "${QUALIFIERS}" ]
        then
            for QUALIFIER in ${QUALIFIERS}
            do
                try-settings "${VAGRANT_DIR}/settings-sample+${QUALIFIER}.sh"
                if [ -e "${SETTINGS}" ]
                then
                    break
                fi
            done
        fi
        if [ \! -e "${SETTINGS}" ]
        then
            SETTINGS='${VAGRANT_DIR}/settings-default.sh'
        fi
    fi
}

function check-installed () {
    local INSTALLED="$(apt-cache policy "$1" | sed -n -e 's/^ *Installed: *//p')"
    if [ ".${INSTALLED}" != '.(none)' ]
    then
        "${SILENT}" || echo "Installed: [$1]" >&2
        echo true
    else
        "${SILENT}" || echo "Not installed: [$1]" >&2
    fi
}

function get-datasource() {
    sed "${SED_EXT}" -n \
        -e '/^.*<property/b property' \
        -e 'b' \
        -e ':property' \
        -e 's/^(.*) name="([^"]*)"(.*)$/\2=\1\3/' \
        -e 's/^([^=]*=).* value="([^"]*)".*/\1'\''\2'\''/' \
        -e 's/^hibernate[.]([a-z0-9]*)[.]/hibernate_\1_/' \
        -e 's/^hibernate[.]/hibernate_/' \
        -e 'p' \
        "$1"
}

function set-datasource() {
    local SILENT="$1"
    local PART="$2"
    hibernate_connection_url=''
    hibernate_connection_username=''
    hibernate_connection_password=''
    "${SILENT}" || get-datasource "${PART}/resources/META-INF/persistence.xml" | sed -e '/password/s/=.*/='\''****'\''/'
    eval "$(get-datasource "${PART}/resources/META-INF/persistence.xml")"
    DB_HOST="$(expr "${hibernate_connection_url}" : 'jdbc:mysql://\([^/]*\)/.*')"
    DB_SCHEMA="$(expr "${hibernate_connection_url}" : '.*/\([^/]*\)')"
    "${SILENT}" || echo "DB=[${hibernate_connection_username}]@[${DB_HOST}].[${DB_SCHEMA}]" >&2
}

function install-packages () {
    local MISSING
    local N=1
    declare -a MISSING
    for P in "$@"
    do
        if [ -z "$(check-installed "${P}")" ]
        then
            MISSING[${N}]="${P}"
            N=$[${N}+1]
        fi
    done
    if [ -n "${MISSING[1]}" ]
    then
        echo "Install missing packages: [${MISSING[@]}]" >&2
        apt-get -q -y install --no-install-recommends "${MISSING[@]}" || true
        echo "Installed missing packages: [${MISSING[@]}]" >&2
    fi
}

MYSQL_ARG_N=0
declare -a MYSQL_ARGS
function add-mysql-args() {
    local ARG
    for ARG in "$@"
    do
        MYSQL_ARG_N=$[${MYSQL_ARG_N}+1]
        MYSQL_ARGS[${MYSQL_ARG_N}]="${ARG}"
    done
}
