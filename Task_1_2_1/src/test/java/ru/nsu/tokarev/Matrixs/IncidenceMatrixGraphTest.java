package ru.nsu.tokarev.Matrixs;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class IncidenceMatrixGraphTest {

    private IncidenceMatrixGraph<String> graph;

    @BeforeEach
    void setUp() {
        graph = new IncidenceMatrixGraph<>();
    }

    @Test
    void testAddVertex() {
        graph.addVertex("A");
        assertTrue(graph.hasVertex("A"));
        assertEquals(1, graph.getAllVertices().size());
    }

    @Test
    void testAddEdge() {
        graph.addEdge("A", "B");
        
        assertTrue(graph.hasVertex("A"));
        assertTrue(graph.hasVertex("B"));
        assertTrue(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "A"));
        
        List<String> neighborsA = graph.getNeighbors("A");
        assertEquals(1, neighborsA.size());
        assertEquals("B", neighborsA.get(0));
    }

    @Test
    void testPreventDuplicateEdges() {
        graph.addEdge("A", "B");
        graph.addEdge("A", "B"); // Duplicate
        
        List<String> neighbors = graph.getNeighbors("A");
        assertEquals(1, neighbors.size());
    }

    @Test
    void testRemoveVertex() {
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "C");
        
        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("A", "C"));
        
        graph.removeVertex("B");
        
        assertFalse(graph.hasVertex("B"));
        assertFalse(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "C"));
        assertTrue(graph.hasEdge("A", "C"));
    }

    @Test
    void testRemoveEdge() {
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        
        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("A", "C"));
        
        graph.removeEdge("A", "B");
        
        assertFalse(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("A", "C"));
        assertTrue(graph.hasVertex("A"));
        assertTrue(graph.hasVertex("B"));
    }

    @Test
    void testGetNeighbors() {
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("A", "D");
        
        List<String> neighbors = graph.getNeighbors("A");
        assertEquals(3, neighbors.size());
        assertTrue(neighbors.contains("B"));
        assertTrue(neighbors.contains("C"));
        assertTrue(neighbors.contains("D"));
        
        List<String> noNeighbors = graph.getNeighbors("B");
        assertTrue(noNeighbors.isEmpty());
    }

    @Test
    void testNonExistentVertices() {
        assertFalse(graph.hasVertex("NonExistent"));
        assertFalse(graph.hasEdge("A", "NonExistent"));
        
        List<String> neighbors = graph.getNeighbors("NonExistent");
        assertTrue(neighbors.isEmpty());
    }

    @Test
    void testComplexOperations() {
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");
        graph.addEdge("A", "D");
        graph.addEdge("B", "D");
        
        assertEquals(4, graph.getAllVertices().size());

        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("B", "C"));
        assertTrue(graph.hasEdge("C", "D"));
        assertTrue(graph.hasEdge("A", "D"));
        assertTrue(graph.hasEdge("B", "D"));

        graph.removeVertex("B");

        assertFalse(graph.hasVertex("B"));
        assertTrue(graph.hasEdge("A", "D"));
        assertTrue(graph.hasEdge("C", "D"));
        assertFalse(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "C"));
        assertFalse(graph.hasEdge("B", "D"));
    }

    @Test
    void testEquals() {
        IncidenceMatrixGraph<String> graph1 = new IncidenceMatrixGraph<>();
        IncidenceMatrixGraph<String> graph2 = new IncidenceMatrixGraph<>();
        
        assertEquals(graph1, graph2);
        
        graph1.addEdge("A", "B");
        graph2.addEdge("A", "B");
        
        assertEquals(graph1, graph2);
        
        graph1.addEdge("B", "C");
        assertNotEquals(graph1, graph2);
    }

    @Test
    void testHashCode() {
        IncidenceMatrixGraph<String> graph1 = new IncidenceMatrixGraph<>();
        IncidenceMatrixGraph<String> graph2 = new IncidenceMatrixGraph<>();
        
        graph1.addEdge("A", "B");
        graph2.addEdge("A", "B");
        
        assertEquals(graph1.hashCode(), graph2.hashCode());
    }

    @Test
    void testToString() {
        String emptyResult = graph.toString();
        assertTrue(emptyResult.contains("Incidence Matrix"));
        assertTrue(emptyResult.contains("empty graph"));
        
        graph.addEdge("A", "B");
        String result = graph.toString();
        assertTrue(result.contains("A"));
        assertTrue(result.contains("B"));
        assertTrue(result.contains("->"));
    }

    @Test
    void testIsolatedVertices() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("C", "D");
        
        assertTrue(graph.hasVertex("A"));
        assertTrue(graph.hasVertex("B"));
        assertTrue(graph.getNeighbors("A").isEmpty());
        assertTrue(graph.getNeighbors("B").isEmpty());
        
        assertFalse(graph.getNeighbors("C").isEmpty());
        assertEquals(1, graph.getNeighbors("C").size());
    }

    @Test
    void testCharacterVertices() {
        IncidenceMatrixGraph<Character> charGraph = new IncidenceMatrixGraph<>();
        
        charGraph.addEdge('X', 'Y');
        charGraph.addEdge('Y', 'Z');
        
        assertTrue(charGraph.hasEdge('X', 'Y'));
        assertTrue(charGraph.hasEdge('Y', 'Z'));
        assertFalse(charGraph.hasEdge('X', 'Z'));
        
        List<Character> neighbors = charGraph.getNeighbors('X');
        assertEquals(1, neighbors.size());
        assertEquals(Character.valueOf('Y'), neighbors.get(0));
    }

    @Test
    void testSelfLoops() {
        graph.addEdge("A", "A");
        
        assertTrue(graph.hasEdge("A", "A"));
        List<String> neighbors = graph.getNeighbors("A");
        assertEquals(1, neighbors.size());
        assertEquals("A", neighbors.get(0));
    }

    @Test
    void testMatrixConsistency() {
        graph.addEdge("A", "B");
        graph.addEdge("C", "D");
        graph.addEdge("B", "C");

        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("C", "D"));
        assertTrue(graph.hasEdge("B", "C"));

        graph.removeEdge("C", "D");
        
        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("B", "C"));
        assertFalse(graph.hasEdge("C", "D"));

        graph.addEdge("D", "A");
        assertTrue(graph.hasEdge("D", "A"));
    }

    @Test
    void testRemoveNonExistentEdge() {
        graph.addEdge("A", "B");

        assertDoesNotThrow(() -> graph.removeEdge("B", "A"));
        assertDoesNotThrow(() -> graph.removeEdge("C", "D"));

        assertTrue(graph.hasEdge("A", "B"));
    }
}
