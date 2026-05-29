package ru.nsu.tokarev.FindCompositeNumber;

import ru.nsu.tokarev.FindCompositeNumber.Finders.SingleThreadFinder;

import java.util.concurrent.ConcurrentHashMap;

public class PrimeCache {
    private final ConcurrentHashMap<Integer, Boolean> cache = new ConcurrentHashMap<>();

    public boolean isPrime(int n) {
        return cache.computeIfAbsent(n, SingleThreadFinder::isPrime);
    }
}
