import java.util.*;

public class Graph {
    private Map<String, Map<String, Integer>> adj = new LinkedHashMap<>();

    public void addVertex(String v) {
        adj.putIfAbsent(v, new LinkedHashMap<>());
    }

    public void addEdge(String u, String v, int w) {
        addVertex(u);
        addVertex(v);
        adj.get(u).put(v, w);
        adj.get(v).put(u, w);
    }

    public void removeEdge(String u, String v) {
        if (adj.containsKey(u)) adj.get(u).remove(v);
        if (adj.containsKey(v)) adj.get(v).remove(u);
    }

    public void restoreEdge(String u, String v, int w) {
        adj.get(u).put(v, w);
        adj.get(v).put(u, w);
    }

    public boolean isAllReachableFromSupplies(Set<String> supplies) {
        Set<String> visited = new HashSet<>(supplies);
        Queue<String> q = new LinkedList<>(supplies);
        while (!q.isEmpty()) {
            String u = q.poll();
            for (String v : adj.getOrDefault(u, Collections.emptyMap()).keySet()) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    q.add(v);
                }
            }
        }
        return visited.containsAll(adj.keySet());
    }

    public Map<String, Map<String, Integer>> floydWarshall() {
        Map<String, Map<String, Integer>> dist = new LinkedHashMap<>();
        for (String u : adj.keySet()) {
            dist.put(u, new LinkedHashMap<>());
            for (String v : adj.keySet()) {
                if (u.equals(v)) dist.get(u).put(v, 0);
                else if (adj.get(u).containsKey(v)) dist.get(u).put(v, adj.get(u).get(v));
                else dist.get(u).put(v, Integer.MAX_VALUE / 2);
            }
        }
        for (String k : adj.keySet()) {
            for (String i : adj.keySet()) {
                for (String j : adj.keySet()) {
                    int dik = dist.get(i).get(k);
                    int dkj = dist.get(k).get(j);
                    int dij = dist.get(i).get(j);
                    if (dik + dkj < dij) {
                        dist.get(i).put(j, dik + dkj);
                    }
                }
            }
        }
        return dist;
    }

    public Set<String> getVertices() {
        return adj.keySet();
    }

    public Map<String, Map<String, Integer>> getAdjacency() {
        return adj;
    }
}