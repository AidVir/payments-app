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
cd payments-app
./gradlew bootRun
```

#### Running all unit and integration tests

```
./gradlew test 
```

#### On **Windows (CMD or PowerShell)**

```cmd
git clone https://github.com/AidVir/payments-app.git
cd payments-app
gradlew.bat bootRun
```

#### Running all unit and integration tests

```
gradlew.bat test 
```

## 📦 API Endpoints

### 0. All of the requests below can be accessed by joining Postman workspace below :

[Join Postman workspace here](https://app.getpostman.com/join-team?invite_code=b0ec1e37640cd882db1543b857cd2ec2cf1802a931ce7ba94a117843cc398147&target_code=22f535309955f689f779d81cc3093aab)

--- 

### 1. Create a Payment

**POST** `/api/payments`

**Request:**

### TYPE1: Only for EUR, requires `details`

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

### TYPE2: Only for USD, `details` is optional

```json
{
  "amount": 2500.00,
  "currency": "USD",
  "debtorIban": "LT333333333333333333",
  "creditorIban": "LT444444444444444444",
  "type": "TYPE2"
}
```

### TYPE3: EUR or USD, requires `creditorBankBic`

```json
{
  "amount": 500.00,
  "currency": "EUR",
  "debtorIban": "LT555555555555555555",
  "creditorIban": "LT666666666666666666",
  "type": "TYPE3",
  "creditorBankBic": "BANKLT21"
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