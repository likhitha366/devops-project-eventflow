# EventFlow

**EventFlow** is a small event-registration application built to demonstrate a practical DevOps workflow. Students can browse events and reserve a seat; an administrator can create events and view registrations.

The application is intentionally small: the value of the project is showing how a real feature travels from Git to testing, Docker, Kubernetes and Jenkins.

## Architecture

```
browser → frontend (Nginx) → event-service (8082)
                         └→ registration-service (8083)
                         └→ user-service (8081)
```

Each service owns its in-memory data for a simple, repeatable classroom demonstration. In a production version, each service would use its own database and communicate through an API gateway/message broker.

## Quick start

Prerequisites: Java 17, Maven, Docker Desktop.

```bash
docker compose up --build
```

Open `http://localhost:8080`. API endpoints are exposed on ports 8081–8083.

To run one service during development:

```bash
cd event-service
mvn spring-boot:run
```

## API summary

| Service | Useful endpoints |
|---|---|
| User | `POST /users/register`, `GET /users/{id}` |
| Event | `GET /events`, `POST /events`, `DELETE /events/{id}` |
| Registration | `POST /registrations`, `GET /registrations`, `DELETE /registrations/{id}` |

Example registration body:

```json
{ "userId": "student-101", "eventId": "evt-techfest" }
```

## CI/CD flow

Every push triggers Jenkins to build and test all services, build Docker images, and apply the Kubernetes manifests. Configure the `REGISTRY` Jenkins environment variable to publish to your Docker registry. The deployment stage requires `kubectl` access to your cluster.

## Project talking point

“EventFlow is not only an event website. It is a small application used to demonstrate how code is version-controlled, tested, containerized and deployed automatically through a DevOps pipeline.”
