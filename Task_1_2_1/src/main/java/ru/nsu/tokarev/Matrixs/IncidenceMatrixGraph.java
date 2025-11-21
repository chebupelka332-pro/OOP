package ru.nsu.tokarev.Matrixs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.Objects;
import java.util.HashSet;

public class IncidenceMatrixGraph<T> implements Graph<T> {
    private final List<T> vertices = new ArrayList<>();
    private final List<T> edgesFrom = new ArrayList<>();  // исходные вершины рёбер
    private final List<T> edgesTo = new ArrayList<>();    // конечные вершины рёбер
    private final List<List<Integer>> incidenceMatrix = new ArrayList<>();
    private final Map<T, Integer> vertexIndices = new HashMap<>();

    private void rebuildIncidenceMatrix() {
        incidenceMatrix.clear();

        for (int i = 0; i < vertices.size(); i++) {
            incidenceMatrix.add(new ArrayList<>());
            for (int j = 0; j < edgesFrom.size(); j++) {
                incidenceMatrix.get(i).add(0);
            }
        }

        for (int edgeIndex = 0; edgeIndex < edgesFrom.size(); edgeIndex++) {
            T from = edgesFrom.get(edgeIndex);
            T to = edgesTo.get(edgeIndex);
            Integer fromIndex = vertexIndices.get(from);
            Integer toIndex = vertexIndices.get(to);

            if (fromIndex != null && toIndex != null) {
                incidenceMatrix.get(fromIndex).set(edgeIndex, 1);
                incidenceMatrix.get(toIndex).set(edgeIndex, -1);
            }
        }
    }

    @Override
    public void addVertex(T vertex) {
        if (vertexIndices.containsKey(vertex)) {
            return;
        }

        vertices.add(vertex);
        vertexIndices.put(vertex, vertices.size() - 1);

        incidenceMatrix.add(new ArrayList<>());

        for (List<Integer> row : incidenceMatrix) {
            while (row.size() < edgesFrom.size()) {
                row.add(0);
            }
        }
    }

    @Override
    public void addEdge(T from, T to) {
        if (!vertexIndices.containsKey(from)) addVertex(from);
        if (!vertexIndices.containsKey(to)) addVertex(to);

        for (int i = 0; i < edgesFrom.size(); i++) {
            if (edgesFrom.get(i).equals(from) && edgesTo.get(i).equals(to)) {
                return;
            }
        }

        edgesFrom.add(from);
        edgesTo.add(to);

        for (int i = 0; i < vertices.size(); i++) {
            incidenceMatrix.get(i).add(0);
        }
        
        int edgeIndex = edgesFrom.size() - 1;
        int fromIndex = vertexIndices.get(from);
        int toIndex = vertexIndices.get(to);

        incidenceMatrix.get(fromIndex).set(edgeIndex, 1);  // исходящее ребро
        incidenceMatrix.get(toIndex).set(edgeIndex, -1);   // входящее ребро
    }

    @Override
    public void removeVertex(T vertex) {
        if (!vertexIndices.containsKey(vertex)) {
            return;
        }

        int vertexIndex = vertexIndices.get(vertex);

        for (int i = edgesFrom.size() - 1; i >= 0; i--) {
            if (edgesFrom.get(i).equals(vertex) || edgesTo.get(i).equals(vertex)) {
                edgesFrom.remove(i);
                edgesTo.remove(i);
            }
        }

        rebuildIncidenceMatrix();

        vertices.remove(vertexIndex);

        vertexIndices.clear();
        for (int i = 0; i < vertices.size(); i++) {
            vertexIndices.put(vertices.get(i), i);
        }
    }

    @Override
    public void removeEdge(T from, T to) {
        int edgeIndex = -1;
        for (int i = 0; i < edgesFrom.size(); i++) {
            if (edgesFrom.get(i).equals(from) && edgesTo.get(i).equals(to)) {
                edgeIndex = i;
                break;
            }
        }

        if (edgeIndex == -1) {
            return;
        }
        
        edgesFrom.remove(edgeIndex);
        edgesTo.remove(edgeIndex);

        for (List<Integer> row : incidenceMatrix) {
            row.remove(edgeIndex);
        }
    }

    @Override
    public List<T> getNeighbors(T vertex) {
        if (!vertexIndices.containsKey(vertex)) {
            return new ArrayList<>();
        }
        
        List<T> neighbors = new ArrayList<>();
        for (int i = 0; i < edgesFrom.size(); i++) {
            if (edgesFrom.get(i).equals(vertex)) {
                neighbors.add(edgesTo.get(i));
            }
        }
        
        return neighbors;
    }

    @Override
    public Set<T> getAllVertices() {
        return new HashSet<>(vertices);
    }

    @Override
    public boolean hasVertex(T vertex) {
        return vertexIndices.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(T from, T to) {
        for (int i = 0; i < edgesFrom.size(); i++) {
            if (edgesFrom.get(i).equals(from) && edgesTo.get(i).equals(to)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (vertices.isEmpty()) {
            return "Incidence Matrix: (empty graph)";
        }
        
        StringBuilder sb = new StringBuilder("Incidence Matrix:\n");

        for (T vertex : vertices) {
            sb.append(vertex).append(" -> ");
            List<T> neighbors = getNeighbors(vertex);
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
    public int hashCode() {
        int result = getAllVertices().hashCode();

        for (T vertex1 : getAllVertices()) {
            for (T vertex2 : getAllVertices()) {
                if (hasEdge(vertex1, vertex2)) {
                    result = 31 * result + Objects.hash(vertex1, vertex2);
                }
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return graphEquals(obj);
    }
}
