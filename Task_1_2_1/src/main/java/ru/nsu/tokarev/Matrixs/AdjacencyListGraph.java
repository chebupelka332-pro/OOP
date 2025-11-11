package ru.nsu.tokarev.Matrixs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.Objects;
import java.util.HashSet;

public class AdjacencyListGraph<T> implements Graph<T> {
    private final Map<T, List<T>> adjacencyList = new HashMap<>();

    @Override
    public void addVertex(T vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    @Override
    public void addEdge(T from, T to) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.putIfAbsent(to, new ArrayList<>());

        List<T> neighbors = adjacencyList.get(from);
        if (!neighbors.contains(to)) {
            neighbors.add(to);
        }
    }

    @Override
    public void removeVertex(T vertex) {
        adjacencyList.remove(vertex);
        for (List<T> neighbors : adjacencyList.values()) {
            neighbors.remove(vertex);
        }
    }

    @Override
    public void removeEdge(T from, T to) {
        List<T> neighbors = adjacencyList.get(from);
        if (neighbors != null) {
            neighbors.remove(to);
        }
    }

    @Override
    public List<T> getNeighbors(T vertex) {
        List<T> neighbors = adjacencyList.get(vertex);
        if (neighbors == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(neighbors);
    }

    @Override
    public Set<T> getAllVertices() {
        return new HashSet<>(adjacencyList.keySet());
    }

    @Override
    public boolean hasVertex(T vertex) {
        return adjacencyList.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(T from, T to) {
        List<T> neighbors = adjacencyList.get(from);
        return neighbors != null && neighbors.contains(to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adjacencyList);
    }

    @Override
    public String toString() {
        if (adjacencyList.isEmpty()) {
            return "Adjacency List: (empty graph)";
        }

        StringBuilder sb = new StringBuilder("Adjacency List:\n");
        for (Map.Entry<T, List<T>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(" -> ");
            List<T> neighbors = entry.getValue();
            if (neighbors.isEmpty()) {
                sb.append("[]");
            } else {
                sb.append(neighbors);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return graphEquals(obj);
    }
}