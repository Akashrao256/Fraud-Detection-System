# Quick Reference: Profile Selection

## 🚀 One Command Startup

```bash
# Local Development (H2) - DEFAULT, No setup needed
mvn clean package && java -jar target/*.jar

# Production (PostgreSQL) - Requires environment variables
mvn clean package && java -jar target/*.jar \
  --spring.profiles.active=postgres \
  --DB_URL=jdbc:postgresql://localhost:5432/frauddetection \
  --DB_USERNAME=postgres \
  --DB_PASSWORD=password
```

---

## 📋 Profile Comparison

| Feature              | H2 Profile      | PostgreSQL Profile |
| -------------------- | --------------- | ------------------ |
| **Setup Required**   | None ✅         | Required ⚙️        |
| **Startup Time**     | ~15 seconds ⚡  | ~15 seconds ⚡     |
| **Data Persistence** | Session only 💾 | Permanent 🔒       |
| **Use Case**         | Development 👨‍💻  | Production 🚀      |
| **External Dep**     | None            | PostgreSQL         |
| **Default**          | ✅ Yes          | No                 |

---

## 🔧 Environment Variables

### PostgreSQL Profile REQUIRES:

```bash
DB_URL=jdbc:postgresql://hostname:5432/database
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Optional:

```bash
SPRING_PROFILES_ACTIVE=postgres  # Override default H2
```

---

## ✅ Verify Installation

```bash
# Should see: "The following 1 profile is active: "h2""
grep "profile is active" application.properties

# Should see H2 JAR in classpath
mvn dependency:tree | grep h2database
```

---

## 🌐 Access Points

```
Swagger UI:        http://localhost:8080/swagger-ui.html
OpenAPI JSON:      http://localhost:8080/v3/api-docs
OpenAPI YAML:      http://localhost:8080/v3/api-docs.yaml
H2 Console:        http://localhost:8080/h2-console (H2 profile only)
Home:              http://localhost:8080/
Login:             http://localhost:8080/login.html
```

---

## 🎯 Common Tasks

### Switch to PostgreSQL

```bash
# Add to command line
java -jar target/*.jar --spring.profiles.active=postgres
```

### View Active Profile

```bash
# In logs, look for:
# The following 1 profile is active: "h2"
# or
# The following 1 profile is active: "postgres"
```

### Test Database Connection

```bash
# H2: Built-in, always works ✅
# PostgreSQL: Will show error if unreachable
```

### Reset Demo Data (H2 only)

```bash
# Restart application (H2 recreates schema)
java -jar target/*.jar
```

---

## 🐳 Docker Quick Start

### H2 (Quick Testing)

```bash
docker run -p 8080:8080 fraud-detection-system
```

### PostgreSQL (Production)

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=postgres \
  -e DB_URL=jdbc:postgresql://postgres:5432/fraud \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=secure \
  fraud-detection-system
```

---

## 📞 Troubleshooting

### Application won't start

```
✅ H2 Profile: Usually works immediately
❌ PostgreSQL: Check environment variables and DB connection
```

### Swagger UI not loading

```
✅ Check: http://localhost:8080/swagger-ui.html
✅ Verify port 8080 is accessible
```

### Lost data on restart

```
✅ H2: Expected (in-memory database)
✅ PostgreSQL: Should persist (check database)
```

### JWT token issues

```
✅ Login first: POST /api/auth/login
✅ Copy token value
✅ Paste as: Bearer <token> in Swagger UI
```

---

**Configuration Status:** ✅ Ready to Use
