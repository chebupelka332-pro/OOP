package ru.nsu.tokarev.Pizzeria.queue;

import java.util.LinkedList;
import java.util.Queue;


public class OrderQueue {
    private final Queue<Order> queue = new LinkedList<>();
    private boolean closed = false;

    public synchronized void put(Order order) {
        queue.add(order);
        notifyAll();
    }

    public synchronized Order take() throws InterruptedException {
        while (queue.isEmpty() && !closed) {
            wait();
        }
        return queue.poll();
    }

    public synchronized void close() {
        closed = true;
        notifyAll();
    }

    public synchronized boolean isClosed() {
        return closed;
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized int size() {
        return queue.size();
    }
}
