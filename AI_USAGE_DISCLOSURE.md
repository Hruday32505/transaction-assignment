# AI Usage Disclosure

I used ChatGPT as an AI coding assistant throughout this exercise.

## How I Used AI

I used AI to help with the implementation and review of the complete
transaction-processing service. This included:

- Understanding the starter Spring Boot project and its existing structure.
- Designing the Controller, Service, Repository, Entity, and exception-handling
  structure.
- Implementing the four required transaction operations.
- Designing and implementing validation rules.
- Designing transaction status-transition rules.
- Implementing REST endpoints and HTTP error handling.
- Writing automated JUnit tests using Mockito.
- Reviewing the implementation for consistency and potential issues.
- Debugging errors encountered while running and testing the application.
- Preparing the README documentation and explaining the design decisions.

## Significant AI Contributions

AI helped generate and refine a substantial portion of the implementation,
including the transaction entity, repository query, service-layer business
logic, controller endpoints, exception handling, and automated tests.

A Controller -> Service -> Repository architecture was used because it keeps
HTTP handling, business logic, and persistence responsibilities separate while
remaining simple enough for the scope of the exercise.

AI also suggested treating PENDING as the only mutable transaction state and
treating COMPLETED, FAILED, and CANCELLED as terminal states. I adopted this
because it provides a simple transaction lifecycle and prevents a completed
or failed transaction from later being changed to a contradictory outcome.

## What I Changed or Corrected

I did not rely on generated code without testing it. I implemented the
suggestions in the starter project, ran the application, manually exercised
the REST endpoints with curl, and investigated failures when they occurred.

During development, the customer transaction lookup initially returned an
empty list even though the individual transaction could be retrieved. I
inspected the Entity, Repository, Service, and Controller implementations and
used temporary diagnostic output to identify the behavior. The repository
query was then verified to correctly return the customer's transaction.

There was also an application startup failure involving `List`. This was
resolved by correcting the required `java.util.List` imports and performing a
clean Maven build.

While testing the endpoints, some 404 responses were caused by the running
application not having the latest endpoint changes loaded. Restarting the
Spring Boot application resolved this. I also identified that the configured
H2 database is in-memory, so restarting the application clears the test data.
This was taken into account during manual testing.

## How I Verified the Final Result

I tested the REST API manually using curl, including successful requests and
important failure cases:

- Creating a valid transaction.
- Rejecting an invalid amount.
- Rejecting a duplicate Transaction ID.
- Retrieving an existing transaction.
- Handling a non-existent transaction.
- Updating PENDING to COMPLETED.
- Rejecting COMPLETED to FAILED.
- Rejecting an invalid transaction status.
- Retrieving transactions belonging to a customer.
- Returning an empty list when a customer has no transactions.

I also added automated tests covering the main business rules and error
conditions.

The final test suite was executed using:

    ./mvnw clean test

Final result:

    Tests run: 9
    Failures: 0
    Errors: 0
    Skipped: 0

    BUILD SUCCESS
    