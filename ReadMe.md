# Tech stack
Frontend: React (Vite)
Backend Java (Spring boot)

# Documentation
## Decisions and project architecture
Can be found in the ```/docs``` directory.

## API documentation
Start the backend following ```Run Dev``` steps and go to ```http://localhost:8080/swagger-ui/index.html``` in your browser.

# Starting local development environment
- Clone the repository
- Install the prerequisuites
- Run ```npm install```
- Follow the ```Run dev``` steps

## Prerequisites
- **Node.js**: 18+ (for frontend development)
- **Java**: 17+ (backend uses Java 17)
- **Maven**: 3.6+ (included with mvnw wrapper)

## Run local environment
### Start backend
Execute ```.\mvnw.cmd spring-boot:run```. 
Accesible at ```http://localhost:8080```.

### Start frontend
Execute ```npm run dev```.
Accesible at ```http://localhost:5173```.

## Unit tests
### Backend
```.\mvnw clean test```

### Frontend
```npm run test```

# Starting Staging

## Prerequisites
- **Docker & Docker Compose**: 20.10+ (for staging with docker-compose)

## Docker compose
```docker compose up --build```