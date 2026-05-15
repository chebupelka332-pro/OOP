package ru.nsu.tokarev.FindCompositeNumber;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrimeCacheTest {

    @Test
    void testPrimesCorrect() {
        PrimeCache cache = new PrimeCache();
        assertTrue(cache.isPrime(2));
        assertTrue(cache.isPrime(3));
        assertTrue(cache.isPrime(7));
        assertTrue(cache.isPrime(6997901));
    }

    @Test
    void testCompositesCorrect() {
        PrimeCache cache = new PrimeCache();
        assertFalse(cache.isPrime(1));
        assertFalse(cache.isPrime(4));
        assertFalse(cache.isPrime(6));
        assertFalse(cache.isPrime(9));
    }

    @Test
    void testCacheReturnsSameResult() {
        PrimeCache cache = new PrimeCache();
        boolean first = cache.isPrime(17);
        boolean second = cache.isPrime(17);
        assertEquals(first, second);
        assertTrue(first);
    }

    @Test
    void testCacheSharedAcrossCalls() {
        PrimeCache cache = new PrimeCache();
        for (int n = 2; n < 1000; n++) {
            cache.isPrime(n);
        }
        assertTrue(cache.isPrime(997));
        assertFalse(cache.isPrime(999));
    }
}
