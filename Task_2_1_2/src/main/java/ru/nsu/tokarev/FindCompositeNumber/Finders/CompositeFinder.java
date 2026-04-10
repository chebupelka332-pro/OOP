package ru.nsu.tokarev.FindCompositeNumber.Finders;

import java.util.concurrent.atomic.AtomicBoolean;

public interface CompositeFinder {
    boolean containsComposite(int[] numbers, AtomicBoolean cancelled);
}
