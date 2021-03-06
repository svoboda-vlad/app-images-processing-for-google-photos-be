# Images Processing For Google Photos (app-images-processing-for-google-photos-be)


This repository contains only backend of the application providing a REST API. There is no UI.

The [app-images-processing-for-google-photos-fe](https://github.com/svoboda-vlad/app-images-processing-for-google-photos-fe) project is a Angular front-end application which consumes the REST API.

## Live demo

**Front-end based on Angular (hosted on GitHub pages)**

[https://svoboda-vlad.github.io/app-images-processing-for-google-photos-fe](https://svoboda-vlad.github.io/app-images-processing-for-google-photos-fe)

**Back-end based on Spring Boot with REST API (hosted on Heroku)**

Swagger UI

[https://processing-gphotos.herokuapp.com/swagger-ui.html](https://processing-gphotos.herokuapp.com/swagger-ui.html)

## Front-end - Registration of a new user and log in

- using an existing Google account and log in:

Menu > User > Log in > "Log In With Google" button

## Back-end - Registration of a new user and log in

- using an existing Google account:

Step 1 - Log in to Google account to obtain Google ID token (using allowed redirect uri "localhost:4200")

[https://accounts.google.com/o/oauth2/v2/auth/identifier?client_id=733460469950-rnm4b6pek82bfrnd8f5hf5esa5an0ikk.apps.googleusercontent.com&redirect_uri=http%3A%2F%2Flocalhost%3A4200%2Fgoogle-login&response_type=id_token%20token&scope=profile&nonce=abcdef&flowName=GeneralOAuthFlow](https://accounts.google.com/o/oauth2/v2/auth/identifier?client_id=733460469950-rnm4b6pek82bfrnd8f5hf5esa5an0ikk.apps.googleusercontent.com&redirect_uri=http%3A%2F%2Flocalhost%3A4200%2Fgoogle-login&response_type=id_token%20token&scope=profile&nonce=abcdef&flowName=GeneralOAuthFlow)

ID token returned in URL fragment identifier (after hash mark #)

http://localhost:4200/google-login#access_token=uvwxyz&token_type=Bearer&expires_in=3599&scope=profile%20https://www.googleapis.com/auth/userinfo.profile&id_token=abcdef

Step 2 - Using ID token in HTTP authorization header

```
Authorization: Bearer abcdef
```

## Back-end - Accessing restricted REST APIs using JWT token

GET /parameters REST API endpoint:

- using cURL tool:

```
curl -i https://processing-gphotos.herokuapp.com/parameters -H "Authorization: Bearer abcdef"
```

data in JSON format returned in HTTP response body:

```
{"timeDiffGroup":1800,"resizeWidth":1000,"resizeHeight":1000}
```

- using Swagger (OpenAPI) interface of the application 

[https://processing-gphotos.herokuapp.com/swagger-ui.html](https://processing-gphotos.herokuapp.com/swagger-ui.html)

"Authorize" button > enter ID token to "bearer-key  (http, Bearer)" field > "Authorize" button

processing-parameters-user-controller
GET /parameters > "Try it out" button" > "Execute" button

Server response - response body:

```
{
  "timeDiffGroup": 1800,
  "resizeWidth": 1000,
  "resizeHeight": 1000
}
```

## Heroku configuration details

Application deployed on a free dyno (512MB RAM, sleeps after 30 mins of inactivity) with Heroku Postgres add-on Hobby Dev (max. 10K rows).

Config vars:

SPRING_PROFILES_ACTIVE=prod

DATABASE_URL=...

ADMIN_USERNAME=...

## Development

Software requirements

- Git
- OpenJDK 11
- Apache Maven
- Eclipse IDE
- for integration testing: PostgreSQL database

## Running the application locally

Download GitHub repository + open the folder

```
git clone https://github.com/svoboda-vlad/app-images-processing-for-google-photos-be.git
cd app-images-processing-for-google-photos-be
```

Build JAR file from the source code with Maven (without running tests)

```
mvn clean install -DskipTests
(sudo mvn clean install -Dhttps.protocols=TLSv1.2 -DskipTests)
```

Run the JAR file (application) with defining administrator account (Google sub: admin) and Spring profiles (dev) using in-memory H2 database

```
java -D"admin.username=admin" -jar target/images-processing-0.0.1-SNAPSHOT.jar
(sudo java -Dadmin.username=admin -jar target/*.jar)
```

After JAR is running successfully, URLs are available:

Swagger UI

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

H2 database console

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)


## Maven build - Spring profiles

Default "dev" profile - unit testing (mocked repositories)

```
mvn clean package
(sudo mvn clean package -Dhttps.protocols=TLSv1.2)
```

Default "dev" profile - integration testing against H2 database

```
mvn clean install
(sudo mvn clean install -Dhttps.protocols=TLSv1.2)
```

"integ" profile - integration testing against PostgreSQL database (see details in section PostgreSQL within Homestead Vagrant box)

```
mvn clean install -D"spring.profiles.active=integ"
sudo mvn clean install -Dhttps.protocols=TLSv1.2 -Dspring.profiles.active=integ
```

## REST API endpoints

All REST API endpoints documentation available in Swagger UI after running the application:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Example of PUT request with JWT token:

```
curl -i -X PUT http://localhost:8080/parameters -d "{\"timeDiffGroup\": 7200,\"resizeWidth\": 1000,\"resizeHeight\": 1000}" -H "Content-Type: application/json" -H "Authorization: Bearer abcdef"
```

Response:

```
{"timeDiffGroup":7200,"resizeWidth":1000,"resizeHeight":1000}
```

**REST API endpoints - domain**

restricted:
- GET + PUT "/parameters" (ProcessingParametersUserController)
- GET "/parameters-reset-to-default" (ProcessingParametersUserController)
- GET "/last-upload-info" (LastUploadInfoController)
- GET "/last-upload-info-update" (LastUploadInfoController)

**REST API endpoints - security + administration**

restricted:
- GET + DELETE "/user" (UserController)

restricted (administrator):
- GET "/admin/users" (UserAdminController)
- GET + PUT "/admin/parameters-default" (ProcessingParametersDefaultController)

## Models

**JPA Entities**

User - id (long), username (String, min = 1, max = 255), givenName (String, min = 1, max = 255), familyName (String, min = 1, max = 255), email (String, min = 1, max = 255)
- no endpoint

ProcessingParametersDefault - id (long), timeDiffGroup (int, min = 60, max = 86400), resizeWidth (int, min = 1, max = 10000), resizeHeight (int, min = 1, max = 10000)
- no endpoint

ProcessingParametersUser - id (long), timeDiffGroup (int, min = 60, max = 86400), resizeWidth (int, min = 1, max = 10000), resizeHeight (int, min = 1, max = 10000), user (User)
- no endpoint

LastUploadInfo - id (long), lastUploadDateTime (Instant), user (User)
- no endpoint

**DTOs**

ProcessingParametersDefaultTemplate - timeDiffGroup (int, min = 60, max = 86400), resizeWidth (int, min = 1, max = 10000), resizeHeight (int, min = 1, max = 10000)
- GET "/admin/parameters-default": {"timeDiffGroup":1800,"resizeWidth":1000,"resizeHeight":1000}
- PUT "/admin/parameters-default"

ProcessingParametersUserTemplate - timeDiffGroup (int, min = 60, max = 86400), resizeWidth (int, min = 1, max = 10000), resizeHeight (int, min = 1, max = 10000)
- GET "/parameters": {"timeDiffGroup":1800,"resizeWidth":1000,"resizeHeight":1000}
- PUT "/parameters"

UserTemplate - username (String, min = 1, max = 255), givenName (String, min = 1, max = 255), familyName (String, min = 1, max = 255), email (String, min = 1, max = 255)
- GET "/user": {"username": "user1","givenName": "User 1","familyName": "User 1","email": "user1@gmail.com"}
- GET "/admin/users": [{"username":"user2","givenName":"User 2","familyName":"User 2","email": "user2@gmail.com"},{"username":"user1","givenName":"User 1","familyName":"User 1","email": "user1@gmail.com"}]

LastUploadInfoTemplate - lastUploadDateTime (Instant)
- GET "/last-upload-info": {"lastUploadDateTime": "2022-06-12T15:10:21.952Z"}
- GET "/last-upload-info-update": {lastUploadDateTime": "2022-06-12T15:10:21.952Z"}

## Database

H2 in-memory database + liquibase

JDBC URL: "jdbc:h2:mem:testdb"

default schema: public

Database tables:
- user - id (int PRIMARY KEY), username (VARCHAR(255) NOT NULL UNIQUE), given_name (VARCHAR(255), family_name (VARCHAR(255)), email (VARCHAR(255))
- processing_parameters_default - id (int PRIMARY KEY), time_diff_group (int NOT NULL), resize_width (int NOT NULL), resize_height (int NOT NULL)
- processing_parameters_user - id (int PRIMARY KEY), time_diff_group (int NOT NULL), resize_width (int NOT NULL), resize_height (int NOT NULL), user_id (int NOT NULL)
- last_upload_info - id (int PRIMARY KEY), last_upload_date_time (TIMESTAMP), user_id (int NOT NULL)

## Authentication - Spring Security
OAuth 2.0 Resource Server

.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)

SessionCreationPolicy.STATELESS

JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
grantedAuthoritiesConverter.setAuthoritiesClaimName("sub");
grantedAuthoritiesConverter.setAuthorityPrefix("");

## Dependencies

compile scope (default):
- spring-boot-starter-web
- spring-boot-starter-oauth2-resource-server
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- liquibase-core
- springdoc-openapi-ui
- springdoc-openapi-security

provided scope:
- lombok

test scope:
- spring-boot-starter-test
- spring-security-test

runtime scope:
- h2
- postgresql

## Properties
DEFAULT:

spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

spring.liquibase.enabled=true

spring.profiles.active=dev

spring.jpa.hibernate.ddl-auto=none

spring.security.oauth2.resourceserver.jwt.issuer-uri=https://accounts.google.com

admin.username=admin

spring.jpa.properties.hibernate.default_schema=public

DEV:

spring.h2.console.enabled=true

spring.h2.console.settings.web-allow-others=true

spring.datasource.generate-unique-name=false

INTEG:

spring.datasource.url=jdbc:postgresql://localhost:5432/homestead

spring.datasource.username=homestead

spring.datasource.password=secret

NOLIQUIBASE:

spring.liquibase.enabled=false

PROD:

spring.h2.console.enabled=false

spring.liquibase.enabled=true


## PostgreSQL within Homestead Vagrant box

URL: jdbc:postgresql://localhost:5432/homestead

username: homestead

password: secret

SQL queries

```
psql -U homestead -h localhost -c '\x' -c 'SELECT * FROM mytable;'
```
Starting/stopping database

```
sudo service postgresql status
sudo service postgresql start
sudo service postgresql stop
```
PostgreSQL version

```
psql --version
```

Drop and create database

```
dropdb homestead -U homestead -h localhost
createdb homestead -U homestead -h localhost
```