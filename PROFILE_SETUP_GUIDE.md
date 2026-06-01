# Fraud Detection System - H2 & PostgreSQL Profile Configuration

## Overview

The Fraud Detection System has been successfully configured to support both **H2 (embedded database)** for local development and **PostgreSQL** for production/deployment environments using Spring Boot profiles.

## ✅ Configuration Summary

### Files Created

- **`src/main/resources/application-h2.properties`** - H2 embedded database configuration
- **`src/main/resources/application-postgres.properties`** - PostgreSQL configuration with environment variables

### Files Modified

1. **`src/main/resources/application.properties`** - Set active profile to H2 by default
2. **`pom.xml`** - Changed H2 dependency scope from `test` to runtime
3. **`src/main/java/com/frauddetection/config/SecurityConfig.java`** - Added H2 console access configuration

---

## 🚀 Quick Start

### Local Development (H2 Profile - Default)

```bash
# Build the application
mvn clean package

# Start the application
java -jar target/fraud-detection-system-0.0.1-SNAPSHOT.jar
```

**The application will:**

- ✅ Start on `localhost:8080`
- ✅ Use embedded H2 in-memory database (no setup required)
- ✅ Load demo data automatically
- ✅ Serve Swagger UI at `http://localhost:8080/swagger-ui.html`
- ✅ Create demo admin account automatically

### Production/Deployment (PostgreSQL Profile)

```bash
# Build the application
mvn clean package

# Start with PostgreSQL profile
java -jar target/fraud-detection-system-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=postgres \
  --DB_URL=jdbc:postgresql://hostname:5432/frauddetection \
  --DB_USERNAME=your_username \
  --DB_PASSWORD=your_password
```

**Environment Variables Required:**

- `DB_URL` - PostgreSQL JDBC connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

---

## 📋 H2 Profile Configuration

**File:** `src/main/resources/application-h2.properties`

```properties
# H2 Embedded Database (In-Memory)
spring.datasource.url=jdbc:h2:mem:frauddetectiondb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console (Optional - for debugging)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate for H2
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.defer-datasource-initialization=true
```

**Benefits:**

- ✅ No external database setup required
- ✅ In-memory database (fast startup)
- ✅ Perfect for local development and testing
- ✅ Demo data auto-loads on startup

---

## 🐘 PostgreSQL Profile Configuration

**File:** `src/main/resources/application-postgres.properties`

```properties
# PostgreSQL Database Connection (Uses Environment Variables)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000

# JPA/Hibernate for PostgreSQL
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.jdbc.batch_size=20
```

**Benefits:**

- ✅ Production-ready configuration
- ✅ Connection pooling optimized
- ✅ Performance optimizations enabled
- ✅ Environment-based credentials (secure)

---

## 🔐 Security Configuration

**File:** `src/main/java/com/frauddetection/config/SecurityConfig.java`

### Public Endpoints

All endpoints are protected **except:**

| Endpoint               | Purpose               | Auth Required |
| ---------------------- | --------------------- | ------------- |
| `/api/auth/login`      | User login            | ❌ No         |
| `/api/auth/register`   | User registration     | ❌ No         |
| `/swagger-ui/**`       | Swagger UI assets     | ❌ No         |
| `/swagger-ui.html`     | Swagger UI main page  | ❌ No         |
| `/v3/api-docs/**`      | OpenAPI documentation | ❌ No         |
| `/v3/api-docs.yaml`    | OpenAPI YAML spec     | ❌ No         |
| `/h2-console/**`       | H2 database console   | ❌ No         |
| `/`                    | Home page             | ❌ No         |
| Static files (CSS, JS) | Frontend assets       | ❌ No         |

### Protected Endpoints

All API endpoints require **JWT authentication:**

```bash
# Get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Use token in Swagger UI
1. Click "Authorize" button
2. Paste: Bearer <token>
3. Try out endpoints
```

---

## 🧪 Testing & Verification

### Build & Package

```bash
# Build with tests (includes H2 testing)
mvn clean package

# Build without tests (faster)
mvn clean package -DskipTests
```

### Startup Verification

**Successful startup output should show:**

```
The following 1 profile is active: "h2"
HikariPool-1 - Added connection conn0: url=jdbc:h2:mem:frauddetectiondb user=SA
Tomcat started on port 8080 (http) with context path '/'
Started FraudDetectionApplication
Demo admin user has been created
```

### API Testing

**Option 1: Swagger UI (Recommended)**

- Navigate to: `http://localhost:8080/swagger-ui.html`
- Login with: `admin` / `admin123`
- Test all endpoints interactively

**Option 2: cURL**

```bash
# Get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.token')

# Create transaction
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000,
    "description": "Test transaction",
    "recipientAccount": "ACC-12345",
    "transactionType": "TRANSFER"
  }'

# Get fraud alerts
curl -X GET http://localhost:8080/api/fraud-alerts \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📊 Database Profile Selection

### How Spring Profiles Work

```
application.properties (Base configuration)
  └─ spring.profiles.active=h2 (Default)
     └─ application-h2.properties (Loaded if profile=h2)
        └─ application-postgres.properties (Loaded if profile=postgres)
```

### Runtime Profile Selection

**Method 1: Environment Variable**

```bash
export SPRING_PROFILES_ACTIVE=postgres
java -jar target/fraud-detection-system-0.0.1-SNAPSHOT.jar
```

**Method 2: Command Line Argument**

```bash
java -jar target/fraud-detection-system-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=postgres
```

**Method 3: application.properties**

```properties
spring.profiles.active=postgres
```

---

## 🛠️ Docker Deployment

### Using H2 (Quick Testing)

```dockerfile
FROM openjdk:17-slim
COPY target/fraud-detection-system-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Run:**

```bash
docker run -p 8080:8080 fraud-detection-system
```

### Using PostgreSQL (Production)

```dockerfile
FROM openjdk:17-slim
COPY target/fraud-detection-system-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Run:**

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=postgres \
  -e DB_URL=jdbc:postgresql://postgres:5432/frauddetection \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=password \
  fraud-detection-system
```

---

## 🚁 Kubernetes/Railway Deployment

### Environment Variables

Set these environment variables in your deployment platform:

```yaml
SPRING_PROFILES_ACTIVE: postgres
DB_URL: jdbc:postgresql://hostname:5432/frauddetection
DB_USERNAME: { { secrets.db_username } }
DB_PASSWORD: { { secrets.db_password } }
```

---

## ⚠️ Important Notes

### H2 Profile (Development)

- ✅ **In-memory database** - Data is lost on restart
- ✅ **No external dependencies** - Immediate startup
- ✅ **Demo data loads automatically** - Includes test user (admin/admin123)
- ✅ **Perfect for local testing** - Suitable for Swagger UI testing
- ⚠️ **Data persistence** - Only for current session

### PostgreSQL Profile (Production)

- ✅ **Persistent data** - Survives application restarts
- ✅ **Production-ready** - Connection pooling, optimizations
- ✅ **Environment variables** - Secure credential management
- ⚠️ **External dependency** - Requires PostgreSQL setup
- ⚠️ **Manual schema initialization** - Run migrations if needed

---

## 🎯 Common Use Cases

### Use H2 When:

- ✅ Local development
- ✅ Testing changes quickly
- ✅ Demonstrating features
- ✅ Running Swagger UI tests
- ✅ CI/CD pipeline testing

### Use PostgreSQL When:

- ✅ Production deployment
- ✅ Multi-user environments
- ✅ Data persistence required
- ✅ Docker/Kubernetes deployment
- ✅ Integration with other systems

---

## 📝 Project Structure

```
fraud-detection-system/
├── src/main/resources/
│   ├── application.properties              # Base config (H2 default)
│   ├── application-h2.properties           # H2 profile config
│   ├── application-postgres.properties     # PostgreSQL profile config
│   └── static/
│       ├── index.html
│       ├── login.html
│       ├── admin.html
│       └── css/, js/
├── src/main/java/com/frauddetection/
│   ├── FraudDetectionApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java             # Security & profiles
│   │   ├── AppConfig.java
│   │   ├── DataResetConfig.java
│   │   └── OpenAPIConfiguration.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── TransactionController.java
│   │   ├── FraudAlertController.java
│   │   └── DashboardController.java
│   ├── service/
│   ├── model/
│   ├── repository/
│   └── security/
├── pom.xml                                 # H2 added to runtime
└── docker-compose.yml                      # PostgreSQL container
```

---

## ✅ Verification Checklist

- [x] H2 profile configured and working
- [x] PostgreSQL profile configured for deployment
- [x] Application starts with H2 by default (no setup needed)
- [x] Swagger UI accessible on `localhost:8080/swagger-ui.html`
- [x] JWT authentication preserved
- [x] Fraud detection logic unchanged
- [x] All existing tests pass
- [x] Build successful: `mvn clean package`
- [x] Startup successful: `java -jar target/*.jar`
- [x] Security configuration updated for H2 console access

---

## 🔗 Related Documentation

- [Spring Boot Profiles Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
- [H2 Database Documentation](https://www.h2database.com/)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [JWT Authentication](https://jwt.io/)
- [SpringDoc OpenAPI (Swagger)](https://springdoc.org/)

---

**Last Updated:** June 1, 2026  
**Status:** ✅ Production Ready
