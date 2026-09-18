import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n===== OS Algorithms Simulator =====");
            System.out.println("1. CPU Scheduling Algorithms");
            System.out.println("2. Banker's Algorithm");
            System.out.println("3. Page Replacement Algorithms");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();  
            int choice;
            try {
                choice = Integer.parseInt(input); 
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number from 1 to 4.");
                continue;
            }
            switch (choice) {
                case 1:
                    CPUSchedulingSimulator cpuScheduler = new CPUSchedulingSimulator();
                    cpuScheduler.run();
                    break;
                case 2:
                    BankersAlgorithm bankersAlgorithm = new BankersAlgorithm();
                    bankersAlgorithm.run();
                    break;
                case 3:
                    PageReplacementSimulator pageReplacementSimulator = new PageReplacementSimulator();
                    pageReplacementSimulator.run();
                    break;
                case 4:
                    exit = true;
                    System.out.println("Exiting program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a number from 1 to 4.");
            }
        }
        scanner.close();
    }
}
