import java.util.Scanner;

public class MiniBank {
    public static void main(String[] args) {
        BankInfo info = new BankInfo("MiniBank", "Main Branch");
        System.out.println("=== Welcome to " + info.name() + " (" + info.branch() + ") ===");
        
        Account[] accounts = new Account[3];
        accounts[0] = new Account("Alice", 1000);
        accounts[1] = new Account("Bob"); 
        accounts[2] = new Account("Charlie", 5000);

        System.out.println("\n--- Performing Transactions ---");
        accounts[0].deposit(500);
        System.out.println("Deposited 500 to Alice's account.");
        
        accounts[1].deposit(200);
        System.out.println("Deposited 200 to Bob's account.");
        
        boolean success = accounts[2].withdraw(1000);
        System.out.println("Charlie withdrew 1000: " + (success ? "Success" : "Failed"));
        
        System.out.println("\n--- Final Account Balances ---");
        for (Account acc : accounts) {
            System.out.println("Account: " + acc.getAccountNumber() + 
                               " | Owner: " + acc.getOwnerName() + 
                               " | Balance: Rs." + acc.getBalance());
        }
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
