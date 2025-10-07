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
│  React-Admin SPA         │  ───────────────────▶   │  Spring Boot API         │
│  (Vite + Nginx)          │   /api/v1/*, /auth/*    │  (WebMVC, Security, JPA) │
│                          │                         │                          │
│  - Auth form (JWT)       │   ◀──────────────────   │  - JWT login (/auth)     │
│  - Transactions (client) │       JSON responses    │  - Transactions,         │
│  - Disputes (admin)      │                         │    Disputes, Users       │
│  - Filters, modals       │                         │  - H2 in-memory DB       │
└──────────────────────────┘                         └──────────────────────────┘
            ▲                                                       │
            │ Swagger UI (for testing)                              │
            └───────────────────────────────────────────────────────┘
```
