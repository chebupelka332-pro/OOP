package ru.nsu.tokarev.FindCompositeNumber;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FinderTest {

    @Test
    public void testWithComposite() {
        int[] input = {6, 8, 7, 13, 5, 9, 4};
        
        SingleThreadFinder single = new SingleThreadFinder();
        assertTrue(single.containsComposite(input));

        ParallelStreamFinder parallelStream = new ParallelStreamFinder();
        assertTrue(parallelStream.containsComposite(input));

        MultiThreadFinder multiThread = new MultiThreadFinder();
        assertTrue(multiThread.containsComposite(input, 4));
    }

    @Test
    public void testAllPrimes() {
        int[] input = {20319251, 6997901, 6997927, 6997937, 17858849, 6997967, 
                       6998009, 6998029, 6998039, 20165149, 6998051, 6998053};

        SingleThreadFinder single = new SingleThreadFinder();
        assertFalse(single.containsComposite(input));

        ParallelStreamFinder parallelStream = new ParallelStreamFinder();
        assertFalse(parallelStream.containsComposite(input));

        MultiThreadFinder multiThread = new MultiThreadFinder();
        assertFalse(multiThread.containsComposite(input, 4));
    }
}

