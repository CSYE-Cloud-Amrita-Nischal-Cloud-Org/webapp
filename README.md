## Technology Stack

### 1. Operating System
* Ubuntu
### 2. Programming Language
* Java 21
### 3. Relational Database
* PostgreSQL 16
### 4. Backend
* Spring Boot
* Gradle

## Build Instructions

### Setup DigitalOcean VM
Execute script in root directory

`./setup_ubuntu.sh`

### Start PostgreSQL server
sudo systemctl restart postgresql.service

### Start the backend server
Navigate to webapp folder<br>
`cd webapp`<br><br>
Run the following command<br>
`./gradlew bootRun --args='--db.username=<USERNAME> --db.password=<PASSWORD>'`

