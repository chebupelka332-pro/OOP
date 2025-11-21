package ru.nsu.tokarev.TopoSort;

import java.util.List;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.tokarev.Matrixs.*;

import static org.junit.jupiter.api.Assertions.*;


public class TopoSortTest {

    private AdjacencyListGraph<String> dag;
    private AdjacencyListGraph<String> cyclicGraph;

    @BeforeEach
    void setUp() {
        dag = new AdjacencyListGraph<>();
        cyclicGraph = new AdjacencyListGraph<>();
    }

    @Test
    void testsortSimpleDAG() {
        dag.addEdge("A", "B");
        dag.addEdge("B", "C");
        
        List<String> result = TopoSort.sort(dag);
        
        assertEquals(3, result.size());
        assertEquals("A", result.get(0));
        assertEquals("B", result.get(1));
        assertEquals("C", result.get(2));
    }

    @Test
    void testsortComplexDAG() {
        dag.addEdge("A", "C");
        dag.addEdge("B", "C");
        dag.addEdge("C", "D");
        
        List<String> result = TopoSort.sort(dag);
        
        assertEquals(4, result.size());
        assertTrue(result.contains("A"));
        assertTrue(result.contains("B"));
        assertTrue(result.contains("C"));
        assertTrue(result.contains("D"));

        int indexA = result.indexOf("A");
        int indexB = result.indexOf("B");
        int indexC = result.indexOf("C");
        int indexD = result.indexOf("D");
        
        assertTrue(indexA < indexC);
        assertTrue(indexB < indexC);
        assertTrue(indexC < indexD);
    }

    @Test
    void testsortDetectsCycle() {
        cyclicGraph.addEdge("A", "B");
        cyclicGraph.addEdge("B", "C");
        cyclicGraph.addEdge("C", "A");
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TopoSort.sort(cyclicGraph)
        );
        
        assertTrue(exception.getMessage().contains("cycles"));
    }

    @Test
    void testsortEmptyGraph() {
        List<String> result = TopoSort.sort(dag);
        assertTrue(result.isEmpty());
    }

    @Test
    void testsortSingleVertex() {
        dag.addVertex("A");
        
        List<String> result = TopoSort.sort(dag);
        
        assertEquals(1, result.size());
        assertEquals("A", result.get(0));
    }

    @Test
    void testsortDisconnectedComponents() {
        dag.addEdge("A", "B");
        dag.addEdge("C", "D");
        
        List<String> result = TopoSort.sort(dag);
        
        assertEquals(4, result.size());
        
        int indexA = result.indexOf("A");
        int indexB = result.indexOf("B");
        int indexC = result.indexOf("C");
        int indexD = result.indexOf("D");
        
        assertTrue(indexA < indexB);
        assertTrue(indexC < indexD);
    }

    @Test
    void testIsDAG() {
        assertTrue(TopoSort.isDAG(dag));

        dag.addEdge("A", "B");
        dag.addEdge("B", "C");
        assertTrue(TopoSort.isDAG(dag));

        cyclicGraph.addEdge("A", "B");
        cyclicGraph.addEdge("B", "C");
        cyclicGraph.addEdge("C", "A");
        assertFalse(TopoSort.isDAG(cyclicGraph));
    }

    @Test
    void testDifferentGraphImplementations() {
        AdjacencyMatrixGraph<String> matrixGraph = new AdjacencyMatrixGraph<>();
        matrixGraph.addEdge("A", "B");
        matrixGraph.addEdge("B", "C");
        
        List<String> matrixResult = TopoSort.sort(matrixGraph);
        assertEquals(Arrays.asList("A", "B", "C"), matrixResult);

        IncidenceMatrixGraph<String> incidenceGraph = new IncidenceMatrixGraph<>();
        incidenceGraph.addEdge("X", "Y");
        incidenceGraph.addEdge("Y", "Z");
        
        List<String> incidenceResult = TopoSort.sort(incidenceGraph);
        assertEquals(Arrays.asList("X", "Y", "Z"), incidenceResult);
    }

    @Test
    void testSelfLoopDetection() {
        dag.addEdge("A", "A");
        
        assertFalse(TopoSort.isDAG(dag));
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TopoSort.sort(dag)
        );
        
        assertTrue(exception.getMessage().contains("cycles"));
    }

    @Test
    void testFormatResult() {
        List<String> empty = Arrays.asList();
        String emptyFormatted = TopoSort.formatResult(empty);
        assertEquals("Empty graph", emptyFormatted);
        
        List<String> single = Arrays.asList("A");
        String singleFormatted = TopoSort.formatResult(single);
        assertEquals("Topological order: A", singleFormatted);
        
        List<String> multiple = Arrays.asList("A", "B", "C");
        String multipleFormatted = TopoSort.formatResult(multiple);
        assertEquals("Topological order: A -> B -> C", multipleFormatted);
    }

    @Test
    void testLargeDAG() {
        for (int i = 0; i < 99; i++) {
            dag.addEdge(String.valueOf(i), String.valueOf(i + 1));
        }
        
        List<String> result = TopoSort.sort(dag);
        
        assertEquals(100, result.size());

        for (int i = 0; i < 100; i++) {
            assertEquals(String.valueOf(i), result.get(i));
        }
    }

    @Test
    void testDiamondDAG() {
        dag.addEdge("A", "B");
        dag.addEdge("A", "C");
        dag.addEdge("B", "D");
        dag.addEdge("C", "D");
        
        List<String> result = TopoSort.sort(dag);
        
        assertEquals(4, result.size());
        assertEquals("A", result.get(0));
        assertEquals("D", result.get(3));

        assertTrue(result.indexOf("B") < result.indexOf("D"));
        assertTrue(result.indexOf("C") < result.indexOf("D"));
    }

    @Test
    void testNumericVertices() {
        AdjacencyListGraph<Integer> numGraph = new AdjacencyListGraph<>();
        numGraph.addEdge(1, 2);
        numGraph.addEdge(2, 3);
        numGraph.addEdge(1, 3);
        
        List<Integer> result = TopoSort.sort(numGraph);
        
        assertEquals(3, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
        assertTrue(result.indexOf(2) < result.indexOf(3));
    }

    @Test
    void testComplexCycleDetection() {
        cyclicGraph.addEdge("A", "B");
        cyclicGraph.addEdge("B", "C");
        cyclicGraph.addEdge("C", "D");
        cyclicGraph.addEdge("D", "B");
        cyclicGraph.addEdge("A", "E");
        
        assertFalse(TopoSort.isDAG(cyclicGraph));
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TopoSort.sort(cyclicGraph)
        );
        
        assertTrue(exception.getMessage().contains("cycles"));
    }

    @Test
    void testProjectDependencies() {
        dag.addEdge("Planning", "Design");
        dag.addEdge("Planning", "Research");
        dag.addEdge("Design", "Development");
        dag.addEdge("Research", "Development");
        dag.addEdge("Development", "Testing");
        dag.addEdge("Testing", "Deployment");
        
        assertTrue(TopoSort.isDAG(dag));
        
        List<String> result = TopoSort.sort(dag);
        
        assertEquals(6, result.size());
        assertEquals("Planning", result.get(0));
        assertEquals("Deployment", result.get(5));
        
        // Verify dependencies
        int planningIdx = result.indexOf("Planning");
        int designIdx = result.indexOf("Design");
        int researchIdx = result.indexOf("Research");
        int developmentIdx = result.indexOf("Development");
        int testingIdx = result.indexOf("Testing");
        int deploymentIdx = result.indexOf("Deployment");
        
        assertTrue(planningIdx < designIdx);
        assertTrue(planningIdx < researchIdx);
        assertTrue(designIdx < developmentIdx);
        assertTrue(researchIdx < developmentIdx);
        assertTrue(developmentIdx < testingIdx);
        assertTrue(testingIdx < deploymentIdx);
    }
}
