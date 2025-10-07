# Transaction Dispute Demo — Spring Boot API + React-Admin SPA

A compact, production-style **transaction & dispute** system you can run locally with **Docker** or in dev mode. It demonstrates clean API design, role-based access control, pragmatic JWT auth, and a React-Admin front-end.

---

## Table of Contents
- [Quick Start](#quickstart)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Repo Structure](#repo-structure)
- [Domain & Security](#domain--security)
- [Build & Run (Docker)](#build--run-docker)
- [Build & Run (Local Dev)](#build--run-local-dev)
- [Configuration](#configuration)
- [API Overview](#api-overview)
- [Frontend Features](#frontend-features)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Future Enhancements](#future-enhancements)
- [Demo Credentials](#demo-credentials)

---

## Architecture

```
┌──────────────────────────┐        HTTP/JSON        ┌──────────────────────────┐
│  React-Admin SPA         │  ───────────────────▶  │  Spring Boot API         │
│  (Vite + Nginx)          │   /api/v1/*, /auth/*    │  (WebMVC, Security, JPA) │
│                          │                         │                          │
│  - Auth form (JWT)       │   ◀──────────────────  │  - JWT login (/auth)     │
│  - Transactions (client) │       JSON responses    │  - Transactions,         │
│  - Disputes (admin)      │                         │    Disputes, Users       │
│  - Filters, modals       │                         │  - H2 in-memory DB       │
└──────────────────────────┘                         └──────────────────────────┘
            ▲                                                       │
            │ Swagger UI (for testing)                              │
            └───────────────────────────────────────────────────────┘
```


**Roles & behavior**
- **CLIENT**: sees only *their* Transactions and Disputes.
- **ADMIN**: sees Disputes (with user column) and Users.
- **Dispute flow**: `OPEN → UNDER_REVIEW → (RESOLVED | REJECTED)`; admin advances status.
- **JWT**: HS256 with `role` and `userId` claims.
- **CORS**: configured for the SPA origin.
- **Swagger bypass (optional)**: allow unauthenticated “Try it out” *only when* called from Swagger UI (by `Referer`).

---

## Tech Stack

**Backend**
- Spring Boot 3 (WebMVC, Security, Validation)
- Spring Data JPA (Hibernate), H2 (in-memory dev DB)
- JWT via `NimbusJwtEncoder/Decoder` (HS256)
- springdoc-openapi (Swagger UI)
- (Optional) Actuator (health/info)

**Frontend**
- React-Admin (Material UI) on Vite
- Client-side filtering/sorting/pagination
- MUI Dialog modals for dispute creation & status timeline
- Nginx for static hosting; optional `/api` reverse-proxy

**Testing**
- JUnit 5, Mockito, spring-security-test
- `@WebMvcTest` (controllers), `@DataJpaTest` (repositories), service unit tests

**Packaging**
- Docker (multi-stage images) & Docker Compose

---

## Repo Structure

