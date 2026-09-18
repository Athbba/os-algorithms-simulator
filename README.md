# Operating Systems Algorithms Simulator

A modular Java simulation tool that models core operating system algorithms for process scheduling, deadlock avoidance, and virtual memory management.

## Features

### 1. CPU Scheduling
- **First-Come, First-Served (FCFS)**
- **Shortest Job First (SJF)** (Preemptive & Non-preemptive)
- **Round Robin (RR)** with configurable time quantum
- Computes Gantt charts, per-process turnaround & waiting times, and compares averages across all algorithms.

### 2. Deadlock Avoidance (Banker's Algorithm)
- Evaluates Allocation, Max, Available, and Need matrices.
- Computes safe execution sequences ($P_i \rightarrow P_j$).
- Simulates real-time resource requests and safe state approvals.

### 3. Page Replacement Simulation
- **First-In-First-Out (FIFO)**
- **Least Recently Used (LRU)**
- **Optimal Replacement**
- Evaluates frame-by-frame memory states, total page faults, hit ratios, and miss ratios.

## How to Run

Compile and run via command line:
```bash
javac *.java
java Main
