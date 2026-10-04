import java.util.Scanner;

public class MiniBank {
    public static void main(String[] args) {
        BankInfo info = new BankInfo("MiniBank", "Main Branch");
        System.out.println("=== Welcome to " + info.name() + " (" + info.branch() + ") ===");
        
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
                scanner.next(); // consume invalid input
                continue;
            }
            
            int choice = scanner.nextInt();
            
            // Map the integer choice to a MenuOption
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
            
            // Switch expression to evaluate a message based on the option
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
