package ru.nsu.tokarev.Pizzeria.warehouse;

import ru.nsu.tokarev.Pizzeria.queue.Order;

import java.util.List;


public interface IWarehouse {
    void put(Order order) throws InterruptedException;
    List<Order> take(int maxCount) throws InterruptedException;
    void close();
    boolean isClosed();
    boolean isEmpty();
    int size();
}
