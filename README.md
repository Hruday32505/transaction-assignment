# Customer Transaction Service

## Problem Understanding

This project implements a small REST service for managing customer
transactions. A transaction contains a Transaction ID, Customer ID, amount,
currency, transaction type, and transaction status.

The service supports four operations:

1. Create a transaction
2. Retrieve a transaction by Transaction ID
3. Update the status of an existing transaction
4. Retrieve all transactions for a Customer ID

The application uses Spring Boot, Spring Data JPA, and an embedded H2
database provided by the starter project.

## Assumptions and Design Decisions

The application uses a simple Controller → Service → Repository structure.

- The Controller handles HTTP requests and responses.
- The Service contains validation and business rules.
- The Repository handles persistence through Spring Data JPA.
- `Transaction` is a JPA entity with `transactionId` as its primary key.
- `BigDecimal` is used for monetary amounts to avoid floating-point
  precision issues.

### Validation Rules

- Transaction ID is required and must not exceed 50 characters.
- Customer ID is required and must not exceed 50 characters.
- Amount is required, must be greater than zero, and may have at most
  two decimal places.
- Currency must be a three-letter uppercase code such as `INR` or `USD`.
- Transaction type must be one of `PAYMENT`, `REFUND`, or `TRANSFER`.
- Transaction status must be one of `PENDING`, `COMPLETED`, `FAILED`, or
  `CANCELLED`.
- A Transaction ID must be unique.

## Transaction Status Rules

A transaction in `PENDING` status may be changed to:

- `COMPLETED`
- `FAILED`
- `CANCELLED`

`COMPLETED`, `FAILED`, and `CANCELLED` are treated as terminal states and
cannot be changed.

The reason for this rule is to prevent a transaction that has reached a
final state from being changed later to a different outcome.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transactions` | Create a transaction |
| GET | `/api/transactions/{transactionId}` | Get one transaction |
| PATCH | `/api/transactions/{transactionId}/status?status=COMPLETED` | Update transaction status |
| GET | `/api/transactions/customer/{customerId}` | Get all transactions for a customer |

### Error Handling

- `400 Bad Request` — validation failure or invalid status.
- `404 Not Found` — requested transaction does not exist.
- `409 Conflict` — duplicate Transaction ID or invalid status transition.

When a valid Customer ID has no transactions, the API returns `200 OK` with
an empty array.

## Testing

Automated tests cover the main business rules and error cases, including:

- Successful transaction creation
- Validation failure
- Duplicate Transaction ID
- Retrieval of an existing transaction
- Retrieval of a non-existent transaction
- Valid status transition
- Invalid status transition
- Retrieval of customer transactions

The starter application's context-loading test is also retained.

Final test command:

    ./mvnw clean test

Final result:

    Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
    BUILD SUCCESS

Manual REST testing was also performed using curl for successful requests and
important validation and error cases.

## Known Limitations

- H2 is an in-memory database, so transaction data is lost when the
  application stops.
- Authentication and authorization are not implemented because they were
  outside the scope of the exercise.
- The API uses simple string values rather than enums for transaction type
  and status.
- There is no pagination for customer transaction retrieval.

## What I Would Improve With More Time

- Use enums for transaction type and status.
- Add more comprehensive controller/integration tests.
- Add pagination and sorting for customer transaction queries.
- Add structured API error responses with consistent error codes.
- Add database-level constraints and indexes where appropriate.
- Add API documentation using OpenAPI/Swagger.
- Consider a persistent database for a production environment.
