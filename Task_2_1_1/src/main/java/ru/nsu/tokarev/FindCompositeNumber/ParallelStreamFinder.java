package ru.nsu.tokarev.FindCompositeNumber;

import java.util.Arrays;

public class ParallelStreamFinder implements CompositeFinder {
    public boolean containsComposite(int[] numbers) {
        return Arrays.stream(numbers)
            .parallel()
            .anyMatch(n -> !SingleThreadFinder.isPrime(n));
    }
}
