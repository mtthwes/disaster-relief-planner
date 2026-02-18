# Disaster Relief Resource Allocation System

A Java application that solves combinatorial optimization problems for disaster relief logistics: determining optimal supply site locations, analyzing network redundancy, and computing efficient delivery routes using graph algorithms.

**Course Project:** CIS 2168 - Data Structures | Temple University | Spring 2025

---

## Overview

This system tackles three critical disaster logistics problems:

1. **Set Cover Problem:** Find the minimum number of supply sites needed to serve all cities (cities can receive supplies from themselves or adjacent neighbors)
2. **Network Redundancy Analysis:** Identify critical roads whose failure would disconnect cities from supply sites
3. **Traveling Salesman Problem (TSP):** Compute the optimal delivery route visiting all supply sites and returning to origin

These are real-world combinatorial optimization problems relevant to emergency response, pharmaceutical distribution, and infrastructure planning.

---

## Key Features

- **Backtracking Set Cover Algorithm** for optimal supply site placement
- **Floyd-Warshall All-Pairs Shortest Path** (O(n³)) for distance computation
- **TSP Solver** using permutation-based exhaustive search with pruning
- **Network Robustness Testing** via edge-removal simulation
- **Graph representation** using adjacency lists with weighted edges
- **File-based input** parsing custom graph format

---

## Technologies

- **Language:** Java
- **Algorithms:** 
  - Backtracking (Set Cover)
  - Floyd-Warshall (All-Pairs Shortest Path)
  - Permutation-based TSP
  - BFS (Reachability Analysis)
- **Data Structures:** HashMap, LinkedHashMap, HashSet, Graph (custom)
- **Complexity:**
  - Set Cover: O(2^n) worst case, optimized with greedy heuristic
  - Floyd-Warshall: O(n³)
  - TSP: O(n! × m) where n = supply sites, m = cities

---

## Project Structure

```
disaster-relief-planner/
├── DisasterPlanner.java       # Main application logic
├── Graph.java                 # Graph data structure with algorithms
├── sample_data/
│   └── road.csv              # Test city network (Western US cities)
└── README.md
```

---

## How to Run

### Compile
```bash
javac DisasterPlanner.java Graph.java
```

### Run
```bash
java DisasterPlanner
```

### Sample Interaction
```
Enter city data filename: sample_data/road.csv
Enter maximum number of supply sites: 3
Supply sites for k=3: [Port, LV, Phoe]
Minimum sites needed: k=3 -> [Port, LV, Phoe]
Network is redundant under any single road failure.
Enter starting city for delivery: Sea
Best tour: [Sea, Port, LV, Phoe, Sea]
```

---

## Input Format

Graph data is stored in a custom adjacency list format:

```
CityName: Neighbor1 (distance), Neighbor2 (distance), ...
```

**Example (road.csv):**
```
Sea: Port (8), But (10)
Port: Sea (8), But (5), SLC (3), Sac (7)
SLC: But (12), Port (3), Sac (1), Mon (8)
```

Cities are represented by abbreviations:
- Sea = Seattle
- Port = Portland  
- SLC = Salt Lake City
- Sac = Sacramento
- LA = Los Angeles
- SD = San Diego
- Phoe = Phoenix
- LV = Las Vegas
- etc.

---

## Algorithm Design

### 1. Set Cover (Supply Site Allocation)

**Problem:** Find minimum k cities to place supply sites such that every city is either a supply site or adjacent to one.

**Approach:** Backtracking with greedy heuristic
- Try cities that cover the most uncovered neighbors first
- Prune branches that exceed k sites
- Track covered cities to avoid redundant work

**Why This Matters:** Pharmaceutical companies like GSK must optimize warehouse placement to minimize infrastructure costs while ensuring every market has access to supplies.

---

### 2. Floyd-Warshall All-Pairs Shortest Path

**Problem:** Compute shortest distance between every pair of cities.

**Approach:** Dynamic programming (O(n³))
```java
for each intermediate vertex k:
    for each source i:
        for each destination j:
            if dist[i][k] + dist[k][j] < dist[i][j]:
                dist[i][j] = dist[i][k] + dist[k][j]
```

**Usage:** Required for TSP solver to compute route distances.

---

### 3. Network Redundancy Check

**Problem:** Identify single points of failure (roads whose removal disconnects cities).

**Approach:** 
- For each edge, temporarily remove it
- Run BFS from supply sites to check if all cities remain reachable
- If not, mark edge as critical

**Why This Matters:** Supply chain resilience — identify roads that need backup routes to prevent delivery disruptions.

---

### 4. Traveling Salesman Problem (TSP)

**Problem:** Find shortest tour visiting all supply sites exactly once and returning to start.

**Approach:** Permutation-based exhaustive search with pruning
- Generate all permutations of supply sites
- Compute tour distance using Floyd-Warshall distances
- Track best (shortest) tour found

**Complexity:** O(n!) where n = number of supply sites (feasible for small n ≤ 10)

---

## Sample Output

```
Enter city data filename: sample_data/road.csv
Enter maximum number of supply sites: 4

Supply sites for k=4: [Sea, Sac, Bar, Nog]
Minimum sites needed: k=3 -> [Port, LV, Phoe]

Network is redundant under any single road failure.

Enter starting city for delivery: Sea
Best tour: [Sea, Port, LV, Phoe, Sea]
Total distance: 47
```

---

## Key Learning Outcomes

- Implemented **graph algorithms** from scratch (Floyd-Warshall, BFS, backtracking)
- Solved **NP-hard combinatorial optimization problems** (Set Cover, TSP)
- Applied **algorithm design techniques**: dynamic programming, backtracking, greedy heuristics
- Modeled **real-world logistics problems** with practical applications to disaster response and supply chain management
- Practiced **clean separation of concerns** (Graph class vs. application logic)

---

## Real-World Applications

### Pharmaceutical R&D Supply Chain (Relevant to GSK)
- **Warehouse Placement:** Optimize lab supply distribution centers
- **Clinical Trial Logistics:** Minimize delivery routes for temperature-sensitive biologics
- **Disaster Preparedness:** Ensure vaccine distribution networks remain operational under infrastructure failures

### General Applications
- Emergency response planning
- Telecommunications network design
- Power grid resilience analysis

---

## Related Projects

- [Doctor-Patient Scheduler](https://github.com/mtthwes/doctor-patient-scheduler) - Healthcare resource allocation using backtracking
- [Mini-NARS AGI System](https://github.com/mtthwes/mini-nars-agi) - Artificial General Intelligence reasoner

---

## License

This is a course project developed for educational purposes at Temple University.

---

## Author

**Matthew Setiadi**  
B.S./M.S. Computational Data Science | Temple University  
[LinkedIn](https://linkedin.com/in/matthewsetiadi) | matthew.setiadi@temple.edu
