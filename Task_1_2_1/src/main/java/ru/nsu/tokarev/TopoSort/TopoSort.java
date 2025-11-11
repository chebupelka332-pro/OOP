package ru.nsu.tokarev.TopoSort;

import ru.nsu.tokarev.Matrixs.Graph;
import java.util.*;


public class TopoSort {
    public static <T> List<T> sort(Graph<T> graph) {
        Set<T> vertices = new HashSet<>(graph.getAllVertices());
        Map<T, Integer> inDegree = new HashMap<>();
        Map<T, Set<T>> adjacencyMap = new HashMap<>();

        for (T vertex : vertices) {
            inDegree.put(vertex, 0);
            adjacencyMap.put(vertex, new HashSet<>());
        }

        for (T from : vertices) {
            for (T to : graph.getNeighbors(from)) {
                adjacencyMap.get(from).add(to);
                inDegree.put(to, inDegree.get(to) + 1);
            }
        }

        Queue<T> queue = new LinkedList<>();
        for (T vertex : vertices) {
            if (inDegree.get(vertex) == 0) {
                queue.offer(vertex);
            }
        }

        List<T> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            T current = queue.poll();
            result.add(current);

            for (T neighbor : adjacencyMap.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        if (result.size() != vertices.size()) {
            throw new IllegalArgumentException("Graph contains cycles");
        }

        return result;
    }

    private static <T> boolean dfsVisit(Graph<T> graph, T vertex, Set<T> visited,
                                       Set<T> recursionStack, Stack<T> stack) {
        visited.add(vertex);
        recursionStack.add(vertex);

        for (T neighbor : graph.getNeighbors(vertex)) {
            if (recursionStack.contains(neighbor)) {
                return true; // Цикл обнаружен
            }
            if (!visited.contains(neighbor)) {
                if (dfsVisit(graph, neighbor, visited, recursionStack, stack)) {
                    return true;
                }
            }
        }

        recursionStack.remove(vertex);
        stack.push(vertex);
        return false;
    }

    public static <T> boolean isDAG(Graph<T> graph) {
        try {
            sort(graph);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static <T> void findCyclesDFS(Graph<T> graph, T vertex, Set<T> visited,
                                         Set<T> recursionStack, Map<T, T> parent,
                                         List<List<T>> cycles) {
        visited.add(vertex);
        recursionStack.add(vertex);

        for (T neighbor : graph.getNeighbors(vertex)) {
            if (recursionStack.contains(neighbor)) {
                List<T> cycle = new ArrayList<>();
                T current = vertex;
                cycle.add(neighbor);
                while (!current.equals(neighbor)) {
                    cycle.add(current);
                    current = parent.get(current);
                }
                Collections.reverse(cycle);
                cycles.add(cycle);
            } else if (!visited.contains(neighbor)) {
                parent.put(neighbor, vertex);
                findCyclesDFS(graph, neighbor, visited, recursionStack, parent, cycles);
            }
        }

        recursionStack.remove(vertex);
    }

    public static <T> String formatResult(List<T> vertices) {
        if (vertices.isEmpty()) {
            return "Empty graph";
        }

        StringBuilder sb = new StringBuilder("Topological order: ");
        for (int i = 0; i < vertices.size(); i++) {
            sb.append(vertices.get(i));
            if (i < vertices.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}
