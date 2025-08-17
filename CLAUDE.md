# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Backend (Java/Spring Boot)
- **Build & Run**: `cd api && ./gradlew bootRun` (runs on port 8080 at `/api`)
- **Build WAR**: `cd api && ./gradlew war` (for WildFly deployment)
- **Run Tests**: `cd api && ./gradlew test`
- **Run Specific Test**: `cd api && ./gradlew test --tests "com.fantasyfootball.service.TeamServiceTest"`
- **Unit Tests Only**: `cd api && ./gradlew test --tests "com.fantasyfootball.service.*" --tests "com.fantasyfootball.controller.TeamControllerTest" --tests "com.fantasyfootball.controller.PlayerControllerTest"`
- **Integration Tests**: `cd api && ./gradlew test --tests "com.fantasyfootball.controller.TeamControllerIntegrationTest"` (requires Docker)

### Frontend (Node.js/Express)
- **Install Dependencies**: `cd frontend && npm install`
- **Start Server**: `cd frontend && npm start` (runs on port 3000)
- **Development Mode**: `cd frontend && npm run dev` (uses nodemon)

### Database Setup
```bash
# Create database
createdb fantasy_football

# Run schema
psql -d fantasy_football -f database/schema.sql
```

## Architecture Overview

This is a 3-tier fantasy football management application:

### Backend (api/)
- **Framework**: Spring Boot 3.2 with Java 17
- **Database**: PostgreSQL with JPA/Hibernate
- **Security**: Basic Auth (admin/admin123)
- **API Docs**: Swagger UI at `/api/swagger-ui.html`
- **Context Path**: `/api` (all endpoints prefixed)

### Frontend (frontend/)
- **Framework**: Node.js/Express with EJS templating
- **Styling**: Vanilla CSS with responsive design
- **API Integration**: Axios for HTTP requests

### Database (database/)
- **PostgreSQL** schema with NFL teams, players, and fantasy team management
- Sample data included for development

## Key Components

### Entity Relationships
- **User** ↔ **Team** (one-to-many): Users own multiple fantasy teams
- **Team** ↔ **Player** (many-to-many via TeamPlayer): Teams have rosters
- **Player** ↔ **NflTeam** (many-to-one): Players belong to NFL teams
- **Player** ↔ **PlayerStats** (one-to-many): Track performance

### API Endpoints Structure
- **Teams**: `/api/teams/*` - CRUD operations, roster management
- **Players**: `/api/players/*` - Search, filter by position/team
- **Roster Management**: Add/remove players, set starters/bench

### Service Layer Pattern
- Controllers handle HTTP requests/responses
- Services contain business logic
- Repositories handle data access
- DTOs separate internal/external representations

## Configuration Notes

### Database Connection
- Local: `jdbc:postgresql://localhost:5432/fantasy_football`
- Username/password configured in `api/src/main/resources/application.yml`
- Uses Flyway for migrations (baseline-on-migrate enabled)

### CORS Configuration
- Backend configured for frontend integration
- Allows requests from frontend (port 3000)

### Testing Strategy
- **Unit Tests**: Mock dependencies, test business logic
- **Integration Tests**: Use Testcontainers with real PostgreSQL
- **Test Data**: Builder pattern in `TestDataBuilder.java`
- **Security Tests**: Use `@WithMockUser` for authenticated endpoints

## Development Workflow

1. **Backend Changes**: Modify Java code, run `./gradlew bootRun` to test
2. **Frontend Changes**: Modify EJS/CSS/JS, restart with `npm start`
3. **Database Changes**: Update `schema.sql`, recreate database
4. **Testing**: Run `./gradlew test` after backend changes
5. **API Documentation**: Changes auto-reflected in Swagger UI

## Production Deployment

### WildFly Deployment
- Build with `./gradlew war`
- Configure PostgreSQL datasource in WildFly
- Deploy WAR to `standalone/deployments/`
- Update `application.yml` to use JNDI datasource

### Environment Configuration
- Update database credentials for production
- Change security passwords from defaults
- Configure proper CORS origins for production frontend