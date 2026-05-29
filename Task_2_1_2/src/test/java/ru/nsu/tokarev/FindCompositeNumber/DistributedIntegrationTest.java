package ru.nsu.tokarev.FindCompositeNumber;

import org.junit.jupiter.api.Test;
import ru.nsu.tokarev.FindCompositeNumber.Finders.DistributedFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;


public class DistributedIntegrationTest {

    private static Thread startWorker(int port) {
        Thread t = new Thread(new CompositeWorker("localhost", port));
        t.setDaemon(true);
        t.start();
        return t;
    }

    @Test
    public void endToEndMultipleWorkersFindComposite() throws Exception {
        int size = 3 * Protocol.CHUNK_SIZE + 5;
        int[] input = new int[size];
        for (int i = 0; i < size - 1; i++) {
            input[i] = 1_000_000_007;
        }
        input[size - 1] = 1_000_000_008;

        DistributedFinder master = new DistributedFinder(0, 5000);
        int port = master.getPort();

        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            workers.add(startWorker(port));
        }

        boolean result = master.containsComposite(input, new AtomicBoolean(false));
        assertTrue(result, "Мастер должен обнаружить составное число через воркеров");
    }

    @Test
    public void endToEndAllPrimesAcrossWorkers() throws Exception {
        int[] input = {20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                       6998009, 6998029, 6998039, 20165149, 6998051, 6998053};

        DistributedFinder master = new DistributedFinder(0, 5000);
        int port = master.getPort();

        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            workers.add(startWorker(port));
        }

        boolean result = master.containsComposite(input, new AtomicBoolean(false));
        assertFalse(result, "Все числа простые - мастер должен вернуть false");
    }

    @Test
    public void workerReceivesStopSignalAfterCompositeFound() throws Exception {
        int[] input = {6, 8, 7, 13, 5, 9, 4};

        DistributedFinder master = new DistributedFinder(0, 5000);
        int port = master.getPort();

        Thread worker = startWorker(port);

        boolean result = master.containsComposite(input, new AtomicBoolean(false));
        assertTrue(result);

        // Воркер должен получить MSG_CANCEL/MSG_NO_MORE_TASKS и завершиться сам
        worker.join(3000);
        assertFalse(worker.isAlive(),
                "Воркер должен был завершиться после получения сигнала остановки");
    }

    @Test
    public void allWorkersStopAfterCompositeFoundConcurrently() throws Exception {
        int size = 5 * Protocol.CHUNK_SIZE;
        int[] input = new int[size];
        for (int i = 0; i < size - 1; i++) {
            input[i] = 1_000_000_007;
        }
        input[size / 2] = 1_000_000_008;

        DistributedFinder master = new DistributedFinder(0, 5000);
        int port = master.getPort();

        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            workers.add(startWorker(port));
        }

        boolean result = master.containsComposite(input, new AtomicBoolean(false));
        assertTrue(result);

        for (Thread w : workers) {
            w.join(3000);
            assertFalse(w.isAlive(),
                    "Все воркеры должны завершиться после обнаружения составного");
        }
    }

    @Test
    public void externalCancellationStopsMasterAndWorkers() throws Exception {
        int[] input = new int[10 * Protocol.CHUNK_SIZE];
        for (int i = 0; i < input.length; i++) {
            input[i] = 1_000_000_007;
        }

        DistributedFinder master = new DistributedFinder(0, 5000);
        int port = master.getPort();

        Thread worker = startWorker(port);

        AtomicBoolean cancel = new AtomicBoolean(false);
        Thread canceller = new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            cancel.set(true);
        });
        canceller.setDaemon(true);
        canceller.start();

        boolean result = master.containsComposite(input, cancel);
        assertFalse(result,
                "При внешней отмене мастер не должен сообщать о найденном составном");

        worker.join(3000);
    }
}
