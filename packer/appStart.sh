#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto
export PATH=$PATH:$JAVA_HOME/bin

java -Ddb.username="$DB_USERNAME" \
     -Ddb.password="$DB_PASSWORD" \
     -Ddb.url="$DB_URL" \
     -Daws.s3_bucket_name="$AWS_S3_BUCKET_NAME" \
     -Daws.sns_topic_name="$AWS_SNS_TOPIC_NAME" \
     -Daws.region="$AWS_REGION" -jar /opt/webapp/app.jar
