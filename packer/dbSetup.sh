#!/bin/bash

# Install PostgreSQL
sudo apt-get install -y postgresql postgresql-contrib

# Restart PostgreSQL
sudo systemctl restart postgresql.service

sudo -u postgres psql -c "CREATE USER ${DB_USERNAME} WITH SUPERUSER PASSWORD '${DB_PASSWORD}';"

# Create database
sudo -u postgres psql -c 'create database app_db;'
