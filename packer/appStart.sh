#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto
export PATH=$PATH:$JAVA_HOME/bin

# shellcheck source=/dev/null
source /opt/webapp/db_creds.sh

java -Ddb.username="$DB_USERNAME" -Ddb.password="$DB_PASSWORD" -jar /opt/webapp/app.jar
