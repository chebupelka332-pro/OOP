package ru.nsu.tokarev.Pizzeria;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;


class Warehouse {
    private final Queue<Order> storage = new LinkedList<>();
    private final int capacity;
    private boolean closed = false;

    Warehouse(int capacity) {
        this.capacity = capacity;
    }

    synchronized void put(Order order) throws InterruptedException {
        while (storage.size() >= capacity) {
            wait();
        }
        order.setState(OrderState.STORING);
        storage.add(order);
        notifyAll();
    }

    synchronized List<Order> take(int maxCount) throws InterruptedException {
        while (storage.isEmpty() && !closed) {
            wait();
        }
        List<Order> batch = new ArrayList<>();
        int count = Math.min(maxCount, storage.size());
        for (int i = 0; i < count; i++) {
            batch.add(storage.poll());
        }
        notifyAll();
        return batch;
    }

    synchronized void close() {
        closed = true;
        notifyAll();
    }

    synchronized boolean isClosed() {
        return closed;
    }

    synchronized boolean isEmpty() {
        return storage.isEmpty();
    }

    synchronized int size() {
        return storage.size();
    }
}
