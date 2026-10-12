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

The Jenkins pipeline builds and tests the services, builds the four Docker images, and can publish and deploy them when enabled. Configure the Jenkins agent with Java 17, Maven, Docker Compose v2, and `kubectl`. Use the Jenkins build parameters to publish and deploy:

| Variable | Example | Purpose |
|---|---|---|
| `REGISTRY` | `my-dockerhub-user` | Image registry and repository prefix; defaults to `eventflow` |
| `IMAGE_TAG` | `release-12` | Optional image tag; blank defaults to the Jenkins build number |
| `PUBLISH_IMAGES` | Checked | Push built images to the configured registry |
| `DEPLOY_TO_K8S` | Checked | Apply the Kubernetes deployment after publishing |

The Jenkins agent must be logged in to the image registry, and `kubectl` must already have credentials for the target cluster. The frontend is exposed as a Kubernetes NodePort at `http://<node-ip>:30080`; its Nginx proxy sends API requests to the in-cluster services.

## Deploying on Render

This repository includes a Render Blueprint in `render.yaml`. In Render, create a new Blueprint and connect this repository's `main` branch. The Blueprint creates separate web services for the frontend and each API, using each service's Dockerfile and wiring the frontend to the APIs over Render's private network. Open the `eventflow-frontend` service URL after all four services finish deploying. The existing one-service setup cannot build from the repository root because there is no root-level `Dockerfile`.

## Project talking point

“EventFlow is not only an event website. It is a small application used to demonstrate how code is version-controlled, tested, containerized and deployed automatically through a DevOps pipeline.”
