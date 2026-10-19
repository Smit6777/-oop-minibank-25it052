import java.util.Scanner;

public class MiniBank {
    public static void main(String[] args) {
        BankInfo info = new BankInfo("MiniBank", "Main Branch");
        System.out.println("=== Welcome to " + info.name() + " (" + info.branch() + ") ===");
        
        Account[] accounts = new Account[3];
        accounts[0] = new SavingsAccount("Alice", 2000, 500);
        accounts[1] = new CurrentAccount("Bob", 1000, 2000);
        accounts[2] = new FixedDepositAccount("Charlie", 10000);

        System.out.println("\n--- Performing Transactions ---");
        accounts[0].deposit(500);
        accounts[1].deposit(200);
        accounts[2].withdraw(1000);
        
        System.out.println("\n--- Printing Accounts (Testing toString) ---");
        for (Account acc : accounts) {
            System.out.println(acc.toString());
        }

        System.out.println("\n--- Testing Polymorphism and Pattern Matching instanceof ---");
        for (Account acc : accounts) {
            System.out.println("Account: " + acc.getAccountNumber() + 
                               " | Type: " + acc.getClass().getSimpleName() + 
                               " | Interest Rate: " + acc.interestRate() + "%");
            
            if (acc instanceof SavingsAccount sa) {
                System.out.println("  -> Special Savings Info: Minimum Balance required is Rs." + sa.getMinBalance());
            } else if (acc instanceof CurrentAccount ca) {
                System.out.println("  -> Special Current Info: Overdraft Limit is Rs." + ca.getOverdraftLimit());
            } else if (acc instanceof FixedDepositAccount) {
                System.out.println("  -> Special Fixed Deposit Info: Account is locked (canWithdraw: " + acc.canWithdraw(100) + ")");
            }
        }
        System.out.println("------------------------------\n");

        System.out.println("\n--- Testing PR4 Features ---");
        System.out.println("Valid Mobile (9876543210): " + Validator.isValidMobile("9876543210"));
        System.out.println("Invalid Mobile (123456789): " + Validator.isValidMobile("123456789"));
        
        System.out.println("Valid Email (test@example.com): " + Validator.isValidEmail("test@example.com"));
        System.out.println("Invalid Email (test.example.com): " + Validator.isValidEmail("test.example.com"));
        
        System.out.println("Valid PAN (ABCDE1234F): " + Validator.isValidPan("ABCDE1234F"));
        System.out.println("Invalid PAN (ABCD12345F): " + Validator.isValidPan("ABCD12345F"));
        
        System.out.println("Valid IFSC (SBIN0123456): " + Validator.isValidIfsc("SBIN0123456"));
        System.out.println("Invalid IFSC (SBIN1234567): " + Validator.isValidIfsc("SBIN1234567"));

        System.out.println("\nTesting Command Parser:");
        String sampleCommand = "DEPOSIT AC0001 500";
        Command parsedCommand = CommandParser.parse(sampleCommand);
        if (parsedCommand != null) {
            System.out.println("Parsed Command -> Type: " + parsedCommand.type() + 
                               ", Account: " + parsedCommand.accountNumber() + 
                               ", Amount: " + parsedCommand.amount());
        }

        System.out.println("\nTesting Statement Formatter:");
        System.out.println(StatementFormatter.buildStatement(accounts[0]));
        System.out.println("------------------------------\n");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            System.out.println("\nMenu Options:");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");
            
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                scanner.next(); 
                continue;
            }
            
            int choice = scanner.nextInt();
            
            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.EXIT;
                default -> null;
            };
            
            if (option == null) {
                System.out.println("Invalid choice. Please select a valid option.");
                continue;
            }
            
            String message = switch (option) {
                case OPEN_ACCOUNT -> "Open Account — to be implemented in a later lab";
                case DEPOSIT -> "Deposit — to be implemented in a later lab";
                case WITHDRAW -> "Withdraw — to be implemented in a later lab";
                case TRANSFER -> "Transfer — to be implemented in a later lab";
                case EXIT -> "Goodbye!";
            };
            
            System.out.println(message);
            
            if (option == MenuOption.EXIT) {
                running = false;
            }
        }
        
        scanner.close();
    }
}
