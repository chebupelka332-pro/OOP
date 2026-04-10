package ru.nsu.tokarev.FindCompositeNumber;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.nsu.tokarev.FindCompositeNumber.Finders.DistributedFinder;
import ru.nsu.tokarev.FindCompositeNumber.Finders.MultiThreadFinder;
import ru.nsu.tokarev.FindCompositeNumber.Finders.ParallelStreamFinder;
import ru.nsu.tokarev.FindCompositeNumber.Finders.SingleThreadFinder;

import java.util.concurrent.atomic.AtomicBoolean;

public class FinderTest {

    @Test
    public void testWithComposite() {
        int[] input = {6, 8, 7, 13, 5, 9, 4};

        SingleThreadFinder single = new SingleThreadFinder();
        assertTrue(single.containsComposite(input, new AtomicBoolean(false)));

        ParallelStreamFinder parallelStream = new ParallelStreamFinder();
        assertTrue(parallelStream.containsComposite(input, new AtomicBoolean(false)));

        MultiThreadFinder multiThread = new MultiThreadFinder();
        assertTrue(multiThread.containsComposite(input, new AtomicBoolean(false)));

        assertTrue(multiThread.containsCompositeThreadCount(input, 4, new AtomicBoolean(false)));
    }

    @Test
    public void testAllPrimes() {
        int[] input = {20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                       6998009, 6998029, 6998039, 20165149, 6998051, 6998053};

        SingleThreadFinder single = new SingleThreadFinder();
        assertFalse(single.containsComposite(input, new AtomicBoolean(false)));

        ParallelStreamFinder parallelStream = new ParallelStreamFinder();
        assertFalse(parallelStream.containsComposite(input, new AtomicBoolean(false)));

        MultiThreadFinder multiThread = new MultiThreadFinder();
        assertFalse(multiThread.containsComposite(input, new AtomicBoolean(false)));
    }

    @Test
    public void testDistributedWithComposites() throws Exception {
        int[] input = {6, 8, 7, 13, 5, 9, 4};
        int workerCount = 3;

        DistributedFinder finder = new DistributedFinder(0, workerCount, 5000);
        int port = finder.getPort();

        for (int i = 0; i < workerCount; i++) {
            new Thread(new CompositeWorker("localhost", port, new SingleThreadFinder())).start();
        }

        assertTrue(finder.containsComposite(input, new AtomicBoolean(false)));
    }

    @Test
    public void testDistributedAllPrimes() throws Exception {
        int[] input = {20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                       6998009, 6998029, 6998039, 20165149, 6998051, 6998053};
        int workerCount = 4;

        DistributedFinder finder = new DistributedFinder(0, workerCount, 5000);
        int port = finder.getPort();

        for (int i = 0; i < workerCount; i++) {
            new Thread(new CompositeWorker("localhost", port, new SingleThreadFinder())).start();
        }

        assertFalse(finder.containsComposite(input, new AtomicBoolean(false)));
    }

    @Test
    public void testDistributedFaultTolerance() throws Exception {
        // Master expects 3 workers, but only 1 connects — rest processed locally
        int[] input = {6, 8, 7, 13, 5, 9, 4};
        int workerCount = 3;

        DistributedFinder finder = new DistributedFinder(0, workerCount, 2000);
        int port = finder.getPort();

        // Start only 1 worker instead of 3
        new Thread(new CompositeWorker("localhost", port, new SingleThreadFinder())).start();

        assertTrue(finder.containsComposite(input, new AtomicBoolean(false)));
    }
}
