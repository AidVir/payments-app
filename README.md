# 💳 Payments Service

A Spring Boot REST API for managing payments as a task for Baltic Amadeus.

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java 17+
- Gradle (or use the included Gradle Wrapper)

---

### ▶️ Run the App (One Command)

### 🔄 OS-Specific Setup

#### On **Linux / macOS**

```bash
git clone https://github.com/AidVir/payments-app.git
cd payments
./gradlew bootRun
```

#### Running all unit and integration tests

```
./gradlew test 
```

#### On **Windows (CMD or PowerShell)**

```cmd
git clone https://github.com/AidVir/payments-app.git
cd payments
gradlew.bat bootRun
```

#### Running all unit and integration tests

```
gradlew.bat test 
```

## 📦 API Endpoints

### 1. Create a Payment

**POST** `/api/payments`

**Request:**

```json
{
  "amount": 150.00,
  "currency": "EUR",
  "debtorIban": "LT111111111111111111",
  "creditorIban": "LT222222222222222222",
  "type": "TYPE1",
  "details": "invoice #123"
}
```

---

### 2. Cancel a Payment

**POST** `/api/payments/{id}/cancel`


---

### 3. Get All Payments

**GET** `/api/payments`

---

### 4. Get Payment by ID

**GET** `/api/payments/{id}`

---

### 5. Filter by Amount Range

**GET** `/api/payments/filter?minAmount=100&maxAmount=500`

---

### 6. Get Notification Logs by Payment ID

**GET** `/api/payments/{paymentId}/notification`

---

### 7. Get All Notification Logs

**GET** `/api/notifications`

---

## 🔧 Configuration

You can override notification URLs via `application.properties` or environment variables.

**Example (`application.properties`):**

```properties
notifications.type1-url=https://service-type1/notify
notifications.type2-url=https://service-type2/notify
```

---