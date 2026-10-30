# MiniBank

A console-based banking application developed as part of the Object-Oriented Programming (Java) laboratory coursework. The project covers core Java concepts ranging from basic OOP to multi-threading, file handling, and generic data storage across 14 practical sessions.

## Student Details
- **Name:** Smit Makwana
- **Roll No:** 25IT052
- **Subject:** Object-Oriented Programming with Java

## Project Overview
MiniBank simulates basic banking operations like creating accounts, depositing money, withdrawing with balance checks, and transferring funds between accounts. As the practicals progressed, features were added step by step:
- Account inheritance (`SavingsAccount`, `CurrentAccount`, `FixedDepositAccount`)
- Custom exceptions for handling invalid amounts and insufficient funds
- Thread safety using synchronization and thread pools to handle concurrent transactions
- File persistence using serialization and transaction logging with Java NIO
- Collections and generic repository for storing and managing accounts

## Project Structure
```
MINIBANK/
├── MiniBank.java              # Main class with menu and test execution
├── model/                     # Account types, records, enums, interfaces
│   ├── Account.java
│   ├── SavingsAccount.java
│   ├── CurrentAccount.java
│   ├── FixedDepositAccount.java
│   ├── Customer.java
│   ├── BankInfo.java
│   ├── Command.java
│   ├── MenuOption.java
│   ├── TransactionType.java
│   ├── Transactable.java
│   ├── InterestBearing.java
│   ├── Premium.java
│   └── annotation/            # Custom annotations (@Id, @Audit, @Positive)
│       ├── Id.java
│       ├── Audit.java
│       ├── Positive.java
│       └── MaxLength.java
├── service/                   # Business logic and services
│   ├── BankService.java
│   ├── Repository.java
│   ├── AccountStore.java
│   ├── TransactionProcessor.java
│   ├── AccountWorker.java
│   ├── TransferService.java
│   ├── CommandParser.java
│   └── WithdrawRule.java
├── util/                      # Helpers for persistence, logging, validation
│   ├── StatePersister.java
│   ├── TransactionLog.java
│   ├── ReportGenerator.java
│   ├── AuditLogger.java
│   ├── AnnotationValidator.java
│   ├── StatementFormatter.java
│   ├── TransactionBuffer.java
│   ├── TransactionSession.java
│   └── Validator.java
├── exception/                 # Custom bank exceptions
│   ├── BankException.java
│   ├── InsufficientFundsException.java
│   ├── InvalidAmountException.java
│   └── AccountNotFoundException.java
├── test/
│   └── BankServiceTest.java   # Unit tests
└── lib/
    ├── junit.jar
    └── hamcrest.jar
```

## Practicals Covered

| Lab | Topic | Description |
|---|---|---|
| PR1 | Basic Setup & Menu | Console input/output using Scanner and switch-case menu loop |
| PR2 | Inheritance | Base Account class with Savings and Current account subclasses |
| PR3 | Interfaces | Transactable and InterestBearing interfaces with default methods |
| PR4 | Records & Validation | Immutable records, enums, and regex validation for user input |
| PR5 | Functional Interfaces | Lambda expressions for custom withdrawal rules |
| PR6 | Exception Handling | Custom bank exceptions and try-with-resources with AutoCloseable |
| PR7 | Custom Annotations | Runtime validation engine using reflection and annotations |
| PR8 | Object Cloning | Customer duplication using Cloneable interface |
| PR9 | Multi-Threading | Concurrency testing and method synchronization |
| PR10 | Thread Pool & Safety | ExecutorService thread pool, producer-consumer, and deadlock fix |
| PR11 | Serialization & NIO | Saving account state to file and generating daily report with NIO |
| PR12 | Collections | Storing accounts in HashMap and sorting using Comparator |
| PR13 | Generic Repository | Reusable Repository<T> with reflection-based @Id lookup |
| PR14 | BankService Capstone | Service layer connecting all components with unit tests |

## How to Run
Compile all files and run from terminal:
```bash
javac -d bin exception/*.java model/annotation/*.java model/*.java util/*.java service/*.java MiniBank.java
java -cp bin MiniBank
```
Or directly run the JAR file:
```bash
java -jar MiniBank.jar
```
