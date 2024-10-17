#!/bin/bash

# Install PostgreSQL
sudo apt-get install -y postgresql postgresql-contrib

# Restart PostgreSQL
sudo systemctl restart postgresql.service

sudo -u postgres psql -c "CREATE USER csye6225 WITH SUPERUSER PASSWORD 'qwertyuiop';"

# Create database
sudo -u postgres psql -c 'create database app_db;'