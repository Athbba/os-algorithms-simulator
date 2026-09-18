import java.util.*;

public class PageReplacementSimulator {
    private Scanner scanner;
    
    public PageReplacementSimulator() {
        scanner = new Scanner(System.in);
    }
    
    public void run() {
        System.out.println("\n===== Page Replacement Simulator =====");
        int frameSize;
        while (true) {
            System.out.print("Enter the number of frames: ");
            String input = scanner.nextLine();
            try {
                frameSize = Integer.parseInt(input);
                if (frameSize <= 0) System.out.println("Must be greater than 0.");
                else break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
            }
        }
        
        int[] referenceString;
        while (true) {
            System.out.print("Enter the reference string (space-separated integers): ");
            String input = scanner.nextLine();
            String[] parts = input.trim().split("\\s+");
            referenceString = new int[parts.length];
            boolean valid = true;
            for (int i = 0; i < parts.length; i++) {
                try {
                    referenceString[i] = Integer.parseInt(parts[i]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid entry: '" + parts[i] + "'. Please enter only numbers.");
                    valid = false;
                    break;
                }
            }
            if (valid) break;
        }
        
        System.out.println("\n===== COMPARING ALL PAGE REPLACEMENT ALGORITHMS =====");
        System.out.println("Reference String: " + Arrays.toString(referenceString));
        System.out.println("Number of Frames: " + frameSize);
        
        int fifoFaults = simulateFIFO(referenceString, frameSize);
        int optimalFaults = simulateOptimal(referenceString, frameSize);
        int lruFaults = simulateLRU(referenceString, frameSize);
        
        System.out.println("\n===== Comparative Results =====");
        System.out.println("+----------------+--------------+-------------+-------------+");
        System.out.println("| Algorithm      | Page Faults  | Hit Ratio   | Miss Ratio  |");
        System.out.println("+----------------+--------------+-------------+-------------+");
        
        double fifoHitRatio = (double) (referenceString.length - fifoFaults) / referenceString.length;
        double fifoMissRatio = (double) fifoFaults / referenceString.length;
        System.out.printf("| FIFO           | %-12d | %-11.2f | %-11.2f |\n", fifoFaults, fifoHitRatio, fifoMissRatio);
        
        double optimalHitRatio = (double) (referenceString.length - optimalFaults) / referenceString.length;
        double optimalMissRatio = (double) optimalFaults / referenceString.length;
        System.out.printf("| Optimal        | %-12d | %-11.2f | %-11.2f |\n", optimalFaults, optimalHitRatio, optimalMissRatio);
        
        double lruHitRatio = (double) (referenceString.length - lruFaults) / referenceString.length;
        double lruMissRatio = (double) lruFaults / referenceString.length;
        System.out.printf("| LRU            | %-12d | %-11.2f | %-11.2f |\n", lruFaults, lruHitRatio, lruMissRatio);
        System.out.println("+----------------+--------------+-------------+-------------+");
    }
    
    private int simulateFIFO(int[] referenceString, int frameSize) {
        System.out.println("\n===== First-In-First-Out (FIFO) =====");
        Queue<Integer> frames = new LinkedList<>();
        Set<Integer> frameSet = new HashSet<>();
        int pageFaults = 0;
        
        for (int page : referenceString) {
            if (!frameSet.contains(page)) {
                pageFaults++;
                if (frames.size() == frameSize) {
                    int removed = frames.poll();
                    frameSet.remove(removed);
                }
                frames.add(page);
                frameSet.add(page);
                System.out.println("Page " + page + " -> " + frames + " (Page Fault)");
            } else {
                System.out.println("Page " + page + " -> " + frames + " (Hit)");
            }
        }
        return pageFaults;
    }
    
    private int simulateOptimal(int[] referenceString, int frameSize) {
        System.out.println("\n===== Optimal Page Replacement =====");
        List<Integer> frames = new ArrayList<>();
        int pageFaults = 0;
        
        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            if (!frames.contains(page)) {
                pageFaults++;
                if (frames.size() < frameSize) {
                    frames.add(page);
                } else {
                    int farthestIndex = -1;
                    int replaceIndex = -1;
                    for (int j = 0; j < frames.size(); j++) {
                        int currentPage = frames.get(j);
                        int k;
                        for (k = i + 1; k < referenceString.length; k++) {
                            if (referenceString[k] == currentPage) break;
                        }
                        if (k == referenceString.length) {
                            replaceIndex = j;
                            break;
                        }
                        if (k > farthestIndex) {
                            farthestIndex = k;
                            replaceIndex = j;
                        }
                    }
                    frames.set(replaceIndex, page);
                }
                System.out.println("Page " + page + " -> " + frames + " (Page Fault)");
            } else {
                System.out.println("Page " + page + " -> " + frames + " (Hit)");
            }
        }
        return pageFaults;
    }
    
    private int simulateLRU(int[] referenceString, int frameSize) {
        System.out.println("\n===== Least Recently Used (LRU) =====");
        List<Integer> frames = new ArrayList<>();
        Map<Integer, Integer> lastUsed = new HashMap<>();
        int pageFaults = 0;
        
        for (int i = 0; i < referenceString.length; i++) {
            int page = referenceString[i];
            if (!frames.contains(page)) {
                pageFaults++;
                if (frames.size() < frameSize) {
                    frames.add(page);
                } else {
                    int lruIndex = 0;
                    int lruValue = Integer.MAX_VALUE;
                    for (int j = 0; j < frames.size(); j++) {
                        int currentPage = frames.get(j);
                        if (lastUsed.get(currentPage) < lruValue) {
                            lruValue = lastUsed.get(currentPage);
                            lruIndex = j;
                        }
                    }
                    frames.set(lruIndex, page);
                }
                System.out.println("Page " + page + " -> " + frames + " (Page Fault)");
            } else {
                System.out.println("Page " + page + " -> " + frames + " (Hit)");
            }
            lastUsed.put(page, i);
        }
        return pageFaults;
    }
}
