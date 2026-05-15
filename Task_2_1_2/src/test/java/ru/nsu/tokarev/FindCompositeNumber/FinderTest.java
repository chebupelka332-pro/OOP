package ru.nsu.tokarev.FindCompositeNumber;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.nsu.tokarev.FindCompositeNumber.Finders.DistributedFinder;
import ru.nsu.tokarev.FindCompositeNumber.Finders.MultiThreadFinder;
import ru.nsu.tokarev.FindCompositeNumber.Finders.ParallelStreamFinder;
import ru.nsu.tokarev.FindCompositeNumber.Finders.SingleThreadFinder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

        DistributedFinder finder = new DistributedFinder(0, 5000);
        int port = finder.getPort();

        for (int i = 0; i < workerCount; i++) {
            new Thread(new CompositeWorker("localhost", port)).start();
        }

        assertTrue(finder.containsComposite(input, new AtomicBoolean(false)));
    }

    @Test
    public void testDistributedAllPrimes() throws Exception {
        int[] input = {20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                       6998009, 6998029, 6998039, 20165149, 6998051, 6998053};
        int workerCount = 4;

        DistributedFinder finder = new DistributedFinder(0, 5000);
        int port = finder.getPort();

        for (int i = 0; i < workerCount; i++) {
            new Thread(new CompositeWorker("localhost", port)).start();
        }

        assertFalse(finder.containsComposite(input, new AtomicBoolean(false)));
    }

    @Test
    public void testDistributedFaultTolerance() throws Exception {
        int[] input = {6, 8, 7, 13, 5, 9, 4};

        DistributedFinder finder = new DistributedFinder(0, 5000);
        int port = finder.getPort();

        new Thread(new CompositeWorker("localhost", port)).start();

        assertTrue(finder.containsComposite(input, new AtomicBoolean(false)));
    }

    @Test
    public void testDistributedWorkerCrashRetransmits() throws Exception {
        int[] input = {6, 8, 7, 13, 5, 9, 4};

        DistributedFinder finder = new DistributedFinder(0, 5000);
        int port = finder.getPort();

        // Плохой воркер
        new Thread(() -> {
            try (Socket s = new Socket("localhost", port)) {
                DataInputStream in = new DataInputStream(s.getInputStream());
                byte tag = in.readByte();
                if (tag == Protocol.MSG_TASK) {
                    Protocol.readTask(in);
                }
                // Закрываем без ответа имитируя падение
            } catch (IOException ignored) {}
        }).start();

        Thread.sleep(100);

        // Хороший воркер подхватит переотправленный чанк
        new Thread(new CompositeWorker("localhost", port)).start();

        assertTrue(finder.containsComposite(input, new AtomicBoolean(false)));
    }

    @Test
    public void testDistributedReconnect() throws Exception {
        int[] input = {20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                       6998009, 6998029, 6998039, 20165149, 6998051, 6998053};

        DistributedFinder finder = new DistributedFinder(0, 5000);
        int port = finder.getPort();

        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            new CompositeWorker("localhost", port).run();
        }).start();

        assertFalse(finder.containsComposite(input, new AtomicBoolean(false)));
    }
}
