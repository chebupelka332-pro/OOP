package ru.nsu.tokarev.Pizzeria;

import java.util.concurrent.atomic.AtomicInteger;


class Order {
    private static final AtomicInteger counter = new AtomicInteger(1);

    private final int id;
    private volatile OrderState state;

    Order() {
        this.id = counter.getAndIncrement();
        this.state = OrderState.WAITING;
        printState();
    }

    int getId() {
        return id;
    }

    OrderState getState() {
        return state;
    }

    void setState(OrderState state) {
        this.state = state;
        printState();
    }

    private void printState() {
        System.out.println("[" + id + "] " + state);
    }
}
