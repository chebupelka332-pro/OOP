package ru.nsu.tokarev.FindCompositeNumber.Finders;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParallelStreamFinder implements CompositeFinder {
    public boolean containsComposite(int[] numbers, AtomicBoolean cancelled) {
        return Arrays.stream(numbers)
            .parallel()
            .anyMatch(n -> !cancelled.get() && !SingleThreadFinder.isPrime(n));
    }
}
