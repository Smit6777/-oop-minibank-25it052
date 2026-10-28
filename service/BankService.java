package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import model.annotation.Audit;
import util.AuditLogger;
import util.StatementFormatter;
import util.TransactionLog;
import util.Validator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BankService {

    private Repository<Account> repository = new Repository<>();
    private Path logsFolder = Paths.get("logs");

    public BankService() {
        // make sure logs folder exists
        try { java.nio.file.Files.createDirectories(logsFolder); } catch (Exception ignored) {}
    }

    @Audit("Open Account")
    public Account openAccount(String ownerName, String type, long openingBalance) {
        AuditLogger.log(this, "openAccount");

        if (ownerName == null || ownerName.isBlank()) {
            System.out.println("Owner name cannot be empty.");
            return null;
        }

        Account account;
        switch (type.toUpperCase()) {
            case "SAVINGS":
                account = new SavingsAccount(ownerName, openingBalance, 500);
                break;
            case "CURRENT":
                account = new CurrentAccount(ownerName, openingBalance, 2000);
                break;
            case "FD":
                account = new FixedDepositAccount(ownerName, openingBalance);
                break;
            default:
                System.out.println("Unknown account type: " + type);
                return null;
        }

        repository.save(account);
        TransactionLog.append(logsFolder.resolve("transactions.log"),
                "OPEN_ACCOUNT, " + account.getAccountNumber() + ", " + ownerName);
        System.out.println("Account opened: " + account.getAccountNumber() + " for " + ownerName);
        return account;
    }

    @Audit("Deposit")
    public void deposit(String accountNumber, long amount) {
        AuditLogger.log(this, "deposit");

        Account account = repository.findById(accountNumber);
        if (account == null) {
            System.out.println("Account not found: " + accountNumber);
            return;
        }
        try {
            account.deposit(amount);
            TransactionLog.append(logsFolder.resolve("transactions.log"),
                    "DEPOSIT, " + amount + ", " + accountNumber);
            System.out.println("Deposited Rs." + amount + " to " + accountNumber + ". New balance: Rs." + account.getBalance());
        } catch (InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    @Audit("Withdraw")
    public void withdraw(String accountNumber, long amount) {
        AuditLogger.log(this, "withdraw");

        Account account = repository.findById(accountNumber);
        if (account == null) {
            System.out.println("Account not found: " + accountNumber);
            return;
        }
        try {
            account.withdraw(amount);
            TransactionLog.append(logsFolder.resolve("transactions.log"),
                    "WITHDRAW, " + amount + ", " + accountNumber);
            System.out.println("Withdrew Rs." + amount + " from " + accountNumber + ". New balance: Rs." + account.getBalance());
        } catch (BankException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    @Audit("Transfer")
    public void transfer(String fromId, String toId, long amount) {
        AuditLogger.log(this, "transfer");

        Account from = repository.findById(fromId);
        Account to = repository.findById(toId);
        if (from == null) { System.out.println("Source account not found: " + fromId); return; }
        if (to == null)   { System.out.println("Target account not found: " + toId);   return; }

        try {
            TransferService.safeTransfer(from, to, amount);
            TransactionLog.append(logsFolder.resolve("transactions.log"),
                    "TRANSFER, " + amount + ", " + fromId + " -> " + toId);
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    @Audit("Statement")
    public void statement(String accountNumber) {
        AuditLogger.log(this, "statement");

        Account account = repository.findById(accountNumber);
        if (account == null) {
            System.out.println("Account not found: " + accountNumber);
            return;
        }
        System.out.println(StatementFormatter.buildStatement(account));
    }

    @Audit("List Accounts")
    public void listAccounts() {
        AuditLogger.log(this, "listAccounts");

        ArrayList<Account> all = repository.findAll();
        if (all.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        for (Account acc : all) {
            System.out.println(acc.toString());
        }
    }

    public Repository<Account> getRepository() {
        return repository;
    }

    public void setRepository(Repository<Account> repository) {
        this.repository = repository;
    }
}
