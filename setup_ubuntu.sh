#!/bin/sh

# Navigate to tmp folder
cd /tmp || exit

# Install Java
wget -O - https://apt.corretto.aws/corretto.key | sudo gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg && \
echo "deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main" | sudo tee /etc/apt/sources.list.d/corretto.list

sudo apt update -y; sudo apt install -y java-21-amazon-corretto-jdk

# Verify Java
java -version

# Update ubuntu
apt update -y

# Install PostgreSQL
apt install -y postgresql postgresql-contrib

# Restart PostgreSQL
sudo systemctl restart postgresql.service

printf "\nEnter postgres user 'csye' password:\n\n"
sudo -u postgres createuser csye -P --superuser

#export JAVA_HOME=$(readlink -f `which javac` | sed "s:/bin/javac::")
echo "export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto" >> ~/.bashrc
echo "export PATH=$PATH:$JAVA_HOME/bin" >> ~/.bashrc

# Create database
sudo -u postgres psql -c 'create database app_db;'

# Restart shell
exec $SHELL -l