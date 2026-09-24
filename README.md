# Task Manager

![Backend](https://img.shields.io/badge/Backend-Java%2021%20%7C%20Spring%20Boot-green)
![Database](https://img.shields.io/badge/Database-MongoDB%208-red)
![Security](https://img.shields.io/badge/Security-Spring%20Security%20%7C%20JWT-blue)
![Testing](https://img.shields.io/badge/Testing-JUnit%20%7C%20Mockito%20%7C%20Testcontainers-orange)
![CI](https://img.shields.io/badge/CI-GitHub%20Actions-black)
![Containerization](https://img.shields.io/badge/Containerization-Docker-blue)

## Overview

A full-stack task management application built with Java, Spring Boot, and React Native.

The project is being developed as a practical backend engineering project, focusing on REST API design, Spring Security, JWT authentication, MongoDB, testing, containerization, and CI/CD.

## API

### Authentication

| Method | Endpoint             | Description                   | Authorization |
| ------ | -------------------- | ----------------------------- | ------------- |
| `POST` | `/api/auth/register` | Register a new user           | Public        |
| `POST` | `/api/auth/login`    | Authenticate and obtain a JWT | Public        |

### User Endpoints

| Method | Endpoint        | Description                          | Authorization    |
| ------ | --------------- | ------------------------------------ | ---------------- |
| `GET`  | `/api/users/me` | Get authenticated user's information | `USER` / `ADMIN` |
| ...    | ...             | Additional user endpoints            | Authenticated    |

### Task Endpoints

#### User endpoints

| Method   | Endpoint          | Description                        | Authorization |
| -------- | ----------------- | ---------------------------------- | ------------- |
| `GET`    | `/api/tasks/me`   | Get current user's tasks           | Authenticated |
| `POST`   | `/api/tasks`      | Create a task for the current user | Authenticated |
| `GET`    | `/api/tasks/{id}` | Get a task                         | Owner / Admin |
| `PUT`    | `/api/tasks/{id}` | Replace a task                     | Owner / Admin |
| `PATCH`  | `/api/tasks/{id}` | Partially update a task            | Owner / Admin |
| `DELETE` | `/api/tasks/{id}` | Delete a task                      | Owner / Admin |

#### Admin endpoints

| Method | Endpoint                   | Description                            | Authorization |
| ------ | -------------------------- | -------------------------------------- | ------------- |
| `GET`  | `/api/tasks`               | Get tasks across users                 | `ADMIN`       |
| `GET`  | `/api/tasks/user/{userId}` | Get tasks belonging to a specific user | `ADMIN`       |


## Resource Ownership

Tasks are associated with the authenticated user.

When creating a task, the `userId` is obtained from the authenticated security context rather than being trusted from the client request.

For operations involving a specific task, the application verifies ownership before allowing the operation.

This prevents a user from manipulating another user's tasks simply by supplying a different `userId` or task ID.

Administrators can access tasks belonging to other users according to the configured authorization rules.

## Technology Stack

### Backend

* Java 21
* Spring Boot 4.1.0
* Spring Web MVC
* Spring Security
* Spring Validation
* Spring Data MongoDB
* Lombok

### Authentication

* Auth0 Java JWT
* BCrypt

### Database

* MongoDB 8

### Testing

* JUnit
* Mockito
* Spring Boot Test
* MockMvc
* Testcontainers

### DevOps

* Docker
* Docker Compose
* GitHub Actions
* Maven

### Planned Frontend

* React Native

## Running the Project

### 1. Start MongoDB

From the project root:

```bash
docker compose -f docker/docker-compose.yaml up -d
```

### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

## Project Status

The backend currently provides a functional REST API with:

* User registration
* User authentication
* JWT authentication
* BCrypt password hashing
* Role-based authorization
* Task CRUD operations
* Task ownership enforcement
* DTO validation
* Unit tests
* Integration tests
* MongoDB Testcontainers
* GitHub Actions CI

## Current Features

### User Management

* User registration
* User authentication
* BCrypt password hashing
* User roles (`USER`, `ADMIN`)
* User-specific endpoints
* Administrative user endpoints
* DTO-based request and response handling
* Input validation using Spring Validation

### Authentication & Security

* Spring Security
* Stateless authentication
* JWT-based authentication
* JWT signature and expiration validation
* Custom JWT authentication filter
* Role-based authorization
* Protected API routes
* Resource ownership validation

### Task Management

Tasks support:

* Title and description
* Status:

  * `TODO`
  * `IN_PROCESS`
  * `DONE`
* Priority:

  * `LOW`
  * `MEDIUM`
  * `HIGH`
* User ownership
* Creation and update timestamps

Supported operations include:

* Create tasks
* Retrieve the authenticated user's tasks
* Retrieve individual tasks
* Update tasks
* Delete tasks
* Filter tasks by status and priority

Users cannot modify or access tasks belonging to other users unless they have the `ADMIN` role.

### Validation & DTOs

The API uses dedicated DTOs instead of exposing MongoDB documents directly.

Examples:

* `RegisterRequestDTO`
* `LoginRequestDTO`
* `AuthResponseDTO`
* `AppUserResponseDTO`
* `TaskRequestDTO`
* `TaskPatchRequestDTO`
* `TaskResponseDTO`

Request validation is implemented using Jakarta Bean Validation annotations such as:

* `@NotBlank`
* `@Size`
* `@Valid`

### Testing

The project contains both unit and integration tests.

#### Unit tests

* `JwtServiceTest`
* `AuthServiceTest`
* `TaskServiceTest`

#### Integration tests

* `AuthControllerIntegrationTest`
* `AuthIntegrationTest`
* `TaskControllerIntegrationTest`

MongoDB integration tests use **Testcontainers**, allowing the tests to run against a real MongoDB container rather than an in-memory mock database.

### Continuous Integration

GitHub Actions is configured to automatically build and test the backend.

### Next Steps

* Build the React Native client
* Expand test coverage where appropriate
* Add additional user/account functionality
* Connect the mobile client to the REST API
* Containerize the complete application stack for deployment