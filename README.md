## Technology Stack

### 1. Operating System
* Ubuntu
### 2. Programming Language
* Java 21
### 3. Relational Database
* PostgreSQL 14
### 4. Backend
* Spring Boot
* Gradle
* localstack (Only for local development)

## Build Instructions

Clone the repo.

### Setup DigitalOcean VM
Execute the setup script in root directory

`./setup_ubuntu.sh`

### Start PostgreSQL server
sudo systemctl restart postgresql.service

### Start LocalStack server (For development locally)
`docker run -p 4566:4566 -p 4571:4571 localstack/localstack`

and make sure all the aws resources are present in localstack.

Like s3 bucket is already created.

`aws --endpoint-url=http://localhost:4566 s3 mb s3://my-local-bucket`

Or Sns topic is present.

`aws --endpoint-url=http://localhost:4566 sns create-topic --name my-sns-topic`



### Start the backend server
Run the following command<br>
`./gradlew bootRun -Dspring.profiles.active=local --args='--db.username=<USERNAME> --db.password=<PASSWORD>'`<br>

Or<br>

- Build jar file
  - `./gradlew clean build`
- Navigate to libs folder after build
  - `cd build/libs`
- Run the following command
  - `java -jar -Dspring.profiles.active=local -Ddb.username=<USERNAME> -Ddb.password=<PASSWORD> -jar webapp-0.0.1-SNAPSHOT.jar`

