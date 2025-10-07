# Transaction Dispute Demo — Spring Boot API + React-Admin SPA

A compact, production-style **transaction & dispute** system you can run locally with **Docker** or in dev mode. It demonstrates clean API design, role-based access control, pragmatic JWT auth, and a React-Admin front-end.

---

## Table of Contents
- [Quick Start](#quickstart)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Repo Structure](#repo-structure)
- [Domain & Security](#domain--security)

- [API Overview](#api-overview)
- [Frontend Features](#frontend-features)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Future Enhancements](#future-enhancements)
- [Demo Credentials](#demo-credentials)

---

## Quick Start (Docker)

**Prereqs**: Docker & Docker Compose.

1) Copy env template and set a strong secret:
```bash
cp .env.example .env
# edit .env and set APP_JWT_SECRET to a long random string (>= 32 bytes)
```
2) Build & run:
docker compose build
docker compose up -d

3) Open:
Frontend (SPA): http://localhost:8082
API / Swagger: http://localhost:8081/swagger-ui/index.html

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

```
tx-dispute/
├─ backend/ # Spring Boot app
│ ├─ src/main/java/com/example/txd/
│ │ ├─ TransactionDisputeApplication.java
│ │ ├─ config/ # SecurityConfig, Cors, DemoSeeder, Swagger bypass chain
│ │ ├─ controller/api/ # AuthApi, TransactionsApi, DisputesApi, UsersApi
│ │ ├─ dto/ # Request/Response DTOs
│ │ ├─ model/ # JPA: AppUser, Transaction, Dispute, etc.
│ │ ├─ repository/ # Spring Data repositories
│ │ ├─ security/ # Sec helper (extract role/userId)
│ │ └─ service/ # Business logic (scoping, transitions)
│ ├─ src/main/resources/application.yml
│ ├─ src/test/java/... # Unit & slice tests
│ └─ Dockerfile
│
├─ frontend/ # React-Admin Single Page Application (SPA)
│ ├─ src/
│ │ ├─ App.tsx # <Admin> + <Resource>s (Transactions, Disputes, Users)
│ │ ├─ authProvider.ts # login/me, 401/403 handling
│ │ ├─ dataProvider.ts # fetchJson + Authorization header + client filters
│ │ ├─ transactions.tsx # List + “Create/View Dispute” modals
│ │ ├─ disputes.tsx # List + Show + filters + Advance buttons
│ │ ├─ disputes/DisputeAdvanceButton.tsx
│ │ ├─ dateLocale.ts # international date formatting
│ │ ├─ ui.ts # shared MUI styles (e.g., pink hover)
│ │ └─ types.ts # shared TS types
│ ├─ Dockerfile
│ └─ nginx.conf
│
├─ docker-compose.yml # spins up both services
├─ .env.example # sample env vars for compose
├─ .gitignore / .gitattributes
└─ README.md

```

## Domain & Security

**Entities**
- `AppUser { id, username, passwordHash, role }`
- `Transaction { id, reference, amount, currency, description, createdAt, user }`
- `Dispute { id, transactionRef, reason, status, openedAt, underReviewAt, resolvedAt, rejectedAt, updatedAt, user }`

**Repositories**
- `TransactionRepository`: `findByUser_Id`, `findByReference`
- `DisputeRepository`: user-scoped queries

**Services**
- **Scoping**:  
  `ADMIN` → `findAll()`; otherwise current user → `findByUser_Id(Sec.userId())`.
- **Transitions**: validate legal moves; set timestamps; `409` on invalid transitions.

**SecurityConfig**
- Resource server (JWT) for `/api/**`
- `permitAll`: `/api/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`
- CORS enabled + configured via `app.cors.*`
- **Swagger bypass** chain (optional): permits `/api/**` **only** when `Referer` contains `/swagger-ui` (review convenience).

**Seeder (`DemoSeeder`)**
- Controlled by `app.seed.enabled=true`
- Adds:
  - `admin / admin1234` (ADMIN)
  - `client / client1234` (CLIENT)
  - Sample transactions for the client

---

## API Overview
** Auth
POST /api/auth/login → { token }
GET /api/auth/me → { id, username, role } (Bearer required)
** Transactions
GET /api/v1/transactions → list (admin: all; client: own)
GET /api/v1/transactions/{id} → get (scoped)
** Disputes
GET /api/v1/disputes → list (admin: all; client: own)
GET /api/v1/disputes/{id} → show (scoped)
POST /api/v1/disputes → create (client/admin)
POST /api/v1/disputes/{id}:advance → admin-only; validates legal transitions
** Users (admin only)
GET /api/v1/users … (if exposed)
