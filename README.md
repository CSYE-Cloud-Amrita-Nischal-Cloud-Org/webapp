## Technology Stack

### 1. Operating System
* MacOS
### 2. Programming Language
* Java 21
### 3. Relational Database
* PostgreSQL 14 via homebrew
### 4. Backend
* Spring Boot
* Gradle

## Build Instructions

### Start PostgreSQL server
`brew services start postgresql@14`

### Start the backend server
Navigate to webapp folder<br>
`cd webapp`<br><br>
Run the following command<br>
`./gradlew bootRun`

