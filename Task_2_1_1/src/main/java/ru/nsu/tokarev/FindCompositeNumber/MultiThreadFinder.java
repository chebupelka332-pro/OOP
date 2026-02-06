package ru.nsu.tokarev.FindCompositeNumber;

import java.util.concurrent.atomic.AtomicBoolean;

public class MultiThreadFinder implements CompositeFinder{
    public boolean containsCompositeThreadCount(int[] numbers, int numberOfThreads) {
        int len = numbers.length;
        if (len == 0) return false;

        numberOfThreads = Math.min(numberOfThreads, len);
        Thread[] threads = new Thread[numberOfThreads];
        AtomicBoolean found = new AtomicBoolean(false);

        int chunkSize = (len + numberOfThreads - 1) / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, len);

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    if (found.get()) return;
                    if (!SingleThreadFinder.isPrime(numbers[j])) {
                        found.set(true);
                        return;
                    }
                }
            });
            threads[i].start();
        }

        try {
            for (Thread thread : threads) {
                if (thread != null) thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return found.get();
    }

    public boolean containsComposite(int[] numbers) {
        return containsCompositeThreadCount(numbers, 4);
    }
}
