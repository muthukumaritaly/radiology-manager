# Radiology Registry Management System

Welcome to the **Radiology Registry Management System** (RRMS). This repository contains a production-grade, enterprise-ready application built on **Spring Boot 3.x** and **Java 21** for the backend, and **Angular 21.x** for the frontend, backed by **PostgreSQL**. The system provides a highly flexible location and device registry for tracking high-value medical imaging hardware (e.g., MRI machines, CT scanners, X-ray equipment, mammography units) installed across nested physical facility divisions.

For detailed information about the system's design decisions, technical justifications, recursive tree builder algorithm, security configuration, testing approach, and DevOps pipelines (GitHub Actions vs. Jenkins), please refer to the [ARCHITECTURE.md](./ARCHITECTURE.md) file.

---

## 1. Project Overview

### The Business Problem
Modern healthcare organizations operate complex, multi-tiered facility footprints. A single healthcare group may manage several campuses (plants), each comprising multiple buildings, floors, wards, and dedicated rooms. Furthermore, certain equipment (such as mobile mammography vans) is itinerant and belongs directly to the parent organization without being stationed in a specific room. 

Traditional relational database schemas fail to accommodate this structural diversity because they rely on fixed, hard-coded spatial hierarchies (e.g., a static `Building -> Floor -> Room` table structure). This approach creates several problems:
- **Inflexibility**: Changing or adding structural levels requires schema migrations.
- **Data Duplication**: Empty level rows must be stored for simpler facilities.
- **Inefficient Queries**: Custom query logic is required for every level of the hierarchy.

### System Objectives
The **Radiology Registry Management System** solves these operational problems by modeling facilities as a recursive tree of containers. The system objectives are:
1. **Dynamic Facility Layouts**: Support unlimited nesting of containers (e.g., `Plant -> Building -> Wing -> Department -> Room -> Cabinet`) to represent any physical layout.
2. **Flexible Equipment Placement**: Allow radiological equipment to be registered directly under an organization or nested within any container.
3. **High-Performance Tree Fetching**: Provide a tree retrieval API that executes in $O(N)$ time with minimal database operations, avoiding the N+1 select query problem.
4. **Role-Based Authorization**: Enforce administrative controls on all write actions (such as registering equipment) while keeping read operations public.

---

## 2. Technology Stack

The technology stack for this project was selected to optimize performance, developer efficiency, environment consistency, and modern enterprise Java patterns:

| Technology | Component/Role | Version | Description |
| :--- | :--- | :--- | :--- |
| **Java** | Programming Language | 21 (LTS) | Utilizes modern language features like Java Records for clean DTOs, text blocks, and virtual thread compatibility. |
| **Spring Boot** | Core Framework | 3.3.x | Back-end framework facilitating REST routing, JPA transactional management, validation, and aspect-oriented programming (AOP). |
| **PostgreSQL** | Database | 15 (Alpine) | ACID-compliant relational database, optimized for foreign key constraints, indexes, and transactional integrity. |
| **Maven** | Build Tool | 3.9+ | Standardizes build lifecycle, simplifies dependency management, and runs verification test loops. |
| **Angular** | Frontend Framework | 21.2.x | Component-based SPA framework for modular UI layout and state isolation. |
| **PrimeNG** | UI Components | 21.1.x | Rich UI widgets (including interactive tree lists and forms) designed for modern web apps. |
| **TypeScript** | Scripting Language | 5.9+ | Adds optional static typing and compile-time verification to Javascript. |
| **Vitest** | Frontend Testing | 4.0+ | Modern unit testing framework providing lightweight frontend test execution. |
| **Nginx** | Reverse Proxy / Server | Alpine | Serves static frontend assets in production and proxies API traffic to the backend. |
| **Docker** | Containerization | Latest | Standardizes execution environments across local development and deployments. |
| **Docker Compose** | Local Orchestration | 3.8+ | Manages multi-container sandbox stack execution (app, frontend, and database) via single command. |
| **GitHub Actions** | CI Checks | - | Runs automated validation checks (compile, unit/integration tests, Docker build verification) on every commit. |
| **Swagger/OpenAPI** | API Documentation | 3.x (Springdoc) | Exposes interactive, self-documenting APIs for easy testing and debugging. |
| **JUnit 5 / Mockito** | Testing Suite | Latest | Facilitates high-coverage unit testing of business and validation logic. |
| **Testcontainers** | Integration Testing | 1.19+ | Launches temporary PostgreSQL Docker containers for real database verification during backend test phases. |

---

## 3. Local Setup Guide

Follow these steps to run and test the application locally:

### Prerequisites
- **Java 21** installed and configured in your path.
- **Node.js 20+** and **npm** installed for frontend execution.
- **Maven 3.8+** (or use the included wrapper `./backend/mvnw`).
- **Docker** and **Docker Compose** installed and running.

---

### Option A: Running with Docker Compose (Recommended)
This option launches the complete stack (Angular frontend + Spring Boot application + PostgreSQL database) inside containers:

1. Clone this repository and navigate to the root directory:
   ```bash
   cd radiology-manager
   ```
2. Copy the environment variables template file:
   ```bash
   cp .env.example .env
   ```
3. Start the application stack:
   ```bash
   docker-compose up --build
   ```
4. Once booted, access the services:
   - **Angular Frontend**: Running on [http://localhost:4200](http://localhost:4200)
   - **Spring Boot Server**: Running on [http://localhost:8080](http://localhost:8080)
   - **Swagger UI Console**: Running on [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
   - **PostgreSQL Database**: Port `5432` mapped to localhost.

---

### Option B: Running without Docker (Local Standalone Execution)
Use this option to run the Spring Boot application and Angular frontend on your host machine while pointing to a PostgreSQL instance:

#### 1. Database Setup
Ensure a PostgreSQL database is running locally. You can start one using Docker:
```bash
docker-compose up -d db
```

#### 2. Running the Backend Service
1. Export the environment variables defined in `.env`:
   ```bash
   export $(grep -v '^#' .env | xargs)
   ```
2. Run the Spring Boot application using Maven:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
3. Access the backend API and Swagger UI console at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

#### 3. Running the Frontend Application
1. Navigate to the frontend directory:
   ```bash
   cd radiology-manager-fe
   ```
2. Install npm dependencies:
   ```bash
   npm install
   ```
3. Start the local development server:
   ```bash
   npm run start
   ```
4. Access the frontend app at [http://localhost:4200](http://localhost:4200). (The development server uses `proxy.conf.json` to proxy API requests under `/api` to the backend on `http://localhost:8080`).

---

### Running the Test Suite
To run the full test suite (both backend and frontend tests):

#### Backend Tests (Unit & Integration)
```bash
cd backend
../mvnw clean test
```

#### Frontend Tests (Vitest)
```bash
cd radiology-manager-fe
npm run test
```

### Testing APIs with Postman
You can import the Postman collection [radiology-manager.postman_collection.json](./radiology-manager.postman_collection.json) located in the project root to quickly verify and run requests for the key endpoints:
- **Register New Equipment** (`POST /api/equipment`)
- **Retrieve Organization Tree** (`GET /api/organizations/{id}/tree`)

The collection is preconfigured with the base URL variable (`baseUrl` = `http://localhost:8080`) and path parameters.

---

## 4. Architectural Documentation

All architectural specifications, technical explanations, design justifications, API endpoints documentation, and CI/CD pipelines information are available in the [ARCHITECTURE.md](./ARCHITECTURE.md) document in the root directory.

