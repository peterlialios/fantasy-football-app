# Fantasy Football API Test Suite

This directory contains comprehensive unit tests for the Fantasy Football API application.

## Test Structure

### Controller Tests (`/controller`)
- **`TeamControllerTest.java`** - Unit tests for TeamController REST endpoints
- **`PlayerControllerTest.java`** - Unit tests for PlayerController REST endpoints
- **`TeamControllerIntegrationTest.java`** - Integration tests using Testcontainers (requires Docker)

### Service Tests (`/service`)
- **`TeamServiceTest.java`** - Unit tests for TeamService business logic

### Utilities (`/util`)
- **`TestDataBuilder.java`** - Builder pattern utilities for creating test data
- **`TestConfiguration.java`** - Test-specific Spring Security configuration

## Test Categories

### Unit Tests
- **Controller Layer**: Mock MVC tests with mocked services
- **Service Layer**: Business logic tests with mocked repositories
- **Coverage**: All major CRUD operations and business rules

### Integration Tests
- **Database Integration**: Using Testcontainers with PostgreSQL
- **Full Spring Context**: End-to-end API testing

## Running Tests

### All Unit Tests
```bash
./gradlew test --tests "com.fantasyfootball.service.*" --tests "com.fantasyfootball.controller.TeamControllerTest" --tests "com.fantasyfootball.controller.PlayerControllerTest"
```

### Specific Test Classes
```bash
# Service tests only
./gradlew test --tests "com.fantasyfootball.service.TeamServiceTest"

# Controller tests only  
./gradlew test --tests "com.fantasyfootball.controller.TeamControllerTest"

# Integration tests (requires Docker)
./gradlew test --tests "com.fantasyfootball.controller.TeamControllerIntegrationTest"
```

### All Tests
```bash
./gradlew test
```

## Test Coverage

### TeamController Tests
- ✅ GET `/teams` - List all teams
- ✅ GET `/teams/{id}` - Get team by ID
- ✅ GET `/teams/{id}/with-players` - Get team with players
- ✅ GET `/teams/owner/{ownerId}` - Get teams by owner
- ✅ POST `/teams` - Create new team
- ✅ PUT `/teams/{id}` - Update team
- ✅ DELETE `/teams/{id}` - Delete team
- ✅ GET `/teams/{id}/roster` - Get team roster
- ✅ GET `/teams/{id}/starters` - Get starting players
- ✅ GET `/teams/{id}/bench` - Get bench players
- ✅ POST `/teams/{teamId}/players/{playerId}` - Add player to team
- ✅ DELETE `/teams/{teamId}/players/{playerId}` - Remove player from team
- ✅ PUT `/teams/{teamId}/players/{playerId}/starter-status` - Update player status
- ✅ GET `/teams/{id}/size` - Get team size

### PlayerController Tests
- ✅ GET `/players` - List all players
- ✅ GET `/players/active` - List active players
- ✅ GET `/players/{id}` - Get player by ID
- ✅ GET `/players/{id}/with-team` - Get player with NFL team
- ✅ GET `/players/{id}/with-stats` - Get player with stats
- ✅ GET `/players/position/{position}` - Get players by position
- ✅ GET `/players/nfl-team/{teamId}` - Get players by NFL team
- ✅ GET `/players/search?name={name}` - Search players by name
- ✅ GET `/players/available/{teamId}` - Get available players not on team
- ✅ POST `/players` - Create new player
- ✅ PUT `/players/{id}` - Update player
- ✅ DELETE `/players/{id}` - Delete player

### TeamService Tests
- ✅ Get all teams
- ✅ Get team by ID (found/not found)
- ✅ Save team
- ✅ Delete team
- ✅ Get teams by owner ID
- ✅ Add player to team (success/team not found/player not found/already on team)
- ✅ Remove player from team (success/not on team)
- ✅ Update player starter status (success/not on team)
- ✅ Get team size
- ✅ Get team roster
- ✅ Get team starters
- ✅ Get team bench

## Test Features

### Mocking
- Uses `@MockBean` for Spring Boot test mocking
- Mockito for behavior verification
- MockMvc for HTTP request simulation

### Security Testing
- Uses `@WithMockUser` for authenticated requests
- Includes CSRF token handling for POST/PUT/DELETE requests

### JSON Validation
- JSONPath assertions for response validation
- Content type verification
- Status code validation

### Error Scenarios
- Tests for 404 Not Found responses
- Tests for 400 Bad Request responses
- Exception handling validation

## Dependencies Used

- **Spring Boot Test** - `@SpringBootTest`, `@WebMvcTest`
- **Mockito** - Mocking framework
- **JUnit 5** - Test framework
- **Spring Security Test** - Security testing utilities
- **Testcontainers** - Integration testing with real database
- **Jackson** - JSON serialization/deserialization for test data

## Notes

- Integration tests require Docker to be running (for Testcontainers)
- Unit tests can run without any external dependencies
- All tests use the builder pattern from `TestDataBuilder` for clean test data creation
- Tests follow AAA pattern: Arrange, Act, Assert