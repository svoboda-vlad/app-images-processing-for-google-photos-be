# Images Processing For Google Photos (app-images-processing-for-google-photos-be)

## Application hosted on Heroku + GitHub pages

**Front-end (based on Angular) hosted on GitHub pages**

[https://svoboda-vlad.github.io/app-images-processing-for-google-photos-fe](https://svoboda-vlad.github.io/app-images-processing-for-google-photos-fe)

**Back-end with REST API hosted on Heroku**

Swagger (OpenAPI) interface of the application 

[https://images-proc-for-google-photos.herokuapp.com/swagger-ui.html](https://images-proc-for-google-photos.herokuapp.com/swagger-ui.html)

## Front-end - Registration of a new user and log in

** Front-end **

- creating a new account and log in:

Menu > User > User registration

- using an existing Google account and log in:

Menu > User > Log in > "Log In With Google" button

## Back-end - Registration of a new user and log in

using cURL command line tool

- creating a new account:

Step 1 - registration of a new user

```
curl -i https://images-proc-for-google-photos.herokuapp.com/user -d "{\"username\": \"test1\",\"password\": \"pass123\", \"givenName\": \"Test 1\", \"familyName\": \"Test 1\"}" -H "Content-Type: application/json"
```

Step 2 - log in and obtaining a JWT token

```
curl -i https://images-proc-for-google-photos.herokuapp.com/login -d "{\"username\": \"test1\", \"password\": \"pass123\"}"
```

JWT token in HTTP response header:

```
Authorization: Bearer abcdef
```

- using an existing Google account:

Step 1 - log in to Google account to obtain Google ID token (using "localhost:4200" redirect uri)

[https://accounts.google.com/o/oauth2/v2/auth/identifier?client_id=733460469950-rnm4b6pek82bfrnd8f5hf5esa5an0ikk.apps.googleusercontent.com&redirect_uri=http%3A%2F%2Flocalhost%3A4200%2Fgoogle-login&response_type=id_token%20token&scope=profile&nonce=abcdef&flowName=GeneralOAuthFlow](https://accounts.google.com/o/oauth2/v2/auth/identifier?client_id=733460469950-rnm4b6pek82bfrnd8f5hf5esa5an0ikk.apps.googleusercontent.com&redirect_uri=http%3A%2F%2Flocalhost%3A4200%2Fgoogle-login&response_type=id_token%20token&scope=profile&nonce=abcdef&flowName=GeneralOAuthFlow)

ID token returned in URL

http://localhost:4200/google-login#access_token=abcdef&token_type=Bearer&expires_in=3599&scope=profile%20https://www.googleapis.com/auth/userinfo.profile&id_token=abcdef

Step 2 - log in and obtaining a JWT token

```
curl -i https://images-proc-for-google-photos.herokuapp.com/google-login -d "{\"idToken\": \"abcdef\"}"
```

JWT token in HTTP response header:

```
Authorization: Bearer abcdef
```

## Back-end - Accessing restricted REST APIs using JWT token

GET /parameters REST API endpoint:

- using cURL tool:

```
curl -i https://images-proc-for-google-photos.herokuapp.com/parameters -H "Authorization: Bearer abcdef"
```

data in JSON format returned in HTTP response body:

```
{"timeDiffGroup":1800,"resizeWidth":1000,"resizeHeight":1000}
```

- using Swagger (OpenAPI) interface of the application 

[https://images-proc-for-google-photos.herokuapp.com/swagger-ui.html](https://images-proc-for-google-photos.herokuapp.com/swagger-ui.html)

"Authorize" button > enter JWT token to "bearer-key  (http, Bearer)" field > "Authorize" button

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

ADMIN_PASSWORD=...


## Development on local machine

Software requirements

- Git
- OpenJDK 11
- Apache Maven
- Eclipse IDE
- for integration testing: PostgreSQL database

Download GitHub repository + open the folder

```
git clone https://github.com/svoboda-vlad/app-images-processing-for-google-photos-be.git
cd app-images-processing-for-google-photos-be
```

Build JAR file from the source code with Maven

```
sudo mvn clean install -Dhttps.protocols=TLSv1.2
```

Run the JAR file (application) with defining administrator account (username: admin, password: pass123)

```
sudo java -Dadmin.username=admin -Dadmin.password=pass123 -Dspring.profiles.active=dev,liquibase -jar target/*.jar
```

After JAR is running successfully, URLs are available:

Swagger (OpenAPI) interface of the application

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

H2 console for H2 database

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)


## REST API Authentication (using cURL)

Authentication using username + password:

username: admin, password: pass123

```
curl -i http://localhost:8080/login -d "{\"username\": \"admin\", \"password\": \"pass123\"}"
```

Returned JWT token:

```
Authorization: Bearer abcdef
```

Authentication using Google ID token (automatic registration of a new user):

```
curl -i http://localhost:8080/google-login -d "{\"idToken\": \"abcdef\"}"
```

Returned JWT token:

```
Authorization: Bearer abcdef
```


## Maven build

default "dev" profile - unit testing (mocked repositories)

```
sudo mvn clean package -Dhttps.protocols=TLSv1.2
```

default "dev" + "liquibase" profile - testing against H2 database

```
sudo mvn clean install -Dhttps.protocols=TLSv1.2
```

"integ" + "liquibase" profile - integration testing against PostgreSQL database

```
sudo mvn clean install -Dhttps.protocols=TLSv1.2 -Dspring.profiles.active=integ
```
## Running the application (compiled JAR file) + creating administrator account

default "dev" + "liquibase" profile - using H2 database

```
sudo java -Dadmin.username=admin -Dadmin.password=pass123 -Dspring.profiles.active=dev,liquibase -jar target/*.jar
```

"prod" profile - using PostgreSQL database

```
sudo java -Dadmin.username=admin -Dadmin.password=pass123 -Dspring.profiles.active=prod -jar target/*.jar
```

## REST API endpoints - domain
http://localhost:8080/

Example of POST request with JWT token:

```
curl -i http://localhost:8080/currency-code -d "{\"id\":0,\"currencyCode\": \"EUR\",\"country\": \"EMU\",\"rateQty\":1}" -H "Content-Type: application/json" -H "Authorization: Bearer abcdef"
```

Response:

```
{"id":6,"currencyCode":"EUR","country":"EMU","rateQty":1}
```

restricted:
- GET + PUT "/parameters" (ProcessingParametersUserController)
- GET "/parameters-reset-to-default" (ProcessingParametersUserController)

## REST API endpoints - security + administration
unrestricted:
- POST "/login" (LoginFilter)
- POST "/google-login" (GoogleLoginFilter)
- GET + POST "/user" (UserController)

restricted:
- PUT + DELETE "/user" (UserController)

restricted (administrator):
- GET "/admin/users" (UserAdminController)
- GET + PUT "/admin/parameters-default" (ProcessingParametersDefaultController)

## Models - domain

ProcessingParametersDefault - id (long), timeDiffGroup (int, min = 60, max = 86400), resizeWidth (int, min = 1, max = 10000), resizeHeight (int, min = 1, max = 10000)
- no endpoint

ProcessingParametersDefaultTemplate - timeDiffGroup (int, min = 60, max = 86400), resizeWidth (int, min = 1, max = 10000), resizeHeight (int, min = 1, max = 10000)
- GET "/admin/parameters-default": {"timeDiffGroup":1800,"resizeWidth":1000,"resizeHeight":1000}

ProcessingParametersUser - id (long), timeDiffGroup (int, min = 60, max = 86400), resizeWidth (int, min = 1, max = 10000), resizeHeight (int, min = 1, max = 10000), user (User)
- no endpoint

ProcessingParametersUserTemplate - timeDiffGroup (int, min = 60, max = 86400), resizeWidth (int, min = 1, max = 10000), resizeHeight (int, min = 1, max = 10000)
- GET "/parameters": {"timeDiffGroup":1800,"resizeWidth":1000,"resizeHeight":1000}

## Models - security

User - id (long), username (String, min = 1, max = 255), password (String, min = 60, max = 60), lastLoginDateTime (LocalDateTime), previousLoginDateTime (LocalDateTime), loginProvider (LoginProvider - enum - INTERNAL, GOOGLE), givenName (String, min = 1, max = 255), familyName (String, min = 1, max = 255)
- no endpoint
- parsed from endpoint POST "/login"

UserInfo - username (String, min = 1, max = 255), lastLoginDateTime (LocalDateTime), previousLoginDateTime (LocalDateTime), givenName (String, min = 1, max = 255), familyName (String, min = 1, max = 255)
- GET "/current-user": {"username": "user1","givenName": "User 1","familyName": "User 1","lastLoginDateTime": "2021-05-05T12:50:12.354751","previousLoginDateTime": "2021-05-05T12:50:12.354751","userRoles":[{"role":{"id":1,"name":"ROLE_USER"}}]}
- GET "/admin/users": [{"username":"user2","givenName":"User 2","familyName":"User 2","lastLoginDateTime": "2021-07-27T08:08:50.759683","previousLoginDateTime": "2021-07-27T08:08:50.759683","userRoles":[{"role":{"id":1,"name":"ROLE_USER"}}]},{"username":"user1","givenName":"User 1","familyName":"User 1","lastLoginDateTime": "2021-07-27T08:08:50.759683","previousLoginDateTime": "2021-07-27T08:08:50.759683","userRoles":[{"role":{"id":1,"name":"ROLE_USER"}},{"role":{"id":2,"name":"ROLE_ADMIN"}}]}]
- POST "/update-user": {"username": "user1","givenName": "User 1","familyName": "User 1"}

UserRegister - username (String, min = 1, max = 255), password (String, min = 4, max = 100)
- POST "/register": {"username": "test","password": "test123", "givenName": "Test", "familyName": "Test"}

GoogleIdTokenEntity - idToken (String, min = 1, max = 2048)
- no endpoint
- parsed from endpoint POST "/google-login"

Role
- no endpoint - id (long), name (String, min = 1, max = 255)

UserRoles
- no endpoint - id (long), user (User), role (Role)

## Database

H2 in-memory database + liquibase

JDBC URL: "jdbc:h2:mem:testdb"

Database tables - domain:
- processing_parameters_default - id (int PRIMARY KEY), time_diff_group (int NOT NULL), resize_width (int NOT NULL), resize_height (int NOT NULL)
- processing_parameters_user - id (int PRIMARY KEY), time_diff_group (int NOT NULL), resize_width (int NOT NULL), resize_height (int NOT NULL), user_id (int NOT NULL)

Database tables - security:
- user - id (int PRIMARY KEY), username (VARCHAR(255) NOT NULL UNIQUE), password (VARCHAR(255) NOT NULL), last_login_date_time (TIMESTAMP), previous_login_date_time (TIMESTAMP), login_provider(VARCHAR(255), given_name(VARCHAR(255), family_name(VARCHAR(255))
- user_roles - user_id (int NOT NULL), role_id (int NOT NULL), user_id + role_id - PRIMARY KEY
- role - id (int PRIMARY KEY), name (VARCHAR(255) NOT NULL UNIQUE) - default values: "ROLE_USER", "ROLE_ADMIN"

## Authentication

UserDetailsService + BCryptPasswordEncoder

Login endpoints:

- POST "/login"

- POST "/google-login"

SessionCreationPolicy.STATELESS

## Dependencies

compile scope (default):
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- jjwt-api
- liquibase-core
- jaxb-api
- springdoc-openapi-ui
- springdoc-openapi-security
- google-api-client

provided scope:
- lombok

test scope:
- spring-boot-starter-test
- spring-security-test

runtime scope:
- jjwt-impl
- jjwt-jackson
- h2
- postgresql

## Properties
DEFAULT:

spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

spring.liquibase.enabled=true

spring.profiles.active=dev

google.client.clientids=...

spring.liquibase.enabled=false

DEV:

spring.h2.console.enabled=true

spring.h2.console.settings.web-allow-others=true

spring.datasource.generate-unique-name=false

LIQUIBASE:

spring.liquibase.enabled=true

INTEG:

spring.datasource.url=jdbc:postgresql://localhost:5432/homestead

spring.datasource.username=homestead

spring.datasource.password=secret

PROD:

spring.h2.console.enabled=false

spring.liquibase.enabled=true


## PostgreSQL within Homestead Vagrant box
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