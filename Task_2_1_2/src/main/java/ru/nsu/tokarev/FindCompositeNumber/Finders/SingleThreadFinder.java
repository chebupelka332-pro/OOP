package ru.nsu.tokarev.FindCompositeNumber.Finders;

import java.util.concurrent.atomic.AtomicBoolean;

public class SingleThreadFinder implements CompositeFinder {
    public boolean containsComposite(int[] numbers, AtomicBoolean cancelled) {
        for (int number : numbers) {
            if (cancelled.get()) return false;
            if (!isPrime(number)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
