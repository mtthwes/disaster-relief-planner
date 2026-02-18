import java.util.*;
import java.io.*;


public class DisasterPlanner {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter city data filename: ");
        String filename = reader.readLine().trim();

        Graph graph = new Graph();
        parseGraph(filename, graph);

        System.out.print("Enter maximum number of supply sites: ");
        int numCities = Integer.parseInt(reader.readLine().trim());

        Set<String> supplyLocations = new LinkedHashSet<>();
        Map<String, Set<String>> roadNetwork = new LinkedHashMap<>();
        for (String city : graph.getVertices()) {
            roadNetwork.put(city, new HashSet<>(graph.getAdjacency().get(city).keySet()));
        }
        boolean ok = allocateResources(roadNetwork, numCities, supplyLocations);

        if (ok) {
            System.out.println("Supply sites for k=" + numCities + ": " + supplyLocations);
        } else {
            System.out.println("Cannot cover all cities with k=" + numCities + " sites.");
        }

        Set<String> bestCover = null;
        for (int k = 1; k <= roadNetwork.size(); k++) {
            Set<String> trial = new LinkedHashSet<>();
            if (allocateResources(roadNetwork, k, trial)) {
                System.out.println("Minimum sites needed: k=" + k + " -> " + trial);
                bestCover = trial;
                break;
            }
        }
        if (bestCover == null) {
            System.out.println("No possible covering at all.");
            return;
        }
        supplyLocations = bestCover;

        List<String> edgesToAdd = checkRedundancy(graph, supplyLocations);
        if (edgesToAdd.isEmpty()) {
            System.out.println("Network is redundant under any single road failure.");
        } else {
            System.out.println("To ensure redundancy, consider adding roads: " + edgesToAdd);
        }

        System.out.print("Enter starting city for delivery: ");
        String rawStart = reader.readLine().trim();

        String start = null;
        for (String city : graph.getVertices()) {
            if (city.equalsIgnoreCase(rawStart)) {
                start = city;
                break;
            }
        }

        if (start == null) {
            System.err.println("Error: \"" + rawStart + "\" is not in the network.");
            return;
        }
        if (!graph.getVertices().contains(start)) {
            System.err.println("Error: “" + start + "” is not in the network.");
            return;
        }

        List<String> tour = deliverToAllCities(graph, start, new ArrayList<>(supplyLocations));
        System.out.println("Best tour: " + tour);
    }

    static void parseGraph(String filename, Graph graph) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(":");
                String city = parts[0].trim();
                graph.addVertex(city);
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    String[] neighbors = parts[1].split(",");
                    for (String nb : neighbors) {
                        nb = nb.trim();
                        if (nb.isEmpty()) continue;
                        String[] np = nb.split("\\s*\\(\\s*|\\s*\\)\\s*");
                        String toCity = np[0].trim();
                        int dist = Integer.parseInt(np[1].trim());
                        graph.addEdge(city, toCity, dist);
                    }
                }
            }
        }
    }

    static boolean allocateResources(Map<String, Set<String>> roadNetwork,
                                     int numCities,
                                     Set<String> supplyLocations) {
        Set<String> covered = new HashSet<>();
        return backtrackAllocate(roadNetwork, numCities, supplyLocations, covered, new LinkedHashSet<>());
    }

    private static boolean backtrackAllocate(Map<String, Set<String>> roadNetwork,
                                             int numCities,
                                             Set<String> supplyLocations,
                                             Set<String> covered,
                                             Set<String> triedCities) {
        if (covered.size() == roadNetwork.size()) return true;
        if (supplyLocations.size() >= numCities) return false;

        List<String> candidates = new ArrayList<>();
        for (String city : roadNetwork.keySet()) {
            if (!supplyLocations.contains(city) && !triedCities.contains(city)) {
                candidates.add(city);
            }
        }

        for (String city : candidates) {
            Set<String> newlyCovered = new HashSet<>();
            if (!covered.contains(city)) newlyCovered.add(city);
            for (String neighbor : roadNetwork.get(city)) {
                if (!covered.contains(neighbor)) newlyCovered.add(neighbor);
            }

            if (newlyCovered.isEmpty()) continue;

            supplyLocations.add(city);
            covered.addAll(newlyCovered);
            triedCities.add(city);

            if (backtrackAllocate(roadNetwork, numCities, supplyLocations, covered, triedCities)) {
                return true;
            }

            supplyLocations.remove(city);
            covered.removeAll(newlyCovered);
            triedCities.remove(city);
        }

        return false;
    }

    static List<String> checkRedundancy(Graph graph, Set<String> supplies) {
        List<String> toAdd = new ArrayList<>();
        List<String[]> edges = new ArrayList<>();
        for (String u : graph.getVertices()) {
            for (String v : graph.getAdjacency().get(u).keySet()) {
                if (u.compareTo(v) < 0) edges.add(new String[]{u, v});
            }
        }

        for (String[] uv : edges) {
            String u = uv[0], v = uv[1];
            int weight = graph.getAdjacency().get(u).get(v);
            graph.removeEdge(u, v);
            if (!graph.isAllReachableFromSupplies(supplies)) {
                toAdd.add(u + "-" + v);
            }
            graph.restoreEdge(u, v, weight);
        }
        return toAdd;
    }

    static List<String> deliverToAllCities(Graph graph,
                                           String start,
                                           List<String> targets) {
        if (targets.isEmpty()) {
            List<String> tour = new ArrayList<>();
            tour.add(start);
            tour.add(start);
            return tour;
        }

        // Ensure targets include only valid supply locations
        List<String> validTargets = new ArrayList<>();
        for (String city : targets) {
            if (graph.getVertices().contains(city)) {
                validTargets.add(city);
            }
        }

        Map<String, Map<String, Integer>> dist = graph.floydWarshall();
        List<String> bestTour = new ArrayList<>();
        int[] bestLength = {Integer.MAX_VALUE};
        boolean[] visited = new boolean[validTargets.size()];
        permute(start, start, validTargets, visited, new ArrayList<>(), 0, dist, bestLength, bestTour);
        return bestTour;
    }

    private static void permute(String start, String current, List<String> targets, boolean[] visited,
                                List<String> path, int currentDist, Map<String, Map<String, Integer>> dist,
                                int[] bestLength, List<String> bestTour) {
        if (path.size() == targets.size()) {
            int returnDist = dist.get(current).get(start);
            if (returnDist != Integer.MAX_VALUE / 2 && currentDist + returnDist < bestLength[0]) {
                bestLength[0] = currentDist + returnDist;
                bestTour.clear();
                bestTour.add(start);
                bestTour.addAll(path);
                bestTour.add(start);
            }
            return;
        }

        for (int i = 0; i < targets.size(); i++) {
            if (!visited[i]) {
                String nextCity = targets.get(i);
                int edgeDist = dist.get(current).get(nextCity);
                if (edgeDist == Integer.MAX_VALUE / 2) continue;

                visited[i] = true;
                path.add(nextCity);
                permute(start, nextCity, targets, visited, path, currentDist + edgeDist, dist, bestLength, bestTour);
                path.remove(path.size() - 1);
                visited[i] = false;
            }
        }
    }
}