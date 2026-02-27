package ru.nsu.tokarev.Pizzeria.warehouse;

import ru.nsu.tokarev.Pizzeria.queue.Order;
import ru.nsu.tokarev.Pizzeria.queue.OrderState;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;


public class Warehouse implements IWarehouse {
    private final Queue<Order> storage = new LinkedList<>();
    private final int capacity;
    private boolean closed = false;

    public Warehouse(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public synchronized void put(Order order) throws InterruptedException {
        while (storage.size() >= capacity) {
            wait();
        }
        order.setState(OrderState.STORING);
        storage.add(order);
        notifyAll();
    }

    @Override
    public synchronized List<Order> take(int maxCount) throws InterruptedException {
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

    @Override
    public synchronized void close() {
        closed = true;
        notifyAll();
    }

    @Override
    public synchronized boolean isClosed() {
        return closed;
    }

    @Override
    public synchronized boolean isEmpty() {
        return storage.isEmpty();
    }

    @Override
    public synchronized int size() {
        return storage.size();
    }
}
