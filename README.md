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

- **Backend**: Java 11, Spring Boot 2.7
- **Database**: H2 (for development), can be configured for production databases
- **Machine Learning**: Weka library for fraud pattern detection
- **Security**: Spring Security
- **Containerization**: Docker

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven
- Docker (for containerization)

### Running the Application

1. Build the application:
   ```
   mvn clean package
   ```

2. Run the application:
   ```
   java -jar target/fraud-detection-system-0.0.1-SNAPSHOT.jar
   ```

### Docker Deployment

1. Build the Docker image:
   ```
   docker build -t fraud-detection-system .
   ```

2. Run the Docker container:
   ```
   docker run -p 8080:8080 fraud-detection-system
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

- Database connection
- Email server for OTP delivery
- Security settings
- Fraud detection thresholds
