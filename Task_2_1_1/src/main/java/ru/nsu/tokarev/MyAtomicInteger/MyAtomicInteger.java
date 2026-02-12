package ru.nsu.tokarev.MyAtomicInteger;

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

public class MyAtomicInteger {
    private int value;

    public MyAtomicInteger(int initialValue) {
        this.value = initialValue;
    }

    public synchronized int get() {
        return value;
    }

    public synchronized void set(int newValue) {
        this.value = newValue;
    }

    public synchronized int updateAndGet(IntUnaryOperator updateFunction) {
        this.value = updateFunction.applyAsInt(this.value);
        return this.value;
    }

    public synchronized int getAndUpdate(IntUnaryOperator updateFunction) {
        int prev = this.value;
        this.value = updateFunction.applyAsInt(this.value);
        return prev;
    }

    public synchronized int accumulateAndGet(int x, IntBinaryOperator accumulatorFunction) {
        this.value = accumulatorFunction.applyAsInt(this.value, x);
        return this.value;
    }

    public synchronized int getAndAccumulate(int x, IntBinaryOperator accumulatorFunction) {
        int prev = this.value;
        this.value = accumulatorFunction.applyAsInt(this.value, x);
        return prev;
    }
}