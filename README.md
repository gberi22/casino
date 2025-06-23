# Casino Balance Management API

A robust, thread-safe Spring Boot API system for managing user accounts and balance operations in a casino environment. This system handles user registration, balance management, and transaction history with proper concurrency control using application-level locking.

## Features

- **User Account Management**: Create and manage user accounts with automatic zero balance initialization
- **Balance Operations**: Secure deposit and withdrawal operations with validation
- **Transaction History**: Complete audit trail of all balance changes
- **Thread Safety**: Concurrent request handling with ReentrantLock-based synchronization per user
- **Real-time Balance Tracking**: Instant balance updates and retrieval


## API Endpoints

### User Management

#### Create User Account
```http
POST /api/user/create
Content-Type: application/json

{
  "username": "player123"
}
```

**Response:**
```http
200 OK
```

#### Deposit Money
```http
POST /api/user/deposit
Content-Type: application/json

{
  "username": "player123",
  "amount": 100.00
}
```

**Response:**
```http
200 OK
```

#### Withdraw Money
```http
POST /api/user/withdraw
Content-Type: application/json

{
  "username": "player123",
  "amount": 25.00
}
```

**Response:**
```http
200 OK
```

#### Get Balance
```http
GET /api/user/balance?username=player123
```

**Response:**
```json
{
  "balance": 75.00
}
```

### Transaction History

#### Get User Transactions
```http
GET /api/transactions?username=player123
```

**Response:**
```json
{
  "transactions": [
    {
      "id": 1,
      "user": {
        "id": 1,
        "username": "player123",
        "balance": 75.00,
        "createdAt": "2025-06-23T10:30:00",
        "updatedAt": "2025-06-23T10:40:00"
      },
      "amount": 25.00,
      "type": "WITHDRAWAL",
      "timestamp": "2025-06-23T10:40:00"
    },
    {
      "id": 2,
      "user": {
        "id": 1,
        "username": "player123",
        "balance": 100.00,
        "createdAt": "2025-06-23T10:30:00",
        "updatedAt": "2025-06-23T10:35:00"
      },
      "amount": 100.00,
      "type": "DEPOSIT",
      "timestamp": "2025-06-23T10:35:00"
    }
  ]
}
```

### Key Benefits:
- **Per-User Locking**: Each user has their own lock, allowing concurrent operations for different users
- **Memory Efficient**: Locks are created on-demand and stored in ConcurrentHashMap
- **Deadlock Prevention**: Single lock per operation prevents circular dependencies
- **Transaction Safety**: Combined with `@Transactional` for database consistency

```
src/main/java/org/example/casino/
├── CasinoApplication.java          # Main Spring Boot application
├── api/
│   ├── TransactionsController.java # Transaction history endpoints
│   ├── UserController.java         # User management endpoints
│   └── UserExceptionHandler.java   # Global exception handling
├── model/
│   ├── APIErrors.java              # Error response model
│   ├── MoneyDepositRequest.java    # Deposit request DTO
│   ├── MoneyWithdrawRequest.java   # Withdrawal request DTO
│   ├── Transaction.java            # Transaction entity
│   ├── TransactionResponse.java    # Transaction response DTO
│   ├── User.java                   # User entity
│   ├── UserRequest.java            # User request DTO
│   └── UserResponse.java           # User response DTO
├── repository/
│   ├── TransactionRepository.java  # Transaction data access
│   └── UserRepository.java         # User data access
└── service/
    ├── TransactionsService.java    # Transaction business logic
    └── UserService.java            # User business logic
```