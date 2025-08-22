# Fantasy Football Management Application

A comprehensive web-based fantasy football management system with a Java REST API backend, PostgreSQL database, and Node.js frontend.

## Architecture

### Backend (Java/Spring Boot)
- **Technology Stack**: Java 17, Spring Boot 3.2, Gradle
- **Database**: PostgreSQL with JPA/Hibernate
- **Deployment**: WAR file for Wildfly server
- **API Documentation**: Swagger/OpenAPI available at `/swagger-ui.html`

### Frontend (Node.js/Express)
- **Technology Stack**: Node.js, Express, EJS templates
- **Port**: 3000 (default)
- **Styling**: Clean, minimal CSS with responsive design

### Database
- **PostgreSQL** with comprehensive schema for fantasy football management
- Includes sample data for teams, players, and statistics

## Project Structure

```
fantasy-football-app/
├── database/
│   └── schema.sql              # PostgreSQL database schema and sample data
├── api/                        # Java Spring Boot REST API
│   ├── build.gradle           # Gradle build configuration
│   ├── src/main/java/com/fantasyfootball/
│   │   ├── FantasyFootballApplication.java
│   │   ├── entity/            # JPA entities
│   │   ├── repository/        # Data access layer
│   │   ├── service/           # Business logic layer
│   │   ├── controller/        # REST controllers
│   │   └── config/            # Configuration classes
│   └── src/main/resources/
│       └── application.yml    # Application configuration
└── frontend/                  # Node.js frontend
    ├── server.js             # Express server
    ├── public/               # Static assets (CSS, JS)
    ├── views/                # EJS templates
    └── package.json          # Node.js dependencies
```

## Features

### Core Functionality
- **Team Management**: Create and manage fantasy teams
- **Player Search**: Search players by name, position, or NFL team
- **Roster Management**: Add/remove players, set starting lineups
- **Player Statistics**: Track fantasy points and performance

### User Interface
- **Left Navigation Menu**: Easy access to teams, players, and positions
- **Responsive Design**: Works on desktop and mobile devices
- **Clean Styling**: Minimal but professional appearance
- **Interactive Features**: Add/remove players with AJAX calls

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL 12 or higher
- Gradle (or use included wrapper)

### Database Setup
1. Create PostgreSQL database:
   ```sql
   CREATE DATABASE fantasy_football;
   ```

2. Run the schema script:
   ```bash
   psql -d fantasy_football -f database/complete_schema.sql
   ```

### API Setup
1. Navigate to the API directory:
   ```bash
   cd api
   ```

2. Update database connection in `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/fantasy_football
       username: your_username
       password: your_password
   ```

3. Build and run the API:
   ```bash
   ./gradlew bootRun
   ```
   
   Or to create a WAR file for Wildfly:
   ```bash
   ./gradlew war
   ```

4. API will be available at: `http://localhost:8080/api`
5. Swagger documentation: `http://localhost:8080/api/swagger-ui.html` or `http://localhost:8080/api/swagger-ui/index.html`

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the frontend server:
   ```bash
   npm start
   ```

4. Frontend will be available at: `http://localhost:3000`

## API Endpoints

### Teams
- `GET /api/teams` - Get all teams
- `GET /api/teams/{id}` - Get team by ID
- `GET /api/teams/{id}/roster` - Get team roster
- `POST /api/teams/{teamId}/players/{playerId}` - Add player to team
- `DELETE /api/teams/{teamId}/players/{playerId}` - Remove player from team

### Players
- `GET /api/players/active` - Get all active players
- `GET /api/players/search?name={name}` - Search players by name
- `GET /api/players/position/{position}` - Get players by position
- `GET /api/players/available/{teamId}` - Get players not on specified team

## Default Credentials

- **API Basic Auth**: admin / admin123
- **Database**: Sample user and team data included in schema

## Development Notes

- The API includes CORS configuration for frontend integration
- Frontend uses EJS templating for server-side rendering
- Database includes sample NFL teams and players
- Security is configured for development (basic auth)
- All monetary values use BigDecimal for precision

## Production Deployment

### WildFly Application Server Deployment

#### Prerequisites
- WildFly 27+ or newer
- PostgreSQL JDBC driver
- Java 17 or higher

#### 1. Download and Setup WildFly
```bash
# Download WildFly
wget https://github.com/wildfly/wildfly/releases/download/27.0.1.Final/wildfly-27.0.1.Final.tar.gz
tar -xzf wildfly-27.0.1.Final.tar.gz
cd wildfly-27.0.1.Final
```

#### 2. Configure PostgreSQL DataSource
1. Download PostgreSQL JDBC driver:
   ```bash
   wget https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
   ```

2. Add PostgreSQL module:
   ```bash
   # Create module directory
   mkdir -p modules/org/postgresql/main
   
   # Copy driver
   cp postgresql-42.6.0.jar modules/org/postgresql/main/
   
   # Create module.xml
   cat > modules/org/postgresql/main/module.xml << EOF
   <?xml version="1.0" encoding="UTF-8"?>
   <module xmlns="urn:jboss:module:1.3" name="org.postgresql">
       <resources>
           <resource-root path="postgresql-42.6.0.jar"/>
       </resources>
       <dependencies>
           <module name="javax.api"/>
           <module name="javax.transaction.api"/>
       </dependencies>
   </module>
   EOF
   ```

3. Start WildFly and configure datasource:
   ```bash
   # Start WildFly
   ./bin/standalone.sh
   
   # In another terminal, add driver and datasource
   ./bin/jboss-cli.sh --connect
   
   # Add PostgreSQL driver
   /subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver)
   
   # Add datasource
   data-source add --name=FantasyFootballDS --jndi-name=java:jboss/datasources/FantasyFootballDS --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5432/fantasy_football --user-name=your_username --password=your_password --enabled=true
   
   # Test connection
   /subsystem=datasources/data-source=FantasyFootballDS:test-connection-in-pool
   
   # Reload configuration
   :reload
   ```

#### 3. Build and Deploy WAR File
```bash
# Build WAR file
cd api
./gradlew war

# Deploy to WildFly
cp build/libs/fantasy-football-api.war /path/to/wildfly/standalone/deployments/
```

#### 4. Alternative: Deploy via Management Console
1. Access WildFly Admin Console: `http://localhost:9990`
2. Create admin user:
   ```bash
   ./bin/add-user.sh
   # Follow prompts to create management user
   ```
3. Navigate to "Deployments" → "Add" → Upload WAR file

#### 5. Update Application Configuration
Create `application.yml` for production:
```yaml
spring:
  datasource:
    jndi-name: java:jboss/datasources/FantasyFootballDS
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  security:
    user:
      name: admin
      password: production_password
      
server:
  servlet:
    context-path: /fantasy-football-api

logging:
  level:
    com.fantasyfootball: INFO
    org.springframework.security: WARN
```

#### 6. Verify Deployment
- Application URL: `http://localhost:8080/fantasy-football-api/`
- Health Check: `http://localhost:8080/fantasy-football-api/actuator/health`
- API Documentation: `http://localhost:8080/fantasy-football-api/swagger-ui.html`

#### 7. Production Considerations
- **Security**: Change default passwords and enable HTTPS
- **Monitoring**: Configure WildFly monitoring and logging
- **Performance**: Tune JVM settings and connection pools
- **Backup**: Implement database backup strategy
- **Clustering**: Configure WildFly cluster for high availability

### Other Deployment Options

1. **Database**: Configure production PostgreSQL instance
2. **Frontend**: Use process manager like PM2 for Node.js
3. **Reverse Proxy**: Configure Nginx/Apache for load balancing
4. **Security**: Update authentication and CORS settings
5. **Environment**: Configure production environment variables