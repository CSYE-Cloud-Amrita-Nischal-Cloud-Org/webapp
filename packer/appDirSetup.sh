#!/bin/bash

set -e

DIR="/opt/webapp"

sudo mkdir -p "${DIR}"

sudo useradd --system -s /usr/sbin/nologin csye6225
