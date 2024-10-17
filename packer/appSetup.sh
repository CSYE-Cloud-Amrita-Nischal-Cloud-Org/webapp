#!/bin/bash

sudo mv /tmp/*.jar /opt/webapp/app.jar

sudo mv /tmp/app.service /etc/systemd/system/app.service

sudo mv /tmp/appStart.sh /opt/webapp/appStart.sh

sudo chmod +x /opt/webapp/appStart.sh

sudo chown -R csye6225:csye6225 /opt/webapp

{
echo "#!/bin/bash" 
echo "export DB_USERNAME=$DB_USERNAME"
echo "export DB_PASSWORD=$DB_PASSWORD"
} >> /tmp/db_creds.sh

sudo mv /tmp/db_creds.sh /opt/webapp/db_creds.sh

sudo systemctl daemon-reload
sudo systemctl enable app.service
