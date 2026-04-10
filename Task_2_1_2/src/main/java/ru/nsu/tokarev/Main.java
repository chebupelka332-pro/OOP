package ru.nsu.tokarev;

import ru.nsu.tokarev.FindCompositeNumber.Finders.DistributedFinder;
import ru.nsu.tokarev.FindCompositeNumber.CompositeWorker;
import ru.nsu.tokarev.FindCompositeNumber.Finders.MultiThreadFinder;
import ru.nsu.tokarev.FindCompositeNumber.Finders.ParallelStreamFinder;
import ru.nsu.tokarev.FindCompositeNumber.Finders.SingleThreadFinder;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {
        int count = 10000;
        int[] data = generateLargePrimes(count);

        System.out.println("Data generated. Array size: " + data.length);

        SingleThreadFinder singleFn = new SingleThreadFinder();
        long start = System.nanoTime();
        boolean res1 = singleFn.containsComposite(data, new AtomicBoolean(false));
        long end = System.nanoTime();
        System.out.println("Sequential: " + (end - start) / 1_000_000 + " ms, Result: " + res1);

        MultiThreadFinder multiFn = new MultiThreadFinder();
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors: " + cores);

        int[] threadCounts = {2, 4, 8, 12, 16, 22};
        for (int t : threadCounts) {
            start = System.nanoTime();
            boolean res2 = multiFn.containsCompositeThreadCount(data, t, new AtomicBoolean(false));
            end = System.nanoTime();
            System.out.println("MultiThread (" + t + " threads): " + (end - start) / 1_000_000 + " ms, Result: " + res2);
        }

        ParallelStreamFinder streamFn = new ParallelStreamFinder();
        start = System.nanoTime();
        boolean res3 = streamFn.containsComposite(data, new AtomicBoolean(false));
        end = System.nanoTime();
        System.out.println("ParallelStream: " + (end - start) / 1_000_000 + " ms, Result: " + res3);

        // Distributed benchmark
        int[] distributedWorkerCounts = {2, 4, 8, 12, 16, 22};
        for (int wc : distributedWorkerCounts) {
            try {
                DistributedFinder distFn = new DistributedFinder(0, wc, 30000);
                int actualPort = distFn.getPort();

                for (int i = 0; i < wc; i++) {
                    new Thread(new CompositeWorker("localhost", actualPort, new SingleThreadFinder())).start();
                }

                start = System.nanoTime();
                boolean res4 = distFn.containsComposite(data, new AtomicBoolean(false));
                end = System.nanoTime();
                System.out.println("Distributed (" + wc + " workers): " + (end - start) / 1_000_000 + " ms, Result: " + res4);
            } catch (Exception e) {
                System.out.println("Distributed (" + wc + " workers): ERROR - " + e.getMessage());
            }
        }
    }

    private static int[] generateLargePrimes(int count) {
        int[] primes = new int[count];
        int num = 1000000007;
        int idx = 0;
        while (idx < count) {
            if (SingleThreadFinder.isPrime(num)) {
                primes[idx++] = num;
            }
            num += 2;
        }
        return primes;
    }
}
