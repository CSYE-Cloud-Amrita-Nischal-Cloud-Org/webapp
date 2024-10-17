#!/bin/bash

sudo mv /tmp/*.jar /opt/webapp/app.jar

sudo mv /tmp/app.service /etc/systemd/system/app.service

sudo mv /tmp/appStart.sh /opt/webapp/appStart.sh

sudo mv /tmp/dbStart.sh /opt/webapp/dbStart.sh

sudo chmod +x /opt/webapp/appStart.sh

sudo chmod +x /opt/webapp/dbStart.sh

sudo chown -R csye6225:csye6225 /opt/webapp

sudo systemctl daemon-reload
sudo systemctl enable app.service
