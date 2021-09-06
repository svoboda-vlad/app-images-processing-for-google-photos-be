# Images Processing For Google Photos (app-images-processing-for-google-photos-be)

## REST API Authentication

cURL (WINDOWS + LINUX) - username + password login:

```
curl -i http://localhost:8080/login -d "{\"username\": \"user1\", \"password\": \"pass123\"}"
```

cURL (WINDOWS + LINUX) - Google ID token login + automatic registration of a new user:

```
curl -i http://localhost:8080/google-login -d "{\"idToken\": \"abcdef\"}"
```

Returned JWT token:

```
Authorization: Bearer abcdef
```

## REST API endpoints - domain
http://localhost:8080/

Example of POST request with JWT token:

cURL (WINDOWS + LINUX)

```
curl -i http://localhost:8080/currency-code -d "{\"id\":0,\"currencyCode\": \"EUR\",\"country\": \"EMU\",\"rateQty\":1}" -H "Content-Type: application/json" -H "Authorization: Bearer abcdef"
```

Response:

```
{"id":6,"currencyCode":"EUR","country":"EMU","rateQty":1}
```

restricted (administrator):
- GET "/admin/parameters-default" (ProcessingParametersDefaultController)

## REST API endpoints - security + administration
unrestricted:
- POST "/login" (LoginFilter)
- POST "/google-login" (GoogleLoginFilter)
- GET + POST "/user" (UserController)

unrestricted, but not REST API:
- GET "/h2-console/**"
- GET "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"

restricted:
- PUT + DELETE "/user" (UserController)
- GET + PUT "/parameters" (ProcessingParametersUserController)

restricted (administrator):
- GET "/admin/users" (UserAdminController)
- GET + PUT "/admin/parameters-default" (ProcessingParametersDefaultController)

Swagger / OpenAPI

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Heroku: [https://images-proc-for-google-photos.herokuapp.com/swagger-ui.html](https://images-proc-for-google-photos.herokuapp.com/swagger-ui.html)

H2 console

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Heroku: [https://images-proc-for-google-photos.herokuapp.com/h2-console](https://images-proc-for-google-photos.herokuapp.com/h2-console)


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

{"username": "user1", "password": "pass123"}

- POST "/google-login"

{"idToken": "abcdef"}

SessionCreationPolicy.STATELESS

## Registration

- POST "/register"

{"username": "test", "password": "test", "givenName": "Test", "familyName": "Test"}

cURL (WINDOWS + LINUX):

```
curl -i http://localhost:8080/register -d "{\"username\": \"test\", \"password\": \"test123\", \"givenName\": \"Test\", \"familyName\": \"Test\"}" -H "Content-Type: application/json"
```

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

spring.profiles.active=dev

google.client.clientids=...

DEV:

spring.h2.console.enabled=true

spring.h2.console.settings.web-allow-others=true

spring.datasource.generate-unique-name=false

INTEG:

spring.datasource.url=jdbc:postgresql://localhost:5432/homestead

spring.datasource.username=homestead

spring.datasource.password=secret

PROD:

spring.h2.console.enabled=false

## Heroku Config Vars

SPRING_PROFILES_ACTIVE=prod

DATABASE_URL=...

ADMIN_USERNAME=...

ADMIN_PASSWORD=...

## Maven build

default "dev" profile

```
sudo mvn clean install -Dhttps.protocols=TLSv1.2
```

"integration" profile - integration testing

```
sudo mvn clean install -Dhttps.protocols=TLSv1.2 -Dspring.profiles.active=integ
```
## Administrator account

```
sudo java -Dadmin.username=admin -Dadmin.password=admin123 -jar target/*.jar
```