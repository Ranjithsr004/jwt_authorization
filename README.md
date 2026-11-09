# Java JWT Authentication API

A Spring Boot REST API that uses JWT bearer tokens to authenticate users. User accounts and roles are stored in MySQL, and the protected API endpoint checks the roles included in the token.

## Requirements

- Java 17 or later
- MySQL
- Maven (or use the included Maven Wrapper)

## Configure MySQL

Create the database:

```sql
CREATE DATABASE security_demo;
```

The default connection settings are in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/security_demo
spring.datasource.username=root
spring.datasource.password=1234
```

Change the username and password to match your local MySQL setup. Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update` and creates or updates the tables when the application starts. SQL logging is enabled.

The application expects the role records `ADMIN` and `USER` to exist before registering users. Add them to the `role` table, for example:

```sql
INSERT INTO role (name) VALUES ('ADMIN'), ('USER');
```

## Run

From the project root:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080` by default.

## API

### Register a user

`POST /auth/register`

```json
{
  "username": "alice",
  "password": "change-me",
  "roles": ["USER"]
}
```

The requested role names must already exist in the database. Passwords are stored using BCrypt.

### Log in

`POST /auth/login`

```json
{
  "username": "alice",
  "password": "change-me"
}
```

On successful login, the response body contains the JWT as plain text. Send it in the `Authorization` header on protected requests:

```text
Authorization: Bearer <token>
```

### Get protected data

`GET /api/protected-data`

Requires a valid, unexpired JWT containing the `ADMIN` or `USER` role. For example:

```bash
curl http://localhost:8080/api/protected-data \
  -H "Authorization: Bearer <token>"
```

The response greets the authenticated user and reports the role or roles in the token. Requests without a valid bearer token are not authenticated; a token without either accepted role is denied.

## Security notes

- The JWT signing key is currently generated in memory when the application starts. Tokens become invalid after a restart, and this key should be replaced with a securely configured persistent secret before deployment.
- Update the example database credentials and use external secret configuration for real deployments.
- Token expiration is configured in `JwtUtil` in milliseconds.

## Main components

- `AuthController` — registration and login endpoints
- `UserController` — protected data endpoint
- `JwtAthenticationFilter` — reads bearer tokens and establishes Spring Security authentication
- `JwtUtil` — creates, parses, and validates JWTs
- `CustomUserDetailsService` — loads users and their authorities from MySQL
- `SecurityConfig` — configures stateless request authorization
