package ru.nsu.tokarev.Pizzeria.queue;

import java.util.concurrent.atomic.AtomicInteger;


public class Order {
    private static final AtomicInteger counter = new AtomicInteger(1);

    private final int id;
    private volatile OrderState state;

    public Order() {
        this.id = counter.getAndIncrement();
        this.state = OrderState.WAITING;
        printState();
    }

    public int getId() {
        return id;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
        printState();
    }

    private void printState() {
        System.out.println("[" + id + "] " + state);
    }
}
