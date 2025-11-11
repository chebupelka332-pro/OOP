package ru.nsu.tokarev;

import ru.nsu.tokarev.Matrixs.*;
import ru.nsu.tokarev.TopoSort.TopoSort;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        testAdjacencyListGraph();
        testAdjacencyMatrixGraph();
        testIncidenceMatrixGraph();

        testTopologicalSort();

        testFileOperations();

        testCycleDetection();
    }

    private static void testAdjacencyListGraph() {
        System.out.println("1. TEST ADJACENCY LIST GRAPH");

        AdjacencyListGraph<String> graph = new AdjacencyListGraph<>();

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "C");

        System.out.println(graph);

        System.out.println("All vertex: " + graph.getAllVertices());
        System.out.println("Neighbors A: " + graph.getNeighbors("A"));
        System.out.println("Edge A->B? " + graph.hasEdge("A", "B"));
        System.out.println("Edge C->A? " + graph.hasEdge("C", "A"));

        System.out.println("\nDell edge A->C...");
        graph.removeEdge("A", "C");
        System.out.println(graph);

        System.out.println("Dell vertex B...");
        graph.removeVertex("B");
        System.out.println(graph);
        System.out.println();
    }

    private static void testAdjacencyMatrixGraph() {
        System.out.println("2. TEST ADJACENCY MATRIX GRAPH");

        AdjacencyMatrixGraph<Integer> graph = new AdjacencyMatrixGraph<>();

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(1, 3);
        graph.addEdge(3, 4);

        System.out.println(graph);

        System.out.println("Vertex count: " + graph.getAllVertices().size());
        System.out.println("Neighbors 1: " + graph.getNeighbors(1));
        System.out.println("Edge 5? " + graph.hasVertex(5));

        AdjacencyMatrixGraph<Integer> graph2 = new AdjacencyMatrixGraph<>();
        graph2.addEdge(1, 2);
        graph2.addEdge(2, 3);
        graph2.addEdge(1, 3);
        graph2.addEdge(3, 4);

        System.out.println("Graphs equals? " + graph.equals(graph2));
        System.out.println();
    }

    private static void testIncidenceMatrixGraph() {
        System.out.println("3. TEST INCIDENCE MATRIX GRAPH");

        IncidenceMatrixGraph<Character> graph = new IncidenceMatrixGraph<>();

        graph.addEdge('X', 'Y');
        graph.addEdge('Y', 'Z');
        graph.addEdge('X', 'Z');

        System.out.println(graph);

        System.out.println("Neighbors X: " + graph.getNeighbors('X'));
        System.out.println("Edge Y->Z? " + graph.hasEdge('Y', 'Z'));

        System.out.println("\nAdd isolated vertex W...");
        graph.addVertex('W');
        System.out.println(graph);

        System.out.println("Dell vertex Y...");
        graph.removeVertex('Y');
        System.out.println(graph);
        System.out.println();
    }

    private static void testTopologicalSort() {
        System.out.println("4. TEST TOPOLOGICAL SORT");
        System.out.println("=".repeat(40));

        AdjacencyListGraph<String> dag = new AdjacencyListGraph<>();

        System.out.println("Creating DAG for task planning...");

        dag.addEdge("Planning", "Design");
        dag.addEdge("Planning", "Research");
        dag.addEdge("Design", "Development");
        dag.addEdge("Research", "Development");
        dag.addEdge("Development", "Testing");
        dag.addEdge("Testing", "Deployment");

        System.out.println("Project structure:");
        System.out.println(dag);

        System.out.println("Is graph DAG? " + TopoSort.isDAG(dag));

        try {
            List<String> order = TopoSort.sort(dag);
            System.out.println(TopoSort.formatResult(order));
            System.out.println("Task execution order: " + order);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nTesting on AdjacencyMatrixGraph...");
        AdjacencyMatrixGraph<String> matrixDag = new AdjacencyMatrixGraph<>();
        matrixDag.addEdge("A", "B");
        matrixDag.addEdge("A", "C");
        matrixDag.addEdge("B", "D");
        matrixDag.addEdge("C", "D");

        List<String> matrixOrder = TopoSort.sort(matrixDag);
        System.out.println("Result for adjacency matrix: " + TopoSort.formatResult(matrixOrder));
        System.out.println();
    }

    private static void testFileOperations() {
        System.out.println("5. TEST FILE OPERATIONS");
        System.out.println("=".repeat(40));

        String filename = "test_graph.txt";
        try {
            System.out.println("Creating test graph file...");
            FileWriter writer = new FileWriter(filename);
            writer.write("Moscow Petersburg\n");
            writer.write("Petersburg Novgorod\n");
            writer.write("Moscow Tula\n");
            writer.write("Tula Voronezh\n");
            writer.write("Novgorod Pskov\n");
            writer.close();

            AdjacencyListGraph<String> fileGraph = new AdjacencyListGraph<>();
            fileGraph.readFromFile(filename);

            System.out.println("Graph loaded from file:");
            System.out.println(fileGraph);

            if (TopoSort.isDAG(fileGraph)) {
                List<String> cities = TopoSort.sort(fileGraph);
                System.out.println("Cities order: " + TopoSort.formatResult(cities));
            } else {
                System.out.println("Graph contains cycles!");
            }

        } catch (IOException e) {
            System.out.println("File operation error: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testCycleDetection() {
        System.out.println("6. TEST CYCLE DETECTION");
        System.out.println("=".repeat(40));

        IncidenceMatrixGraph<String> cyclicGraph = new IncidenceMatrixGraph<>();

        System.out.println("Creating graph with cycle...");
        cyclicGraph.addEdge("A", "B");
        cyclicGraph.addEdge("B", "C");
        cyclicGraph.addEdge("C", "A");  // Creates cycle A -> B -> C -> A
        cyclicGraph.addEdge("A", "D");

        System.out.println(cyclicGraph);

        System.out.println("Is graph DAG? " + TopoSort.isDAG(cyclicGraph));

        try {
            List<String> order = TopoSort.sort(cyclicGraph);
            System.out.println("Unexpected success: " + TopoSort.formatResult(order));
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }
}
