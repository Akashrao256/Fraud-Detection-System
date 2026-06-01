# Quick Start: Swagger/OpenAPI Documentation

## 🚀 Quick Access

### View Documentation (No Authentication Required)

```
Swagger UI:        http://localhost:8080/swagger-ui.html
OpenAPI JSON:      http://localhost:8080/v3/api-docs
OpenAPI YAML:      http://localhost:8080/v3/api-docs.yaml
```

## 🔐 Authentication Flow

### 1. Get JWT Token

**In Swagger UI:**

1. Scroll to **Authentication** section
2. Click **POST /api/auth/login** → "Try it out"
3. Enter credentials:
   ```json
   {
     "username": "admin",
     "password": "password",
     "location": "New York"
   }
   ```
4. Click **Execute**
5. Copy the `token` value from response

### 2. Authorize Swagger UI

1. Click green **Authorize** button (top-right)
2. Paste token in format: `Bearer <your-token>`
3. Click **Authorize**
4. Click **Close**

### 3. Test Authorized Endpoints

All subsequent calls will automatically include your JWT token.

---

## 📚 API Endpoints Overview

### Authentication (Public)

```
POST   /api/auth/login      → Get JWT token
POST   /api/auth/register   → Create new user
```

### Transactions (Protected)

```
POST   /api/transactions               → Create transaction with fraud check
GET    /api/transactions/user/{name}   → Get user's transactions
GET    /api/transactions/{id}          → Get specific transaction
GET    /api/transactions/flagged       → Get flagged transactions
```

### Fraud Alerts (Protected)

```
GET    /api/fraud-alerts               → Get all alerts
GET    /api/fraud-alerts/unresolved    → Get pending alerts
GET    /api/fraud-alerts/user/{name}   → Get user's alerts
GET    /api/fraud-alerts/{id}          → Get specific alert
POST   /api/fraud-alerts/{id}/resolve  → Resolve an alert
GET    /api/fraud-alerts/type/{type}   → Filter alerts by type
```

### Dashboard (Protected)

```
GET    /api/admin/dashboard → Real-time fraud statistics
```

---

## 💡 Common Tasks

### Register New User

```json
POST /api/auth/register

{
  "username": "john_doe",
  "password": "SecurePass123!",
  "email": "john@example.com",
  "fullName": "John Doe",
  "phoneNumber": "+1-555-0123",
  "location": "San Francisco"
}

Response: 201 Created
```

### Create Transaction

```json
POST /api/transactions
Authorization: Bearer <token>

{
  "username": "john_doe",
  "amount": "1500.50",
  "location": "San Francisco",
  "transactionType": "TRANSFER",
  "description": "Payment for services",
  "recipientAccount": "ACC-987654"
}

Response: 200 OK with risk assessment
```

### Check Dashboard

```
GET /api/admin/dashboard
Authorization: Bearer <token>

Response: 200 OK with statistics:
- Total transactions
- Fraud percentage
- Risk distribution
- High-risk transactions
- Recent alerts
```

---

## 🛠️ Development Tips

### Testing Endpoints

1. Use "Try it out" button in Swagger UI
2. For POST endpoints, edit the example JSON
3. Responses shown with color-coded status codes
4. Green = Success, Red = Error

### Schema Information

- Click on schema name to expand details
- See required fields and data types
- View validation rules and examples

### Response Examples

- Swagger UI shows actual response format
- Check "Response body" section
- Use for understanding data structure

---

## 📊 Tips for Admin Dashboard

The dashboard endpoint provides comprehensive statistics:

```json
{
  "summary": {
    "totalTransactions": 150,
    "totalFlaggedTransactions": 12,
    "fraudPercentage": 8.0,
    "totalUsers": 45,
    "highRiskTransactions": 8,
    "recentFraudAlerts": 3
  },
  "transactionsOverTime": [...],
  "fraudAlertsOverTime": [...],
  "fraudVsSafe": { "fraud": 12, "safe": 138 },
  "riskDistribution": { "LOW": 100, "MEDIUM": 40, "HIGH": 10 },
  "highRiskTransactions": [...],
  "recentFraudAlerts": [...]
}
```

---

## ❓ FAQ

**Q: Token expired, how to get new one?**
A: Call /api/auth/login again with credentials

**Q: Where is my token stored?**
A: Only in browser memory during your Swagger session. It's not saved.

**Q: Can I use the token in my app?**
A: Yes! Copy the token and use in Authorization header: `Authorization: Bearer <token>`

**Q: Which endpoints need authentication?**
A: Everything except `/api/auth/*` and `/swagger-ui/**`

**Q: How long is token valid?**
A: Default 24 hours (86400000 ms). Configure in `application.properties`

---

## 🔗 Related Documentation

- [Full Implementation Guide](./SWAGGER_IMPLEMENTATION.md)
- [API Architecture](./README.md)
- [Security Configuration](./src/main/java/com/frauddetection/config/SecurityConfig.java)

---

**Version:** v1.0 | **Last Updated:** June 1, 2026
