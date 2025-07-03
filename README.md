# SecretLink Backend

## Overview

Spring Boot REST API for a self-hosted secret sharing service.  
Stores encrypted secrets with expiration and one-time access.

## Features

- AES-256 encrypted secret storage
- One-time secret retrieval with expiration
- Scheduled cleanup of expired/used secrets
- Configurable via environment variables
- CORS enabled for frontend integration

## Requirements

- Java 17+
- PostgreSQL database
- Docker (optional)

## Configuration

Set environment variables or use `application.yml`:

| Variable                    | Description                    | Example                                 |
|-----------------------------|-------------------------------|-----------------------------------------|
| `SPRING_DATASOURCE_URL`      | JDBC URL to PostgreSQL         | `jdbc:postgresql://db:5432/database`   |
| `SPRING_DATASOURCE_USERNAME` | Database username             | `user`                                  |
| `SPRING_DATASOURCE_PASSWORD` | Database password             | `password`                              |
| `CRYPTO_SECRET_KEY`          | 32-byte secret key for AES-256| `aBcDeFgHiJkLmNoPqRsTuVwXyZ123456`     |
| `CLEANUP_TASK_RATE`          | Cleanup task interval in ms    | `3600000` (1 hour)                      |

## Build & Run

### Locally

```
./mvnw clean package
java -jar target/secretlink-backend.jar
```

### With Docker

Build image:

```
docker build -t secretlink-backend:latest -f docker/Dockerfile .
```

Run container (adjust env vars):

```
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/database \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e CRYPTO_SECRET_KEY=aBcDeFgHiJkLmNoPqRsTuVwXyZ123456 \
  secretlink-backend:latest
```


### OR with docker compose
navigate to the docker compose file 
in SecretLink/docker
adjust the env vars as needed
``` 
docker-compose up
```

### NOTE (Frontend)
for the frontend of this application navigate to https://github.com/HarounAhmad/SecretLink-Frontend
and read the README.md file there to build the dockerfile

both dockerfiles can be started using the
```
docker-compose up 
```
command in this repository

## API Endpoints

- `POST /api/v1/secrets/create` - Create a secret
- `GET /api/v1/secrets/{id}/{token}` - Reveal secret

## Notes

- Database must be accessible and initialized before starting backend.
- Secrets are encrypted and stored securely.
- Secrets auto-expire and get cleaned up regularly.
