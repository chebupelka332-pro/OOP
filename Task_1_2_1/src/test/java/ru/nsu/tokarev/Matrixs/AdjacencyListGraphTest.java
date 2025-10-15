package ru.nsu.tokarev.Matrixs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class AdjacencyListGraphTest {

    private AdjacencyListGraph<String> graph;

    @BeforeEach
    void setUp() {
        graph = new AdjacencyListGraph<>();
    }

    @Test
    void testAddVertex() {
        graph.addVertex("A");
        assertTrue(graph.hasVertex("A"));
        assertEquals(1, graph.getAllVertices().size());
    }

    @Test
    void testAddDuplicateVertex() {
        graph.addVertex("A");
        graph.addVertex("A");
        assertEquals(1, graph.getAllVertices().size());
    }

    @Test
    void testAddEdge() {
        graph.addEdge("A", "B");
        
        assertTrue(graph.hasVertex("A"));
        assertTrue(graph.hasVertex("B"));
        assertTrue(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "A"));
    }

    @Test
    void testAddDuplicateEdge() {
        graph.addEdge("A", "B");
        graph.addEdge("A", "B");
        
        List<String> neighbors = graph.getNeighbors("A");
        assertEquals(1, neighbors.size());
        assertEquals("B", neighbors.get(0));
    }

    @Test
    void testRemoveVertex() {
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");
        
        graph.removeVertex("B");
        
        assertFalse(graph.hasVertex("B"));
        assertFalse(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "C"));
        assertTrue(graph.hasVertex("A"));
        assertTrue(graph.hasVertex("C"));
    }

    @Test
    void testRemoveEdge() {
        graph.addEdge("A", "B");
        assertTrue(graph.hasEdge("A", "B"));
        
        graph.removeEdge("A", "B");
        assertFalse(graph.hasEdge("A", "B"));
        assertTrue(graph.hasVertex("A"));
        assertTrue(graph.hasVertex("B"));
    }

    @Test
    void testGetNeighbors() {
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "C");
        
        List<String> neighborsA = graph.getNeighbors("A");
        assertEquals(2, neighborsA.size());
        assertTrue(neighborsA.contains("B"));
        assertTrue(neighborsA.contains("C"));
        
        List<String> neighborsC = graph.getNeighbors("C");
        assertTrue(neighborsC.isEmpty());
    }

    @Test
    void testGetNeighborsNonExistent() {
        List<String> neighbors = graph.getNeighbors("NonExistent");
        assertNotNull(neighbors);
        assertTrue(neighbors.isEmpty());
    }

    @Test
    void testGetAllVertices() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("C", "D");
        
        Set<String> vertices = graph.getAllVertices();
        assertEquals(4, vertices.size());
        assertTrue(vertices.containsAll(Arrays.asList("A", "B", "C", "D")));
    }

    @Test
    void testHasVertex() {
        assertFalse(graph.hasVertex("A"));
        
        graph.addVertex("A");
        assertTrue(graph.hasVertex("A"));
        
        graph.removeVertex("A");
        assertFalse(graph.hasVertex("A"));
    }

    @Test
    void testHasEdge() {
        assertFalse(graph.hasEdge("A", "B"));
        
        graph.addEdge("A", "B");
        assertTrue(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "A"));
        
        graph.removeEdge("A", "B");
        assertFalse(graph.hasEdge("A", "B"));
    }

    @Test
    void testReadFromFile() throws IOException {
        String filename = "test_graph.txt";
        
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println("A B");
            writer.println("B C");
            writer.println("A C");
        }
        
        graph.readFromFile(filename);
        
        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("B", "C"));
        assertTrue(graph.hasEdge("A", "C"));
        assertEquals(3, graph.getAllVertices().size());
        
        Files.deleteIfExists(Paths.get(filename));
    }

    @Test
    void testReadFromEmptyFile() throws IOException {
        String filename = "empty_graph.txt";
        
        try (PrintWriter writer = new PrintWriter(filename)) {
            // Empty file
        }
        
        graph.readFromFile(filename);
        assertTrue(graph.getAllVertices().isEmpty());
        
        Files.deleteIfExists(Paths.get(filename));
    }

    @Test
    void testReadFromFileWithInvalidLines() throws IOException {
        String filename = "invalid_graph.txt";
        
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println("A B");
            writer.println("SingleVertex");
            writer.println("A B C D");
            writer.println("");
            writer.println("C D");
        }
        
        graph.readFromFile(filename);
        
        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("C", "D"));
        assertEquals(4, graph.getAllVertices().size());
        
        Files.deleteIfExists(Paths.get(filename));
    }

    @Test
    void testEquals() {
        AdjacencyListGraph<String> graph1 = new AdjacencyListGraph<>();
        AdjacencyListGraph<String> graph2 = new AdjacencyListGraph<>();

        assertEquals(graph1, graph2);

        graph1.addEdge("A", "B");
        graph1.addEdge("B", "C");
        graph2.addEdge("A", "B");
        graph2.addEdge("B", "C");
        
        assertEquals(graph1, graph2);

        graph2.addEdge("C", "A");
        assertNotEquals(graph1, graph2);
    }

    @Test
    void testHashCode() {
        AdjacencyListGraph<String> graph1 = new AdjacencyListGraph<>();
        AdjacencyListGraph<String> graph2 = new AdjacencyListGraph<>();
        
        graph1.addEdge("A", "B");
        graph2.addEdge("A", "B");
        
        assertEquals(graph1.hashCode(), graph2.hashCode());
    }

    @Test
    void testToString() {
        String result = graph.toString();
        assertTrue(result.contains("Adjacency List"));
        assertTrue(result.contains("empty graph"));
        
        graph.addEdge("A", "B");
        result = graph.toString();
        assertTrue(result.contains("A"));
        assertTrue(result.contains("B"));
        assertTrue(result.contains("->"));
    }

    @Test
    void testSelfLoop() {
        graph.addEdge("A", "A");
        
        assertTrue(graph.hasEdge("A", "A"));
        List<String> neighbors = graph.getNeighbors("A");
        assertEquals(1, neighbors.size());
        assertEquals("A", neighbors.get(0));
    }

    @Test
    void testNeighborsDefensiveCopy() {
        graph.addEdge("A", "B");
        List<String> neighbors = graph.getNeighbors("A");

        neighbors.add("C");
        
        List<String> actualNeighbors = graph.getNeighbors("A");
        assertEquals(1, actualNeighbors.size());
        assertEquals("B", actualNeighbors.get(0));
    }

    @Test
    void testVerticesDefensiveCopy() {
        graph.addVertex("A");
        Set<String> vertices = graph.getAllVertices();

        vertices.add("B");
        
        Set<String> actualVertices = graph.getAllVertices();
        assertEquals(1, actualVertices.size());
        assertTrue(actualVertices.contains("A"));
        assertFalse(actualVertices.contains("B"));
    }
}
