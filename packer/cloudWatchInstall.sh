#!/bin/bash

sudo wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb

sudo dpkg -i -E ./amazon-cloudwatch-agent.deb

sudo apt-get update && sudo apt-get -y install collectd

sudo touch /opt/webapp/logs/webapp.log

sudo chmod 755 /opt/webapp/logs/webapp.log

sudo chown csye6225:csye6225 /tmp/cloudwatch-config.json

sudo mv /tmp/cloudwatch-config.json /opt/aws/amazon-cloudwatch-agent/bin