# Task Management REST API

## Description
REST API to manage tasks using **Java 21, Spring Boot, HSQL, Hibernate JPA, Lombok, 
Spring Security, Swagger, JUnit, and Mockito**.  
Includes test coverage with **JaCoCo**.

## Technologies
    Java 21
    Spring Boot 3.3.x
    Spring Data JPA
    HSQLDB (in-memory)
    Springdoc OpenAPI (Swagger)
    Lombok
    Spring Security (HTTP Basic)
    Maven
    JUnit 5 + Mockito
    JaCoCo

---

## Features

- Create, list, update, and delete tasks.
- Field validation (title required, maximum 100 characters).
- Business rule: a task cannot be marked as **DONE** if it is still **IN_PROGRESS**.
- Swagger API documentation.
- Basic authentication with **HTTP Basic**.
- Unit and integration tests.
- Test coverage with JaCoCo.

---

## Task Model

| Field       | Type       | Description                        |
|------------|-----------|------------------------------------|
| id         | UUID      | Auto-generated                     |
| title      | String    | Required, max 100 characters       |
| description| String    | Optional                            |
| status     | Enum      | PENDING, IN_PROGRESS, DONE         |
| createdAt  | Timestamp | Creation date                       |
| updatedAt  | Timestamp | Last updated date                   |

---

## Endpoints

| Method | Path          | Description                    |
|--------|---------------|--------------------------------|
| POST   | /tasks        | Create a new task             |
| GET    | /tasks        | List all tasks                |
| GET    | /tasks/{id}   | Get task by ID                |
| PUT    | /tasks/{id}   | Update an existing task       |
| DELETE | /tasks/{id}   | Delete a task                 |

---

## Configuration

- In-memory **HSQL** database.
- Default user: `admin`
- Password: `admin`
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Running App
```mvn clean spring-boot:run```

## Build and run jar
```mvn clean package```
```java -jar target/tasks-1.0.0.jar```

## Test and Coverage
```mvn test```
```mvn jacoco:report```


