import java.util.Scanner;

public class BankersAlgorithm {
    private Scanner scanner;
    private int n;
    private int m;
    private int[] available;
    private int[][] max;
    private int[][] allocation;
    private int[][] need;
    
    public BankersAlgorithm() {
        scanner = new Scanner(System.in);
    }
    
    public void run() {
        System.out.println("\n===== Banker's Algorithm Simulator =====");
        while (true) {
            System.out.print("Enter the number of processes: ");
            String input = scanner.nextLine();
            try {
                n = Integer.parseInt(input);
                if (n <= 0) System.out.println("Must be greater than 0.");
                else break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a valid number.");
            }
        }
        while (true) {
            System.out.print("Enter the number of resources: ");
            String input = scanner.nextLine();
            try {
                m = Integer.parseInt(input);
                if (m <= 0) System.out.println("Must be greater than 0.");
                else break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a valid number.");
            }
        }
        
        available = new int[m];
        max = new int[n][m];
        allocation = new int[n][m];
        need = new int[n][m];
        
        System.out.println("\nEnter the number of instances for each resource:");
        int[] total = new int[m];
        for (int i = 0; i < m; i++) {
            System.out.print("Resource " + i + ": ");
            total[i] = scanner.nextInt();
        }
        
        System.out.println("\nEnter the Allocation Matrix (row by row):");
        for (int i = 0; i < n; i++) {
            System.out.println("Process " + i + ":");
            for (int j = 0; j < m; j++) {
                System.out.print("Resource " + j + ": ");
                allocation[i][j] = scanner.nextInt();
            }
        }
        
        System.out.println("\nEnter the Max Matrix (row by row):");
        for (int i = 0; i < n; i++) {
            System.out.println("Process " + i + ":");
            for (int j = 0; j < m; j++) {
                System.out.print("Resource " + j + ": ");
                max[i][j] = scanner.nextInt();
            }
        }
        
        for (int j = 0; j < m; j++) {
            int allocatedSum = 0;
            for (int i = 0; i < n; i++) {
                allocatedSum += allocation[i][j];
            }
            available[j] = total[j] - allocatedSum;
        }
        
        calculateNeedMatrix();
        System.out.println("\n===== BANKER'S ALGORITHM COMPLETE ANALYSIS =====");
        displayNeedMatrix();
        displayAvailableResources();
        
        boolean isSafe = isSafeState();
        if (isSafe) {
            System.out.println("\nThe system is in a safe state.");
            System.out.println("Safe sequence: " + getSafeSequence());
        } else {
            System.out.println("\nThe system is NOT in a safe state.");
        }
        
        System.out.println("\n===== Resource Request Simulation =====");
        System.out.print("Enter the process ID (0 to " + (n-1) + ") that is requesting resources: ");
        int processId = scanner.nextInt();
        
        int[] request = new int[m];
        System.out.println("Enter the resource request for Process " + processId + ":");
        for (int i = 0; i < m; i++) {
            System.out.print("Resource " + i + ": ");
            request[i] = scanner.nextInt();
        }
        
        boolean canBeGranted = checkResourceRequest(processId, request);
        if (canBeGranted) {
            int[] tempAvailable = available.clone();
            int[][] tempAllocation = new int[n][m];
            int[][] tempNeed = new int[n][m];
            
            for (int i = 0; i < n; i++) {
                tempAllocation[i] = allocation[i].clone();
                tempNeed[i] = need[i].clone();
            }
            
            for (int j = 0; j < m; j++) {
                tempAvailable[j] -= request[j];
                tempAllocation[processId][j] += request[j];
                tempNeed[processId][j] -= request[j];
            }
            
            boolean safeAfterRequest = isSafeStateWithTemp(tempAvailable, tempAllocation, tempNeed);
            if (safeAfterRequest) {
                System.out.println("Safe sequence after allocation: " + getSafeSequenceWithTemp(tempAvailable, tempAllocation, tempNeed));
                for (int j = 0; j < m; j++) {
                    available[j] -= request[j];
                    allocation[processId][j] += request[j];
                    need[processId][j] -= request[j];
                }
            } else {
                System.out.println("Request cannot be granted immediately as it would lead to an unsafe state.");
            }
        } else {
            System.out.println("Request cannot be granted. Process " + processId + " is requesting more resources than its maximum claim or more than available resources.");
        }
    }
    
    private void displayAvailableResources() {
        System.out.println("\nAvailable Resources:");
        for (int j = 0; j < m; j++) System.out.print(available[j] + " ");
        System.out.println();
    }
    
    private void calculateNeedMatrix() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
    }
    
    private void displayNeedMatrix() {
        System.out.println("\nNeed Matrix:");
        System.out.print("   ");
        for (int j = 0; j < m; j++) System.out.printf("R%-3d", j);
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.printf("P%-2d", i);
            for (int j = 0; j < m; j++) System.out.printf("%-4d", need[i][j]);
            System.out.println();
        }
    }
    
    private boolean isSafeState() {
        int[] work = available.clone();
        boolean[] finish = new boolean[n];
        int count = 0;
        while (count < n) {
            boolean found = false;
            for (int i = 0; i < n; i++) {
                if (!finish[i]) {
                    int j;
                    for (j = 0; j < m; j++) {
                        if (need[i][j] > work[j]) break;
                    }
                    if (j == m) {
                        for (int k = 0; k < m; k++) work[k] += allocation[i][k];
                        finish[i] = true;
                        found = true;
                        count++;
                    }
                }
            }
            if (!found) break;
        }
        return count == n;
    }
    
    private String getSafeSequence() {
        int[] work = available.clone();
        boolean[] finish = new boolean[n];
        int[] safeSeq = new int[n];
        int count = 0;
        while (count < n) {
            boolean found = false;
            for (int i = 0; i < n; i++) {
                if (!finish[i]) {
                    int j;
                    for (j = 0; j < m; j++) {
                        if (need[i][j] > work[j]) break;
                    }
                    if (j == m) {
                        for (int k = 0; k < m; k++) work[k] += allocation[i][k];
                        safeSeq[count++] = i;
                        finish[i] = true;
                        found = true;
                    }
                }
            }
            if (!found) break;
        }
        StringBuilder sequence = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sequence.append("P").append(safeSeq[i]);
            if (i < n - 1) sequence.append(" → ");
        }
        return sequence.toString();
    }
    
    private boolean checkResourceRequest(int processId, int[] request) {
        for (int j = 0; j < m; j++) {
            if (request[j] > need[processId][j] || request[j] > available[j]) return false;
        }
        return true;
    }
    
    private boolean isSafeStateWithTemp(int[] avail, int[][] alloc, int[][] nd) {
        int[] work = avail.clone();
        boolean[] finish = new boolean[n];
        int count = 0;
        while (count < n) {
            boolean found = false;
            for (int i = 0; i < n; i++) {
                if (!finish[i]) {
                    int j;
                    for (j = 0; j < m; j++) {
                        if (nd[i][j] > work[j]) break;
                    }
                    if (j == m) {
                        for (int k = 0; k < m; k++) work[k] += alloc[i][k];
                        finish[i] = true;
                        found = true;
                        count++;
                    }
                }
            }
            if (!found) break;
        }
        return count == n;
    }
    
    private String getSafeSequenceWithTemp(int[] avail, int[][] alloc, int[][] nd) {
        int[] work = avail.clone();
        boolean[] finish = new boolean[n];
        int[] safeSeq = new int[n];
        int count = 0;
        while (count < n) {
            boolean found = false;
            for (int i = 0; i < n; i++) {
                if (!finish[i]) {
                    int j;
                    for (j = 0; j < m; j++) {
                        if (nd[i][j] > work[j]) break;
                    }
                    if (j == m) {
                        for (int k = 0; k < m; k++) work[k] += alloc[i][k];
                        safeSeq[count++] = i;
                        finish[i] = true;
                        found = true;
                    }
                }
            }
            if (!found) break;
        }
        StringBuilder sequence = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sequence.append("P").append(safeSeq[i]);
            if (i < n - 1) sequence.append(" → ");
        }
        return sequence.toString();
    }
}
