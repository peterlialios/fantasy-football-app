# Docker Setup for Fantasy Football Application

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- At least 4GB RAM available for Docker

## Before You Start

**IMPORTANT: Docker Desktop must be running before you can build and run containers.**

### Check if Docker is running:
```bash
docker --version
docker ps
```

If you get errors like "Cannot connect to the Docker daemon", Docker isn't running.

### Start Docker:
- **macOS**: Open Docker Desktop app
- **Windows**: Start Docker Desktop
- **Linux**: `sudo systemctl start docker`

## Quick Start

1. **Ensure Docker Desktop is running** (see above)

2. **Build and start all services:**
   ```bash
   docker-compose up --build
   ```

3. **Access the application:**
   - Frontend: http://localhost:3000
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - Database: localhost:5432

4. **Stop the application:**
   ```bash
   docker-compose down
   ```

## Detailed Commands

### Build Services
```bash
# Build all services
docker-compose build

# Build specific service
docker-compose build api
docker-compose build frontend
```

### Run Services
```bash
# Start in foreground (see logs)
docker-compose up

# Start in background
docker-compose up -d

# Start specific services
docker-compose up postgres api
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f api
docker-compose logs -f frontend
```

### Database Management
```bash
# Connect to PostgreSQL
docker-compose exec postgres psql -U peterlialios -d fantasy_football

# Reset database (WARNING: destroys data)
docker-compose down -v
docker-compose up --build
```

### Development Workflow
```bash
# Rebuild and restart after code changes
docker-compose up --build

# Restart specific service
docker-compose restart api
docker-compose restart frontend
```

## Container Architecture

### Services

1. **postgres** - PostgreSQL 14 database
   - Port: 5432
   - Data persisted in Docker volume
   - Auto-initializes with schema.sql

2. **api** - Spring Boot application
   - Port: 8080
   - Multi-stage build (Gradle + OpenJDK 17)
   - Health checks enabled
   - Waits for database to be ready

3. **frontend** - Node.js Express server
   - Port: 3000
   - Health checks enabled
   - Waits for API to be ready

### Network

All services run on the `fantasy-network` bridge network, allowing internal communication:
- Frontend connects to API via `http://api:8080/api`
- API connects to database via `jdbc:postgresql://postgres:5432/fantasy_football`

## Troubleshooting

### Common Issues

1. **Port conflicts:**
   ```bash
   # Check what's using ports
   lsof -i :3000
   lsof -i :8080
   lsof -i :5432
   
   # Stop conflicting services
   docker-compose down
   ```

2. **Database connection issues:**
   ```bash
   # Check database health
   docker-compose ps
   docker-compose logs postgres
   
   # Reset database
   docker-compose down -v
   docker volume rm fantasy-football-app_postgres_data
   ```

3. **Memory issues:**
   ```bash
   # Check Docker resource usage
   docker stats
   
   # Clean up unused images/containers
   docker system prune
   ```

4. **Build failures:**
   ```bash
   # Clean build
   docker-compose down
   docker-compose build --no-cache
   ```

### Health Checks

All services include health checks:
- **Database**: `pg_isready` command
- **API**: HTTP request to `/actuator/health`
- **Frontend**: HTTP request to root endpoint

Check health status:
```bash
docker-compose ps
```

### Environment Variables

Override default settings by creating a `.env` file:
```bash
# Database
POSTGRES_DB=fantasy_football
POSTGRES_USER=peterlialios
POSTGRES_PASSWORD=admin

# API
SPRING_PROFILES_ACTIVE=docker
```

## Production Considerations

1. **Security:**
   - Change default passwords
   - Use secrets management
   - Enable HTTPS with reverse proxy

2. **Performance:**
   - Increase JVM heap size for API
   - Configure connection pooling
   - Use production-grade PostgreSQL settings

3. **Monitoring:**
   - Add logging aggregation
   - Configure metrics collection
   - Set up alerts

4. **Backup:**
   - Regular database backups
   - Persistent volume management
   - Disaster recovery plan