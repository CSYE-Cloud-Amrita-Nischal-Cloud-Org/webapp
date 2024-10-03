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

Clone the repo.

### Setup DigitalOcean VM
Execute the setup script in root directory

`./setup_ubuntu.sh`

### Start PostgreSQL server
sudo systemctl restart postgresql.service

### Start the backend server
Run the following command<br>
`./gradlew bootRun --args='--db.username=<USERNAME> --db.password=<PASSWORD>'`<br>

Or<br>

- Build jar file
  - `./gradlew clean build`
- Navigate to libs folder after build
  - `cd build/libs`
- Run the following command
  - `java -jar -Ddb.username=<USERNAME> -Ddb.password=<PASSWORD> -jar webapp-0.0.1-SNAPSHOT.jar`

