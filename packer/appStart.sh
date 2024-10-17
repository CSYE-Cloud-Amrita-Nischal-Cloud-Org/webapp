#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto
export PATH=$PATH:$JAVA_HOME/bin
export DB_USERNAME=csye6225
export DB_PASSWORD=qwertyuiop

java -Ddb.username=csye6225 -Ddb.password=qwertyuiop -jar /opt/webapp/app.jar
