# MiniBank — OOP Java Project

A student banking application built lab by lab across 14 practicals.  
Each PR maps to one lab session, so the Git history is your lab record.

---

## Project Structure

```
MINIBANK/
├── MiniBank.java              # Main class — all PR demos + interactive menu
├── model/
│   ├── Account.java           # Abstract base (Serializable, Comparable)
│   ├── SavingsAccount.java    # Savings with min-balance rule
│   ├── CurrentAccount.java    # Current with overdraft limit
│   ├── FixedDepositAccount.java
│   ├── Customer.java          # Serializable customer entity
│   ├── BankInfo.java          # Record type
│   ├── Command.java           # Record for parsed commands
│   ├── MenuOption.java        # Enum for menu choices
│   ├── TransactionType.java   # Enum for DEPOSIT / WITHDRAW
│   ├── Transactable.java      # Interface: deposit / withdraw / getBalance
│   ├── InterestBearing.java   # Interface: interestRate / yearlyInterest (default)
│   └── Premium.java           # Marker interface
│   └── annotation/
│       ├── Id.java            # @Id — marks the primary key field
│       ├── Audit.java         # @Audit — marks service methods for logging
│       ├── Positive.java      # @Positive — field must be > 0
│       └── MaxLength.java     # @MaxLength — field max character length
├── service/
│   ├── BankService.java       # PR14: wires everything — open/deposit/withdraw/transfer/statement
│   ├── Repository.java        # PR13: generic HashMap store with @Id reflection
│   ├── AccountStore.java      # PR12: HashMap + ArrayList history + sortedByBalance
│   ├── TransactionProcessor.java  # PR10: fixed thread pool + ConcurrentHashMap
│   ├── AccountWorker.java     # PR9: Runnable for concurrent deposits
│   ├── TransferService.java   # PR10: deadlock-safe transfer (lock ordering)
│   ├── CommandParser.java     # PR4: parses "DEPOSIT AC0001 500" strings
│   └── WithdrawRule.java      # PR5: functional interface for withdrawal rules
├── util/
│   ├── StatePersister.java    # PR11: ObjectOutputStream / ObjectInputStream
│   ├── TransactionLog.java    # PR11: NIO append-only log
│   ├── ReportGenerator.java   # PR11: NIO directory walk + end-of-day report
│   ├── AuditLogger.java       # PR14: reflective logger for @Audit methods
│   ├── AnnotationValidator.java # PR7: reflection-based field validator
│   ├── StatementFormatter.java  # PR4: account statement builder
│   ├── TransactionBuffer.java   # PR10: wait/notify producer-consumer buffer
│   ├── TransactionSession.java  # PR6: AutoCloseable session (try-with-resources)
│   └── Validator.java         # PR4: mobile / email / PAN / IFSC regex checks
├── exception/
│   ├── BankException.java
│   ├── InsufficientFundsException.java
│   ├── InvalidAmountException.java
│   └── AccountNotFoundException.java
├── test/
│   └── BankServiceTest.java   # PR14: 8 JUnit 4 tests
├── lib/
│   ├── junit.jar
│   └── hamcrest.jar
├── MiniBank.jar               # Runnable JAR
├── accounts.dat               # Serialized account state (auto-saved on exit)
├── logs/transactions.log      # Append-only transaction log
└── report.txt                 # End-of-day summary report
```

---

## How to Build & Run

### Compile
```powershell
javac -d bin exception/*.java model/annotation/*.java model/*.java util/*.java service/*.java MiniBank.java
```

### Run
```powershell
java -cp bin MiniBank
```

### Or use the JAR
```powershell
java -jar MiniBank.jar
```

---

## How to Run JUnit Tests

```powershell
# Compile tests
javac -d bin -cp "bin;lib/junit.jar;lib/hamcrest.jar" test/BankServiceTest.java

# Run tests
java -cp "bin;lib/junit.jar;lib/hamcrest.jar" org.junit.runner.JUnitCore BankServiceTest
```

**Test Results:** 8/8 tests pass ✅

| # | Test | What it checks |
|---|------|---------------|
| 1 | `testDepositIncreasesBalance` | Balance grows after deposit |
| 2 | `testDepositZeroThrowsException` | Zero deposit → `InvalidAmountException` |
| 3 | `testDepositNegativeThrowsException` | Negative deposit → `InvalidAmountException` |
| 4 | `testWithdrawOverLimitThrowsException` | Below min-balance → `InsufficientFundsException` |
| 5 | `testWithdrawReducesBalance` | Balance shrinks after withdraw |
| 6 | `testConcurrentDepositsAreCorrect` | 10 threads × 100 deposits stay correct (sync check) |
| 7 | `testAccountNumbersAreUnique` | Each account gets a unique number |
| 8 | `testCompareToSortsByAccountNumber` | `Comparable` orders by account number |

---

## PR Summary

| PR | Topic |
|----|-------|
| PR1 | Classes, objects, constructors, basic I/O |
| PR2 | Inheritance, abstract classes, polymorphism |
| PR3 | Interfaces, default methods, marker interfaces |
| PR4 | Records, enums, static imports, string parsing |
| PR5 | Functional interfaces, lambdas |
| PR6 | Exception handling, try-with-resources |
| PR7 | Custom annotations, reflection, validation |
| PR8 | Inner classes, generics intro |
| PR9 | Threads, synchronization, race conditions |
| PR10 | Thread pool, producer-consumer, deadlock fix |
| PR11 | Serialization, NIO file I/O, report generation |
| PR12 | Collections — HashMap, ArrayList, Comparator, Comparable |
| PR13 | Generic Repository with @Id reflection, bounded type params |
| PR14 | BankService capstone — wires all layers + JUnit tests |

---

*Student: Smit Makwana | Roll No: 25IT052 | OOP Lab*
