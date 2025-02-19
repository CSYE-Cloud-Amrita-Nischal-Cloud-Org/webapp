#!/bin/bash

sudo mv /tmp/*.jar /opt/webapp/app.jar

sudo mv /tmp/app.service /etc/systemd/system/app.service

sudo mv /tmp/appStart.sh /opt/webapp/appStart.sh

sudo chmod 755 /opt/webapp/appStart.sh

sudo chown -R csye6225:csye6225 /opt/webapp

sudo systemctl enable app.service
