package ru.nsu.tokarev.Matrixs;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class AdjacencyMatrixGraphTest {

    private AdjacencyMatrixGraph<String> graph;

    @BeforeEach
    void setUp() {
        graph = new AdjacencyMatrixGraph<>();
    }

    @Test
    void testAddVertex() {
        graph.addVertex("A");
        assertTrue(graph.hasVertex("A"));
        assertEquals(1, graph.getAllVertices().size());
    }

    @Test
    void testMatrixExpansion() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        
        assertEquals(3, graph.getAllVertices().size());

        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        
        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("B", "C"));
        assertFalse(graph.hasEdge("A", "C"));
    }

    @Test
    void testVertexRemovalMatrixRestructuring() {
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "C");
        
        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("A", "C"));
        
        graph.removeVertex("B");
        
        assertFalse(graph.hasVertex("B"));
        assertTrue(graph.hasEdge("A", "C"));
        assertFalse(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "C"));
    }

    @Test
    void testEdgeOperations() {
        graph.addEdge("X", "Y");
        
        assertTrue(graph.hasEdge("X", "Y"));
        assertFalse(graph.hasEdge("Y", "X"));
        
        graph.removeEdge("X", "Y");
        
        assertFalse(graph.hasEdge("X", "Y"));
        assertTrue(graph.hasVertex("X"));
        assertTrue(graph.hasVertex("Y"));
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
        
        List<String> emptyNeighbors = graph.getNeighbors("B");
        assertTrue(emptyNeighbors.isEmpty());
    }

    @Test
    void testNonExistentVertices() {
        assertFalse(graph.hasVertex("NonExistent"));
        assertFalse(graph.hasEdge("A", "NonExistent"));
        
        List<String> neighbors = graph.getNeighbors("NonExistent");
        assertTrue(neighbors.isEmpty());

        assertDoesNotThrow(() -> graph.removeVertex("NonExistent"));
        assertDoesNotThrow(() -> graph.removeEdge("A", "NonExistent"));
    }

    @Test
    void testEqualsAndHashCode() {
        AdjacencyMatrixGraph<String> graph1 = new AdjacencyMatrixGraph<>();
        AdjacencyMatrixGraph<String> graph2 = new AdjacencyMatrixGraph<>();
        
        assertEquals(graph1, graph2);
        assertEquals(graph1.hashCode(), graph2.hashCode());
        
        graph1.addEdge("A", "B");
        graph2.addEdge("A", "B");
        
        assertEquals(graph1, graph2);
        assertEquals(graph1.hashCode(), graph2.hashCode());
        
        graph1.addEdge("B", "C");
        assertNotEquals(graph1, graph2);
    }

    @Test
    void testLargeGraph() {
        int size = 100;

        for (int i = 0; i < size - 1; i++) {
            graph.addEdge(String.valueOf(i), String.valueOf(i + 1));
        }
        
        assertEquals(size, graph.getAllVertices().size());

        assertTrue(graph.hasEdge("0", "1"));
        assertTrue(graph.hasEdge("98", "99"));
        assertFalse(graph.hasEdge("99", "0"));

        List<String> firstNeighbors = graph.getNeighbors("0");
        assertEquals(1, firstNeighbors.size());
        assertEquals("1", firstNeighbors.get(0));
        
        List<String> lastNeighbors = graph.getNeighbors("99");
        assertTrue(lastNeighbors.isEmpty());
    }

    @Test
    void testToString() {
        String emptyResult = graph.toString();
        assertTrue(emptyResult.contains("Adjacency Matrix"));
        
        graph.addEdge("A", "B");
        String result = graph.toString();
        assertTrue(result.contains("A"));
        assertTrue(result.contains("B"));
    }

    @Test
    void testIntegerVertices() {
        AdjacencyMatrixGraph<Integer> intGraph = new AdjacencyMatrixGraph<>();
        
        intGraph.addEdge(1, 2);
        intGraph.addEdge(2, 3);
        
        assertTrue(intGraph.hasEdge(1, 2));
        assertTrue(intGraph.hasEdge(2, 3));
        assertFalse(intGraph.hasEdge(1, 3));
        
        assertEquals(3, intGraph.getAllVertices().size());
    }

    @Test
    void testMatrixConsistency() {
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");

        assertTrue(graph.hasEdge("A", "B"));
        assertTrue(graph.hasEdge("B", "C"));
        assertTrue(graph.hasEdge("C", "A"));

        graph.removeVertex("B");

        assertTrue(graph.hasEdge("C", "A"));
        assertFalse(graph.hasEdge("A", "B"));
        assertFalse(graph.hasEdge("B", "C"));

        graph.addEdge("A", "D");
        assertTrue(graph.hasEdge("A", "D"));
        assertTrue(graph.hasEdge("C", "A"));
    }
}
