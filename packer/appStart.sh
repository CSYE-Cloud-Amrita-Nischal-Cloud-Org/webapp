#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto
export PATH=$PATH:$JAVA_HOME/bin

java -Ddb.username="$DB_USERNAME" -Ddb.password="$DB_PASSWORD" -Ddb.url="$DB_URL" -jar /opt/webapp/app.jar
