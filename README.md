# AI-Assisted Fraud Detection & Risk Monitoring Platform

A secure, enterprise-style fraud detection and transaction monitoring platform built using Java, Spring Boot, PostgreSQL, JWT Authentication, Docker, and Machine Learning.

The platform analyzes financial transactions in real time using a combination of rule-based fraud detection and machine-learning-assisted risk analysis. It assigns risk scores, generates fraud alerts, and provides administrative dashboards for monitoring suspicious activities.

---

## Project Overview

Financial institutions process thousands of transactions every day. Detecting suspicious activities quickly is critical to prevent fraud and financial loss.

This platform helps identify potentially fraudulent transactions by:

- Monitoring transaction behavior
- Evaluating risk factors
- Detecting anomalies using predefined fraud rules
- Applying machine-learning-assisted fraud analysis
- Generating fraud alerts
- Providing risk monitoring dashboards

The system supports secure authentication, transaction tracking, fraud investigation, and administrative monitoring.

---

## Key Features

### Authentication & Security

- JWT-based Authentication
- Spring Security Integration
- BCrypt Password Encryption
- Stateless Authentication
- Protected REST APIs

### Fraud Detection Engine

The platform detects suspicious activity using multiple fraud indicators:

#### Large Transaction Detection

Flags transactions above configured thresholds.

#### Unusual Location Detection

Detects transactions originating from unfamiliar locations.

#### Frequent Transaction Detection

Flags multiple transactions occurring within a short time window.

#### Late-Night Transaction Detection

Identifies transactions performed during unusual hours.

#### Machine Learning Assisted Detection

Uses a Weka Random Forest model to assist in fraud prediction and risk evaluation.

---

## Risk Scoring System

Each fraud indicator contributes to a cumulative risk score.

| Fraud Rule                 | Score Contribution |
| -------------------------- | ------------------ |
| Large Transaction          | +40                |
| Unusual Location           | +30                |
| Frequent Transactions      | +20                |
| Late-Night Transaction     | +15                |
| Machine Learning Detection | +25                |

Maximum Risk Score:

```text
100
```

### Risk Levels

| Score Range | Risk Level |
| ----------- | ---------- |
| 0 - 29      | LOW        |
| 30 - 69     | MEDIUM     |
| 70 - 100    | HIGH       |

Example:

```json
{
  "riskScore": 95,
  "riskLevel": "HIGH",
  "flagged": true,
  "flagReason": "Large transaction, Unusual location, ML detection"
}
```

---

## System Architecture

```text
Frontend (HTML / CSS / JavaScript)
                |
                v
      JWT Authentication
                |
                v
      Spring Boot REST APIs
                |
      --------------------
      |                  |
      v                  v
Rule-Based Engine   ML Detection Engine
      |                  |
      --------------------
                |
                v
        Risk Scoring Engine
                |
                v
          PostgreSQL
                |
                v
      Admin Monitoring Dashboard
```

---

## Technology Stack

### Backend

- Java 17
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA

### Database

- PostgreSQL
- H2 Database (Testing Profile)

### Machine Learning

- Weka Random Forest

### Security

- JWT Authentication
- BCrypt Password Encoding

### Documentation

- Swagger / OpenAPI

### Containerization

- Docker
- Docker Compose

### Build Tools

- Maven

---

## Dashboard Features

### User Dashboard

- Secure Login
- Transaction Submission
- Transaction History
- Risk Level Visibility
- Fraud Alert Tracking

### Admin Dashboard

- Fraud Monitoring
- Risk Analytics
- Alert Management
- Fraud Statistics
- Risk Distribution Charts
- Alert Resolution Workflow

---

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI Specification:

```text
http://localhost:8080/v3/api-docs
```

---

## REST API Endpoints

### Authentication

#### Register User

```http
POST /api/auth/register
```

#### Login User

```http
POST /api/auth/login
```

Returns JWT token for authenticated requests.

---

### Transactions

#### Create Transaction

```http
POST /api/transactions
```

#### Get User Transactions

```http
GET /api/transactions/user/{username}
```

#### Get Transaction By ID

```http
GET /api/transactions/{id}
```

#### Get Flagged Transactions

```http
GET /api/transactions/flagged
```

---

### Fraud Alerts

#### Get All Alerts

```http
GET /api/fraud-alerts
```

#### Get User Alerts

```http
GET /api/fraud-alerts/user/{username}
```

#### Resolve Alert

```http
POST /api/fraud-alerts/{id}/resolve
```

---

### Dashboard Analytics

#### Dashboard Summary

```http
GET /api/admin/dashboard
```

Returns:

```json
{
  "totalTransactions": 120,
  "totalFlaggedTransactions": 18,
  "fraudPercentage": 15.0,
  "totalUsers": 25,
  "highRiskTransactions": 8,
  "recentFraudAlerts": 12
}
```

---

## Local Development Setup

### Prerequisites

- Java 17
- Maven
- PostgreSQL
- Docker (Optional)

---

### Environment Variables

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/frauddb"
$env:DB_USERNAME="fraud_user"
$env:DB_PASSWORD="fraud_password"
```

Optional:

```powershell
$env:APP_RESET_DATA="false"
```

---

### Build Application

```powershell
.\mvnw.cmd clean package
```

---

### Run Application

```powershell
java -jar target\fraud-detection-system-0.0.1-SNAPSHOT.jar
```

Application URL:

```text
http://localhost:8080
```

---

## Docker Deployment

### Build Image

```powershell
docker build -t fraud-detection-system .
```

### Run Using Docker Compose

```powershell
docker compose up --build
```

### Run Docker Image

```powershell
docker run -p 8080:8080 `
-e DB_URL=jdbc:postgresql://host.docker.internal:5432/frauddb `
-e DB_USERNAME=fraud_user `
-e DB_PASSWORD=fraud_password `
fraud-detection-system
```

---

## Testing

Run all tests:

```powershell
.\mvnw.cmd test
```

Verified:

```text
Tests Run: 5
Failures: 0
Errors: 0
```

---

## Future Enhancements

- Kafka-based Real-Time Event Streaming
- Redis Caching
- Role-Based Access Control (RBAC)
- Flyway Database Migrations
- React Frontend
- Advanced ML Models
- Real-Time Fraud Notifications
- Kubernetes Deployment

---

## Project Highlights

- Secure JWT Authentication
- PostgreSQL Data Persistence
- Real-Time Fraud Detection
- Machine Learning Assisted Analysis
- Risk Scoring Engine
- Fraud Alert Management
- Interactive Monitoring Dashboard
- Swagger API Documentation
- Dockerized Deployment
- Clean Layered Architecture

---

## Author

**Akash Rao**

Software Developer | Java | Spring Boot | REST APIs | SQL | PostgreSQL

Built as a portfolio project to demonstrate backend development, security, fraud analytics, database management, API design, and machine-learning integration.
