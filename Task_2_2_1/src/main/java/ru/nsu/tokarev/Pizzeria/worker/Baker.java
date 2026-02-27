package ru.nsu.tokarev.Pizzeria.worker;

import ru.nsu.tokarev.Pizzeria.queue.Order;
import ru.nsu.tokarev.Pizzeria.queue.OrderQueue;
import ru.nsu.tokarev.Pizzeria.queue.OrderState;
import ru.nsu.tokarev.Pizzeria.warehouse.IWarehouse;


public class Baker implements IWorker {
    private final int id;
    private final int bakingTime;
    private final OrderQueue orderQueue;
    private final IWarehouse warehouse;

    Baker(int id, int bakingTime, OrderQueue orderQueue, IWarehouse warehouse) {
        this.id = id;
        this.bakingTime = bakingTime;
        this.orderQueue = orderQueue;
        this.warehouse = warehouse;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Baker #" + id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Order order = orderQueue.take();

                // null means queue is closed and empty – no more work
                if (order == null) {
                    break;
                }
                order.setState(OrderState.BAKING);
                System.out.println(this + " is baking order [" + order.getId() + "]");

                Thread.sleep(bakingTime);

                System.out.println(this + " finished order [" + order.getId() + "], moving to warehouse");
                warehouse.put(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(this + " stopped.");
    }
}
