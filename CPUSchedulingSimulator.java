import java.util.*;

public class CPUSchedulingSimulator {
    private Scanner scanner;
    
    public CPUSchedulingSimulator() {
        scanner = new Scanner(System.in);
    }
    
    public void run() {
        System.out.println("\n===== CPU Scheduling Simulator =====");
        
        int n;
        while (true) {
            System.out.print("Enter the number of processes: ");
            String input = scanner.nextLine();
            try {
                n = Integer.parseInt(input);
                if (n <= 0) {
                    System.out.println("Please enter a number greater than zero.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        
        Process[] processes = new Process[n];
        
        for (int i = 0; i < n; i++) {
            System.out.println("\nProcess " + (i + 1) + ":");
            System.out.print("Enter arrival time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Enter burst time: ");
            int burstTime = scanner.nextInt();
            
            processes[i] = new Process(i + 1, arrivalTime, burstTime);
        }
        
        System.out.print("Enter time quantum for Round Robin: ");
        int quantum = scanner.nextInt();
        
        System.out.println("\n===== COMPARING ALL CPU SCHEDULING ALGORITHMS =====");
        
        Process[] processesCopy1 = deepCopyProcesses(processes);
        Process[] processesCopy2 = deepCopyProcesses(processes);
        Process[] processesCopy3 = deepCopyProcesses(processes);
        Process[] processesCopy4 = deepCopyProcesses(processes);
        
        fcfs(processesCopy1);
        sjfNonPreemptive(processesCopy2);
        sjfPreemptive(processesCopy3);
        roundRobin(processesCopy4, quantum);
        
        System.out.println("\n===== CPU Scheduling Algorithms Comparison =====");
        System.out.println("+------------------------+-----------------+---------------------+");
        System.out.println("| Algorithm              | Avg Waiting Time | Avg Turnaround Time |");
        System.out.println("+------------------------+-----------------+---------------------+");
        
        double fcfsAvgWait = calculateAvgWaitingTime(processesCopy1);
        double fcfsAvgTurnaround = calculateAvgTurnaroundTime(processesCopy1);
        System.out.printf("| FCFS                   | %-15.2f | %-19.2f |\n", fcfsAvgWait, fcfsAvgTurnaround);
        
        double sjfNonPreemptiveAvgWait = calculateAvgWaitingTime(processesCopy2);
        double sjfNonPreemptiveAvgTurnaround = calculateAvgTurnaroundTime(processesCopy2);
        System.out.printf("| SJF (Non-preemptive)   | %-15.2f | %-19.2f |\n", sjfNonPreemptiveAvgWait, sjfNonPreemptiveAvgTurnaround);
        
        double sjfPreemptiveAvgWait = calculateAvgWaitingTime(processesCopy3);
        double sjfPreemptiveAvgTurnaround = calculateAvgTurnaroundTime(processesCopy3);
        System.out.printf("| SJF (Preemptive)       | %-15.2f | %-19.2f |\n", sjfPreemptiveAvgWait, sjfPreemptiveAvgTurnaround);
        
        double rrAvgWait = calculateAvgWaitingTime(processesCopy4);
        double rrAvgTurnaround = calculateAvgTurnaroundTime(processesCopy4);
        System.out.printf("| Round Robin (q=%-2d)     | %-15.2f | %-19.2f |\n", quantum, rrAvgWait, rrAvgTurnaround);
        
        System.out.println("+------------------------+-----------------+---------------------+");
        
        String bestAlgorithm = "FCFS";
        double minAvgWait = fcfsAvgWait;
        
        if (sjfNonPreemptiveAvgWait < minAvgWait) {
            minAvgWait = sjfNonPreemptiveAvgWait;
            bestAlgorithm = "SJF (Non-preemptive)";
        }
        if (sjfPreemptiveAvgWait < minAvgWait) {
            minAvgWait = sjfPreemptiveAvgWait;
            bestAlgorithm = "SJF (Preemptive)";
        }
        if (rrAvgWait < minAvgWait) {
            minAvgWait = rrAvgWait;
            bestAlgorithm = "Round Robin";
        }
        
        System.out.println("\nBest algorithm based on Average Waiting Time: " + bestAlgorithm);
    }
    
    private Process[] deepCopyProcesses(Process[] original) {
        Process[] copy = new Process[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new Process(original[i].getId(), original[i].getArrivalTime(), original[i].getBurstTime());
        }
        return copy;
    }
    
    private double calculateAvgWaitingTime(Process[] processes) {
        double sum = 0;
        for (Process process : processes) {
            sum += process.getWaitingTime();
        }
        return sum / processes.length;
    }
    
    private double calculateAvgTurnaroundTime(Process[] processes) {
        double sum = 0;
        for (Process process : processes) {
            sum += process.getTurnaroundTime();
        }
        return sum / processes.length;
    }
    
    private void fcfs(Process[] processes) {
        System.out.println("\n===== First Come First Serve (FCFS) =====");
        Arrays.sort(processes, Comparator.comparingInt(Process::getArrivalTime));
        int currentTime = 0;
        List<GanttChartEntry> ganttChart = new ArrayList<>();
        
        for (Process process : processes) {
            if (currentTime < process.getArrivalTime()) {
                currentTime = process.getArrivalTime();
            }
            process.setWaitingTime(currentTime - process.getArrivalTime());
            process.setTurnaroundTime(process.getWaitingTime() + process.getBurstTime());
            ganttChart.add(new GanttChartEntry(process.getId(), currentTime, currentTime + process.getBurstTime()));
            currentTime += process.getBurstTime();
        }
        displayResults(processes, ganttChart);
    }
    
    private void sjfNonPreemptive(Process[] processes) {
        System.out.println("\n===== Shortest Job First (Non-preemptive) =====");
        Process[] processQueue = Arrays.copyOf(processes, processes.length);
        Arrays.sort(processQueue, Comparator.comparingInt(Process::getArrivalTime));
        int currentTime = 0;
        List<GanttChartEntry> ganttChart = new ArrayList<>();
        List<Process> readyQueue = new ArrayList<>();
        int completed = 0;
        
        while (completed < processQueue.length) {
            for (Process process : processQueue) {
                if (process.getArrivalTime() <= currentTime && !process.isCompleted() && !readyQueue.contains(process)) {
                    readyQueue.add(process);
                }
            }
            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }
            Process shortestJob = Collections.min(readyQueue, Comparator.comparingInt(Process::getBurstTime));
            readyQueue.remove(shortestJob);
            shortestJob.setWaitingTime(currentTime - shortestJob.getArrivalTime());
            shortestJob.setTurnaroundTime(shortestJob.getWaitingTime() + shortestJob.getBurstTime());
            ganttChart.add(new GanttChartEntry(shortestJob.getId(), currentTime, currentTime + shortestJob.getBurstTime()));
            currentTime += shortestJob.getBurstTime();
            shortestJob.setCompleted(true);
            completed++;
        }
        for (Process process : processes) {
            process.setCompleted(false);
        }
        displayResults(processes, ganttChart);
    }
    
    private void sjfPreemptive(Process[] processes) {
        System.out.println("\n===== Shortest Job First (Preemptive) =====");
        Process[] processQueue = new Process[processes.length];
        for (int i = 0; i < processes.length; i++) {
            processQueue[i] = new Process(processes[i].getId(), processes[i].getArrivalTime(), processes[i].getBurstTime());
        }
        Arrays.sort(processQueue, Comparator.comparingInt(Process::getArrivalTime));
        int currentTime = 0, completed = 0;
        int[] completionTime = new int[processQueue.length];
        int[] waitingTime = new int[processQueue.length];
        int[] turnaroundTime = new int[processQueue.length];
        int[] startTime = new int[processQueue.length];
        boolean[] started = new boolean[processQueue.length];
        List<GanttChartEntry> ganttChart = new ArrayList<>();
        int prevProcess = -1;
        
        while (completed < processQueue.length) {
            int shortestJobIndex = -1;
            int shortestBurst = Integer.MAX_VALUE;
            for (int i = 0; i < processQueue.length; i++) {
                if (processQueue[i].getArrivalTime() <= currentTime && 
                    processQueue[i].getRemainingBurstTime() > 0 && 
                    processQueue[i].getRemainingBurstTime() < shortestBurst) {
                    shortestJobIndex = i;
                    shortestBurst = processQueue[i].getRemainingBurstTime();
                }
            }
            if (shortestJobIndex == -1) {
                currentTime++;
                continue;
            }
            if (!started[shortestJobIndex]) {
                startTime[shortestJobIndex] = currentTime;
                started[shortestJobIndex] = true;
            }
            if (prevProcess != shortestJobIndex) {
                ganttChart.add(new GanttChartEntry(processQueue[shortestJobIndex].getId(), currentTime, currentTime + 1));
                prevProcess = shortestJobIndex;
            } else if (!ganttChart.isEmpty()) {
                GanttChartEntry lastEntry = ganttChart.get(ganttChart.size() - 1);
                lastEntry.setEndTime(lastEntry.getEndTime() + 1);
            }
            processQueue[shortestJobIndex].decrementRemainingBurstTime();
            currentTime++;
            if (processQueue[shortestJobIndex].getRemainingBurstTime() == 0) {
                completed++;
                int index = processQueue[shortestJobIndex].getId() - 1;
                completionTime[index] = currentTime;
                turnaroundTime[index] = completionTime[index] - processQueue[shortestJobIndex].getArrivalTime();
                waitingTime[index] = turnaroundTime[index] - processQueue[shortestJobIndex].getBurstTime();
            }
        }
        for (int i = 0; i < processes.length; i++) {
            for (int j = 0; j < processQueue.length; j++) {
                if (processes[i].getId() == processQueue[j].getId()) {
                    processes[i].setWaitingTime(waitingTime[j]);
                    processes[i].setTurnaroundTime(turnaroundTime[j]);
                    break;
                }
            }
        }
        displayResults(processes, ganttChart);
    }
    
    private void roundRobin(Process[] processes, int quantum) {
        System.out.println("\n===== Round Robin (RR) with Quantum = " + quantum + " =====");
        Process[] processQueue = new Process[processes.length];
        for (int i = 0; i < processes.length; i++) {
            processQueue[i] = new Process(processes[i].getId(), processes[i].getArrivalTime(), processes[i].getBurstTime());
        }
        Arrays.sort(processQueue, Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0, completed = 0;
        int[] completionTime = new int[processQueue.length];
        int[] waitingTime = new int[processQueue.length];
        int[] turnaroundTime = new int[processQueue.length];
        List<GanttChartEntry> ganttChart = new ArrayList<>();
        
        readyQueue.add(processQueue[0]);
        int currentIndex = 1;
        
        while (completed < processQueue.length) {
            if (readyQueue.isEmpty()) {
                if (currentIndex < processQueue.length) {
                    currentTime = Math.max(currentTime, processQueue[currentIndex].getArrivalTime());
                    readyQueue.add(processQueue[currentIndex++]);
                } else {
                    break;
                }
            }
            Process currentProcess = readyQueue.poll();
            int executeTime = Math.min(quantum, currentProcess.getRemainingBurstTime());
            ganttChart.add(new GanttChartEntry(currentProcess.getId(), currentTime, currentTime + executeTime));
            currentTime += executeTime;
            currentProcess.decrementRemainingBurstTime(executeTime);
            
            while (currentIndex < processQueue.length && processQueue[currentIndex].getArrivalTime() <= currentTime) {
                readyQueue.add(processQueue[currentIndex++]);
            }
            if (currentProcess.getRemainingBurstTime() > 0) {
                readyQueue.add(currentProcess);
            } else {
                completed++;
                int index = currentProcess.getId() - 1;
                completionTime[index] = currentTime;
                turnaroundTime[index] = completionTime[index] - currentProcess.getArrivalTime();
                waitingTime[index] = turnaroundTime[index] - currentProcess.getBurstTime();
            }
        }
        for (int i = 0; i < processes.length; i++) {
            processes[i].setWaitingTime(waitingTime[i]);
            processes[i].setTurnaroundTime(turnaroundTime[i]);
        }
        displayResults(processes, ganttChart);
    }
    
    private void displayResults(Process[] processes, List<GanttChartEntry> ganttChart) {
        System.out.println("\nGantt Chart:");
        System.out.print("|");
        for (GanttChartEntry entry : ganttChart) {
            System.out.print(" P" + entry.getProcessId() + " |");
        }
        System.out.println();
        System.out.print("0");
        for (GanttChartEntry entry : ganttChart) {
            System.out.print("    " + entry.getEndTime());
        }
        System.out.println();
        
        System.out.println("\nProcess Details:");
        System.out.println("+-------+-------------+------------+---------------+-------------------+");
        System.out.println("| PID   | Arrival Time | Burst Time | Waiting Time | Turnaround Time  |");
        System.out.println("+-------+-------------+------------+---------------+-------------------+");
        
        double totalWaitingTime = 0, totalTurnaroundTime = 0;
        for (Process process : processes) {
            System.out.printf("| P%-4d | %-11d | %-10d | %-13d | %-17d |\n",
                    process.getId(), process.getArrivalTime(), process.getBurstTime(),
                    process.getWaitingTime(), process.getTurnaroundTime());
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }
        System.out.println("+-------+-------------+------------+---------------+-------------------+");
        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWaitingTime / processes.length);
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaroundTime / processes.length);
    }
}

class Process {
    private int id;
    private int arrivalTime;
    private int burstTime;
    private int remainingBurstTime;
    private int waitingTime;
    private int turnaroundTime;
    private boolean completed;
    
    public Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.completed = false;
    }
    
    public int getId() { return id; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingBurstTime() { return remainingBurstTime; }
    public void decrementRemainingBurstTime() { remainingBurstTime--; }
    public void decrementRemainingBurstTime(int time) { remainingBurstTime -= time; }
    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

class GanttChartEntry {
    private int processId;
    private int startTime;
    private int endTime;
    
    public GanttChartEntry(int processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public int getProcessId() { return processId; }
    public int getStartTime() { return startTime; }
    public int getEndTime() { return endTime; }
    public void setEndTime(int endTime) { this.endTime = endTime; }
}
