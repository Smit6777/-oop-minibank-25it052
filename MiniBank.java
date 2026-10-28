import exception.*;
import model.*;
import service.AccountStore;
import service.AccountWorker;
import service.BankService;
import service.CommandParser;
import service.Repository;
import service.TransactionProcessor;
import service.TransferService;
import service.WithdrawRule;
import util.AnnotationValidator;
import util.AuditLogger;
import util.ReportGenerator;
import util.StatePersister;
import util.TransactionBuffer;
import util.TransactionLog;
import util.TransactionSession;
import util.Validator;
import static util.StatementFormatter.buildStatement;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MiniBank {
    public static void main(String[] args) {
        BankInfo info = new BankInfo("MiniBank", "Main Branch");
        System.out.println("====================================================");
        System.out.println("        MiniBank — OOP Java Lab Project             ");
        System.out.println("        Student : Smit Makwana                      ");
        System.out.println("        Roll No : 25IT052                           ");
        System.out.println("====================================================");
        System.out.println("=== Welcome to " + info.name() + " (" + info.branch() + ") ===");

        // -------------------------------------------------------
        // PR1-PR8: Core demos (kept for viva / lab record)
        // -------------------------------------------------------
        Account[] accounts = new Account[3];
        accounts[0] = new SavingsAccount("Alice", 2000, 500);
        accounts[1] = new CurrentAccount("Bob", 1000, 2000);
        accounts[2] = new FixedDepositAccount("Charlie", 10000);

        System.out.println("\n--- Performing Transactions with Exception Handling ---");
        try {
            accounts[0].deposit(500);
            System.out.println("Deposit of 500 into Alice's account successful.");
        } catch (InvalidAmountException e) {
            System.out.println("Deposit error: " + e.getMessage());
        }
        try {
            accounts[1].deposit(-100);
        } catch (InvalidAmountException e) {
            System.out.println("Expected deposit error: " + e.getMessage());
        }
        try {
            accounts[0].withdraw(3000);
        } catch (InsufficientFundsException e) {
            System.out.println("Expected withdraw error: " + e.getMessage() + " (Shortfall: Rs." + e.getShortfall() + ")");
        } catch (InvalidAmountException e) {
            System.out.println("Invalid amount: " + e.getMessage());
        } finally {
            System.out.println("Account operations check completed.");
        }

        System.out.println("\n--- Testing Transfer with Exceptions ---");
        try {
            accounts[0].transfer(accounts[1], 500);
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        System.out.println("\n--- Testing try-with-resources ---");
        try (TransactionSession session = new TransactionSession("TX-9901")) {
            System.out.println("Executing safe transaction inside auto-closable session...");
        }

        // -------------------------------------------------------
        // PR9: Multi-Threading & Synchronization
        // -------------------------------------------------------
        System.out.println("\n--- Testing PR9 Multi-Threading & Synchronization ---");
        Account sharedAccount = new SavingsAccount("SharedAccount", 0, 0);
        int threadCount = 10;
        int depositsPerThread = 1000;
        long depositAmount = 10;
        long expectedBalance = (long) threadCount * depositsPerThread * depositAmount;

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new AccountWorker(sharedAccount, depositsPerThread, depositAmount), "WorkerThread-" + (i + 1));
            threads[i].start();
        }
        for (int i = 0; i < threadCount; i++) {
            try { threads[i].join(); }
            catch (InterruptedException e) { System.out.println("Thread interrupted: " + e.getMessage()); }
        }
        System.out.println("Expected Balance: Rs." + expectedBalance);
        System.out.println("Actual Synchronized Balance: Rs." + sharedAccount.getBalance());
        System.out.println("----------------------------------------------------\n");

        // -------------------------------------------------------
        // PR10: ThreadPool + Producer-Consumer + Deadlock fix
        // -------------------------------------------------------
        System.out.println("\n--- Testing PR10 ThreadPool, Producer-Consumer & Deadlock Prevention ---");
        TransactionProcessor processor = new TransactionProcessor();
        for (int i = 0; i < 5; i++) {
            final int taskId = i + 1;
            processor.submit(() -> {
                try {
                    accounts[0].deposit(100);
                    System.out.println("Pool task " + taskId + " deposited 100 to Alice's account.");
                } catch (InvalidAmountException e) {
                    System.out.println("Pool error: " + e.getMessage());
                }
            });
        }
        processor.stop();

        TransactionBuffer buffer = new TransactionBuffer();
        Thread producerThread = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                buffer.produce(new Command(TransactionType.DEPOSIT, "AC0001", i * 100));
            }
        }, "ProducerThread");
        Thread consumerThread = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                buffer.consume();
            }
        }, "ConsumerThread");
        producerThread.start(); consumerThread.start();
        try { producerThread.join(); consumerThread.join(); }
        catch (InterruptedException e) { System.out.println("Interrupted: " + e.getMessage()); }

        Thread t1 = new Thread(() -> {
            try { TransferService.safeTransfer(accounts[0], accounts[1], 100); }
            catch (BankException e) { System.out.println(e.getMessage()); }
        }, "Transfer-A-to-B");
        Thread t2 = new Thread(() -> {
            try { TransferService.safeTransfer(accounts[1], accounts[0], 50); }
            catch (BankException e) { System.out.println(e.getMessage()); }
        }, "Transfer-B-to-A");
        t1.start(); t2.start();
        try { t1.join(); t2.join(); }
        catch (InterruptedException e) { System.out.println("Interrupted: " + e.getMessage()); }
        System.out.println("----------------------------------------------------\n");

        // -------------------------------------------------------
        // PR11: Serialization, Transaction Log & NIO Report
        // -------------------------------------------------------
        System.out.println("\n--- Testing PR11 Serialization, Transaction Log & Report ---");
        Path logsFolder = Paths.get("logs");
        Path saveFile   = Paths.get("accounts.dat");
        Path reportFile = Paths.get("report.txt");
        try { Files.createDirectories(logsFolder); } catch (Exception e) { System.out.println(e.getMessage()); }

        TransactionLog.append(logsFolder.resolve("transactions.log"), "DEPOSIT, 500, AC0001");
        TransactionLog.append(logsFolder.resolve("transactions.log"), "DEPOSIT, 300, AC0002");
        TransactionLog.append(logsFolder.resolve("transactions.log"), "WITHDRAW, 200, AC0001");

        StatePersister.save(accounts, saveFile);
        Account[] loaded = StatePersister.load(saveFile);
        System.out.println("Loaded accounts from disk:");
        for (Account acc : loaded) { System.out.println(acc.toString()); }
        ReportGenerator.generate(logsFolder, reportFile);
        System.out.println("----------------------------------------------------\n");

        // -------------------------------------------------------
        // PR12: Collections Framework — AccountStore
        // -------------------------------------------------------
        System.out.println("\n--- Testing PR12 Collections Framework ---");
        AccountStore store = new AccountStore();
        store.add(new SavingsAccount("Priya", 8000, 500));
        store.add(new CurrentAccount("Raj", 3000, 1000));
        store.add(new SavingsAccount("Neha", 15000, 500));
        store.add(new FixedDepositAccount("Arjun", 500));
        store.logTransaction("DEPOSIT 1000 to Priya");
        store.logTransaction("WITHDRAW 500 from Raj");
        System.out.println("All accounts sorted by balance:");
        for (Account acc : store.sortedByBalance()) { System.out.println(acc.toString()); }
        System.out.println("\nTransaction history:");
        for (String entry : store.getTransactionHistory()) { System.out.println(" - " + entry); }
        System.out.println("----------------------------------------------------\n");

        // -------------------------------------------------------
        // PR13: Generic Repository with @Id reflection
        // -------------------------------------------------------
        System.out.println("\n--- Testing PR13 Generic Repository ---");
        Repository<Account> repo = new Repository<>();
        repo.saveAll(
            new SavingsAccount("Kiran", 5000, 500),
            new CurrentAccount("Mehul", 2000, 1000),
            new FixedDepositAccount("Pooja", 20000)
        );
        System.out.println("All accounts in repository:");
        for (Account acc : repo.findAll()) { System.out.println(acc.toString()); }
        String firstId = repo.findAll().get(0).getAccountNumber();
        Account found = repo.findById(firstId);
        System.out.println("\nFetched by ID (" + firstId + "): " + found);
        boolean deleted = repo.delete(firstId);
        System.out.println("Deleted " + firstId + ": " + deleted);
        System.out.println("Remaining accounts: " + repo.findAll().size());
        Long[] balances = {5000L, 2000L, 20000L};
        System.out.println("Average balance: Rs." + Repository.average(balances));
        System.out.println("----------------------------------------------------\n");

        // -------------------------------------------------------
        // PR14: BankService — wires everything together
        // -------------------------------------------------------
        System.out.println("\n--- Testing PR14 BankService ---");
        BankService bankService = new BankService();

        // Load saved accounts into the service repository on startup
        Account[] startupAccounts = StatePersister.load(saveFile);
        if (startupAccounts != null) {
            Repository<Account> serviceRepo = new Repository<>();
            for (Account acc : startupAccounts) { serviceRepo.save(acc); }
            bankService.setRepository(serviceRepo);
            System.out.println("Accounts loaded from disk into BankService.");
        }

        // Demonstrate BankService operations (audit logs printed automatically)
        Account newAcc = bankService.openAccount("Smit", "SAVINGS", 1000);
        if (newAcc != null) {
            bankService.deposit(newAcc.getAccountNumber(), 500);
            bankService.withdraw(newAcc.getAccountNumber(), 200);
            bankService.statement(newAcc.getAccountNumber());
        }
        bankService.listAccounts();

        // Batch processing with TransactionProcessor over ConcurrentHashMap store
        System.out.println("\nBatch processing with thread pool:");
        TransactionProcessor batchProcessor = new TransactionProcessor();
        for (int i = 0; i < 5; i++) {
            final long amt = (i + 1) * 100L;
            batchProcessor.submit(() -> bankService.deposit(
                    newAcc != null ? newAcc.getAccountNumber() : "", amt));
        }
        batchProcessor.stop();

        // Generate report as menu-accessible action
        System.out.println("\nGenerating end-of-day report:");
        ReportGenerator.generate(logsFolder, reportFile);
        System.out.println("----------------------------------------------------\n");

        // -------------------------------------------------------
        // Misc demos kept for viva
        // -------------------------------------------------------
        System.out.println("\n--- Printing Accounts (Testing toString) ---");
        for (Account acc : accounts) { System.out.println(acc.toString()); }

        System.out.println("\n--- Testing Polymorphism and Default Interface Method ---");
        for (Account acc : accounts) {
            System.out.println("Account: " + acc.getAccountNumber() +
                               " | Rate: " + acc.interestRate() + "%" +
                               " | Yearly Interest: Rs." + acc.yearlyInterest());
            if (acc instanceof Premium) {
                System.out.println("  -> " + acc.getAccountNumber() + " is marked as a Premium account.");
            }
        }
        System.out.println("------------------------------\n");

        System.out.println("\n--- Testing WithdrawRule (Functional Interface) ---");
        WithdrawRule dailyLimitRule = (account, amount) -> amount <= 5000;
        WithdrawRule positiveBalanceRule = (account, amount) -> account.getBalance() >= amount;
        System.out.println("Daily limit check (Withdraw 6000): " + dailyLimitRule.allow(accounts[0], 6000));
        System.out.println("Positive balance check (Withdraw 500): " + positiveBalanceRule.allow(accounts[0], 500));
        System.out.println("------------------------------\n");

        System.out.println("\n--- Testing PR7 Custom Annotations & Reflection Validator ---");
        Account badAccount = new SavingsAccount("Dave", -500, 500);
        String[] errors = AnnotationValidator.validate(badAccount);
        System.out.println("Validation errors for bad account (opening balance -500):");
        for (String err : errors) { System.out.println(" - " + err); }
        System.out.println("------------------------------\n");

        System.out.println("\n--- Testing PR4 Features ---");
        System.out.println("Valid Mobile (9876543210): " + Validator.isValidMobile("9876543210"));
        System.out.println("Valid Email (test@example.com): " + Validator.isValidEmail("test@example.com"));
        System.out.println("\nTesting Command Parser:");
        String sampleCommand = "DEPOSIT AC0001 500";
        Command parsedCommand = CommandParser.parse(sampleCommand);
        if (parsedCommand != null) {
            System.out.println("Parsed Command -> Type: " + parsedCommand.type() +
                               ", Account: " + parsedCommand.accountNumber() +
                               ", Amount: " + parsedCommand.amount());
        }
        System.out.println("\nTesting Statement Formatter (Static Import):");
        System.out.println(buildStatement(accounts[0]));
        System.out.println("------------------------------\n");

        // -------------------------------------------------------
        // PR14: Live interactive menu wired to BankService
        // -------------------------------------------------------
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n========== MiniBank Menu ==========");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. View Statement");
            System.out.println("6. List All Accounts");
            System.out.println("7. Generate Report");
            System.out.println("8. Exit");
            System.out.print("Enter your choice (1-8): ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter owner name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter type (SAVINGS / CURRENT / FD): ");
                    String type = scanner.nextLine().trim();
                    System.out.print("Enter opening balance: ");
                    long bal = scanner.nextLong();
                    bankService.openAccount(name, type, bal);
                }
                case 2 -> {
                    System.out.print("Enter account number: ");
                    String accNo = scanner.nextLine().trim();
                    System.out.print("Enter amount to deposit: ");
                    long amt = scanner.nextLong();
                    bankService.deposit(accNo, amt);
                }
                case 3 -> {
                    System.out.print("Enter account number: ");
                    String accNo = scanner.nextLine().trim();
                    System.out.print("Enter amount to withdraw: ");
                    long amt = scanner.nextLong();
                    bankService.withdraw(accNo, amt);
                }
                case 4 -> {
                    System.out.print("Enter source account number: ");
                    String from = scanner.nextLine().trim();
                    System.out.print("Enter target account number: ");
                    String to = scanner.nextLine().trim();
                    System.out.print("Enter amount: ");
                    long amt = scanner.nextLong();
                    bankService.transfer(from, to, amt);
                }
                case 5 -> {
                    System.out.print("Enter account number: ");
                    String accNo = scanner.nextLine().trim();
                    bankService.statement(accNo);
                }
                case 6 -> bankService.listAccounts();
                case 7 -> ReportGenerator.generate(logsFolder, reportFile);
                case 8 -> {
                    // Save all accounts on exit
                    ArrayList<Account> allAccounts = bankService.getRepository().findAll();
                    StatePersister.save(allAccounts.toArray(new Account[0]), saveFile);
                    System.out.println("Accounts saved. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Enter 1-8.");
            }
        }
        scanner.close();
    }
}
