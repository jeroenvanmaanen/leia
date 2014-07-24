#!/bin/bash

set -e

SILENT=true

BIN="$(cd "$(dirname "$0")" ; pwd)"

## exec > "/tmp/vagrant-up-$$-$(date '+%Y%m%d').log" 2>&1

echo "Running command: vagrant up" >&2

date
"${SILENT}" || type -a vagrant

"${SILENT}" || echo "BIN=${BIN}"
cd "${BIN}/../../.."
"${SILENT}" || pwd

vagrant up

read -e -p 'Press any key to continue... ' -n 1
