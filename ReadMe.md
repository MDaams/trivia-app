# Trivia App

## Tech Stack
- **Frontend**: React (Vite)
- **Backend**: Java (Spring Boot)

## Quick Start

### Prerequisites
- **Node.js**: 18+ (for frontend development)
- **Java**: 17+ (backend uses Java 17)
- **Maven**: 3.6+ (included with mvnw wrapper)

### Local Development

#### Start Backend
```
.\mvnw.cmd spring-boot:run
```
Accessible at `http://localhost:8080`

#### Start Frontend
```
npm install
npm run dev
```
Accessible at `http://localhost:5173`

### Running Tests

#### Backend
```
.\mvnw clean test
```

#### Frontend
```
npm run test
```

## Staging

### Prerequisites
- **Docker & Docker Compose**: 20.10+

### Run with Docker Compose
```
docker compose up --build
```

## Documentation

- **Architecture & Decisions**: See `/docs` directory
- **API Documentation**: Start the backend and visit `http://localhost:8080/swagger-ui/index.html`
- **Detailed Docs**: Backend.md, Frontend.md, and architecture diagrams in `/docs`