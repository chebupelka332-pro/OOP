package ru.nsu.tokarev.Pizzeria;

import java.util.LinkedList;
import java.util.Queue;


class OrderQueue {
    private final Queue<Order> queue = new LinkedList<>();
    private boolean closed = false;

    synchronized void put(Order order) {
        queue.add(order);
        notifyAll();
    }

    synchronized Order take() throws InterruptedException {
        while (queue.isEmpty() && !closed) {
            wait();
        }
        return queue.poll();
    }

    synchronized void close() {
        closed = true;
        notifyAll();
    }

    synchronized boolean isClosed() {
        return closed;
    }

    synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    synchronized int size() {
        return queue.size();
    }
}
