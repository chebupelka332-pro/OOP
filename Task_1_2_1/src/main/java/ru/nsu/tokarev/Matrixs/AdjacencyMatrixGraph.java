package ru.nsu.tokarev.Matrixs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.Objects;
import java.util.HashSet;
import java.util.Collections;

public class AdjacencyMatrixGraph<T> implements Graph<T> {
    private final List<T> vertices = new ArrayList<>();
    private List<List<Integer>> matrix = new ArrayList<>();
    private final Map<T, Integer> vertexIndices = new HashMap<>();

    @Override
    public void addVertex(T vertex) {
        if (vertexIndices.containsKey(vertex)) {
            return;
        }

        int newIndex = vertices.size();
        vertices.add(vertex);
        vertexIndices.put(vertex, newIndex);

        matrix.add(new ArrayList<>());

        int newSize = vertices.size();
        for (int i = 0; i < matrix.size(); i++) {
            while (matrix.get(i).size() < newSize) {
                matrix.get(i).add(0);
            }
        }
    }

    @Override
    public void addEdge(T from, T to) {
        if (!vertexIndices.containsKey(from)) addVertex(from);
        if (!vertexIndices.containsKey(to)) addVertex(to);

        int fromIndex = vertexIndices.get(from);
        int toIndex = vertexIndices.get(to);
        matrix.get(fromIndex).set(toIndex, 1);
    }

    @Override
    public List<T> getNeighbors(T vertex) {
        if (!vertexIndices.containsKey(vertex)) {
            return Collections.emptyList();
        }
        int vertexIndex = vertexIndices.get(vertex);
        List<T> neighbors = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (matrix.get(vertexIndex).get(i) == 1) {
                neighbors.add(vertices.get(i));
            }
        }
        return neighbors;
    }

    @Override
    public void removeVertex(T vertex) {
        if (!vertexIndices.containsKey(vertex)) {
            return;
        }

        int removeIndex = vertexIndices.get(vertex);

        vertices.remove(removeIndex);
        vertexIndices.remove(vertex);

        vertexIndices.clear();
        for (int i = 0; i < vertices.size(); i++) {
            vertexIndices.put(vertices.get(i), i);
        }

        matrix.remove(removeIndex);

        for (List<Integer> row : matrix) {
            row.remove(removeIndex);
        }
    }

    @Override
    public void removeEdge(T from, T to) {
        if (!vertexIndices.containsKey(from) || !vertexIndices.containsKey(to)) {
            return;
        }

        int fromIndex = vertexIndices.get(from);
        int toIndex = vertexIndices.get(to);
        matrix.get(fromIndex).set(toIndex, 0);
    }

    @Override
    public boolean hasVertex(T vertex) {
        return vertexIndices.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(T from, T to) {
        if (!vertexIndices.containsKey(from) || !vertexIndices.containsKey(to)) {
            return false;
        }

        int fromIndex = vertexIndices.get(from);
        int toIndex = vertexIndices.get(to);
        return matrix.get(fromIndex).get(toIndex) == 1;
    }

    @Override
    public Set<T> getAllVertices() {
        return new HashSet<>(vertices);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Adjacency Matrix:\n");
        for (int i = 0; i < vertices.size(); i++) {
            sb.append(vertices.get(i)).append(": ");
            sb.append(matrix.get(i)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, matrix);
    }

    @Override
    public boolean equals(Object obj) {
        return graphEquals(obj);
    }
}