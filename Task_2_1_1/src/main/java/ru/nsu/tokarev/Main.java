package ru.nsu.tokarev;

import ru.nsu.tokarev.FindCompositeNumber.MultiThreadFinder;
import ru.nsu.tokarev.FindCompositeNumber.ParallelStreamFinder;
import ru.nsu.tokarev.FindCompositeNumber.SingleThreadFinder;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int count = 10000;
        int[] data = generateLargePrimes(count);

        System.out.println("Data generated. Array size: " + data.length);

        SingleThreadFinder singleFn = new SingleThreadFinder();
        long start = System.nanoTime();
        boolean res1 = singleFn.containsComposite(data);
        long end = System.nanoTime();
        System.out.println("Sequential: " + (end - start) / 1_000_000 + " ms, Result: " + res1);

        MultiThreadFinder multiFn = new MultiThreadFinder();
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors: " + cores);

        int[] threadCounts = {2, 4, 8, 12, 16, 22};
        for (int t : threadCounts) {
            start = System.nanoTime();
            boolean res2 = multiFn.containsComposite(data, t);
            end = System.nanoTime();
            System.out.println("MultiThread (" + t + " threads): " + (end - start) / 1_000_000 + " ms, Result: " + res2);
        }

        ParallelStreamFinder streamFn = new ParallelStreamFinder();
        start = System.nanoTime();
        boolean res3 = streamFn.containsComposite(data);
        end = System.nanoTime();
        System.out.println("ParallelStream: " + (end - start) / 1_000_000 + " ms, Result: " + res3);
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