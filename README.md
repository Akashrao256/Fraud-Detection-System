# Cloud-Based Fraud Detection System

A real-time fraud detection system built using Java, Spring Boot, and machine learning to intelligently detect suspicious activities. This system secures financial transactions by monitoring patterns and flagging anomalies.

## Features

### Fraud Detection Mechanisms

1. **Multiple Failed Login Attempts**: Flags a user after 3 incorrect login attempts
2. **Large Transaction Detection**: Flags any transaction above ₹5000
3. **Unusual Location Detection**: Flags transactions from unknown locations
4. **Frequent Transactions**: Flags if more than 3 transactions are made within 1 minute
5. **Late-Night Transaction Detection**: Flags transactions between 12 AM and 5 AM

### Technical Features

- RESTful API for transaction processing and fraud detection
- Machine learning model for advanced fraud pattern detection
- Real-time alerts and notifications
- User authentication and authorization
- Comprehensive logging and monitoring

## Technology Stack

- **Backend**: Java 17, Spring Boot
- **Database**: PostgreSQL
- **Machine Learning**: Weka library for fraud pattern detection
- **Security**: Spring Security
- **Containerization**: Docker

## Getting Started

### Prerequisites

- Java 17
- Maven
- PostgreSQL
- Docker (for containerization)

### Running the Application

1. Create a PostgreSQL database and set environment variables:

   ```powershell
   $env:DB_URL="jdbc:postgresql://localhost:5432/frauddb"
   $env:DB_USERNAME="fraud_user"
   $env:DB_PASSWORD="fraud_password"
   ```

2. Build the application:

   ```
   .\mvnw.cmd clean package
   ```

3. Run the application:
   ```
   java -jar target/fraud-detection-system-0.0.1-SNAPSHOT.jar
   ```

### Docker Deployment

1. Build the application jar:

   ```
   .\mvnw.cmd clean package
   ```

2. Run the app with PostgreSQL using Docker Compose:

   ```
   docker compose up --build
   ```

Or build and run only the application image against an existing PostgreSQL database:

   ```
   docker build -t fraud-detection-system .
   ```

   ```
   docker run -p 8080:8080 ^
     -e DB_URL=jdbc:postgresql://host.docker.internal:5432/frauddb ^
     -e DB_USERNAME=fraud_user ^
     -e DB_PASSWORD=fraud_password ^
     fraud-detection-system
   ```

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login a user

### Transactions

- `POST /api/transactions` - Create a new transaction
- `GET /api/transactions/user/{username}` - Get all transactions for a user
- `GET /api/transactions/{id}` - Get transaction by ID

- `GET /api/transactions/flagged` - Get all flagged transactions

### Fraud Alerts

- `GET /api/alerts` - Get all fraud alerts
- `GET /api/alerts/unresolved` - Get all unresolved fraud alerts
- `GET /api/alerts/user/{username}` - Get all fraud alerts for a user
- `GET /api/alerts/{id}` - Get fraud alert by ID
- `POST /api/alerts/{id}/resolve` - Resolve a fraud alert

## Configuration

The application can be configured through the `application.properties` file. Key configurations include:

- PostgreSQL database connection through `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`
- Email server for OTP delivery
- Security settings
- Fraud detection thresholds

Optional:

- `APP_RESET_DATA=true` resets data on startup. The default is `false` so PostgreSQL data persists across restarts.
